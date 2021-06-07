package nettoyage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


public class Methodes {

	static class Patient_maladies {
		static ArrayList<Patient> patient;
		static HashSet<String> hash;
	};
	/**
	 * uniquement sur les valeurs numeriques cette fonction s'aplique renvoi un
	 * tableau donc la premier valeur est minnimun et la deuxieme valeur est la
	 * valeur maximal pour une colonne d'un fichier donc l'indixe dans le
	 * fichier est specifier en entree
	 * 
	 * @param file
	 * @param indeice_colone
	 * @return
	 * @throws IOException
	 * 
	 */
	public double[] min_et_max(String file, int indeice_colone)
			throws IOException {
		TreeSet<Double> liste_valeur = new TreeSet<Double>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		br = new BufferedReader(new FileReader(file));
		br.readLine();
		while ((line = br.readLine()) != null) {
			String[] observation = line.split(cvsSplitBy);
			liste_valeur.add(Double.parseDouble(observation[indeice_colone]));
		}
		double max = (double) liste_valeur.toArray()[liste_valeur.size() - 1];
		double min = (double) liste_valeur.toArray()[0];
		//System.out.println("Max => "+max);
		//System.out.println("Min => "+min);
		//System.err.println(liste_valeur.toString());
		return new double[] { (double) liste_valeur.toArray()[0],
				(double) liste_valeur.toArray()[liste_valeur.size() - 1] };

	}
	/**
	 * calcul de la probalite d'appartenance d'une valeur dans un intervale
	 * donnee
	 * 
	 * @param valeur_patient
	 * @param intervale
	 * @param separateur_intervale
	 * @param distance_max
	 * @return
	 */
	public  double probabilite_appartenance(double valeur_patient,
			String intervale, String separateur_intervale, double distance_max) {

		double borne_inf = Double.parseDouble(intervale.substring(
				intervale.indexOf("[") + 1, (intervale.length() - 1)).split(
						separateur_intervale)[0]);
		double borne_sup = Double.parseDouble(intervale.substring(
				intervale.indexOf("[") + 1, (intervale.length() - 1)).split(
						separateur_intervale)[1]);
		double centre = ((borne_inf + borne_sup)) / 2;
		double dist_valeur_centre = Math.sqrt(Math.pow(
				(valeur_patient - centre), 2));
		double result = 1 - (dist_valeur_centre / distance_max);

		return result;
	}

	/**
	 * renvoi la distance max entre le plus petit intervale et le plus grand
	 * intervale des intervales exemple {[10,50],[51,70],[71,89],[90,100]}
	 * renvoie
	 * 
	 * @param liste_intervale
	 * @return
	 */
	public  double distance_max(HashSet<String> liste_intervale) {
		Map<Double, Double> map = new TreeMap();
		Iterator<String> iter = liste_intervale.iterator();
		while (iter.hasNext()) {
			String intervale = iter.next();
			if (intervale.contains(";")) {
				double borne_inf = Double.parseDouble(intervale.substring(
						intervale.indexOf("[") + 1, (intervale.length() - 1))
						.split(";")[0]);
				double borne_sup = Double.parseDouble(intervale.substring(
						intervale.indexOf("[") + 1, (intervale.length() - 1))
						.split(";")[1]);
				map.put(borne_sup, borne_inf);
			} else if (intervale.contains(",")) {
				double borne_inf = Double.parseDouble(intervale.substring(
						intervale.indexOf("[") + 1, (intervale.length() - 1))
						.split(",")[0]);
				double borne_sup = Double.parseDouble(intervale.substring(
						intervale.indexOf("[") + 1, (intervale.length() - 1))
						.split(",")[1]);
				map.put(borne_sup, borne_inf);
			}
		}

		// je met dans des hassets pour avoir les valeurs ordonnees
		TreeSet<Double> b_s = new TreeSet<Double>();
		TreeSet<Double> b_i = new TreeSet<Double>();
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Double, Double> entry = (Map.Entry) it.next();
			b_s.add(entry.getKey());
			b_i.add(entry.getValue());
		}
		double b_sup_max = b_s.last();
		double b_inf_min = b_i.first();
		double b_sup_inf = map.get(b_sup_max);
		double centre = (b_sup_inf + b_sup_max) / 2;

		double result = centre - b_inf_min;

		return result;
	}
	/**
	 * nous creons les intervales jusqu'au max
	 * 
	 * @param val_min
	 * @param val_max
	 * @param pat
	 * @return
	 */
	public  HashSet<String> formation_interval(double val_m,
			double val_ma, int pat) {
		HashSet<String> result = new HashSet<String>();
		int val_min = (int) val_m;
		int val_max = (int) val_ma;
		if (pat > val_max) {
			// System.err.println("pas d'interval");
		} else if (val_max > pat && (val_min + pat) < val_max
				&& val_max % pat != 0) {
			val_max = val_max + 1;
			int increment = val_min;
			do {
				int sup = increment + pat;
				result.add("[" + increment + "," + sup + "]");
				increment = sup + 1;
			} while (increment <= val_max);

		} else if (val_max > pat && (val_min + pat) < val_max
				&& val_max % pat == 0) {
			int increment = val_min;
			do {
				int sup = increment + pat;
				result.add("[" + increment + "," + sup + "]");
				increment = sup + 1;
			} while (increment <= val_max);
		} else if (val_max - val_min == pat) {
			result.add("[" + val_min + "," + val_max + "]");
		}
		return result;

	}
	/**
	 *
	 * @param p_m  
	 * @return parcours la liste des patients contenues dans p_m a la recherche
	 *         des nbres apparitions des maladies renvoi une de maladie et leur
	 *         fraquence
	 */							
	public  Map<String, Integer> maladie_frequent(Patient_maladies p_m) {
		// chaque maladie et leur frequence d'apparition dans le jeu de
		// donnee(liste des patients)
		Map<String, Integer> mal_freq = new TreeMap<String, Integer>();
		ArrayList<Patient> patient = new ArrayList<Patient>();

		patient = p_m.patient;
		// initialisation de ma liste de maladie avec pour chacun une frequence
		// de zero
		Iterator init = patient.get(0).hash_mal.entrySet().iterator();
		while (init.hasNext()) {
			Map.Entry<String, Integer> entry = (Map.Entry) init.next();
			mal_freq.put(entry.getKey(), 0);
		}

		for (int i = 0; i < patient.size(); i++) {
			Iterator it = patient.get(i).hash_mal.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> entry = (Map.Entry) it.next();
				String maladie = entry.getKey();
				int valeur = entry.getValue();
				mal_freq.put(maladie, mal_freq.get(maladie) + valeur);
			}

		}
		// System.out.print(mal_freq.toString());
		return mal_freq;
	}
	/**
	 * 
	 * @param p_m
	 * @param mal_freq
	 * @param pourcent_split
	 *            => correspond au nombre d'element du jeu de donnee test que
	 *            l'on poura avoir au max
	 * @param freq_apparition
	 *            => correspond au nombre d'apparition des maladies pour chaque
	 *            observation dans le jeu de donnee test
	 * @return renvoie un map de deux elements l'un contiendra les donnees
	 *         d'apprentissage l'autre contient contiendra les donnees de test
	 * 
	 */
	public Map<String, ArrayList<Patient>> split_ens_freq_(
			Patient_maladies p_m, Map<String, Integer> mal_freq,
			double pourcent_split, int freq_apparition) {
		Map<String, ArrayList<Patient>> result = new TreeMap<String, ArrayList<Patient>>();
		ArrayList<Patient> donnees_test = new ArrayList<Patient>();
		ArrayList<Patient> donnees_train = new ArrayList<Patient>();
		// liste ens frequents
		HashSet<Map<String, Integer>> mal_f = new HashSet<Map<String, Integer>>();

		ArrayList<Patient> patient = p_m.patient;
		int arret = (int) ((patient.size() * pourcent_split) / 100);
		for (int i = 0; i < patient.size(); i++) {
			boolean a = false;
			Iterator it = patient.get(i).hash_mal.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> entry = (Map.Entry) it.next();
				String maladie = entry.getKey();
				if (mal_freq.get(maladie) == freq_apparition) {
					a = true;
				} else {
					a = false;
				}
			}
			/***
			 * si tout les maladies du patient respecte le seuil de frequence
			 * alors je le met dans les donnees test si l'ensemble correspondant
			 * a ces maladies n'y est pas encore
			 */
			boolean b = false;
			if (a && donnees_test.size() < arret) {
				/***
				 * on ne peut faire des tests sur des nouveaux cas que si on a
				 * eu au paravent des cas similaire lors de l'entrainement
				 */
				for (Patient p : donnees_train) {
					if (p.hash_mal.equals(patient.get(i).hash_mal)) {
						b = true;
						break;
					} else {
						b = false;
					}
				}

				if (mal_f.contains(patient.get(i).hash_mal)
						|| donnees_train.isEmpty()) {
					donnees_train.add(patient.get(i));
				} else if (b
						&& mal_f.contains(patient.get(i).hash_mal) == false) {
					mal_f.add(patient.get(i).hash_mal);
					donnees_test.add(patient.get(i));
				} else {
					donnees_train.add(patient.get(i));
				}
			} else {
				donnees_train.add(patient.get(i));
			}

		}
		/*
		 * System.err.println("train =>"+donnees_train.toString());
		 * System.err.println("taille du train =>"+donnees_train.size());
		 * System.err.println("test =>"+donnees_test.toString());
		 * System.err.println("taille du test=>"+donnees_test.size());
		 */
		result.put("training", donnees_train);
		result.put("test", donnees_test);
		return result;
	}

	public  Map<String, ArrayList<Patient>> split_ens_freq_v2(
			Patient_maladies p_m, Map<String, Integer> mal_freq,
			double pourcent_split, int freq_apparition) {
		Map<String, ArrayList<Patient>> result = new TreeMap<String, ArrayList<Patient>>();
		ArrayList<Patient> donnees_test = new ArrayList<Patient>();
		ArrayList<Patient> donnees_train = new ArrayList<Patient>();
		// liste ens frequents
		HashSet<Map<String, Integer>> mal_f = new HashSet<Map<String, Integer>>();

		ArrayList<Patient> patient = p_m.patient;
		int arret = (int) ((patient.size() * pourcent_split) / 100);
		for (int i = 0; i < patient.size(); i++) {
			boolean a = false;
			Iterator it = patient.get(i).hash_mal.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> entry = (Map.Entry) it.next();
				a = mal_freq.get(entry.getKey()) == freq_apparition ? true
						: false;
			}
			if (a && donnees_test.size() < arret) {
				donnees_test.add(patient.get(i));
			} else {
				donnees_train.add(patient.get(i));
			}

		}

		/*
		 * System.err.println("train =>"+donnees_train.toString());
		 * System.err.println("taille du train =>"+donnees_train.size());
		 * System.err.println("test =>"+donnees_test.toString());
		 * System.err.println("taille du test=>"+donnees_test.size());
		 */
		result.put("training", donnees_train);
		result.put("test", donnees_test);
		return result;

	}

	public  Map<String, ArrayList<Patient>> split_ens_freq(
			Patient_maladies p_m, Map<String, Integer> mal_freq,
			double pourcent_split, int freq_apparition) {
		Map<String, ArrayList<Patient>> result = new TreeMap<String, ArrayList<Patient>>();
		ArrayList<Patient> donnees_test = new ArrayList<Patient>();
		ArrayList<Patient> donnees_train = new ArrayList<Patient>();
		// liste ens frequents
		HashSet<Map<String, Integer>> mal_f = new HashSet<Map<String, Integer>>();

		// System.err.println(mal_freq.toString());
		ArrayList<Patient> patient = p_m.patient;
		int arret = (int) ((patient.size() * pourcent_split) / 100);
		for (int i = 0; i < patient.size(); i++) {
			int compteur = 0;
			int nbre_mal_patient = 0;
			boolean a = false;// nous permettra de verifier si toute les
			// maladies du patient respecte la frequence
			// d'apparition fixe
			Iterator it = patient.get(i).hash_mal.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> entry = (Map.Entry) it.next();
				String maladie = entry.getKey();
				int val_maladie = entry.getValue();
				// c'etait d'abord egal
				// doit etre superieur ou egale jepense
				if (val_maladie == 1) {
					nbre_mal_patient++;
					if (freq_apparition <= mal_freq.get(maladie)) {
						// System.out.println("freq_apparition => "+freq_apparition+" "+maladie+" freq_liste "+mal_freq.get(maladie));
						compteur++;
					}
				}
			}
			/***
			 * si tout les maladies du patient respecte le seuil de frequence
			 * alors je le met dans les donnees test si l'ensemble correspondant
			 * a ces maladies n'y est pas encore
			 */
			a = compteur == nbre_mal_patient ? true : false;
			boolean b = false;
			if (a && donnees_test.size() < arret) {
				/***
				 * on ne peut faire des tests sur des nouveaux cas que si on a
				 * eu au paravent des cas similaire lors de l'entrainement
				 */
				for (Patient p : donnees_train) {
					if (p.hash_mal.equals(patient.get(i).hash_mal)) {
						b = true;
						break;
					} else {
						b = false;
					}
				}

				if (mal_f.contains(patient.get(i).hash_mal)
						|| donnees_train.isEmpty()) {
					donnees_train.add(patient.get(i));
				} else if (b
						&& mal_f.contains(patient.get(i).hash_mal) == false) {
					mal_f.add(patient.get(i).hash_mal);
					donnees_test.add(patient.get(i));
				} else {
					donnees_train.add(patient.get(i));
				}
			} else {
				donnees_train.add(patient.get(i));
			}

		}
		/*
		 * System.err.println("train =>"+donnees_train.toString());
		 * System.err.println("taille du train =>"+donnees_train.size());
		 * System.err.println("test =>"+donnees_test.toString());
		 * System.err.println("taille du test=>"+donnees_test.size());
		 */
		result.put("training", donnees_train);
		result.put("test", donnees_test);
		return result;
	}

	/**
	 *
	 * @param p_m
	 * @return
	 * parcours la liste des patients contenues dans p_m a la recherche des nbres apparitions des maladies
	 * renvoi une de maladie et leur fraquence
	 */
	public  Map<String, Integer> maladie_frequent_v1(Patient_maladies p_m){
		//chaque maladie et leur frequence d'apparition dans le jeu de donnee(liste des patients)
		Map<String, Integer> mal_freq=new TreeMap<String,Integer>();
		ArrayList<Patient> patient=new ArrayList<Patient>();

		patient = p_m.patient;
		//initialisation de ma liste de maladie avec pour chacun une frequence de zero
		Iterator init = patient.get(0).hash_mal.entrySet().iterator();
		while (init.hasNext()){
			Map.Entry<String, Integer> entry = (Map.Entry)init.next();
			mal_freq.put(entry.getKey(),0);
		}

		for(int i=0;i<patient.size();i++){
			Iterator it = patient.get(i).hash_mal.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> entry = (Map.Entry)it.next();
				String maladie=entry.getKey();
				int valeur=entry.getValue();
				mal_freq.put(maladie, mal_freq.get(maladie)+valeur);
			}

		}
		//System.out.print(mal_freq.toString());
		return mal_freq;
	}
    
	public static ArrayList<Patient> chargement(TreeMap<String, ArrayList<String>> liste){
	    ArrayList<Patient> result=new ArrayList();
		Iterator it = liste.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, ArrayList<String>> entry = (Map.Entry)it.next();
	        Patient e=new Patient();
	        e.moi=entry.getKey();
	        e.voisin.addAll(entry.getValue());
	        result.add(e);
	        //System.out.println(entry.getKey() + " = " + entry.getValue()); 
	    }
	    return result;
	}
	
	/**
	 * RENVOIE LE DICTIONNAIRE DE SIGNES POUR DES MALADES DE COEURS
	 * @param p
	 * @return
	 */
	public  HashSet<String> liste_signe (ArrayList<Patient> p){
		HashSet<String> a= new HashSet<String>();
		ArrayList<String> b =new ArrayList<String>();
		for(int i=0;i<p.size();i++){
			String []tab=p.get(i).libellecons.split("-");
			for(String elt:tab){
				String var=elt.trim();
				if(!var.isEmpty())
					a.add(var);
			}

		}
		b.addAll(a);
		Collections.sort(b);
		a=new HashSet<String>();
		a.addAll(b);
		System.out.print("la taille de la liste des signes =>"+a.size());
		return a;
	}
	
	/**
	 * le rectifier  de fautes de saisi et positionnement de chaine
	 * @param liste
	 * @return
	 */
	public  HashSet<String> correcteur(HashSet<String> liste){
		HashSet<String> result =new HashSet<String>();
		for(String elt:liste){
			if(elt.contains("angines a repetion".toUpperCase())){
			elt=elt.replace("angines a repetion".toUpperCase(), "angines a repetition".toUpperCase());
			result.add(elt);
			}else
			if(elt.contains("bronchite a repetition".toUpperCase())){
			elt=elt.replace("bronchite a repetition".toUpperCase(), "bronchites a repetition".toUpperCase());
			result.add(elt);
			}else
			if(elt.contains("hippocratisme digital discret".toUpperCase()) && elt.split("hippocratisme digital discret").length==0){
					elt=elt.replace("hippocratisme digital discret".toUpperCase(), "hippocratisme digital discret".toUpperCase());
					result.add(elt);}else
			if(elt.contains("deviation du choc de pointe".toUpperCase())){
			elt=elt.replace("deviation du choc de pointe".toUpperCase(), "deviation choc pointe".toUpperCase());
			result.add(elt);}else
			if(elt.contains("discret hippocratisme digital".toUpperCase())){
			elt=elt.replace("discret hippocratisme digital".toUpperCase(), "hippocratisme digital discret".toUpperCase());
			result.add(elt);
			}else
			if(elt.contains("hippocratisme digital discrets".toUpperCase())){
			elt=elt.replace("hippocratisme digital discrets".toUpperCase(), "hippocratisme digital discret".toUpperCase());
			result.add(elt);}else
			
			if(elt.contains("discret hypocatisme digital".toUpperCase())){
			elt=elt.replace("discret hypocatisme digital".toUpperCase(), "hippocratisme digital discret".toUpperCase());
			result.add(elt);}else
			if(elt.contains("hyppocratisme digital".toUpperCase())){
			elt=elt.replace("hyppocratisme digital".toUpperCase(), "hippocratisme digital".toUpperCase());
			result.add(elt);}else
			if(elt.contains("malaises anoxiques".toUpperCase())){
			elt=elt.replace("malaises anoxiques".toUpperCase(), "malaise anoxique".toUpperCase());
			result.add(elt);}else
			
			
			if(elt.contains("muqueuse moyennement colorees".toUpperCase())){
				elt=elt.replace("muqueuse moyennement colorees".toUpperCase(), "muqueuses moyennement colorees".toUpperCase());
				result.add(elt);
				}else
				if(elt.contains("polyartralgies".toUpperCase())){
				elt=elt.replace("polyartralgies".toUpperCase(), "polyarthralgie".toUpperCase());
				result.add(elt);
				}else
				if(elt.contains("pouls biens percus".toUpperCase())){
				elt=elt.replace("pouls biens percus".toUpperCase(), "pouls bien percus".toUpperCase());
				result.add(elt);}else
				if(elt.contains("pouls peripheriques percu symetriques".toUpperCase())){
				elt=elt.replace("pouls peripheriques percu symetriques".toUpperCase(), "pouls peripheriques percus symetriques".toUpperCase());
				result.add(elt);
				}else
				if(elt.contains("pouls peripheriques presents symetriques".toUpperCase())){
				elt=elt.replace("pouls peripheriques presents symetriques".toUpperCase(), "pouls peripheriques percus symetriques".toUpperCase());
				result.add(elt);}else
				if(elt.contains("pouls peripheriques symetriques".toUpperCase())){
				elt=elt.replace("pouls peripheriques symetriques".toUpperCase(), "pouls peripheriques percus symetriques".toUpperCase());
				result.add(elt);}else
				if(elt.contains("pouls peripheriquies percus symetriques".toUpperCase())){
				elt=elt.replace("pouls peripheriquies percus symetriques".toUpperCase(), "pouls peripheriques percus symetriques".toUpperCase());
				result.add(elt);}else
				if(elt.contains("pouls peripheriquies percus bien battants symetriques".toUpperCase())){
				elt=elt.replace("pouls peripheriquies percus bien battants symetriques".toUpperCase(), "pouls peripheriques percus bien battants symetriques".toUpperCase());
				result.add(elt);}else
				if(elt.contains("pouls peripheriquies bien battants symetriques".toUpperCase())){
				elt=elt.replace("pouls peripheriquies bien battants symetriques".toUpperCase(), "pouls peripheriques percus bien battants symetriques".toUpperCase());
				result.add(elt);}else
			
				if(elt.contains("precordiagies".toUpperCase())){
					elt=elt.replace("precordiagies".toUpperCase(), "precordialgies".toUpperCase());
					result.add(elt);
					}else
					if(elt.contains("regurgitations".toUpperCase())){
					elt=elt.replace("regurgitations".toUpperCase(), "regurgitation".toUpperCase());
					result.add(elt);
					}else
					if(elt.contains("rhinites".toUpperCase())){
					elt=elt.replace("rhinites".toUpperCase(), "rhinite".toUpperCase());
					result.add(elt);}else
					if(elt.contains("ronchi bilatelal".toUpperCase())){
					elt=elt.replace("ronchi bilatelal".toUpperCase(), "ronchis bilateraux".toUpperCase());
					result.add(elt);
					}else
					if(elt.contains("souffle diastolique latero seternal gauche 2/6".toUpperCase())){
					elt=elt.replace("souffle diastolique latero seternal gauche 2/6".toUpperCase(), "souffle diastolique latero sternal gauche 2/6".toUpperCase());
					result.add(elt);}else
					if(elt.contains("souffle systolique apexien 2/6".toUpperCase())){
					elt=elt.replace("souffle systolique apexien 2/6".toUpperCase(), "souffle systolique 2/3 apexien".toUpperCase());
					result.add(elt);}else
					if(elt.contains("souffle systolique 2/6 mesocardique".toUpperCase())){
					elt=elt.replace("souffle systolique 2/6 mesocardique".toUpperCase(), "souffle systolique 2/6 mesocardiaque".toUpperCase());
					result.add(elt);}else
					if(elt.contains("souffle systolique 2/6 ieme sous claviculaire gauche".toUpperCase())){
					elt=elt.replace("souffle systolique 2/6 ieme sous claviculaire gauche".toUpperCase(), "souffle systolique 2/6 sous claviculaire gauche".toUpperCase());
					result.add(elt);}else
					if(elt.contains("souffle systolique apexien 3/6".toUpperCase())){
					elt=elt.replace("souffle systolique apexien 3/6".toUpperCase(), "souffle systolique 3/6 apexien".toUpperCase());
					result.add(elt);}else
					
					
					if(elt.contains("souffle systolique apexo".toUpperCase())){
						elt=elt.replace("souffle systolique apexo".toUpperCase(), "souffle systolique apexien".toUpperCase());
						result.add(elt);
						}else
						if(elt.contains("souffle systolique mesocardiaque 2/6".toUpperCase())){
						elt=elt.replace("souffle systolique mesocardiaque 2/6".toUpperCase(), "souffle systolique 2/6 mesocardiaque".toUpperCase());
						result.add(elt);
						}else
						if(elt.contains("SOUFFLE SYSTOLIQUE MESOCARDIAQUE 2 A3/6".toUpperCase())){
						elt=elt.replace("SOUFFLE SYSTOLIQUE MESOCARDIAQUE 2 A3/6".toUpperCase(), "SOUFFLE SYSTOLIQUE 2 A 3/6 MESOCARDIAQUE".toUpperCase());
						result.add(elt);}else
						if(elt.contains("souffle systolique mesocardiaque 3/6".toUpperCase())){
						elt=elt.replace("souffle systolique mesocardiaque 3/6".toUpperCase(), "souffle systolique 3/6 mesocardiaque".toUpperCase());
						result.add(elt);
						}else
						if(elt.contains("souffle systolique pulmonaire 2/6".toUpperCase())){
						elt=elt.replace("souffle systolique pulmonaire 2/6".toUpperCase(), "souffle systolique 2/6 pulmonaire".toUpperCase());
						result.add(elt);}else
						if(elt.contains("souffle systolo".toUpperCase())){
						elt=elt.replace("souffle systolo".toUpperCase(), "souffle systolique".toUpperCase());
						result.add(elt);}else
						if(elt.contains("sternale".toUpperCase())){
						elt=elt.replace("sternale".toUpperCase(), "sternal".toUpperCase());
						result.add(elt);}else
						if(elt.contains("sueurs".toUpperCase())){
						elt=elt.replace("sueurs".toUpperCase(), "sueur".toUpperCase());
						result.add(elt);}else
						if(elt.contains("turgescence spontanee des jugulaires".toUpperCase())){
						elt=elt.replace("turgescence spontanee des jugulaires".toUpperCase(), "turgescence spontanee des veines jugulaires".toUpperCase());
						result.add(elt);}else
						if(elt.contains("UN SOUFFLE SYSTOLIQUE 3/6 MESOCARDIAQUE".toUpperCase())){
						elt=elt.replace("UN SOUFFLE SYSTOLIQUE 3/6 MESOCARDIAQUE".toUpperCase(), "SOUFFLE SYSTOLIQUE 3/6 MESOCARDIAQUE".toUpperCase());
					     result.add(elt);}else
						if(elt.contains("SOUFFLE SYSTOLIQUE PULMONAIRE 2 A3/6".toUpperCase())){
						elt=elt.replace("SOUFFLE SYSTOLIQUE PULMONAIRE 2 A3/6".toUpperCase(), "SOUFFLE SYSTOLIQUE PULMONAIRE 2 A 3/6".toUpperCase());
						result.add(elt);}else
						if(elt.contains("SOUFFLE SYSTOLIQUE PULMONAIRE 2A3/6".toUpperCase())){
						elt=elt.replace("SOUFFLE SYSTOLIQUE PULMONAIRE 2 A 3/6".toUpperCase(), "SOUFFLE SYSTOLIQUE PULMONAIRE 2 A 3/6".toUpperCase());
						result.add(elt);}else
				
						if(elt.contains("deviation choc de pointe".toUpperCase())){
								elt=elt.replace("deviation choc de pointe".toUpperCase(), "deviation choc pointe".toUpperCase());
								result.add(elt);}else
								
						
						if(elt.contains("un souffle".toUpperCase())){
							elt=elt.replace("un souffle".toUpperCase(), "souffle".toUpperCase());
							result.add(elt);}else
							if(elt.contains("vomissents".toUpperCase())){
							elt=elt.replace("vomissents".toUpperCase(), "vomissements".toUpperCase());
							result.add(elt);}else
							if(elt.contains("SOUFLE DIASTOLIQUE 2/6 LATERO STERNAL GAUCHE".toUpperCase())){
							elt=elt.replace("SOUFLE DIASTOLIQUE 2/6 LATERO STERNAL GAUCHE".toUpperCase(), "SOUFFLE DIASTOLIQUE 2/6 LATERO STERNAL GAUCHE".toUpperCase());
							result.add(elt);}else
							if(elt.contains("SOUFLE SYSTOLIQUE 2/6 APEXIEN".toUpperCase())){
							elt=elt.replace("SOUFLE SYSTOLIQUE 2/6 APEXIEN".toUpperCase(), "SOUFFLE SYSTOLIQUE 2/6 APEXIEN".toUpperCase());
							result.add(elt);}else
							if(elt.contains("SOUFLE SYSTOLIQUE CONTINU SOUS CLAVICULAIRE GAUCHE 2/6".toUpperCase())){
							elt=elt.replace("SOUFLE SYSTOLIQUE CONTINU SOUS CLAVICULAIRE GAUCHE 2/6".toUpperCase(), "SOUFFLE SYSTOLIQUE 2/6 CONTINU SOUS CLAVICULAIRE GAUCHE".toUpperCase());
							result.add(elt);}else{
								result.add(elt);
							}
				
		}
		return result;
	}
	
	
	public  int min(int a,int b,int c){
		ArrayList<Integer> i=new ArrayList();
		i.add(a);
		i.add(b);
		i.add(c);
		Collections.sort(i);
		return i.get(0);
	}
	/***
	 * renvoie la distance de levenshtein entre deux chaines
	 * @param chaine1
	 * @param chaine2
	 * @return
	 */
	public  int distance_levenshtein(String chaine1, String chaine2){
		char []tab1=chaine1.toCharArray();
		char []tab2=chaine2.toCharArray();
		int l_chaine1=chaine1.length();
		int l_chaine2=chaine2.length();
		int[][] mat = new int[l_chaine1+1][l_chaine2+1];
		for(int i=0;i<=l_chaine1;i++){
			for(int j=0;j<=l_chaine2;j++){
				if(i==0){mat[i][j]=j;}
				if(j==0){mat[i][j]=i;}
				if(i>0 && j>0){
					if(tab1[i-1]!=tab2[j-1]){
						mat[i][j]=min(mat[i][j-1],mat[i-1][j],mat[i-1][j-1])+1;
					}else{
						mat[i][j]=mat[i-1][j-1];
					}
				}
			}
		}
		return mat[l_chaine1][l_chaine2];

	}
	/**
	 * rnvoi pour chaque chaine les chaines les plus proches de lui via la 
	 * distance de levenshtein specifier
	 * @param a
	 * @return
	 */
	public  TreeMap<String,ArrayList<String>> mesVoisins(HashSet<String> a,int distance_levenshtein){
		ArrayList<String> a1=new ArrayList();
		ArrayList<String> a2=new ArrayList();
		a1.addAll(a);
		a2.addAll(a);
		TreeMap<String, ArrayList<String>> result=new TreeMap<String, ArrayList<String>>();
		for(int i=0;i<a1.size();i++){
			String clef=a1.get(i);
			ArrayList<String> valeur=new ArrayList<String>();
			for(int j=0;j<a2.size();j++){
				String elt=a2.get(j);
				if(!(elt.equalsIgnoreCase(clef))  && distance_levenshtein(elt, clef)<= distance_levenshtein
						&& !(valeur.contains(elt))){
					valeur.add(elt);
				}
			}
			result.put(clef, valeur);
		}
		return result;

	}
	
	/**
	 * renvoi pour chaque chaine une liste de chaines 
	 *ou a chaque chaine est associee 
	 * la distance de levenshtein 
	 * @param a
	 * @return
	 */
	public  TreeMap<String,TreeMap<Integer,String>> mesVoisins_distance(HashSet<String> a){
		ArrayList<String> a1=new ArrayList();
		ArrayList<String> a2=new ArrayList();
		a1.addAll(a);
		a2.addAll(a);
		TreeMap<String, TreeMap<Integer,String>> result=new TreeMap<String, TreeMap<Integer,String>>();
		for(int i=0;i<a1.size();i++){
			String clef=a1.get(i);
			TreeMap<Integer,String> valeur=new TreeMap<Integer,String>();
			for(int j=0;j<a2.size();j++){
				String elt=a2.get(j);
				if(!(elt.equalsIgnoreCase(clef))){
					valeur.put(distance_levenshtein(clef,elt),elt);
				}
			}
			result.put(clef, valeur);
		}
		return result;

	}
	/**
	 * renvoie les n voisins les plus proches d'une chaine a
	 * @param a
	 * @param n
	 * @return
	 */
	public  TreeMap<String,ArrayList<String>> voisinage_plus_proche(TreeMap<String,TreeMap<Integer,String>>
					a,int n){
		TreeMap<String,ArrayList<String>>  result=new TreeMap<String, ArrayList<String>>();
		int i;
		System.out.println(a.size()+" debut ");
		Iterator it = a.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, TreeMap<Integer,String>> entry = (Map.Entry)it.next();
	        String clef=entry.getKey();
	        ArrayList<String> voisinage_plus_proche_de_n=new ArrayList<String>();
	        i=0;
	        TreeMap<Integer,String> valeur=entry.getValue();
	        Iterator it_valeur = valeur.entrySet().iterator();
	        while (it_valeur.hasNext()) {
		        Map.Entry<Integer,String> entry_v = (Map.Entry)it_valeur.next();
		        	//System.out.println(entry_v.getKey() + " = " + entry_v.getValue()); 
		        	voisinage_plus_proche_de_n.add( entry_v.getValue());
		        	i++;
		        	if(i==n){break;}
	        }
	        Collections.sort(voisinage_plus_proche_de_n);
	        result.put(clef, voisinage_plus_proche_de_n);
	    }
		return result;
	}


	public  String list_to_string(ArrayList<String> list){
		String result="";
		if(!list.isEmpty()){
			result=list.get(0);
			for (int i=1;i<list.size();i++){
				result.concat("||"+list.get(i));
				System.out.println("okey");
			}
		}
		return result;
	}
	public  String removeDiacriticalMarks(String string) {
	    return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	public static void main(String []args){
		Methodes p=new Methodes();
		System.out.print(p.distance_levenshtein("11M74.036.0150.016.035.41007510168DEXTROCARDIE+ CIV + STENOSE AORTIQUE",
				"11M74.036.0150.016.035.41007510168DEXTROCARDIE+ CIV + STENOSE AORTIQUE"));
		
	}
}

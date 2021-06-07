package nettoyage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import model.Models;
import nettoyage.Methodes.Patient_maladies;


public class DataSet_build6 {

	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 *             renvoir les listes des patients et l'ensemble des differentes
	 *             maladies
	 */
	public static Patient_maladies diagnostique_ecoder(String file)
			throws IOException {
		
		Methodes m= new Methodes();
		/**dictionnaire de signe **/
		HashSet<String> liste = m.liste_signe (Libelle_clean.diagnostique_ecoder(Models.file).patient);
		
		Patient_maladies retour = null;
		HashSet<String> result = new HashSet<String>();
		ArrayList<Patient> p = new ArrayList<Patient>();
		Patient patient;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";

		br = new BufferedReader(new FileReader(file));
		br.readLine();
		while ((line = br.readLine()) != null) {
			patient = new Patient();
			String[] observation = line.split(cvsSplitBy);
			patient.id_consultation = Integer.parseInt(observation[0]);
			patient.id_patient = Integer.parseInt(observation[1]);
			patient.sexe = observation[2];
			patient.sexe_M = observation[2].trim().equalsIgnoreCase("M") ? 1
					: 0;
			patient.sexe_F = observation[2].trim().equalsIgnoreCase("F") ? 1
					: 0;
			patient.age = Double.parseDouble(observation[3]);
			patient.poids = Double.parseDouble(observation[4]);
			patient.taille = Double.parseDouble(observation[5]);
			patient.imc = Double.parseDouble(observation[6]);
			patient.temperature = Double.parseDouble(observation[7]);
			patient.spo2 = Integer.parseInt(observation[8]);
			patient.freqcard = Integer.parseInt(observation[9]);
			patient.pression_systolique = Integer.parseInt(observation[10]);
			patient.pression_diastolique = Integer.parseInt(observation[11]);
			patient.libellecons = m.removeDiacriticalMarks(observation[12].toUpperCase().replace("  ", " ").replace("\"", "").replace("\n", "").replace("\r\n", "").trim());
			patient.diagnostique=observation[13].toUpperCase();

			for (String e : observation[observation.length - 1].split("\\+")) {
				result.add(e.toUpperCase().trim());
			}

			p.add(patient);
		}
		//ceci concerne l'activation des signes pour chaque patient
				HashMap<String, Double> signe;
				   Models mm=new Models();
				for(int i = 0 ; i < p.size(); i++){
					signe=new HashMap<String, Double>();
					Iterator< String> iter = mm.signe_cor_75.iterator();
					while (iter.hasNext()) {
						String valeur=iter.next();
						signe.put(valeur, (double) (p.get(i).libellecons.contains(valeur)? 1 :0));
					}
					p.get(i).hash_signe_.putAll(signe);


				};
		// ceci concerne l'activation des maladies pour chaque patient
		HashMap<String, Integer> a;

		for (int i = 0; i < p.size(); i++) {
			a = new HashMap<String, Integer>();
			Iterator<String> iter = result.iterator();
			while (iter.hasNext()) {
				String valeur = iter.next();
				a.put(valeur, p.get(i).diagnostique.contains(valeur) ? 1 : 0);
			}
			p.get(i).hash_mal.putAll(a);

		}
		;
		// ceci concerne la categoriesation suivant l'age
		String fichier = Models.file;
		double[] tab_A = m.min_et_max(fichier, 3);
		// intervale par pat de 12 MOIS
		double val_min_A = tab_A[0];
		double val_max_A = tab_A[1];
		System.err.println("val_min "+tab_A[0]);
		System.err.println("val_max "+tab_A[1]);
		HashSet<String> liste_inter_age = m.formation_interval(val_min_A,
				val_max_A, 12);
		System.err.println(liste_inter_age.toString());
		// recuperation de la distance_max intervale
		double dist_max_A = m.distance_max(liste_inter_age);
		System.err.println("dist_max_A "+dist_max_A);
		HashMap<String, Double> map_a = new HashMap<String, Double>();
		Iterator<String> iter_a = liste_inter_age.iterator();
		while (iter_a.hasNext()) {
			map_a.put("A_" + iter_a.next(), (double) 0);
		}
		// Affectation de probabilite d'appartennance pour chaque patient
		for (int i = 0; i < p.size(); i++) {
			p.get(i).hash_age_.putAll(map_a);
			double age = p.get(i).age;
			Iterator it_a = p.get(i).hash_age_.entrySet().iterator();
			while (it_a.hasNext()) {
				Map.Entry<String, Double> entry_a = (Map.Entry) it_a.next();
				String clef = entry_a.getKey();
				int min_a=Integer.parseInt(clef.substring(3,(clef.length()-1)).split(",")[0]);
				int max_a=Integer.parseInt(clef.substring(3,(clef.length()-1)).split(",")[1]);
				if(min_a<=age && age<max_a){
					p.get(i).hash_age_.replace(clef, 1.0);
				}
				
			}

		}

		// ceci concerne la normalisation et la categorisation suivant la
		// freqcard pour chaque patient

		double[] tab = m.min_et_max(fichier, 9);
		// intervale par pat de 9
		double val_min = tab[0];
		double val_max = tab[1];
		HashSet<String> liste_inter_fq = m.formation_interval(val_min, val_max, 9);
		// recuperation de la distance_max intervale
		double dist_max_fq = m.distance_max(liste_inter_fq);
		// System.out.println("dist_max_fq=->"+dist_max_fq);
		HashMap<String, Double> map_fq = new HashMap<String, Double>();
		Iterator<String> iter_fq = liste_inter_fq.iterator();
		while (iter_fq.hasNext()) {
			map_fq.put("Fq_" + iter_fq.next(), (double) 0);
		}
		// 1 si la frequence cardiaque appartient a cet intervalle
		for (int i = 0; i < p.size(); i++) {
			p.get(i).hash_freqcard_.putAll(map_fq);
			double fq = p.get(i).freqcard;
			// System.out.println("freq->"+fq);
			Iterator it_fq = p.get(i).hash_freqcard_.entrySet().iterator();
			while (it_fq.hasNext()) {
				Map.Entry<String, Double> entry_fq = (Map.Entry) it_fq.next();
				String clef = entry_fq.getKey();
				int min_fq=Integer.parseInt(clef.substring(4,(clef.length()-1)).split(",")[0]);
				int max_fq=Integer.parseInt(clef.substring(4,(clef.length()-1)).split(",")[1]);
				if(min_fq<=fq && fq<max_fq){
					p.get(i).hash_freqcard_.replace(clef, 1.0);
				}
				
			}

		}

		// encodage de la colonne corespondant au poids du patient
		double[] tab_p = m.min_et_max(fichier, 4);
		// intervale par pat de 5
		double val_min_p = tab_p[0];
		double val_max_p = tab_p[1];
		/*System.err.println("val_min "+tab_p[0]);
		System.err.println("val_max "+tab_p[1]);*/
		HashSet<String> liste_inter_poids = m.formation_interval(val_min_p,
				val_max_p, 5);
		//System.err.println(liste_inter_poids.toString());
		// recuperation de la distance_max intervale
		double dist_max_p = m.distance_max(liste_inter_poids);
		// System.out.println(dist_max_p);
		HashMap<String, Double> map_poids = new HashMap<String, Double>();
		Iterator<String> iter_p = liste_inter_poids.iterator();
		while (iter_p.hasNext()) {
			map_poids.put("P_" + iter_p.next(), (double) 0);
		}
		// Affectation de probabilite d'appartennance pour chaque patient
		for (int i = 0; i < p.size(); i++) {
			p.get(i).hash_poids_.putAll(map_poids);
			double poids = p.get(i).poids;
			Iterator it_p = p.get(i).hash_poids_.entrySet().iterator();
			while (it_p.hasNext()) {
				Map.Entry<String, Double> entry_p = (Map.Entry) it_p.next();
				String clef = entry_p.getKey();
				p.get(i).hash_poids_.replace(clef,
						m.probabilite_appartenance(poids, clef, ",", dist_max_p));
				
	}

		}

		// encodage de la colonne corespondant au taille t du patient
		double[] tab_t = m.min_et_max(fichier, 5);
		// intervale par pat de 50 CM
		double val_min_t = tab_t[0];
		double val_max_t = tab_t[1];
		HashSet<String> liste_inter_taille = m.formation_interval(val_min_t,
				val_max_t, 50);
		
		// recuperation de la distance_max intervale
		double dist_max_t = m.distance_max(liste_inter_taille);
		HashMap<String, Double> map_taille = new HashMap<String, Double>();
		Iterator<String> iter_t = liste_inter_taille.iterator();
		while (iter_t.hasNext()) {
			map_taille.put("T_" + iter_t.next(), (double) 0);
		}
		// Affectation de probabilite d'appartennance pour chaque patient
		for (int i = 0; i < p.size(); i++) {
			p.get(i).hash_taille_.putAll(map_taille);
			double taille = p.get(i).taille;
			Iterator it_t = p.get(i).hash_taille_.entrySet().iterator();
			while (it_t.hasNext()) {
				Map.Entry<String, Double> entry_t = (Map.Entry) it_t.next();
				String clef = entry_t.getKey();
				int min_t=Integer.parseInt(clef.substring(3,(clef.length()-1)).split(",")[0]);
				int max_t=Integer.parseInt(clef.substring(3,(clef.length()-1)).split(",")[1]);
				if(min_t<=taille && taille<max_t){
					p.get(i).hash_taille_.replace(clef, 1.0);
				}

			}

		}
		// encodage de imc du patient: valeur de imc diviser par la valeur max
		double[] tab_imc = m.min_et_max(fichier, 6);
		double max_imc = tab_imc[1];
		for (int i = 0; i < p.size(); i++) {
			p.get(i).imc_encoder = p.get(i).imc / max_imc;
		}

		// encodage de temps du patient: valeur de temp diviser par la valeur
		// max
		// qui peut atteindre 39,4 °c pour les bebes
		// si superieur a 39,4 alors encoder a 1
		double[] tab_temp = m.min_et_max(fichier, 7);
		double max_temp = tab_temp[1];
		for (int i = 0; i < p.size(); i++) {
			p.get(i).temperature_encoder = (p.get(i).temperature < max_temp
					&& max_temp < (39.4) ? p.get(i).temperature / max_temp : 1);
		}

		// encodage de Spo2 du patient: valeur de Spo2 diviser par 100
		// encodage de pression systolique du patient: valeur diviser par le max
		// soit 140
		// encodage de la pression diastolique du patient: valeur diviser par le
		// max soit 90
		double spo2_pourcentage = 100;
		double max_dias = 90;
		double max_systo = 140;
		for (int i = 0; i < p.size(); i++) {
			p.get(i).spo2_encoder = p.get(i).spo2 / spo2_pourcentage;
			p.get(i).pre_dias_enc = (p.get(i).pression_diastolique < max_dias ? p
					.get(i).pression_diastolique / max_dias
					: 1);
			p.get(i).pre_sys_enc = (p.get(i).pression_systolique < max_systo ? p
					.get(i).pression_systolique / max_systo
					: 1);
		}

		retour.patient = p;
		retour.hash = result;

		return retour;

	}

	/**
	 *
	 * @param p_m
	 * @return parcours la liste des patients contenues dans p_m a la recherche
	 *         des nbres apparitions des maladies renvoi une de maladie et leur
	 *         fraquence
	 */
	public static Map<String, Integer> maladie_frequent(Patient_maladies p_m) {
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

	/***
	 * ecrire dans un fichier excel
	 * 
	 * @param thingsToWrite
	 * @param separator
	 * @param fileName
	 */
	public static void writeToCsvFile(Patient_maladies ph, String separator,
			String fileName) {
		try (FileWriter writer = new FileWriter(fileName)) {
			writer.append(String.valueOf("id_consultation"));
			writer.append(separator);
			writer.append(String.valueOf("id_patient"));
			writer.append(separator);
			// writer.append(String.valueOf("sexe"));
			// writer.append(separator);
			writer.append(String.valueOf("sexe_M"));
			writer.append(separator);
			writer.append(String.valueOf("sexe_F"));
			writer.append(separator);
			// writer.append(String.valueOf("age"));
			// writer.append(separator);

			// je prends un patient et je recupere les intervales d'age
			Map<String, Double> v_a = ph.patient.get(0).hash_age_;
			Iterator it_a = v_a.entrySet().iterator();
			while (it_a.hasNext()) {
				Map.Entry<String, Double> entry = (Map.Entry) it_a.next();
				writer.append(String.valueOf(entry.getKey()));
				writer.append(separator);
			}
			// writer.append(String.valueOf("poids"));
			// writer.append(separator);

			// je prends un patient et je recupere les intervales de poids
			Map<String, Double> v_p = ph.patient.get(0).hash_poids_;
			Iterator it_p = v_p.entrySet().iterator();
			while (it_p.hasNext()) {
				Map.Entry<String, Double> entry = (Map.Entry) it_p.next();
				writer.append(String.valueOf(entry.getKey()));
				writer.append(separator);
			}

			// writer.append(String.valueOf("taille"));
			// writer.append(separator);
			// je prends un patient et je recupere les intervales d'age
			Map<String, Double> v_t = ph.patient.get(0).hash_taille_;
			Iterator it_t = v_t.entrySet().iterator();
			while (it_t.hasNext()) {
				Map.Entry<String, Double> entry = (Map.Entry) it_t.next();
				writer.append(String.valueOf(entry.getKey()));
				writer.append(separator);
			}

			// writer.append(String.valueOf("imc"));
			// writer.append(separator);
			// imc encoder
			writer.append(String.valueOf("imc_encoder"));
			writer.append(separator);

			// writer.append(String.valueOf("temperature"));
			// writer.append(separator);
			// temp encoder
			writer.append(String.valueOf("temperature_encoder"));
			writer.append(separator);

			// writer.append(String.valueOf("spo2"));
			// writer.append(separator);
			// spo2 encoder
			writer.append(String.valueOf("spo2_encoder"));
			writer.append(separator);

			// writer.append(String.valueOf("freqcard"));
			// writer.append(separator);
			// je prends un patient et je recupere les intervales freqcard
			Map<String, Double> v_fq = ph.patient.get(0).hash_freqcard_;
			Iterator it_fq = v_fq.entrySet().iterator();
			while (it_fq.hasNext()) {
				Map.Entry<String, Double> entry = (Map.Entry) it_fq.next();
				writer.append(String.valueOf(entry.getKey()));
				writer.append(separator);
			}

			// writer.append(String.valueOf("pression_systolique"));
			// writer.append(separator);
			// pression sytolique encoder
			writer.append(String.valueOf("pre_sys_enc"));
			writer.append(separator);

			// writer.append(String.valueOf("pression_diastolique"));
			// writer.append(separator);
			writer.append(String.valueOf("pre_dias_enc"));
			writer.append(separator);

			writer.append(String.valueOf("diagnostique"));
			// je prends un patient et je recupere les noms des maladies
			Map<String, Integer> v = ph.patient.get(0).hash_mal;

			Iterator it = v.entrySet().iterator();
			while (it.hasNext()) {
				writer.append(separator);
				Map.Entry<String, Integer> entry = (Map.Entry) it.next();
				writer.append(String.valueOf("D_" + entry.getKey()));

			}
			writer.append(System.lineSeparator());
			for (int y = 0; y < ph.patient.size(); y++) {
				writer.append(String.valueOf(ph.patient.get(y).id_consultation));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).id_patient));
				writer.append(separator);
				// writer.append(String.valueOf(ph.patient.get(y).sexe));
				// writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).sexe_M));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).sexe_F));
				writer.append(separator);
				// writer.append(String.valueOf(ph.patient.get(y).age));
				// writer.append(separator);

				// je recupere les valeurs d'age
				Map<String, Double> v2_a = ph.patient.get(y).hash_age_;
				Iterator val_a = v2_a.entrySet().iterator();
				while (val_a.hasNext()) {
					Map.Entry<String, Double> entry = (Map.Entry) val_a.next();
					writer.append(String.valueOf(entry.getValue()));
					writer.append(separator);

				}

				// writer.append(String.valueOf(ph.patient.get(y).poids));
				// writer.append(separator);
				// je recupere les valeurs d'age
				Map<String, Double> v2_p = ph.patient.get(y).hash_poids_;
				Iterator val_p = v2_p.entrySet().iterator();
				while (val_p.hasNext()) {
					Map.Entry<String, Double> entry = (Map.Entry) val_p.next();
					writer.append(String.valueOf(entry.getValue()));
					writer.append(separator);

				}

				// writer.append(String.valueOf(ph.patient.get(y).taille));
				// writer.append(separator);
				// taille encoder
				Map<String, Double> v2_t = ph.patient.get(y).hash_taille_;
				Iterator val_t = v2_t.entrySet().iterator();
				while (val_t.hasNext()) {
					Map.Entry<String, Integer> entry = (Map.Entry) val_t.next();
					writer.append(String.valueOf(entry.getValue()));
					writer.append(separator);

				}

				// writer.append(String.valueOf(ph.patient.get(y).imc));
				// writer.append(separator);
				// encodage imc
				writer.append(String.valueOf(ph.patient.get(y).imc_encoder));
				writer.append(separator);

				// writer.append(String.valueOf(ph.patient.get(y).temperature));
				// writer.append(separator);
				// encodage temperature
				writer.append(String.valueOf(ph.patient.get(y).temperature_encoder));
				writer.append(separator);

				// writer.append(String.valueOf(ph.patient.get(y).spo2));
				// writer.append(separator);
				// encodage spo2
				writer.append(String.valueOf(ph.patient.get(y).spo2_encoder));
				writer.append(separator);

				// writer.append(String.valueOf(ph.patient.get(y).freqcard));
				// writer.append(separator);
				// encodage freqcard
				Map<String, Double> v2_fq = ph.patient.get(y).hash_freqcard_;
				Iterator val_fq = v2_fq.entrySet().iterator();
				while (val_fq.hasNext()) {
					Map.Entry<String, Double> entry = (Map.Entry) val_fq.next();
					writer.append(String.valueOf(entry.getValue()));
					writer.append(separator);

				}

				// writer.append(String.valueOf(ph.patient.get(y).pression_systolique));
				// writer.append(separator);
				// encodage pression systolique
				writer.append(String.valueOf(ph.patient.get(y).pre_sys_enc));
				writer.append(separator);

				// writer.append(String.valueOf(ph.patient.get(y).pression_diastolique));
				// writer.append(separator);
				// encodage de pression diastolique
				writer.append(String.valueOf(ph.patient.get(y).pre_dias_enc));
				writer.append(separator);

				// writer.append(String.valueOf(ph.patient.get(y).diagnostique));
				// je prends un patient et je recupere les valeurs des maladies
				Map<String, Integer> v2 = ph.patient.get(y).hash_mal;
				Iterator val = v2.entrySet().iterator();
				while (val.hasNext()) {
					writer.append(separator);
					Map.Entry<String, Integer> entry = (Map.Entry) val.next();
					writer.append(String.valueOf(entry.getValue()));

				}
				writer.append(System.lineSeparator());
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeToCsvFiles(ArrayList<Patient> ph, String separator,
			String fileName) {
		try (FileWriter writer = new FileWriter(fileName)) {
			
			 /*writer.append(String.valueOf("id_consultation"));
			 writer.append(separator);
			 writer.append(String.valueOf("id_patient"));
			 writer.append(separator);*/
			 
			// writer.append(String.valueOf("sexe"));
			// writer.append(separator);
			/*writer.append(String.valueOf("sexe_M"));
			writer.append(separator);*/
			/*writer.append(String.valueOf("sexe_F"));
			writer.append(separator);*/
			// writer.append(String.valueOf("age"));
			// writer.append(separator); */

			// je prends un patient et je recupere les intervales d'age
			/*Map<String, Double> v_a = ph.get(0).hash_age_;
			Iterator it_a = v_a.entrySet().iterator();
			while (it_a.hasNext()) {
				Map.Entry<String, Double> entry = (Map.Entry) it_a.next();
				writer.append(String.valueOf(entry.getKey()));
				writer.append(separator);
			}*/
			// writer.append(String.valueOf("poids"));
			// writer.append(separator);

			// je prends un patient et je recupere les intervales de poids
			/*Map<String, Double> v_p = ph.get(0).hash_poids_;
			Iterator it_p = v_p.entrySet().iterator();
			while (it_p.hasNext()) {
				Map.Entry<String, Double> entry = (Map.Entry) it_p.next();
				writer.append(String.valueOf(entry.getKey()));
				writer.append(separator);
			}  */

			// writer.append(String.valueOf("taille"));
			// writer.append(separator);
			// je prends un patient et je recupere les intervales d'age
			/*Map<String, Double> v_t = ph.get(0).hash_taille_;
			Iterator it_t = v_t.entrySet().iterator();
			while (it_t.hasNext()) {
				Map.Entry<String, Double> entry = (Map.Entry) it_t.next();
				writer.append(String.valueOf(entry.getKey()));
				writer.append(separator);
			}*/

			// writer.append(String.valueOf("imc"));
			// writer.append(separator);
			// imc encoder
			/*writer.append(String.valueOf("imc_encoder"));
			writer.append(separator);*/

			// writer.append(String.valueOf("temperature"));
			// writer.append(separator);*/
			// temp encoder
			/*writer.append(String.valueOf("temperature_encoder"));
			writer.append(separator);  */

			// writer.append(String.valueOf("spo2"));
			// writer.append(separator);
			// spo2 encoder
			/*writer.append(String.valueOf("spo2_encoder"));
			writer.append(separator);*/

			// writer.append(String.valueOf("freqcard"));
			// writer.append(separator);
			// je prends un patient et je recupere les intervales freqcard
		/*	Map<String, Double> v_fq = ph.get(0).hash_freqcard_;
			Iterator it_fq = v_fq.entrySet().iterator();
			while (it_fq.hasNext()) {
				Map.Entry<String, Double> entry = (Map.Entry) it_fq.next();
				writer.append(String.valueOf(entry.getKey()));
				writer.append(separator);
			}*/

			// writer.append(String.valueOf("pression_systolique"));
			// writer.append(separator);
			// pression sytolique encoder
		/*	writer.append(String.valueOf("pre_sys_enc"));
			writer.append(separator);*/

			// writer.append(String.valueOf("pression_diastolique"));
			// writer.append(separator);
		/*	writer.append(String.valueOf("pre_dias_enc"));
			writer.append(separator);*/

			/**signe  **/
			Map<String, Double> sig = ph.get(0).hash_signe_;

			Iterator it_s = sig.entrySet().iterator();
			while (it_s.hasNext()) {
				Map.Entry<String, Double> entry = (Map.Entry)it_s.next();
				writer.append(String.valueOf("S_"+entry.getKey()));
				writer.append(separator);

			}
			// writer.append(String.valueOf("diagnostique"));
			// je prends un patient et je recupere les noms des maladies
			Map<String, Integer> v = ph.get(0).hash_mal;

			Iterator it = v.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> entry = (Map.Entry) it.next();
				writer.append(String.valueOf("D_" + entry.getKey()));
				writer.append(separator);

			}
			writer.append(System.lineSeparator());
			for (int y = 0; y < ph.size(); y++) {
				
				 /* writer.append(String.valueOf(ph.get(y).id_consultation));
				  writer.append(separator);
				  writer.append(String.valueOf(ph.get(y).id_patient));
				  writer.append(separator);*/
				 
				// writer.append(String.valueOf(ph.patient.get(y).sexe));
				// writer.append(separator);
				/*writer.append(String.valueOf(ph.get(y).sexe_M));
				writer.append(separator);*/
				/*writer.append(String.valueOf(ph.get(y).sexe_F));
				writer.append(separator);*/
				// writer.append(String.valueOf(ph.patient.get(y).age));
				// writer.append(separator); */

				// je recupere les valeurs d'age
				/*Map<String, Double> v2_a = ph.get(y).hash_age_;
				Iterator val_a = v2_a.entrySet().iterator();
				while (val_a.hasNext()) {
					Map.Entry<String, Double> entry = (Map.Entry) val_a.next();
					writer.append(String.valueOf(entry.getValue()));
					writer.append(separator);

				}*/

				// writer.append(String.valueOf(ph.patient.get(y).poids));
				// writer.append(separator);
				// je recupere les valeurs d'age
			/*	Map<String, Double> v2_p = ph.get(y).hash_poids_;
				Iterator val_p = v2_p.entrySet().iterator();
				while (val_p.hasNext()) {
					Map.Entry<String, Double> entry = (Map.Entry) val_p.next();
					writer.append(String.valueOf(entry.getValue()));
					writer.append(separator);

				}*/

				// writer.append(String.valueOf(ph.patient.get(y).taille));
				// writer.append(separator);
				// taille encoder
				/*Map<String, Double> v2_t = ph.get(y).hash_taille_;
				Iterator val_t = v2_t.entrySet().iterator();
				while (val_t.hasNext()) {
					Map.Entry<String, Integer> entry = (Map.Entry) val_t.next();
					writer.append(String.valueOf(entry.getValue()));
					writer.append(separator);

				}*/

				// writer.append(String.valueOf(ph.patient.get(y).imc));
				// writer.append(separator);
				// encodage imc
				/*writer.append(String.valueOf(ph.get(y).imc_encoder));
				writer.append(separator);*/

				// writer.append(String.valueOf(ph.patient.get(y).temperature));
				// writer.append(separator);
				// encodage temperature
				/*writer.append(String.valueOf(ph.get(y).temperature_encoder));
				writer.append(separator);*/  

				// writer.append(String.valueOf(ph.patient.get(y).spo2));
				// writer.append(separator);
				// encodage spo2
			/*	writer.append(String.valueOf(ph.get(y).spo2_encoder));
				writer.append(separator);*/

				// writer.append(String.valueOf(ph.patient.get(y).freqcard));
				// writer.append(separator);
				// encodage freqcard
			/*	Map<String, Double> v2_fq = ph.get(y).hash_freqcard_;
				Iterator val_fq = v2_fq.entrySet().iterator();
				while (val_fq.hasNext()) {
					Map.Entry<String, Double> entry = (Map.Entry) val_fq.next();
					writer.append(String.valueOf(entry.getValue()));
					writer.append(separator);

				}*/

				// writer.append(String.valueOf(ph.patient.get(y).pression_systolique));
				// writer.append(separator);
				// encodage pression systolique
		/*		writer.append(String.valueOf(ph.get(y).pre_sys_enc));
				writer.append(separator);*/

				// writer.append(String.valueOf(ph.patient.get(y).pression_diastolique));
				// writer.append(separator);
				// encodage de pression diastolique
			/*	writer.append(String.valueOf(ph.get(y).pre_dias_enc));
				writer.append(separator);*/
				/**signe **/
				Map<String, Double> sig_ = ph.get(y).hash_signe_;
				Iterator val_s = sig_.entrySet().iterator();
				while (val_s.hasNext()) {
					Map.Entry<String, Double> entry = (Map.Entry)val_s.next();
					writer.append(String.valueOf(entry.getValue()));
					writer.append(separator);
				}
				// writer.append(String.valueOf(ph.patient.get(y).diagnostique));
				// je prends un patient et je recupere les valeurs des maladies
				Map<String, Integer> v2 = ph.get(y).hash_mal;
				Iterator val = v2.entrySet().iterator();
				while (val.hasNext()) {
					Map.Entry<String, Integer> entry = (Map.Entry) val.next();
					writer.append(String.valueOf(entry.getValue()));
					writer.append(separator);

				}
				writer.append(System.lineSeparator());
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String file = Models.file;
		Patient_maladies pt = diagnostique_ecoder(file);
		Methodes m= new Methodes();
		//double[] tab = min_et_max(file, 4);
		//System.err.println(tab[0]);
		//System.err.println(tab[1]);
		
		// writeToCsvFile(pt, ";",
		// "D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\patient_dataSet_M3_normalise_probabilise.csv");

		Map<String, Integer> mal_freq = maladie_frequent(pt);
		double pourcent_split = Models.split_;
		/**
		 * cette variable est dangereuse car en fonction de ca valeur peut nous
		 * produire des liste vide de patient et ainsi ne pas creer de fichier.
		 */
		int freq_apparition = Models.freq_app;
		Map<String, ArrayList<Patient>> donnees = new TreeMap<String, ArrayList<Patient>>();
		donnees = m.split_ens_freq(pt, mal_freq, pourcent_split, freq_apparition);
		// split_ens_freq_v2(pt, mal_freq, pourcent_split, freq_apparition);
		Iterator i = donnees.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, ArrayList<Patient>> entry = (Map.Entry) i.next();
			ArrayList<Patient> liste_patient = entry.getValue();
			if (liste_patient.isEmpty() == false)
				writeToCsvFiles(
						liste_patient,
						";",
						"D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\"
								+ "version_final\\juste_les_signes_correle_moins_de_75_pour\\"
								+ entry.getKey()
								+ "_dataSet_normalise_poids_probabilise.csv");

		}

		System.err.println("okey");
	}

}

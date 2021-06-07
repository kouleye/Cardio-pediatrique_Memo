package nettoyage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class DataSet_build {

	static class Patient_maladies{
		static ArrayList<Patient>  patient;
		static HashSet<String> hash;
	};

	/**
	 * 
	 * @param file
	 * file est notre data set de depart contenant la colonne diagnostique
	 * pour chaque observation, diagnostique a plus d'une maladie 
	 * nous allons eclater diagnostique et chaque maladie representera 
	 * une nouvelle colonne
	 * ainsi une observation vera sa colonne maladie a 1 si il est atteint de cette derniere
	 * 0 sinon
	 * nous stockons le resultat dans un tableau excel
	 * @throws IOException 
	 */


	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * renvoir les listes des patients et l'ensemble des differentes maladies
	 */
	public static Patient_maladies diagnostique_eclate(String file) throws IOException{
		Patient_maladies retour = null;
		HashSet<String> result = new HashSet<String>();
		ArrayList<Patient> p=new ArrayList<Patient>();
		Patient patient ;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		br = new BufferedReader(new FileReader(file));
		br.readLine();
		while ((line = br.readLine()) != null) {
			patient = new Patient();
			String[] observation = line.split(cvsSplitBy);
			patient.id_consultation=Integer.parseInt(observation[0]);
			patient.id_patient=Integer.parseInt(observation[1]);
			patient.sexe=observation[2];
			patient.sexe_M=observation[2].trim().equalsIgnoreCase("M") ? 1 : 0;
			patient.sexe_F=observation[2].trim().equalsIgnoreCase("F") ? 1 : 0;
			patient.age=Double.parseDouble(observation[3]);
			patient.poids=Double.parseDouble(observation[4]);
			patient.taille=Double.parseDouble(observation[5]);
			patient.imc=Double.parseDouble(observation[6]);
			patient.temperature=Double.parseDouble(observation[7]);
			patient.spo2=Integer.parseInt(observation[8]);
			patient.freqcard=Integer.parseInt(observation[9]);
			patient.pression_systolique=Integer.parseInt(observation[10]);
			patient.pression_diastolique=Integer.parseInt(observation[11]);
			patient.diagnostique=observation[12];
			
			for(String e:observation[observation.length-1].split("\\+")){
				result.add(e.trim());
			}

			p.add(patient);
		}
		
		HashMap<String, Integer> a;
		
		for(int i = 0 ; i < p.size(); i++){
			a=new HashMap<String, Integer>();
			Iterator< String> iter = result.iterator();
			while (iter.hasNext()) {
				String valeur=iter.next();
				a.put(valeur, p.get(i).diagnostique.contains(valeur)? 1 :0);
			}
			p.get(i).hash_mal.putAll(a);


		};
 
		retour.patient=p;
		retour.hash=result;


		return retour;


	}

	/**
	 * return une matrix tel que chacune des colones est
	 * a 1 si la maladie de cette colonne est identifier 
	 * dans le diagnostique du patient en question et 0 si non
	 * @param p
	 * @return
	 */
	public static int[][] matrix_maladie(Patient_maladies p_m){
		int rows=p_m.patient.size();
		int cols=p_m.hash.size();
		int vecteur[];
		int[][] matrix = new int [rows][cols];
		
		return matrix;

	}

	/***
	 * ecrire dans un fichier excel
	 * @param thingsToWrite
	 * @param separator
	 * @param fileName
	 */
	public static void writeToCsvFile(Patient_maladies ph,String separator, String fileName) {
		try (FileWriter writer = new FileWriter(fileName)) {
			writer.append(String.valueOf("id_consultation"));
			writer.append(separator);
			writer.append(String.valueOf("id_patient"));
			writer.append(separator);
			writer.append(String.valueOf("sexe"));
			writer.append(separator);
			writer.append(String.valueOf("sexe_M"));
			writer.append(separator);
			writer.append(String.valueOf("sexe_F"));
			writer.append(separator);
			writer.append(String.valueOf("age"));
			writer.append(separator);
			writer.append(String.valueOf("poids"));
			writer.append(separator);
			writer.append(String.valueOf("taille"));
			writer.append(separator);
			writer.append(String.valueOf("imc"));
			writer.append(separator);
			writer.append(String.valueOf("temperature"));
			writer.append(separator);
			writer.append(String.valueOf("spo2"));
			writer.append(separator);
			writer.append(String.valueOf("freqcard"));
			writer.append(separator);
			writer.append(String.valueOf("pression_systolique"));
			writer.append(separator);
			writer.append(String.valueOf("pression_diastolique"));
			writer.append(separator);
			writer.append(String.valueOf("diagnostique"));
			//je prends un patient et je recupere les noms des maladies
			Map<String, Integer> v = ph.patient.get(0).hash_mal;
		
			Iterator it = v.entrySet().iterator();
		    while (it.hasNext()) {
		    	writer.append(separator);
		        Map.Entry<String, Integer> entry = (Map.Entry)it.next();
		        writer.append(String.valueOf("D_"+entry.getKey()));
		        
		    }
			writer.append(System.lineSeparator());
			for (int y=0;y<ph.patient.size();y++) {
				writer.append(String.valueOf(ph.patient.get(y).id_consultation));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).id_patient));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).sexe));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).sexe_M));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).sexe_F));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).age));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).poids));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).taille));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).imc));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).temperature));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).spo2));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).freqcard));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).pression_systolique));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).pression_diastolique));
				writer.append(separator);
				writer.append(String.valueOf(ph.patient.get(y).diagnostique));
				//je prends un patient et je recupere les valeurs des maladies
				Map<String, Integer> v2 = ph.patient.get(y).hash_mal;
				Iterator val = v2.entrySet().iterator();
			    while (val.hasNext()) {
			    	writer.append(separator);
			        Map.Entry<String, Integer> entry = (Map.Entry)val.next();
			        writer.append(String.valueOf(entry.getValue()));
			        
			    }
				writer.append(System.lineSeparator());
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * renvoir les listes des patients et l'ensemble des differentes maladies
	 */
	public static Patient_maladies temperature_ecode(String file) throws IOException{
		Patient_maladies retour = null;
		HashSet<String> result = new HashSet<String>();
		ArrayList<Patient> p=new ArrayList<Patient>();
		Patient patient ;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		br = new BufferedReader(new FileReader(file));
		br.readLine();
		while ((line = br.readLine()) != null) {
			patient = new Patient();
			String[] observation = line.split(cvsSplitBy);
			patient.id_consultation=Integer.parseInt(observation[0]);
			patient.id_patient=Integer.parseInt(observation[1]);
			patient.sexe=observation[2];
			patient.sexe_M=observation[2].trim().equalsIgnoreCase("M") ? 1 : 0;
			patient.sexe_F=observation[2].trim().equalsIgnoreCase("F") ? 1 : 0;
			patient.age=Double.parseDouble(observation[3]);
			patient.poids=Double.parseDouble(observation[4]);
			patient.taille=Double.parseDouble(observation[5]);
			patient.imc=Double.parseDouble(observation[6]);
			patient.temperature=Double.parseDouble(observation[7]);
			patient.spo2=Integer.parseInt(observation[8]);
			patient.freqcard=Integer.parseInt(observation[9]);
			patient.pression_systolique=Integer.parseInt(observation[10]);
			patient.pression_diastolique=Integer.parseInt(observation[11]);
			patient.diagnostique=observation[12];
			
			for(String e:observation[observation.length-1].split("\\+")){
				result.add(e.trim());
			}

			p.add(patient);
		}
		
		HashMap<String, Integer> a;
		
		for(int i = 0 ; i < p.size(); i++){
			a=new HashMap<String, Integer>();
			Iterator< String> iter = result.iterator();
			while (iter.hasNext()) {
				String valeur=iter.next();
				a.put(valeur, p.get(i).diagnostique.contains(valeur)? 1 :0);
			}
			p.get(i).hash_mal.putAll(a);


		};
 
		retour.patient=p;
		retour.hash=result;


		return retour;


	}
	
	

	public static void main(String[] args) throws IOException {
		System.err.println("-- begin --");
		// TODO Auto-generated method stub
	/*	String file="D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\patient_dataSet_input.csv";
		Patient_maladies resultat = diagnostique_eclate(file);
		writeToCsvFile(resultat, ",", "D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\patient_dataSet_M1.csv");
        System.out.println("ok");*/
	}


}

package nettoyage;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.Models;
import nettoyage.Methodes.Patient_maladies;



public class Libelle_clean {

	
	

	public static Patient_maladies diagnostique_ecoder(String file)
			throws IOException {
		Methodes n=new Methodes();
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
			
			String chaine=n.removeDiacriticalMarks(observation[12].toUpperCase().replace("  ", " ").replace("\"", "").replace("\n", "").replace("\r\n", "").trim());
			while(chaine.contains("  ")){
				chaine=chaine.replace("  ", " ");
			}
			patient.libellecons = chaine;
			patient.diagnostique = observation[13].toUpperCase();

			p.add(patient);
		}
		
		retour.patient = p;
		retour.hash = result;

		return retour;

	}
	public static void libel_affiche(ArrayList<Patient> p){
		for(int i=0;i<p.size();i++){

			System.out.println("id_patient :"+String.valueOf(p.get(i).id_consultation)+"  Libelle :"+p.get(i).libellecons+"\n"+"suivant");
		}
	}
	
	
	public static void writeToCsvFiles(ArrayList<Patient> ph, String separator,
			String fileName) {
		
		try (FileWriter writer = new FileWriter(fileName)) {
			writer.append(String.valueOf("Signes"));
			writer.append(separator);
			writer.append(String.valueOf("Signes similaires"));
			writer.append(separator);
			writer.append(System.lineSeparator());
			// je iteres pour recuperer au fur et amesure les valeur
			for (int y = 0; y < ph.size(); y++) {		
				writer.append(String.valueOf(ph.get(y).moi));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).voisin));
				writer.append(separator);
				//System.out.println(ph.get(y).moi + " = " + ph.get(y).voisin); 
				writer.append(System.lineSeparator());
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//libel_affiche(diagnostique_ecoder("D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\newdata.csv").patient);
		Methodes m=new Methodes();
		String fichier=Models.file;
		HashSet<String> l = m.liste_signe (diagnostique_ecoder(fichier).patient);
		HashSet<String> liste=m.correcteur(l);
		writeToCsvFiles(
				m.chargement(m.voisinage_plus_proche(m.mesVoisins_distance(liste), 3)),
				";",
				"D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\"
						+ "Libelle_dataSet1.csv");

		//chargement(mesVoisins(liste,2));
		
		System.out.print("fin_levenshtein");		
		System.out.println(m.distance_levenshtein("dyspnée d'effort avec squatting", "dyspnée effort"));
		System.out.print(m.removeDiacriticalMarks("POULS PÉRIPHÉRIQUES BIEN PERÇUS").
				equalsIgnoreCase(m.removeDiacriticalMarks("POULS PÉRIPHÉRIQUES BIEN PERÇUS")));
		/*Methodes m=new Methodes();
		HashSet<String> liste = m.liste_signe (diagnostique_ecoder(Models.file).patient);
		System.out.println(liste.toString());
		System.out.println(correcteur(liste).toString());
		System.out.println("liste"+liste.size());
		System.out.println("correcteur"+correcteur(liste).size());*/
	
		
	
	}

}


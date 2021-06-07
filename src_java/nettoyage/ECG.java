package nettoyage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import model.Models;
import nettoyage.Methodes.Patient_maladies;

public class ECG {
	
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * renvoir les listes des patients 
	 */
	public static ArrayList<Patient> consultation_patient(String file) throws IOException{

		ArrayList<Patient> p=new ArrayList<Patient>();
		Patient patient ;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
	
		br = new BufferedReader(new FileReader(file));
		br.readLine();
		int i=0;
		while ((line = br.readLine()) != null) {
			patient = new Patient();
			String[] observation = line.split(cvsSplitBy);
			patient.id_consultation=Integer.parseInt(observation[0]);
			patient.id_patient=Integer.parseInt(observation[1]);
			patient.nom=(observation[2].equalsIgnoreCase(null)?"":observation[2]);
			patient.prenom=(observation[3].equalsIgnoreCase(" ")?"":observation[3]);
			i++;
			p.add(patient);
			//System.out.println("ligne "+i+"=>"+ observation[0]+" "+observation[1]+" "+patient.nom+" "+patient.prenom);
			
		}
		
		return p;
	
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * renvoir les listes des patients 
	 */
	public static ArrayList<Patient> consultation_finale(String file) throws IOException{
		
		ArrayList<Patient> p=new ArrayList<Patient>();
		Patient patient ;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		br = new BufferedReader(new FileReader(file));
		br.readLine();
		while ((line = br.readLine()) != null) {
			patient = new Patient();
			String[] observation = line.split(cvsSplitBy);
			patient.id_consultation=Integer.parseInt(observation[0]);
			patient.id_patient=Integer.parseInt(observation[1]);
			patient.sexe=observation[2];
			patient.age=Double.parseDouble(observation[3]);
			patient.poids=Double.parseDouble(observation[4]);
			patient.taille=Double.parseDouble(observation[5]);
			patient.imc=Double.parseDouble(observation[6]);
			patient.temperature=Double.parseDouble(observation[7]);
			patient.spo2=Integer.parseInt(observation[8]);
			patient.freqcard=Integer.parseInt(observation[9]);
			patient.pression_systolique=Integer.parseInt(observation[10]);
			patient.pression_diastolique=Integer.parseInt(observation[11]);
			patient.libellecons = observation[12];
			patient.diagnostique=observation[13].toUpperCase();
			p.add(patient);
		}
		return p;
		
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * renvoir les listes des patients 
	 */
	public static ArrayList<Patient> patients_(ArrayList<Patient> liste1_,ArrayList<Patient> liste2_){
		for(Patient p1:liste1_){
			for(Patient p2:liste2_){
				if(p1.id_consultation==p2.id_consultation && p1.id_patient==p2.id_patient){
				p1.nom=p2.nom;
				p1.prenom=p2.prenom;
				}
			}
			
		}
		return liste1_;	
	}
	
	/**
	 * ecriture dans un fichier csv 
	 * @param ph
	 * @param separator
	 * @param fileName
	 */
	public static void writeToCsvFiles(ArrayList<Patient> ph,String separator, String fileName) {
		try (FileWriter writer = new FileWriter(fileName)) {
			writer.append(String.valueOf("id_consultation"));
			writer.append(separator);
			writer.append(String.valueOf("id_patient"));
			writer.append(separator);
			writer.append(String.valueOf("nom"));
			writer.append(separator);
			writer.append(String.valueOf("prenom"));
			writer.append(separator);
			
			writer.append(String.valueOf("sexe"));
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
			//libellecons
			writer.append(String.valueOf("libellecons"));
			writer.append(separator);
			writer.append(String.valueOf("diagnostique"));
			
			writer.append(System.lineSeparator());
			for (int y=0;y<ph.size();y++) {
				writer.append(String.valueOf(ph.get(y).id_consultation));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).id_patient));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).nom));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).prenom));
				writer.append(separator);
				
				writer.append(String.valueOf(ph.get(y).sexe));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).age));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).poids));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).taille));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).imc));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).temperature));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).spo2));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).freqcard));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).pression_systolique));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).pression_diastolique));
				writer.append(separator);
				writer.append(String.valueOf(ph.get(y).libellecons));
				writer.append(separator);
				
				writer.append(String.valueOf(ph.get(y).diagnostique));
				
				writer.append(System.lineSeparator());
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ArrayList<Patient> liste_consultation = consultation_patient("D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2"
				+ "\\pratique\\ECG\\myFile.csv");
		ArrayList<Patient> liste_final = consultation_finale("D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2"
				+ "\\pratique\\ECG\\newdata.csv");
		//System.out.println(patients_(liste_final,liste_consultation).toString());
		writeToCsvFiles(patients_(liste_final,liste_consultation)
				,";", "D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2"
						+ "\\pratique\\ECG\\newdata_final.csv");
		System.out.println("fin");
		
		
	}

}

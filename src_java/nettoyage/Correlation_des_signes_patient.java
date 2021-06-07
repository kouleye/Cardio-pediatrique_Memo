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
import java.util.TreeMap;
import java.util.Map.Entry;

import model.Models;
import nettoyage.Methodes.Patient_maladies;

public class Correlation_des_signes_patient {

	public static double split_=20;
	
	public static HashSet<String> recuperation_signe(String file) throws IOException{
		HashSet<String> result=new HashSet<>();
		
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";

		br = new BufferedReader(new FileReader(file));
		br.readLine();
		while ((line = br.readLine()) != null) {
			String[] signes = line.split(cvsSplitBy);
			String signe= signes[0].toUpperCase().replace("  ", " ").replace("\"", "").replace("\n", "").replace("\r\n", "").trim();
			result.add(signe);
		}
		return result;	
	}
	
	
	
	
	
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
		/**liste de patient et leurs constante et signe respective**/
		ArrayList<Patient> nos_observations=Libelle_clean.diagnostique_ecoder(Models.file).patient;

		/**dictionnaire de signe **/
		HashSet<String> liste = m.liste_signe (nos_observations);	
		HashMap<String,Integer>   dict_liste=new HashMap<>();
		Iterator< String> iterer = liste.iterator();
		int j=0;
		while (iterer.hasNext()) {
			String valeur=iterer.next();
			dict_liste.put(valeur, j+1);
			j++;
		}
		
		

		Patient_maladies retour = null;
		HashSet<String> result = new HashSet<String>();
		ArrayList<Patient> p = Libelle_clean.diagnostique_ecoder(Models.file).patient;
		Patient patient;
		BufferedReader br = null;

			//ceci concerne l'activation des signes pour chaque patient
			HashMap<String, Double> signe;

			for(int i = 0 ; i < p.size(); i++){
				signe=new HashMap<String, Double>();
				Iterator< String> iter = liste.iterator();
				while (iter.hasNext()) {
					String valeur=iter.next();
					//**principe de generation de matrice 1 -0 **//*
					//signe.put(valeur, (double) (p.get(i).libellecons.contains(valeur)? 1 :0));
					/**principe deux avec des identifiant **/
					signe.put(valeur, (double) (p.get(i).libellecons.contains(valeur)? dict_liste.get(valeur)  :0));
				}
				p.get(i).hash_signe_.putAll(signe);


			};



			retour.patient = p;
			retour.hash = result;

			return retour;

		}

		public static void writeToCsvFiles(ArrayList<Patient> ph, String separator,
				String fileName) {
			try (FileWriter writer = new FileWriter(fileName)) {


				Map<String, Double> sig = ph.get(0).hash_signe_;

				Iterator it_s = sig.entrySet().iterator();
				while (it_s.hasNext()) {
					Map.Entry<String, Double> entry = (Map.Entry)it_s.next();
					writer.append(String.valueOf(entry.getKey()));
					writer.append(separator);

				}

				writer.append(System.lineSeparator());
				for (int y = 0; y < ph.size(); y++) {


					/**signe **/
					Map<String, Double> sig_ = ph.get(y).hash_signe_;
					Iterator val_s = sig_.entrySet().iterator();
					while (val_s.hasNext()) {
						Map.Entry<String, Double> entry = (Map.Entry)val_s.next();
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
			Models m =new Models();
			System.out.println("==");
			System.out.println(" 60=> "+m.signe_cor_60.toString());
			System.out.println("==");
			System.out.println(" 75=> "+m.signe_cor_75.size());
			System.out.println("==");
			/*Patient_maladies pt = diagnostique_ecoder(file);
			Methodes m= new Methodes();
			ArrayList<Patient> liste_patient=pt.patient;
		    writeToCsvFiles(
					liste_patient,
					";",
					"D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\"
							+ "version_final\\correlation_Signe\\"+ "Signes_patient_v2.csv");*/

		}







	}

package nettoyage;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

public class bd_consultation {
	public static ArrayList<Patient> listePatient = new ArrayList<Patient>();
	
	public static void extraction() {
		String login = "root";
		String password = "";
		String url = "jdbc:mysql://localhost/cardiopediatrie";
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		HashSet<String> maladie = new HashSet<String>();
		// String
		// sql="select id_consultation,id_patient,diagnostique from patient_v1_final";
		String sql ="select id_consultation,id_patient,datenaiss,sexe,libellecons,poids,taille,spo2,imc,temperature,tension,freqcard,examdemander, traitement,diagnostique from patient_v1";	

		try {
			/** chargement du Driver ODBC-JDBC */
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			/** Connexion à la base */
			con = DriverManager.getConnection(url, login, password);
			/** création d’une zone d’exécution de requêtes SQL */
			st = con.createStatement();

			/** exécution de requêtes et récupération des données demandées */
			rs = st.executeQuery(sql);
			
			/** parcours du resultat de la requete **/
			//System.out.println("ici");
			// ("id_consultation  ==> id_patient ==>    diagnostique ");
			while (rs.next()) {
				//System.err.println("je suis");
				
				//,tension
				Patient p = new Patient();
				p.id_consultation = rs.getInt("id_consultation");
				p.id_patient = rs.getInt("id_patient");
				//p.spo2 = rs.getInt("spo2");
				//p.freqcard = rs.getInt("freqcard");
				//p.pression_systolique = rs.getInt("pression_systolique");
				//p.pression_diastolique = rs.getInt("pression_diastolique");
				p.sexe = rs.getString("sexe");
				//p.age = rs.getDouble("age");
				//p.taille = rs.getDouble("taille"); 
			
				//p.imc = rs.getDouble("imc");
				System.err.println("je suis");
			    //p.temperature = rs.getDouble("temperature");
			    
			    //p.poids = rs.getDouble("poids");
				
			   /* p.traitement=rs.getString("traitement");
			    p.examdemander=rs.getString("examdemander");
			   
			    p.datenaiss=rs.getString("datenaiss");
				p.libellecons = rs.getString("libellecons");*/
				p.diagnostique = rs.getString("diagnostique");
				listePatient.add(p);
				// System.out.println
				// (id_consultation+" ==> "+id_patient+" ==>"+diagnostique+" -||");
				// maladie.addAll(clean_v2(diagnostique));
			}
			// affiche toutes les maladies
			/**
			 * Iterator< String> i = maladie.iterator(); while (i.hasNext()) {
			 * System.err.println(i.next()); }
			 **/

		} catch (Exception e) {
			System.out.println("Erreur dans le chargement du driver");
			System.exit(0);
		}
		/** fermeture de tout lien avec la BD */
		finally {
			try {
				rs.close();
				st.close();
				con.close();
			} catch (Exception e) {
			}
		}

		System.out.println(" -- All tasks done.");

	}

	/***
	 * ecrire dans un fichier excel
	 * 
	 * @param thingsToWrite
	 * @param separator
	 * @param fileName
	 *            int id_consultation,id_patient,sexe,age,poids,taille,imc,
	 *            temperature,spo2,freqcard,pression_systolique,
	 *            pression_diastolique, diagnostique;
	 */
	public static void writeToCsvFile(ArrayList<Patient> thingsToWrite,
			String separator, String fileName) {
		//datenaiss, ,tension, ,examdemander, traitement from patient_v1";	

		try (FileWriter writer = new FileWriter(fileName)) {
			writer.append(String.valueOf("id_consultation"));
			writer.append(separator);
			writer.append(String.valueOf("id_patient"));
			writer.append(separator);
			writer.append(String.valueOf("sexe"));
			writer.append(separator);
		    /*writer.append(String.valueOf("age"));
		    writer.append(separator);*/
			/*writer.append(String.valueOf("poids"));
			writer.append(separator);
		    writer.append(String.valueOf("taille"));
			writer.append(separator); 
			writer.append(String.valueOf("imc"));
		    writer.append(separator);*/
		    /*writer.append(String.valueOf("temperature"));
		    writer.append(separator);
			writer.append(String.valueOf("spo2"));
			writer.append(separator);
			writer.append(String.valueOf("freqcard"));
			writer.append(separator);
			/*writer.append(String.valueOf("pression_systolique"));
			writer.append(separator);
			writer.append(String.valueOf("pression_diastolique"));
			writer.append(separator);
			writer.append(String.valueOf("libellecons"));
			writer.append(separator);*/
			writer.append(String.valueOf("diagnostique"));
			writer.append(System.lineSeparator());
			for (Patient p : thingsToWrite) {
				writer.append(String.valueOf(p.id_consultation));
				writer.append(separator);
				writer.append(String.valueOf(p.id_patient));
				writer.append(separator);
				writer.append(String.valueOf(p.sexe));
				writer.append(separator);
			   // writer.append(String.valueOf(p.age));
				//writer.append(separator);
				/*writer.append(String.valueOf(p.poids));
				writer.append(separator);
			    writer.append(String.valueOf(p.taille));
			    writer.append(separator);
			    writer.append(String.valueOf(p.imc));
			    writer.append(separator);
			    /*writer.append(String.valueOf(p.temperature));
				writer.append(separator);
				writer.append(String.valueOf(p.spo2));
				writer.append(separator);
				writer.append(String.valueOf(p.freqcard));
				writer.append(separator);
				/*writer.append(String.valueOf(p.pression_systolique));
				writer.append(separator);
				writer.append(String.valueOf(p.pression_diastolique));
				writer.append(separator);
				
				writer.append(String.valueOf(p.libellecons));
				writer.append(separator);*/
				writer.append(String.valueOf(p.diagnostique));
				writer.append(System.lineSeparator());
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		extraction();
		System.out.println(listePatient.toString());
		writeToCsvFile(listePatient,
				";", "D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\bd_consultation.csv");
		System.out.print("fin");
	}

}

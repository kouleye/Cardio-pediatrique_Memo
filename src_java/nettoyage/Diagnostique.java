package nettoyage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import nettoyage.DataSet_build.Patient_maladies;

public class Diagnostique {

	public static ArrayList<Patient> listePatient = new ArrayList<Patient>();

	/**
	 * renvoie pour une diagnostique donnee les differentes anomalies et
	 * maladies qui la constitue
	 * 
	 * @return
	 */
	public static HashSet<String> clean(String diagnostique) {
		HashSet<String> result = new HashSet<String>();
		String[] tempon = diagnostique.split(":");
		String[] part_one, part_deux = null;
		if (tempon.length < 2) {
			part_deux = diagnostique.replace("avec", "+").replace(" et ", "+")
					.replace(",", "+").replace("/", "+").replace("+", ";")
					.split(";");

		} else {
			part_one = tempon[0].replace("avec", "+").replace(" et ", "+")
					.replace(",", "+").replace("/", "+").replace("+", ";")
					.split(";");
			part_deux = tempon[1].replace("avec", "+").replace(" et ", "+")
					.replace(",", "+").replace("/", "+").replace("+", ";")
					.split(";");
		}

		System.err.println("temp_0-->" + tempon[0]);
		// System.err.print(part_deux.length);
		for (int i = 0; i < part_deux.length; i++) {
			result.add(part_deux[i]);
		}

		return result;

	}

	/**
	 * renvoie pour une diagnostique donnee les differentes anomalies et
	 * maladies qui la constitue
	 * 
	 * @return
	 */
	public static HashSet<String> clean_v2(String diagnostique) {
		HashSet<String> result = new HashSet<String>();
		String[] maladie_ou_malformation = null;
		maladie_ou_malformation = diagnostique.replace("\r\n", "")
				.replace(":", ";").replace("avec", "+").replace(" et ", "+")
				.replace(",", "+").replace("/", "+").replace("+", ";")
				.split(";");
		for (int i = 0; i < maladie_ou_malformation.length; i++) {
			result.add(maladie_ou_malformation[i]);
		}

		return result;

	}

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
		String sql = "select id_consultation,id_patient,IF(LENGTH(poids)>3,CAST(ROUND((CAST(poids AS unsigned)/1000.0),0)AS unsigned), CAST(poids as unsigned) ) AS poids , CAST(spo2 AS unsigned) AS spo2,CAST(LEFT(tension, INSTR(tension, '/')-1) AS unsigned) as pression_systolique,CAST(SUBSTRING(tension, INSTR(tension, '/') +1) AS unsigned) as pression_diastolique,CAST(freqcard AS unsigned) AS freqcard,libellecons,diagnostique from patient_v1 WHERE diagnostique<>'' and poids<>'' and spo2<>''and freqcard<>'' and tension<>'' and NOT ISNULL(diagnostique) and NOT ISNULL(poids) and NOT ISNULL(spo2) and NOT ISNULL(freqcard) and NOT ISNULL(tension) and diagnostique<>'NULL' and poids<>'NULL' and spo2<>'NULL' and freqcard<>'NULL' and tension<>'NULL';";

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
			// System.out.println
			// ("id_consultation  ==> id_patient ==>    diagnostique ");
			while (rs.next()) {
				//System.err.println("je suis");
				Patient p = new Patient();
				p.id_consultation = rs.getInt("id_consultation");
				p.id_patient = rs.getInt("id_patient");
				p.spo2 = rs.getInt("spo2");
				p.freqcard = rs.getInt("freqcard");
				p.pression_systolique = rs.getInt("pression_systolique");
				p.pression_diastolique = rs.getInt("pression_diastolique");
				/*
				 * p.sexe = rs.getString("sexe"); p.age = rs.getDouble("age");
				  p.taille =
				 * rs.getDouble("taille"); p.imc = rs.getDouble("imc");
				 * p.temperature = rs.getDouble("temperature");
				 */
			    p.poids = rs.getDouble("poids");
				p.libellecons = rs.getString("libellecons");
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
		try (FileWriter writer = new FileWriter(fileName)) {
			writer.append(String.valueOf("id_consultation"));
			writer.append(separator);
			writer.append(String.valueOf("id_patient"));
			writer.append(separator);
			/*
			 * writer.append(String.valueOf("sexe")); writer.append(separator);
			 * writer.append(String.valueOf("age"));
			 */
			//writer.append(separator);
			writer.append(String.valueOf("poids"));
			writer.append(separator);
			/*
			 * writer.append(String.valueOf("taille"));
			 * writer.append(separator); writer.append(String.valueOf("imc"));
			 * writer.append(separator);
			 * writer.append(String.valueOf("temperature"));
			 */
			//writer.append(separator);
			writer.append(String.valueOf("spo2"));
			writer.append(separator);
			writer.append(String.valueOf("freqcard"));
			writer.append(separator);
			writer.append(String.valueOf("pression_systolique"));
			writer.append(separator);
			writer.append(String.valueOf("pression_diastolique"));
			writer.append(separator);
			writer.append(String.valueOf("libellecons"));
			writer.append(separator);
			writer.append(String.valueOf("diagnostique"));
			writer.append(System.lineSeparator());
			for (Patient p : thingsToWrite) {
				writer.append(String.valueOf(p.id_consultation));
				writer.append(separator);
				writer.append(String.valueOf(p.id_patient));
				writer.append(separator);
				/*
				 * writer.append(String.valueOf(p.sexe));
				 * writer.append(separator);
				 * writer.append(String.valueOf(p.age));
				 */
				//writer.append(separator);
				writer.append(String.valueOf(p.poids));
				writer.append(separator);
				/*
				 * writer.append(String.valueOf(p.taille));
				 * writer.append(separator);
				 * writer.append(String.valueOf(p.imc));
				 * writer.append(separator);
				 * writer.append(String.valueOf(p.temperature));
				 */
				//writer.append(separator);
				writer.append(String.valueOf(p.spo2));
				writer.append(separator);
				writer.append(String.valueOf(p.freqcard));
				writer.append(separator);
				writer.append(String.valueOf(p.pression_systolique));
				writer.append(separator);
				writer.append(String.valueOf(p.pression_diastolique));
				writer.append(separator);
				writer.append(String.valueOf(p.libellecons));
				writer.append(separator);
				writer.append(String.valueOf(p.diagnostique));
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
	 *             renvoir les listes des patients et l'ensemble des differentes
	 *             maladies
	 */
	public static Patient_maladies diagnostique_eclate(String file)
			throws IOException {
		Patient_maladies retour = null;
		HashSet<String> result = new HashSet<String>();
		ArrayList<Patient> p = new ArrayList<Patient>();
		Patient patient;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

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
			patient.diagnostique = observation[13];

			for (String e : observation[observation.length - 1].split("\\+")) {
				result.add(e.trim());
			}

			p.add(patient);
		}

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

		retour.patient = p;
		retour.hash = result;

		return retour;

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println(" -- begining.");
		extraction();
	/*	writeToCsvFile(
				listePatient,
				";",
				"D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\donnees_patient_v2.csv");

		/*
		 * String file=
		 * "D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\donnees_patient&,,,,.csv"
		 * ; Patient_maladies res= diagnostique_eclate(file); ArrayList<Patient>
		 * resultat = res.patient; writeToCsvFile(resultat, ",",
		 * "D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\patient_dataSet_v1.csv"
		 * ); System.out.println("ok");
		 */
	}

}

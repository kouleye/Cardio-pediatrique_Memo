package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import nettoyage.CardioPediatrie;
import nettoyage.Correlation_des_signes_patient;
import nettoyage.DataSet_build;
import nettoyage.DataSet_build1;
import nettoyage.DataSet_build10;
import nettoyage.DataSet_build11;
import nettoyage.DataSet_build2;
import nettoyage.DataSet_build3;
import nettoyage.DataSet_build4;
import nettoyage.DataSet_build5;
import nettoyage.DataSet_build6;
import nettoyage.DataSet_build7;
import nettoyage.DataSet_build8;
import nettoyage.DataSet_build9;
import nettoyage.One_class_classification;
import nettoyage.Patient;
import neuron_classic.NeuralNetwork;

public class Models {

	public static int freq_app=2;
	public static double split_=20;
	public static String file_one_class="D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\one_class\\newdata.csv";
	public static String file="D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\newdata.csv";
	public static String file_60="D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\version_final\\signe_corel_60_pour.csv";
	public static String file_75="D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\version_final\\signe_corel_75_pour.csv";
	public static HashSet<String> signe_cor_75 ;
	public static HashSet<String> signe_cor_60;
	public Models() throws IOException {
		// TODO Auto-generated constructor stub
		signe_cor_75 =Correlation_des_signes_patient.recuperation_signe(Models.file_75);
		signe_cor_60=Correlation_des_signes_patient.recuperation_signe(Models.file_60);
	}

	
	
	
	static class Donnees {
		File train;
		File test;
		/**
		 * le model de reseau de neurone prend en entree des valeurs contenu
		 * dans un tableau ainsi double[][] va contenir les valeurs ou
		 * observations des variables explicatives et int[][] va contenir les
		 * valeurs des variables cibles pour chaque observation Ainsi a chaque
		 * rang du tableau double[][] correspondra une cible dans le tableau
		 * int[][]
		 */
		Map<double[][], double[][]> train_;
		Map<double[][], double[][]> test_;

		public String toString() {
			return "donnees train => " + train.getName().toString()
					+ ": donnees test =>" + test.getName().toString();

		}

	}

	/***
	 * renvoi le nombre de ligne d'un fichier exclut la premiere ligne
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static int nbre_ligne(String file) throws IOException {
		BufferedReader br = null;
		int[] t = new int[2];
		String line = "";
		String cvsSplitBy = ";";
		br = new BufferedReader(new FileReader(file));
		br.readLine();
		int nbre_line = 0;
		while ((line = br.readLine()) != null) {
			nbre_line++;
		}
		br.close();
		return nbre_line;
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 *             renvoie un tableau a deux entiers donc le premier elt est le
	 *             nombre de colone des variables explicatives le deuxieme elt
	 *             est le nbre de colone du fichier excel
	 * 
	 */
	public static int[] nbre_var(String file) throws IOException {
		BufferedReader br = null;
		int[] t = new int[2];
		String line = "";
		String cvsSplitBy = ";";
		br = new BufferedReader(new FileReader(file));
		line = br.readLine();
		br.close();
		String[] tab = line.split(cvsSplitBy);

		for (int i = 0; i < tab.length; i++) {
			if (tab[i].startsWith("D_")) {
				t[0] = i;
				t[1] = tab.length;
				break;
			}
		}

		return t;

	}

	/**
	 * renvoi une liste de data_set d'un repertoire pour chaque data_set a ses
	 * donnees d'entrainement et ces donnees test
	 * 
	 * @param repertoire
	 * @return
	 */
	public static List<Donnees> data_set(String repertoire) {
		File rep = new File(repertoire);
		List list_DataSet = new ArrayList<Donnees>();

		List<File> chemin_test = new ArrayList<File>();
		List<File> chemin_train = new ArrayList<File>();
		File[] files = rep.listFiles();
		for (File f : files) {
			if (f.getName().endsWith("csv")) {
				if (f.getName().startsWith("test_")) {
					chemin_test.add(f);
				} else if (f.getName().startsWith("train")) {
					chemin_train.add(f);
				}

			}
		}
		for (File f : chemin_test) {
			String[] t = f.getName()
					.substring(f.getName().lastIndexOf("test_")).split("test_");
			for (File fi : chemin_train) {
				if (fi.getName().endsWith(t[t.length - 1])) {
					Donnees d = new Donnees();
					d.test = f;
					d.train = fi;
					list_DataSet.add(d);
				}
			}

		}

		return list_DataSet;

	}

	/**
	 * charge un fichier excel sous forme tableau [][]
	 * 
	 * @param file
	 * @throws IOException 
	 */
	public static Map<double[][], double[][]> tempon_charge(String file) throws IOException {
		Map<double[][], double[][]> result = new HashMap<double[][], double[][]>();
		int nbre_ligne=nbre_ligne( file);
		int[] t=nbre_var(file);
		double [][]var_exp= new double[nbre_ligne][t[0]];
		double [][] var_cible= new double[nbre_ligne][t[1]-t[0]];

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		br = new BufferedReader(new FileReader(file));
		br.readLine();
		int ligne=0;
		while ((line = br.readLine()) != null) {
			String[] observation = line.split(cvsSplitBy);
			int conpteur_cible=0;
			for(int i=0;i<observation.length;i++){
				if(i<t[0]){
					var_exp[ligne][i]=Double.parseDouble(observation[i]);
				}else{
					var_cible[ligne][conpteur_cible]=Integer.parseInt(observation[i]);
					conpteur_cible++;
				}
			}
			ligne++;
		}
		result.put(var_exp, var_cible);
		return result;

	}

	/**
	 * chargement des tableaux des data_set de la liste avec des valeurs tableau
	 * qui permettrons l'entrainement et test
	 * 
	 * @param list
	 * @return
	 * @throws IOException 
	 */
	public static List<Donnees> tableau_charge(List<Donnees> list) throws IOException {
		for (int i = 0; i < list.size(); i++) {
			File training = list.get(i).train;
			File test = list.get(i).test;
			list.get(i).train_=tempon_charge(training.toString());
			list.get(i).test_=tempon_charge(test.toString());
		}
		return list;

	}
	/**
	 * ici nous entrainnons et testons
	 * @param list
	 * @throws IOException 
	 */
	public static void test_(List<Donnees> list) throws IOException{
		for(Donnees elt: list){
			System.err.println("Data set =>"+elt.train.getName());
			int[] tab = nbre_var(elt.train.toString());
			NeuralNetwork nn = new NeuralNetwork(tab[0], 8,tab[1]-tab[0]);	
			Iterator it_train = elt.train_.entrySet().iterator();
			double[][] var_exp= null;
			double[][] cible = null;
			while (it_train.hasNext()) {
				Map.Entry<double[][], double[][]> entry_train = (Map.Entry) it_train.next();
				var_exp = entry_train.getKey();
				cible = entry_train.getValue(); ;
			}
			nn.fit(var_exp,cible, 200000);


			double[][] var_exp_test = null;
			double[][] cible_test = null;
			Iterator it_test = elt.test_.entrySet().iterator();
			while (it_test.hasNext()) {
				Map.Entry<double[][], double[][]> entry_test = (Map.Entry) it_test.next();
				var_exp_test = entry_test.getKey();
				cible_test = entry_test.getValue(); ;
			}

			List<Double> output = null;	
			for(double d[] : var_exp_test){
				output = nn.predict(d);
				System.out.println(var_exp_test.toString());
				System.out.println(output.toString());
				System.out.println(cible_test.toString());
				System.out.println("----------------\n");
			}

		}

	}
	/*public static double precision(output, target, seuil = 0.5)
	def precision(output, target, seuil = 0.5):
		tp = 0.0
		fp = 0.0
		for i in range(len(output)):
			if(output[i] > seuil): # output[i] = 1
				if(target[i] == 1):
					tp += 1.0
				else:
					fp += 1.0

		if(tp > 0 or fp > 0):
			return tp / (tp + fp)
		else:
			return 0.0

	def recall(output, target, seuil = 0.5):
		tp = 0.0
		fn = 0.0
		for i in range(len(output)):
			if(output[i] > seuil and target[i] == 1):
				tp += 1.0
		for i in range(len(target)):
			if(target[i] == 1 and not(output[i] > seuil)):
				fn += 1

		if(tp > 0 or fn > 0):
			return tp / (tp + fn)
		else:
			return 0.0

	def rmse(vec):
		# print(str(vec))
		# print(str(vec.shape))
		# exit(0)
		sum = 0
		for elt in vec:
			sum += (elt * elt)
		return sqrt(sum / len(vec))*/


	public static void main(String[] args) throws IOException {
		/**CardioPediatrie.main(args);
		DataSet_build.main(args);
		DataSet_build1.main(args);
		DataSet_build2.main(args);
		DataSet_build3.main(args);
		DataSet_build4.main(args);
		DataSet_build5.main(args);
		DataSet_build6.main(args);
		DataSet_build7.main(args);
		DataSet_build8.main(args);
		DataSet_build9.main(args);
		DataSet_build10.main(args);
		DataSet_build11.main(args);**/
		One_class_classification.main(args);
		
		/*String repertoire = "D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\execute\\ed\\";
		test_(tableau_charge(data_set(repertoire)));*/

	}

}

package nettoyage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;


public class Patient {
	//poids,taille,spo2,imc,
	//temperature,tension,freqcard,diagnostique
	
	int id_consultation,spo2,id_patient,freqcard,pression_systolique,sexe_M,sexe_F,pression_diastolique;
	String sexe,datenaiss,datecons,examdemander, traitement;
	double age,poids,taille,imc,temperature;
	double imc_encoder,temperature_encoder,spo2_encoder,pre_sys_enc,pre_dias_enc;
	String diagnostique,libellecons,nom,prenom;
	//la clef represente la maladie et la valeur soit a 1 ou 0 s'il a la maladie
	Map<String, Integer> hash_mal=new TreeMap<String,Integer>();
	//la clef represente l'intervale de l'age et la valeur soit a 1 ou 0 s'il appartient a l'intervale
	Map<String, Integer> hash_age=new TreeMap<String,Integer>();
	Map<String, Double> hash_age_=new TreeMap<String,Double>();
	//la clef represente l'intervale du poids et la valeur soit a 1 ou 0 s'il appartient a l'intervale
	Map<String, Integer> hash_poids=new TreeMap<String,Integer>();
	Map<String, Double> hash_poids_=new TreeMap<String,Double>();
	//la clef represente l'intervale de taille et la valeur soit a 1 ou 0 s'il appartient a l'intervale
	Map<String, Integer> hash_taille=new TreeMap<String,Integer>();
	Map<String, Double> hash_taille_=new TreeMap<String,Double>();
	//la clef represente l'intervale de temperature et la valeur soit a 1 ou 0 s'il appartient a l'intervale
	Map<String, Integer> hash_temperature=new TreeMap<String,Integer>();
	Map<String, Double> hash_temperature_=new TreeMap<String,Double>();
	//la clef represente l'intervale de frequence Cardiaque et la valeur soit a 1 ou 0 s'il appartient a l'intervale
	Map<String, Integer> hash_freqcard=new TreeMap<String,Integer>();
	Map<String,Double> hash_freqcard_=new TreeMap<String,Double>();
	//Correspond a d'autre signe de la maladie
	Map<String, Double> hash_signe_=new TreeMap<String,Double>();
	//juste pour la creation d'un dictionnaire compact de signe nous avons creer deux variables 
	//un signe moi et ses voisins, les voisins sont determines en utilisant la distance de levenshtein
	ArrayList<String> voisin=new ArrayList<String>();
	String moi=new String();
	public String toString(){
		return id_consultation+" "+" "+id_patient+" "+nom+" "+prenom+" "+sexe+" "+age;
		
	}

}

package nettoyage;
import java.io.IOException;

import java.util.HashSet;

import nettoyage.Methodes;
import nettoyage.Libelle_clean;
import model.Models;
public class DataSet_build_specify {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
        Methodes m=new Methodes();
		String fichier=Models.file;
		HashSet<String> liste = m.liste_signe (Libelle_clean.diagnostique_ecoder(fichier).patient);
		Libelle_clean.libel_affiche(Libelle_clean.diagnostique_ecoder(fichier).patient);
		Methodes n=new Methodes();
		

	}

}

package nettoyage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.*;

public class CardioPediatrie {
	/**
	 * 
	 * @param _text
	 * @return
	 * prend un text contenu entre double cote en entree(" ")
	 * renvoie un text ne contenant pas de sauts(\r\n) de ligne ni de retour a la ligne
	 * ni de point-virgule(;),ni de "\"
	 */
	protected static String clean(String _text)
	{
		int from = 0, to;
		String text = _text, subtext, newsubtext;

		int i =1 ;
		do
		{
			i++;
			from = text.indexOf("\"");
			if (from >= 0)
			{
				to = text.indexOf("\"", from + i);
				subtext = text.substring(from, (to - from + 1));
				newsubtext = subtext.replace("\n", "").replace("\r\n", "").replace(";", ",").replace("\"", "");
				System.out.println(" -- from " +from+" to "+to);
			    text.replace(subtext, newsubtext);
			}
		}
		while (from >= 0);
		return text;
	}
	/**
	 * convertion d'une chaine contenant la date en type date
	 * @param dateDTPicker
	 * @return
	 */
	protected static String dateToMysql(String dateDTPicker)
	{
		if (dateDTPicker.isEmpty() || dateDTPicker==null)
			return "NULL";

		//Console.WriteLine(" --> " + dateDTPicker);
		String[] date = dateDTPicker.split("/");
		if (date.length < 3)
			return "\"" + dateDTPicker + "\"";
		else
			return "\"" + date[2] + "-" + date[1] + "-" + date[0] + "\"";
	}
	/**
	 * 
	 * @param path
	 * extraction des donnees du fichier csv et chargement dans la BD des variables supposees pertinent
	 * pour notre cas d'etude
	 */
	public static void extractData(String file) throws SQLException{
		String login = "root";
		String password = "";
		String url = "jdbc:mysql://localhost/cardiopediatrie";
		Connection con=null;
		Statement st=null;
		BufferedReader br = null;
		ResultSet rs = null ;
		String stringSeparators=";";
		String stringligne="\n";

		try {
			String line,tmp="", sql;
			String[] p;
			//String text = File.ReadAllText(path);
			//gestion des accents nous utilisons InputStreamReader
			 br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			//br=new BufferedReader(new FileReader(file));
		
			try {
				/** chargement du Driver ODBC-JDBC*/
				Class.forName ("com.mysql.jdbc.Driver").newInstance ( ) ;
				/**Connexion à la base*/
				con = DriverManager.getConnection (url,login,password);
				/**création d’une zone d’exécution de requêtes SQL*/
				st = con.createStatement ( );
				 br.readLine();
			        while ((line = br.readLine()) != null) {
			           tmp=tmp+"\n"+line;
			        }
			  System.out.println(" - ==>."+tmp);   
			 String text = CardioPediatrie.clean(tmp);
			 System.out.println(" - VVV1 ==>."+text);
			    //de 1 a 32 soit 32 colonne
			    /**String[] lines = text.split(stringligne);
			    for(String l:lines){
			    	System.out.println(l);
			    	System.err.println("FIN");
			    }**/
			    
			    /**exécution de requêtes et récupération des données demandées*/
				st.executeUpdate ("Delete from patient_v1");

	          
				System.out.println(" - tasks done.");
			} 
			catch (Exception e) {
				System.out.println ("Erreur dans le chargement du driver"); System.exit ( 0 );
			}
			/** fermeture de tout lien avec la BD*/
			finally { try{  rs.close ( );
			st.close ( );
			con.close ( );} 
			catch (Exception e) { }
			}

			System.out.println(" -- All tasks done.");

		} catch (Exception e) {
			// TODO: handle exception
		}finally{

		}

	}	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	/*	try {
		  extractData("D:\\school\\Master 2 BI\\recherche\\Mon Memoire de M2\\pratique\\bdconsultation.csv");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}

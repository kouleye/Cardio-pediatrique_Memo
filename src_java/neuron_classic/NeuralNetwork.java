package neuron_classic;

import java.util.List;

public class NeuralNetwork {
    /**
     * weights_ih = matrice de poids entre la couche d'entree et la couche cache
     * weights_ho = matrice de poids entre la couche cachee et la couche de sortie
     * bias_h     = vecteur de biais de la couche de milieur
     * bias_o     = vecteur de biais de la couche de sortie
     */
	Matrix weights_ih, weights_ho, bias_h, bias_o;	
	double l_rate = 0.01;
    /**
     * initialisation des matrice de poids en forme deja transposee 
     * si W est la matrice de poids alors ce constructeur 
     * la transforme en la transposant
     * @param i = nombre de neurone d'entree
     * @param h = nombre de neurone de la couche cachee
     * @param o = nombre de neurone de la couche de sortie
     */
	public NeuralNetwork(int i, int h, int o) {
		weights_ih = new Matrix(h, i);
		weights_ho = new Matrix(o, h);

		bias_h = new Matrix(h, 1);
		bias_o = new Matrix(o, 1);
	}
     /**
      * c'est elle qui fait la prediction 
      * les donnees passent de la couche d'entrer
      *  vers la couche de sortie
      * @param X
      * @return
      */
	public List<Double> predict(double[] X) {
		Matrix input = Matrix.fromArray(X);
		Matrix hidden = Matrix.dot(weights_ih, input);
		hidden.add(bias_h);
		hidden.sigmoid();

		Matrix output = Matrix.dot(weights_ho,hidden);
		output.add(bias_o);
		output.sigmoid();

		return output.toArray();
	}
    /**
     * Phase d'apprentissage
     * ici nous entrainons le modele sur plusieurs epoques
     * pour qu'il puisse nous produire les resultats escontes
     * a une certaine probabilite
     * @param X= valeur d'entree
     * @param Y= valeur attendu
     * @param epochs
     */
	public void fit(double[][]X, double[][]Y, int epochs)
	{
		int step = epochs / 100;
		for(int s=0; s < 100; s++)
		{
			for(int i=0; i < step; i++)
			{	
				int sampleN = (int)(Math.random() * X.length );
				this.train(X[sampleN], Y[sampleN]);
			}
			System.out.println(" -- " + (s+1) + "% ...");
		}

		step *= 100;
		for(int i=step; i < epochs; i++)
		{	
			int sampleN =  (int)(Math.random() * X.length );
			this.train(X[sampleN], Y[sampleN]);
		}
		/*int sampleN =  (int)(Math.random() * X.length );
		this.train(X[sampleN], Y[sampleN]);*/
		System.out.println(" -- All tasks done");
	}
    /***
     * fonction qui permet d'entrainer mon modele
     * @param X
     * @param Y
     */
	public void train(double [] X, double [] Y)
	{
		Matrix input = Matrix.fromArray(X);
		Matrix hidden = Matrix.dot(weights_ih, input);
		hidden.add(bias_h);
		hidden.sigmoid();

		Matrix output = Matrix.dot(weights_ho,hidden);
		output.add(bias_o);
		output.sigmoid();

		Matrix target = Matrix.fromArray(Y);
        //on obtient la matrice d'erreur
		Matrix error = Matrix.subtract(target, output);
		
		Matrix gradient = output.dsigmoid();
		//on multiplie par la matrice d'erreur de prediction pour reapprendre?
		gradient.multiply(error);
		/**
		 * multiplie par le taux d'erreur
		 */
		gradient.multiply(l_rate);

		Matrix hidden_T = Matrix.transpose(hidden);
		//correction des poids de la matrix entre la couche de sortie et la couche cachee
		Matrix who_delta =  Matrix.dot(gradient, hidden_T);
        
		/**
		 * corrige les poids entre la sortie et la couche cache	 
		 * car chaque poids ne participe pas de la meme facon
		 */
		weights_ho.add(who_delta);
		bias_o.add(gradient);

		Matrix who_T = Matrix.transpose(weights_ho);
		Matrix hidden_errors = Matrix.dot(who_T, error);

		Matrix h_gradient = hidden.dsigmoid();
		h_gradient.multiply(hidden_errors);
		h_gradient.multiply(l_rate);

		Matrix i_T = Matrix.transpose(input);
		Matrix wih_delta = Matrix.dot(h_gradient, i_T);

		weights_ih.add(wih_delta);
		bias_h.add(h_gradient);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

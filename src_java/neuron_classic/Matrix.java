package neuron_classic;

import java.util.ArrayList;
import java.util.List;

public class Matrix {
	double [][]data;
	int rows,cols;

	public Matrix(int rows,int cols) {
	//System.out.print("-> Matrix("+rows+","+cols+")");
	//permet de creer et d'initialiser les matrices de poids synaptiques
	data=new double[rows][cols];
	this.rows=rows;
	this.cols=cols;
	for(int i=0;i<rows;i++)
	{
		for(int j=0;j<cols;j++)
		{
			data[i][j]=Math.random()*2-1;
		}
		
	}
	}
	/**
	 * Afficher la matrice avec ses differentes 
	 * valeurs representant des poids dans notre cas
	 */
	public void print()
	{
		System.out.print("[");
		
		for(int i=0;i<rows;i++)
		{
			if(i != 0)
				System.out.print("\n ");
			System.out.print("[" + this.data[i][0]);
			
			for(int j=1;j<cols;j++)
			{
				System.out.print(" " + this.data[i][j]);
			}
			System.out.print("]");
		}
		
		System.out.println("]");
	}
	
	/**
	 * ajouter a chaque valeur de la matrix 
	 * la valeur  scaler
	 * @param scaler
	 */
	public void add(int scaler)
	{
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<cols;j++)
			{
				this.data[i][j]+=scaler;
			}
			
		}
	}
	/**
	 * addition de deux matrixs
	 * @param m
	 */
	public void add(Matrix m)
	{
		if(cols!=m.cols || rows!=m.rows) {
			//System.out.println("Shape Mismatch : [" + cols + ", " + rows + "] -- [" + m.cols + ", " + m.rows + "]");
			return;
		}
		//System.out.println("Shape match : [" + cols + ", " + rows + "] -- [" + m.cols + ", " + m.rows + "]");
		
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<cols;j++)
			{
				this.data[i][j]+=m.data[i][j];
			}
		}
	}
	/**
	 * transposition du vecteur ligne 
	 * en vecteur colonne
	 * @param x
	 * @return
	 */
	public static Matrix fromArray(double[] x)
	{
		Matrix temp = new Matrix(x.length, 1);
		for(int i =0;i<x.length;i++)
			temp.data[i][0]=x[i];
		return temp;
		
	}
	/**
	 * recuperation des elements de la matrice
	 * et stockage dans une liste
	 * @return
	 */
	public List<Double> toArray() {
		List<Double> temp= new ArrayList<Double>()  ;
		
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<cols;j++)
			{
				temp.add(data[i][j]);
			}
		}
		return temp;
	}
	/**
	 * soustraction entre deux matrices
	 * membre par menbre
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix subtract(Matrix a, Matrix b) {
		Matrix temp=new Matrix(a.rows,a.cols);
		for(int i=0;i<a.rows;i++)
		{
			for(int j=0;j<a.cols;j++)
			{
				temp.data[i][j]=a.data[i][j]-b.data[i][j];
			}
		}
		return temp;
	}

	public static Matrix transpose(Matrix a) {
		Matrix temp=new Matrix(a.cols, a.rows);
		for(int i=0;i<a.rows;i++)
		{
			for(int j=0;j<a.cols;j++)
			{
				temp.data[j][i] = a.data[i][j];
			}
		}
		return temp;
	}
	/**
	 * produit matriciell
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix dot(Matrix a, Matrix b) {
		Matrix temp = new Matrix(a.rows, b.cols);
		for(int i=0;i<temp.rows;i++)
		{
			for(int j=0;j<temp.cols;j++)
			{
				double sum=0;
				for(int k=0;k<a.cols;k++)
				{
					sum += a.data[i][k]*b.data[k][j];
				}
				temp.data[i][j]=sum;
			}
		}
		return temp;
	}
	/**
	 * produit de la matrice par une autre
	 * element par element
	 * @param a
	 */
	public void multiply(Matrix a) {
		for(int i=0;i<a.rows;i++)
		{
			for(int j=0;j<a.cols;j++)
			{
				this.data[i][j]*=a.data[i][j];
			}
		}
		
	}
	/**
	 * multiplication d'une matrce par un scalaire
	 * @param a
	 */
	public void multiply(double a) {
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<cols;j++)
			{
				this.data[i][j]*=a;
			}
		}
		
	}
	/**
	 * application de la fonction sigmoide 
	 * aux valeurs d'une matrix
	 */
	public void sigmoid() {
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<cols;j++)
				this.data[i][j] = 1/(1+Math.exp(-this.data[i][j])); 
		}
		
	}
	/**
	 * application de la derive de
	 * sigmoide au element d'une matrix
	 * @return
	 */
	public Matrix dsigmoid() {
		Matrix temp = new Matrix(rows, cols);
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<cols;j++)
				temp.data[i][j] = this.data[i][j] * (1-this.data[i][j]);
		}
		return temp;
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	

	}

}

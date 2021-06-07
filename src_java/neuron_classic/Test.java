package neuron_classic;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Test {
	public static void test1() {
		double [][] X = {
				{1,1,1, 1,0,1, 1,0,1, 1,0,1, 1,1,1},
				{1,1,0, 0,1,0, 0,1,0, 0,1,0, 1,1,1},
				{1,1,1, 0,0,1, 1,1,1, 1,0,0, 1,1,1},
				{1,1,1, 0,0,1, 0,1,1, 0,0,1, 1,1,1},				
				{0,1,1, 1,0,1, 1,1,1, 0,0,1, 0,0,1},
				{1,1,1, 1,0,0, 1,1,1, 0,0,1, 1,1,1},
				{1,1,1, 1,0,0, 1,1,1, 1,0,1, 1,1,1},
				{1,1,0, 0,1,0, 1,1,1, 0,1,0, 0,1,0},
				{1,1,1, 1,0,1, 1,1,1, 1,0,1, 1,1,1},
				{1,1,1, 1,0,1, 1,1,1, 0,0,1, 1,1,1}
		};
		double [][] Y = {
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
		};

		NeuralNetwork nn = new NeuralNetwork(15, 8, 10);		
		nn.fit(X, Y, 200000);

		double [][] input = {
				{1,1,1, 1,0,1, 1,0,1, 1,0,1, 1,1,1}, // 0
				{1,1,0, 0,1,0, 0,1,0, 0,1,0, 1,1,1}, // 1
				{1,1,1, 0,0,1, 1,0,1, 1,0,0, 1,1,1}, // ? 
				{1,1,1, 0,0,1, 0,1,1, 0,0,1, 1,1,1}, // 3
				{1,1,1, 1,0,1, 1,0,1, 0,0,1, 1,1,1}  // 0 ou 9
		};

		List<Double> output = null;	
		for(double d[] : input){
			output = nn.predict(d);
			System.out.println(input.toString());
			System.out.println(output.toString());
			System.out.println("----------------\n");
		}		


	}

	public static void test2() {
		double [][] X = {
				{0,0},
				{0,1},
				{1,0},
				{1,1}
		};
		double [][] Y = {
				{1, 0},{0, 1},{0, 1},{1, 0}
		};

		NeuralNetwork nn = new NeuralNetwork(2,3,2);		
		nn.fit(X, Y, 50000);

		double [][] input = {{0,0},{0,1},{1,0},{1,1}};

		List<Double> output = null;	
		for(double d[] : input){
			output = nn.predict(d);
			System.out.println(output.toString());
		}		
	}

	
	public static void main(String[] args) {
		Test.test1();
		//System.out.println("---------------");
		//Test.test2();
	}
}

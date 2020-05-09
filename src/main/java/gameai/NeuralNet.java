package gameai;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class NeuralNet {
	public static double deviationWeight = 0.0;
	
	private static final int RECURRENT_SIZE = 10;
	private static final int INPUT_SIZE = 22 + RECURRENT_SIZE;
	private static final int HIDDEN1_SIZE = 64;
	private static final int HIDDEN2_SIZE = 16;
	private static final int OUTPUT_SIZE = 3 + RECURRENT_SIZE;

	public Matrix weights1;
	public Matrix biases1;
	public Matrix weights2;
	public Matrix biases2;
	public Matrix weights3;
	public Matrix biases3;
	private Matrix recurrent;

	public ArrayList<Integer> scores = new ArrayList<Integer>();
	public int sum = 0;
	public double mean;
	public double deviation;
	public double oScore;

	public NeuralNet() {
		weights1 = new Matrix(INPUT_SIZE, HIDDEN1_SIZE, true);
		biases1 = new Matrix(1, HIDDEN1_SIZE, true);
		weights2 = new Matrix(HIDDEN1_SIZE, HIDDEN2_SIZE, true);
		biases2 = new Matrix(1, HIDDEN2_SIZE, true);
		weights3 = new Matrix(HIDDEN2_SIZE, OUTPUT_SIZE, true);
		biases3 = new Matrix(1, OUTPUT_SIZE, true);

		recurrent = new Matrix(1, RECURRENT_SIZE, false);
	}

	public NeuralNet(Matrix weights1, Matrix biases1, Matrix weights2, Matrix biases2, Matrix weights3,
			Matrix biases3) {
		super();
		this.weights1 = weights1.clone();
		this.biases1 = biases1.clone();
		this.weights2 = weights2.clone();
		this.biases2 = biases2.clone();
		this.weights3 = weights3.clone();
		this.biases3 = biases3.clone();

		recurrent = new Matrix(1, RECURRENT_SIZE, false);
	}

	public NeuralNet(String modelPath) {
		weights1 = new Matrix(INPUT_SIZE, HIDDEN1_SIZE, true);
		biases1 = new Matrix(1, HIDDEN1_SIZE, true);
		weights2 = new Matrix(HIDDEN1_SIZE, HIDDEN2_SIZE, true);
		biases2 = new Matrix(1, HIDDEN2_SIZE, true);
		weights3 = new Matrix(HIDDEN2_SIZE, OUTPUT_SIZE, true);
		biases3 = new Matrix(1, OUTPUT_SIZE, true);

		recurrent = new Matrix(1, RECURRENT_SIZE, false);

		try {
			DataInputStream dos = new DataInputStream(new FileInputStream(modelPath));
			weights1.readFromFile(dos);
			biases1.readFromFile(dos);
			weights2.readFromFile(dos);
			biases2.readFromFile(dos);
			weights3.readFromFile(dos);
			biases3.readFromFile(dos);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public KeyInput predict(Game game) {
		Matrix input = new Matrix(1, INPUT_SIZE);
		ArrayList<Enemy> enemies = game.getEnemies(3);
		assert enemies.size() <= 6;
		int offset = (6 - enemies.size()) / 2;
		for (int i = 0; i < enemies.size(); i++) {
			input.mat[0][(offset + i) * 3 + 0] = (enemies.get(i).x - game.player.x) / 50;
			input.mat[0][(offset + i) * 3 + 1] = (enemies.get(i).y - game.player.y) / 50;
			input.mat[0][(offset + i) * 3 + 2] = enemies.get(i).mx / 8;
		}
		input.mat[0][18] = game.player.x / game.w;
		input.mat[0][19] = game.player.y / game.h;
		input.mat[0][20] = game.player.mx / 8;
		input.mat[0][21] = game.player.isStomping ? 1 : 0;
		for (int i = 0; i < RECURRENT_SIZE; i++) {
			input.mat[0][22 + i] = recurrent.mat[0][i];
		}

		Matrix hidden1 = input.dot(weights1);
		hidden1.add(biases1);
		hidden1.relu();

		Matrix hidden2 = hidden1.dot(weights2);
		hidden2.add(biases2);
		hidden2.relu();

		Matrix output = hidden2.dot(weights3);
		output.add(biases3);
		output.sigmoid();

		for (int i = 0; i < RECURRENT_SIZE; i++) {
			recurrent.mat[0][i] = output.mat[0][3 + i];
		}

		KeyInput outputKeys = new KeyInput();
		if (output.mat[0][0] >= 0.5)
			outputKeys.d = true;
		if (output.mat[0][1] >= 0.5)
			outputKeys.l = true;
		if (output.mat[0][2] >= 0.5)
			outputKeys.r = true;

		return outputKeys;
	}

	public void randomize(double rChance, double rAmount, double rPAmount) {
		weights1.randomize(rChance, rAmount, rPAmount);
		biases1.randomize(rChance, rAmount, rPAmount);
		weights2.randomize(rChance, rAmount, rPAmount);
		biases2.randomize(rChance, rAmount, rPAmount);
		weights3.randomize(rChance, rAmount, rPAmount);
		biases3.randomize(rChance, rAmount, rPAmount);
	}

	public void crossOver(NeuralNet n2, double weightSelf) {
		weights1.crossOver(n2.weights1, weightSelf);
		biases1.crossOver(n2.biases1, weightSelf);
		weights2.crossOver(n2.weights2, weightSelf);
		biases2.crossOver(n2.biases2, weightSelf);
		weights3.crossOver(n2.weights3, weightSelf);
		biases3.crossOver(n2.biases3, weightSelf);
	}

	public void writeToFile(String filePath) {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath));
			weights1.writeToFile(dos);
			biases1.writeToFile(dos);
			weights2.writeToFile(dos);
			biases2.writeToFile(dos);
			weights3.writeToFile(dos);
			biases3.writeToFile(dos);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void calcStats() {
		for (int s : scores) {
			sum += s;
		}
		mean = (double) sum / scores.size();
		double totalDev = 0;
		for (double s : scores) {
			totalDev += Math.abs(mean - s);
		}
		deviation = totalDev / scores.size();
		oScore = mean - deviation * deviationWeight;
	}

	public void resetStats() {
		scores = new ArrayList<Integer>();
		sum = 0;
		mean = 0;
		deviation = 0;
	}

	public NeuralNet clone() {
		NeuralNet newNN = new NeuralNet(weights1, biases1, weights2, biases2, weights3, biases3);
		return newNN;
	}

}

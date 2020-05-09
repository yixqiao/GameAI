package gameai;

import static gameai.Vars.random;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Population {
	public static final int POPULATION_SIZE = 1000;
	public static int SIMS_PER_NN = 10;
	private static final int THREAD_COUNT = 8;
	public static final int SAVE_INTERVAL = 100;
	private static final int PAST_COUNT = 20;
	public int generations = 0;

	private Queue<Double> prevMeans;
	private double prevSum;

	private Queue<Integer> needSim;
	private S1G simThread;

	public NeuralNet nns[];

	private class SortNNs implements Comparator<NeuralNet> {
		public int compare(NeuralNet a, NeuralNet b) {
			return Double.compare(b.oScore, a.oScore); // Descending
		}
	}

	private class SimNN extends Thread {
		public void run() {
			try {
				while (true) {
					int curNN;
					synchronized (needSim) {
						if (needSim.isEmpty())
							return;
						assert !needSim.isEmpty();
						curNN = needSim.remove();
					}
					synchronized (nns[curNN]) {
						for (int j = 0; j < Population.SIMS_PER_NN; j++) {
							Game g = new Game(0, 0, 1600, 900);
							while (g.isPlaying) {
								g.update(nns[curNN].predict(g));
							}
							nns[curNN].scores.add(g.score);
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	private class S1G extends Thread {
		public void run() {
			try {
				long startTime = System.currentTimeMillis();

				updateParams();

				for (int i = 0; i < POPULATION_SIZE; i++) {
					nns[i].resetStats();
				}

				simulateAll();
				shuffle();

				double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;

				prevSum += nns[0].mean;

				prevMeans.add(nns[0].mean);
				if (prevMeans.size() > PAST_COUNT) {
					prevSum -= prevMeans.remove();
				}

				System.out.println(String.format("G: %d\tM: %d\tD: %.1f\tP: %d\tT: %.2f", generations,
						(int) nns[0].mean, nns[0].deviation, (int) (prevSum / prevMeans.size()), elapsedTime));

				if (generations % SAVE_INTERVAL == 0) {
					nns[0].writeToFile(
							String.format("models/G%04d-%d.mdl", generations, (int) (prevSum / prevMeans.size())));
				}

			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	public Population() {
		nns = new NeuralNet[POPULATION_SIZE];
		for (int i = 0; i < POPULATION_SIZE; i++) {
			nns[i] = new NeuralNet();
		}
		prevMeans = new LinkedList<Double>();
	}

	public static void clearSavedModels() {
		File[] children = new File("models").listFiles();
		if (children.length != 0) {
			Scanner sc = new Scanner(System.in);
			System.out.print("Delete " + children.length + " files? ");
			String s = sc.next();
			sc.close();
			if (!s.equals("y")) {
				return;
			}
		}
		for (int i = 0; i < children.length; i++) {
			children[i].delete();
		}
	}

	public void oneGeneration() {
		if (!finishedSim()) {
			System.err.println("Nope");
			return;
		}
		simThread = new S1G();
		simThread.start();
	}

	private void updateParams() {
		NeuralNet.deviationWeight = Math.min(0.5, (double) generations / 4000 * 0.5);

		if (generations == 1000 || generations == 2000 || generations == 4000)
			SIMS_PER_NN += 10;
	}

	public boolean finishedSim() {
		return simThread == null || !simThread.isAlive();
	}

	public void simulateAll() {
		needSim = new LinkedList<Integer>();
		for (int i = 0; i < POPULATION_SIZE; i++) {
			needSim.add(i);
		}

		SimNN sThreads[] = new SimNN[THREAD_COUNT];
		for (int i = 0; i < THREAD_COUNT; i++) {
			sThreads[i] = new SimNN();
			sThreads[i].start();
		}
		while (true) {
			synchronized (needSim) {
				if (needSim.isEmpty())
					break;
			}
		}

		for (int i = 0; i < THREAD_COUNT; i++) {
			try {
				sThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// System.out.println(scores[i]);
		generations++;
	}

	public void shuffle() {
		synchronized (nns) {
			for (NeuralNet nn : nns) {
				nn.calcStats();
			}
			Arrays.sort(nns, new SortNNs());

			for (int i = 0; i < POPULATION_SIZE / 5; i++) {
				nns[POPULATION_SIZE / 5 + i] = nns[i].clone();
				nns[POPULATION_SIZE / 5 + i].randomize(0.1, 0.2, 0.05);

				nns[POPULATION_SIZE * 2 / 5 + i] = nns[i].clone();
				nns[POPULATION_SIZE * 2 / 5 + i].randomize(0.5, 0.25, 0.1);

				nns[POPULATION_SIZE * 3 / 5 + i] = nns[i].clone();
				nns[POPULATION_SIZE * 3 / 5 + i].crossOver(nns[random.nextInt(POPULATION_SIZE / 5)], 0.8);
			}

			for (int i = POPULATION_SIZE * 4 / 5; i < POPULATION_SIZE; i++) {
				nns[i] = new NeuralNet();
			}
		}
	}

	public NeuralNet getBest() {
		double bestS = -1;
		int bestI = -1;
		for (int i = 0; i < POPULATION_SIZE; i++) {
			if (nns[i].oScore > bestS) {
				bestS = nns[i].oScore;
				bestI = i;
			}
		}
		return nns[bestI];
	}

}

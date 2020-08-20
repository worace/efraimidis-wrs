package wrs.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import wrs.EnumWeightsType;
import wrs.WeightedItem;
import wrs.WeightedSet;
import wrs.stream.StreamSamplerChao;
import wrs.stream.StreamSamplerChaoWithJumps;
import wrs.stream.StreamSamplerES;
import wrs.stream.StreamSamplerESWithJumps;

public class StreamSamplerBenchForReport {
	public static void main(String[] args) {
		long ltimeES;
		long ltimeESJ;
		long ltimeChao;
		long ltimeChaoWithJumps;

		if (args.length < 9) {
			System.out
					.println("usage: seed loops weightsType nFromValue nValues nStep mFromValue mValues mStep");
			System.exit(-1);
		}

		// Parameters
		long seed = Long.parseLong(args[0]);
		int nLoops = Integer.parseInt(args[1]);
		// int nSize = 100000;
		// int nSample = 50;
		EnumWeightsType nWType = EnumWeightsType.valueOf(args[2]);

		// int nFromValue = 10000;
		// int nValues = 51;
		// int nStep = 1000;
		// int mFromValue = 100;
		// int mValues = 51;
		// int mStep = 100;
		
		int nFromValue = Integer.parseInt(args[3]);
		int nValues = Integer.parseInt(args[4]);
		int nStep = Integer.parseInt(args[5]);
		int mFromValue = Integer.parseInt(args[6]);
		int mValues = Integer.parseInt(args[7]);
		int mStep = Integer.parseInt(args[8]);

		System.out.println("seed = " + seed);
		Random myRandom = new Random(seed);

		int nToValue = nFromValue + nStep * (nValues - 1);
		int mToValue = mFromValue + mStep * (mValues - 1);

		// Arrays to store the measurements
		long[][] measES = new long[nValues][mValues];
		long[][] measESJ = new long[nValues][mValues];
		long[][] measChao = new long[nValues][mValues];
		long[][] measChaoJ = new long[nValues][mValues];

		// Print parameter values
		System.out
				.println("indexN \t indexM \t n \t m \t loops \t seed \t A-ES \t A-ESJ \t A-Chao \t A-ChaoJ");

		// Prepare the weighted population
		WeightedSet<WeightedItem> wSet = new WeightedSet<WeightedItem>();
		wSet.ensureCapacity(nToValue);

		for (int num = 0; num < nToValue; num++) {
			double w = 0.5;
			WeightedItem wItem = new WeightedItem(num);
			switch (nWType) {
			case USER:
				System.out
						.println("user-given weights not supported in this version !!");
				System.exit(-1);
				break;
			case INCREASING:
				w = 1 + num;
				break;
			case DECREASING:
				w = nToValue - num;
				break;
			case UNIFORM:
				w = 1;
				break;
			case RANDOM:
			default:
				w = 1 + nToValue * myRandom.nextDouble();
				break;
			}
			wItem.setWeight(w);
			wSet.add(wItem);
		}

		// The first execution of the algorithms is sometimes slower,
		// may be due to just in time compilations ...
		// So we make one additional execution at the beginning
		boolean firstExecution = true;		
		
		int indexN = 0;
		for (int n = nFromValue; n <= nToValue; n += nStep) {
			int indexM = 0;
			{
				int m = mFromValue;
				while (m <= mToValue) {
//				for (int m = mFromValue; m <= mToValue; m += mStep) {
					System.out.print(indexN + ", " + indexM + ", " + n + ", "
							+ m + ", " + nLoops + ", " + seed + ", ");

					// Algorithm ES
					StreamSamplerES samplerES = new StreamSamplerES(m, seed);
					StreamSamplerBenchSet benchSet = new StreamSamplerBenchSet(
							samplerES, wSet);
					ltimeES = benchSet.runExperiment(nLoops);

					// Algorithm ES with Exponential Jumps
					StreamSamplerESWithJumps samplerESJ = new StreamSamplerESWithJumps(
							m, seed);
					benchSet = new StreamSamplerBenchSet(samplerESJ, wSet);
					ltimeESJ = benchSet.runExperiment(nLoops);

					// Algorithm Chao
					StreamSamplerChao samplerChao = new StreamSamplerChao(m,
							seed);
					benchSet = new StreamSamplerBenchSet(samplerChao, wSet);
					ltimeChao = benchSet.runExperiment(nLoops);

					// Algorithm ChaoWithJumps
					StreamSamplerChaoWithJumps samplerChaoWithJumps = new StreamSamplerChaoWithJumps(
							m, seed);
					benchSet = new StreamSamplerBenchSet(samplerChaoWithJumps,
							wSet);
					ltimeChaoWithJumps = benchSet.runExperiment(nLoops);

					measES[indexN][indexM] = ltimeES;
					measESJ[indexN][indexM] = ltimeESJ;
					measChao[indexN][indexM] = ltimeChao;
					measChaoJ[indexN][indexM] = ltimeChaoWithJumps;

					// Print Results
					System.out.println(ltimeES + ", " + ltimeESJ + ", "
							+ ltimeChao + ", " + ltimeChaoWithJumps);

					if (firstExecution) {
						firstExecution = false;
						System.out.println("### Repeating first execution (time usually improves ...) ###");

						continue;
					} else {
						m+= mStep;
					}

					indexM++;
				}
			}
			indexN++;
		}

		writeParametersToFile("wrsParameters.dat", nValues, mValues, nLoops,
				seed);
		writeMeasToFile("A-ES", "wrsES.dat", measES, nLoops, seed);
		writeMeasToFile("A-ESJ", "wrsESJ.dat", measESJ, nLoops, seed);
		writeMeasToFile("A-Chao", "wrsChao.dat", measChao, nLoops, seed);
		writeMeasToFile("A-ChaoJ", "wrsChaoJ.dat", measChaoJ, nLoops, seed);
	}

	static private void writeMeasToFile(String algName, String fName,
			long[][] meas, int nLoops, long seed) {
		try {
			FileWriter fileWriter = new FileWriter(fName);
			PrintWriter printWriter = new PrintWriter(fileWriter);

			int n = meas.length;
			int m = meas[0].length;

			// printWriter.println("### alg: " + algName + ", n: " + n + ", m: "
			// + m + ", nLoops: " + nLoops + ", seed: " + seed);

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					printWriter.print("  " + meas[i][j]);
				}
				printWriter.println();
			}
			printWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static private void writeParametersToFile(String fName, int nValues,
			int mValues, int nLoops, long seed) {
		try {
			FileWriter fileWriter = new FileWriter(fName);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			// TODO: Date
			printWriter.println("### n: " + nValues + ", m: " + mValues
					+ ", nLoops: " + nLoops + ", seed: " + seed);

			printWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package wrs.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import wrs.EnumWeightsType;
import wrs.Parameters;
import wrs.WeightedItem;
import wrs.WeightedSet;
import wrs.stream.StreamSamplerChao;
import wrs.stream.StreamSamplerChaoWithJumps;
import wrs.stream.StreamSamplerES;
import wrs.stream.StreamSamplerESWithJumps;

public class StreamSamplerBenchMain {
	public static void main(String[] args) {
		long ltimeES;
		long ltimeESJ;
		long ltimeChao;
		long ltimeChaoWithJumps;

		Parameters par = new Parameters();

		par.seed = 123;
		par.nLoops = 100;
		par.nSize = 10000;
		par.nSample = 10;
		par.nWType = EnumWeightsType.RANDOM;

		System.out.println("seed = " + par.seed);
		Random myRandom = new Random(par.seed);

		WeightedSet<WeightedItem> wSet = new WeightedSet<WeightedItem>();
		wSet.ensureCapacity(par.nSize);

		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));

		for (int num = 0; num < par.nSize; num++) {
			double w = 0.5;
			WeightedItem wItem = new WeightedItem(num);
			switch (par.nWType) {
			case USER:
				System.out.println("Enter Weight for item " + num + " :");
				try {
					String buf = stdin.readLine();
					w = Double.valueOf(buf).doubleValue();
					break;
				} catch (Exception e) {
					System.err.println("Exception: " + e.getMessage());
					w = 0.5;
					System.err.println("Assigned weight w:= " + w);
				}
			case INCREASING:
				w = 1 + num;
				break;
			case DECREASING:
				w = par.nSize - num;
				break;
			case UNIFORM:
				w = 1;
				break;
			case RANDOM:
			default:
				w = 1 + par.nSize * myRandom.nextDouble();
				break;
			}
			wItem.setWeight(w);
			wSet.add(wItem);
		}

		// Print parameter values
		System.out.println(par.toString());

		// Algorithm ES
		StreamSamplerES samplerES = new StreamSamplerES(par.nSample, par.seed);
		StreamSamplerBenchSet benchSet = new StreamSamplerBenchSet(samplerES, wSet);
		ltimeES = benchSet.runExperiment(par.nLoops);
		
		// Algorithm ES with Exponential Jumps
		StreamSamplerESWithJumps samplerESJ = new StreamSamplerESWithJumps(
				par.nSample, par.seed);
		benchSet = new StreamSamplerBenchSet(samplerESJ, wSet);
		ltimeESJ = benchSet.runExperiment(par.nLoops);
	
		// Algorithm Chao
		StreamSamplerChao samplerChao = new StreamSamplerChao(par.nSample,
				par.seed);
		benchSet = new StreamSamplerBenchSet(samplerChao, wSet);
		ltimeChao = benchSet.runExperiment(par.nLoops);

		// Algorithm ChaoWithJumps
		StreamSamplerChaoWithJumps samplerChaoWithJumps = new StreamSamplerChaoWithJumps(
				par.nSample, par.seed);
		benchSet = new StreamSamplerBenchSet(samplerChaoWithJumps, wSet);
		ltimeChaoWithJumps = benchSet.runExperiment(par.nLoops);

		// Print Results
		System.out.println("Time for Algorithm ES (msec):" + ltimeES);
		System.out
				.println("Time for Algorithm ES with Exponential Jumps (msec):"
						+ ltimeESJ);
		System.out.println("Time for Algorithm Chao (msec):" + ltimeChao);
		System.out.println("Time for Algorithm ChaoWithJumps (msec/loop):"
				+ ltimeChaoWithJumps);

	}

}

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

public class StreamSamplerBench {
	public static void main(String[] args) {
		int numOfInsertions;
		long ltime;
		long ltimeES;
		long ltimeESJ;
		long ltimeChao;
		long ltimeChaoWithJumps;

		Parameters par = new Parameters();

		par.seed = 12345;
		par.nLoops = 1;
		par.nSize = 5000;
		par.nSample = 30;
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
		numOfInsertions = 0;
		ltime = System.currentTimeMillis();
		for (int i = 0; i < par.nLoops; i++) {
			samplerES.initiate();
			for (WeightedItem wItem : wSet) {
				samplerES.feedItem(wItem);
			}
			numOfInsertions += samplerES.getNumOfInsertions();
		}
		ltimeES = System.currentTimeMillis() - ltime;

		// Algorithm ES with Exponential Jumps
		StreamSamplerESWithJumps samplerESJ = new StreamSamplerESWithJumps(
				par.nSample, par.seed);
		numOfInsertions = 0;
		ltime = System.currentTimeMillis();
		for (int i = 0; i < par.nLoops; i++) {
			samplerESJ.initiate();
			for (WeightedItem wItem : wSet) {
				samplerESJ.feedItem(wItem);
			}
			numOfInsertions += samplerESJ.getNumOfInsertions();
		}
		ltimeESJ = System.currentTimeMillis() - ltime;

		// Algorithm Chao
		StreamSamplerChao samplerChao = new StreamSamplerChao(par.nSample,
				par.seed);
		numOfInsertions = 0;
		ltime = System.currentTimeMillis();
		for (int i = 0; i < par.nLoops; i++) {
			samplerChao.initiate();
			for (WeightedItem wItem : wSet) {
				samplerChao.feedItem(wItem);
			}
			numOfInsertions += samplerChao.getNumOfInsertions();
		}
		ltimeChao = System.currentTimeMillis() - ltime;

		// Algorithm ChaoWithJumps
		StreamSamplerChaoWithJumps samplerChaoWithJumps = new StreamSamplerChaoWithJumps(
				par.nSample, par.seed);
		numOfInsertions = 0;
		ltime = System.currentTimeMillis();
		for (int i = 0; i < par.nLoops; i++) {
			samplerChaoWithJumps.initiate();
			for (WeightedItem wItem : wSet) {
				samplerChaoWithJumps.feedItem(wItem);
			}
			numOfInsertions += samplerChaoWithJumps.getNumOfInsertions();
		}
		ltimeChaoWithJumps = System.currentTimeMillis() - ltime;

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

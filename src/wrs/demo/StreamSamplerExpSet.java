package wrs.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import wrs.EnumWeightsType;
import wrs.Parameters;
import wrs.SampledSet;
import wrs.Statistics;
import wrs.WeightedItem;
import wrs.WeightedSet;
import wrs.stream.StreamSamplerChao;
import wrs.stream.StreamSamplerChaoWithJumps;
import wrs.stream.StreamSamplerES;
import wrs.stream.StreamSamplerESWithJumps;

public class StreamSamplerExpSet {
	public static void main(String[] args) {
		int numOfInsertions;
		long ltime;
		long ltimeES;
		long ltimeESJ;
		long ltimeChao;
		long ltimeChaoWithJumps;

		Parameters par = new Parameters();

		par.seed = 1234;
		par.nLoops = 100000;
		par.nSize = 5;
		par.nSample = 3;
		par.nWType = EnumWeightsType.DECREASING;

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
		
//		Statistics statsDummy = new Statistics(new WeightedSet<WeightedItem>(),
//				"Algorithm ES");
		Statistics statsDummy = null;

		{
			// Algorithm ES
			System.out.println();
			System.out.println("Algorithm ES with a Reservoir!");

			StreamSamplerES samplerES = new StreamSamplerES(par.nSample, par.seed);
			Statistics statsES = statsDummy;
			statsES = new Statistics(wSet, "Algorithm ES with a Reservoir");

			numOfInsertions = 0;
			ltime = System.currentTimeMillis();
			for (int i = 0; i < par.nLoops; i++) {
				samplerES.initiate();
				for (WeightedItem wItem : wSet) {
					samplerES.feedItem(wItem);
				}
				SampledSet sample = samplerES.getSample();
				numOfInsertions += samplerES.getNumOfInsertions();
				statsES.addSample(sample);
			}
			ltimeES = System.currentTimeMillis() - ltime;
			ltime = System.currentTimeMillis();

			statsES.print();

			double ESAvgNumIns = ((double) numOfInsertions) / par.nLoops;
			System.out.println("Average number of insertions : " + ESAvgNumIns);

			System.out.println("Time for Algorithm ES (msec/loop):"
					+ ((double) ltimeES / par.nLoops));

			System.out.println("Algorithm ES with a Reservoir!");
		}

		{
			// Algorithm ES with Exponential Jumps
			System.out.println();
			StreamSamplerESWithJumps samplerESJ = new StreamSamplerESWithJumps(
					par.nSample, par.seed);
			Statistics statsESJ = statsDummy;
			statsESJ = new Statistics(wSet,
					"Algorithm ES with Exponential Jumps");

			numOfInsertions = 0;
			ltime = System.currentTimeMillis();
			for (int i = 0; i < par.nLoops; i++) {
				samplerESJ.initiate();
				for (WeightedItem wItem : wSet) {
					samplerESJ.feedItem(wItem);
				}
				SampledSet sample = samplerESJ.getSample();
				numOfInsertions += samplerESJ.getNumOfInsertions();
				statsESJ.addSample(sample);
			}
			ltimeESJ = System.currentTimeMillis() - ltime;
			ltime = System.currentTimeMillis();

			statsESJ.print();

			double ESJAvgNumIns = ((double) numOfInsertions) / par.nLoops;
			System.out
					.println("Average number of insertions : " + ESJAvgNumIns);

			System.out
					.println("Time for Algorithm ES with Exponential Jumps (msec/loop):"
							+ ((double) ltimeESJ / par.nLoops));
		}
		
		{
			// Algorithm Chao
			System.out.println();
			StreamSamplerChao samplerChao = new StreamSamplerChao(
					par.nSample, par.seed);
			Statistics statsChao = statsDummy;
			statsChao = new Statistics(wSet,
					"Algorithm Chao");

			numOfInsertions = 0;
			ltime = System.currentTimeMillis();
			for (int i = 0; i < par.nLoops; i++) {
				samplerChao.initiate();
				for (WeightedItem wItem : wSet) {
					samplerChao.feedItem(wItem);
				}
				SampledSet sample = samplerChao.getSample();
				numOfInsertions += samplerChao.getNumOfInsertions();
				statsChao.addSample(sample);
			}
			ltimeChao = System.currentTimeMillis() - ltime;
			ltime = System.currentTimeMillis();

			statsChao.print();

			double ChaoAvgNumIns = ((double) numOfInsertions) / par.nLoops;
			System.out
					.println("Average number of insertions : " + ChaoAvgNumIns);

			System.out
					.println("Time for Algorithm Chao (msec/loop):"
							+ ((double) ltimeChao / par.nLoops));
		}
		
		{
			// Algorithm ChaoWithJumps
			System.out.println();
			StreamSamplerChaoWithJumps samplerChaoWithJumps = new StreamSamplerChaoWithJumps(
					par.nSample, par.seed);
			Statistics statsChaoWithJumps = statsDummy;
			statsChaoWithJumps = new Statistics(wSet,
					"Algorithm ChaoWithJumps");

			numOfInsertions = 0;
			ltime = System.currentTimeMillis();
			for (int i = 0; i < par.nLoops; i++) {
				samplerChaoWithJumps.initiate();
				for (WeightedItem wItem : wSet) {
					samplerChaoWithJumps.feedItem(wItem);
				}
				SampledSet sample = samplerChaoWithJumps.getSample();
				numOfInsertions += samplerChaoWithJumps.getNumOfInsertions();
				statsChaoWithJumps.addSample(sample);
			}
			ltimeChaoWithJumps = System.currentTimeMillis() - ltime;
			ltime = System.currentTimeMillis();

			statsChaoWithJumps.print();

			double ChaoWithJumpsAvgNumIns = ((double) numOfInsertions) / par.nLoops;
			System.out
					.println("Average number of insertions : " + ChaoWithJumpsAvgNumIns);

			System.out
					.println("Time for Algorithm ChaoWithJumps (msec/loop):"
							+ ((double) ltimeChaoWithJumps / par.nLoops));
		}
	}

}

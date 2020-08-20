package wrs.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import wrs.EnumWeightsType;
import wrs.SampledSet;
import wrs.WeightedItem;
import wrs.WeightedSet;
import wrs.stream.StreamSamplerChao;
import wrs.stream.StreamSamplerES;
import wrs.stream.StreamSamplerESWithJumps;

public class StreamSamplerDemo {
	public static void main(String[] args) {

		// Parameters
		int populationSize; // Population Size;
		int sampleSize; // Sample Size

		populationSize = 20;
		sampleSize = 5;

		// Random number generator
		long seed = 1234;
		Random myRandom = new Random(seed);

		// Prepare random weighted population V
		WeightedSet<WeightedItem> wSet = new WeightedSet<WeightedItem>();
		wSet.ensureCapacity(populationSize);

		EnumWeightsType nWType = EnumWeightsType.RANDOM;

		BufferedReader stdin = new BufferedReader(new InputStreamReader(
				System.in));

		for (int num = 0; num < populationSize; num++) {
			double w = 0.5;
			WeightedItem wItem = new WeightedItem(num);
			switch (nWType) {
			case USER:
				System.out.print("Enter Weight for item " + num + ": ");
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
				w = populationSize - num;
				break;
			case UNIFORM:
				w = 1;
				break;
			case RANDOM:
			default:
				w = 1 + populationSize * myRandom.nextDouble();
				break;
			}
			wItem.setWeight(w);
			wSet.add(wItem);
		}

		{
			// Stream Sampler with Algorithm ES
			System.out.println("Algorithm ES with a Reservoir!");
			StreamSamplerES samplerES = new StreamSamplerES(sampleSize, seed);
			samplerES.initiate();
			for (WeightedItem wItem : wSet) {
				samplerES.feedItem(wItem);
			}
			SampledSet sampleES = samplerES.getSample();
			sampleES.printAll();
		}

		{
			// Stream Sampler with Algorithm ESJ
			System.out.println("Algorithm ES with Jumps!");
			StreamSamplerESWithJumps samplerESJ = new StreamSamplerESWithJumps(
					sampleSize, seed);
			samplerESJ.initiate();
			for (WeightedItem wItem : wSet) {
				samplerESJ.feedItem(wItem);
			}
			SampledSet sampleESJ = samplerESJ.getSample();
			sampleESJ.printAll();
		}

		{
			// Stream Sampler with Algorithm Chao
			System.out.println("Algorithm Chao!");
			StreamSamplerChao samplerChao = new StreamSamplerChao(sampleSize,
					seed);
			samplerChao.initiate();
			for (WeightedItem wItem : wSet) {
				samplerChao.feedItem(wItem);
			}
			SampledSet sampleChao = samplerChao.getSample();
			sampleChao.printAll();
		}

	}

}

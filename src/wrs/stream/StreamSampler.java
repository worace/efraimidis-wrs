package wrs.stream;

import java.util.Random;

import wrs.SampledSet;
import wrs.WeightedItem;

abstract public class StreamSampler {
	protected long seed;
	protected Random random;
	protected int sampleSize; // sampleSize, reservoirLength
	protected long itemsProcessed; // the number of items that have been processed so far
	
	public StreamSampler(int parSampleSize, long parSeed) {
		sampleSize = parSampleSize;
		seed = parSeed;
		random = new Random(seed);
	}

	abstract public void initiate();
	
	abstract public void feedItem(WeightedItem newItem);
	
	abstract public SampledSet getSample();
	
	abstract public int getNumOfInsertions();
}

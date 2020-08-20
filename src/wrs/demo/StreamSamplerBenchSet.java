package wrs.demo;

import wrs.WeightedItem;
import wrs.WeightedSet;
import wrs.stream.StreamSampler;

public class StreamSamplerBenchSet {
	StreamSampler streamSampler; // The sampler algorithm
	WeightedSet<WeightedItem> wSet; // The random population

	int numOfInsertions;
	long processingTime;
	
	public StreamSamplerBenchSet(StreamSampler parStreamSampler, WeightedSet<WeightedItem> parWSet) {
		streamSampler = parStreamSampler;
		wSet = parWSet;
	}
	
	public long runExperiment(int nLoops) {
		numOfInsertions = 0;
		long time = System.currentTimeMillis();
		for (int i = 0; i < nLoops; i++) {
			streamSampler.initiate();
			for (WeightedItem wItem : wSet) {
				streamSampler.feedItem(wItem);
			}
			numOfInsertions += streamSampler.getNumOfInsertions();
		}
		processingTime = System.currentTimeMillis() - time;	
		return processingTime;
	}
}

package wrs.stream;

import wrs.SampledSet;
import wrs.WeightedItem;
import wrs.WeightedSet;

public class StreamDemo {
	public static void main(String[] args) {
		int nSize = 1000;
		int nSample = 10;
		
		long seed = 103;
		
		WeightedSet<WeightedItem> wSet = new WeightedSet<WeightedItem>();
		wSet.ensureCapacity(nSize);
		
		for (int num = 0; num < nSize; num++) {
			WeightedItem wItem = new WeightedItem(num);
			double w = 1 + num;
			wItem.setWeight(w);
			wSet.add(wItem);
		}
		
		StreamSamplerES sampler = new StreamSamplerES(nSample, seed);
		
		for (WeightedItem wi:wSet) {
			sampler.feedItem(wi);
		}
		
		SampledSet s = sampler.getSample();
		
		s.printAll();
    }
}

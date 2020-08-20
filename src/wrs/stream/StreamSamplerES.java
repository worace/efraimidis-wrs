package wrs.stream;

import wrs.MyHeap;
import wrs.SampledItem;
import wrs.SampledSet;
import wrs.WeightedItem;

public class StreamSamplerES extends StreamSampler {

	
	// The heap data structure for the reservoir
	// The generic class "java.util.PriorityQueue" can also be used
	MyHeap<SampledItem> myHeap;
	
	
	int numOfInsertions; // Contains the number of jumps/reservoir insertions

	// during the last Sample Generation

	public StreamSamplerES(int parSampleSize, long parSeed) {
		super(parSampleSize, parSeed);

		myHeap = new MyHeap<SampledItem>(sampleSize);
	}

	public void initiate() {
		myHeap.clearAndInit();
		
		itemsProcessed = 0;
		numOfInsertions = 0;
	}
	
	public void feedItem(WeightedItem newItem) {

		if (itemsProcessed < sampleSize) {
			// The first m items go directly into the reservoir
			// WeightedItem wi = (WeightedItem) netItem.get(i);
			double key = newItem.genKey(random);
			SampledItem kItem = new SampledItem(newItem, key);
			myHeap.addItem(kItem);
			// kvalue[i] += key;
			// kcount[i] += 1;
		} else {
			// current threshold to enter the reservoir
			SampledItem rWorstItem = myHeap.rootItem();
			double CurrentThreshold = rWorstItem.key;

			// generate random key
			double key = newItem.genKey(random);

			// If the key is larger then the current threshold
			// insert the item into the reservoir
			if (key > CurrentThreshold) {
				SampledItem kItem = new SampledItem(newItem, key);
				myHeap.replaceHead(kItem);
				numOfInsertions++;
			}
		}
		
		itemsProcessed++;
	}

	public SampledSet getSample() {
		int numOfItemsInSample = myHeap.getLength(); 
			
		// Prepare the output: SampledSet
		SampledSet s = new SampledSet(numOfItemsInSample);
		for (int i = 0; i < numOfItemsInSample; i++) {
			s.sample[i] = new SampledItem();
		}
		
		for (int i = 0; i < numOfItemsInSample; i++) {
			SampledItem kItem =  myHeap.peek(i);
			SampledItem sItem = s.sample[i];
			sItem.wItem = kItem.wItem;
			sItem.key = kItem.key;
		}
		return s;
	}
	
	public int getNumOfInsertions() {
		return numOfInsertions;
	}
}

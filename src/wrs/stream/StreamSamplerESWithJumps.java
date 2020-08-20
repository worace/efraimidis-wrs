package wrs.stream;

import wrs.MyHeap;
import wrs.SampledItem;
import wrs.SampledSet;
import wrs.WeightedItem;

public class StreamSamplerESWithJumps extends StreamSampler {

	MyHeap<SampledItem> myHeap;
	// PriorityQueue <SampledItem> myHeap;
	int numOfInsertions; // Contains the number of jumps/reservoir insertions

	// Data of Last Exponential Jump
	boolean inFlight; // The current exponential is not exhausted yet
	double r1; // the random variate used in the current exponential jump
	double expJump; // the current exponential jump

	// 
	double currentWeight;
	double nextWeight;
	long index; // The item
	long i; // current index
	
	/**
	 * 
	 * @param parSampleSize
	 * @param parSeed
	 */
	public StreamSamplerESWithJumps(int parSampleSize, long parSeed) {
		super(parSampleSize, parSeed);

		myHeap = new MyHeap<SampledItem>(sampleSize);

	}

	public void initiate() {
		myHeap.clearAndInit();
		
		itemsProcessed = 0;
		numOfInsertions = 0;
		inFlight = false;
	}

	
	public void feedItem(WeightedItem newItem) {
		SampledItem rWorstItem;
		double currentThreshold;
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
			rWorstItem = myHeap.rootItem();
			currentThreshold = rWorstItem.key;

			// Exponential Jump
			if (!inFlight) {		
				// generate random key
				// double key = newItem.genKey(random);
				
				// Generate exponential jump
				r1 = random.nextDouble();
				expJump = Math.log(r1) / Math.log(currentThreshold);
				
				currentWeight = 0;
				nextWeight = 0;
				index = -1;
				i = itemsProcessed;
				
				inFlight = true;
			}		
			
			// Check if the Exponential Jump lands on the current item
			nextWeight = currentWeight + newItem.getWeight();
			if (expJump < nextWeight) {
				index = itemsProcessed;
				double lowJ = currentWeight;
				double highJ = nextWeight;
				
				// Prepare the new item
				SampledItem sItem = new SampledItem();
				sItem.wItem = newItem;

				// We have to calculate a key for the new item
				// The ley has to be in the interval (key-of-replaced-item, max-key]
				double weight = sItem.wItem.getWeight();
				double lowR = Math.pow(currentThreshold, weight);

				// We use the random number of the exponential jump 
				// to calculate the random key
				// The random number has to be "normalized" for its new use
				double lthr = Math.pow(currentThreshold, highJ);
				double hthr = Math.pow(currentThreshold, lowJ);
				double r2 = (r1 - lthr) / (hthr - lthr);

				// OK double r3 = lowR + (1-lowR) * myRandom.rand();
				double r3 = lowR + (1 - lowR) * r2; // myRandom.rand();
				double key = Math.pow(r3, 1 / weight);

				sItem.key = key;

				// Insert the Item into the Reservoir
				myHeap.replaceHead(sItem);
				numOfInsertions++;
				
				inFlight = false;
			} else {
				currentWeight = nextWeight;
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
			SampledItem kItem = myHeap.peek(i);
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

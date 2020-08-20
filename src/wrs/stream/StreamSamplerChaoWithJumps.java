package wrs.stream;

import java.util.Iterator;
import java.util.TreeSet;

import wrs.MyHeap;
import wrs.SampledItem;
import wrs.SampledSet;
import wrs.WeightedItem;
import wrs.WeightedSet;

public class StreamSamplerChaoWithJumps extends StreamSampler {

	MyHeap<SampledItem> myHeap;

	// PriorityQueue <SampledItem> myHeap;
	int numOfInsertions; // Contains the number of jumps/reservoir insertions

	// set A defined by Chao: items with probLevel = 1
	TreeSet<WeightedItem> itemsA;

	// set B defined by Chao: items with probLevel = 1 in the previous round
	// and probLevel < 1 in the current round
	ChaoWeightedSetB itemsB;

	double accumulatedWeight; // total weight of all processed items
	double totalWeightInA; // total weight of items in A
	int numOfItemsInA; // total number of items in A

	// all sampled items with probLevel < 1
	WeightedSet<WeightedItem> feasSample;
	int numOfItemsInFeasSample;

	private class RandomJump {
		double jump; // The random jump: [0,1]
		double probMassCovered; // How much of the probability mass has been covered
		double probNoneSelectedSoFar; // Prob that none item has been selected so far (in this jump)
		boolean valid; // Is the current data valid
		
		RandomJump() {
			valid = false;
		}
		
		void clearAndInit() {
			jump = random.nextDouble();
			probMassCovered = 0;
			probNoneSelectedSoFar = 1;
			valid = true;
		}
		
		void makeInvalid() {
			valid = false;
		}
		
		boolean itemIsRandomlySelected(double probLevel) {
			boolean itemIsSelected = false;
			
			// make sure there is a valid jump
			if (!valid) {
				clearAndInit();
			}
			double newProbMass = probNoneSelectedSoFar * probLevel;
			probMassCovered += newProbMass;
			probNoneSelectedSoFar *= (1-probLevel);
			
			if (jump < probMassCovered) {
				valid = false;
				itemIsSelected = true;
			}
			
			return itemIsSelected;
		}
	}

	RandomJump randomJump;
	
	/**
	 * 
	 * @param parSampleSize
	 * @param parSeed
	 */
	public StreamSamplerChaoWithJumps(int parSampleSize, long parSeed) {
		super(parSampleSize, parSeed);

		myHeap = new MyHeap<SampledItem>(sampleSize);
		randomJump = new RandomJump();
	}

	public void initiate() {
		myHeap.clearAndInit();
		randomJump.clearAndInit();

		itemsProcessed = 0;
		numOfInsertions = 0;

		itemsA = new TreeSet<WeightedItem>(new WeightedItemComparator());
		itemsB = new ChaoWeightedSetB();

		accumulatedWeight = 0;
		totalWeightInA = 0;
		numOfItemsInA = 0;

		// all sampled items with probLevel < 1
		feasSample = new WeightedSet<WeightedItem>(sampleSize);
		numOfItemsInFeasSample = 0;
	}


	public void feedItem(WeightedItem newItem) {

		if (itemsProcessed < sampleSize) {
			// The first m items go directly into the reservoir
			// The first m items are all "OverItems" (prob of inclusion >= 1)
			// and can go directly into the SampledItem

			// System.out.println("Algorithm Chao: Processing item: " +
			// newItem);
			itemsA.add(newItem);
			accumulatedWeight += newItem.getWeight();

		} else {
			// Items m+1, m+2, ..., n

			// System.out.println("Algorithm Chao: Processing item: " +
			// newItem);

			// update the accumulated weight
			accumulatedWeight += newItem.getWeight();

			double probWk = 0; // The probability that the new item is inserted
			// into the sample (defined in Chao)

			boolean newItemInA = false;
			boolean newItemInSample = false;
			boolean itemsAOldEmpty = itemsA.isEmpty();

			// check if there are "overItems"
			// and prepare the sets A and B (defined in Chao's paper)

			numOfItemsInA = 0;

			if (itemsAOldEmpty) {
				// itemsA is empty
				double probLevel = calculateProbLevel(newItem,
						accumulatedWeight, sampleSize);
				if (probLevel >= 1) {
					probWk = 1;
					// new item in A
					newItemInA = true;
					numOfItemsInA++;
					totalWeightInA += newItem.getWeight();
					itemsA.add(newItem);
					//numOfItemsInTempSample++;
				} else {
					// new item not in A; A remains empty
					probWk = probLevel;
				}
			} else {
				// itemsA is not empty
				// create a set with all items of the old A and the new D (the
				// new item)
				// TreeSet<WeightedItem> itemsAD = (TreeSet<WeightedItem>)
				// itemsA
				// .clone();
				itemsA.add(newItem); // add the new item

				Iterator<WeightedItem> iteratorA = itemsA.descendingIterator();

				// clear the set A
				// itemsA.clear();

				// clear the set B
				itemsB.clear();

				boolean doneWithA = false;
				numOfItemsInA = 0;
				totalWeightInA = 0;
				while (iteratorA.hasNext()) {
					if (!doneWithA) {
						// check for each item of AD if it will enter A
						// (including the new item "newItem")
						WeightedItem itemAD = iteratorA.next();

						double probLevel = calculateProbLevel(itemAD,
								accumulatedWeight - totalWeightInA, sampleSize
										- numOfItemsInA);
						if (itemAD == newItem) {
							probWk = Math.min(probLevel, 1);
						}

						if (probLevel >= 1) {
							// the item remains in A
							numOfItemsInA++;
							totalWeightInA += itemAD.getWeight();
							// itemsA.add(itemAD);
							if (itemAD == newItem) {
								newItemInA = true;
							}
						} else {
							// remove the item from A
							iteratorA.remove();

							// all following items will also be removed
							// since they have <= weight
							doneWithA = true;
							if (itemAD != newItem) {
								ChaoWeightedItem chaoItemAD = new ChaoWeightedItem(
										itemAD);
								chaoItemAD.setChaoProb(probLevel);
								itemsB.add(chaoItemAD);
							}
						}
					} else {
						// done with A; all remaining items of AD are moved to B
						// (except the new item "newItem")
						WeightedItem itemAD = iteratorA.next();
						double probLevel = calculateProbLevel(itemAD,
								accumulatedWeight - totalWeightInA, sampleSize
										- numOfItemsInA);
						if (probLevel > 1) {
							String errorStr = "ERROR: probLevel:" + probLevel
									+ ", should be <= 1!";
							System.err.println(errorStr);
							// throw new Exception(errorStr);
							System.exit(-1);
						}
						iteratorA.remove();
						if (itemAD != newItem) {
							ChaoWeightedItem chaoItemAD = new ChaoWeightedItem(
									itemAD);
							chaoItemAD.setChaoProb(probLevel);
							itemsB.add(chaoItemAD);
						} else {
							probWk = probLevel;
						}
					}
				}
			}

			// //////////////////////////////////////////////
			// decide if the new item will be in the sample
			if (itemsAOldEmpty && (!newItemInA)) {
				// case 1: A was empty and remains empty
				double probLevel = calculateProbLevel(newItem,
						accumulatedWeight, sampleSize);
				if (probLevel >= 1) {
					System.err
							.println("New item with ProbLevel >=1: Unexpected at this place !");// TODO
					// throw new Exception(
					// "New item with ProbLevel >=1: Unexpected at this place !");
					System.exit(-1);
				} else {
					//newItemInSample = (random.nextDouble() < probLevel);
					newItemInSample = randomJump.itemIsRandomlySelected(probLevel);
				}
			} else if (itemsAOldEmpty && newItemInA) {
				// case 2: A was empty and the new item is inserted into A
				newItemInSample = true;
				randomJump.makeInvalid(); // make the random Jump invalid, because of the "infeasible" item
			} else if ((!itemsAOldEmpty) && (!newItemInA)) {
				// A has items and the new item is not in A
				double probLevel = calculateProbLevel(newItem,
						accumulatedWeight - totalWeightInA, sampleSize
								- numOfItemsInA);
				if (probLevel >= 1) {
					System.err
							.println("New item with ProbLevel >=1: Unexpected at this place !");// TODO
					// throw new Exception(
					// "New item with ProbLevel >=1: Unexpected at this place !");
					System.exit(-1);
				} else {
					//newItemInSample = (random.nextDouble() < probLevel);
					newItemInSample = randomJump.itemIsRandomlySelected(probLevel);
				}
			} else if ((!itemsAOldEmpty) && (newItemInA)) {
				// A has items and the item is in A
				newItemInSample = true;
				randomJump.makeInvalid(); // make the random Jump invalid, because of the "infeasible" item
			}

			// ////////////////////////////////////////////////////
			// make the modifications

			if (!newItemInSample) {
				// the new item is not in the random sample
				// no modifications to the sample
				for (ChaoWeightedItem chaoItemB : itemsB) {
					// tempSample.set(numOfItemsInTempSample, itemB);
					WeightedItem itemB = chaoItemB.getWeightedItem();
					feasSample.add(itemB);
					numOfItemsInFeasSample++;
					if (numOfItemsInFeasSample > sampleSize) {
						System.err.println("numOfItemsInFeasSample: " + numOfItemsInFeasSample 
								+ ", sampleSize: " + sampleSize);
					}
				}
				itemsB.clear();
			} else {
				// the new item is in the sample
				if (itemsB.isEmpty()) {
					// B is empty

					// select uniformly an item from the reservoir
					int index = random.nextInt(sampleSize - numOfItemsInA);

					if (newItemInA) {
						// remove the selected item to free one (more) place for
						// set A
						feasSample.remove(index);
						numOfItemsInFeasSample--;
					} else {
						// and replace the selected item with the new item
						feasSample.set(index, newItem);
					}
					
					if ((numOfItemsInA + numOfItemsInFeasSample) != sampleSize) {
						System.err.println("A-ChaoJ: Total items are "
								+ (numOfItemsInA + numOfItemsInFeasSample)
								+ " items (" + numOfItemsInA + " in A and " + numOfItemsInFeasSample +" in feasSample;"
								+ "while the expected size was "
								+ sampleSize + " items !");// TODO
						System.exit(-1);
					}

				} else { // B has items
					// calculate prob for items in B
					itemsB.updateChaoTkjProbabilities(probWk);
					int numOfItemsInB = itemsB.size();

					// check
					// double probToDropFromB = itemsB.getTotalChaoTkjProb();

					double randomJumpInB = (random.nextDouble());
					int indexB = itemsB.selectItemByChaoTkjProb(0, randomJumpInB);

					if (indexB >= 0) {
						// an item from B has been selected and will be dropped

						// replace this item with the new item
						if (newItemInA) {
							// remove the selected item to free one (more) place
							// for set A
							itemsB.remove(indexB);
							numOfItemsInB--;
						} else {
							// and replace the selected item with the new item
							itemsB.set(indexB, new ChaoWeightedItem(newItem));
						}
					} else {
						// select an item from the tempSample and remove it
						int index = random.nextInt(numOfItemsInFeasSample);

						// replace this item with the new item
						if (newItemInA) {
							// remove the selected item to free one (more) place
							// for set A
							feasSample.remove(index);
							numOfItemsInFeasSample--;
						} else {
							// and replace the selected item with the new item
							feasSample.set(index, newItem);
						}
					}
					// insert the remaining items of B into the tempSample
					for (ChaoWeightedItem chaoItemB : itemsB) {
						// tempSample.set(numOfItemsInTempSample, itemB);
						WeightedItem itemB = chaoItemB.getWeightedItem();
						feasSample.add(itemB);
						numOfItemsInFeasSample++;
						if (numOfItemsInFeasSample > sampleSize) {
							System.err.println("numOfItemsInFeasSample: " + numOfItemsInFeasSample 
									+ ", sampleSize: " + sampleSize);
						}
					}
					itemsB.clear();
				}

				numOfInsertions++;
			}

			// /////////////////////////////////////////////////////////

		}

		itemsProcessed++;
	}

	public SampledSet getSample() {
		SampledSet s = new SampledSet(sampleSize);

		// Prepare the output: SampledSet
		int finalSamplePosition = 0;

		// sampled items (with probLevel < 1)
		for (WeightedItem wItem : feasSample) {
			SampledItem kItem = new SampledItem(wItem, Double.NaN);
			s.sample[finalSamplePosition] = kItem;
			finalSamplePosition++;
		}

		// items in set A
		for (WeightedItem aItem : itemsA) {
			SampledItem kItem = new SampledItem(aItem, Double.NaN);
			s.sample[finalSamplePosition] = kItem;
			finalSamplePosition++;
		}

		// check
		if (finalSamplePosition == sampleSize) {
			// System.out.println("OK: Sampled " + sanpleSize + " items !");
		} else {
			System.err.println("ERROR: Sampled " + finalSamplePosition
					+ " items instead of " + sampleSize + " items !");
		}

		return s;
	}

	public int getNumOfInsertions() {
		return numOfInsertions;
	}

	/**
	 * 
	 * @param wi
	 *            the weighted item
	 * @param effectiveAccumulatedWeight
	 *            the total weight of items that are not in the "overItems" set
	 * @param effectiveReservoirSize
	 *            the size of the reservoir minus the positions that are
	 *            occupied by "over" items
	 * @return
	 */
	public static double calculateProbLevel(WeightedItem wi,
			double effectiveAccumulatedWeight, double effectiveReservoirSize) {
		double probLevel = (wi.getWeight() * effectiveReservoirSize)
				/ effectiveAccumulatedWeight;
		return probLevel;
	}
}

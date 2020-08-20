package wrs.stream;

import java.util.ArrayList;

import wrs.ChaoUtils;
import wrs.WeightedItem;

public class ChaoWeightedSetB extends ArrayList<ChaoWeightedItem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ChaoWeightedSetB() {
		this(0);
	}

	public ChaoWeightedSetB(int len) {
		super(len);
	}

	public double getTotalWeight() {
		// m_totalWeight = -1;

		double totalWeight = 0;

		for (ChaoWeightedItem chaoItemB:this) {
			totalWeight += chaoItemB.getWeight();
		}

		return totalWeight;
	}

	public double getTotalChaoTkjProb() {
		double totalTkjProb = 0;

		for (ChaoWeightedItem chaoItemB:this) {
			totalTkjProb += chaoItemB.getChaoTkjProb();
		}

		return totalTkjProb;
	}
	
	public int selectItemByChaoTkjProb(int startItem, double jump) {
		double currentWeight = 0;
		double nextWeight = 0;

		int index = -1; // The item
		int size = this.size();
		for (int i = startItem; i < size; i++) {
			nextWeight = currentWeight
					+ ((ChaoWeightedItem) this.get(i)).getChaoTkjProb();
			if (jump < nextWeight && jump > currentWeight) {
				index = i;
				break;
			}
			currentWeight = nextWeight;
		}
		return index;
	}
	
	public void updateChaoProbabilities(double effectiveAccumulatedWeight, double effectiveReservoirSize) {
		for (ChaoWeightedItem chaoItemB:this) {
			WeightedItem itemB = chaoItemB.getWeightedItem(); 
			double probLevel = ChaoUtils.calculateProbLevel(itemB, effectiveAccumulatedWeight, effectiveReservoirSize);
			double chaoProb = Math.min(probLevel, 1);
			chaoItemB.setChaoProb(chaoProb);
		}
	}
	
	public void updateChaoTkjProbabilities(double probWk) {
		for (ChaoWeightedItem chaoItemB:this) {
			double chaoProb = chaoItemB.getChaoProb();
			double chaoTkjProb = (1 - chaoProb) / probWk;
			chaoItemB.setChaoTkjProb(chaoTkjProb);
		}
	}

}

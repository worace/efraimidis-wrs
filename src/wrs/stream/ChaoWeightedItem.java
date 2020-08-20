package wrs.stream;

import wrs.WeightedItem;

public class ChaoWeightedItem {
	WeightedItem wi;
	double chaoProb; // probability pi() defined in Chao
	double chaoTkjProb; // probability Tkj defined in Chao

	public ChaoWeightedItem(WeightedItem parWi) {
		wi = parWi;
	}

	public WeightedItem getWeightedItem() {
		return wi;
	}
	
	public double getWeight() {
		return wi.getWeight();
	}

	public void setChaoProb(double parChaoProb) {
		chaoProb = parChaoProb;
	}

	public double getChaoProb() {
		return chaoProb;
	}

	public void setChaoTkjProb(double parChaoTkjProb) {
		chaoTkjProb = parChaoTkjProb;
	}

	public double getChaoTkjProb() {
		return chaoTkjProb;
	}

	public String toString() {
		return "ChaoWeightedItem, id:" + wi.getID() + ", weight:"
				+ wi.getWeight() + ", chaoProb:" + chaoProb
				+ "chaoTkjProb:" + chaoTkjProb;
	}
}

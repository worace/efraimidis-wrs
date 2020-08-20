package wrs;

/**
 * The Population
 *
 */

import java.util.ArrayList;


public class WeightedSet<E> extends ArrayList <E>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	double m_totalWeight;
	double m_totalInverseWeight;
	double m_maxWeight;

	public WeightedSet() {
		this(0);
	}

	public WeightedSet(int len) {
		super(len);
		m_totalWeight = -1; // Not valid
		m_totalInverseWeight = -1; // Not valid
		m_maxWeight = -1;
	}
	
	public double getMaxWeight() {
		if (m_maxWeight >= 0)
			return m_maxWeight;
		else {
			double maxWeight = 0;

			int Size = this.size();
			for (int i = 0; i < Size; i++) {
				double weight = ((WeightedItem) this.get(i)).getWeight();
				if (weight > maxWeight) {
					maxWeight = weight;
				}
			}
			m_maxWeight = maxWeight;
			return maxWeight;
		}
	}

	public double getTotalWeight() {
		if (m_totalWeight >= 0)
			return m_totalWeight;
		else {
			double totalWeight = 0;

			int Size = this.size();
			for (int i = 0; i < Size; i++) {
				totalWeight += ((WeightedItem) this.get(i)).getWeight();
			}
			m_totalWeight = totalWeight;
			return totalWeight;
		}
	}

	// Given a (weight <= total weight) start from item c and find the
	// appropriate item to which this weight corresponds
	public int selectItemByWeightAndReturnSurroundingBounds(int startItem, double weight, MyDouble lowJ,
			MyDouble highJ) {
		double currentWeight = 0;
		double nextWeight = 0;

		int index = -1; // The item
		int Size = this.size();
		for (int i = startItem; i < Size; i++) {
			nextWeight = currentWeight
					+ ((WeightedItem) this.get(i)).getWeight();
			if (weight < nextWeight && weight > currentWeight) {
				index = i;
				lowJ.setValue(currentWeight);
				highJ.setValue(nextWeight);
				break;
			}
			currentWeight = nextWeight;
		}

		return index;
	}

	// Creates a Subset that contains all items except "id"
	public WeightedSet<E> createSubset(int id) {
		WeightedSet<E> subset;

		subset = (WeightedSet<E>) this.clone();

		// Remove the item with id = "id";
		int size = this.size();
		for (int i = 0; i < size; i++) {
			if (((WeightedItem) subset.get(i)).getID() == id) {
				subset.removeItem(i);
				return subset;
			}
		}

		return subset;
	}

	private double calculateRecursive(WeightedSet<E> wSet, int id, int depth) {
		if (depth < 0)
			return 0;

		if (depth == 0)
			return 0;

		int Cardinality = wSet.size();
		double TotalSetWeight = wSet.getTotalWeight();
		double TotalProbability = 0;
		double TmpWeight;
		double RecWeight;
		for (int i = 0; i < Cardinality; i++) {
			WeightedItem wItem = (WeightedItem) wSet.get(i);
			if (wItem.getID() == id) {
				TmpWeight = wItem.getWeight() / TotalSetWeight;
				TotalProbability += TmpWeight;
			} else {
				WeightedSet<E> wSubset = wSet.createSubset(wItem.getID());
				TmpWeight = wItem.getWeight() / TotalSetWeight;
				RecWeight = calculateRecursive(wSubset, id, depth - 1);
				// RecWeight /= TotalSetWeight;
				TotalProbability += TmpWeight * RecWeight;
			}
		}
		// System.out.println("Recursive: ID=" + id + ", Depth=" + depth +
		// ", PROB=" + TotalProbability);
		return TotalProbability;
	}

	public void calculateTheoreticProb(int sample) {
		int Size = this.size();
		for (int num = 0; num < Size; num++) {
			double w = this.calculateRecursive(this, num, sample);
			WeightedItem wItem = (WeightedItem) this.get(num);
			wItem.setTheoreticProb(w);
			System.out
					.println("ID: " + wItem.getID() + ", Weight: "
							+ wItem.getWeight() + ", Prob: "
							+ wItem.getTheoreticProb());
		}
	}

	public double totalTheoreticProb() {
		double dbl = 0;

		int Size = this.size();
		for (int num = 0; num < Size; num++) {
			WeightedItem wItem = (WeightedItem) this.get(num);
			dbl += wItem.getTheoreticProb();
		}

		return dbl;
	}

	public void printTheoreticProb() {
		System.out.println("-> Theoretical Probabilities");
		int Size = this.size();
		for (int num = 0; num < Size; num++) {
			WeightedItem wItem = (WeightedItem) this.get(num);
			System.out
					.println("ID: " + wItem.getID() + ", Weight: "
							+ wItem.getWeight() + ", Prob: "
							+ wItem.getTheoreticProb());
		}
	}

	public void removeItem(int index) {
		WeightedItem wItem = (WeightedItem) this.remove(index);
		if (m_totalWeight >= 0) {
			m_totalWeight -= wItem.getWeight();
		}
		if (m_totalInverseWeight >= 0) {
			m_totalInverseWeight -= 1/(wItem.getWeight());
		}
		if (wItem.getWeight() >= m_maxWeight) {
			m_maxWeight = -1;
			getMaxWeight();
		}
	}
	
	public void clear() {
		super.clear();
		m_maxWeight = -1;
		m_totalWeight = -1;
		m_totalInverseWeight = -1;
	}
}

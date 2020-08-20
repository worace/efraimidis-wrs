package wrs;

import java.util.Random;

/**
 * One item of the Population
 *
 */

public class WeightedItem {
  //private int m_id;
  int m_id;
  private double m_weight = 0;
  private double m_TheoreticalProbability = -1;

  public WeightedItem(int id) {
    m_id = id;
  }

  public int getID() {
    return m_id;
  }

  public double getWeight() {
    return m_weight;
  }

  public void setWeight(double weight) {
    m_weight = weight;
  }

  public double genKey(Random myRandom) {
       return Math.pow(myRandom.nextDouble(), 1/m_weight);
  }

  public void setTheoreticProb(double prob)
  {
    m_TheoreticalProbability = prob;
  }

  public double getTheoreticProb()
  {
    return m_TheoreticalProbability;
  }
  
  public String toString() {
	  return "WeightedItem, id:" + m_id + ", weight:" + m_weight;
  }
  
  public int compare(WeightedItem item) {
	  return compare(this, item);
  }
  
  static public int compare(WeightedItem item1, WeightedItem item2) {
		WeightedItem wi1 =  (WeightedItem) item1;
		WeightedItem wi2 =  (WeightedItem) item2;
		double item1Weight = wi1.getWeight();
		double item2Weight = wi2.getWeight();
		if (item1Weight > item2Weight)
			return 1;
		else if (item1Weight < item2Weight)
			return -1;
		else {
			// the weights are equal, use the ID's
			// this is important for data structures like TreeSet's
			// that do not allow duplicate items
			int id1 = wi1.getID();
			int id2 = wi2.getID();
			if (id1 < id2) {
				return 1;
			} else if (id1 > id2) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}

package wrs;

public class ChaoUtils {
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

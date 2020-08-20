package wrs;


/**
 * A sampled item (for the random sample)
 *
 */

public class SampledItem {
  public WeightedItem wItem; // The corresponding weighted item
  public int order = -1; // The step when the item was selected (valid for Algorithm D)
  public double key = -1; // The key of the item (valid for algorithm A)

  //double threshold = -1; // Threshold before this item
//  double totalweight = -1; // The total keyweight accumulated by this item
//  boolean bEnteredReservoir = false; // Indicates that the item entered (at least temporarily) the reservoir

  public SampledItem() {

  }

  public SampledItem(WeightedItem wItem, double key) {
    this.wItem = wItem;
    this.key = key;
  }

  public SampledItem(WeightedItem wItem, double key, int order) {
    this.wItem = wItem;
    this.key = key;
    this.order = order;
  }
}

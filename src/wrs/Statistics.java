package wrs;


public class Statistics {
  String name;
  int nsam; // Number of samples that have been processed
  double[] pos; // Information for every item
  WeightedSet<WeightedItem> ws; // The population

  public Statistics(WeightedSet<WeightedItem> ws, String name) {
    this.ws = ws;
    this.name = name;
    nsam = 0;
    int plen = ws.size();
    pos = new double[plen];

    for ( int i = 0; i < plen; i ++ ) {
      pos[i] = 0;
    }
  }

  public void addSample(SampledSet sset) {
    nsam ++; // One more sample
    int slen = sset.length();
    for ( int i = 0; i < slen; i ++ ) {
      SampledItem sItem = sset.sample[i];
      int id = sItem.wItem.getID();
      pos[id] += 1;
    }
  }
  public void print() {
   System.out.println("Statistics " + name + " :");
   int plen = ws.size();

   for (int i = 0 ; i < plen; i ++ ) {
     WeightedItem wItem = ws.get(i);
     int id = wItem.getID();
     System.out.println("Item " + i + " := " + id + ", WEIGHT:=" + wItem.getWeight() + ", Prob:=" + prob(i));
   }
 }

 // return measured probability of item
 public double prob(int item) {
   return pos[item]/nsam;
 }

 public double totalProb() {
   double dbl = 0;
   int plen = ws.size();

   for (int i = 0; i < plen; i++) {
     dbl += pos[i];
   }
   double res = dbl / nsam;
   return res;
 }
}

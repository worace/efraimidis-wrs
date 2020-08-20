package wrs;


/**
 * A Random Sample
 * 
 */

public class SampledSet {
	public SampledItem[] sample;

	public SampledSet(int size) {
		sample = new SampledItem[size];
	}

	public int length() {
		if (sample == null)
			return -1;
		else
			return sample.length;
	}

	public void clear() {
		 java.util.Arrays.fill(sample, null); 
	}
	
	public void printAll() {
		System.out.println("Sampled Set :");
		int slen = sample.length;

		for (int i = 0; i < slen; i++) {
			SampledItem sItem = (SampledItem) sample[i];
			System.out.println("Item " + i + " := " + sItem.wItem.getID()
					+ ", KEY:=" + sItem.key);
		}
	}

}

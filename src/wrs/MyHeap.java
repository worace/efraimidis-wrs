package wrs;

import java.util.Vector;


public class MyHeap <E> {
	Vector<E> vHeap; // Contains the items of the heap
	int last; // The last valid entry of the heap
	int size;

	public MyHeap(int parSize) {
		size = parSize;
		vHeap = new Vector<E>(size);
		vHeap.setSize(size);
		last = -1;
	}

	public E rootItem() {
		if (last >= 0) {
			return vHeap.firstElement();
		} else {
			return null;
		}
	}

	public int getLength() {
		return last + 1;
	}

	public void clear() {
		vHeap.clear();
		last = -1;
	}

	public void clearAndInit() {
		vHeap.clear();
		vHeap.setSize(size);
		last = -1;
	}
	
	public E addItem(E item) {
		last++;
		vHeap.setElementAt(item, last);

		int current = last;
		int parent;
		SampledItem cItem; // current
		SampledItem pItem; // parent
		boolean bStop = false;
		do {
			parent = (current - 1) / 2;
			if (parent >= current || parent < 0) {
				bStop = true;
				break;
			} else {
				cItem = (SampledItem) vHeap.get(current);
				pItem = (SampledItem) vHeap.get(parent);
				if (cItem.key < pItem.key) {
					swapItems(current, parent);
					current = parent;
				} else {
					bStop = true;
					break;
				}
			}
		} while (!bStop);

		return item;
	}

	void swapItems(int item1, int item2) {
		E ptmp;
		ptmp = vHeap.get(item1);
		vHeap.set(item1, vHeap.get(item2));
		vHeap.set(item2, ptmp);
	}

	private void percolateDown() {
		int current = 0;
		int left;
		int right;
		int min;
		SampledItem cItem; // current
		SampledItem lItem; // left
		SampledItem rItem; // right
		SampledItem mItem; // min
		boolean bStop = false;
		do {
			if (current <= last) {
				cItem = (SampledItem) vHeap.get(current);
			} else {
				// current (index) is out of the heap
				return;
			}
			left = 2 * current + 1;
			right = 2 * current + 2;
			if (left <= last) {
				lItem = (SampledItem) vHeap.get(left);
				if (right > last) {
					min = left;
					mItem = lItem;
				} else {
					rItem = (SampledItem) vHeap.get(right);
					if (lItem.key < rItem.key) {
						min = left;
						mItem = lItem;
					} else {
						min = right;
						mItem = rItem;
					}
				}
				// Compare current with min
				if (cItem.key > mItem.key) {
					swapItems(current, min);
					current = min;
				} else {
					bStop = true;
					break;
				}
			} else {
				bStop = true;
				break;
			}
		} while (!bStop);
	}

	public E replaceHead(E item) {
		if (last < 0) {
			// Heap Empty
			// throw exception
			item = null;
		} else {
			vHeap.set(0, item);
			percolateDown();
		}

		return item;
	}

	public E removeHead() {
		E item;
		if (last < 0) {
			// Heap Empty
			// throw exception
			item = null;
		} else if (last == 0) {
			item = vHeap.remove(0);
		} else {
			swapItems(0, last);
			item = vHeap.remove(last);
			last--;
			percolateDown();
		}
		return item;
	}

	public void printAll() {
		System.out.println("Heap Contents");
		for (int i = 0; i <= last; i++) {
			SampledItem rItem = (SampledItem) vHeap.get(i);
			System.out.println("Item:" + i + ", id:" + rItem.wItem.getID()
					+ ", key:" + rItem.key);
		}
	}

	public E peek(int item) {
		return vHeap.get(item);
	}
}

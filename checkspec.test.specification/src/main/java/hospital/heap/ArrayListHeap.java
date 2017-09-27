package hospital.heap;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class ArrayListHeap<T extends Comparable<T>> extends AbstractBinaryHeap<T> {

	private ArrayList<T> nodes = new ArrayList<T>(50);

	@Override
	public int getSize() {
		return 0;
		// implementation
	}

	@Override
	public boolean isEmpty() {
		return false;
		// implementation
	}

	@Override
	protected boolean isHeap(int parentIndex, int childIndex) {
		return false;
		// implementation
	}

	@Override
	protected void swapNodes(int parentIndex, int childIndex) {
		// implementation
	}

	@Override
	public void push(T element) {
		// implementation
	}

	@Override
	public T top() {
		return null;
		// implementation
	}

	@Override
	public T pop() {
		return null;
		// implementation
	}
}

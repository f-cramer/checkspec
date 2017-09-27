package hospital.heap;

public abstract class AbstractBinaryHeap<T extends Comparable<T>> {

	public abstract int getSize();

	public abstract boolean isEmpty();

	protected abstract boolean isHeap(int parentIndex, int childIndex);

	protected abstract void swapNodes(int parentIndex, int childIndex);

	public abstract void push(T element);

	public abstract T top();

	public abstract T pop();

	protected void heapifyUp(int index) {
		// implementation
	}

	protected void heapifyDown(int index) {
		// implementation
	}

	protected int getLeftChildIndex(int parentIndex) {
		return 0;
		// implementation
	}

	protected int getRightChildIndex(int parentIndex) {
		return 0;
		// implementation
	}

	protected int getParentIndex(int childIndex) {
		return 0;
		// implementation
	}
}

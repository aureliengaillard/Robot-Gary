package programmes;

public class Semaphore {
	private int value;

	public Semaphore(int init) {
		if (init < 0)
			init = 0;
		value = init;
	}

	public synchronized void acquire() {
		while (value == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		value--;
	}

	public synchronized void release() {
		value++;
		notify();
	}
}
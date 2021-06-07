import java.util.concurrent.Semaphore;

public class Floor {
	int num;
	Semaphore sem;
	
	public Floor(int _num) {
		num = _num;
		sem = new Semaphore(0, false);
	}
	
	public void waitForElevator() {
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void elevatorReached() {
		sem.release();
	}
}

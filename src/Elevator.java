import java.util.concurrent.Semaphore;

public class Elevator extends Thread {
	enum ElevatorState {
		UP,
		DOWN;
	}
	
	int[] dest;
	int[] calls;
	Floor[] floors;
	Semaphore mutex;
	ElevatorState state = ElevatorState.UP;
	int currentFloor = 0;
	int floorsNum;
	
	public Elevator(Floor[] _f, Semaphore mu) {
		this.floorsNum = _f.length;
		this.dest = new int[floorsNum];
		this.calls = new int[floorsNum];
		this.floors = _f;
		this.mutex = mu;		
	}
	
	public void goTo(int floor) {
		dest[floor]++;
	}
	
	public void arrivedTo(int floor) {
		dest[floor]--;
	}
	
	public boolean hasDestAt(int floor) {
		return (dest[floor] > 0 ? true : false);
	}
	
	public void callFrom(int floor) {
		calls[floor]++;
	}
	
	public void removeCallFrom(int floor) {
		calls[floor]--;
	}
	
	public boolean hasCallsAt(int floor) {
		return (calls[floor] > 0 ? true : false);
	}

	public void openDoors() {
		System.out.println("[Ding !] Elevator doors opens at floor " + currentFloor);
	}
	
	public void closeDoors() {
		System.out.println("[Ding !] Elevator doors closes at floor " + currentFloor);
	}
	
	public void busy(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
    
    public void semWait() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public void semSignal() {
		mutex.release();
    }
	
	public void run() {
		while(!Thread.interrupted()) {
			if(state == ElevatorState.UP) {
				for (int i = currentFloor; i < floorsNum; i++) {
					if(hasDestAt(i) || hasCallsAt(i)) {
						busy(20);
						System.out.println("Elevator moving up to floor " + floors[i].num);

						busy(Math.abs(i - currentFloor) * floorsNum);
						
						currentFloor = i;
						openDoors();
						
						while(hasDestAt(i)){
							semWait();
							arrivedTo(currentFloor);
							semSignal();
							floors[currentFloor].elevatorReached();
						}

						busy(200);
						
						while(hasCallsAt(currentFloor)){
							removeCallFrom(currentFloor);
							floors[currentFloor].elevatorReached();
						}
						busy(20);
						
						closeDoors();

					}
				}
				state = ElevatorState.DOWN;
			}
			if (state == ElevatorState.DOWN) {
				for (int i = currentFloor-1; i >= 0; i--) {
					if(hasDestAt(i) || hasCallsAt(i)) {
						busy(20);
						System.out.println("Elevator moving down to floor " + floors[i].num);

						busy(Math.abs(i - currentFloor) * floorsNum);
						
						currentFloor = i;
						openDoors();
						
						while(hasDestAt(i)){
							semWait();
							arrivedTo(currentFloor);
							semSignal();
							floors[currentFloor].elevatorReached();
						}
						
						busy(50);
						
						while(hasCallsAt(currentFloor)){
							removeCallFrom(currentFloor);
							floors[currentFloor].elevatorReached();
						}
						busy(20);
						
						closeDoors();

					}
				}
				state = ElevatorState.UP;
			}
		}
	}
	
}

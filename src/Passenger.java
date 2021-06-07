import java.util.Random;
import java.util.concurrent.Semaphore;

public class Passenger extends Thread {
	enum PassengerState {
		INSIDE_ELEVATOR,
		OUTSIDE_ELEVATOR;
	}
	
	int id;
	PassengerState state;
	Elevator elevator;
	Floor[] floors;
	int currentFloor;
	int destFloor = 0;
	Semaphore mutex;
	
	
	public Passenger(int _id, Elevator _e, Floor[] _f, Semaphore mu) {
		this.id = _id;
		this.floors = _f;
		this.elevator = _e;
		this.mutex = mu;
		this.currentFloor = 0;
		this.state = PassengerState.OUTSIDE_ELEVATOR;
	}
	
    public int randomFloor() {
        Random rand = new Random();
        int floor = rand.nextInt(floors.length);
        return floor;
    }
    
    public void chooseFloor() {
		while(destFloor == currentFloor) {
			destFloor = randomFloor();
		}
    }
    
    public void busy() {
        try {
        	Random rand = new Random();
			Thread.sleep(floors.length * rand.nextInt(500));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
			semWait();
			System.out.println("Passenger " + id + " calls elevator from floor " + currentFloor);
			elevator.callFrom(currentFloor);
			semSignal();
		
			floors[currentFloor].waitForElevator();
			
			System.out.println("Passenger "+ id + " enters the elevator");
			state = PassengerState.INSIDE_ELEVATOR;
			
			semWait();
			// Passenger selects a random floor
			chooseFloor();
			// Passenger pushes a floor button
			elevator.goTo(destFloor);
			semSignal();
			
			System.out.println("Passenger "+ id + " requests to go to floor "+ destFloor);

			floors[destFloor].waitForElevator();
			
			System.out.println("Passenger "+ id + " leaves the eleavator");
			state = PassengerState.OUTSIDE_ELEVATOR;
			currentFloor = destFloor;
			busy();
			
		}
	}
}

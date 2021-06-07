import java.util.Random;
import java.util.concurrent.Semaphore;

public class Main {
    
    public static void busy() {
        try {
        	Random rand = new Random();
			Thread.sleep(rand.nextInt(500));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
	
	public static void main(String argv[]) {

		int floorsNum = 10;
		Floor[] floors = new Floor[floorsNum];
		Semaphore mutex = new Semaphore(1, false);
		Passenger passengers[] = new Passenger[5];
		
		for(int i = 0; i < floorsNum; i++) {
			floors[i] = new Floor(i);
		}
		Elevator elevator = new Elevator(floors, mutex);
		
	    for (int i = 0; i < 5; i++) {
	    	passengers[i] = new Passenger(i, elevator, floors, mutex);
	    }
		
		elevator.start();

        
        // other passengers arrive
	    for (int i = 0; i < 5; i++) {
	    	passengers[i].start();
	    	busy();
	    }
	}
	
}

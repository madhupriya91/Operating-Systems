import java.util.Random;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * 
 * 
 * This PostOffice class defines all the semaphores and the queue. 
 * It also defines the function for generating random task
 * @author Madhupriya
 * 
 */

public class PostOffice {

	int maxCustomers = 50;
	int cusSerialNumber = 0;
	Queue<Integer> cusQueue = new LinkedList<Integer>();
	int mapWorkerCustomer[] = new int[maxCustomers];

	
	Semaphore semMaxCustomers = new Semaphore(10);
	Semaphore semWorkers = new Semaphore(3);
	Semaphore semCustReady = new Semaphore(0);
	Semaphore mutex1 = new Semaphore(1);
	Semaphore mutex2 = new Semaphore(1);
	Semaphore semGiveTask[] = new Semaphore[maxCustomers];
	Semaphore semFinishedServe[] = new Semaphore[maxCustomers];
	Semaphore semCustTurn[] = new Semaphore[maxCustomers];
	

	
	
 

	
	/*Function to generate task randomly */
	public int generateTask() {

		Random rand = new Random();

		return (rand.nextInt(3) + 1);
	}

	
}



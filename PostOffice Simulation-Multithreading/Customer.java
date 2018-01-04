import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * 
 * 
 * This Customer class defines the functionalities needed by the customer. Like
 * enqueue(), enter(),assigning task to work and exit().
 * 
 * @author Madhupriya
 * 
 */

public class Customer implements Runnable {
	int number;
	int task;
	/*Semaphore semMaxCustomers;
	Semaphore semWorkers;
	Semaphore semCustReady;
	Semaphore mutex1;*/
	static int count;
	int serialNumber;
	PostOffice po;
	/*Semaphore semFinServe[];
	Semaphore semCustTurn[];
	Semaphore semGiveTask[];*/

	public Customer(int index, int task, PostOffice po) {

		
		this.number = index;
		this.task = task;
		
		this.po = po;
	}

	/* Function to enqueue customer number or a ask */
	
	public void enqueue(int num) {

		po.cusQueue.add(num);
		
	}
	public void run() {
		try {

			po.semMaxCustomers.acquire();

			this.enter();
			po.semWorkers.acquire();                              //Getting one postal worker
			po.mutex1.acquire();                                 //Mutual exclusion for the queue
			enqueue(number);
			enqueue(task);
			po.mutex1.release();
			po.semCustReady.release();                              //Letting the worker know that the customer is ready
			po.semCustTurn[number].acquire();                      // Waiting for turn to be served
			System.out.println("Customer " + number + " asks postal worker "
					+ getMap(number) + " " + taskAssign());
			po.semGiveTask[number].release();                        //Signal to start the task
			po.semFinishedServe[number].acquire();                        //Waiting for the task completion
			System.out.println("Customer " + number + " finished "
					+ taskFinished());
			custExit();
			po.semMaxCustomers.release();
		} catch (InterruptedException ex) {

		}
	}

	private String taskFinished() {
		
		String taskName = new String();
		switch (task) {
		case 1:
			taskName = "buying stamps";

			break;
		case 2:
			taskName = "mailing a letter";

			break;
		case 3:
			taskName = "mailing a package";

			break;

		}
		return (taskName);

	}

	public String taskAssign() {
		String taskName = new String();
		switch (task) {
		case 1:
			taskName = "to get him some stamps";

			break;
		case 2:
			taskName = "for mailing a letter";

			break;
		case 3:
			taskName = "for mailing a package";

			break;

		}
		return (taskName);
	}

	public void enter() {

		System.out.println("Customer " + number + " enters post office");
	}

	

	public int getMap(int index) {
		return po.mapWorkerCustomer[index];
	}
	public void custExit() {

		System.out.println("Customer " + number + " leaves the post office");
	}

}

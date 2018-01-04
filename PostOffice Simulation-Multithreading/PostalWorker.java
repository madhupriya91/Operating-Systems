import java.util.concurrent.Semaphore;

/**
 * 
 * 
 * This PostalWorker class defines the functionalities needed by a Postal worker. Like
 * dequeue() and serve().
 * 
 * @author Madhupriya
 * 
 */

public class PostalWorker implements Runnable {

	int empNumber;
	int custNum, custTask;
	
	PostOffice po;

	public PostalWorker(int num, PostOffice po) {
		this.empNumber = num;
	
		this.po=po;
	}

	/* Function to dequeue*/
	
	public int dequeue() {
		int queueFront;
		return (queueFront = po.cusQueue.poll());
	}

	public void run() {
		// System.out.println("Created worker "+empNumber);
		while (true) {
			try {
				po.semCustReady.acquire();                  //Waiting for a customer
				po.mutex1.acquire();                        //Mutual exclusion for the queue while dequeuing
				custNum = dequeue();
				custTask = dequeue();
				po.mutex1.release();
				setMap(custNum, empNumber);           //Populating the customer array with the postal worker number
				System.out.println("Postal worker " + empNumber
						+ " serving customer " + custNum);
				po.semCustTurn[custNum].release();              //Letting the customer know that it is now his/her turn
				po.semGiveTask[custNum].acquire();              //Waiting for the customer to give a task
				serve();
				System.out.println("Postal worker " + empNumber
						+ " finished serving customer " + custNum);
				po.semFinishedServe[custNum].release();               //Letting the customer that the task is done
				po.semWorkers.release();

			} catch (InterruptedException ex) {

			}

		}
	}

	public void setMap(int index, int value) {
		po.mapWorkerCustomer[index] = value;

	}
	public void serve() {

		try {

			switch (custTask) {
			case 1:
				Thread.sleep(1000);

				break;
			case 2:
				Thread.sleep(1500);

				break;
			case 3:
				po.mutex2.acquire();             //Mutual exclusion for the scales
				System.out.println("Scales in use by postal worker "
						+ empNumber);
				Thread.sleep(2000);
				System.out.println("Scales released by postal worker "
						+ empNumber);
				po.mutex2.release();
				break;

			}
		} catch (InterruptedException ex) {

		}
	}

	

}

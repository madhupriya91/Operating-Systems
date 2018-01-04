import java.util.concurrent.Semaphore;

/**
 * 
 * 
 * This class creates all the threads and joins the customer threads as well.
 * 
 * @author Madhupriya
 * 
 */

public class Project2 {

	
	public static void main(String args[]) {

		PostOffice po = new PostOffice();
		
		/*for (int i = 0; i < po.maxCustomers; i++) {
			po.semFinishedServe[i] = new Semaphore(0);
		}*/
		int task;
		// int count;
		for (int a = 0; a < po.maxCustomers; a++) {
			

			po.semCustTurn[a] = new Semaphore(0);
				po.semGiveTask[a] = new Semaphore(0);
				po.semFinishedServe[a] = new Semaphore(0);
			}
		/*Creation of postal worker threads*/
		Thread tWorkers[] = new Thread[3];
		for (int w = 0; w < 3; w++) {
			tWorkers[w] = new Thread(new PostalWorker(w, po));
			System.out.println("Postal worker " + w + " created");
			tWorkers[w].start();

		}

		/*Creation of customer threads*/
		Thread tCustomer[] = new Thread[po.maxCustomers];
		for (int c = 0; c < po.maxCustomers; c++) {
			task = po.generateTask();
			tCustomer[c] = new Thread(new Customer(c, task, po));
			System.out.println("Customer " + c + " created.");
			tCustomer[c].start();

		}

		 /*Joining the customer threads */
		for (int cus = 0; cus < po.maxCustomers; cus++) {
			try {
				// tObj.join();

				tCustomer[cus].join();
				// System.out.println( tCustomer[cus].isAlive());
				System.out.println("Customer " + cus + " joined...");
			}

			catch (InterruptedException e) {
			}
		}
	
		System.exit(0);
	}

	
}

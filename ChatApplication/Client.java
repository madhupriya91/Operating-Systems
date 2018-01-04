import java.io.*;
//import java.io.PrintWriter;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.ArrayList;

public class Client {

	Socket cSocket;

	// int port;

	public Client(String mName, int port) {
		try {
			cSocket = new Socket(mName, port);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String args[]) {

		ArrayList messages = new ArrayList();
		Boolean isExit = false;
		String message[] = null;
		String line;
		Date todayDate;
		String sendingTime;
		Client myClient = new Client(args[0], Integer.parseInt(args[1]));
		try {
			PrintWriter out = new PrintWriter(
					myClient.cSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					myClient.cSocket.getInputStream()));
			System.out.println("Connecting to " + args[0] + ":" + args[1]);
			System.out.println("Enter your name : ");
			Scanner word = new Scanner(System.in);
			String input = word.nextLine();
			// System.out.println("you typed " + input);
			out.println(input);
			//System.out.println(in.readLine());
			if(!(in.readLine().equals("Success"))){
				
				System.out.println("Already connected ");
				isExit=true;
			}
			while (!isExit) {
				System.out.println("Enter your choice :\n "
						+ "1. See all known users \n"
						+ "2. See all connected users \n "
						+ "3. Send message to a particular user\n "
						+ "4. Send message to all currently connected users\n "
						+ "5. Send message to all known users\n "
						+ "6. Get my messages\n " + "7. Exit\n");
				// int choice=word.nextInt();
				input = word.nextLine();
				out.println(input);
				switch (input) {
				case "1":

					line = in.readLine();
					// System.out.println("ORIGINAL MESSAGE FROM SERVER:  "+line);
					message = line.split("~", 50);
					// message=line.split("~");
					// String line1=message[0];
					// String line2=message[1];
					// System.out.println("Name :"+line1+"\nIs connected :"+line2);
					int knownCount = 1;
					// while(knownCount<message.length){
					System.out.println("Known users :");
					for (String name : message) {
						if (knownCount < message.length) {
							System.out.println(knownCount + ". " + name);
							knownCount++;
						}
					}
					// }
					break;
				case "2":

					line = in.readLine();

					message = line.split("~", 50);

					int connectedCount = 1;
					System.out.println("Currently connected users :");
					for (String name : message) {
						if (connectedCount < message.length) {
							System.out.println(connectedCount + ". " + name);
							connectedCount++;
						}
					}
					break;
				case "3":
					StringBuffer unicastMessage = new StringBuffer();
					System.out.println("Enter the recepient's name :");
					input = word.nextLine();
					String recName = input;
					unicastMessage.append(input + "~");
					System.out.println("Enter your message :");
					input = word.nextLine();
					unicastMessage.append(input + "~");
					todayDate = new Date();
					SimpleDateFormat dateAndTime = new SimpleDateFormat(
							" MM/dd/yyyy hh:mm a");
					sendingTime = dateAndTime.format(todayDate);
					unicastMessage.append(sendingTime);
					out.println(unicastMessage);
					if ((line = in.readLine()).equals("Success"))
						System.out.println("Message posted to " + recName);
					else
						System.out.println("Delivery Failed :(");
					break;

				case "4":
					StringBuffer multicastMessage = new StringBuffer();
					// System.out.println("Enter the recepient's name :");
					// input=word.nextLine();
					// String recName=input;
					// unicastMessage.append(input+"~");
					System.out.println("Enter your message :");
					input = word.nextLine();
					multicastMessage.append(input + "~");
					todayDate = new Date();
					SimpleDateFormat dateAndTimeMulticast = new SimpleDateFormat(
							" MM/dd/yyyy hh:mm a");
					sendingTime = dateAndTimeMulticast.format(todayDate);
					multicastMessage.append(sendingTime);
					out.println(multicastMessage);
					if ((line = in.readLine()).equals("Success"))
						System.out
								.println("Message sent to all the connected users ");
					else
						System.out.println("Delivery Failed :(");

					break;

				case "5":
					StringBuffer broadcastMessage = new StringBuffer();
					// System.out.println("Enter the recepient's name :");
					// input=word.nextLine();
					// String recName=input;
					// unicastMessage.append(input+"~");
					System.out.println("Enter your message :");
					input = word.nextLine();
					broadcastMessage.append(input + "~");
					todayDate = new Date();
					SimpleDateFormat dateAndTimeBroadcast = new SimpleDateFormat(
							" MM/dd/yyyy hh:mm a");
					sendingTime = dateAndTimeBroadcast.format(todayDate);
					broadcastMessage.append(sendingTime);
					out.println(broadcastMessage);
					if ((line = in.readLine()).equals("Success"))
						System.out.println("Message sent to all the users ");
					else
						System.out.println("Delivery Failed :(");

					break;

				case "6":
					line = in.readLine();
					message = line.split("~", 50);
					int i = 0,
					j = 1;
					int k = 2;
					int count = message.length;
					System.out.println("Your messages :");
					while (k < count) {

						System.out.println("From : " + message[i] + " , "
								+ message[k] + ", " + message[j]);
						i += 3;
						j += 3;
						k += 3;

					}
					break;
				case "7":
					isExit = true;
					break;
				default:
					System.out.println("Please enter a valid choice ..");
					break;
				}

				// if (input.equals("quit"))
				// isExit = true;

			}
		} catch (IOException e) {
			System.out.println("Read failed");
			System.exit(1);
		}

	}
}

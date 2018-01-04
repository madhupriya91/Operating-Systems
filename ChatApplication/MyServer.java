import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.text.SimpleDateFormat;
import java.util.Date;

class ClientThread implements Runnable {
	Socket sClient;
	MyServer ser;

	ClientThread(Socket client, MyServer ser) {

		sClient = client;
		this.ser = ser;
	}

	public void run() {
		String name;
		String line;
		int clientChoice;
		BufferedReader input = null;
		PrintWriter output = null;
		Boolean isExit = false;
		try {
			input = new BufferedReader(new InputStreamReader(
					sClient.getInputStream()));

			output = new PrintWriter(sClient.getOutputStream(), true);

		} catch (IOException e) {
			System.out.println("in or out failed");
			System.exit(1);
		}
		try {
			line = input.readLine();
			name = line;
			if (!ser.findClient(line)) {
				try {

					ser.freshKnownUser.acquire(); // to make a user wait if it
													// is reading all the known
													// clients
					ser.newConnectingUser.acquire(); // to make a user wait if
														// he is reading all the
														// connected users
					ClientDetails newClient = new ClientDetails(line);
					newClient.setIsConnected(1);
					ser.addClient(line, newClient);
					ser.newConnectingUser.release();
					ser.freshKnownUser.release();
					ser.todayDate = new Date();
					SimpleDateFormat dateAndTime = new SimpleDateFormat(
							" MM/dd/yyyy hh:mm:ss a ");
					ser.currentTime = dateAndTime.format(ser.todayDate);

					ser.showAction(ser.currentTime
							+ ", Connection by new user " + line);
					output.println("Success");
				} catch (InterruptedException ex) {

				}
			} else {
				try {
					ClientDetails client = ser.getClient(line);
					if (client.isConnected != 1) {
						ser.reconnectingUser.acquire(); // to make a user wait
														// if he is reading all
														// the connected users
						ser.reconnectClient(client);
						ser.reconnectingUser.release();
						ser.todayDate = new Date();
						SimpleDateFormat dateAndTime = new SimpleDateFormat(
								" MM/dd/yyyy hh:mm:ss a ");
						ser.currentTime = dateAndTime.format(ser.todayDate);
						ser.showAction(ser.currentTime
								+ ", Connection by known user " + line);
						output.println("Success");
					} else {
						//System.out
								//.println("Already " + line + " is connected.");
						output.println("X");
						System.out.println("Duplicate user entry with same name :"+name);
						isExit=true;
						//sClient.close();
						
					}
				} catch (InterruptedException e) {

				}
			}
			while (!isExit) {

				line = input.readLine();
				switch (line) {

				case "1":
					ser.todayDate = new Date();
					SimpleDateFormat dateAndTime = new SimpleDateFormat(
							" MM/dd/yyyy hh:mm:ss a ");
					ser.currentTime = dateAndTime.format(ser.todayDate);
					ser.showKnownUsers(output);
					ser.showAction(ser.currentTime + ", " + name
							+ " displays all known users.");

					break;
				case "2":
					ser.todayDate = new Date();
					SimpleDateFormat dateAndTime1 = new SimpleDateFormat(
							" MM/dd/yyyy hh:mm:ss a ");
					ser.currentTime = dateAndTime1.format(ser.todayDate);
					ser.showConnectedUsers(output);
					ser.showAction(ser.currentTime + ", " + name
							+ " displays all connected users.");

					break;

				case "7":
					try {
						ser.todayDate = new Date();
						SimpleDateFormat dateAndTime2 = new SimpleDateFormat(
								" MM/dd/yyyy hh:mm:ss a ");
						ser.currentTime = dateAndTime2.format(ser.todayDate);

						ser.exitingUser.acquire(); // to make a user wait if it
													// is reading all the
													// connected users
						ClientDetails client = ser.getClient(name);

						ser.disconnectClient(client);
						isExit = true;
						ser.exitingUser.release();
						ser.showAction(ser.currentTime + ", " + name
								+ " exits.");
					} catch (InterruptedException e) {

					}
					break;
				case "3":
					String recipientName;
					String senderName;
					String textMessage;
					String time;
					senderName = name;
					String[] textDetails = (input.readLine()).split("~", 3);
					recipientName = textDetails[0];
					textMessage = textDetails[1];
					time = textDetails[2];
					ser.todayDate = new Date();
					SimpleDateFormat dateAndTime3 = new SimpleDateFormat(
							" MM/dd/yyyy hh:mm:ss a ");
					ser.currentTime = dateAndTime3.format(ser.todayDate);
					if (true == ser.deliverMessage(recipientName, textMessage,
							senderName, time))
						output.println("Success");
					ser.showAction(ser.currentTime + ", " + name
							+ " sends message to " + recipientName);
					break;
				case "4":
					String multicastSenderName = name;
					String[] multicastTextDetails = (input.readLine()).split(
							"~", 2);

					String multicastText = multicastTextDetails[0];
					time = multicastTextDetails[1];
					ser.todayDate = new Date();
					SimpleDateFormat dateAndTime4 = new SimpleDateFormat(
							" MM/dd/yyyy hh:mm:ss a ");
					ser.currentTime = dateAndTime4.format(ser.todayDate);
					if (true == ser.multicastMessage(multicastText,
							multicastSenderName, time))
						output.println("Success");
					ser.showAction(ser.currentTime + ", " + name
							+ " sends message to all connected users");

					break;
				case "5":
					String broadcastSenderName = name;
					String broadcastTextDetails[] = (input.readLine()).split(
							"~", 2);
					String broadcastText = broadcastTextDetails[0];
					time = broadcastTextDetails[1];
					ser.todayDate = new Date();
					SimpleDateFormat dateAndTime5 = new SimpleDateFormat(
							" MM/dd/yyyy hh:mm:ss a ");
					ser.currentTime = dateAndTime5.format(ser.todayDate);
					if (true == ser.broadcastMessage(broadcastText,
							broadcastSenderName, time))
						output.println("Success");
					ser.showAction(ser.currentTime + ", " + name
							+ " sends message to all known users");

					break;
				case "6":
					ser.todayDate = new Date();
					SimpleDateFormat dateAndTime6 = new SimpleDateFormat(
							" MM/dd/yyyy hh:mm:ss a ");
					ser.currentTime = dateAndTime6.format(ser.todayDate);
					ser.getMyMessages(name, output);
					ser.showAction(ser.currentTime + ", " + name
							+ " gets messages.");
					break;
				}

				

			}
		} catch (IOException e) {

			System.out.println("Read failed");
			System.exit(1);
		}

		try {

			sClient.close();

		} catch (IOException e) {
			System.out.println("Close failed");
			System.exit(1);
		}

	}

}

class MyMessage {
	String message;
	String sender;
	String time;

	public MyMessage(String message, String sender, String time) {
		this.message = message;
		this.sender = sender;
		this.time = time;

	}

	public String toString() {

		return message;
	}

}

class ClientDetails {
	int isConnected = 0; // multiple read, single write
	int isKnown = 0;
	Semaphore semReadMessage = new Semaphore(1);
	Semaphore semWriteMessage = new Semaphore(1);

	ArrayList<MyMessage> myMessages = new ArrayList<>(); // multiple writes,
															// single read

	public void addMessage(String textMessage, String sender, String time) {

		int len = myMessages.size();
		
		myMessages.add(new MyMessage(textMessage, sender, time));

		

	}

	public StringBuffer getMessages() {
		// System.out.println("IN CLIENTDETAILS.GETMESSAGES");
		StringBuffer allMessages = new StringBuffer();
		int len;

		for (int i = 0; i < myMessages.size(); i++) {

			allMessages.append(myMessages.get(i).sender);
			allMessages.append("~");
			allMessages.append(myMessages.get(i).message);
			allMessages.append("~");
			allMessages.append(myMessages.get(i).time);
			allMessages.append("~");
		}

		return allMessages;
	}

	public int getIsKnown() {
		return isKnown;
	}

	public void setIsKnown(int isKnown) {
		this.isKnown = isKnown;
	}

	String name;

	public ClientDetails(String line) {

		setName(line);
		// setIsConnected(1);
		setIsKnown(1);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIsConnected() {
		return isConnected;
	}

	public synchronized void setIsConnected(int value) {
		this.isConnected = value;
	}

	public void removeMessages() {
		int i = myMessages.size() - 1;
		// for (int i = 0; i <= myMessages.size(); i++) {
		while (myMessages.size() != 0) {
			// System.out.println(myMessages.get(i).message);
			myMessages.remove(i);
			i--;

		}
		// System.out.println("Messages removed, now size is  "
		// + myMessages.size());

	}

}

public class MyServer {

	ServerSocket server = null;
	Semaphore semClientsReadingConnected = new Semaphore(1);
	Semaphore semClientsReadingKnown = new Semaphore(1);
	int clientsReadingConnected = 0;
	int clientsReadingKnown = 0;
	Semaphore reconnectingUser = new Semaphore(1);
	Semaphore exitingUser = new Semaphore(1);
	Semaphore newConnectingUser = new Semaphore(1);
	Semaphore freshKnownUser = new Semaphore(1);
	Map<String, ClientDetails> clients = new TreeMap<>();  //Used a tree map to store all the client details
	StringBuffer message = null;
	Date todayDate;
	String currentTime;

	// ArrayList clientList=new ArrayList();
	public void getSocketClient(int port) {
		try {
			server = new ServerSocket(port);

		} catch (IOException e) {
			System.out.println("Error creating a socket");
		}

		while (true) {
			ClientThread c;
			try {
				c = new ClientThread(server.accept(), this);
				Thread t = new Thread(c);
				t.start();

			} catch (IOException e) {
				System.out.println("Error in accept");
				System.exit(1);
			}

		}
	}

	public void showAction(String action) {

		System.out.println(action + "\n");
	}

	public boolean multicastMessage(String multicastText, String senderName,
			String time) {
		ClientDetails recipientClient;
		try {
			int i = 1;
			for (Entry<String, ClientDetails> client : clients.entrySet()) {
				recipientClient = client.getValue();
				if ((recipientClient.getIsConnected() == 1)
						&& (!(recipientClient.getName().equals(senderName)))) {
					recipientClient.semWriteMessage.acquire(); // No one else's
																// message
																// should be
																// written at the same time
					recipientClient.addMessage(multicastText, senderName, time);
					recipientClient.semWriteMessage.release();

				}
			}
		} catch (InterruptedException ex) {

		}

		return true;

	}

	public boolean broadcastMessage(String broadcastText, String senderName,
			String time) {
		ClientDetails recipientClient;
		try {
			int i = 1;
			for (Entry<String, ClientDetails> client : clients.entrySet()) {
				recipientClient = client.getValue();
				if ((recipientClient.getIsKnown() == 1)
						&& (!(recipientClient.getName().equals(senderName)))) {
					recipientClient.semWriteMessage.acquire();
					recipientClient.addMessage(broadcastText, senderName, time);
					recipientClient.semWriteMessage.release();

				}
			}

		} catch (InterruptedException e) {

		}
		return true;

	}

	public void showConnectedUsers(PrintWriter output) {
		StringBuffer allConnectedUsers = new StringBuffer();
		ClientDetails clientObj;
		String name;
		int i = 1;
		try {
			semClientsReadingConnected.acquire();

			clientsReadingConnected++;
			if (clientsReadingConnected == 1) {
				reconnectingUser.acquire(); // If this is the first client who
											// is reading all connected user names, then no client is
											// allowed to connect or disconnect at this instant.
				exitingUser.acquire();
				newConnectingUser.acquire();

			}
			semClientsReadingConnected.release();
			for (Entry<String, ClientDetails> client : clients.entrySet()) {

				clientObj = client.getValue();

				if (clientObj.getIsConnected() == 1) {
					name = client.getKey();

					allConnectedUsers.append(name + "~");

				}

			}

			semClientsReadingConnected.acquire();
			clientsReadingConnected--;
			if (clientsReadingConnected == 0) {
				reconnectingUser.release();
				exitingUser.release();
				newConnectingUser.release();
			}
			semClientsReadingConnected.release();

			output.println(allConnectedUsers);
		} catch (InterruptedException ex) {

		}
	}

	public void showKnownUsers(PrintWriter output) {
		StringBuffer allKnownUsers = new StringBuffer();
		ClientDetails clientObj;
		String name;
		int i = 1;
		try {
			semClientsReadingKnown.acquire();

			clientsReadingKnown++;
			if (clientsReadingKnown == 1) {
				freshKnownUser.acquire(); // If a new client enters at the same time , he should
											// wait

			}
			semClientsReadingKnown.release();
			for (Entry<String, ClientDetails> client : clients.entrySet()) {

				clientObj = client.getValue();
				if (clientObj.getIsKnown() == 1) {
					name = client.getKey();

					allKnownUsers.append(name + "~");

				}

			}

			semClientsReadingKnown.acquire();
			clientsReadingKnown--;
			if (clientsReadingKnown == 0) {

				freshKnownUser.release();

			}
			semClientsReadingKnown.release();
			output.println(allKnownUsers);
		} catch (InterruptedException e) {

		}

	}

	public void getMyMessages(String name, PrintWriter output) {
		// System.out.println("----IN GETMYMESSAGES ---");
		ClientDetails client = getClient(name);
		try {

			client.semWriteMessage.acquire(); // No one should be able to send
												// message to this client at
												// this time
			StringBuffer receivedMessages = client.getMessages();
			client.removeMessages();

			output.println(receivedMessages);
			client.semWriteMessage.release();
		} catch (InterruptedException ex) {

		}
	}

	public boolean deliverMessage(String recipientName, String textMessage,
			String senderClient, String time) {
		try {
			ClientDetails recipientClient;
			if (findClient(recipientName) == true)

				recipientClient = getClient(recipientName);

			else {
				recipientClient = new ClientDetails(recipientName);
				addClient(recipientName, recipientClient);
				freshKnownUser.acquire(); // If anyone is reading all known
											// clients, he should wait
				recipientClient.setIsKnown(1);
				freshKnownUser.release();
			}
			recipientClient.semWriteMessage.acquire();
			recipientClient.addMessage(textMessage, senderClient, time);
			recipientClient.semWriteMessage.release();
		} catch (InterruptedException e) {

		}
		return true;

	}

	public void disconnectClient(ClientDetails client) {
		// If any client is seeing all the connected users at this time, this
		// write should wait
		client.setIsConnected(0);

	}

	public void reconnectClient(ClientDetails client) {

		client.setIsConnected(1);

	}

	public ClientDetails getClient(String line) {
		return clients.get(line);
	}

	public boolean findClient(String line) {

		if (clients.containsKey(line))
			return true;

		return false;
	}

	public synchronized void addClient(String name, ClientDetails clientObj) {  // Made it synchronised so that simultaneous access to the tree map is avoided.

		clients.put(name, clientObj);

	}

	public void display(PrintWriter output) {
		message = new StringBuffer();

		for (Entry<String, ClientDetails> client : clients.entrySet()) {

			String name = client.getKey();
			ClientDetails clientObj = client.getValue();
			message.append(name + "~" + clientObj.getIsConnected() + "~");

		}
		output.println(message);

	}

	public void finalize() {
		try {
			server.close();

		} catch (IOException e) {
			System.out.println("Could not close socket");
			System.exit(-1);

		}
	}
	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Port number required as an argument ");
			System.exit(1);

		}
		InetAddress ip;
		try {

			ip = InetAddress.getLocalHost();

			int port = Integer.parseInt(args[0]);
			MyServer myServer = new MyServer();
			System.out.println("Server is running on " + ip.getHostName() + ":"
					+ port);
			myServer.getSocketClient(port);
		} catch (UnknownHostException e) {

			e.printStackTrace();

		}
	}
}

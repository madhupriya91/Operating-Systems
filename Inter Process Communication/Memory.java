import java.io.*;
import java.lang.Runtime;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 
 * Memory class represents the Memory of the Computer System. It has two
 * functionalities a. Read b. Write to the Memory. Based on the signal received
 * from the CPU and the address value, it either reads or writes to the given
 * address. Memory is implemented as an array of 0 to 2000 where 0 to 999 is
 * User-Memory and 1000 to 1999 is System memory. User Memory is used to store
 * users data and system memory is used by the system for Stack, ISR etc.
 * 
 * @author Madhupriya
 * 
 */
public class Memory {

	int content[] = new int[2000];
	int notEmpty = 0;

	public int read(int address) {
		return content[address];

	}

	public void write(int address, int value) {
		content[address] = value;

	}

	public static void main(String args[]) throws IOException {
		// int arr[]=new int[2];
		String line;
		int ind = 0;
		Memory myMemory = new Memory();
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {

			fileInputStream = new FileInputStream(args[0]);
			inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			bufferedReader = new BufferedReader(inputStreamReader);
			Pattern regex = Pattern.compile("[.]");
			while ((line = bufferedReader.readLine()) != null) {
				String words[] = line.split(" ");
				Matcher matcher = regex.matcher(words[0]);
				if (matcher.find()) {                          //For entry like .1000
					StringBuilder sb = new StringBuilder();
					sb.append(words[0]);
					sb.deleteCharAt(0);
					String newIndex = sb.toString();
					int index = Integer.parseInt(newIndex);
					ind = index;

				} else {
					if (words[0].equals("")) {           //Skipping blanks
						continue;
					}
					myMemory.content[ind] = Integer.parseInt(words[0]);
					ind++;
				}
			}
			Scanner sc = new Scanner(System.in);

			int a = 8;
			int i = 0;

			String input = null;
			int z = 0;

			// System.out.println("In memory...");

			// if (sc.hasNext()){

			int value;
			int j = 0;

			int stackTop, mode;

			int location = 0;

			System.out.println("Ready");

			String str;
			while (sc.hasNext()) {
				str = sc.next();
				String[] params = str.split("-");
				if (params.length == 1) // Normal Read from Memory
				{
					location = Integer.parseInt(params[0]);
					
					System.out.println(myMemory.read(location));
				} else  
				{
					// Read/Write  to Memory for special instructions like JUMP, STACK specific instructions 
					int signal = Integer.parseInt(params[0]);
					switch (signal) {
					case 20:
						location = Integer.parseInt(params[1]);
						
						System.out.println(myMemory.read(location));
						break;
					case 21:
						location = Integer.parseInt(params[1]);
						
						System.out.println(myMemory.read(location));
						break;
					case 22:
						location = Integer.parseInt(params[1]);
						
						System.out.println(myMemory.read(location));
						break;
					case 7:
						int loc = Integer.parseInt(params[1]);
						
						myMemory.write(loc, Integer.parseInt(params[2]));
						if (sc.hasNext()) {
							str = sc.next();
							params = str.split("-");     

							location = Integer.parseInt(params[0]);
							
							System.out.println(myMemory.read(location));
						}
						break;
					case 27:

						stackTop = Integer.parseInt(params[1]);

						if (myMemory.read(stackTop) == 0) {
							// arr[stackTop] = Integer.parseInt(params[2]);
							myMemory.write(stackTop,
									Integer.parseInt(params[2]));
							myMemory.notEmpty = 1;
							System.out.println("5");
						} else {
							System.out.println("1");
						}

						break;
					case 28:
						stackTop = Integer.parseInt(params[1]);

						if (myMemory.read(stackTop) == 0
								&& myMemory.notEmpty != 1) {
							System.out.println("-2");           //Sending error signal if popping from empty stack
						} else {
							// System.out.println(arr[stackTop]);
							System.out.println(myMemory.read(stackTop));
							// arr[stackTop]=0;
							myMemory.write(stackTop, 0);
							if (stackTop == 1999 || stackTop == 999)
								myMemory.notEmpty = 0;

						}

					}

				}

			}

		} catch (Throwable t) {
			t.printStackTrace();

		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}
	}

	// }
}
import java.io.*;
import java.lang.Runtime;
import java.util.*;
import java.util.Scanner;

/**
 * 
 * 
 *         This CPU class does the functionality of the CPU where each
 *         functionality is defined as an individual function like
 *         copyFromX(),setAc() etc. It also creates the Memory process and gets
 *         the instructions to be executed from it using IO streams. Based on
 *         the instruction received, the CPU decodes and executes it. Registers
 *         are implemented as class variables like Accumulator AC, Program
 *         Counter PC, Stack Pointer SP, Instruction Register IR etc
 * @author Madhupriya
 * 
 */
public class CPU {
	int PC = 0, SP = 1000, IR, X = 1, Y, AC = 1, port, mode = 0, intEnable = 1,
			timerFlag = 0, intFlag = 0;
	int userStackLimit = 999;
	int kernelStackLimit = 1999;
	int dontCount = 0;
	int tempAC, tempX, tempY;

	/**
	 * set the value acV to the Accumulator
	 * 
	 * @param acV
	 *            - value to be set in Accumulator
	 */
	public void setAc(int acV) {
		this.AC = acV;
	}

	/**
	 * get the value from Accumulator
	 * 
	 * @return
	 */
	public int getAc() {
		return this.AC;
	}

	/**
	 * sets the ProgramCounter to the given value
	 * 
	 * @param acV
	 *            - value to be set in PC
	 */
	public void setPC(int acV) {
		this.PC = acV;
	}

	/**
	 * get the value from ProgramCounter
	 * 
	 * @return
	 */
	public int getPC() {
		return this.PC;
	}

	public int getPort() {
		return this.port;

	}

	public void setPort(int p) {
		// System.out.println("Set port called");
		this.port = p;
		// System.out.println("Port entered is :" + this.getPort());

	}

	public int getIR() {
		return this.IR;

	}

	public void setIR(int value) {
		this.IR = value;
	}

	public void setMode(int i) {
		mode = i;
		// if(i==1)//System.out.println("Switched to KERNEL mode");
	}

	public void get() {
		Random rand = new Random();

		AC = rand.nextInt(50) + 1;
		// System.out.println("AC value after random no. generation :" + AC);
	}

	/**
	 * Loading the value from the Memory to Accumulator
	 * @param sc
	 * @param pw
	 */
	public void loadValue(Scanner sc, PrintWriter pw) {
		// System.out.println("Case 1");
		++PC;
		// System.out.println("PC = " + ++PC);
		pw.print(PC + "\n");
		pw.flush();
		// .sleep(10);
		if (sc.hasNextInt()) {
			int v = sc.nextInt();
		

			loadAc(v);
		}

	}

	public void put(int portValue) {

		if (1 == portValue) {                             //Print accumulator
			System.out.print((int) AC);
		} else if (2 == portValue) {

			System.out.print((char) AC);
		}
	}

	public void putPort(Scanner sc, PrintWriter pw) {
		
		++PC;
	
		pw.print(PC + "\n");
		pw.flush();
		
		if (sc.hasNextInt()) {
			int v = sc.nextInt();
			
			setPort(v);
			put(getPort());
		}
	}

	public void copyToX() {

		X = AC;
	
	}

	public void copyToY() {

		Y = AC;
		
	}

	public void copyFromX() {
		AC = X;
	}

	public void copyFromY() {
		AC = Y;
	}

	public void addX() {
		
		AC += X;
	
	}

	public void addY() {
	
		AC += Y;
	
	}

	public void loadAc(int v) {
		this.AC = v;

	}

	public void incrementX() {
		this.X++;
	}

	public void decrementX() {
		X--;
	}

	public void jump(int addr) {
		
		if ((mode == 0) && (addr > userStackLimit)) {
			System.out
					.println("Jump : cannot jump to kernel address from user mode");
			PC++;
			return;
		}
		setPC(addr);

	}

	public void jumpAddr(Scanner sc, PrintWriter pw) {
		++PC;
		pw.print("20-" + PC + "\n");                       //Sending jump instruction with signal to memory
		pw.flush();
		if (sc.hasNextInt()) {
			int v = sc.nextInt();
			jump(v);

		

		}

	}

	public void jumpIfEqualAddr(Scanner sc, PrintWriter pw) {

		++PC;
		if (getAc() == 0) {

			pw.print("21-" + PC + "\n");
			pw.flush();
			
			if (sc.hasNextInt()) {
				int v = sc.nextInt();
				jump(v);
			}
		} else
			++PC;

	}

	public void jumpIfNotEqual(Scanner sc, PrintWriter pw) {
		++PC;
		if (getAc() != 0) {

			pw.print("22-" + PC + "\n");
			pw.flush();
			if (sc.hasNextInt()) {
				int v = sc.nextInt();
				jump(v);
			}
		} else
			++PC;

	}

	public int loadIdxX(int v) {
		
		return this.X + v;

	}

	public void subX() {
		AC -= X;
	}

	public void subY() {
		AC -= Y;
	}

	public void loadSpX(Scanner sc, PrintWriter pw) {             
		int addr = X + SP;                                //Calculating the indirect address 
		pw.print(addr + "\n");
		pw.flush();
		if (sc.hasNextInt()) {
			int v = sc.nextInt();
			
			setAc(v);
			
		}
	}

	public void storeAddr(Scanner sc, PrintWriter pw) {
		++PC;
		pw.print(PC + "\n");
		pw.flush();
		if (sc.hasNextInt()) {
			int addr1 = sc.nextInt();
			if ((mode == 0) && (addr1 > userStackLimit)) {
				System.out
						.println("storeAddr : Cannot access kernel address from user mode");
				return;

			}
			pw.print("7-" + addr1 + "-" + AC + "\n");          //Sending the address to be written to memory
			pw.flush();
		}

	}

	public void loadAddr(Scanner sc, PrintWriter pw) {
	
		++PC;
	
		pw.print(PC + "\n");
		pw.flush();

		recSendAddr(sc, pw);
		if (sc.hasNextInt()) {
			int acV = sc.nextInt();
			
			setAc(acV);
		
		}

	}

	public void loadIndAddr(Scanner sc, PrintWriter pw) {
		// System.out.println("Case 3");
		++PC;
		// System.out.println("PC = " + ++PC);
		pw.print(PC + "\n");
		pw.flush();

		recSendAddr(sc, pw);
		recSendAddr(sc, pw);

		if (sc.hasNextInt()) {
			int acV = sc.nextInt();

			// System.out.println("Value received from memory " + acV);
			// System.out.println("AC value before :" + getAc());
			setAc(acV);
			// System.out.println("AC value now :" + getAc());
		}

	}

	public int loadIdxY(int v) {
		// Y = 1; // REMOVE THIS LINE
		// System.out.println("Y " + Y + "Y + v " + (Y + v));
		return this.Y + v;

	}

	public void loadIdxXAaddr(Scanner sc, PrintWriter pw) {
		// System.out.println("Case 4");
		++PC;
		// System.out.println("PC = " + ++PC);
		pw.print(PC + "\n");
		pw.flush();
		// .sleep(10);
		if (sc.hasNextInt()) {
			int v = sc.nextInt();
			// System.out.println("Next entry after 4 is : " + v);
			int addr = loadIdxX(v);
			// System.out.println("Address calculated is  :" + addr);
			// System.out.println("Sending " + addr + "to memory");
			pw.print(addr + "\n");
			pw.flush();
			if (sc.hasNextInt()) {
				int acV = sc.nextInt();
				// System.out.println("Value received from memory "
				// + acV);
				// System.out.println("AC value before :"
				// + getAc());
				setAc(acV);
				// System.out.println("AC value now :"
				// + getAc());
			}
		}

	}

	public void loadIdxYAddr(Scanner sc, PrintWriter pw) {
		// System.out.println("Case 5");
		++PC;
		// System.out.println("PC = " + ++PC);
		pw.print(PC + "\n");
		pw.flush();
		// .sleep(10);
		if (sc.hasNextInt()) {
			int v = sc.nextInt();
			// System.out.println("Next entry after 5 is : " + v);
			int addr = loadIdxY(v);
			// System.out.println("Address calculated is  :" + addr);
			// System.out.println("Sending " + addr + "to memory");
			pw.print(addr + "\n");
			pw.flush();
			if (sc.hasNextInt()) {
				int acV = sc.nextInt();
				// System.out.println("Value received from memory "
				// + acV);
				// System.out.println("AC value before :"
				// + getAc());
				setAc(acV);
				// System.out.println("AC value now :"
				// + getAc());
			}
		}

	}

	public void copyToSp() {
		SP = AC;
	}

	public void copyFromSp() {
		AC = SP;
	}

	public void push(Scanner sc, PrintWriter pw, int value) {
		// System.out.println("Case push");
		SP--;
		if (mode == 0 && SP > userStackLimit) {
			System.out.println("Cant access kernel mode in user mode");
			SP++;
			return;
		}
		if (mode == 0 && SP < 0) {
			System.out.println("Cant exceed the min address of user memory);");
			SP++;
			return;
		}
		if (mode == 1 && SP > kernelStackLimit) {

			System.out
					.println("Cant exceed the maximum address of kernel memory");
			return;
		}
		pw.print("27-" + SP + "-" + value + "\n");
		pw.flush();
		int retValue;
		if (sc.hasNext()) {
			retValue = Integer.parseInt(sc.next());
			switch (retValue) {
			case 0:
				System.out.println("Out of stack");
				// System.out.println(retValue);
				break;
			case 1:
				System.out.println("stack is full");
				// System.out.println(retValue);
				break;

			case 5:
				// System.out.println("Pushed value into the stack");
				// System.out.println(retValue);
				break;

			}
		}

	}

	public void pop(Scanner sc, PrintWriter pw) {
		// System.out.println("Case pop");
		pw.print("28-" + SP + "-" + "\n");
		pw.flush();
		if (mode == 0 && SP > userStackLimit) {
			System.out.println("Cant access kernel mode in user mode");
			return;
		}
		if (mode == 1 && SP > kernelStackLimit) {
			System.out
					.println("Pop error : Cant exceed max address in kernel area");
			return;
		}
		int retValue;
		if (sc.hasNext()) {
			retValue = Integer.parseInt(sc.next());
			switch (retValue) {
			case -1:
				System.out.println("Out of stack");
				System.out.println(retValue);
				break;
			case -2:
				System.out.println("Stack is empty !!");
				System.out.println(retValue);
				break;
			default:// System.out.println("Stack Top is ");
				// System.out.println(retValue);
				setAc(retValue);
				SP++;
				break;
			}

		}

	}

	public void callAddr(Scanner sc, PrintWriter pw) {
		// System.out.println("Case 23");
		++PC;
		// System.out.println("PC = " + ++PC);
		int retAddr = PC + 1;

		push(sc, pw, retAddr);
		pw.print(PC + "\n");
		pw.flush();
		if (sc.hasNextInt()) {
			int v = sc.nextInt();
			jump(v);
		}

	}

	public void interrupt(Scanner sc, PrintWriter pw) {
		// System.out.println("Case Interrupt");
		intFlag = 1;
		dontCount = 1;
		intEnable = 0;
		int tempSP = SP;
		SP = 2000; // Switching to kernel stack
		push(sc, pw, PC + 1);
		push(sc, pw, tempSP);
		setPC(1500);

	}

	public void iRet(Scanner sc, PrintWriter pw) {
		pop(sc, pw);
		int tempSP = AC;
		pop(sc, pw);
		setPC(AC);
		setSP(tempSP);

		setMode(0);
		// if(timerFlag==1){
		if (((intEnable == 0) && (timerFlag == 1))
				|| ((intEnable == 0) && (intFlag == 1))) {
			dontCount = 0;
			timerFlag = 0;
			AC = tempAC;
			intFlag = 0;
			/*
			 * X=tempX; Y=tempY;
			 */
		}
		/*
		 * if(0==intEnable){ AC=tempAC; X=tempX; Y=tempY;
		 * 
		 * }
		 */

		intEnable = 1;

	}

	public void setSP(int aC2) {
		SP = aC2;

	}

	public void timer(Scanner sc, PrintWriter pw) {
		// System.out.println("Case Timer Interrupt");
		dontCount = 1;
		tempAC = AC;
		tempX = X;
		tempY = Y;
		intEnable = 0;
		int tempSP = SP;
		SP = 2000; // Switching to kernel stack
		push(sc, pw, PC);
		push(sc, pw, tempSP);
		setPC(1000);

	}

	public void ret(Scanner sc, PrintWriter pw) {
		// System.out.println("Case 24");
		pop(sc, pw);
		setPC(getAc());
		// System.out.println("PC value now :"+getPC());

	}

	public void recSendAddr(Scanner sc, PrintWriter pw) {

		if (sc.hasNextInt()) {
			int v = sc.nextInt();
			// System.out.println("Next entry after 2 is : " + v);
			if ((mode == 0) && (v > userStackLimit)) {
				System.out
						.println("redSendAddr : cannot access kernel address from user mode");

			}
			pw.print(v + "\n");
			pw.flush();
		}
	}

	public static void main(String args[]) {
		CPU processor = new CPU();
                  


		try {

			Runtime rt = Runtime.getRuntime();
			int counter = Integer.parseInt(args[1]);
			int i = 0;
			String ins;
			int value;
			// System.out.println(System.getProperty("user.dir"));
			// System.out.println(System.getProperty("java.home"));
			// Process proc = rt.exec("ls -l");
			Process proc = rt.exec("java Memory " + args[0]);

			String current = new java.io.File(".").getCanonicalPath();
			// System.out.println("Current dir:" + current);
			InputStream is = proc.getInputStream();
			OutputStream os = proc.getOutputStream();
			PrintWriter pw = new PrintWriter(os);

			String s;
			Scanner sc = new Scanner(is);

			// System.out.println("In CPU");

			while (!(s = sc.nextLine()).equals("Ready")) {            //Waiting for a ready signal from memory
				// System.out.println(s);

				// System.out.println("Now I can begin the execution");
			}
			int inst = 0;
			while (counter != 0) {
				pw.print(processor.PC + "\n");
				pw.flush();

				// counter--;
				if (sc.hasNextInt()) {

					processor.setIR(sc.nextInt());
					// System.out.println(" \n\nIR content is : "
					// + processor.getIR());
					if (processor.getIR() == 50) {
						proc.destroy();
						break;
					}
				}
				switch (processor.getIR()) {
				case 1:
					processor.loadValue(sc, pw);
					break;

				case 2:
					processor.loadAddr(sc, pw);
					break;

				case 3:
					processor.loadIndAddr(sc, pw);
					break;

				case 4:
					processor.loadIdxXAaddr(sc, pw);

					break;

				case 5:
					processor.loadIdxYAddr(sc, pw);

					break;
				case 6:
					processor.loadSpX(sc, pw);
					break;
				case 7:
					processor.storeAddr(sc, pw);
					break;
				case 8:
					processor.get();
					break;
				case 9:
					processor.putPort(sc, pw);
					break;

				case 10:
					processor.addX();
					break;
				case 11:
					processor.addY();
					break;
				case 12:
					processor.subX();
					break;
				case 13:
					processor.subY();
					break;
				case 14:
					processor.copyToX();
					break;
				case 15:
					processor.copyFromX();
					break;
				case 16:
					processor.copyToY();
					break;
				case 17:
					processor.copyFromY();
					break;
				case 18:
					processor.copyToSp();
					break;
				case 19:
					processor.copyFromSp();
					break;
				case 20:
					processor.jumpAddr(sc, pw);

					break;
				case 21:
					processor.jumpIfEqualAddr(sc, pw);
					break;
				case 22:
					processor.jumpIfNotEqual(sc, pw);
					break;
				case 23:
					processor.callAddr(sc, pw);
					break;
				case 24:
					processor.ret(sc, pw);
					break;
				case 25:
					// System.out.println("Case 25; PC : " + processor.getPC());
					processor.incrementX();
					break;

				case 26:
					// System.out.println("Case 26; PC : " + processor.getPC());
					processor.decrementX();
					break;
				case 27:
					processor.push(sc, pw, processor.getAc());
					break;
				case 28:
					processor.pop(sc, pw);
					break;
				case 29:
					if (processor.intEnable == 1) {           //Checking interrupt enable flag
						processor.setMode(1);
						processor.interrupt(sc, pw);
					} else {
						System.out.println("Interrupt disabled ");
						processor.setPC(processor.PC + 1);
					}

					break;
				case 30:
					processor.iRet(sc, pw);
				case 50:
					break;

				}
				//
				// if(processor.getIR()==50)
				// {System.out.println(" 50 came");break;}
				// System.out.println(processor.getIR());
				if ((20 != processor.getIR())
						&& (21 != processor.getIR())
						&& (22 != processor.getIR())
						&& (23 != processor.getIR() && (24 != processor.getIR()))
						&& (29 != processor.getIR())
						&& (30 != processor.getIR()))
					processor.PC++;                        //PC should not be incremented if it is a jump instruction

				if (processor.getIR() != 0) {
					if (processor.dontCount != 1 && processor.getIR() != 30) {
						counter--;                       //Counter for timer interrupt
						inst++;
					}
					/*
					 * if(processor.dontCount==1 && processor.timerFlag==0)
					 * {processor.dontCount=0; /*processor.AC=processor.tempAC;
					 * processor.X=processor.tempX; processor.Y=processor.tempY;
					 * }
					 */
				}
				// System.out.println("Instruction number  : " + inst);
				if (counter == 0) {
					if (processor.intEnable == 1) {
						processor.setMode(1);             //Changing mode to kernel mode on timer interrupt
						processor.timerFlag = 1;         //Setting timer interrupt flag
						processor.timer(sc, pw);
					} else {
						System.out.println("Interrupt disabled");
					}
					counter = Integer.parseInt(args[1]);

				}
			}
			/*
			 * else{ processor.setIR(sc.nextInt()); if(processor.getIR()==50) {
			 * System.out.println("END called"); proc.destroy(); break; } }
			 */
			// break;
			// if(Processor.getIR()==50) break;

			// System.out.println("Counter :  " + counter + "  PC  : "
			// + processor.PC);

			proc.waitFor();

			int exitVal = proc.exitValue();

			System.out.println("Back to parent : " + exitVal);

		} catch (Throwable t) {
			t.printStackTrace();

		}
		// System.out.println(System.getProperty("user.dir"));

	}

}

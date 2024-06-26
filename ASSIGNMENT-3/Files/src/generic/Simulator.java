package generic;

import processor.Clock;
import processor.Processor;

import java.io.*;
import java.nio.ByteBuffer;

public class Simulator {
		
	static Processor processor;
	static boolean simulationComplete;
	
	public static void setupSimulation(String assemblyProgramFile, Processor p)
	{
		Simulator.processor = p;
		loadProgram(assemblyProgramFile);
		
		simulationComplete = false;
	}
	
	static void loadProgram(String assemblyProgramFile)
	{
		/*
		 * TODO
		 * 1. load the program into memory according to the program layout described
		 *    in the ISA specification
		 * 2. set PC to the address of the first instruction in the main
		 * 3. set the following registers:
		 *     x0 = 0
		 *     x1 = 65535
		 *     x2 = 65535
		 */
		try {
			// create a buffered stream for the assembly program file
			FileInputStream fis = new FileInputStream(assemblyProgramFile);
			BufferedInputStream bis = new BufferedInputStream(fis);

			// read one byte at a time
			int pc = 0; //Stores current PC value
			boolean check = true;
			int trl; //used for reading lines from file
			int cnt = 0; //used to keep track of number of lines read from assembly file
			byte[] temp = new byte[4]; //temporary location to store an instruction from assembly file

			int n1 = -1; //Bytes 0 to n1 of memory have global data stored in them;
			int n2 = 0; //Bytes n1 + 1 to n2 of memory have text/code segment stored in them

			//sets pc to the memory address of the first instruction
			if((trl = bis.read(temp, 0, 4)) != -1){
				check = false;
				pc = ByteBuffer.wrap(temp).getInt();
				processor.getRegisterFile().setProgramCounter(pc);
			}

			//writing global data to main memory
			while (true) {
				if(cnt >= pc){
					check = true;
					break;
				}
				trl = bis.read(temp, 0, 4);
				n1 += 1;
				int num = ByteBuffer.wrap(temp).getInt(); //temporary integer to store one integer of global data
				processor.getMainMemory().setWord(n1, num);
				n2 = n1;
				cnt += 1;
			}

			//writing instructions to main memory
			trl = bis.read(temp, 0, 4);
			while (trl != -1) {
				n2 += 1;
				int ins = ByteBuffer.wrap(temp).getInt(); //temporary integer to store one instruction
				processor.getMainMemory().setWord(n2, ins);
				cnt += 1;
				trl = bis.read(temp, 0, 4);
			}
			// System.out.println(check);
			if(check){
			processor.getRegisterFile().setValue(0, 0);
			processor.getRegisterFile().setValue(1, 65535);
			processor.getRegisterFile().setValue(2, 65535);
			}
			// close the buffered stream 
			bis.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void simulate()
	{
		Statistics.setNumberOfInstructions(0);
		Statistics.setNumberOfCycles(0);

		while(simulationComplete == false)
		{
			processor.getIFUnit().performIF();
			Clock.incrementClock();
			processor.getOFUnit().performOF();
			Clock.incrementClock();
			processor.getEXUnit().performEX();
			Clock.incrementClock();
			processor.getMAUnit().performMA();
			Clock.incrementClock();
			processor.getRWUnit().performRW();
			Clock.incrementClock();

			Statistics.setNumberOfInstructions(Statistics.getNumberOfInstructions() + 1);
			Statistics.setNumberOfCycles(Statistics.getNumberOfCycles() + 1);
		}
	}
	
	public static void setSimulationComplete(boolean value)
	{
		simulationComplete = value;
	}
}

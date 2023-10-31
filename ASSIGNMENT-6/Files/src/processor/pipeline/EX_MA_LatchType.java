package processor.pipeline;

import generic.Instruction;

public class EX_MA_LatchType {
	
	boolean MA_enable;
	Instruction instruction;
	int aluResult;
	boolean MA_Lock;
	boolean MA_Busy;

	public EX_MA_LatchType()
	{
		MA_enable = false;
		MA_Lock = false;
		MA_Busy = false;
	} 
	
	public void setInstruction(Instruction instruction){
		this.instruction = instruction;
	}

	public Instruction getInstruction() {
		return this.instruction;
	}

	public void setAluResult(int ALUResult){
		this.aluResult = ALUResult;
	}

	public int getAluResult(){
		return this.aluResult;
	}

	public boolean isMA_enable() {
		return MA_enable;
	}

	public void setMA_enable(boolean val) {
		MA_enable = val;
	}

	public boolean isMA_Locked() {
		return MA_Lock; 
	}

	public void setMA_Lock(boolean val) {
		MA_Lock = val; 
	}

	public void setMA_Busy(boolean val) { 
		MA_Busy = val; 
	}

	public boolean isMA_Busy() { 
		return MA_Busy; 
	}

}
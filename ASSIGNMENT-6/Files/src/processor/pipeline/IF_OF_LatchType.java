package processor.pipeline;

public class IF_OF_LatchType {
	
	boolean OF_enable;
	int instruction;
	boolean OF_Busy;
	
	public IF_OF_LatchType()
	{
		OF_enable = false;
		OF_Busy = false;
	}

	public boolean isOF_enable() {
		return OF_enable;
	}

	public void setOF_enable(boolean val) {
		OF_enable = val;
	}

	public int getInstruction() {
		return instruction;
	}

	public void setInstruction(int instruction) {
		this.instruction = instruction;
	}

	public void setOF_Busy(boolean val) {
		OF_Busy = val; 
	}

	public boolean isOF_Busy() {
		return OF_Busy; 
	}

}

package processor.pipeline;

public class EX_IF_LatchType {

	boolean EX_IF_enable;
	int PC;

	public EX_IF_LatchType() {
		EX_IF_enable = false;
	}

	public EX_IF_LatchType(boolean val) {
		EX_IF_enable = val;
	}

	public EX_IF_LatchType(boolean val, int pc) {
		EX_IF_enable = val;
		PC = pc;
	}

	public boolean isEX_IF_enable() {
		return EX_IF_enable;
	}

	public void setEX_IF_enable(boolean val) {
		EX_IF_enable = val;
	}
	
	public void setEX_IF_enable(boolean val, int pc) {
		EX_IF_enable = val;
		PC = pc;
	}

	public void setPC(int pc) {
		PC = pc;
	}

	public int getPC() {
		return PC;
	}

}

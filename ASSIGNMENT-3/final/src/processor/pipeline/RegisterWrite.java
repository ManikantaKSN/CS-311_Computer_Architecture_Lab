package processor.pipeline;

import generic.Simulator;
import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}
	
	public void performRW()
	{
		if(MA_RW_Latch.isRW_enable()) {
			Instruction inst = MA_RW_Latch.getInstruction();
			OperationType opr = inst.getOperationType();
			boolean sett = false;
			int rd = -1;
			int ldResult = -1;
			int aluResult = -1;
			if(opr == OperationType.jmp || opr == OperationType.blt || opr == OperationType.bgt || opr == OperationType.beq || opr == OperationType.bne || opr == OperationType.store) {
				sett = true;
			}
			else if(opr == OperationType.load){
				ldResult = MA_RW_Latch.getLdResult();
				rd = inst.getDestinationOperand().getValue();
				containingProcessor.getRegisterFile().setValue(rd, ldResult);
			}
			else if(opr == OperationType.end){
				Simulator.setSimulationComplete(true);
			}
			else{
				rd = inst.getDestinationOperand().getValue();
				aluResult = MA_RW_Latch.getAluResult();
				containingProcessor.getRegisterFile().setValue(rd, aluResult);
			}
			if(sett){
				//System.out.println("No write");
			}
			MA_RW_Latch.setRW_enable(false);
			IF_EnableLatch.setIF_enable(true);
		}
	}

}

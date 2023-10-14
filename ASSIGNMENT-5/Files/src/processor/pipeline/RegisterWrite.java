package processor.pipeline;

import generic.Simulator;
import generic.Statistics;
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
		if(MA_RW_Latch.isRW_Locked()){
			MA_RW_Latch.setRW_Lock(false);
		}
		else if(MA_RW_Latch.isRW_enable()) {
			Statistics.setNumberOfRWStageInstructions(Statistics.getNumberOfRWStageInstructions() + 1);
			Instruction inst = MA_RW_Latch.getInstruction();
			OperationType opr = inst.getOperationType();			
			int rd = -1;
			int ldResult = -1;
			int aluResult = -1;
			boolean sett = true;
			if(opr == OperationType.store || opr == OperationType.beq || opr == OperationType.bgt ||
			opr == OperationType.blt || opr == OperationType.jmp){
				sett = false;
			}
			else if(opr == OperationType.end){
				Simulator.setSimulationComplete(true);
			}
			else if(opr == OperationType.load){
				sett = false;
				ldResult = MA_RW_Latch.getLdResult();
				rd = inst.getDestinationOperand().getValue();
				containingProcessor.getRegisterFile().setValue(rd, ldResult);
			}
			else{
				//for alu op and call instruction
				sett = false;
				rd = inst.getDestinationOperand().getValue();
				aluResult = MA_RW_Latch.getAluResult();
				containingProcessor.getRegisterFile().setValue(rd, aluResult);
			}
			if(opr != OperationType.end && sett == false){
				IF_EnableLatch.setIF_enable(true);
			}
		}
	}
}

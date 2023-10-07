package processor.pipeline;

import generic.Instruction;
import processor.Processor;
import generic.Instruction.OperationType;

public class MemoryAccess {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_Enable_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_Enable_Latch;
	}
	
	public void performMA()
	{
		if(EX_MA_Latch.isMA_Locked()){
			MA_RW_Latch.setRW_Lock(true);
			MA_RW_Latch.setInstruction(null);
			EX_MA_Latch.setMA_Lock(false);
		}
		else if(EX_MA_Latch.isMA_enable()){
			Instruction inst = EX_MA_Latch.getInstruction();
			int aluResult = EX_MA_Latch.getAluResult();
			OperationType opr = inst.getOperationType();

			if(opr == OperationType.load){
				int ldResult = containingProcessor.getMainMemory().getWord(aluResult);
				MA_RW_Latch.setLdResult(ldResult);
			}
			else if(opr == OperationType.store){
				int stWord = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
				containingProcessor.getMainMemory().setWord(aluResult, stWord);
			}
			else if(opr == OperationType.end){
				IF_EnableLatch.setIF_enable(false);
			}
			MA_RW_Latch.setAluResult(aluResult);
			MA_RW_Latch.setInstruction(inst);
			MA_RW_Latch.setRW_enable(true);
		}
	}
}

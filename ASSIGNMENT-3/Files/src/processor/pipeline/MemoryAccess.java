package processor.pipeline;

import generic.Instruction;
import processor.Processor;
import generic.Instruction.OperationType;

public class MemoryAccess {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
	}
	
	public void performMA()
	{
		if(EX_MA_Latch.isMA_enable()){
			Instruction inst = EX_MA_Latch.getInstruction();
			int aluResult = EX_MA_Latch.getAluResult();
			OperationType currentOperation = inst.getOperationType();
			if(currentOperation == OperationType.load){
				int ldResult = containingProcessor.getMainMemory().getWord(aluResult);
				MA_RW_Latch.setLdResult(ldResult);
			}
			else if(currentOperation == OperationType.store){
				int stWord = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
				containingProcessor.getMainMemory().setWord(aluResult, stWord);
			}
			MA_RW_Latch.setAluResult(aluResult);
			MA_RW_Latch.setInstruction(inst);
			EX_MA_Latch.setMA_enable(false);
			MA_RW_Latch.setRW_enable(true);
		}
	}
}

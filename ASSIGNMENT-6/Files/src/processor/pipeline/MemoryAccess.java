package processor.pipeline;

import processor.Processor;
import generic.Instruction.OperationType;
import generic.*;
import processor.Clock;
import configuration.Configuration;

public class MemoryAccess implements Element{
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_IF_LatchType EX_IF_Latch;
	public EX_MA_LatchType EX_MA_Latch;
	public MA_RW_LatchType MA_RW_Latch;
	public Instruction instruction;

	public MemoryAccess(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch, EX_IF_LatchType eX_IF_Latch, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
	}
	
	public void performMA()
	{
		if(EX_MA_Latch.isMA_Busy()){
			OF_EX_Latch.setEX_Busy(true);
		}
		else{
			OF_EX_Latch.setEX_Busy(false);
			if(EX_MA_Latch.isMA_Locked()){
				MA_RW_Latch.setRW_Lock(true);
				MA_RW_Latch.setInstruction(null);//null = nop
				EX_MA_Latch.setMA_Lock(false);
			}
			else if(EX_MA_Latch.isMA_enable()){
				Instruction inst = EX_MA_Latch.getInstruction();
				instruction = inst;
				int aluResult = EX_MA_Latch.getAluResult();
				OperationType opr = inst.getOperationType();
				MA_RW_Latch.setInstruction(instruction);
				if(opr == OperationType.load){
					Simulator.getEventQueue().addEvent(
							new MemoryReadEvent(
									Clock.getCurrentTime() + containingProcessor.getL1dCache().getCacheLatency(), this,
									containingProcessor.getL1dCache(), aluResult));
					EX_MA_Latch.setMA_Busy(true);
					return;
				}
				else if(opr == OperationType.store){
					int stWord = containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue());
					Simulator.getEventQueue().addEvent(
						new MemoryWriteEvent(
								Clock.getCurrentTime() + containingProcessor.getL1dCache().getCacheLatency(), this,
								containingProcessor.getL1dCache(), aluResult, stWord));
					EX_MA_Latch.setMA_Busy(true);
					return;		
				}
				if(opr == OperationType.end){
					IF_EnableLatch.setIF_enable(false);
				}
				MA_RW_Latch.setAluResult(aluResult);
				MA_RW_Latch.setInstruction(inst);
				MA_RW_Latch.setRW_enable(true);
				EX_MA_Latch.setMA_enable(false);
			}
		}
	}
	@Override
	public void handleEvent(Event e) {
		if(e.getEventType() == Event.EventType.MemoryResponse) {
			MemoryResponseEvent event = (MemoryResponseEvent) e;
			int ldResult = event.getValue();
			MA_RW_Latch.setLdResult(ldResult);
			MA_RW_Latch.setInstruction(instruction);
			EX_MA_Latch.setMA_Busy(false);
			MA_RW_Latch.setRW_enable(true);
			OF_EX_Latch.setEX_Busy(false);
			EX_MA_Latch.setMA_enable(false);
		}
	}
}

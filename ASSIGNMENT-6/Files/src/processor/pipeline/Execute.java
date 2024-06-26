package processor.pipeline;

import configuration.Configuration;
import generic.Element;
import generic.Event;
import generic.ExecutionCompleteEvent;
import generic.Instruction;
import generic.Statistics;
import processor.Clock;
import processor.Processor;
import generic.Instruction.OperationType;
import generic.Operand.OperandType;
import generic.Simulator;

public class Execute implements Element{
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	IF_OF_LatchType IF_OF_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch, IF_OF_LatchType iF_OF_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}
	
	public void performEX()
	{
		if(OF_EX_Latch.isEX_Busy()){
			IF_OF_Latch.setOF_Busy(true);
		}
		else{
			IF_OF_Latch.setOF_Busy(false);
			if(OF_EX_Latch.isEX_Locked()){
				EX_MA_Latch.setMA_Lock(true);
				OF_EX_Latch.setEX_Lock(false);
				EX_MA_Latch.setInstruction(null);//null = nop
				OF_EX_Latch.setEX_enable(false);
			}
			else if(OF_EX_Latch.isEX_enable()){
				OF_EX_Latch.setEX_enable(false);
				Instruction instr = OF_EX_Latch.getInstruction();
				boolean checking = false;
				int curr_pc = instr.getProgramCounter() - 1;
				OperationType opr = instr.getOperationType();
				int sourceOperand1 = -1;
				int sourceOperand2 = -1;
				int imm, rem;
				int aluResult;

				if(opr == OperationType.jmp || opr == OperationType.beq || opr == OperationType.bgt ||
				opr == OperationType.blt|| opr== OperationType.bne || opr == OperationType.end){
					Statistics.setNumberOfBranchesTaken(Statistics.getNumberOfBranchesTaken() + 2);
					checking = true;
					IF_EnableLatch.setIF_enable(false);
					IF_OF_Latch.setOF_enable(false);
					OF_EX_Latch.setEX_enable(false);
				}	

				switch (opr){
					case add:
						checking = true; 
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult = sourceOperand1 + sourceOperand2;
						event_scheduler(opr,this);
						break;
					case addi:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						imm = instr.getSourceOperand2().getValue();
						aluResult = sourceOperand1 + imm;
						event_scheduler(opr,this);
						break;
					case sub:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult = sourceOperand1 - sourceOperand2;
						event_scheduler(opr,this);
						break;
					case subi:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						imm = instr.getSourceOperand2().getValue();
						aluResult = sourceOperand1 - imm;
						event_scheduler(opr,this);
						break;
					case mul:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult = sourceOperand1 * sourceOperand2;
						event_scheduler(opr,this);
						break;
					case muli:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						imm = instr.getSourceOperand2().getValue();
						aluResult = sourceOperand1 * imm;
						event_scheduler(opr,this);
						break;
					case div:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult = sourceOperand1 / sourceOperand2;
						rem = (sourceOperand1 % sourceOperand2);
						containingProcessor.getRegisterFile().setValue(31, rem);
						event_scheduler(opr,this);
						break;
					case divi:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						imm = instr.getSourceOperand2().getValue();
						aluResult = sourceOperand1 / imm;
						rem = (sourceOperand1 % imm);
						containingProcessor.getRegisterFile().setValue(31, rem);
						event_scheduler(opr,this);
						break;
					case and:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult = sourceOperand1 & sourceOperand2;
						event_scheduler(opr,this);
						break;
					case andi:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						imm = instr.getSourceOperand2().getValue();
						aluResult = sourceOperand1 & imm;
						event_scheduler(opr,this);
						break;
					case or:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult = sourceOperand1 | sourceOperand2;
						event_scheduler(opr,this);
						break;
					case ori:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						imm = instr.getSourceOperand2().getValue();
						aluResult = sourceOperand1 | imm;
						event_scheduler(opr,this);
						break;
					case xor:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult = sourceOperand1 ^ sourceOperand2;
						event_scheduler(opr,this);
						break;
					case xori:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						imm = instr.getSourceOperand2().getValue();
						aluResult = sourceOperand1 ^ imm;
						event_scheduler(opr,this);
						break;
					case slt:
						checking = false;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						if(sourceOperand1 < sourceOperand2)
							aluResult = 1;
						else
							aluResult = 0;
						event_scheduler(opr,this);
						break;
					case slti:
						checking = false;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						imm = instr.getSourceOperand2().getValue();
						if(sourceOperand1 < imm)
							aluResult = 1;
						else
							aluResult = 0;
						event_scheduler(opr,this);
						break;
					case sll:
						checking = false;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult = sourceOperand1 << sourceOperand2;
						event_scheduler(opr,this);
						break;
					case slli:
						checking = false;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						imm = instr.getSourceOperand2().getValue();
						aluResult = sourceOperand1 << imm;
						event_scheduler(opr,this);
						break;
					case srl:
						checking = false;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult = sourceOperand1 >>> sourceOperand2;
						event_scheduler(opr,this);
						break;
					case srli:
						checking = false;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						imm = instr.getSourceOperand2().getValue();
						aluResult = sourceOperand1 >>> imm;
						event_scheduler(opr,this);
						break;
					case sra:
						checking = false;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult = sourceOperand1 >> sourceOperand2;
						event_scheduler(opr,this);
						break;
					case srai:
						checking = false;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						imm = instr.getSourceOperand2().getValue();
						aluResult = sourceOperand1 >> imm;
						event_scheduler(opr,this);
						break;
					case load:
						checking = false;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						aluResult =-1;
						imm = instr.getSourceOperand2().getValue();
						aluResult = sourceOperand1 + imm;
						event_scheduler(opr,this);
						break;
					case store:
						checking = false;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getDestinationOperand().getValue());
						aluResult =-1;
						imm = instr.getSourceOperand2().getValue();
						aluResult = sourceOperand1 + imm;
						event_scheduler(opr,this);
						break;
					case jmp:
						checking = true;
						OperandType jump = instr.getDestinationOperand().getOperandType();
						aluResult =-1;
						if(jump == OperandType.Register){
							imm = containingProcessor.getRegisterFile().getValue(instr.getDestinationOperand().getValue());
						}
						else{
							imm = instr.getDestinationOperand().getValue();
						}
						aluResult = curr_pc + imm;
						EX_IF_Latch.setEX_IF_enable(true, aluResult);
						event_scheduler(opr,this);
						break;
					case beq:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult =-1;
						imm = instr.getDestinationOperand().getValue();
						if(sourceOperand1 == sourceOperand2){
							aluResult = curr_pc + imm;
							EX_IF_Latch.setEX_IF_enable(true, aluResult);
						}
						event_scheduler(opr,this);
						break;
					case bne:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult =-1;
						imm = instr.getDestinationOperand().getValue();
						if(sourceOperand1 != sourceOperand2){
							aluResult = curr_pc + imm;
							EX_IF_Latch.setEX_IF_enable(true, aluResult);
						}
						event_scheduler(opr,this);
						break;
					case blt:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult =-1;
						imm = instr.getDestinationOperand().getValue();
						if(sourceOperand1 < sourceOperand2){
							aluResult = curr_pc + imm;
							EX_IF_Latch.setEX_IF_enable(true, aluResult);
						}
						event_scheduler(opr,this);
						break;
					case bgt:
						checking = true;
						sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
						sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
						aluResult =-1;
						imm = instr.getDestinationOperand().getValue();
						if(sourceOperand1 > sourceOperand2){
							aluResult = curr_pc + imm;
							EX_IF_Latch.setEX_IF_enable(true, aluResult);
						}
						event_scheduler(opr,this);
						break;
					case end:
						aluResult=-1;
						break;
					default:
						aluResult=-1;
						break;
				}
				if(checking){
					//System.out.println("Execution done");
				}
				EX_MA_Latch.setAluResult(aluResult);
				EX_MA_Latch.setInstruction(instr);
				EX_MA_Latch.setMA_enable(true);
			}
		}
	}
	public void event_scheduler(OperationType opr, Execute execute){
		long lat;
		if(opr == OperationType.mul || opr == OperationType.muli)
		{
			lat=Configuration.multiplier_latency;
		}
		else if(opr == OperationType.div || opr == OperationType.divi)
		{
			lat=Configuration.divider_latency;
		}
		else
		{
			lat=Configuration.ALU_latency;
		}
		Simulator.getEventQueue().addEvent(
				new ExecutionCompleteEvent(
						Clock.getCurrentTime()+lat,
						execute,execute));
		OF_EX_Latch.setEX_Busy(true);
	}
	@Override
	public void handleEvent(Event event)
	{
		if(EX_MA_Latch.isMA_Busy()) {
			event.setEventTime(Clock.getCurrentTime()+1);
			Simulator.getEventQueue().addEvent(event);
		}
		else {
			OF_EX_Latch.setEX_enable(false);
			EX_MA_Latch.setMA_enable(true);
			OF_EX_Latch.setEX_Busy(false);
		}	
	}
}

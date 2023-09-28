package processor.pipeline;

import generic.Instruction;
import processor.Processor;
import generic.Instruction.OperationType;
import generic.Operand.OperandType;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	
	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
	}
	
	public void performEX()
	{
		if(OF_EX_Latch.isEX_enable()){
			Instruction instr = OF_EX_Latch.getInstruction();
			int pc = instr.getProgramCounter() - 1;
			boolean checking = false;
			OperationType opr = instr.getOperationType();
			int sourceOperand1 = -1;
			int sourceOperand2 = -1;
			int imm, rem;
			int aluResult = -1;
			switch (opr){
				case add:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					aluResult = sourceOperand1 + sourceOperand2;
					break;
				case addi:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					imm = instr.getSourceOperand2().getValue();
					aluResult = sourceOperand1 + imm;
					break;
				case sub:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					aluResult = sourceOperand1 - sourceOperand2;
					break;
				case subi:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					imm = instr.getSourceOperand2().getValue();
					aluResult = sourceOperand1 - imm;
					break;
				case mul:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					aluResult = sourceOperand1 * sourceOperand2;
					break;
				case muli:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					imm = instr.getSourceOperand2().getValue();
					aluResult = sourceOperand1 * imm;
					break;
				case div:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					aluResult = sourceOperand1 / sourceOperand2;
					rem = (sourceOperand1 % sourceOperand2);
					containingProcessor.getRegisterFile().setValue(31, rem);
					break;
				case divi:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					imm = instr.getSourceOperand2().getValue();
					aluResult = sourceOperand1 / imm;
					rem = (sourceOperand1 % imm);
					containingProcessor.getRegisterFile().setValue(31, rem);
					break;
				case and:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					aluResult = sourceOperand1 & sourceOperand2;
					break;
				case andi:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					imm = instr.getSourceOperand2().getValue();
					aluResult = sourceOperand1 & imm;
					break;
				case or:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					aluResult = sourceOperand1 | sourceOperand2;
					break;
				case ori:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					imm = instr.getSourceOperand2().getValue();
					aluResult = sourceOperand1 | imm;
					break;
				case xor:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					aluResult = sourceOperand1 ^ sourceOperand2;
					break;
				case xori:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					imm = instr.getSourceOperand2().getValue();
					aluResult = sourceOperand1 ^ imm;
					break;
				case slt:
					checking = false;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					if(sourceOperand1 < sourceOperand2)
						aluResult = 1;
					else
						aluResult = 0;
					break;
				case slti:
					checking = false;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					imm = instr.getSourceOperand2().getValue();
					if(sourceOperand1 < imm)
						aluResult = 1;
					else
						aluResult = 0;
					break;
				case sll:
					checking = false;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					aluResult = sourceOperand1 << sourceOperand2;
					break;
				case slli:
					checking = false;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					imm = instr.getSourceOperand2().getValue();
					aluResult = sourceOperand1 << imm;
					break;
				case srl:
					checking = false;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					aluResult = sourceOperand1 >>> sourceOperand2;
					break;
				case srli:
					checking = false;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					imm = instr.getSourceOperand2().getValue();
					aluResult = sourceOperand1 >>> imm;
					break;
				case sra:
					checking = false;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					aluResult = sourceOperand1 >> sourceOperand2;
					break;
				case srai:
					checking = false;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					imm = instr.getSourceOperand2().getValue();
					aluResult = sourceOperand1 >> imm;
					break;
				case load:
					checking = false;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					imm = instr.getSourceOperand2().getValue();
					aluResult = sourceOperand1 + imm;
					break;
				case store:
					checking = false;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getDestinationOperand().getValue());
					imm = instr.getSourceOperand2().getValue();
					aluResult = sourceOperand1 + imm;
					break;
				case jmp:
					checking = true;
					OperandType jump = instr.getDestinationOperand().getOperandType();
					if(jump == OperandType.Register){
						imm = containingProcessor.getRegisterFile().getValue(instr.getDestinationOperand().getValue());
					}
					else{
						imm = instr.getDestinationOperand().getValue();
					}
					aluResult = pc + imm;
					EX_IF_Latch.setEX_IF_enable(true, aluResult);
					break;
				case beq:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					imm = instr.getDestinationOperand().getValue();
					if(sourceOperand1 == sourceOperand2){
						aluResult = pc + imm;
						EX_IF_Latch.setEX_IF_enable(true, aluResult);
					}
					break;
				case bne:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					imm = instr.getDestinationOperand().getValue();
					if(sourceOperand1 != sourceOperand2){
						aluResult = pc + imm;
						EX_IF_Latch.setEX_IF_enable(true, aluResult);
					}
					break;
				case blt:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					imm = instr.getDestinationOperand().getValue();
					if(sourceOperand1 < sourceOperand2){
						aluResult = pc + imm;
						EX_IF_Latch.setEX_IF_enable(true, aluResult);
					}
					break;
				case bgt:
					checking = true;
					sourceOperand1 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand1().getValue());
					sourceOperand2 = containingProcessor.getRegisterFile().getValue(instr.getSourceOperand2().getValue());
					imm = instr.getDestinationOperand().getValue();
					if(sourceOperand1 > sourceOperand2){
						aluResult = pc + imm;
						EX_IF_Latch.setEX_IF_enable(true, aluResult);
					}
					break;
				case end:
					checking = true;
					break;
				default:
					break;
			}
			EX_MA_Latch.setAluResult(aluResult);
			EX_MA_Latch.setInstruction(instr);
			if(checking){
				//System.out.println("Execution done");
			}
			OF_EX_Latch.setEX_enable(false);
			EX_MA_Latch.setMA_enable(true);

	}
}
}

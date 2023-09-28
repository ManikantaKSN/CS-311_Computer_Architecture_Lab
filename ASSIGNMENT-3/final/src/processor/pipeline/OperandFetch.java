package processor.pipeline;

import generic.Instruction;
import generic.Operand;
import processor.Processor;
import generic.Instruction.OperationType;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
	}

	//Method to find two's complement
	public static String twoscomplement(StringBuffer str)
	{
		int n = str.length();
		int i;
		for (i = n-1 ; i >= 0 ; i--)
			if (str.charAt(i) == '1')
				break;
		if (i == -1)
			return "1" + str;
		for (int k = i-1 ; k >= 0; k--)
		{
			if (str.charAt(k) == '1')
				str.replace(k, k+1, "0");
			else
				str.replace(k, k+1, "1");
		}
		return str.toString();
	}

	public static String toBinary(int x, int len){
		if (len > 0) {
			return String.format("%" + len + "s",
					Integer.toBinaryString(x)).replace(" ", "0");
		}
		return null;
	}

	public static int toInteger(String binary){
		if(binary.charAt(0) == '1'){
			StringBuffer bufferBinary = new StringBuffer();
			bufferBinary.append(binary);
			binary = "-" + twoscomplement(bufferBinary);
		}
		else{
			binary = "+" + binary;
		}
		return Integer.parseInt(binary, 2);
	}

	public void performOF()
	{
		if(IF_OF_Latch.isOF_enable())
		{
			int inst = IF_OF_Latch.getInstruction();
			int pc = containingProcessor.getRegisterFile().getProgramCounter();
			String binaryInstruction =  toBinary(inst, 32);

			OperationType[] operationTypes = OperationType.values(); //getting all operation types from OperationType enum
			int opCodeInt = Integer.parseInt(binaryInstruction.substring(0, 5), 2);
			OperationType opr = operationTypes[opCodeInt]; //opr = array[index]

			Instruction instr = new Instruction();
			Operand rs1 = new Operand();
			Operand rs2 = new Operand();
			Operand rd = new Operand();
			Operand jump = new Operand();
			Operand imm = new Operand();
			int needtow = 0;
			int regsrc1 = -1;
			int regsrc2 = -1;
			int regdest = -1;
			int immediate = -1;
			instr.setProgramCounter(pc);
			instr.setOperationType(opr); 
			if(opr == OperationType.add || opr == OperationType.sub || opr == OperationType.mul || opr == OperationType.div ||
			opr == OperationType.and || opr == OperationType.or || opr == OperationType.xor ||opr == OperationType.slt ||
			opr == OperationType.sll || opr == OperationType.srl || opr == OperationType.sra){ 
					needtow =1; //R3 type
					rs1.setOperandType(Operand.OperandType.Register);
					regsrc1 = Integer.parseInt((binaryInstruction.substring(5, 10)), 2);
					rs1.setValue(regsrc1);
					instr.setSourceOperand1(rs1);
					rs2.setOperandType(Operand.OperandType.Register);
					regsrc2 = Integer.parseInt((binaryInstruction.substring(10, 15)), 2);
					rs2.setValue(regsrc2);
					instr.setSourceOperand2(rs2);
					rd.setOperandType(Operand.OperandType.Register);
					regdest = Integer.parseInt((binaryInstruction.substring(15, 20)), 2);
					rd.setValue(regdest);
					instr.setDestinationOperand(rd);
			}
			else if(opr == OperationType.beq || opr == OperationType.bgt || opr == OperationType.blt || opr == OperationType.bne){
					needtow = 3;
					rs1.setOperandType(Operand.OperandType.Register);
					regsrc1 = Integer.parseInt((binaryInstruction.substring(5, 10)), 2);
					rs1.setValue(regsrc1);
					instr.setSourceOperand1(rs1);
					rs2.setOperandType(Operand.OperandType.Register);
					regsrc2 = Integer.parseInt((binaryInstruction.substring(10, 15)), 2);
					rs2.setValue(regsrc2);
					instr.setSourceOperand2(rs2);
					imm.setOperandType(Operand.OperandType.Immediate);
					immediate = toInteger(binaryInstruction.substring(15, 32));
					imm.setValue(immediate);
					instr.setDestinationOperand(imm);
			}
			else if(opr == OperationType.jmp){
					needtow =2;
					regdest = Integer.parseInt((binaryInstruction.substring(5, 10)), 2);
					immediate = toInteger(binaryInstruction.substring(10, 32));
					if(immediate != 0){
						jump.setOperandType(Operand.OperandType.Immediate);
						jump.setValue(immediate);
					}
					else{
						jump.setOperandType(Operand.OperandType.Register);
						jump.setValue(regdest);
					}
					instr.setDestinationOperand(jump); 
			}
			else if( opr == OperationType.end){
				needtow =5;
			}
			else{
					needtow = 4;
					rs1.setOperandType(Operand.OperandType.Register);
					regsrc1 = Integer.parseInt((binaryInstruction.substring(5, 10)), 2);
					rs1.setValue(regsrc1);
					instr.setSourceOperand1(rs1);
					rs2.setOperandType(Operand.OperandType.Immediate);
					immediate = toInteger(binaryInstruction.substring(15, 32));
					rs2.setValue(immediate);
					instr.setSourceOperand2(rs2);
					rd.setOperandType(Operand.OperandType.Register);
					regdest = Integer.parseInt((binaryInstruction.substring(10, 15)), 2);
					rd.setValue(regdest);
					instr.setDestinationOperand(rd);
			}
			if(needtow!=0){
			//System.out.println("Status:" + needtow);
			}
			OF_EX_Latch.setInstruction(instr);
			IF_OF_Latch.setOF_enable(false);
			OF_EX_Latch.setEX_enable(true);
		}
	}

}

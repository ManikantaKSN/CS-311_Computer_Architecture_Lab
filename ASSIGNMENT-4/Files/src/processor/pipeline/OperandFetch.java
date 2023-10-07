package processor.pipeline;

import generic.Instruction;
import generic.Operand;
import generic.Statistics;
import processor.Processor;
import java.util.*;

import generic.Instruction.OperationType;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}

	//Method to find two's complement
	public String twoscomplement(StringBuffer str)
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

	public String toBinary(int x, int len){
		if (len > 0) {
			return String.format("%" + len + "s",
					Integer.toBinaryString(x)).replace(" ", "0");
		}
		return null;
	}

	public int toInteger(String binary){
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

	public boolean isConflict(int rs1, int rs2){
		boolean returnValue;
		// Creating Conflict Operations Set
		Set<String> ConflictInstructions = new HashSet<String>();
		//Adding Instructions that can cause conflict
		ConflictInstructions.add("add");
		ConflictInstructions.add("addi");
		ConflictInstructions.add("sub");
		ConflictInstructions.add("subi");
		ConflictInstructions.add("mul");
		ConflictInstructions.add("muli");
		ConflictInstructions.add("div");
		ConflictInstructions.add("divi");
		ConflictInstructions.add("and");
		ConflictInstructions.add("andi");
		ConflictInstructions.add("or");
		ConflictInstructions.add("ori");
		ConflictInstructions.add("xor");
		ConflictInstructions.add("xori");
		ConflictInstructions.add("slt");
		ConflictInstructions.add("slti");
		ConflictInstructions.add("sll");
		ConflictInstructions.add("slli");
		ConflictInstructions.add("srl");
		ConflictInstructions.add("srli");
		ConflictInstructions.add("sra");
		ConflictInstructions.add("srai");
		ConflictInstructions.add("load");
		ConflictInstructions.add("store");

		//Creating set of division instructions
		Set<String> DivisionInstructions = new HashSet<String>();
		//Adding the division instructions
		DivisionInstructions.add("div");
		DivisionInstructions.add("divi");

		Instruction EX_Stage_Ins = OF_EX_Latch.getInstruction();
		Instruction MA_Stage_Ins = EX_MA_Latch.getInstruction();
		Instruction RW_Stage_Ins = MA_RW_Latch.getInstruction();

		//Conflict check due to RAW Hazard
		int ex_rd = -100;
		int ma_rd = -100;
		int rw_rd = -100;
		boolean isEXDiv = false;
		boolean isMADiv = false;
		boolean isRWDiv = false;
		if(EX_Stage_Ins != null) {
			String ins = EX_Stage_Ins.getOperationType().name();
			if (ConflictInstructions.contains(ins)) {
				if(EX_Stage_Ins.getDestinationOperand() != null){
					ex_rd = EX_Stage_Ins.getDestinationOperand().getValue();
				}
				if(DivisionInstructions.contains(ins)){
					isEXDiv = true;
				}
			}
		}
		if(MA_Stage_Ins != null){
			String ins = MA_Stage_Ins.getOperationType().name();
			if(ConflictInstructions.contains(ins)){
				if(MA_Stage_Ins.getDestinationOperand() != null){
					ma_rd = MA_Stage_Ins.getDestinationOperand().getValue();
				}
				if(DivisionInstructions.contains(ins)){
					isMADiv = true;
				}
			}
		}
		if(RW_Stage_Ins != null){
			String ins = RW_Stage_Ins.getOperationType().name();
			if(ConflictInstructions.contains(ins)){
				if(RW_Stage_Ins.getDestinationOperand() != null){
					rw_rd = RW_Stage_Ins.getDestinationOperand().getValue();
				}
				if(DivisionInstructions.contains(ins)){
					isRWDiv = true;
				}
			}
		}
		if(rs1 == ex_rd || rs1 == ma_rd || rs1 == rw_rd || rs2 == ex_rd || rs2 == ma_rd || rs2 == rw_rd){
			returnValue = true;
		}
		else {
			returnValue =  false;
		}

		//Conflict check due to Division
		if(isEXDiv || isMADiv || isRWDiv){
			if(rs1 == 31 || rs2 == 31){
				System.out.println("Conflict due to Division");
				returnValue = true;
			}
			else if(!returnValue){
				returnValue = false;
			}
		}
		return returnValue;
	}

	public void conflictObserved(){
		// System.out.println("Conflict and Lock");
		IF_EnableLatch.setIF_enable(false);
		OF_EX_Latch.setEX_Lock(true);
	}

	public void performOF()
	{
		if(IF_OF_Latch.isOF_enable())
		{
			Statistics.setNumberOfOFStageInstructions(Statistics.getNumberOfOFStageInstructions() + 1);
			int inst = IF_OF_Latch.getInstruction();
			int curr_pc = containingProcessor.getRegisterFile().getProgramCounter();
			String binaryInst =  toBinary(inst, 32);

			OperationType[] operationTypes = OperationType.values(); //getting all operation types from OperationType enum
			int opCodeInt = Integer.parseInt(binaryInst.substring(0, 5), 2);
			OperationType opr = operationTypes[opCodeInt];

			Instruction instr = new Instruction();
			Operand rs1 = new Operand();
			Operand rs2 = new Operand();
			Operand rd = new Operand();
			Operand jump = new Operand();
			Operand imm = new Operand();
			int needtow = 0;
			int chk = 0;
			int regsrc1 = -1;
			int regsrc2 = -1;
			int regdest = -1;
			int immediate = -1;
			instr.setProgramCounter(curr_pc);
			instr.setOperationType(opr);

			//Creating set of branch instructions
			Set<String> BranchInstructions = new HashSet<String>();
			//Adding the branch instructions
			BranchInstructions.add("jmp");
			BranchInstructions.add("beq");
			BranchInstructions.add("bne");
			BranchInstructions.add("blt");
			BranchInstructions.add("bgt");

			if(BranchInstructions.contains(opr.name())){
				IF_EnableLatch.setIF_enable(false);
			}

			if(opr == OperationType.add || opr == OperationType.sub || opr == OperationType.mul || opr == OperationType.div ||
			opr == OperationType.and || opr == OperationType.or || opr == OperationType.xor ||opr == OperationType.slt ||
			opr == OperationType.sll || opr == OperationType.srl || opr == OperationType.sra){ 
					needtow =1; //R3 type
					rs1.setOperandType(Operand.OperandType.Register);
					regsrc1 = Integer.parseInt((binaryInst.substring(5, 10)), 2);
					rs1.setValue(regsrc1);
					
					rs2.setOperandType(Operand.OperandType.Register);
					regsrc2 = Integer.parseInt((binaryInst.substring(10, 15)), 2);
					rs2.setValue(regsrc2);
					
					rd.setOperandType(Operand.OperandType.Register);
					regdest = Integer.parseInt((binaryInst.substring(15, 20)), 2);
					rd.setValue(regdest);
					
					if(isConflict(regsrc1, regsrc2)){
						this.conflictObserved();
						chk = 1;
					}

					if(chk == 0){
					instr.setSourceOperand1(rs1);
					instr.setSourceOperand2(rs2);
					instr.setDestinationOperand(rd);
					}
			}
			else if(opr == OperationType.beq || opr == OperationType.bgt || opr == OperationType.blt || opr == OperationType.bne){
					needtow = 3;
					rs1.setOperandType(Operand.OperandType.Register);
					regsrc1 = Integer.parseInt((binaryInst.substring(5, 10)), 2);
					rs1.setValue(regsrc1);
					
					rs2.setOperandType(Operand.OperandType.Register);
					regsrc2 = Integer.parseInt((binaryInst.substring(10, 15)), 2);
					rs2.setValue(regsrc2);
					
					imm.setOperandType(Operand.OperandType.Immediate);
					immediate = toInteger(binaryInst.substring(15, 32));
					imm.setValue(immediate);
					
					if(isConflict(regsrc1, regsrc2)){
						this.conflictObserved();
						chk = 1;
					}

					if(chk == 0){
					instr.setSourceOperand1(rs1);
					instr.setSourceOperand2(rs2);
					instr.setDestinationOperand(imm);
					}
			}
			else if(opr == OperationType.jmp){
					needtow =2;
					regdest = Integer.parseInt((binaryInst.substring(5, 10)), 2);
					immediate = toInteger(binaryInst.substring(10, 32));
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
				IF_EnableLatch.setIF_enable(false);
			}
			else{
					//for R2I types
					needtow = 4;
					rs1.setOperandType(Operand.OperandType.Register);
					regsrc1 = Integer.parseInt((binaryInst.substring(5, 10)), 2);
					rs1.setValue(regsrc1);
					
					rs2.setOperandType(Operand.OperandType.Immediate);
					immediate = toInteger(binaryInst.substring(15, 32));
					rs2.setValue(immediate);
					
					rd.setOperandType(Operand.OperandType.Register);
					regdest = Integer.parseInt((binaryInst.substring(10, 15)), 2);
					rd.setValue(regdest);
					
					if(isConflict(regsrc1, regsrc1)){
						this.conflictObserved();
						chk = 1;
					}

					if(chk == 0){
					instr.setSourceOperand1(rs1);
					instr.setSourceOperand2(rs2);
					instr.setDestinationOperand(rd);
					}
			}
			if(needtow!=0){
			//System.out.println("Status:" + needtow);
			}
			OF_EX_Latch.setInstruction(instr);
			OF_EX_Latch.setEX_enable(true);
		}
	}
}
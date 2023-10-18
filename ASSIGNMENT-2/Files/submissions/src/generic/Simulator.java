package generic;

import generic.Operand.OperandType;
import java.io.*;
import java.nio.ByteBuffer;

public class Simulator {

	static FileInputStream inputcodeStream = null;

	public static void setupSimulation(String assemblyProgramFile) {
		int firstCodeAddress = ParsedProgram.parseDataSection(assemblyProgramFile);
		ParsedProgram.parseCodeSection(assemblyProgramFile, firstCodeAddress);
		ParsedProgram.printState();
	}

	public static String binary_conv(Operand opr, int len, int x) {
		int val;
		if (opr == null) {
			val = x;
		} else if (opr.getOperandType() == OperandType.Label) {
			val = ParsedProgram.symtab.get(opr.getLabelValue());
		} else {
			val = opr.getValue();
		}
		if (len > 0) {
			return String.format("%" + len + "s", Integer.toBinaryString(val)).replace(" ", "0");
		}
		return null;
	}

	public static void assemble(String objectProgramFile) {
		try {
			// TODO your assembler code
			// 1. open the objectProgramFile in binary mode
			OutputStream op_file = new FileOutputStream(objectProgramFile);
			BufferedOutputStream op_st = new BufferedOutputStream(op_file);
			// 2. write the firstCodeAddress to the file
			byte[] address_code = ByteBuffer.allocate(4).putInt(ParsedProgram.firstCodeAddress).array();
			op_st.write(address_code);
			// 3. write the data to the file
			for (Integer temp_data : ParsedProgram.data) {
				byte[] data = ByteBuffer.allocate(4).putInt(temp_data).array();
				op_st.write(data);
			}
			// 4. assemble one instruction at a time, and write to the file
			for (Instruction ins : ParsedProgram.code) {
				String op_code = "";
				int ins_type = 0; //0 for R3, 1 for R2I type, 2 for RI type
				Instruction.OperationType inst = ins.getOperationType();
				switch (inst.name()) {
					case "add":
						ins_type = 0;
						op_code = "00000";
						break;
					case "addi":
						ins_type = 1;
						op_code = "00001";
						break;
					case "sub":
						ins_type = 0;
						op_code = "00010";
						break;
					case "subi":
						ins_type = 1;
						op_code = "00011";
						break;
					case "mul":
						ins_type = 0;
						op_code = "00100";
						break;
					case "muli":
						ins_type = 1;
						op_code = "00101";
						break;
					case "div":
						ins_type = 0;
						op_code = "00110";
						break;
					case "divi":
						ins_type = 1;
						op_code = "00111";
						break;
					case "and":
						ins_type = 0;
						op_code = "01000";
						break;
					case "andi":
						ins_type = 1;
						op_code = "01001";
						break;
					case "or":
						ins_type = 0;
						op_code = "01010";
						break;
					case "ori":
						ins_type = 1;
						op_code = "01011";
						break;
					case "xor":
						ins_type = 0;
						op_code = "01100";
						break;
					case "xori":
						ins_type = 1;
						op_code = "01101";
						break;
					case "slt":
						ins_type = 0;
						op_code = "01110";
						break;
					case "slti":
						ins_type = 1;
						op_code = "01111";
						break;
					case "sll":
						ins_type = 0;
						op_code = "10000";
						break;
					case "slli":
						ins_type = 1;
						op_code = "10001";
						break;
					case "srl":
						ins_type = 0;
						op_code = "10010";
						break;
					case "srli":
						ins_type = 1;
						op_code = "10011";
						break;
					case "sra":
						ins_type = 0;
						op_code = "10100";
						break;
					case "srai":
						ins_type = 1;
						op_code = "10101";
						break;
					case "load":
						ins_type = 1;
						op_code = "10110";
						break;
					case "store":
						ins_type = 1;
						op_code = "10111";
						break;
					case "jmp":
						ins_type = 2;
						op_code = "11000";
						break;
					case "beq":
						ins_type = 1;
						op_code = "11001";
						break;
					case "bne":
						ins_type = 1;
						op_code = "11010";
						break;
					case "blt":
						ins_type = 1;
						op_code = "11011";
						break;
					case "bgt":
						ins_type = 1;
						op_code = "11100";
						break;
					case "end":
						ins_type = 2;
						op_code = "11101";
						break;
					default:
						op_code = "";
						break;
				}
				String ins_bi = "";
				if (ins_type == 0) {
					String rs1 = binary_conv(ins.getSourceOperand1(), 5, 0);
					String rs2 = binary_conv(ins.getSourceOperand2(), 5, 0);
					String rd = binary_conv(ins.getDestinationOperand(), 5, 0);
					String leftover_bits = binary_conv(null, 12, 0);
					ins_bi += op_code + rs1 + rs2 + rd + leftover_bits;
				} 
				else if (ins_type == 1) {
					String rs1 = binary_conv(ins.getSourceOperand1(), 5, 0);
					String imm_val = "";
					String rs2 = "";
					String rd = "";
					int pc = ins.getProgramCounter();
					if (op_code.equals("11001") || op_code.equals("11010") || op_code.equals("11011")
							|| op_code.equals("11100")) {
						rs2 = binary_conv(ins.getSourceOperand2(), 5, 0);
						imm_val = binary_conv(ins.getDestinationOperand(), 5, 0);
						assert imm_val != null;
						int imm_value = Integer.parseInt(imm_val, 2) - pc;
						String imm_v = binary_conv(null, 17, imm_value);
						String imm = imm_v.substring(imm_v.length() - 17);
						rd = "";
						ins_bi += op_code + rs1 + rs2 + imm;
					} else {
						String imm = "";
						rs2 = binary_conv(ins.getSourceOperand2(), 17, 0);
						rd = binary_conv(ins.getDestinationOperand(), 5, 0);
						ins_bi += op_code + rs1 + rd + rs2;
					}
				} else if (ins_type == 2) {
					Operand rd = ins.getDestinationOperand();
					String leftover_bits = "";
					int pc = ins.getProgramCounter();

					if (op_code.equals("11101")) {
						leftover_bits = binary_conv(null, 27, 0);
						ins_bi += op_code + leftover_bits;
					} 
					else if (op_code.equals("11000")) {
						String rd_val = binary_conv(rd, 5, 0);
						assert rd_val != null;
						int rd_value = Integer.parseInt(rd_val, 2) - pc;
						String rd_v = binary_conv(null, 22, rd_value);
						String rd_vl = rd_v.substring(rd_v.length() - 22);
						leftover_bits = binary_conv(null, 5, 0);
						ins_bi += op_code + leftover_bits + rd_vl;
					}
				} 
				else {
					continue;
				}
				int instr = (int) Long.parseLong(ins_bi, 2);
				byte[] inst_bi = ByteBuffer.allocate(4).putInt(instr).array();
				op_st.write(inst_bi);
			}
			// 5. close the file
			op_st.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}

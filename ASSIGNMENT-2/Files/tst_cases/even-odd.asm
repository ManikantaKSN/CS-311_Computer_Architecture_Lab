	.data
a:
	10
	.text
main:
	load %x0, $a, %x3
	addi %x0, 2, %x4
	div %x3, %x4, %x5
	beq %x0, %x31, evenf
	addi %x0, 1, %x10
	end
evenf:
	subi %x0, 1, %x10
	end
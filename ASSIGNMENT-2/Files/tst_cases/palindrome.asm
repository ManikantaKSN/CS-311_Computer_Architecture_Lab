	.data
a:
	123
	.text
main:
	load $x0, $a, $x3
	addi $3, 0, $x4
	addi $x0, 0, $x5
loop:
	beq $x3, 0, check
	divi $x3, 10, $x3
	muli $x5, 10, $x5
	add $x5, $x31, $x5
	jmp loop
check:
	beq $x4, $x5, asg
	subi $x0, 1, $x10
	end
asg:
	addi $x0, 1, $x10
	end

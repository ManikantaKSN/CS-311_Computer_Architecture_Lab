	.data
a:
	10
	.text
main:
	load $x0, $a, $x3
	beq $x3, 2, ftwo
	addi $x0, 2, $x4
check:
	div $x3, $x4, $x5
	beq $x31, 0, npr
	addi $x4, 1, $x4
	bgt $x3, $x4, check
	addi $x0, 1, $x10
	end
ftwo:
	addi $x0, 1, $x10
	end
npr:
	subi $x0, 1, $x10
	end
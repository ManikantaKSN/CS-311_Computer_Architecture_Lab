	.data
n:
	5
	.text
main:
	load $x0, $n, $x3
	addi $x0, 65535, $x4
	sub $x4, $x3, $x8
	addi $x0, 0, $x5
	addi $x0, 1, $x6
	store $x5, $n, $x4
	beq $x3, 1, endl
	subi $x4, 1, $x4
	store $x6, $n, $x4 
loop:
	subi $x4, 1, $x4
	beq $x4, $x8, endl
	add $x5, $x6, $x7
	store $x7, $n, $x4
	add $x0, $x6, $x5
	add $x0, $x7, $x6
	jmp loop
endl:
	end
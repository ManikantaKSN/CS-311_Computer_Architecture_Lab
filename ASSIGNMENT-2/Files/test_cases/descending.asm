	.data
a:
	70
	80
	40
	20
	10
	30
	50
	60
n:
	8
	.text
main:
	load $x0, $n, $x3
	subi $x3, 1, $x4
	subi $x0, 1, $x5
	addi $x0, 0, $x6
loop:
	addi $x5, 1, $x5
	addi $x5, 0, $x6
	bgt $x4, $x5, loop2
	beq $x4, $x5, endl
	jmp loop
loop2:
	addi $x6, 1, $x6
	bgt $x3, $x6, loop3
	beq $x3, $x6, loop
	jmp loop2
loop3:
	load $x5, $a, $x7
	load $x6, $a, $x8
	bgt $x8, $x7, loop4
	jmp loop2
loop4:
	store $x7, $a, $x6
	store $x8, $a, $x5
	jmp loop2
endl:
	end
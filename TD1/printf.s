      .text
      .globl main
main:
      subq    $8, %rsp        # alignement
      movq  %rsp, %rbp
      movq  $argu1, %rdi
      movq  $42, %rsi
      xorq  %rax, %rax      # %rax = 0 = pas de registres vecteurs
	call	printf
      movq $0, %rax      # sortie avec code 0
      addq  $8, %rsp
	ret
      
      .data
argu1: 
      .string "n = %d\n"
      
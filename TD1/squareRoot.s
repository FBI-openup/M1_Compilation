      .text
      .globl main
main:
    pushq   %rbp
    movq    %rsp, %rbp
    subq    $16, %rsp        # alignement
    movl    $0, -4(%rbp)     # n = 0
.loop2:
    cmpl    $20, -4(%rbp)        # s <= n?
    jg      .end2            # if s > n，end
    add     eax, 1          # c++
    lea     ecx, [eax*2]    # eax 2*c
    add     edx, ecx        # s += 2*c
    add     edx, 1          # s += 1
    jmp     .loop2          

.end2:
    addq    $8, %rsp
    ret                     
    

isqrt:
    xor     eax, eax        # c = 0
    mov     edx, 1          # s = 1

.loop1:
    cmp     edx, edi        # s <= n?
    jg      .end            # if s > n，end
    add     eax, 1          # c++
    lea     ecx, [eax*2]    # eax 2*c
    add     edx, ecx        # s += 2*c
    add     edx, 1          # s += 1
    jmp     .loop1          

.end1:
    ret                     # return c

      .data   
argu1: 
      .string "qrt(%2d) = %2d\n"
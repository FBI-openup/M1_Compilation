CSC_52064 - TD 1 - x86-64 assembly
The goal of this lab is to get some familiarity with x86-64 assembly language, by manually compiling small C programs.
An assembly code is written in a file with suffix .s and looks like this:

      .text
      .globl main
main:
      ...
      mov  $0, %rax       # exit code
      ret
      .data
      ...
You can compile and run such a program as follows:
  gcc -g file.s -o file
  ./file
(Add the option -no-pie if you use gcc version 5 or later.)
When needed, you can use gdb (installed in salles info) to execute your program step by step. Use the following commands

gdb ./file
(gdb) break main
(gdb) run
and then execute one step with command step. More information in this tutorial.
This page by Andrew Tolmach provides some information to write / debug x86-64 assembly code. These notes on x86-64 programming are really useful.

Printing using printf
Compile the following C program:
#include <stdio.h>

int main() {
  printf("n = %d\n", 42);
  return 0;
}
To call the library function printf, we pass its first argument (the format string) in register %rdi and its second argument (here the integer 42) in register %rsi, as specified by the calling conventions. We must also set register %rax to zero before calling printf, since it is a variadic function (in that case, %rax indicates the number of arguments passed in vector registers --- here none).
The format string must be declared in the data segment (.data) using the directive .string that adds a trailing 0-character.

solution
Integer Square Root
Compile the following C program:
#include <stdio.h>

int isqrt(int n) {
  int c = 0, s = 1;
  while (s <= n) {
    c++;
    s += 2*c + 1;
  }
  return c;
}

int main() {
  int n;
  for (n = 0; n <= 20; n++)
    printf("sqrt(%2d) = %2d\n", n, isqrt(n));
  return 0;
}
Try to do the following:
a single branching instruction per loop iteration;
a single instruction to compile the assignment s += 2*c + 1.
solution
A Slightly More Complex (and More Interesting) Program
We are now considering the compilation of a C program to solve the following problem: given this 15x15 integer matrix, determine the maximal sum we can obtain using exactly one element per row and per colum. (This is problem 345 from project Euler.)
The C program matrix.c contains a solution, to be read carefully. This solution uses two main ingredients:

Generalize the problem to a subset of rows and columns. This subset is defined using two integers i and c :
We only consider rows i..14 ;
We only consider columns j for which the bit j in the integer c is 1. (It is an invariant that c has exactly 15-i bits that are set.)
The call f(i, c) returns the maximal sum for the subset defined by i and c.

We use memoization, to avoid recomputing f(i, c) several times. For this purpose, an array memo is declared. We store the result of f(i, c) at index c << 4 | i, when it is computed, and 0 otherwise.
Representing the Matrix
In the C program, the matrix m is declared as follows:
const int m[N][N] = { { 7, 53, ..., 583 }, { 627, 343, ... }, ... };
In the memory layout, integers are stored consecutively, by rows. Each integer is stored on 32 bits and thus the matrix m uses 900 bytes in total. The integer m[i][j] is located at address m + 4 * (15*i + j).
We provide a file matrix.s that contains static data for the matrix m, as well as space for the array memo. The latter is initialized with zeros, and thus can be allocated in section .bss so that it does not increase the size of the executable unnecessarily.

Compiling the program
Compile functions f and main. Regarding function f, we need to allocate registers for local variables key, r, s, etc. If we choose callee-saved registers, we need to restore them before returning. If we choose caller-saved registers, we weed to restore them after a call (if we need their value after the call).
Be careful: to compute 1 << j, one must do a shift whose size is not a constant. To do so, the operand of instruction sal, which is j here, must be placed in the byte register %cl. For this reason, it is a good idea to allocate variable j in register %rcx.


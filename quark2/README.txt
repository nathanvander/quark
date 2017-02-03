README

Quark is my experimental virtual machine.  The list of opcodes are in OpCode.java.

The name is just a random word starting with the letter Q.  I am already familiar with the P-machine (Pascal),
Z-machine, J-machine (Java), and G-machine (Glulx), so this follows in that tradition.

A program in Quark is an array of integers, most of which are opcodes, but they could also
be labels, integer constants, or even encoded Strings.

The entire program can be seen as a function, with the parameters set on the stack which is passed
in and the return value is passed back on the stack.

Internally, the function uses subroutines. A subroutine is a location in the code that starts with the word
LABEL and is followed by a code which identifies it. The parameters to the subroutine are passed in on the stack, and return
values passed back on the stack, the same as a function.  

A subroutine is called with the CALL or LCALL opcodes.  This saves the return address and passes the program
control to the location of the subroutine.  A RET returns from a subroutine.

The next step would be to add PL/0 and TinyBasic compilers.

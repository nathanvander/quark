# quark
Quark is my attempt to build a Glulx Virtual Machine.  

The Glulx specification is at http://www.eblong.com/zarf/glulx/glulx-spec.html.

The reference implementation in C is at https://github.com/erkyrath/glulxe.

This is a refactoring of the Zag code at https://github.com/novalis/zag.  
There is also another version of Zag at https://github.com/Banbury/zag, but my code is based on the novalis branch.

The code is much easier to read than the above implementations, but is still more complicated than it should be. I have only implemented
a few of the opcodes.  It will read a ULX file in and run it until it encounters an opcode that it doesn't understand.  So it would be safe
to say that the code kind of works but is incomplete and buggy.

There are some things I really like about this and some things I hate.

The Glulx platform is very easy to write to, with the most English-like programming language ever - Inform  7 (http://inform7.com/).  
It would be easy to add new opcodes to, since opcodes are up to 4 bytes.

The documentation is poor and incomplete, and the reference applications are a mess of incomprehensible code.  There are a lot of global
variables: sp, pc, fp, vp, lp which constantly change.

Anyways, I have done enough work on this to merit posting, and it may be a useful starting point for someone to improve.

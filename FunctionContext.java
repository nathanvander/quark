package quark;

/**
* This is the program state.  It could be renamed Context,
* but I am holding back in case something else pops up.  Definitely
* do not add any more fields.
*
* From Wikipedia (https://en.wikipedia.org/wiki/Program_counter)
In most processors, the PC is incremented after fetching an instruction, and holds the memory address of ("points to") the next instruction
that would be executed. (In a processor where the incrementation precedes the fetch, the PC points to the current instruction being executed.)

Processors usually fetch instructions sequentially from memory, but control transfer instructions change the sequence by placing a new value in
the PC. These include branches (sometimes called jumps), subroutine calls, and returns. A transfer that is conditional on the truth of some assertion
lets the computer follow a different sequence under different conditions.
*/
public class FunctionContext {
	public int sp;		//Stack Pointer
	public Address pc=new Address(0);		//Program Counter
	public int fp;	//FramePtr?
  	public int vp;
  	public int lp;
	public boolean running;
}
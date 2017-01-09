package quark;

/**
* These originally came from funcs.c.
*
* Since this is an interface, the implementing class is not static
*
*/
public interface Function {
	//added for use with RelativeJump
	public void leaveFunction();

	//returns the stub address for debugging
	public Address popCallstub(int retval);

	//related functions
	public void enterFunction(Address addr,int numargs, int[] args);
	public void pushCallstub(int mode, Address addr);

 	public int[] popArguments(int numargs);

	public StringCallResult popCallstubString();
}
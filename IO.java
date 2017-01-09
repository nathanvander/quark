package quark;

/**
* originated with FunctionManager
*/
public interface IO {

	/**
	* This asks for Zag as input. This is used as follows:
	* z.memory
	* z.fatal
	* passed to htree.readtree
	* z.pushCallstub
	* z.pc
	* z.enterFunction
	* It sure looks like context so that is how we will treat it.
	*/
	//the first parameter is program_counter
	public void streamString(Address pc, int inmiddle, Address addr);
	//io.streamString(this, pc, 2, daddr);

	public void streamNum(int num, boolean started, Address addr);
	//io.streamNum(this, pc, true, daddr);

  	//this doesn't really belong here, but it works
  	public void fatal(String s);

}
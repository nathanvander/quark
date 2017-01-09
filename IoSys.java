package quark;

/**
* stub to use for now.
*/
public class IoSys implements IO {
	public void streamString(Address pc, int inmiddle, Address addr) {
		System.out.println("DEBUG: IoSys.streamString"+pc+","+inmiddle+","+addr.toString());
	}
	//io.streamString(this, pc, 2, daddr);


	public void streamNum(int num, boolean started, Address addr) {
		System.out.println("DEBUG: IoSys.streamNum("+num+","+started+","+addr.toString());
	}
	//io.streamNum(this, pc, true, daddr);

  	//this doesn't really belong here, but it works
  	public void fatal(String s) {
		System.out.println(s);
		System.exit(0);
	}
}
package quark;
import org.p2c2e.util.FastByteBuffer;

/**
* This interface is just for design.  It isn't used anywhere so it could be eliminated.
*/
public interface Operand {
	//storeOperand(memory, dtype, daddr, retval);
	public void storeOperand(int mode,Address addr, int val);

 	public void storeOperand(int mode,Address addr, float val);

	public void storeOperand(int mode, Address addr, double val);


    public void storeShortOperand(int mode,Address addr, short val);

    public void storeByteOperand(int mode,Address addr, byte val);
}
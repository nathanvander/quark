package quark;
import org.p2c2e.util.FastByteBuffer;
/**
* This is another horrible piece of code that I am trying to isolate and pin down.
* It uses out variables, eww.
*
* This looks kind of like FunctionManager, but we want to keep it separate because this is a bad boy.
*
* How many modes and values are needed?  Maximum arity is 8.
*
* For the meanings of modes, see this, copied from http://www.eblong.com/zarf/glulx/glulx-spec_1.html#s.5
*
* The operand addressing modes are a list of fields which tell where opcode arguments are read from or written to.
* Each is four bits long, and they are packed two to a byte. (They occur in the same order as the arguments, low bits first.
* If there are an odd number, the high bits of the last byte are left zero.)
*
* Since each addressing mode is a four-bit number, there are sixteen addressing modes. Each is associated with a fixed number
* of bytes in the "operand data" segment of the instruction. These bytes appear after the addressing modes, in the same order.
* (There is no alignment padding.)

0: Constant zero. (Zero bytes)
1: Constant, -80 to 7F. (One byte)
2: Constant, -8000 to 7FFF. (Two bytes)
3: Constant, any value. (Four bytes)
4: (Unused)
5: Contents of address 00 to FF. (One byte)
6: Contents of address 0000 to FFFF. (Two bytes)
7: Contents of any address. (Four bytes)
8: Value popped off stack. (Zero bytes)
9: Call frame local at address 00 to FF. (One byte)
A: Call frame local at address 0000 to FFFF. (Two bytes)
B: Call frame local at any address. (Four bytes)
C: (Unused)
D: Contents of RAM address 00 to FF. (One byte)
E: Contents of RAM address 0000 to FFFF. (Two bytes)
F: Contents of RAM, any address. (Four bytes)
*/
public class OperandParser {
	//from OpConstants
	  public static final int LOAD = 0;
	  public static final int STORE = 1;

	Global global;
	FastByteBuffer stack;
    FastByteBuffer mem;

	public OperandParser(Global context,FastByteBuffer stack,FastByteBuffer memory) {
		global=context;
		this.stack=stack;
		this.mem=memory;
	}

	//fix this later.  right now I don't care
	public void fatal(String s) {
		System.out.println(s);
	}

	//the original method was
	//void parseOperands(Op op, int[] modes,int[] values)
	public Signature parse(Op op) {
		//if (op.opcode==353) {
		//	System.out.println(op.opcode+", arity="+op.arity);
		//}
		Signature s=new Signature();
		s.opcode=op.opcode;
		s.arity=op.arity;
		s.modes=new int[s.arity];
		s.values=new int[s.arity];

		//i'm trying to keep the code the same as much as possible
    	int i;
    	int rawmode = 0;
    	int arity = op.arity;
    	int[] format = op.format;
    	int modeaddr = mem.position();

    	mem.position(modeaddr + ((arity + 1) / 2));

    	for (i = 0; i < arity; i++)
    	{
    	  if ((i & 1) == 0)
    	  {
    	    rawmode = (int) mem.get(modeaddr);
    	    s.modes[i] = rawmode & 0x0f;
    	  }
	      else
	      {
	        s.modes[i] = (rawmode >> 4) & 0x0f;
	        modeaddr++;
	      }

      if (format[i] == LOAD)
      {
        switch(s.modes[i])
        {
        case 0:
          s.values[i] = 0;
          break;
        case 1:
          s.values[i] = (int) mem.get();
          break;
        case 2:
          s.values[i] = (int) mem.getShort();
          break;
        case 3:
          s.values[i] = mem.getInt();
          break;
        case 5:
          s.values[i] = mem.getInt(((int) mem.get()) & 0xff);
          break;
        case 6:
          s.values[i] = mem.getInt(((int) mem.getShort()) & 0xffff);
          break;
        case 7:
          s.values[i] = mem.getInt(mem.getInt());
          break;
        case 8:
          global.sp = global.sp - 4;
          s.values[i] = stack.getInt(global.sp);
          break;
        case 9:
          s.values[i] = stack.getInt(global.lp + (0xff & ((int) mem.get())));
          break;
        case 10:
          s.values[i] = stack.getInt(global.lp + (0xffff & ((int) mem.getShort())));
          break;
        case 11:
          s.values[i] = stack.getInt(global.lp + mem.getInt());
          break;
        case 13:
          s.values[i] = mem.getInt((((int) mem.get()) & 0xff) + global.ramstart.addr);
          break;
        case 14:
          s.values[i] = mem.getInt((((int) mem.getShort()) & 0xffff) + global.ramstart.addr);
          break;
        case 15:
          s.values[i] = mem.getInt(mem.getInt() + global.ramstart.addr);
          break;
        default:
          fatal("Non-existent addressing mode: " + s.modes[i]);
        }
      }
      else
      {
		  //FORMAT is STORE
		  //this happens only once per instruction, so there is a definite store_param
		  //note that these manipulate the mode
		  s.store_param=i;
		  s.orig_store_mode=s.modes[i];

        switch(s.modes[i])
        {
        case 0:
          s.values[i] = 0;
          break;
        case 8:
          s.modes[i] = 3;
          s.values[i] = 0;
          break;
        case 5:
          s.modes[i] = 1;
          s.values[i] = ((int) mem.get()) & 0xff;
          break;
        case 9:
          s.modes[i] = 2;
          s.values[i] = ((int) mem.get()) & 0xff;
          break;
        case 13:
          s.modes[i] = 1;
          s.values[i] = global.ramstart.addr + (((int) mem.get()) & 0xff);
          break;
        case 6:
          s.modes[i] = 1;
          s.values[i] = ((int) mem.getShort()) & 0xffff;
          break;
        case 10:
          s.modes[i] = 2;
          s.values[i] = ((int) mem.getShort()) & 0xffff;
          break;
        case 14:
          s.modes[i] = 1;
          s.values[i] = global.ramstart.addr + (((int) mem.getShort()) & 0xffff);
          break;
        case 7:
          s.modes[i] = 1;
          s.values[i] = mem.getInt();
          break;
        case 11:
          s.modes[i] = 2;
          s.values[i] = mem.getInt();
          break;
        case 15:
          s.modes[i] = 1;
          s.values[i] = global.ramstart.addr + mem.getInt();
          break;
        default:
          fatal("Non-existent addressing mode (store): " + s.modes[i]);
        } //end switch
        s.store_mode=s.modes[i];
        s.address=new Address(s.values[i]);

      } //end else
    } //end for
    	s.v0=s.values[0];
    	if (s.arity>1) {
			s.v1=s.values[1];
		}
    	if (s.arity>2) {
			s.v1=s.values[2];
		}

    	return s;
    } //end parse

}	//end class
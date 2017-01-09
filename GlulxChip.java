package quark;
import org.p2c2e.util.FastByteBuffer;

/**
* This is a mythical motherboard that you call into.
*
* It could implement an interface, but that will happen as I go along.
*/

public class GlulxChip {
	FunctionManager mgr;

	public GlulxChip(FunctionManager mgr) {
		this.mgr=mgr;
	}

	//NOP = 0x00;
	public void NOP() {}

	//========================================================
	//Group 1 - Basic math operations
	/** This is the first one so I will put more effort into it.
	* Mode is a number from 0..3 that indicates what will be done with the output.
	* Addr is the address of the memory or stack to store, if applicable
	*/
	public void ADD(int mode,Address addr,int val0, int val1) {
		mgr.storeOperand(mode,addr,val0+val1);
	}

    public void SUB(int mode,Address addr,int val0, int val1) {
        mgr.storeOperand(mode,addr,val0-val1);
	}

	public void MUL(int mode,Address addr,int val0, int val1) {
		mgr.storeOperand(mode,addr,val0 * val1);
	}

	public void DIV(int mode,Address addr,int val0, int val1) {
		if (val1==0) {
			mgr.fatal("Division by zero.");
		} else {
			mgr.storeOperand(mode,addr,val0 / val1);
		}
	}

	public void MOD(int mode,Address addr,int val0, int val1) {
		if (val1==0) {
			mgr.fatal("Modulus by zero.");
		} else {
			mgr.storeOperand(mode,addr,val0 % val1);
		}
	}

	public void NEG(int mode,Address addr,int val0) {
		mgr.storeOperand(mode,addr,0 - val0);
	}

	//===============================================
	//Group 2 - Bitwise operations

	public void BITAND(int mode,Address addr,int val0, int val1) {
		mgr.storeOperand(mode,addr,val0 & val1);
	}

	public void BITOR(int mode,Address addr,int val0, int val1) {
		mgr.storeOperand(mode,addr,val0 | val1);
	}

	public void BITXOR(int mode,Address addr,int val0, int val1) {
		mgr.storeOperand(mode,addr,val0 ^ val1);
	}

	public void BITNOT(int mode,Address addr,int val0) {
		mgr.storeOperand(mode,addr,~val0);
	}

	//shift left
	public void SHIFTL(int mode,Address addr,int val0, int val1) {
		int val=((val1 & 0xff) > 31) ? 0 : val0 << (val1 & 0xff);
		mgr.storeOperand(mode, addr, val);
	}

	//unsigned shift right
	public void USHIFTR(int mode,Address addr,int val0, int val1) {
		int val = ((val1 & 0xff) > 31) ? 0 : val0 >>> (val1 & 0xff);
		mgr.storeOperand(mode, addr, val);
	}

	//signed shift right
	public void SSHIFTR(int mode,Address addr,int val0, int val1) {
		int val=0;
        if (val1 >= 32) {
            val = (val0 & 0x80000000) == 0 ? 0 : 0xffffffff;
        } else {
            val = val0 >> (val1 & 0xff);
        }
        mgr.storeOperand(mode, addr, val);
	}

	//==============================
	//jumps
	//JUMP = 0x20;
	public void JUMP(int offset) {
		RelativeJump.handleRelativeJump(mgr.context,mgr,offset);
	}

	public void JZ(int val0,int offset) {
		if (val0 == 0) {
          	RelativeJump.handleRelativeJump(mgr.context,mgr,offset);
	  	} //else NOP
	}

	public void JNZ(int val0,int offset) {
		if (val0 != 0) {
          	RelativeJump.handleRelativeJump(mgr.context,mgr,offset);
	  	} //else NOP
	}

	public void JEQ(int val0,int val1,int offset) {
		if (val0 == val1) {
          	RelativeJump.handleRelativeJump(mgr.context,mgr,offset);
	  	} //else NOP
	}

	public void JNE(int val0,int val1,int offset) {
        if (val0 != val1) {
          	RelativeJump.handleRelativeJump(mgr.context,mgr,offset);
	  	} //else NOP
	}

	public void JLT(int val0,int val1,int offset) {
        if (val0 < val1) {
          	RelativeJump.handleRelativeJump(mgr.context,mgr,offset);
	  	} //else NOP
	}

	public void JLE(int val0,int val1,int offset) {
        if (val0 <= val1) {
          	RelativeJump.handleRelativeJump(mgr.context,mgr,offset);
	  	} //else NOP
	}

	public void JGT(int val0,int val1,int offset) {
        if (val0 > val1) {
          	RelativeJump.handleRelativeJump(mgr.context,mgr,offset);
	  	} //else NOP
	}

	//JGE = 0x27;
	public void JGE(int val0,int val1,int offset) {
        if (val0 >= val1) {
          	RelativeJump.handleRelativeJump(mgr.context,mgr,offset);
	  	} //else NOP
	}

	//JLTU = 0x2a;
	//compare as unsigned value
	public void JLTU(int val0,int val1,int offset) {
        if (((long) val0 & 0xffffffffl) < ((long) val1 & 0xffffffffl)) {
			RelativeJump.handleRelativeJump(mgr.context,mgr,offset);
	  	} //else NOP
	}

	//JLEU = 0x2d;
	public void JLEU(int val0,int val1,int offset) {
        if (((long) val0 & 0xffffffffl) <= ((long) val1 & 0xffffffffl)) {
			RelativeJump.handleRelativeJump(mgr.context,mgr,offset);
	  	} //else NOP
	}

	//JGTU = 0x2c;
	public void JGTU(int val0,int val1,int offset) {
        if (((long) val0 & 0xffffffffl) > ((long) val1 & 0xffffffffl)) {
			RelativeJump.handleRelativeJump(mgr.context,mgr,offset);
	  	} //else NOP
	}

	//JGEU = 0x2b;
	public void JGEU(int val0,int val1,int offset) {
        if (((long) val0 & 0xffffffffl) >= ((long) val0 & 0xffffffffl)) {
			RelativeJump.handleRelativeJump(mgr.context,mgr,offset);
	  	} //else NOP
	}
	//-----------------------------------------------------
	//CALL = 0x30;
	//call L1 L2 S1
	//Call function whose address is L1, passing in L2 arguments, and store the return result at S1.
	//The arguments are taken from the stack. Before you execute the call opcode, you must push the arguments on, in backward order
	//(last argument pushed first, first argument topmost on the stack.) The L2 arguments are removed before the new function's call
	//frame is constructed. (If L1, L2, or S1 use the stack pop/push modes, the arguments are taken after L1 or L2 is popped, but before
	//the result is pushed.)
	//
	//Note: I don't thing the above comments are accurate.  The results are not stored.  I guess they are put back on the stack
	public void CALL(Address funcAddress,int numargs,int mode,Address stubAddress) {
		System.out.print("at "+mgr.context.pc);
		System.out.println(" calling function "+funcAddress+" with a return address of "+stubAddress);
        int[] funargs=mgr.popArguments(numargs);
        mgr.pushCallstub(mode, stubAddress);
        mgr.enterFunction(funcAddress, numargs, funargs);
	}

	public void RETURN(int offset) {

        mgr.leaveFunction();
        if (mgr.context.sp == 0) {
          	mgr.context.running = false;
        } else {
          	Address a=mgr.popCallstub(offset);
          	//System.out.println("returning to "+a);
		}
	}

	//------------------------------------------------
	//goto!
	//JUMPABS = 0x104;
	public void JUMPABS(Address pc) {
		mgr.context.pc=pc;
	}

	//COPY = 0x40;
	//copy L1 S1
	//Read L1 and store it at S1, without change.
	public void COPY(int mode,Address addr,int val0) {
		mgr.storeOperand(mode,addr,val0);
	}

	//COPYS = 0x41;
    //Read a 16-bit value from L1 and store it at S1.
    public void COPYS(int mode,Address addr,short s) {
		mgr.storeShortOperand(mode,addr,s);
	}

	//COPYB = 0x42;
    public void COPYB(int mode,Address addr,byte b) {
		mgr.storeShortOperand(mode,addr,b);
	}

	//SEXS = 0x44;
	//sexs L1 S1
	//Sign-extend a value, considered as a 16-bit value. If the value's 8000 bit is set, the upper 16 bits are all set;
	//otherwise, the upper 16 bits are all cleared.
	public void SEXS(int mode,Address addr,int val0) {
      	int val = ((val0 & 0x8000) != 0)
          ? (val0 | 0xffff0000) : (val0 & 0x0000ffff);
        mgr.storeOperand(mode,addr, val);
 	}

	//SEXB = 0x45;
	public void SEXB(int mode,Address addr,int val0) {
    	int val = ((val0 & 0x80) != 0)
          ? (val0 | 0xffffff00) : (val0 & 0x000000ff);
        mgr.storeOperand(mode,addr, val);
	}

	//aload L1 L2 S1
	//Load a 32-bit value from main memory address (L1+4*L2), and store it in S1.
	//ALOAD = 0x48;
	public void ALOAD(int mode,Address addr,int L1,int L2) {
		//I'm going to use an address just to emphasize it.  it could be optimized out
		Address aval=new Address(L1+4*L2);
		int ival=mgr.mem.getInt(aval.addr);
		mgr.storeOperand(mode,addr,ival);
	}

	//aloads L1 L2 S1
	//Load an 16-bit value from main memory address (L1+2*L2), and store it in S1.
	//ALOADS = 0x49;
	public void ALOADS(int mode,Address addr,int L1,int L2) {
		Address aval=new Address(L1+2*L2);
		short sval=mgr.mem.getShort(aval.addr);
		int v=(int)sval;
		mgr.storeOperand(mode,addr,v);
	}
		//mgr.storeShortOperand(mode,addr,sval);
      	//this is the code I am modifying
        //storeOperand(mem, modes[2], values[2],((int) mem.getShort(values[0] + (2 * values[1]))) & 0xffff);
        //we have storeShortOperand, and might as well use it.  or does it not work right?

	public void ASTORE(Address a,int v) {
    	mgr.storeOperand(1, a, v);
	}

	public void ASTORES(Address a,short s) {
        mgr.storeShortOperand(1,a,s);
	}

	public void ASTOREB(Address a,byte b) {
        mgr.storeByteOperand(1,a,b);
	}


	//---------------------------------------
	//public static final int GETMEMSIZE = 0x102;
	public void GETMEMSIZE(int mode,Address addr) {
		int memsize=mgr.mem.limit();
        mgr.storeOperand(mode, addr, memsize);
	}
	//by special demand
	//ox161
	public void CALLF(Address funcAddress,int mode,Address stubAddr) {
		System.out.print("at "+mgr.context.pc);
		System.out.println(" calling function "+funcAddress+" with a return address of "+stubAddr);

		 mgr.pushCallstub(mode,stubAddr);
		 mgr.enterFunction(funcAddress,0,null);
	}

	//ox161
	public void CALLFI(Address funcAddress,int arg1,int mode,Address stubAddr) {
		System.out.print("at "+mgr.context.pc);
		System.out.println(" calling function "+funcAddress+" with a return address of "+stubAddr);

		 int[] funcargs=new int[1];
		 funcargs[0]=arg1;
		 mgr.pushCallstub(mode,stubAddr);
		 mgr.enterFunction(funcAddress,1,funcargs);
	}

	//0x162;
	 public void CALLFII(Address funcAddress,int arg1,int arg2,int mode,Address stubAddr) {
		System.out.print("at "+mgr.context.pc);
		System.out.println(" calling function "+funcAddress+" with a return address of "+stubAddr);

		 int[] funcargs=new int[2];
		 funcargs[0]=arg1;
		 funcargs[1]=arg2;
		 mgr.pushCallstub(mode,stubAddr);
		 mgr.enterFunction(funcAddress,2,funcargs);
	 }

}

 /**
      case ALOADB:
        storeOperand(mem, modes[2], values[2],
                     ((int) mem.get(values[0] + values[1])) & 0xff);
        break;
      case ALOADBIT:
        addr = values[0] + (values[1] / 8);
        val = values[1] % 8;

        if (val < 0)
        {
          addr--;
          val += 8;
        }

        storeOperand(mem, modes[2], values[2],
                     ((mem.get(addr) & (byte) (1 << val)) != 0) ? 1 : 0);
        break;

      case ASTOREBIT:
        addr = values[0] + (values[1] / 8);
        val = values[1] % 8;

        if (val < 0)
        {
          addr--;
          val += 8;
        }

        if (values[2] == 0)
          mem.put(addr,(byte) (mem.get(addr) & ((byte) ~(1 << val))));
        else
          mem.put(addr, (byte) (mem.get(addr) | (byte) (1 << val)));
        break;
      case STKCOUNT:
        storeOperand(mem, modes[0], values[0], (sp - vp) / 4);
        break;
      case STKPEEK:
        if (values[0] < 0 || values[0] >= ((sp - vp) / 4))
          fatal("stkpeek: outside valid stack range");

        storeOperand(mem, modes[1], values[1],
                     stack.getInt(sp - (4 * (values[0] + 1))));
        break;
      case STKSWAP:
        if (sp - vp < 8)
          fatal("Must be at least two values on the stack to execute stkswap.");

        val = stack.getInt(sp - 4);
        addr = stack.getInt(sp - 8);
        stack.putInt(sp - 8, val);
        stack.putInt(sp - 4, addr);
        break;
      case STKCOPY:
        if (sp - vp < 4 * values[0])
          fatal("Cannot copy " + values[0] + " stack items.  Stack too small.");
        for (i = 0; i < values[0]; i++)
          stack.putInt(sp + (4 * i), stack.getInt(sp - (4 * (values[0] - i))));
        sp += 4 * values[0];
        break;
      case STKROLL:
        if (values[0] < 0)
          fatal("Cannot roll negative number of stack entries.");
        if (((sp - vp) / 4) < values[0])
          fatal("Cannot roll more stack values than there are on the stack.");

        /* Algorithm thanks to Andrew Plotkin...

        if (values[0] == 0)
          break;

        if (values[1] > 0)
          val = values[0] - (values[1] % values[0]);
        else
          val = (-values[1]) % values[0];

        if (val == 0)
          break;

        addr = sp - (4 * values[0]);
        for (i = 0; i < val; i++)
          stack.putInt(sp + (4 * i), stack.getInt(addr + (4 * i)));
        for (i = 0; i < values[0]; i++)
          stack.putInt(addr + (4 * i), stack.getInt(addr + (4 * (val + i))));
        break;


      case TAILCALL:
        popArguments(values[1]);
        leaveFunction();
        enterFunction(mem, values[0], values[1], funargs);
        break;
      case CATCH:
        pushCallstub(modes[0], values[0]);
        storeOperand(mem, modes[0], values[0], sp);
        handleRelativeJump(values[1]);
        break;
      case THROW:
        sp = values[1];
        popCallstub(values[0]);
        break;
       case STREAMCHAR:
        io.streamChar(this, values[0] & 0xff);
        break;
      case STREAMNUM:
        io.streamNum(this, values[0], false, 0);
        break;
      case STREAMSTR:
        io.streamString(this, values[0], 0, 0);
        break;
      case STREAMUNICHAR:
        io.streamUniChar(this, values[0]);
        break;
 */

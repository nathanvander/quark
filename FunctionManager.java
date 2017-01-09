package quark;
import org.p2c2e.util.FastByteBuffer;

/**
* class FunctionManager.  I would prefer to make everything static, but I am
* following the rhythm of the code.
*
* This code is based originally on funcs.c
*
* This makes calls to IO, which obviously look like another interface
*
* It also calls: storeOperand, which looks like yet another interface.
*
* This does not use Accelerated Functions.  The code is complicated enough.  They can be
* added in later, if desired.
*
* I combined this with OperandManager, because the code is similar
*/
public class FunctionManager implements Function, Operand {
	FunctionContext context;
	FastByteBuffer stack;
	FastByteBuffer mem;
	IO io;

	public FunctionManager(FunctionContext context,FastByteBuffer stack,FastByteBuffer memory,IO io) {
		this.context=context;
		this.stack=stack;
		mem=memory;
		this.io=io;
	}

	//-------------
	//not part of the interface.  convenience method for the function calls
	public void fatal(String s) {
		io.fatal(s);
	}
	//-----------------


	//final void leaveFunction()
	public void leaveFunction()
	{
	    context.sp = context.fp;

	    System.out.println("+----------------LEAVE FUNCTION----------------------------+");
	    System.out.println("| Stack Pointer = "+ (context.sp));
	    System.out.println("+----------------LEAVE FUNCTION----------------------------+");
  	}

  	//final void popCallstub(int retval)
  	//note that retval could also be the offset
  	public Address popCallstub(int retval)
  {

    int dtype;
    Address daddr=new Address();

    context.sp -= 16;
    context.fp = stack.getInt(context.sp + 12);
    context.pc.addr = stack.getInt(context.sp + 8);
    daddr.addr = stack.getInt(context.sp + 4);
    dtype = stack.getInt(context.sp);

	  System.out.println("+----------------POP CALL STUB----------------------------+");
	  System.out.println("| Beginning Stack Pointer = "+ (context.sp-16));
	  System.out.println("| FramePtr = "+context.fp);
	  System.out.println("| ProgramCtr = "+context.pc);
	  System.out.println("| DestAddr = "+daddr);
	  System.out.println("| DestType = "+ dtype+": "+describeMode(dtype));
	  System.out.println("| Return Value = "+retval);
	  System.out.println("| Ending Stack Pointer = "+context.sp);
	  System.out.println("+----------------POP CALL STUB----------------------------+");

    context.vp = context.fp + stack.getInt(context.fp);
    context.lp = context.fp + stack.getInt(context.fp + 4);

    if (context.sp < context.vp)
      io.fatal("while popping callstub, sp=" + context.sp + "; vp=" + context.vp);

	//question.  I though DestType was a number from 0..3.  Now it goes up to 15?

    switch(dtype)
    {
    case 0x10:
      //io.streamString(this, pc, 2, daddr);
      io.streamString(context.pc, 2, daddr);
      break;
    case 0x11:
      io.fatal("String terminator callstub found at end of function call.");
      break;
    case 0x12:
      //io.streamNum(this, pc, true, daddr);
      //this is going to require more experimentation
      //as best I can tell, this is asking for the address as a number so it can print it out
      io.streamNum(context.pc.addr, true, daddr);
      break;
    case 0x13:
      io.streamString(context.pc, 1, daddr);
      break;
    case 0x14:
      io.streamString(context.pc, 3, daddr);
      break;
    default:
      //op.storeOperand(mem, dtype, daddr, retval);
      storeOperand(dtype, daddr, retval);
    }

    return daddr;
  }

  //------------------------------------------
  //this is called by IO
    public StringCallResult popCallstubString()
    {
      StringCallResult r = new StringCallResult();
      int desttype=0;
      Address destaddr=new Address();
      Address newpc=new Address();

      context.sp -= 16;
      desttype = stack.getInt(context.sp);
      destaddr.addr = stack.getInt(context.sp + 4);
      newpc.addr = stack.getInt(context.sp + 8);

      context.pc = newpc;

      if (desttype == 0x11)
      {
        r.pc.addr = 0;
        r.bitnum.addr = 0;
        return r;
      }

      if (desttype == 0x10)
      {
        r.pc = context.pc;
        r.bitnum = destaddr;
        return r;
      }

      io.fatal("Function terminator call stub at the end of a string.");
      return null;
  }
  //--------------------------------------------
  public void pushCallstub(int mode, Address address)
  {
	//this provides more debugging info
	  System.out.println("+----------------PUSH CALL STUB----------------------------+");
	  System.out.println("| Beginning Stack Pointer = "+context.sp);
	  System.out.println("| DestType = "+ mode+": "+describeMode(mode));
	  System.out.println("| DestAddr = "+address);
	  System.out.println("| ProgramCtr = "+context.pc);
	  System.out.println("| FramePtr = "+context.fp);
	  System.out.println("| Ending Stack Pointer = "+(context.sp+16));
	  System.out.println("+----------------PUSH CALL STUB----------------------------+");

    stack.putInt(context.sp, mode);
    stack.putInt(context.sp + 4, address.addr);
    stack.putInt(context.sp + 8, context.pc.addr);
    stack.putInt(context.sp + 12, context.fp);
    context.sp += 16;
  }

  public String describeMode(int mode) {
	  if (mode==0) {
		  return "Do not store. The result value is discarded. DestAddr should be zero.";
	  } else if (mode==1) {
		  return "Store in main memory. The result value is stored in the main-memory address given by DestAddr.";
	  } else if (mode==2) {
		  return "Store in local variable. The result value is stored in the call frame at position ((FramePtr+LocalsPos) + DestAddr).";
	  } else if (mode==3) {
		  return "Push on stack. The result value is pushed on the stack. DestAddr should be zero.";
	  }
	  return "unknown mode";
  }
  //-----------------------------------------
  //I am going to document the shit out of this
  public void enterFunction(Address address,int numargs, int[] args) {
	  System.out.println("+----------------CALL FRAME-------------------------------+");
	  System.out.println("| Function Address ="+address);
	  System.out.println("| Beginning Stack Pointer = "+context.sp);
	  System.out.println("| Beginning Frame Pointer = "+context.fp);
	  System.out.println("| Number of Arguments = "+numargs);
	  //System.out.print("with "+numargs+" arguments: ");
	  //for (int i=0;i<numargs;i++) {
		//  System.out.print(args[i]+", ");
	  //}
	  //System.out.println();

	//get rid of some complexity here
    //AcceleratedFunction func = (AcceleratedFunction) accelTable.get(addr);
    //if (func != null) {
    //    int retval = funcontext.enterFunction(numargs, args);
    //    popCallstub(retval);
    //    return;
    //}

    int ltype, lnum;
    int format, local;
    int i, j;
    int len = 0;
    int funtype = ((int) mem.get(address.addr++)) & 0xff;
	//System.out.println("function type = "+Integer.toHexString(funtype));
	System.out.println("| Function Type = "+Integer.toHexString(funtype));
    if (funtype != 0xc0 && funtype != 0xc1)
    {
      if (funtype >= 0xc0 && funtype <= 0xdf)
        io.fatal("Unknown type of function.");
      else
        io.fatal("Attempt to call non-function.");
    }

    context.fp = context.sp;

    //figure out the length of the locals
    i = 0;
    while (true)
    {
      ltype = ((int) mem.get(address.addr++)) & 0xff;
      System.out.println("| ["+(context.fp + 8 + (2 * i))+"] Locals Type = "+ltype);
      lnum = ((int) mem.get(address.addr++)) & 0xff;
      System.out.println("| ["+(context.fp + 8 + (2 * i)+1)+"] Locals Num = "+lnum);
      stack.put(context.fp + 8 + (2 * i), (byte) ltype);
      stack.put(context.fp + 8 + (2 * i) + 1, (byte) lnum);
      i++;

      if (ltype == 0)
      {
        if ((i & 1) != 0)
        {
			//padding
          stack.put(context.fp + 8 + (2 * i), (byte) 0);
          stack.put(context.fp + 8 + (2 * i) + 1, (byte) 0);
          i++;
        }
        break;
      }

      if (ltype == 4)
      {
        while ((len & 3) != 0)
          len++;
      }
      else if (ltype == 2)
      {
        while ((len & 1) != 0)
          len++;
      }

      len += ltype * lnum;
    } //end while

    System.out.println("| Len = "+len);
    System.out.println("| Stack Pointer = "+context.sp);
	System.out.println("+-------------------------------");
	System.out.println("| LOCALS SECTION");
    while ((len & 3) != 0)
      len++;

    context.lp = context.fp + 8 + (2 * i);
    context.vp = context.lp + len;

    stack.putInt(context.fp, 8 + (2 * i) + len);
    System.out.println("| vp = "+context.vp);
    System.out.println("| ["+context.fp+"] vp2 = " + (8 + (2 * i) + len));
    stack.putInt(context.fp + 4, 8 + (2 * i));
	System.out.println("| lp = "+context.lp);
	System.out.println("| ["+(context.fp+4)+"] lp2 = "+(8 + (2 * i)));
    context.sp = context.vp;
    context.pc = address;

	//padding
    for (j = 0; j < len; j++) {
      stack.put(context.lp + j, (byte) 0);
    }

	System.out.println("+-------------------------------");
	System.out.println("| VALUES SECTION");

    //put locals on stack according to function's format
    if (funtype == 0xc0)
    {
      for (j = numargs - 1; j >=0; j--)
      {
        stack.putInt(context.sp, args[j]);
        System.out.println("| ["+context.sp+"] arg("+j+") ="+args[j]);
        context.sp += 4;
      }
      stack.putInt(context.sp, numargs);
      System.out.println("| ["+context.sp+"] numargs ="+numargs);
      context.sp += 4;
    }
    else
    {
      format = context.fp + 8;
      local = context.lp;
      i = 0;
      while (i < numargs)
      {
        ltype = ((int) stack.get(format++)) & 0xff;
        lnum = ((int) stack.get(format++)) & 0xff;
        if (ltype == 0)
          break;
        if (ltype == 4)
        {
          while ((local & 3) != 0)
            local++;
          while (i < numargs && lnum != 0)
          {
            stack.putInt(local, args[i++]);
            System.out.println("| ["+local+"] (int) args("+(i-1)+") ="+args[i-1]);
            local += 4;
            lnum--;
          }
        }
        else if (ltype == 2)
        {
          while ((local & 1) != 0)
            local++;
          while (i < numargs && lnum != 0)
          {
            stack.putShort(local, (short) (args[i++] & 0xffff));
            System.out.println("| ["+local+"] (short) args("+(i-1)+") ="+args[i-1]);
            local += 2;
            lnum--;
          }
        }
        else
        {
          while (i < numargs && lnum != 0)
          {
            stack.put(local, (byte) (args[i++] & 0xff));
            System.out.println("| ["+local+"] (byte) args("+(i-1)+") ="+args[i-1]);
            local++;
            lnum--;
          }
        }
      }
    }

	  System.out.println("| Ending Frame Pointer = "+context.fp);
	  System.out.println("| Ending Stack Pointer = "+context.sp);
	  System.out.println("+------------END CALL FRAME--------------------------------+");
  }
  //-----------------
  //from OperandManager
	//public void storeOperand( int mode,int addr, int val) {
	//we put an address object here to make it clear that it is an address
	public void storeOperand( int mode,Address address, int val) {
    	switch(mode)
    	{
    case 0:
      break;
    case 1:
      mem.putInt(address.addr, val);
      break;
    case 2:
      stack.putInt(address.addr + context.lp, val);
      break;
    case 3:
      stack.putInt(context.sp, val);
      context.sp += 4;
      break;
    default:
      io.fatal("storing illegal operand");
    	}
  	}

	//---------------------------
	  public void storeOperand(int mode,Address address, float val) {

	      storeOperand(mode, address, Float.floatToRawIntBits(val));
	  }

	  public void storeOperand(int mode,Address address, double val) {

	      storeOperand(mode, address, Float.floatToRawIntBits((float)val));
  	}

	//---------------------------------
    public void storeShortOperand(int mode,Address address, short s)
    {
      //short s = (short) val;

      switch(mode)
      {
      case 0:
        break;
      case 1:
        mem.putShort(address.addr, s);
        break;
      case 2:
        stack.putShort(address.addr + context.lp, s);
        break;
      case 3:
        stack.putInt(context.sp, ((int) s) & 0xffff);
        context.sp += 4;
        break;
      default:
        io.fatal("storing illegal operand");
      }
    }

    public void storeByteOperand(int mode,Address address, byte b)
    {
      //byte b = (byte) val;

      switch(mode)
      {
      case 0:
        break;
      case 1:
        mem.put(address.addr, b);
        break;
      case 2:
        stack.put(address.addr + context.lp, b);
        break;
      case 3:
        stack.putInt(context.sp, ((int) b) & 0xff);
        context.sp += 4;
        break;
      default:
        io.fatal("storing illegal operand");
      }
  }

  //-------------------------------------------------
  public int[] popArguments(int numargs)
  {
    if (context.sp < (context.vp + (4 * numargs))) {
      	io.fatal("Attempting to pop too many [" + numargs +
            "] function arguments.  sp=" + context.sp + "; vp=" + context.vp);
 	}

 	int[] funargs=new int[numargs];


    for (int i = 0; i < numargs; i++)
    {
      context.sp -= 4;
      funargs[i] = stack.getInt(context.sp);
    }
    return funargs;
  }

}
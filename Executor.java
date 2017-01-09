package quark;
import org.p2c2e.util.FastByteBuffer;
import org.p2c2e.zag.OpConstants;

/**
* This has the massive switch statement.  This could be in a thread.
*/
public class Executor implements Runnable,OpConstants {
	//passed in objects
	Global context;
	FastByteBuffer stack;
    FastByteBuffer mem;
    IO io;
    FunctionManager mgr;

    //newly created objects
	OperandParser parser;
	GlulxChip chip;

	public Executor(Global c,FastByteBuffer stack,FastByteBuffer memory,IO io,FunctionManager mgr) {
		if (c==null || stack==null || io==null || mgr==null) {
			throw new IllegalArgumentException("parameters may not be null");
		}
		if (c.pc==null) {
			throw new IllegalArgumentException("pc is null");
		}

		this.context=c;
		this.stack=stack;
		this.mem=memory;
		this.io=io;
		this.mgr=mgr;

		//set memory position to pc
		mem.position(context.pc.addr);

		//create an OperandParser
		parser=new OperandParser(context,stack,memory);

		//create glulx
		chip=new GlulxChip(mgr);
	}

	public int getOpCode() {
		//System.out.println("Executor.getOpCode(): PC="+context.pc.addr+" ("+context.pc+")");
		//System.out.println("Executor.getOpCode(): mem.position="+mem.position());
		if (context.pc.addr!=mem.position()) {
			System.out.println("warning: context.pc.addr!=mem.position()");
		}
      	int opcode = ((int)mem.get()) & 0xff;
      	if ((opcode & 0x80) != 0)
      	//if the first byte is at least 128
      	{
			//4-byte opcode
        	if ((opcode & 0x40) != 0)
        	//if the first byte is at least 192
        	{
        	  opcode &= 0x3f;
        	  opcode = (opcode << 8) | ((int)mem.get()) & 0xff;
        	  opcode = (opcode << 8) | ((int)mem.get()) & 0xff;
        	  opcode = (opcode << 8) | ((int)mem.get()) & 0xff;
        	  return opcode;
        	}
        	else
        	{
			  //2-byte opcode
			  //the first byte is in the range 128..191
        	  opcode &= 0x7f;
        	  //add 127 to it
        	  opcode = (opcode << 8) | ((int)mem.get()) & 0xff;
        	  return opcode;
        	}
      	}
      	else
      	{
			//1 byte opcode
			return opcode;
		}
	}

	//is exec.c this is called: execute_loop()
	//in Zag, this is called exec()
	//this is slightly short thatn Zag.exec, which has
	//about 750 lines of code
	public void run() {

		int opcode=0;
		Signature signature=null;	//the current signature

		while(context.running) {
			mem.position(context.pc.addr);
			//get the next opcode
			opcode=getOpCode();

      		if (Op.OPS[opcode] == null) {
      		    System.out.println("bad op: " + opcode+" (0x"+Integer.toHexString(opcode)+")");
      		    throw new IllegalStateException("opcode 0x"+Integer.toHexString(opcode));
      		}
      		//load meta-data about the op

      		if (Op.OPS[opcode].format != null) {
	 	    	signature=parser.parse(Op.OPS[opcode]);
				context.pc = new Address(mem.position());
		   	}

			if (opcode<128) {
				switch_group_one(opcode,signature);
			} else {
				switch_group_two(opcode,signature);
			}


		}
	}


	public void switch_group_one(int opcode,Signature s) {
		//System.out.println("Executor.switch_group_one calling opcode "+opcode);
      		switch(opcode) {
 case NOP:
        break;
 case ADD:
 		chip.ADD(s.store_mode,s.address, s.v0, s.v1); break;
 case SUB:
  		chip.SUB(s.store_mode,s.address,s.v0,s.v1); break;
 case MUL:
  		chip.MUL(s.store_mode,s.address,s.v0,s.v1); break;
 case DIV:
 		chip.DIV(s.store_mode,s.address,s.v0,s.v1); break;
 case MOD:
 		chip.MOD(s.store_mode,s.address,s.v0,s.v1); break;
 case NEG:
 		chip.NEG(s.store_mode,s.address,s.v0); break;
case BITAND:
		chip.BITAND(s.store_mode,s.address,s.v0,s.v1); break;
case BITOR:
		chip.BITOR(s.store_mode,s.address,s.v0,s.v1); break;
case BITNOT:
		chip.BITNOT(s.store_mode,s.address,s.v0); break;
case SHIFTL:
 		chip.SHIFTL(s.store_mode,s.address,s.v0,s.v1); break;
case SSHIFTR:
 		chip.SSHIFTR(s.store_mode,s.address,s.v0,s.v1); break;
case USHIFTR:
 		chip.USHIFTR(s.store_mode,s.address,s.v0,s.v1); break;
case JUMP:
		chip.JUMP(s.v0); break;
case JZ:
		chip.JZ(s.v0,s.v1); break;
case JNZ:
		chip.JNZ(s.v0,s.v1); break;
case JEQ:
		chip.JEQ(s.v0,s.v1, s.v2); break;
case JNE:
		chip.JNE(s.v0,s.v1, s.v2); break;
case JLT:
		chip.JLT(s.v0,s.v1, s.v2); break;
case JGE:
		chip.JGE(s.v0,s.v1, s.v2); break;
case JGT:
		chip.JGT(s.v0,s.v1, s.v2); break;
case JLE:
		chip.JLE(s.v0,s.v1, s.v2); break;
case JLTU:
		chip.JLTU(s.v0,s.v1, s.v2); break;
case JGEU:
		chip.JGEU(s.v0,s.v1, s.v2); break;
case JGTU:
		chip.JGTU(s.v0,s.v1, s.v2); break;
case JLEU:
		chip.JLEU(s.v0,s.v1, s.v2); break;
case CALL:
		//System.out.println("preparing to CALL");
		Address funcAddress=new Address(s.v0);
		int numargs=s.v1;
		int mode=s.modes[2];
		Address stubAddress=new Address(s.values[2]);
		chip.CALL(funcAddress,numargs,mode,stubAddress);
		break;
case RETURN:
		chip.RETURN(s.v0); break;
//COPY = 0x40;
case COPY:
		chip.COPY(s.store_mode,s.address,s.v0); break;

case ALOAD:
		chip.ALOAD(s.store_mode,s.address,s.v0,s.v1); break;
case ALOADS:
		chip.ALOADS(s.store_mode,s.address,s.v0,s.v1); break;

case ASTORE:
        chip.ASTORE(new Address(s.values[0] + (4 * s.values[1])), s.values[2]); break;
case ASTORES:
        chip.ASTORES(new Address(s.values[0] + (2 * s.values[1])), (short)s.values[2]); break;
case ASTOREB:
        chip.ASTOREB(new Address(s.values[0] + s.values[1]), (byte)s.values[2]); break;

  //public static final int CATCH = 0x32;
  //public static final int THROW = 0x33;
  //public static final int TAILCALL = 0x34;
   //public static final int COPY = 0x40;
 // public static final int COPYS = 0x41;
 // public static final int COPYB = 0x42;
 // public static final int SEXS = 0x44;
 // public static final int SEXB = 0x45;

 default:
 		System.out.println("unknown opcode "+Integer.toHexString(opcode));

 		//nothing to do
			}  //end switch


	}	//end switch_group_one


	public void switch_group_two(int opcode,Signature s) {
		System.out.println("Executor.switch_group_two calling opcode "+opcode+" (0x"+Integer.toHexString(opcode)+")");
      		switch(opcode) {

case GETMEMSIZE:
		chip.GETMEMSIZE(s.store_mode,s.address); break;

//0x160
case CALLF:
		chip.CALLF(new Address(s.values[0]),s.modes[1],new Address(s.values[1])); break;

case CALLFI:
		System.out.println("Executor: CALLFI, arity="+s.arity);
		Address funcAddress1=new Address(s.values[0]);
		Address stubAddr1=new Address(s.values[2]);
		chip.CALLFI(funcAddress1,s.values[1],s.modes[2],stubAddr1);
		break;

case CALLFII:
		Address funcAddress2=new Address(s.values[0]);
		Address stubAddr2=new Address(s.values[3]);
		chip.CALLFII(funcAddress2,s.values[1],s.values[2],s.modes[3],stubAddr2);
		break;
default:
 		System.out.println("unknown opcode "+Integer.toHexString(opcode));
			}
		}
}
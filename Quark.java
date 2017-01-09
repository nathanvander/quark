package quark;
import java.io.*;
import org.p2c2e.util.FastByteBuffer;

/**
* modified from org.p2c2e.zag.Zag;
*
* This is a basic Glulxe virtual machine.  It does not have acceleration functions or heaps, or glk.
*
* The gamefile is not used, except upon startup.
*/
public class Quark {
	Global global;
	FastByteBuffer stack;
    FastByteBuffer mem;
    IO io;
    FunctionManager mgr;
    Executor exec;

  	public Quark(String filename, int iStart) throws IOException
  	{
		//all variables are held in here
		global=new Global();
		global.file_name=filename;
		global.gamefile_start=iStart;

		//comment this out for now to speed up the process
 	   	//if (verify(global,true) != 0) {
 	    // 	throw new IOException("Gamefile failed checksum.");
	 	//}
 	   	mem=readFileIntoMemory(global);

 	   	System.out.println("memory created, with ROM from &00000 to "+(new Address(global.ramstart.addr-1).toString()));
 	   	System.out.println("and RAM from "+global.ramstart.toString()+" to "+global.endmem.toString());
 	   	System.out.println("the starting function is at "+global.startfuncaddr);
 	   	System.out.println("the string table is at "+global.origstringtable);


		//we don't use a heap
      	//heap = new Heap(this);

      	stack = new FastByteBuffer(global.stacksize);
      	System.out.println("the stack has a size of "+global.stacksize);

      	//create stub io. this will need more work
    	io=new IoSys();

 	   	//start up function manager.  it is needed to enter the function
 	   	mgr=new FunctionManager(global,stack,mem,io);
 	   	exec=new Executor(global,stack,mem,io,mgr);
 	 }

 	 public void start()
 	 {
 	   global.running = true;
 	   global.pc=global.startfuncaddr;
 	   mgr.enterFunction(global.pc, 0, null);
 	   exec.run();
 	 }

	//============
	//load file
	//it has already been verified
	//this loads the game-level data from the file and loads the file into memory
	//this modifies the global object
  	private static FastByteBuffer readFileIntoMemory(Global g) throws IOException {
    	int b;
    	FastByteBuffer buf;
    	DataInputStream in;
    	File gamefile=new File(g.file_name);
    	RandomAccessFile f = new RandomAccessFile(gamefile, "r");

    	f.seek(g.gamefile_start);

		//read the magic number
		f.read(g.magic);
		System.out.println("the magic word is "+new String(g.magic));

		//version
		g.version=f.readInt();
		System.out.println("the version is "+g.version);

		//other variables from the header
		g.ramstart.addr=f.readInt();
		g.extstart=f.readInt();
		g.endmem.addr=f.readInt();
		g.stacksize=f.readInt();
		g.startfuncaddr.addr=f.readInt();
		g.origstringtable.addr=f.readInt();
		g.checksum=f.readInt();

		displayCompilerInfo(f);


      	buf = new FastByteBuffer(g.endmem.addr);
      	buf.setMinSize(g.endmem.addr);

      	f.seek(g.gamefile_start);
      	//set the limit on the buf to extstart
      	buf.limit(g.extstart);
      	f.getChannel().read(buf.asByteBuffer());

      	//reset the buffer position to 0 and set the limit to endmem
      	buf.clear();
      	f.close();
		return buf;
    }

    public static void displayCompilerInfo(RandomAccessFile f) throws IOException {
		f.seek(36);
		byte[] comp=new byte[4];
		f.read(comp);
		String compiler=new String(comp);
		System.out.println("the compiler is "+compiler);
		if (!compiler.equals("Info")) {
			return;
		}

		int format=f.readInt();
		byte[] version=new byte[4];
		f.read(version);
		System.out.println("the Inform compiler version is "+new String(version));
		byte[] glulx_compiler=new byte[4];
		f.read(glulx_compiler);
		System.out.println("the Glulx compiler version is "+new String(glulx_compiler));
		short game_release=f.readShort();
		byte[] serial=new byte[6];
		f.read(serial);
		System.out.println("the game serial number is "+new String(serial));


	}

	//==========================================
	//verify the game file.  This is static, so pass in the global object
	//the progress boolean is for the glk display, which is not implemented
 	public static int verify(Global g,boolean progress)
  {
    int len;
    int check;
    int sum = 0;
    int val;
    boolean okay = true;
    RandomAccessFile f;
    DataInputStream in;

    try
    {
		File gamefile=new File(g.file_name);
      f = new RandomAccessFile(gamefile, "r");
      f.seek(g.gamefile_start);
      okay &= ((char) f.read()) == 'G';
      okay &= ((char) f.read()) == 'l';
      okay &= ((char) f.read()) == 'u';
      okay &= ((char) f.read()) == 'l';

      f.seek(g.gamefile_start + 12);
      len = f.readInt();
      System.out.println("game file length = "+len);
      okay &= f.length() >= (long) (g.gamefile_start + len);

      f.seek(g.gamefile_start + 32);
      check = f.readInt();

      f.seek(g.gamefile_start);
      in = new DataInputStream(new BufferedInputStream(new FileInputStream(f.getFD())));
      for (int i = 0; i < len / 4; i++)
      {
        	if (progress && ((i % 4096)== 0) )  {
          		//Glk.progress("Verifying file...", 0, (len / 4), i);
          		System.out.println("Verifying file..."+(i*4)+" out of "+len+" bytes read");
	  		}

        val = in.readInt();
        sum += (i == 8) ? 0 : val;
      }
      in.close();
      f.close();

      System.out.println("check sum per file is "+check);
      System.out.println("check sum calculated is "+sum);

      //if (progress)
      //  Glk.progress(null, 0, 0, 0);

      okay &= (sum == check);

      return (okay) ? 0 : 1;
    }
    catch (Exception e)
    {
      return 1;
    }
  }

  //======================================
  //read in a program from the command line
  public static void main(String[] args) throws IOException
  {
	  Quark q=new Quark(args[0],0);
	  q.start();
  }

} //end class
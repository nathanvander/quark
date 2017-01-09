package quark;

/**
* This holds all the global variables that are strewn through-out the code
*/
public class Global extends FunctionContext {
	//passed in at the beginning
	String file_name;
	int gamefile_start;

	byte[] magic=new byte[4];
	int version;
	Address ramstart=new Address();
	int extstart;	//aka endgamefile;
					//this doesn't need to be an address as it is never used that way
	Address endmem=new Address();
	int stacksize;
	Address startfuncaddr=new Address();
	Address origstringtable=new Address();
	int checksum;
}

public interface OpCode {
	//the instruction set numbers are subject to change
	//constants
	public final static int ICONST_0 = 0;	//same as false
	public final static int ICONST_1 = 1;	//same as true
	public final static int ICONST_2 = 2;
	public final static int ICONST_3 = 3;
	public final static int ICONST = 7;		//push a different number on the stack
	public final static int ICONST_M2 = 8;
	public final static int ICONST_M1 = 9;

	//math
	public final static int ADD = 10;    	// int add
	public final static int INC = 11;		// add one
    public final static int SUB = 12;    	// int sub
    public final static int MUL = 13;    	// int mul
    public final static int DIV = 14;		// integer division
    public final static int MOD = 15;		// remainder after integer division
    public final static int NEG = 16;		//negation. return the negative of a number

	//boolean
    public final static int ODD = 20;	//push 1 if odd, 0 if even
    public final static int EQ = 21;     //push 1 if equal
    public final static int NEQ = 22;	//the opposite
    public final static int EQZ = 23;    //aka FALSE push 1 if equal zero
    public final static int LT = 24;     //push 1 if first is less than second
    public final static int LTE = 25;	//push 1 if first is less than or equals second
    public final static int GT = 26;
    public final static int GTE = 27;
    public final static int GTZ = 28;	//aka TRUE push 1 if greater than zero, 0 if false
    public final static int AND = 29;	//boolean and
    public final static int OR = 30;
    public final static int NOT = 31;

	//variable manipulation
    public final static int GLOAD = 40;     // load from global
    public final static int GSTORE = 41;    // store in global memory
    public final static int POP = 43;       // throw away top of the stack
    public final static int DUP = 44;		//duplicate top number on stack
    public final static int SWAP = 45;		//swap top 2 numbers on the stack
    public final static int PRINT = 46;   	// print value on top of the stack
    public final static int PRINTLN = 47;
    public final static int PRINTS = 48;	//interpret the number as a 4 byte ascii string and print it
    public final static int NEWLN = 49;		//line feed

	//program control
	public final static int CALL = 50;
	public final static int RET = 51;
	public final static int RETT = 52;		//return 1
	public final static int RETF = 53;		//return 1
    public final static int JMP = 55;        // branch
	public final static int JMPT = 56;       // branch if true
    public final static int JMPF = 57;       // branch if false
	public final static int HALT = 59;
	public final static int NOP = 63;

	//experimental code for labels
	public final static int LABEL = 70;		//a label is a place in the code followed by a number which is at least 1000
    public final static int LJMP = 71;        // branch
	public final static int LJMPT = 72;       // branch if true
    public final static int LJMPF = 73;       // branch if false
    public final static int LCALL = 74;			//use if you are jumping to a label

}
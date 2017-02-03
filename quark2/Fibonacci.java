/**
* The hardest thing here is stack manipulation.
* All of the parameters are on the stack, and all must be removed
* leaving only the return value.
*/

public class Fibonacci implements OpCode {
	public final static int MAIN=35;

	public static int[] program() {
		//line
		int FIB=0;	//line number of the function
		int LABEL_1=8;
		int LABEL_2=16;
		int LABEL_3=22;
		//function fib(n: integer) return integer
		//	this expects one number on the stack
		int code[] = {
			//int FIB(n)
			//FIB:
			//if N==3 return 2
			ICONST_3, EQ, JMPF,LABEL_1,		//0
			POP, ICONST_2, RET,				//4 return 2
			NOP,							//7

			//LABEL_1:
			//IF N==2 return 1
			ICONST_2, EQ, JMPF, LABEL_2,	//8
			POP, ICONST_1, RET,				//12 return 1
			NOP,							//15

			//LABEL2:
			//IF N<2 return N.  doesn't work for negative numbers
			ICONST_2, LT, JMPF, LABEL_3,	//16
			RET, 							//20
			NOP,							//21

			//LABEL_3:
			// else return Fibonacci(n - 1) + Fibonacci(n - 2); }
			DUP, 							//22
			ICONST_1, SUB,					//23
			CALL,FIB,						//25 call fib(n-1)
			SWAP,							//27
			ICONST_2, SUB,					//28
			CALL,FIB,						//30 call fib (n-2)
			ADD,							//32
			RET,							//33
			NOP,							//34

			//MAIN:
			// CALL FIB(N)
			CALL,FIB,						//35
			HALT							//37
		};
		return code;
	}

	public static void main(String[] args) {
		//size should be a number from 0..10 for testing
		int size=Integer.parseInt(args[0]);
		System.out.println("calculating the "+size+"th Fibonacci number");
		int[] code=program();
		Quark.Stack p=new Quark.Stack();
		p.push(size);
		Quark.VM vm=new Quark.VM(code,MAIN,p);
		vm.run();
		//get result off of stack
		int result=p.pop();
		System.out.println("the "+size+"th Fibonacci number is "+result);
	}

}

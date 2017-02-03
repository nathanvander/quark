/**
* The hardest thing here is stack manipulation.
* All of the parameters are on the stack, and all must be removed
* leaving only the return value.
*
* Version with labels
*/

public class Fibonacci2 implements OpCode {
	//define labels
	public final static int MAIN=1001;
	public final static int FIB=1002;
	public final static int LABEL_N2=1004;
	public final static int LABEL_N1=1005;
	//I call this n4 because it is bigger than 3
	public final static int LABEL_N4=1006;



	public static int[] program() {

		//function fib(n: integer) return integer
		//	this expects one number on the stack
		int code[] = {
			LABEL,MAIN,						//define the main method
			LCALL,FIB,
			HALT,

			//int FIB(n)
			//FIB:
			//if N==3 return 2
			LABEL,FIB,						//define function FIB
			ICONST_3, EQ, LJMPF,LABEL_N2,	//IF N==3
			POP, ICONST_2, RET,				//return 2

			//IF N==2 return 1
			LABEL,LABEL_N2,
			ICONST_2, EQ, LJMPF, LABEL_N1,	//IF N==2
			POP, ICONST_1, RET,				//return 1

			//IF N<2 return N.
			LABEL,LABEL_N1,
			ICONST_2, LT, LJMPF,LABEL_N4,	//IF N < 2
			RET, 							//return n

			// else return Fibonacci(n - 1) + Fibonacci(n - 2); }
			LABEL,LABEL_N4,
			DUP,
			ICONST_1, SUB,
			LCALL,FIB,						//call fib(n-1)
			SWAP,
			ICONST_2, SUB,
			LCALL,FIB,						//call fib (n-2)
			ADD,
			RET,


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
		Quark.VM vm=new Quark.VM(code,0,p);
		vm.run();
		//get result off of stack
		int result=p.pop();
		System.out.println("the "+size+"th Fibonacci number is "+result);
	}

}

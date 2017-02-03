import java.util.Vector;
import java.util.Hashtable;

/**
* adding experimental support for labels
*/
public class Quark implements OpCode {

	public static class Heap extends Vector<Integer> {
		public void store(int address,int value) {
			ensureCapacity(address);
			set(address,Integer.valueOf(value));
		}
		public int load(int address) {
			Integer i=(Integer)elementAt(address);
			return i.intValue();
		}
	}

	/**
	* A Stack, as used here, can hold either data, or line numbers.
	*/
	public static class Stack extends Vector<Integer> {
		public void push(int i) {
			add(Integer.valueOf(i));
		}
		public int pop() {
			if (size()<1) {
				throw new IllegalArgumentException("trying to pop() but size()="+size());
			}
			Integer i=remove(size()-1);
			return i.intValue();
		}

		//view the last element without removing it
		public int peek() {
			Integer i=lastElement();
			return i.intValue();
		}

		//see what is in the stack
		public String dump() {
			StringBuffer sb=new StringBuffer("[");
			for (int i=0; i<size(); i++) {
				if (i>0) sb.append(",");
				sb.append(elementAt(i));
			}
			sb.append("]");
			return sb.toString();
		}
	}

	public static class Labels extends Hashtable<Integer,Integer> {
		//translate a code to a line number
		public void put(int code, int line) {
			if (code<1000) {
				throw new IllegalStateException("a label code must be at least 1000");
			}
			//see if code is already defined
			Integer i=get(Integer.valueOf(code));
			if (i!=null) {
				throw new IllegalStateException("label "+code+" is already defined");
			}
			if (line<0) {
				throw new IllegalStateException("invalid line "+line);
			}
			put(Integer.valueOf(code),Integer.valueOf(line));
		}

		//retrieve the line number associated with a give code
		//return -1 if invalid
		public int get(int code) {
			Integer i=get(Integer.valueOf(code));
			if (i==null) {
				return -1;
			} else {
				return i.intValue();
			}
		}
	}

	public static class VM implements Runnable {
		Heap heap;
		Stack stack;		//this has temporary data and parameters
		Stack callStack;	//this has the return address
		int[] code;
		int pc;
		//no stack pointer or frame pointer, they are not needed
		Labels labels;

		//turn an int into a 4 byte String
		//must be negative
		public static String decode(int i) {
			if (!(i<0)) {
				throw new IllegalArgumentException("invalid code "+i);
			}
			i= i * -1;
			byte[] b=new byte[4];

			for (int j=0; j<4; j++) {
				b[3-j] =(byte)(i % 256);
				i = i / 256;
			}
			return new String(b);
		}

		//param is a way of passing in command line arguments
		public VM(int[] code, int main, Stack param) {
			heap=new Heap();
			if (param!=null) {
				stack=param;
			} else {
				stack=new Stack();
			}
			callStack=new Stack();
			this.code=code;
			pc=main;
			labels=new Labels();
			findLabels();
		}

		//do a first pass and find all the labels
		//there are 2 meanings of the word code here
		private void findLabels() {
			for (int i=0;i<code.length; i++) {
				int opcode=code[i];
				if (opcode==LABEL) {
					//get next entry
					int c=code[i+1];
					//System.out.println("the value of code["+(i+1)+"] is "+c);
					if (c<1000) {
						throw new IllegalStateException("on line "+i+" label code ("+c+") must be at least 1000");
					} else {
						//add it.  The jumpto location is the next line
						labels.put(c,i+2);
					}
				}
			}

		}

		public void PUSH(int i) {stack.push(i);}

		public int POP() {return stack.pop();}

		public int PEEK() {return stack.peek();}

		//get the code at the pc, and then increment pc
		public int NEXT() {
			return code[pc++];
		}

		public void run() {
			int a;		//the address register, also second address
			int b;		//the main data.  also value, boolean
			int c;		//result, composite
			while (true) {
				int opcode = NEXT();
				//System.out.println((pc-1)+": opcode "+opcode);
				//System.out.println("stack = "+stack.dump());

				switch(opcode) {
					case ICONST_0:  PUSH(0);  break;
					case ICONST_1:  PUSH(1);  break;
					case ICONST_2:  PUSH(2);  break;
					case ICONST_3:  PUSH(3);  break;
					case ICONST:  	b=NEXT();  PUSH(b); break;
					case ICONST_M2:  PUSH(-2);  break;
					case ICONST_M1:  PUSH(-1);  break;

					//math
					case ADD:	b=POP(); a=POP();  PUSH(a+b);
						System.out.println("adding "+b+" and "+a);
						break;
					case INC:	b=POP(); PUSH(b++); break;
					case SUB:	b=POP(); a=POP();	PUSH(a-b); break;
					case MUL:	b=POP(); a=POP();	PUSH(a*b); break;
					case DIV:	b=POP(); a=POP();	PUSH(a / b); break;
					case MOD:	b=POP(); a=POP();	PUSH(a % b); break;
					case NEG:	b=POP();	PUSH (b * -1); break;

					//boolean
					//new rule: the recent boolean operators are consumed
					//but the old number is not
					//except for AND OR NOT
					case ODD:	b=PEEK(); a=(b % 2); PUSH(a); break;
					case EQ:	b=POP(); a=PEEK(); c = (a == b ? 1 : 0); PUSH(c); break;
					case NEQ:	b=POP(); a=PEEK(); c = (a != b ? 1 : 0); PUSH(c); break;
					case EQZ:   b=PEEK(); b=(b==0 ? 1 : 0); PUSH(b); break;
					case LT:	b=POP(); a=PEEK(); c = (a < b ? 1 : 0);
						//System.out.println("a="+a+",b="+b+",c="+c);
						PUSH(c);
						break;
					case LTE:	b=POP(); a=PEEK(); c = (a <= b ? 1 : 0); PUSH(c); break;
					case GT:	b=POP(); a=PEEK(); c = (a > b ? 1 : 0); PUSH(c); break;
					case GTE:	b=POP(); a=PEEK(); c = (a >= b ? 1 : 0); PUSH(c); break;
					case GTZ:	b=PEEK();  b=(b > 0 ? 1 : 0); PUSH(b); break;
					case AND:	b=POP(); b=(b>0?1:0); a=POP(); a=(a>0?1:0);  PUSH(a & b); break;
					case OR:	b=POP(); b=(b>0?1:0); a=POP(); a=(a>0?1:0);  PUSH(a | b); break;
					case NOT:	b=POP(); b=(b>0?0:1);  PUSH(b); break;

					//GLOAD syntax: GLOAD &address
					case GLOAD:				//access global memory
						a=NEXT();		//get the address
						b=heap.load(a); //get the value
						PUSH(a);			//put it on the stack
						break;
					//GSTORE syntax: v GSTORE &address
					case GSTORE:
						a=NEXT();
						b=POP();		//get the value
						heap.store(a,b);
						break;
					case POP: POP(); break;	//throw-away the top of the stack
					case DUP:
						b=PEEK();
						PUSH(b);
						break;
					case SWAP:
						b=POP(); a=POP(); PUSH(b); PUSH(a);	break;
					case PRINT: System.out.print(POP()); break;
					case PRINTLN: System.out.println(POP()); break;
					case PRINTS:
						b=POP();
						System.out.print(decode(b));
						break;
					case NEWLN: System.out.println(); break;

					case CALL:
						//System.out.println("IN CALL, PC = "+pc);
						callStack.push(pc+1);  //save the current location
						pc = NEXT();
						System.out.println("CALL function at "+pc+". Return address is "+callStack.peek()+". Arg = "+stack.peek());
						break;
					//RET syntax: 	retval RET
					case RET:
						pc = callStack.pop();
						System.out.println("RET from function. return value is "+stack.peek()+", returning control to line "+pc);
						break;
					case RETT: PUSH(1); pc= callStack.pop(); break;
					case RETF: PUSH(0); pc= callStack.pop(); break;
					case JMP: pc = NEXT();  break;
					case JMPT:
						a = NEXT();
						b = POP();
						if (b==1) {
							pc = a;
						}
						break;
					case JMPF:
						a = NEXT();
						b = POP();
						if (b==0) {
							pc = a;
						}
						break;
					case HALT:
						//b = POP();
						//System.out.println("return value: "+b);
						return;
					case NOP: break;	//this is padding

					//------------------
					//experimental support for labels
					case LABEL:
						//since we have already pre-processed the labels, this is a nop
						//but consume the label code so it doesn't confuse the interpreter
						NEXT();				//advance PC 1 step
						break;
					case LJMP:
						a = NEXT();  		//get the code
						c = labels.get(a);	//lookup the actual line to jump to
						pc = c;
						break;
					case LJMPT:
						a = NEXT();			//get the code
						c = labels.get(a);	//get the location
						b = POP();
						if (b==1) {
							pc = c;
						}
						break;
					case LJMPF:
						a = NEXT();
						c = labels.get(a);
						b = POP();
						if (b==0) {
							pc = c;
						}
						break;
					case LCALL:
						//System.out.println("IN CALL, PC = "+pc);
						callStack.push(pc+1);  //save the current location
						a = NEXT();
						c = labels.get(a);
						pc = c;
						System.out.println("CALL function at "+pc+". Return address is "+callStack.peek()+". Arg = "+stack.peek());
						break;
					default:
						throw new IllegalStateException("unknown opcode "+opcode);
				}  //switch
			}	//while
		}
	}

}
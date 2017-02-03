public class Hello implements OpCode {

	public static int[] program() {
		int[] code=new int[] {
			ICONST,
			-1214606444,		//Hell
			PRINTS,
			ICONST,
			-1862301551,		//o wo
			PRINTS,
			ICONST,
			-1919706145,		//rld!
			PRINTS,
			NEWLN,
			HALT
		};
		return code;
	}

	public static void main(String[] args) {
		int[] code=program();
		Quark.VM vm=new Quark.VM(code,0,null);
		vm.run();
	}
}
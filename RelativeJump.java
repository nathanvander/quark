package quark;

/**
* I choose RelativeJump as my first "compilation unit" because it is called
* from numerous places.
*
* It calls 2 methods:
*	leaveFunction
*	popCallstub
* These both come from funcs.c, so that gives us a hint
*/
public class RelativeJump {

	//private final void handleRelativeJump(int offset)
	public static void handleRelativeJump(FunctionContext c,Function f,int offset)

  {
    if (offset == 0 || offset == 1)
    {
      f.leaveFunction();
      if (c.sp == 0)
      {
        c.running = false;
        return;
      }
      f.popCallstub(offset);
    }
    else
    {
		//pc = pc + offset - 2;
      c.pc.addr = c.pc.addr + offset - 2;
    }
  }

}
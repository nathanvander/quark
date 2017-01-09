package quark;
//package org.p2c2e.zag;
import org.p2c2e.zag.OpConstants;

/**
* class Op. I would import it, except it is not public.
*
* An OP is meta-data about an operation.
* I modified this to add opcode. If it is metadata
* it should know at least its own opcode, no?
*
* I think this class should be renamed Signature, but I don't want to do too many changes.
*/

public class Op implements OpConstants
{
  static final Op[] OPS = new Op[0x1CA];

  int opcode;	//repeated for clarity
  int arity;
  int[] format;

  Op(int c,int a, int[] f)
  {
	opcode=c;
    arity = a;
    format = f;
  }

  static
  {
    OPS[NOP] = new Op(NOP,0, null);
    OPS[ADD] = new Op(ADD,3, LLS);
    OPS[SUB] = new Op(SUB,3, LLS);
    OPS[MUL] = new Op(MUL,3, LLS);
    OPS[DIV] = new Op(DIV,3, LLS);
    OPS[MOD] = new Op(MOD,3, LLS);
    OPS[NEG] = new Op(NEG,2, LS);
    OPS[BITAND] = new Op(BITAND,3, LLS);
    OPS[BITOR] = new Op(BITOR,3, LLS);
    OPS[BITXOR] = new Op(BITXOR,3, LLS);
    OPS[BITNOT] = new Op(BITNOT,2, LS);
    OPS[SHIFTL] = new Op(SHIFTL,3, LLS);
    OPS[SSHIFTR] = new Op(SSHIFTR,3, LLS);
    OPS[USHIFTR] = new Op(USHIFTR,3, LLS);
    OPS[JUMP] = new Op(JUMP,1, L);
    OPS[JZ] = new Op(JZ,2, LL);
    OPS[JNZ] = new Op(JNZ,2, LL);
    OPS[JEQ] = new Op(JEQ,3, LLL);
    OPS[JNE] = new Op(JNE,3, LLL);
    OPS[JLT] = new Op(JLT,3, LLL);
    OPS[JGE] = new Op(JGE,3, LLL);
    OPS[JGT] = new Op(JGT,3, LLL);
    OPS[JLE] = new Op(JLE,3, LLL);
    OPS[JLTU] = new Op(JLTU,3, LLL);
    OPS[JGEU] = new Op(JGEU,3, LLL);
    OPS[JGTU] = new Op(JGTU,3, LLL);
    OPS[JLEU] = new Op(JLEU,3, LLL);
    OPS[CALL] = new Op(CALL,3, LLS);
    OPS[RETURN] = new Op(RETURN,1, L);
    OPS[CATCH] = new Op(CATCH,2, SL);
    OPS[THROW] = new Op(THROW,2, LL);
    OPS[TAILCALL] = new Op(TAILCALL,2, LL);
    OPS[COPY] = new Op(COPY,2, LS);
    OPS[COPYS] = new Op(COPYS,2, LS);
    OPS[COPYB] = new Op(COPYB,2, LS);
    OPS[SEXS] = new Op(SEXS,2, LS);
    OPS[SEXB] = new Op(SEXB,2, LS);
    OPS[ALOAD] = new Op(ALOAD,3, LLS);
    OPS[ALOADS] = new Op(ALOADS,3, LLS);
    OPS[ALOADB] = new Op(ALOADB,3, LLS);
    OPS[ALOADBIT] = new Op(ALOADBIT,3, LLS);
    OPS[ASTORE] = new Op(ASTORE,3, LLL);
    OPS[ASTORES] = new Op(ASTORES,3, LLL);
    OPS[ASTOREB] = new Op(ASTOREB,3, LLL);
    OPS[ASTOREBIT] = new Op(ASTOREBIT,3, LLL);
    OPS[STKCOUNT] = new Op(STKCOUNT,1, S);
    OPS[STKPEEK] = new Op(STKPEEK,2, LS);
    OPS[STKSWAP] = new Op(STKSWAP,0, null);
    OPS[STKROLL] = new Op(STKROLL,2, LL);
    OPS[STKCOPY] = new Op(STKCOPY,1, L);
    OPS[STREAMCHAR] = new Op(STREAMCHAR,1, L);
    OPS[STREAMNUM] = new Op(STREAMNUM,1, L);
    OPS[STREAMSTR] = new Op(STREAMSTR,1, L);
    OPS[STREAMUNICHAR] = new Op(STREAMUNICHAR,1, L);
    OPS[GESTALT] = new Op(GESTALT,3, LLS);
    OPS[DEBUGTRAP] = new Op(DEBUGTRAP,1, L);
    OPS[GETMEMSIZE] = new Op(GETMEMSIZE,1, S);
    OPS[SETMEMSIZE] = new Op(SETMEMSIZE,2, LS);
    OPS[JUMPABS] = new Op(JUMPABS,1, L);
    OPS[RANDOM] = new Op(RANDOM,2, LS);
    OPS[SETRANDOM] = new Op(SETRANDOM,1, L);
    OPS[QUIT] = new Op(QUIT,0, null);
    OPS[VERIFY] = new Op(VERIFY,1, S);
    OPS[RESTART] = new Op(RESTART,0, null);
    OPS[SAVE] = new Op(SAVE,2, LS);
    OPS[RESTORE] = new Op(RESTORE,2, LS);
    OPS[SAVEUNDO] = new Op(SAVEUNDO,1, S);
    OPS[RESTOREUNDO] = new Op(RESTOREUNDO,1, S);
    OPS[PROTECT] = new Op(PROTECT,2, LL);
    OPS[GLK] = new Op(GLK,3, LLS);
    OPS[GETSTRINGTBL] = new Op(GETSTRINGTBL,1, S);
    OPS[SETSTRINGTBL] = new Op(SETSTRINGTBL,1, L);
    OPS[GETIOSYS] = new Op(GETIOSYS,2, SS);
    OPS[SETIOSYS] = new Op(SETIOSYS,2, LL);
    OPS[LINEARSEARCH] = new Op(LINEARSEARCH,8, LLLLLLLS);
    OPS[BINARYSEARCH] = new Op(BINARYSEARCH,8, LLLLLLLS);
    OPS[LINKEDSEARCH] = new Op(LINKEDSEARCH,7, LLLLLLS);
    OPS[CALLF] = new Op(CALLF,2, LS);
    OPS[CALLFI] = new Op(CALLFI,3, LLS);
    OPS[CALLFII] = new Op(CALLFII,4, LLLS);
    OPS[CALLFIII] = new Op(CALLFIII,5, LLLLS);
    OPS[MZERO] = new Op(MZERO,2, LL);
    OPS[MCOPY] = new Op(MCOPY,3, LLL);
    OPS[MALLOC] = new Op(MALLOC,2, LS);
    OPS[MFREE] = new Op(MFREE,1, L);

    OPS[ACCELFUNC] = new Op(ACCELFUNC,2, LL);
    OPS[ACCELPARAM] = new Op(ACCELPARAM,2, LL);

    OPS[NUMTOF] = new Op(NUMTOF,2, LS);
    OPS[FTONUMZ] = new Op(FTONUMZ,2, LS);
    OPS[FTONUMN] = new Op(FTONUMN,2, LS);
    OPS[CEIL] = new Op(CEIL,2, LS);
    OPS[FLOOR] = new Op(FLOOR,2, LS);

    OPS[FADD] = new Op(FADD,3, LLS);
    OPS[FSUB] = new Op(FSUB,3, LLS);
    OPS[FMUL] = new Op(FMUL,3, LLS);
    OPS[FDIV] = new Op(FDIV,3, LLS);
    OPS[FMOD] = new Op(FMOD,4, LLSS);

    OPS[SQRT] = new Op(SQRT,2, LS);
    OPS[EXP] = new Op(EXP,2, LS);
    OPS[LOG] = new Op(LOG,2, LS);
    OPS[POW] = new Op(POW,3, LLS);

    OPS[SIN] = new Op(SIN,2, LS);
    OPS[COS] = new Op(COS,2, LS);
    OPS[TAN] = new Op(TAN,2, LS);
    OPS[ASIN] = new Op(ASIN,2, LS);
    OPS[ACOS] = new Op(ACOS,2, LS);
    OPS[ATAN] = new Op(ATAN,2, LS);
    OPS[ATAN2] = new Op(ATAN2,3, LLS);

    OPS[JFEQ] = new Op(JFEQ,4, LLLL);
    OPS[JFNE] = new Op(JFNE,4, LLLL);
    OPS[JFLT] = new Op(JFLT,3, LLL);
    OPS[JFLE] = new Op(JFLE,3, LLL);
    OPS[JFGT] = new Op(JFGT,3, LLL);
    OPS[JFGE] = new Op(JFGE,3, LLL);

    OPS[JISNAN] = new Op(JISNAN,2, LL);
    OPS[JISINF] = new Op(JISINF,2, LL);

  }
}

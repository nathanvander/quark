package quark;

/**
* I call this signature, because it is the signature
* of the parameters that are passed to the Glulx chip
* it has the type and value.
* It could be an inner class, but I think inner classes should always be static.
*
* On further thought, Signature isn't the best name for this, maybe parameters.
* and Op should be renamed Signature.
* But I like the name Signature better, so keeping it for now.
*/

public class Signature {
	//it should be obviously which opcode we are doing but we want to make it totally clear
	int opcode;
	int arity;			//this is the number of parameters.  usually 3, but could be anywhere from 0..8
						//see the Op class

	int store_param;	//this is the number of the store_param, usually 2 but could be different
						//this is not really needed, just another sanity check

	int orig_store_mode;	//this is the store_mode before it was manipulate.
							//it is a value from 0..15
							//for the meaning of this see http://www.eblong.com/zarf/glulx/glulx-spec_1.html#s.5
							//this is not needed for anything

	int store_mode;		//the mode of the store param.  This is from 0..3
						//this is the same as: modes[store_param]
						//this is the only mode we care about, we don't care about the load modes

	Address address;	//the address to store the output, or null if n/a
						//this is the same as: new Address(values[store_param])

	int[] modes;		//the array of modes from 0..arity-1
						//you don't need this, but it is here anyways.

	int[] values;		//the values of the parameters
						//there are arity number of these
						//you don't need the value for the store_param because it is set in Address
						//but it is here anyways

	int v0;				//shortcut for values[0]
	int v1;				//shortcut for values[1]
	int v2;				//shortcut for values[2]
}

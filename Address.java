package quark;

/**
* Address is just a wrapper around int to show that it is an absolute memory address.
* This may prevent some bugs.
*/
public class Address {
	public int addr;

	public Address() {}
	public Address(int a) {addr=a;}

	public String toString() {
		return "&"+Integer.toHexString(addr).toUpperCase();
	}
}
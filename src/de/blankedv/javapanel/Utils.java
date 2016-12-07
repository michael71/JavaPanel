/*
 * some Util functions like min and max of 3 integers
 */
package de.blankedv.javapanel;

import static de.blankedv.javapanel.Defines.*;
        
/**
 *
 * @author mblank
 */
public class Utils {
    /**
	 * calculate minimum of 3 integers, the first one is always a
	 * valid number, the other can if INVALID_INT (=>not taken into
	 * account) or valid integers, then they are evaluated
	 */
	public static int min(int x, int xt, int x2) {
		int m = x;  // is always defined.
		if (x == INVALID_INT) System.out.println(TAG+"  Utils.min: x is undefined.");
		if ((xt != INVALID_INT) && (xt<m)) m=xt;
		if ((x2 != INVALID_INT) && (x2<m)) m=x2;
		return m;
	}
	
	/**
	 * calculate maximum of 3 integers, the first one is always a
	 * valid number, the other can if INVALID_INT (=>not taken into
	 * account) or valid integers, then they are evaluated
	 */
	public static int max(int x, int xt, int x2) {
		int m = x;
		if (x == INVALID_INT) System.out.println(TAG+"Utils.min: x is undefined.");
		if ((xt != INVALID_INT) && (xt>m)) m=xt;
		if ((x2 != INVALID_INT) && (x2>m)) m=x2;
		return m;
	}
}

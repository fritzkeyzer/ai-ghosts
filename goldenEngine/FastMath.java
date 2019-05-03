package GoldenEngine;

// http://www.java-gaming.org/index.php?topic=36467.0
// Icecore's atan2 ( http://www.java-gaming.org/topics/extremely-fast-atan2/36467/msg/346145/view.html#msg346145 )

public final class FastMath {

	private static final int Size_Ac = 100000;
	private static final int Size_Ar = Size_Ac + 1;
	public static final double Pi = (double) Math.PI;
	public static final double Pi_H = Pi / 2;

	private static final double Atan2[] = new double[Size_Ar];
	private static final double Atan2_PM[] = new double[Size_Ar];
	private static final double Atan2_MP[] = new double[Size_Ar];
	private static final double Atan2_MM[] = new double[Size_Ar];

	private static final double Atan2_R[] = new double[Size_Ar];
	private static final double Atan2_RPM[] = new double[Size_Ar];
	private static final double Atan2_RMP[] = new double[Size_Ar];
	private static final double Atan2_RMM[] = new double[Size_Ar];
	
	
	static final int precision = 100; // gradations per degree, adjust to suit
	static final int modulus = 360*precision;
	static final float[] sin = new float[modulus]; // lookup table

	private static final double TWO_POW_450 = Double.longBitsToDouble(0x5C10000000000000L);
	private static final double TWO_POW_N450 = Double.longBitsToDouble(0x23D0000000000000L);
	private static final double TWO_POW_750 = Double.longBitsToDouble(0x6ED0000000000000L);
	private static final double TWO_POW_N750 = Double.longBitsToDouble(0x1110000000000000L);


	static {
		for (int i = 0; i <= Size_Ac; i++) {
			double d = (double) i / Size_Ac;
			double x = 1;
			double y = x * d;
			double v = (double) Math.atan2(y, x);
			Atan2[i] = v;
			Atan2_PM[i] = Pi - v;
			Atan2_MP[i] = -v;
			Atan2_MM[i] = -Pi + v;

			Atan2_R[i] = Pi_H - v;
			Atan2_RPM[i] = Pi_H + v;
			Atan2_RMP[i] = -Pi_H + v;
			Atan2_RMM[i] = -Pi_H - v;
		}
		
		// a static initializer fills the table
		// in this implementation, units are in degrees
		for (int i = 0; i<sin.length; i++) {
			sin[i]=(float)Math.sin((i*Math.PI)/(precision*180));
		}
	}

	public static final double atan2(double y, double x) {
		if (y < 0) {
			if (x < 0) {
				//(y < x) because == (-y > -x)
				if (y < x) {
					return Atan2_RMM[(int) (x / y * Size_Ac)];
				} else {
					return Atan2_MM[(int) (y / x * Size_Ac)];
				}
			} else {
				y = -y;
				if (y > x) {
					return Atan2_RMP[(int) (x / y * Size_Ac)];
				} else {
					return Atan2_MP[(int) (y / x * Size_Ac)];
				}
			}
		} else {
			if (x < 0) {
				x = -x;
				if (y > x) {
					return Atan2_RPM[(int) (x / y * Size_Ac)];
				} else {
					return Atan2_PM[(int) (y / x * Size_Ac)];
				}
			} else {
				if (y > x) {
					return Atan2_R[(int) (x / y * Size_Ac)];
				} else {
					return Atan2[(int) (y / x * Size_Ac)];
				}
			}
		}
	}
	
	// Private function for table lookup
	private static float sinLookup(int a) {
		return a>=0 ? sin[a%(modulus)] : -sin[-a%(modulus)];
	}

	// These are your working functions:
	public static double sin(double a) {
		return Math.sin(a);
		//return sinLookup((int)(a * precision + 0.5f));
	}
	public static double cos(double a) {
		return Math.cos(a);
		//return sinLookup((int)((a+90f) * precision + 0.5f));
	}
	
	public static double hypot(double x, double y) {
    x = Math.abs(x);
    y = Math.abs(y);
    if (y < x) {
        double a = x;
        x = y;
        y = a;
    } else if (!(y >= x)) { // Testing if we have some NaN.
        if ((x == Double.POSITIVE_INFINITY) || (y == Double.POSITIVE_INFINITY)) {
            return Double.POSITIVE_INFINITY;
        } else {
            return Double.NaN;
        }
    }
    if (y-x == y) { // x too small to substract from y
        return y;
    } else {
        double factor;
        if (x > TWO_POW_450) { // 2^450 < x < y
            x *= TWO_POW_N750;
            y *= TWO_POW_N750;
            factor = TWO_POW_750;
        } else if (y < TWO_POW_N450) { // x < y < 2^-450
            x *= TWO_POW_750;
            y *= TWO_POW_750;
            factor = TWO_POW_N750;
        } else {
            factor = 1.0;
        }
        return factor * Math.sqrt(x*x+y*y);
    }
}
	
}
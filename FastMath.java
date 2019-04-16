public final class FastMath 
{

	private static final int Size_Ac = 100000;
	private static final int Size_Ar = Size_Ac + 1;
	private static final double Pi = (double) Math.PI;
	private static final double Pi_H = Pi / 2;

	private static final double Atan2[] = new double[Size_Ar];
	private static final double Atan2_PM[] = new double[Size_Ar];
	private static final double Atan2_MP[] = new double[Size_Ar];
	private static final double Atan2_MM[] = new double[Size_Ar];

	private static final double Atan2_R[] = new double[Size_Ar];
	private static final double Atan2_RPM[] = new double[Size_Ar];
	private static final double Atan2_RMP[] = new double[Size_Ar];
	private static final double Atan2_RMM[] = new double[Size_Ar];

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
}
package GoldenEngine;


/*
Uses Jafama FastMath library for faster atan2 calculations

By Fritz Keyzer:

Last Edited on 10 March 2019



*/




//import net.jafama.FastMath;


public class Vector2D{
	
	private double x = 0;
	private double y = 0;
	
	public static final double pi = Math.PI;
	public static final double halfPi = 0.5*Math.PI;
	public static final double twoPi = 2*Math.PI;
	
	public Vector2D(double _x, double _y){
		x = _x;
		y = _y;
	}
	
	//********** Getters & Setters
	public double x(){
		return x;
	}
	
	public double y(){
		return y;
	}
	
	public void x(double _in){
		x = _in;
	}
	
	public void y(double _in){
		y = _in;
	}
	
	//********** Public static methods
	public static Vector2D polar(double _mag, double _angle){
		return new Vector2D(_mag*FastMath.cos(_angle), _mag*FastMath.sin(_angle));
	}
	
	public static Vector2D zero(){
		return new Vector2D(0, 0);
	}
	
	public static Vector2D middle(Vector2D _a, Vector2D _b){
		return new Vector2D((_a.x+_b.x)/2, (_a.y+_b.y)/2);
	}
	
	public static Vector2D sumOf(Vector2D _a, Vector2D _b){
		return new Vector2D((_a.x+_b.x), (_a.y+_b.y));
	}
	
	public static Vector2D divide(Vector2D _a, double _b){
		return new Vector2D((_a.x/_b),(_a.y/_b));
	}
	
	public static Vector2D multiply(Vector2D _a, double _b){
		return new Vector2D((_a.x*_b),(_a.y*_b));
	}
	
	public static double dist(Vector2D _a, Vector2D _b){
		return FastMath.hypot(_a.y-_b.y, _a.x-_b.x);
	}
	
	public static double dot(Vector2D _a, double _angle){
		double theta = _a.angle()-_angle;
		//return FastMath.hypot(FastMath.cos(theta)*_a.x(), FastMath.sin(theta)*_a.y());
		return _a.mag()*(FastMath.cos(theta));
	}
	
	public static Vector2D max(Vector2D _in, double _max){
		
		if (_in.mag() > _max){
			return Vector2D.polar(_max, _in.angle());
		}
		return _in;
	}
	
	public static Vector2D findPerpindicularIntersector(Vector2D _lineA, Vector2D _lineB, Vector2D _otherPointC){
		
		

		double angleAtoB = _lineA.angleTo(_lineB);
		double angleAtoC = _lineA.angleTo(_otherPointC);
		double angleBtoC = angleAtoC - angleAtoB;
		double intersectorDistFromA = dist(_lineA, _otherPointC) * FastMath.cos(angleBtoC);
		
		if (intersectorDistFromA <= dist(_lineA, _lineB)){
			//there exist a normal off the line AB that will reach _otherPointC
			return _lineA.addPolar(intersectorDistFromA, angleAtoB);
		}
		return null;
		
		//public static boolean inRange(double start_x, double start_y, double end_x, double end_y,
        //                      double point_x, double point_y) {
		//	double dx = end_x - start_x;
		//	double dy = end_y - start_y;
		//	double innerProduct = (point_x - start_x)*dx + (point_y - start_y)*dy;
		//	return 0 <= innerProduct && innerProduct <= dx*dx + dy*dy;
		//}
		
	}
	
	//********** Public non-static methods
	public Vector2D unit(){
		double mag = mag();
		return new Vector2D(x/mag, y/mag);
	}
	
	public double angleTo(Vector2D _otherPoint){
		return FastMath.atan2(_otherPoint.y-y, _otherPoint.x-x);
	}
	
	public double mag(){
		return FastMath.hypot(y, x);
	}
	
	public double angle(){
		return FastMath.atan2(y, x);
	}
	
	public double distTo(Vector2D _otherPoint){
		return FastMath.hypot(_otherPoint.y-y, _otherPoint.x-x);
	}
	
	public Vector2D addPolar(double _mag, double _angle){
		return new Vector2D(x+FastMath.cos(_angle)*_mag, y+FastMath.sin(_angle)*_mag);
	}
	
	public Vector2D add(Vector2D _toAdd){
		return new Vector2D(x+_toAdd.x, y+_toAdd.y);
	}
	
	public Vector2D subtract(Vector2D _toSub){
		return new Vector2D(x-_toSub.x, y-_toSub.y);
	}
	
	public Vector2D multiply(double _multiplier){
		return new Vector2D(x*_multiplier, y*_multiplier);
	}
	
	public Vector2D rotate(double _angle){
		double a = angle();
		double m = mag();
		return new Vector2D(FastMath.cos(a+_angle)*m, FastMath.sin(a+_angle)*m);
	}
	
	//public Vector2D dotTo(Vector2D _b){
	//	double mag = mag();
	//	double angleB = _b.angle();
	//	double angle = angle()-angleB;
	//	return Vector2D.polar(mag*FastMath.cos(angle), angleB);
	//}
	
}
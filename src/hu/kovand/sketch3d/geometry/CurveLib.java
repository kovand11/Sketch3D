package hu.kovand.sketch3d.geometry;

public class CurveLib {
	
	
	//return = {knot1,...,knot_(n+p+2),cpx1,cpy2,cpz3,..,cpx_(n+1),cpy2_(n+1),cpz_(n+1)} 
	public native static double[] approximate(double[] px,double[] py,double[] pz,int p,int n);
	
	//return = {px1,py1,pz1...pxn,pyn,pzn} n=points
	public native static double[] evaluateN(double[] knots,double[] cpx,double[] cpy,double[] cpz,int points);
	
	//return = {px,py,pz)
	public native static double[] evaluate(double[] knots,double[] cpx,double[] cpy,double[] cpz,float t);
	
	//return = {u,dist,x,y,z}	
	public native static double[] projectPoint(double[] knots, double[] cpx, double[] cpy, double[] cpz,double x,double y,double z, int resolution, double distance_tol, double cosine_tol);
	
	
	
	static{
		System.loadLibrary("hu_kovand_sketch3d_geometry_CurveLib");
	}
	
	
	
	

}


//knot = n + p + 2

//p = knot - cp - 1
package hu.kovand.sketch3d.geometry;

import android.util.Log;
import hu.kovand.sketch3d.utility.MyMath;

public class HermiteSpline {
	
	Point3D p1;
	Point3D m1;
	Point3D p2;
	Point3D m2;
	

	public HermiteSpline(Point3D p1,Point3D m1,Point3D p2,Point3D m2) {
		this.p1 = p1;
		this.m1 = m1;
		this.p2 = p2;
		this.m2 = m2;
		
		
	}
	
	public PolyLine evaluate(int points)
	{
		PolyLine result = new PolyLine(points);
		
		Log.d("points", Float.toString(p1.getX()) + " " +Float.toString(p1.getY()) + " " +Float.toString(p1.getZ()));
		Log.d("points", Float.toString(m1.getX()) + " " +Float.toString(m1.getY()) + " " +Float.toString(m1.getZ()));
		Log.d("points", Float.toString(p2.getX()) + " " +Float.toString(p2.getY()) + " " +Float.toString(p2.getZ()));
		Log.d("points", Float.toString(m2.getX()) + " " +Float.toString(m2.getY()) + " " +Float.toString(m2.getZ()));

		
		
		for (int i = 0;i<points;i++)
		{
			float t =i/(points-1.0f);
			float c00 = hermiteCoeff(0, 0, t);
			float c10 = hermiteCoeff(1, 0, t);
			float c01 = hermiteCoeff(0, 1, t);
			float c11 = hermiteCoeff(1, 1, t);
			
			Log.d("weights", Float.toString(t) + " " +Float.toString(c00) + " " +Float.toString(c10) + " " +Float.toString(c01)+ " " +Float.toString(c11));		
			
			
			float x = p1.getX()*c00 + m1.getX()*c10 + p2.getX()*c01 + m2.getX()*c11;
			float y = p1.getY()*c00 + m1.getY()*c10 + p2.getY()*c01 + m2.getY()*c11;
			float z = p1.getZ()*c00 + m1.getZ()*c10 + p2.getZ()*c01 + m2.getZ()*c11;
			
			Log.d("result", Float.toString(x) + " " +Float.toString(y) + " " +Float.toString(z));
			
			result.append(new Point3D(x, y, z, 0, 0));
		}
		
		return result;
	}
	
	public static float hermiteCoeff(int i,int j,float t)
	{
		if (i==0 && j==0){
			return 1 - 3*t*t + 2*t*t*t;				
		}
		else if (i==0 && j==1){
			return 3*t*t -2*t*t*t;						
		}
		else if (i==1 && j==0){
			return t - 2*t*t+t*t*t;
		}
		else if (i==1 && j==1){
			return -t*t + t*t*t;
		}
		else return 0.0f;
	}

}

package hu.kovand.sketch3d.geometry;

import android.util.Log;

public class HermiteSpline {
	
	Vec3 p1;
	Vec3 m1;
	Vec3 p2;
	Vec3 m2;
	

	public HermiteSpline(Vec3 p1,Vec3 m1,Vec3 p2,Vec3 m2) {
		this.p1 = p1;
		this.m1 = m1;
		this.p2 = p2;
		this.m2 = m2;
		
		
	}
	
	public PolyLine evaluate(int points)
	{
		PolyLine result = new PolyLine();
		


		
		
		for (int i = 0;i<points;i++)
		{
			float t =i/(points-1.0f);
			float c00 = hermiteCoeff(0, 0, t);
			float c10 = hermiteCoeff(1, 0, t);
			float c01 = hermiteCoeff(0, 1, t);
			float c11 = hermiteCoeff(1, 1, t);		
			
			
			float x = p1.getX()*c00 + m1.getX()*c10 + p2.getX()*c01 + m2.getX()*c11;
			float y = p1.getY()*c00 + m1.getY()*c10 + p2.getY()*c01 + m2.getY()*c11;
			float z = p1.getZ()*c00 + m1.getZ()*c10 + p2.getZ()*c01 + m2.getZ()*c11;
			
			
			result.add(new Vec3(x, y, z));
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

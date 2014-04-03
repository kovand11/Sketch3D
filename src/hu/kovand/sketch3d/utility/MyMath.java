package hu.kovand.sketch3d.utility;

import java.security.PolicySpi;
import java.util.ArrayList;

import android.util.Log;


import hu.kovand.sketch3d.geometry.Point3D;
import hu.kovand.sketch3d.geometry.PolyLine;


public class MyMath {
	
	public static final int DIRECTION_FORWARD = 0;
	public static final int DIRECTION_BACKWARD = 1;
	
	public static float polygonAreaIgnoringZ(ArrayList<Point3D> p) 
	{ 
		float area = 0;         
		int j = p.size() - 1;

		for (int i=0; i<p.size(); i++){
			area = area +  (p.get(j).getX()+p.get(i).getX()) * (p.get(j).getY()-p.get(i).getY()); 
			j = i;
	    }
		return area/2;
	}
	
	public static float distanceL2(ArrayList<Point3D> p)
	{
		float d = 0.0f;
		
		for (int i=1;i<p.size();i++)
		{
			float dx = p.get(i).getX() - p.get(i-1).getX();
			float dy = p.get(i).getY() - p.get(i-1).getY();
			float dz = p.get(i).getZ() - p.get(i-1).getZ();
			d += Math.sqrt(dx*dx + dy*dy + dz*dz);
		}		
		return d;
	}
	
	public static float distanceL2(Point3D p1,Point3D p2){
		
		float dx = p2.getX() - p1.getX();
		float dy = p2.getY() - p1.getY(); 
		float dz = p2.getZ() - p1.getZ();
		
		return (float) Math.sqrt(dx*dx+dy*dy+dz*dz);
	}
	

	
	public static Point3D tangent(Point3D p1,Point3D p2)
	{
		float l = distanceL2(p1, p2);
		float dx = p2.getX() - p1.getX();
		float dy = p2.getY() - p1.getY(); 
		float dz = p2.getZ() - p1.getZ();
		
		return new Point3D(dx/l, dy/l, dz/l);
				
	}
	
	//TODO optimizeable ?
	
	//last + 1 if the curve shorter
	public static int findSpan(ArrayList<Point3D> p,int startindex,float distance,int direction)
	{
		int i;
		float d;
		if (direction == DIRECTION_FORWARD)
		{
			for (i=startindex;i<p.size();i++)
			{			
				d = distanceL2(p.get(startindex), p.get(i));
				if (d>=distance)
					break;
			}
			return i;
		}
		else
		{
			for (i=startindex;i>=0;i--)
			{			
				d = distanceL2(p.get(startindex), p.get(i));
				if (d>distance)
					break;
			}
			return i;			
		}

	}
	
	//TODO optimizeable 
	
	public static int findClosest(ArrayList<Point3D> arr,Point3D p)
	{
		float min = Float.MAX_VALUE;
		int index = -1;
		for (int i=0;i<arr.size();i++)
		{
			float d = distanceL2(p, arr.get(i));
			if (d<min){
				index = i;
				min = d;
			}
		}		
		return index;
	}
	
	public static float weightFunction(float t,float qw,float hw)
	{		
		return (-48.0f*hw + 113.777778f*qw)*t*t + 
				   (352.0f*hw - 682.66667f*qw)*t*t*t + 
				   (-816.0f*hw + 1479.11111f*qw)*t*t*t*t + 
				   (768.0f*hw - 1365.33333f*qw)*t*t*t*t*t + 
				   (-256.0f*hw + 455.11111f*qw)*t*t*t*t*t*t;	
	}
	
	public static ArrayList<IntersectionAddress> findIntersections(PolyLine l1,PolyLine l2)
	{
		ArrayList<IntersectionAddress> addresses = new ArrayList<IntersectionAddress>();
		for(int i=0;i<l1.size()-1;i++)
		{
			for(int j=0;j<l2.size()-1;j++)
			{
				float p1x = l1.get(i).getX();
				float p1y = l1.get(i).getY();
				
				float p2x = l1.get(i+1).getX();
				float p2y = l1.get(i+1).getY();
				
				float p3x = l2.get(j).getX();
				float p3y = l2.get(j).getY();
				
				float p4x = l2.get(j+1).getX();
				float p4y = l2.get(j+1).getY();
				
				float t1 = (-(p3y*p4x) + p1y*(-p3x + p4x) + p1x*(p3y - p4y) + p3x*p4y)/(p2y*(p3x - p4x) + p1y*(-p3x + p4x) + (p1x - p2x)*(p3y - p4y));
				float t2 = (p1y*(p2x - p3x) + p2y*p3x - p2x*p3y + p1x*(-p2y + p3y))/(p2y*(p3x - p4x) + p1y*(-p3x + p4x) + (p1x - p2x)*(p3y - p4y));
				
				
				if (t1>=0 && t1<1 && t2>=0 && t2<1)
				{
					addresses.add(new IntersectionAddress(i,t1,j,t2));
				}
				
			}			
		}
		return addresses;
	}
	
	
	
}

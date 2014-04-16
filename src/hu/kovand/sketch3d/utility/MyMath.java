package hu.kovand.sketch3d.utility;

import java.security.PolicySpi;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;


public class MyMath {
	
	public static final int DIRECTION_FORWARD = 0;
	public static final int DIRECTION_BACKWARD = 1;
	
	public static float polygonArea(ArrayList<Vec2> p) 
	{ 
		float area = 0;         
		int j = p.size() - 1;

		for (int i=0; i<p.size(); i++){
			area = area +  (p.get(j).getX()+p.get(i).getX()) * (p.get(j).getY()-p.get(i).getY()); 
			j = i;
	    }
		return area/2;
	}
	
	public static float length(List<Vec3> p)
	{
		return length(p, 1.0f, 1.0f, 1.0f);		
	}
	
	public static float length(List<Vec3> p,float xlen,float ylen,float zlen)
	{
		float d = 0.0f;
		
		for (int i=1;i<p.size();i++)
		{
			float dx = (p.get(i).getX() - p.get(i-1).getX())*xlen;
			float dy = (p.get(i).getY() - p.get(i-1).getY())*ylen;
			float dz = (p.get(i).getZ() - p.get(i-1).getZ())*zlen;
			d += Math.sqrt(dx*dx + dy*dy + dz*dz);
		}		
		return d;
	}
	
	
	


	
	//TODO optimizeable ?
	
	//last + 1 if the curve shorter
	public static int findSpan(List<Vec3> p,int startindex,float distance,int direction)
	{
		int i;
		float d;
		if (direction == DIRECTION_FORWARD)
		{
			for (i=startindex;i<p.size();i++)
			{				
				d = Vec3.distance(p.get(startindex), p.get(i)) ;
				if (d>=distance)
					break;
			}
			return i;
		}
		else
		{
			for (i=startindex;i>=0;i--)
			{			
				d = Vec3.distance(p.get(startindex), p.get(i));
				if (d>distance)
					break;
			}
			return i;			
		}

	}
	
	
	public static int findClosest(List<Vec3> arr,Vec3 p)
	{
		float min = Float.MAX_VALUE;
		int index = -1;
		for (int i=0;i<arr.size();i++)
		{
			float d = Vec3.distance(p, arr.get(i));
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
	
	public static List<IntersectionAddress> findIntersections(List<Vec3> l1,List<Vec3> l2)
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
				
				float t1 = -1.0f;
				float t2 = -1.0f;
				
				try{
				
				t1 = (-(p3y*p4x) + p1y*(-p3x + p4x) + p1x*(p3y - p4y) + p3x*p4y)/(p2y*(p3x - p4x) + p1y*(-p3x + p4x) + (p1x - p2x)*(p3y - p4y));
				t2 = (p1y*(p2x - p3x) + p2y*p3x - p2x*p3y + p1x*(-p2y + p3y))/(p2y*(p3x - p4x) + p1y*(-p3x + p4x) + (p1x - p2x)*(p3y - p4y));
				}
				catch(ArithmeticException e)
				{
					continue;					
				}
				
				
				if (t1>=0 && t1<1 && t2>=0 && t2<1)
				{
					addresses.add(new IntersectionAddress(i,t1,j,t2));
				}
				
			}			
		}
		return addresses;
	}
	
	
	
	
	
}

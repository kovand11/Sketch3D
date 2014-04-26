package hu.kovand.sketch3d.geometry;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Vec2 {
	
	private float x;
	private float y;


	public Vec2(float[] v) {
		x = v[0];
		y = v[1];
	}
	
	public Vec2(float x,float y)
	{
		this.x = x;
		this.y = y;
	}
	
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public static int findClosest(List<Vec2> list,Vec2 p,float lx,float ly)
	{
		float min = Float.MAX_VALUE;
		int index = -1;
		for (int i=0;i<list.size();i++)
		{
			float d = Vec2.distance(p, list.get(i));
			if (d<min){
				index = i;
				min = d;
			}
		}		
		return index;
	}
	
	public static Vec2 add (Vec2  a,Vec2  b)
	{
		float x = a.getX() + b.getX();
		float y = a.getY() + b.getY();
		return new Vec2 (x, y);
	}
	
	public static Vec2 subtract (Vec2 a,Vec2 b)
	{
		float x = a.getX() - b.getX();
		float y = a.getY() - b.getY();
		return new Vec2(x, y);						
	}
	
	public static Vec2 multiply(Vec2 p,float a)
	{
		float x = p.getX() * a;
		float y = p.getY() * a;
		return new Vec2(x, y);
	}
	
	public static float length(Vec2 p)
	{
		float x = p.getX();
		float y = p.getY();		
		return (float) Math.sqrt(x*x + y*y);						
	}
	
	public static float distance(Vec2 a,Vec2 b)
	{
		return length(subtract(a, b));
	}

	

}

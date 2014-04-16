package hu.kovand.sketch3d.geometry;

public class Vec3 {
	
	private float x;
	private float y;
	private float z;

	public Vec3(float[] v) {
		x = v[0];
		y = v[1];
		z = v[2];
	}
	
	public Vec3(float x,float y,float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;	
	}
	
	public float getX()
	{
		return x;		
	}
	public float getY()
	{
		return y;
		
	}
	public float getZ()
	{
		return z;		
	}
	
	public static Vec3 add (Vec3  a,Vec3  b)
	{
		float x = a.getX() + b.getX();
		float y = a.getY() + b.getY();
		float z = a.getZ() + b.getZ();
		return new Vec3 (x, y, z);
	}
	
	public static Vec3 subtract (Vec3 a,Vec3 b)
	{
		float x = a.getX() - b.getX();
		float y = a.getY() - b.getY();
		float z = a.getZ() - b.getZ();
		return new Vec3(x, y, z);						
	}
	
	public static Vec3 multiply(Vec3 p,float a)
	{
		float x = p.getX() * a;
		float y = p.getY() * a;
		float z = p.getZ() * a;
		return new Vec3(x, y, z);
	}
	
	public static float length(Vec3 p)
	{
		float x = p.getX();
		float y = p.getY();
		float z = p.getZ();
		
		return (float) Math.sqrt(x*x + y*y + z*z);						
	}
	
	public static float distance(Vec3 a,Vec3 b)
	{
		return length(subtract(a, b));
	}
	
	public static Vec3 weightedAdd(Vec3 p1,float w1,Vec3 p2,float w2)
	{
		float x = (w1*p1.getX()+w2*p2.getX());
		float y = (w1*p1.getY()+w2*p2.getY());
		float z = (w1*p1.getZ()+w2*p2.getZ());		
		return new Vec3(x, y, z);
	}
	
	public static Vec3 crossProduct(Vec3 a,Vec3 b)
	{
		return new Vec3(-a.getZ()*b.getY() + a.getY()*b.getZ(), a.getZ()*b.getX() - a.getX()*b.getZ(), -a.getY()*b.getX() + a.getX()*b.getY());
	}
	
	public Vec2 ignoreZ()
	{
		return new Vec2(x,y);		
	}
	
	

}

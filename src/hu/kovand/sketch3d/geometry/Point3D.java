package hu.kovand.sketch3d.geometry;

public class Point3D {
	
	protected float x;
	protected float y;
	protected float z;
	

	
	public Point3D(float x,float y,float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float getX()	{
		return x;
	}
	
	public float getY()	{
		return y;
	}
	
	public float getZ()	{
		return z;
	}	
	
	
	public static Point3D add (Point3D a,Point3D b)
	{
		float x = a.getX() + b.getX();
		float y = a.getY() + b.getY();
		float z = a.getZ() + b.getZ();
		return new Point3D(x, y, z);
	}
	
	public static Point3D subtract (Point3D a,Point3D b)
	{
		float x = b.getX() - a.getX();
		float y = b.getY() - a.getY();
		float z = b.getZ() - a.getZ();
		return new Point3D(x, y, z);						
	}
	
	public static Point3D multiply(Point3D p,float a)
	{
		float x = p.getX() * a;
		float y = p.getY() * a;
		float z = p.getZ() * a;
		return new Point3D(x, y, z);
	}
	
	public static float length(Point3D p)
	{
		float x = p.getX();
		float y = p.getY();
		float z = p.getZ();
		
		return (float) Math.sqrt(x*x + y*y + z*z);						
	}
	
	public static float distance(Point3D a,Point3D b)
	{
		return length(subtract(a, b));
	}
	
	public static Point3D weightedAdd(Point3D p1,float w1,Point3D p2,float w2)
	{
		float x = (w1*p1.getX()+w2*p2.getX());
		float y = (w1*p1.getY()+w2*p2.getY());
		float z = (w1*p1.getZ()+w2*p2.getZ());		
		return new Point3D(x, y, z);
	}
	
	@Override
	public String toString() {
		return Float.toString(x)+" "+Float.toString(y)+" "+Float.toString(z);
	}
	
	

	
	
	
	

}

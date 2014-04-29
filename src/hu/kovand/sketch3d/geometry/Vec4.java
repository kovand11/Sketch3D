package hu.kovand.sketch3d.geometry;

import android.opengl.Matrix;

public class Vec4 {
	
	private float x;
	private float y;
	private float z;
	private float t;

	public Vec4(float[] v) 
	{
		x = v[0];
		y = v[1];
		z = v[2];
		t = v[3];		
	}
	
	public Vec4(float x,float y,float z,float t)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.t = t;		
	}
	
	/**Generate normalized vector.
	 * 
	 * @param v3 3d vector
	 */
	public Vec4(Vec3 v3)
	{
		x = v3.getX();
		y = v3.getY(); 
		z = v3.getZ(); 
		t = 1.0f;				
	}
	
	public Vec3 denormalize()
	{
		return new Vec3(x/t, y/t, z/t);		
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
	
	public float getT()
	{
		return t;		
	}
	
	public float getXAsNorm()
	{
		return x/t;		
	}
	public float getYAsNorm()
	{
		return y/t;
		
	}
	public float getZAsNorm()
	{
		return z/t;		
	}
	
	public float[] toArray()
	{
		float[] arr = new float[4];
		arr[0] = x;
		arr[1] = y;
		arr[2] = z;
		arr[3] = t;
		return arr;
		
	}
	
	public static Vec4 addAsNorm (Vec4 a,Vec4 b)
	{
		Vec3 ad = a.denormalize();
		Vec3 bd = b.denormalize();
		Vec3 sum = Vec3.add(ad, bd);
		return new Vec4(sum);
	}
	
	public static Vec4 subtractAsNorm (Vec4 a,Vec4 b)
	{
		Vec3 ad = a.denormalize();
		Vec3 bd = b.denormalize();
		Vec3 dif = Vec3.subtract(ad, bd);
		return new Vec4(dif);						
	}
	
	public static Vec4 multiplyAsNorm(Vec4 p,float a)
	{
		float x = p.getX() * a;
		float y = p.getY() * a;
		float z = p.getZ() * a;
		float t = p.getT();
		return new Vec4(x, y, z, t);
	}
	
	public static Vec4 multipleMV(Vec4 v,float [] mat)
	{
		float[] res = new float[4];
		Matrix.multiplyMV(res, 0, mat, 0, v.toArray(), 0);
		return new Vec4(res);
		
	}
	
	public static float lengthAsNorm(Vec4 p)
	{
		float x = p.getX()/p.getT();
		float y = p.getY()/p.getT();
		float z = p.getZ()/p.getT();		
		return (float) Math.sqrt(x*x + y*y + z*z);						
	}
	
	@Override
	public String toString() {
		return Float.toString(x)+ " " +Float.toString(y)+ " "+Float.toString(z)+ " "+Float.toString(t);
	}
	
	
	

}

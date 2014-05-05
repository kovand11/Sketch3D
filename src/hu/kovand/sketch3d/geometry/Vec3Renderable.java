package hu.kovand.sketch3d.geometry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Vec3Renderable extends Vec3 {
	
	public static final float RADIUS_SMALL = 5.0f;
	public static final float RADIUS_BIG = 7.0f;
	
	public static final int COORDS = 3;
	public static final int BYTES_PER_FLOAT = 4;
	public static final int VERTEX_PER_POINT = 36;
	
	private FloatBuffer vertexBuffer;

	public Vec3Renderable(float[] v, float renderRadius) {
		super(v);		
		generateBuffer(v[0], v[1], v[2], renderRadius);
		
	}
	
	public Vec3Renderable(float x,float y,float z, float renderRadius)
	{
		super(x,y,z);
		generateBuffer(x, y, z, renderRadius);
	}
	
	public Vec3Renderable(Vec3 v, float renderRadius)
	{
		super(v.getX(), v.getY(), v.getZ());
		generateBuffer(v.getX(), v.getY(), v.getZ(), renderRadius);
		
	}
	
	private void generateBuffer(float x,float y,float z,float r)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(COORDS*BYTES_PER_FLOAT*VERTEX_PER_POINT);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.position(0);
		
		//front
		vertexBuffer.put(18*0+0,x+r);		vertexBuffer.put(18*0+1,y+r); 		vertexBuffer.put(18*0+2,z+r);		
		vertexBuffer.put(18*0+3,x-r);		vertexBuffer.put(18*0+4,y+r);		vertexBuffer.put(18*0+5,z+r);	
		vertexBuffer.put(18*0+6,x-r);		vertexBuffer.put(18*0+7,y-r);		vertexBuffer.put(18*0+8,z+r);		
		vertexBuffer.put(18*0+9,x+r);		vertexBuffer.put(18*0+10,y+r);		vertexBuffer.put(18*0+11,z+r);		
		vertexBuffer.put(18*0+12,x-r);		vertexBuffer.put(18*0+13,y-r);		vertexBuffer.put(18*0+14,z+r);		
		vertexBuffer.put(18*0+15,x+r);		vertexBuffer.put(18*0+16,y-r);		vertexBuffer.put(18*0+17,z+r);
		
		//BACK
		vertexBuffer.put(18*1+0,x+r);		vertexBuffer.put(18*1+1,y+r);		vertexBuffer.put(18*1+2,z-r);		
		vertexBuffer.put(18*1+3,x-r);		vertexBuffer.put(18*1+4,y+r);		vertexBuffer.put(18*1+5,z-r);		
		vertexBuffer.put(18*1+6,x-r);		vertexBuffer.put(18*1+7,y-r);		vertexBuffer.put(18*1+8,z-r);		
		vertexBuffer.put(18*1+9,x+r);		vertexBuffer.put(18*1+10,y+r);		vertexBuffer.put(18*1+11,z-r);		
		vertexBuffer.put(18*1+12,x-r);		vertexBuffer.put(18*1+13,y-r);		vertexBuffer.put(18*1+14,z-r);		
		vertexBuffer.put(18*1+15,x+r);		vertexBuffer.put(18*1+16,y-r);		vertexBuffer.put(18*1+17,z-r);
		
		//LEFT
		vertexBuffer.put(18*2+0,x-r);		vertexBuffer.put(18*2+1,y+r);		vertexBuffer.put(18*2+2,z+r);		
		vertexBuffer.put(18*2+3,x-r);		vertexBuffer.put(18*2+4,y+r);		vertexBuffer.put(18*2+5,z-r);		
		vertexBuffer.put(18*2+6,x-r);		vertexBuffer.put(18*2+7,y-r);		vertexBuffer.put(18*2+8,z-r);		
		vertexBuffer.put(18*2+9,x-r);		vertexBuffer.put(18*2+10,y+r);		vertexBuffer.put(18*2+11,z+r);		
		vertexBuffer.put(18*2+12,x-r);		vertexBuffer.put(18*2+13,y-r);		vertexBuffer.put(18*2+14,z-r);		
		vertexBuffer.put(18*2+15,x-r);		vertexBuffer.put(18*2+16,y-r);		vertexBuffer.put(18*2+17,z+r);
			
		//RIGHT
		vertexBuffer.put(18*3+0,x+r);		vertexBuffer.put(18*3+1,y+r);		vertexBuffer.put(18*3+2,z+r);		
		vertexBuffer.put(18*3+3,x+r);		vertexBuffer.put(18*3+4,y+r);		vertexBuffer.put(18*3+5,z-r);		
		vertexBuffer.put(18*3+6,x+r);		vertexBuffer.put(18*3+7,y-r);		vertexBuffer.put(18*3+8,z-r);		
		vertexBuffer.put(18*3+9,x+r);		vertexBuffer.put(18*3+10,y+r);		vertexBuffer.put(18*3+11,z+r);		
		vertexBuffer.put(18*3+12,x+r);		vertexBuffer.put(18*3+13,y-r);		vertexBuffer.put(18*3+14,z-r);		
		vertexBuffer.put(18*3+15,x+r);		vertexBuffer.put(18*3+16,y-r);		vertexBuffer.put(18*3+17,z+r);
		
		//BOTTOM
		vertexBuffer.put(18*4+0,x+r);		vertexBuffer.put(18*4+1,y-r);		vertexBuffer.put(18*4+2,z+r);		
		vertexBuffer.put(18*4+3,x-r);		vertexBuffer.put(18*4+4,y-r);		vertexBuffer.put(18*4+5,z+r);		
		vertexBuffer.put(18*4+6,x-r);		vertexBuffer.put(18*4+7,y-r);		vertexBuffer.put(18*4+8,z-r);		
		vertexBuffer.put(18*4+9,x+r);		vertexBuffer.put(18*4+10,y-r);		vertexBuffer.put(18*4+11,z+r);		
		vertexBuffer.put(18*4+12,x-r);		vertexBuffer.put(18*4+13,y-r);		vertexBuffer.put(18*4+14,z-r);		
		vertexBuffer.put(18*4+15,x+r);		vertexBuffer.put(18*4+16,y-r);		vertexBuffer.put(18*4+17,z-r);
				
		//TOP
		vertexBuffer.put(18*5+0,x+r);		vertexBuffer.put(18*5+1,y+r);		vertexBuffer.put(18*5+2,z+r);		
		vertexBuffer.put(18*5+3,x-r);		vertexBuffer.put(18*5+4,y+r);		vertexBuffer.put(18*5+5,z+r);		
		vertexBuffer.put(18*5+6,x-r);		vertexBuffer.put(18*5+7,y+r);		vertexBuffer.put(18*5+8,z-r);		
		vertexBuffer.put(18*5+9,x+r);		vertexBuffer.put(18*5+10,y+r);		vertexBuffer.put(18*5+11,z+r);		
		vertexBuffer.put(18*5+12,x-r);		vertexBuffer.put(18*5+13,y+r);		vertexBuffer.put(18*5+14,z-r);		
		vertexBuffer.put(18*5+15,x+r);		vertexBuffer.put(18*5+16,y+r);		vertexBuffer.put(18*5+17,z-r);
	}
	
	public FloatBuffer getVertexBuffer()
	{
		return vertexBuffer;
	}
}

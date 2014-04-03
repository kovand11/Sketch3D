package hu.kovand.sketch3d.geometry;

import hu.kovand.sketch3d.graphics.GLRenderer;
import hu.kovand.sketch3d.utility.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES30;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

public class PointSet {
	public static final String TAG = "PointSet";
	
	static final int COORDS = 3;
	static final int BYTES_PER_FLOAT = 4;
	static final int VERTEX_PER_POINT = 36;
	
	
	
	private ArrayList<Point3D> points;
	private FloatBuffer vertexBuffer;

	public PointSet(int reserve_size) 
	{
		points = new ArrayList<Point3D>();
		ByteBuffer bb = ByteBuffer.allocateDirect(reserve_size*COORDS*BYTES_PER_FLOAT*VERTEX_PER_POINT);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.position(0);
	}
	
	public void add(Point3D p)
	{
		points.add(p);
		int pos = VERTEX_PER_POINT * COORDS * (points.size()-1);
		
		float x = p.getX();
		float y = p.getY();
		float z = p.getZ()-Constants.POINT_SQUARE_RADIUS;		
		float r = Constants.POINT_SQUARE_RADIUS;
		
		Log.d("points", p.toString());
		

		
		//front
		vertexBuffer.put(pos+18*0+0,x+r);		vertexBuffer.put(pos+18*0+1,y+r); 		vertexBuffer.put(pos+18*0+2,z+r);		
		vertexBuffer.put(pos+18*0+3,x-r);		vertexBuffer.put(pos+18*0+4,y+r);		vertexBuffer.put(pos+18*0+5,z+r);	
		vertexBuffer.put(pos+18*0+6,x-r);		vertexBuffer.put(pos+18*0+7,y-r);		vertexBuffer.put(pos+18*0+8,z+r);		
		vertexBuffer.put(pos+18*0+9,x+r);		vertexBuffer.put(pos+18*0+10,y+r);		vertexBuffer.put(pos+18*0+11,z+r);		
		vertexBuffer.put(pos+18*0+12,x-r);		vertexBuffer.put(pos+18*0+13,y-r);		vertexBuffer.put(pos+18*0+14,z+r);		
		vertexBuffer.put(pos+18*0+15,x+r);		vertexBuffer.put(pos+18*0+16,y-r);		vertexBuffer.put(pos+18*0+17,z+r);
		
		//BACK
		vertexBuffer.put(pos+18*1+0,x+r);		vertexBuffer.put(pos+18*1+1,y+r);		vertexBuffer.put(pos+18*1+2,z-r);		
		vertexBuffer.put(pos+18*1+3,x-r);		vertexBuffer.put(pos+18*1+4,y+r);		vertexBuffer.put(pos+18*1+5,z-r);		
		vertexBuffer.put(pos+18*1+6,x-r);		vertexBuffer.put(pos+18*1+7,y-r);		vertexBuffer.put(pos+18*1+8,z-r);		
		vertexBuffer.put(pos+18*1+9,x+r);		vertexBuffer.put(pos+18*1+10,y+r);		vertexBuffer.put(pos+18*1+11,z-r);		
		vertexBuffer.put(pos+18*1+12,x-r);		vertexBuffer.put(pos+18*1+13,y-r);		vertexBuffer.put(pos+18*1+14,z-r);		
		vertexBuffer.put(pos+18*1+15,x+r);		vertexBuffer.put(pos+18*1+16,y-r);		vertexBuffer.put(pos+18*1+17,z-r);
		
		//LEFT
		vertexBuffer.put(pos+18*2+0,x-r);		vertexBuffer.put(pos+18*2+1,y+r);		vertexBuffer.put(pos+18*2+2,z+r);		
		vertexBuffer.put(pos+18*2+3,x-r);		vertexBuffer.put(pos+18*2+4,y+r);		vertexBuffer.put(pos+18*2+5,z-r);		
		vertexBuffer.put(pos+18*2+6,x-r);		vertexBuffer.put(pos+18*2+7,y-r);		vertexBuffer.put(pos+18*2+8,z-r);		
		vertexBuffer.put(pos+18*2+9,x-r);		vertexBuffer.put(pos+18*2+10,y+r);		vertexBuffer.put(pos+18*2+11,z+r);		
		vertexBuffer.put(pos+18*2+12,x-r);		vertexBuffer.put(pos+18*2+13,y-r);		vertexBuffer.put(pos+18*2+14,z-r);		
		vertexBuffer.put(pos+18*2+15,x-r);		vertexBuffer.put(pos+18*2+16,y-r);		vertexBuffer.put(pos+18*2+17,z+r);
			
		//RIGHT
		vertexBuffer.put(pos+18*3+0,x+r);		vertexBuffer.put(pos+18*3+1,y+r);		vertexBuffer.put(pos+18*3+2,z+r);		
		vertexBuffer.put(pos+18*3+3,x+r);		vertexBuffer.put(pos+18*3+4,y+r);		vertexBuffer.put(pos+18*3+5,z-r);		
		vertexBuffer.put(pos+18*3+6,x+r);		vertexBuffer.put(pos+18*3+7,y-r);		vertexBuffer.put(pos+18*3+8,z-r);		
		vertexBuffer.put(pos+18*3+9,x+r);		vertexBuffer.put(pos+18*3+10,y+r);		vertexBuffer.put(pos+18*3+11,z+r);		
		vertexBuffer.put(pos+18*3+12,x+r);		vertexBuffer.put(pos+18*3+13,y-r);		vertexBuffer.put(pos+18*3+14,z-r);		
		vertexBuffer.put(pos+18*3+15,x+r);		vertexBuffer.put(pos+18*3+16,y-r);		vertexBuffer.put(pos+18*3+17,z+r);
		
		//BOTTOM
		vertexBuffer.put(pos+18*4+0,x+r);		vertexBuffer.put(pos+18*4+1,y-r);		vertexBuffer.put(pos+18*4+2,z+r);		
		vertexBuffer.put(pos+18*4+3,x-r);		vertexBuffer.put(pos+18*4+4,y-r);		vertexBuffer.put(pos+18*4+5,z+r);		
		vertexBuffer.put(pos+18*4+6,x-r);		vertexBuffer.put(pos+18*4+7,y-r);		vertexBuffer.put(pos+18*4+8,z-r);		
		vertexBuffer.put(pos+18*4+9,x+r);		vertexBuffer.put(pos+18*4+10,y-r);		vertexBuffer.put(pos+18*4+11,z+r);		
		vertexBuffer.put(pos+18*4+12,x-r);		vertexBuffer.put(pos+18*4+13,y-r);		vertexBuffer.put(pos+18*4+14,z-r);		
		vertexBuffer.put(pos+18*4+15,x+r);		vertexBuffer.put(pos+18*4+16,y-r);		vertexBuffer.put(pos+18*4+17,z-r);
		
		
		//TOP
		vertexBuffer.put(pos+18*5+0,x+r);		vertexBuffer.put(pos+18*5+1,y+r);		vertexBuffer.put(pos+18*5+2,z+r);		
		vertexBuffer.put(pos+18*5+3,x-r);		vertexBuffer.put(pos+18*5+4,y+r);		vertexBuffer.put(pos+18*5+5,z+r);		
		vertexBuffer.put(pos+18*5+6,x-r);		vertexBuffer.put(pos+18*5+7,y+r);		vertexBuffer.put(pos+18*5+8,z-r);		
		vertexBuffer.put(pos+18*5+9,x+r);		vertexBuffer.put(pos+18*5+10,y+r);		vertexBuffer.put(pos+18*5+11,z+r);		
		vertexBuffer.put(pos+18*5+12,x-r);		vertexBuffer.put(pos+18*5+13,y+r);		vertexBuffer.put(pos+18*5+14,z-r);		
		vertexBuffer.put(pos+18*5+15,x+r);		vertexBuffer.put(pos+18*5+16,y+r);		vertexBuffer.put(pos+18*5+17,z-r);
		
		
		
	}
	
	public void clear()
	{
		points.clear();
	}
	
	public int renderSize()
	{
		return points.size()*VERTEX_PER_POINT;
	}
	
	//Rendering interface
	
	//size()
	
	public FloatBuffer getVertexBuffer()
	{
		return vertexBuffer;
	}
	
	public int getRenderMode()
	{
		return GLRenderer.RENDERMODE_TRIANGLES;
	}
	
	public Point3D get(int index){
		return points.get(index);		
	}
	
	public int size()
	{
		return points.size();
	}
	
	public ArrayList<Point3D> getPointList()
	{
		return points;
	}
	
	

}

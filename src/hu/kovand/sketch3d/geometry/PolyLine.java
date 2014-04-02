package hu.kovand.sketch3d.geometry;

import hu.kovand.sketch3d.GLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class PolyLine {
public static final String TAG = "PolyLine";
	
	static final int COORDS = 3;
	static final int BYTES_PER_FLOAT = 4;
	
	
	private ArrayList<Point3D> points;	
	private FloatBuffer vertexBuffer;
	
	public PolyLine(int reserve_size)
	{
		points = new ArrayList<Point3D>();
		points.ensureCapacity(reserve_size);
		ByteBuffer bb = ByteBuffer.allocateDirect(reserve_size*COORDS*BYTES_PER_FLOAT);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.position(0);
	}
	

	
	public ArrayList<Point3D> getPoints()	{
		return points;
	}
	
	public void append(Point3D p)
	{	
		points.add(p);
		int pos = COORDS * (points.size()-1);
		vertexBuffer.put(pos,p.getX());
		vertexBuffer.put(pos+1,p.getY());
		vertexBuffer.put(pos+2,p.getZ());
	}
	
	public void clear()
	{
		points.clear();
	}
	
	public int size()
	{
		return points.size();
	}
	
	//Rendering interface
	
	//size()
	
	public FloatBuffer getVertexBuffer()
	{
		return vertexBuffer;
	}
	
	public int getRenderMode()
	{
		return GLRenderer.RENDERMODE_LINE_STRIP;
	}
	
	public Point3D get(int index)
	{
		return points.get(index);
	}
	
	public PolyLine subPolyLine(int start,int end)
	{
		PolyLine pl;
		if (start<=end)
		{
			pl = new PolyLine(end-start+1);
			for (int i=start;i<=end;i++){
				pl.append(get(i));
			}
		}
		else
		{
			pl = new PolyLine(start-end+1);
			for (int i=end;i>=start;i--){
				pl.append(get(i));
			}			
		}
		
		return pl;
		
		
	}
	
	
	

}

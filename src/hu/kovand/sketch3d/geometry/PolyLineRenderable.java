package hu.kovand.sketch3d.geometry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

public class PolyLineRenderable extends PolyLine {
	
	static final int COORDS = 3;
	static final int BYTES_PER_FLOAT = 4;
	
	private FloatBuffer vertexBuffer;
	private int bufferSize;
	private int bufferCapacity;

	public PolyLineRenderable(int reserve) {
		super();
		bufferSize = 0;
		bufferCapacity = reserve;
		ByteBuffer bb = ByteBuffer.allocateDirect(reserve*COORDS*BYTES_PER_FLOAT);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.position(0);
	}

	public PolyLineRenderable(List<Point3D> ps) {
		super(ps);
		bufferSize = ps.size();
		bufferCapacity = ps.size();
		ByteBuffer bb = ByteBuffer.allocateDirect(ps.size()*COORDS*BYTES_PER_FLOAT);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.position(0);
		
		//TODO add the poinnts
	}
	
	@Override
	public void add(Point3D p) {
		super.add(p);
		
		if (bufferSize+1>bufferCapacity){
			resize(bufferCapacity*2);			
		}
		
		int pos = COORDS * bufferSize;
		vertexBuffer.put(pos,p.getX());
		vertexBuffer.put(pos+1,p.getY());
		vertexBuffer.put(pos+2,p.getZ());
		
		bufferSize++;
	}
	
	@Override
	public void add(List<Point3D> ps) {
		super.add(ps);
		
		if (bufferSize+ps.size()>bufferCapacity){
			int newSize;
			if (bufferSize+ps.size()>bufferCapacity*2){
				newSize = bufferSize+ps.size();				
			}
			else{
				newSize = bufferCapacity*2;
			}
			resize(newSize);
			
		}
		
		for (int i=0;i<ps.size();i++)
		{
			int pos = COORDS * bufferSize;
			vertexBuffer.put(pos,ps.get(i).getX());
			vertexBuffer.put(pos+1,ps.get(i).getY());
			vertexBuffer.put(pos+2,ps.get(i).getZ());						
		}
		
		bufferSize+=ps.size();
	}
	
	public FloatBuffer getVertexBuffer()
	{
		return vertexBuffer;
	}
	
	private void resize(int size)
	{
		FloatBuffer newVertexBuffer;
		ByteBuffer bb = ByteBuffer.allocateDirect(size*COORDS*BYTES_PER_FLOAT);
		bb.order(ByteOrder.nativeOrder());
		newVertexBuffer = bb.asFloatBuffer();
		vertexBuffer.position();
		newVertexBuffer.position(0);
		newVertexBuffer.put(vertexBuffer);
		vertexBuffer = newVertexBuffer;
		
		bufferCapacity = size;
				
	}
	
	

}

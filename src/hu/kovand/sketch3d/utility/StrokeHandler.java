package hu.kovand.sketch3d.utility;

import java.nio.FloatBuffer;

import android.view.MotionEvent;
import android.view.View;

import hu.kovand.sketch3d.geometry.Point3D;
import hu.kovand.sketch3d.geometry.PolyLine;
import hu.kovand.sketch3d.geometry.PolyLineRenderable;
import hu.kovand.sketch3d.graphics.GLRenderer;






public class StrokeHandler {
	

	public static int RESERVE_SIZE = 100;
	public static int MIN_POINT_COUNT = 15;
	
	
	PolyLineRenderable curve;
	onStrokeListener listener;

	public StrokeHandler() {
		curve = new PolyLineRenderable(RESERVE_SIZE);
	}
	
	//Rendering interface	
	public int size()
	{
		return curve.size(); 		
	}
	
	public FloatBuffer getVertexBuffer()
	{
		return curve.getVertexBuffer();
	}
	
	public int getRenderMode()
	{
		return GLRenderer.RENDERMODE_LINE_STRIP;
	}
	
	public boolean onTouchEvent(View v,MotionEvent event)
	{
		int action = event.getAction();
		Point3D p = new Point3D(event.getX()-v.getWidth()/2,v.getHeight()/2-event.getY(), Constants.Z_FOR_2D);		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			curve.add(p);
			break;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			curve.add(p);
			if (listener!=null && curve.size()>=MIN_POINT_COUNT){
				listener.onStroke(curve);
			}
			curve = new PolyLineRenderable(RESERVE_SIZE);
			break;

		default:
			break;
		}
		return true;		
	}
	
	public void setOnStrokeListener(onStrokeListener l)
	{
		listener = l;		
	}
	
	public interface onStrokeListener
	{
		boolean onStroke(PolyLine stroke);	
	}
	
	

}

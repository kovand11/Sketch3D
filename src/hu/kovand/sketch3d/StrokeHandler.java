package hu.kovand.sketch3d;

import java.nio.FloatBuffer;

import android.view.MotionEvent;
import android.view.View;

import hu.kovand.sketch3d.geometry.Point3D;
import hu.kovand.sketch3d.geometry.PolyLine;






public class StrokeHandler {
	
	public static int RESERVE_SIZE = 5000;
	public static int MIN_POINT_COUNT = 10;
	
	
	PolyLine curve;
	onStrokeListener listener;

	public StrokeHandler() {
		curve = new PolyLine(RESERVE_SIZE);
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
		Point3D p = new Point3D(event.getX(),v.getHeight()-event.getY(), GlobalConstants.Z_FOR_2D, 0, 0);		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			curve.append(p);
			break;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			curve.append(p);
			if (listener!=null && curve.size()>=MIN_POINT_COUNT){
				listener.onStroke(curve);
			}
			curve = new PolyLine(RESERVE_SIZE);
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
	
	interface onStrokeListener
	{
		boolean onStroke(PolyLine stroke);	
	}
	
	

}

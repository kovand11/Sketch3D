package hu.kovand.sketch3d.utility;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;

import hu.kovand.sketch3d.geometry.PolyLineRenderable;
import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;






public class StrokeHandler {
	

	public static int RESERVE_SIZE = 100;
	public static int MIN_POINT_COUNT = 15;
	
	
	List<Vec2> curve;
	onStrokeListener listener;
	PolyLineRenderable renderable;

	public StrokeHandler() {
		curve = new ArrayList<Vec2>();
		renderable = new PolyLineRenderable(RESERVE_SIZE);
	}
	
	//Rendering interface	
	public int size()
	{
		return renderable.size(); 		
	}
	
	public FloatBuffer getVertexBuffer()
	{
		return renderable.getVertexBuffer();		
	}
	
	
	public boolean onTouchEvent(View v,MotionEvent event)
	{
		int action = event.getAction();
		Vec2 p = new Vec2(event.getX()/v.getWidth()*2.0f-1.0f, 1.0f-event.getY()/v.getHeight()*2.0f);		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			renderable.add(new Vec3(p.getX(), p.getY(), 1.0f));
			curve.add(p);
			if (listener!=null){
				listener.onStrokeBegin(p);		
			}
			break;
		case MotionEvent.ACTION_MOVE:
			renderable.add(new Vec3(p.getX(), p.getY(), 1.0f));
			curve.add(p);
			if (listener!=null){
				listener.onStroke(curve);
			}
			
			break;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			renderable.add(new Vec3(p.getX(), p.getY(), 0.0f));
			curve.add(p);
			if (listener!=null){
				listener.onStrokeEnd(curve,curve.size()>=MIN_POINT_COUNT);
			}
			renderable = new PolyLineRenderable(RESERVE_SIZE);
			curve.clear();
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
		boolean onStrokeBegin(Vec2 addr);
		boolean onStroke(List<Vec2> stroke);
		void onStrokeEnd(List<Vec2> stroke,boolean valid);
	}
	
	

}

package hu.kovand.sketch3d.activity;

import java.util.ArrayList;
import java.util.List;

import hu.kovand.sketch3d.R;
import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.graphics.GLRenderer;
import hu.kovand.sketch3d.graphics.Model3D;
import hu.kovand.sketch3d.model.ModelElement;
import hu.kovand.sketch3d.utility.StrokeHandler;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {	
	public static final String TAG = "MainActivity";
	
	private Context context;	
	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;	
	private ActivityManager activityManager;
	private ConfigurationInfo configurationInfo;
	private boolean supportsEs2;	
	GLRenderer renderer;		

	Model3D model3D;	
	private GestureDetector fingerDetector;
	private GestureDetector penDetector;
	private StrokeHandler strokeHandler;
	private ScaleGestureDetector fingerScaleGestureDetector;
	
	
	//UI
	Switch rotationLockSwitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
			
		super.onCreate(savedInstanceState);
		
		context = getApplicationContext();	
		
		//Getting OpenGL ES support info
		activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		configurationInfo =	activityManager.getDeviceConfigurationInfo();
		supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;	
		
		
		glSurfaceView = new GLSurfaceView(this);
		registerForContextMenu(glSurfaceView);
		if (supportsEs2) {
			glSurfaceView.setEGLContextClientVersion(2);
			renderer = new GLRenderer(context);
			glSurfaceView.setRenderer(renderer);
			rendererSet = true;
		}
		else {
			Toast.makeText(context, "This device does not support OpenGL ES 2.0.",Toast.LENGTH_LONG).show();
			return;
		}		
		setContentView(glSurfaceView);
		
		
		glSurfaceView.setOnTouchListener(onTouchListener);	
		

		
		model3D = new Model3D();		
		strokeHandler = new StrokeHandler();
		strokeHandler.setOnStrokeListener(strokeListener);			
		renderer.setModel3D(model3D);
		renderer.setStrokeHandler(strokeHandler);		
		fingerDetector = new GestureDetector(context,new fingerGestureListener());
		penDetector = new GestureDetector(context,new PenGestureListener());
		fingerScaleGestureDetector = new ScaleGestureDetector(context,fingerScaleGestureListener);
		
			

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (rendererSet){
			glSurfaceView.onPause();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (rendererSet){
			glSurfaceView.onResume();
		}
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.action_rotation_switch);
		View v = item.getActionView();
		rotationLockSwitch = (Switch)v.findViewById(R.id.rotationLockSwitch);
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		switch (id)
		{
		case R.id.action_accept:
			break;
			
		case R.id.action_cancel:
			glSurfaceView.queueEvent(new Runnable() {				
				@Override
				public void run() {
					model3D.clear();					
				}
			});
			break;
			
		case R.id.action_undo:
			break;
			
		case R.id.action_save:
			break;
			
		case R.id.action_settings:
			openSettingsActivity();
			break;
			
		default:
			break;	
			
		}
		
		return true;
	}
	
	
	//
	//procedures
	
	
	
	void openSettingsActivity()
	{
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);		
	}
	
	void openLogActivity()
	{
		//TODO unimplemented
				
	}
	
	

	
	//	
	//event listeners
	
	
	View.OnTouchListener onTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(final View v, final MotionEvent event) {
			
			fingerScaleGestureDetector.onTouchEvent(event);
			
			int id = 0;
			int tool = 0;
			int pointers = event.getPointerCount();
			try{
				id = event.getPointerId(0);
				tool = event.getToolType(id);				
			}
			catch (IllegalArgumentException e){
				return false;			
			}			
			
			if (tool == MotionEvent.TOOL_TYPE_STYLUS)
			{
				penDetector.onTouchEvent(event);
				strokeHandler.onTouchEvent(v, event);
				
	
			}
			else //finger
			{
				if (pointers>1)
				{
					
					
				}
				else
				{
					fingerDetector.onTouchEvent(event);					
				}
			}		
			
			
			return true;
		}
	};
	
	ScaleGestureDetector.OnScaleGestureListener fingerScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
		
		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			//GESTURE finger scale-end
			Log.d(TAG+".scale.end", Float.toString(detector.getScaleFactor()));
		}
		
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			//GESTURE finger scale-begin
			Log.d(TAG+".scale.begin", Float.toString(detector.getScaleFactor()));
			return true;
		}
		
		@Override
		public boolean onScale(final ScaleGestureDetector detector) {
			//GESTURE finger onscale
			
			final float scale = detector.getScaleFactor();
			
			Log.d(TAG+".scale.on", Float.toString(scale));
			
			
			
			glSurfaceView.queueEvent(new Runnable() {
				
				@Override
				public void run() {
					renderer.zoom(scale);
					
				}
			});
			
			return true;
		}
	};
	
	StrokeHandler.onStrokeListener strokeListener = new StrokeHandler.onStrokeListener() {
		
		@Override
		public void onStrokeEnd(List<Vec2> stroke, boolean valid) {
			if (valid)
			{
				List<Vec2> curve = new ArrayList<Vec2>();
				for (int i=0;i<stroke.size();i++)
				{
					if (i%2==0)
					{
						Vec2 addr = model3D.getActiveSurface().findRayIntersection(stroke.get(i), renderer.getMVP());
						Log.d("addr", Float.toString(addr.getX()) + " " + Float.toString(addr.getY()));
						curve.add(addr);
					}
				}
				model3D.addCurve(curve);				
			}
			
		}
		
		@Override
		public boolean onStrokeBegin(Vec2 addr) {
			return false;
		}
		
		@Override
		public boolean onStroke(List<Vec2> stroke) {
			return false;
		}
	};
	
	class fingerGestureListener extends GestureDetector.SimpleOnGestureListener
	{
		
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,final float distanceX, final float distanceY) {
			
			//renderer.move(distanceX , distanceY);
			
			if (rotationLockSwitch.isChecked())
			{
				glSurfaceView.queueEvent(new Runnable() {					
					@Override
					public void run() {
						renderer.move(distanceX , -distanceY);						
					}
				});				
			}
			else
			{
				glSurfaceView.queueEvent(new Runnable() {
					
					@Override
					public void run() {
						renderer.rotate(distanceX , -distanceY);
						
					}
				});
				
			}
			
			
			
			return true;
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Toast.makeText(context, "Finger: Tap",Toast.LENGTH_SHORT).show();
			return super.onSingleTapConfirmed(e);
		}
		
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Toast.makeText(context, "Finger: Doubletap",Toast.LENGTH_SHORT).show();
			return super.onDoubleTap(e);
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			openContextMenu(glSurfaceView);
			super.onLongPress(e);
		}
		
		
		
	};
	
	class PenGestureListener extends GestureDetector.SimpleOnGestureListener
	{
		
		@Override
		public boolean onSingleTapConfirmed(final MotionEvent e) {
			
			glSurfaceView.queueEvent(new Runnable() {
				
				@Override
				public void run() {
					Vec2 p = new Vec2(e.getX()/glSurfaceView.getWidth()*2.0f-1.0f, 1.0f-e.getY()/glSurfaceView.getHeight()*2.0f);
					ModelElement elem = model3D.getElementByScreenPosition(p, glSurfaceView.getWidth()/2, glSurfaceView.getHeight()/2, renderer.getMVP());
					if (elem != null){
						if (model3D.isSelected(elem)){
							model3D.unselect(elem);
						}
						else{
							model3D.select(elem);
						}
					}
					
				}
			});
			
			/*Vec2 p = new Vec2(e.getX()/glSurfaceView.getWidth()*2.0f-1.0f, 1.0f-e.getY()/glSurfaceView.getHeight()*2.0f);
			ModelElement elem = model3D.getElementByScreenPosition(p, glSurfaceView.getWidth()/2, glSurfaceView.getHeight()/2, renderer.getMVP());
			if (elem != null){
				if (model3D.isSelected(elem)){
					model3D.unselect(elem);
				}
				else{
					model3D.select(elem);
				}
				Toast.makeText(context, elem.getId().toString(),Toast.LENGTH_SHORT).show();
			}*/
			return true;
		}	
		
		
		@Override
		public boolean onDoubleTap(final MotionEvent e) {
			Vec2 p = new Vec2(e.getX()/glSurfaceView.getWidth()*2.0f-1.0f, 1.0f-e.getY()/glSurfaceView.getHeight()*2.0f);
			Vec2 mapped = model3D.getActiveSurface().findRayIntersection(p, renderer.getMVP());
			model3D.addPoint(mapped);
			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			openContextMenu(glSurfaceView);
			super.onLongPress(e);
		}
		
	};


			



}

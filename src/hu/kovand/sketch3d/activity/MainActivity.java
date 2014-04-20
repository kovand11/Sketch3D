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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
	Spinner surfaceDefineSpinner; ArrayAdapter<CharSequence> surfaceDefineSpinnerAdapter;

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
		
		item = menu.findItem(R.id.action_surface_define);
		v = item.getActionView();		
		surfaceDefineSpinner = (Spinner)v.findViewById(R.id.surface_define_spinner);
		List<CharSequence> spinnerArray = new ArrayList<CharSequence>();
		surfaceDefineSpinnerAdapter = new ArrayAdapter<CharSequence>(this, R.layout.simple_spinner_dropdown_item_light, spinnerArray);
		surfaceDefineSpinner.setAdapter(surfaceDefineSpinnerAdapter);
		surfaceDefineSpinner.setOnItemSelectedListener( new surfaceDefineListener());
		surfaceDefineSpinnerAdapter.add("Select");
		
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
			model3D.clear();
			glSurfaceView.queueEvent(new Runnable() {				
				@Override
				public void run() {
					model3D.refreshAllBuffer();										
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
		}
		
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			return true;
		}
		
		@Override
		public boolean onScale(final ScaleGestureDetector detector) {
			//GESTURE finger onscale
			
			final float scale = detector.getScaleFactor();
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
						curve.add(addr);
					}
				}
				model3D.addCurve(curve);
				
				glSurfaceView.queueEvent(new Runnable() {					
					@Override
					public void run() {
						model3D.refreshAllBuffer();						
					}
				});
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
			
			glSurfaceView.queueEvent(new Runnable() {
				
				@Override
				public void run() {
					model3D.refreshAllBuffer();					
				}
			});
			
			
			surfaceDefineSpinnerAdapter.clear();			
			surfaceDefineSpinnerAdapter.add("Select");
			
			List<String> defs = model3D.getPossibleSurfaceDefinitions();
			surfaceDefineSpinner.setSelection(0);
			for (int i=0;i<defs.size();i++)
			{
				surfaceDefineSpinnerAdapter.add(defs.get(i));						
			}			
								
		

			return true;
		}	
		
		
		@Override
		public boolean onDoubleTap(final MotionEvent e) {
			Vec2 p = new Vec2(e.getX()/glSurfaceView.getWidth()*2.0f-1.0f, 1.0f-e.getY()/glSurfaceView.getHeight()*2.0f);
			Vec2 mapped = model3D.getActiveSurface().findRayIntersection(p, renderer.getMVP());
			model3D.addPoint(mapped);
			glSurfaceView.queueEvent(new Runnable() {
				
				@Override
				public void run() {
					model3D.refreshAllBuffer();
					
				}
			});
			return true;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			openContextMenu(glSurfaceView);
			super.onLongPress(e);
		}
		
	};
	
	
	class surfaceDefineListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			
			
			final String str = (String)parent.getItemAtPosition(position);
			
			model3D.changeActiveSurface(str);
			
			glSurfaceView.queueEvent(new Runnable() {				
				@Override
				public void run() {
					model3D.refreshAllBuffer();
				}
			});
						
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
		
	};
	

	
	

	
	


			



}

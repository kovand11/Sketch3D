package hu.kovand.sketch3d.activity;

import hu.kovand.sketch3d.R;
import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.graphics.GLRenderer;
import hu.kovand.sketch3d.graphics.Model3D;
import hu.kovand.sketch3d.graphics.ModelOverlay;
import hu.kovand.sketch3d.model.ModelCurve;
import hu.kovand.sketch3d.model.ModelElement;
import hu.kovand.sketch3d.model.ModelSurface;
import hu.kovand.sketch3d.utility.StrokeHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
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

	Model3D model3D; List<Model3D> model3DList;	boolean isModelChanged;
	ModelOverlay modelOverlay;
	private GestureDetector fingerDetector;
	private GestureDetector penDetector;
	private StrokeHandler strokeHandler;
	private ScaleGestureDetector fingerScaleGestureDetector;
	
	
	//UI
	Switch rotationLockSwitch;
	Spinner surfaceDefineSpinner; ArrayAdapter<CharSequence> surfaceDefineSpinnerAdapter;
	Spinner elementDefineSpinner; ArrayAdapter<CharSequence> elementDefineSpinnerAdapter;

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
		model3DList = new ArrayList<Model3D>();
		modelOverlay = new ModelOverlay();
		
		isModelChanged = false;

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
		
		//Reference to roatationSwitch
		MenuItem item = menu.findItem(R.id.action_rotation_switch);
		View v = item.getActionView();
		rotationLockSwitch = (Switch)v.findViewById(R.id.rotationLockSwitch);
		
		//Reference to surfaceDefineSpinner and assign adapter
		item = menu.findItem(R.id.action_surface_define);
		v = item.getActionView();		
		surfaceDefineSpinner = (Spinner)v.findViewById(R.id.surface_define_spinner);
		List<CharSequence> surfaceSpinnerArray = new ArrayList<CharSequence>();
		surfaceDefineSpinnerAdapter = new ArrayAdapter<CharSequence>(this, R.layout.simple_spinner_dropdown_item_light, surfaceSpinnerArray);
		surfaceDefineSpinner.setAdapter(surfaceDefineSpinnerAdapter);
		surfaceDefineSpinner.setOnItemSelectedListener( new surfaceDefineListener());
		surfaceDefineSpinnerAdapter.add(Model3D.DEFINE_SURFACE_DEFAULT);
		
		//Reference to elementDefineSpinner and assign adapter
		item = menu.findItem(R.id.action_element_define);
		v = item.getActionView();
		elementDefineSpinner = (Spinner)v.findViewById(R.id.element_define_spinner);
		List<CharSequence> elementSpinnerArray = new ArrayList<CharSequence>();
		elementDefineSpinnerAdapter = new ArrayAdapter<CharSequence>(this, R.layout.simple_spinner_dropdown_item_light, elementSpinnerArray);
		elementDefineSpinner.setAdapter(elementDefineSpinnerAdapter);
		//elementDefineSpinner.setOnItemSelectedListener( new elementDefineListener());
		elementDefineSpinnerAdapter.add(Model3D.DEFINE_ELEMENT_DEFAULT);
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id)
		{
		case R.id.action_commit:
			Toast.makeText(context, "Commited: Id=" + Integer.toString(model3DList.size()),Toast.LENGTH_SHORT).show();
			Model3D commited = new Model3D(model3D);
			model3DList.add(commited);
			break;
			
		
			
		case R.id.action_undo:			
			if (isModelChanged)
			{
				if (model3DList.size()>=1)
				{
					Toast.makeText(context, "Restored: Id=" + Integer.toString(model3DList.size()-1),Toast.LENGTH_SHORT).show();
					model3D = new Model3D (model3DList.get(model3DList.size()-1));
					isModelChanged = false;
					glSurfaceView.queueEvent(new Runnable() {						
						@Override
						public void run() {
							model3D.refreshBuffer(Model3D.REFRESH_BUFFER_ALL);
							renderer.setModel3D(model3D);							
						}
					});															
				}				
			}
			else
			{
				if (model3DList.size()>=2)
				{
					Toast.makeText(context, "Restored: Id=" + Integer.toString(model3DList.size()-2),Toast.LENGTH_SHORT).show();
					model3D = new Model3D (model3DList.get(model3DList.size()-2));
					isModelChanged = false;
					model3DList.remove(model3DList.size()-1);
					glSurfaceView.queueEvent(new Runnable() {						
						@Override
						public void run() {
							model3D.refreshBuffer(Model3D.REFRESH_BUFFER_ALL);
							renderer.setModel3D(model3D);							
						}
					});				
				}
			}				
			break;
			
		case R.id.action_cancel:
			Toast.makeText(context, "Cancel",Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.action_save:
			Toast.makeText(context, "Save",Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.action_settings:
			openSettingsActivity();
			break;
			
		default:
			break;	
			
		}
		
		return true;
	}
	
	void openSettingsActivity()
	{
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);		
	}

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
		public void onStrokeEnd(final List<Vec2> stroke, boolean valid) {
			if (valid)
			{
				
				isModelChanged = true;
				
				final ModelSurface s = (ModelSurface)model3D.getElementById(model3D.getActiveSurface());
				
				glSurfaceView.queueEvent(new Runnable() {
					
					@Override
					public void run() {
						modelOverlay.importSurface(model3D, s, model3D.exportActiveSurfaceAndDelete(), renderer.getMVP(), glSurfaceView.getWidth()/2, glSurfaceView.getHeight()/2);
						modelOverlay.addCurve(stroke);
						model3D.importActiveSurface(modelOverlay.exportSurface());
						model3D.refreshBuffer(Model3D.REFRESH_BUFFER_CURVE_ADD);
						
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
			UUID elem = model3D.getElementByScreenPosition(p, glSurfaceView.getWidth()/2, glSurfaceView.getHeight()/2, renderer.getMVP());
			
			if (elem != null){
				if (model3D.isSelected(elem)){
					model3D.unselectElement(elem);
				}
				else{
					model3D.selectElement(elem);
				}
			}	
			
			glSurfaceView.queueEvent(new Runnable() {
				
				@Override
				public void run() {
					model3D.refreshBuffer(Model3D.REFRESH_BUFFER_ALL);
					//TODO consider distinguish point and curve
				}
			});
			
			
			surfaceDefineSpinnerAdapter.clear();			
			surfaceDefineSpinnerAdapter.add(Model3D.DEFINE_SURFACE_DEFAULT);
			
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
			final Vec2 p = new Vec2(e.getX()/glSurfaceView.getWidth()*2.0f-1.0f, 1.0f-e.getY()/glSurfaceView.getHeight()*2.0f);
			final ModelSurface s = (ModelSurface)model3D.getElementById(model3D.getActiveSurface());
			final UUID id = model3D.getElementByScreenPosition(p, 2.0f, 2.0f, renderer.getMVP());
			if (model3D.isSelected(id) && model3D.getElementById(id).getType() == ModelElement.TYPE_CURVE)
			{
				glSurfaceView.queueEvent(new Runnable() {
					
					@Override
					public void run() {						
						model3D.increaseBsplineHint(id);
						model3D.refreshBuffer(Model3D.REFRESH_BUFFER_ALL);
						//TODO make const
						
					}
				});
				
			}
			else
			{
				glSurfaceView.queueEvent(new Runnable() {				
					@Override
					public void run() {
						modelOverlay.importSurface(model3D, s, model3D.exportActiveSurfaceAndDelete(), renderer.getMVP(), glSurfaceView.getWidth()/2, glSurfaceView.getHeight()/2);
						modelOverlay.addPoint(p);
						model3D.importActiveSurface( modelOverlay.exportSurface());
						model3D.refreshBuffer(Model3D.REFRESH_BUFFER_POINT_ADD);					
					}
				});
				
			}
			
			isModelChanged = true;
			
			
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
			
			model3D.defineActiveSurface(str);
			if (str != Model3D.DEFINE_SURFACE_DEFAULT){
				model3D.unselectAll();
				surfaceDefineSpinnerAdapter.clear();			
				surfaceDefineSpinnerAdapter.add(Model3D.DEFINE_SURFACE_DEFAULT);
				surfaceDefineSpinnerAdapter.notifyDataSetChanged();
			}			
			glSurfaceView.queueEvent(new Runnable() {				
				@Override
				public void run() {
					model3D.refreshBuffer(Model3D.REFRESH_BUFFER_ALL);
				}
			});						
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {			
		}
		
	};
	
	/*class elementDefineListener implements OnItemSelectedListener{

		/*@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			
			final String str = (String)parent.getItemAtPosition(position);
			
			model3D.defineActiveSurface(str);
			
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
		
	};	*/


			



}

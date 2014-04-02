package hu.kovand.sketch3d;

import hu.kovand.sketch3d.geometry.HybridCurve;
import hu.kovand.sketch3d.geometry.Point3D;
import hu.kovand.sketch3d.geometry.PolyLine;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.text.style.StrikethroughSpan;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
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
	ModelScreen modelScreen;
	Model3D model3D;	
	private GestureDetector fingerDetector;
	private GestureDetector penDetector;
	private StrokeHandler strokeHandler;
	private ScaleGestureDetector fingerScaleGestureDetector;
	
	String logText;

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
			// Request an OpenGL ES 2.0 compatible context.
			glSurfaceView.setEGLContextClientVersion(2);
			// Assign our renderer.
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
		

		
		modelScreen = new ModelScreen();
		model3D = new Model3D();
		
		strokeHandler = new StrokeHandler();
		strokeHandler.setOnStrokeListener(strokeListener);
		
		
		renderer.setModel3D(model3D);
		renderer.setStrokeHandler(strokeHandler);
		renderer.setModelScreen(modelScreen);
		
			
		
		fingerDetector = new GestureDetector(context,new fingerGestureListener());
		penDetector = new GestureDetector(context,new PenGestureListener());
		
		logText = new String();
		
		
		//
		//testonly
		
		HybridCurve.testoutput = modelScreen;		
		
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.context_menu, menu);
	}
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		switch (id)
		{
		case R.id.action_accept:
			break;
			
		case R.id.action_cancel:
			modelScreen.clear();
			break;
			
		case R.id.action_undo:
			break;
			
		case R.id.action_save:
			break;
			
		case R.id.action_settings:
			openSettings();
			break;
			
		default:
			break;	
			
		}
		
		return true;
	}
	
	
	//
	//procedures
	
	void openSettings()
	{
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);		
	}
	

	
	

	
	//	
	//event listeners
	
	
	View.OnTouchListener onTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {	
			
			int id = 0;
			int tool = 0;
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
				fingerDetector.onTouchEvent(event);
			}		
			
			//multitouch later
			
			return true;
		}
	};
	
	ScaleGestureDetector.OnScaleGestureListener fingerScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
		
		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	StrokeHandler.onStrokeListener strokeListener = new StrokeHandler.onStrokeListener() {
		
		@Override
		public boolean onStroke(PolyLine stroke) {
			modelScreen.addHybridCurve(stroke, ModelScreen.MERGE_DISABLED);
			return true;
		}
	};
	
	class fingerGestureListener extends GestureDetector.SimpleOnGestureListener
	{
		
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			Toast.makeText(context, "Finger: Scroll",Toast.LENGTH_SHORT).show();
			return super.onScroll(e1, e2, distanceX, distanceY);
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
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Toast.makeText(context, "Pen: Tap",Toast.LENGTH_SHORT).show();
			return super.onSingleTapConfirmed(e);
		}	
		
		
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Toast.makeText(context, "Pen: Doubletap",Toast.LENGTH_SHORT).show();
			Point3D p = new Point3D(e.getX(),glSurfaceView.getHeight() -  e.getY(), GlobalConstants.Z_FOR_2D, 0, 0);
			modelScreen.addPoint(p);
			return super.onDoubleTap(e);
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			openContextMenu(glSurfaceView);
			super.onLongPress(e);
		}
		
	};


			



}

package hu.kovand.sketch3d.graphics;

import hu.kovand.sketch3d.R;
import hu.kovand.sketch3d.utility.Constants;
import hu.kovand.sketch3d.utility.ShaderHelper;
import hu.kovand.sketch3d.utility.StrokeHandler;
import hu.kovand.sketch3d.utility.TextResourceReader;


import java.nio.FloatBuffer;
import java.util.ArrayList;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;


public class GLRenderer implements Renderer {	
	public static final String TAG = "GLRenderer";
	
	private int width;
	private int height;
	
	private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private static final String U_MVPMATRIX = "u_MVPMatrix";
    private int uColorLocation = 0;
    private int aPositionLocation = 0;
    private int uMVPMatrixLocation = 0;
    
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int BYTES_PER_FLOAT = 4;
    private final Context context;
    private int program = 0;
    
    //
    //colors
    private static final float[] GESTURE_COLOR = { 0.6f ,0.1f ,0.1f ,0.7f} ;
    private static final float[] SURFACE_POINT_COLOR = { 0.1f ,0.3f ,0.7f ,0.7f} ;
    private static final float[] SURFACE_CURVE_COLOR = { 0.1f ,0.3f ,0.9f ,0.7f} ;
    private static final float[] MODEL_POINT_COLOR = { 0.05f ,0.05f ,0.05f ,0.8f} ;
    private static final float[] MODEL_CURVE_COLOR = { 0.1f ,0.1f ,0.1f ,0.8f} ;
    
    public static final int RENDERMODE_POINTS = 0;
    public static final int RENDERMODE_LINE_STRIP = 1;
    public static final int RENDERMODE_TRIANGLES = 2;
    
    
    

    private final float[] MVPMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix= new float[16];
    
    
    


    StrokeHandler strokeHandler;
    ModelScreen modelScreen;
    Model3D model3D;

    

 	public GLRenderer(Context context)
	{
		this.context = context;	        
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {		
		GLES20.glClearColor(0.9f, 0.9f, 0.9f, 0.0f);
		GLES20.glLineWidth(4.0f);
		
		String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.fragment_shader);
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        GLES20.glUseProgram(program);
        
        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);        
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        uMVPMatrixLocation = GLES20.glGetUniformLocation(program, U_MVPMATRIX);
        
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        

        
	}
	
	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		
		this.width = width;
		this.height = height;
		
		Matrix.perspectiveM(projectionMatrix, 0, 45.0f, (1.0f*width)/height, Constants.MODEL_FRONT_Z, Constants.MODEL_BACK_Z);
		Matrix.setLookAtM(viewMatrix, 0, 0, 0, (float)(height/(2*Math.tan(Math.toRadians(45.0/2)))), 0, 0, 0f, 0f, height/2, 0.0f);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
		
		
		

	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		
		
		  
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT );
		
		FloatBuffer vertexData;
		int size;
		int coords;
		
		//
		//2D Draw
		//
		
		//GesureHandler
		//
		vertexData = strokeHandler.getVertexBuffer();
		size = strokeHandler.size();
		coords = 3;
		//		
		vertexData.position(0);
		GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT ,GLES20.GL_FLOAT, true, coords * BYTES_PER_FLOAT , vertexData);        
        GLES20.glUniform4fv(uColorLocation, 1, GESTURE_COLOR , 0);        
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);        
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, size);
        GLES20.glDisableVertexAttribArray(aPositionLocation);		
		
		
		//ModelSurface.PointSet
		//
		vertexData = modelScreen.getPointSet().getVertexBuffer();
		size = modelScreen.getPointSet().renderSize();		
		
		coords = 3;
		//	
		vertexData.position(0);
		GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT ,GLES20.GL_FLOAT, true, coords * BYTES_PER_FLOAT , vertexData);        
        GLES20.glUniform4fv(uColorLocation, 1, SURFACE_POINT_COLOR , 0);
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);        
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, size);
        GLES20.glDisableVertexAttribArray(aPositionLocation);
        
        
        //ModelSurface.HybridLines
        //       
        ArrayList<FloatBuffer> vertexDataList = modelScreen.getStoredCurvesVertexBufferList();
        ArrayList<Integer> sizeList = modelScreen.getStoredCurvesSizeList();
        int curve_count = vertexDataList.size();
        for (int i=0;i<curve_count;i++)
        {
        	vertexData = vertexDataList.get(i);
    		size = sizeList.get(i);		
    		vertexData.position(0);
    		GLES20.glEnableVertexAttribArray(aPositionLocation);
            GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT ,GLES20.GL_FLOAT, true, coords * BYTES_PER_FLOAT , vertexData);        
            GLES20.glUniform4fv(uColorLocation, 1, SURFACE_CURVE_COLOR , 0);        
            GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);        
            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, size);
            GLES20.glDisableVertexAttribArray(aPositionLocation);        	
        }    
        
        
        
        //
        //3D Draw
        //
		
		
		
		
		
	}
	
	
	public void setModel3D(Model3D m)
	{
		model3D = m;
	}
	
	public void setModelScreen(ModelScreen m)
	{
		modelScreen = m;		
	}
	
	public void setStrokeHandler(StrokeHandler sh)
	{
		strokeHandler = sh;		
	}
	
	
	
	
	
}

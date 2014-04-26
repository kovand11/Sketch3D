package hu.kovand.sketch3d.graphics;

import hu.kovand.sketch3d.R;
import hu.kovand.sketch3d.geometry.PolyLine;
import hu.kovand.sketch3d.geometry.PolyLineRenderable;
import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.geometry.Vec3Renderable;
import hu.kovand.sketch3d.geometry.Vec4;
import hu.kovand.sketch3d.utility.Constants;
import hu.kovand.sketch3d.utility.ShaderHelper;
import hu.kovand.sketch3d.utility.StrokeHandler;
import hu.kovand.sketch3d.utility.TextResourceReader;


import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;


public class GLRenderer implements Renderer {	
	public static final String TAG = "GLRenderer";
	
	
	private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private static final String U_MVPMATRIX = "u_MVPMatrix";
    private int uColorLocation = 0;
    private int aPositionLocation = 0;
    private int uMVPMatrixLocation = 0;
    
    private static final int COORDS = 3;
    private static final int BYTES_PER_FLOAT = 4;
    private final Context context;
    private int program = 0;
    
    //
    //colors
    private static final float[] GESTURE_COLOR = { 1.0f ,0.28f ,0.0f ,0.7f};
    private static final float[] SELECTED_POINT_COLOR = {  0.7f ,0.1f ,0.1f ,0.9f};//dark red
    private static final float[] SELECTED_CURVE_COLOR = { 0.8f ,0.1f ,0.1f ,0.9f}; //dark red
    private static final float[] ACTIVE_POINT_COLOR = { 0.1f ,0.3f ,0.7f ,0.7f};
    private static final float[] ACTIVE_CURVE_COLOR = { 0.1f ,0.3f ,0.9f ,0.7f}; 
    private static final float[] EXTRA_POINT_COLOR = { 0.1f ,0.6f ,0.1f ,0.7f};
    private static final float[] PASSIVE_POINT_COLOR = { 0.05f ,0.05f ,0.05f ,0.8f};
    private static final float[] PASSIVE_CURVE_COLOR = { 0.1f ,0.1f ,0.1f ,0.8f};
    private static final float[] SURFACE_BOARDER_COLOR = { 0.0f ,0.0f ,0.0f ,0.1f};
    
    public static final int RENDERMODE_POINTS = 0;
    public static final int RENDERMODE_LINE_STRIP = 1;
    public static final int RENDERMODE_TRIANGLES = 2;
    
    public static final float ROTATION_COEFF = 0.1f;
    
    
    

    private final float[] MVPMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix= new float[16];
    private final float[] identityMatrix= new float[16];
    
    
    
    


    StrokeHandler strokeHandler;
    ModelOverlay modelOverlay;
    Model3D model3D;
    
    
    //perspective
    float fovy;
    float aspect;
    float zNear;
    float zFar;
    
    //view

    
    float[] eyeVec;    
    float[] centerVec;    
    float[] upVec;
    
    //from upVec
    float[] rightVec;

    
    
    
    
    

    

 	public GLRenderer(Context context)
	{
		this.context = context;
		
		eyeVec = new float[4];
		centerVec = new float[4];
		upVec = new float[4];
		rightVec = new float[4];
		
		
		
		Matrix.setIdentityM(identityMatrix, 0);

		
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
		

		
		
			
		fovy = 45.0f;
	    aspect = (1.0f*width)/height;
	    zNear = Constants.MODEL_FRONT_Z;
	    zFar = Constants.MODEL_BACK_Z;
	    
	    //view
	    eyeVec[0] = 0.0f;
	    eyeVec[1] = 0.0f;
	    eyeVec[2] = (float)(height/(2*Math.tan(Math.toRadians(45.0/2))));
	    eyeVec[3] = 1.0f;
	    
	    centerVec[0] = 0.0f;
	    centerVec[1] = 0.0f;
	    centerVec[2] = 0.0f;
	    centerVec[3] = 1.0f;
	    
	    
	    upVec[0] = 0.0f;
	    upVec[1] = 1.0f;
	    upVec[2] = 0.0f;
	    upVec[3] = 1.0f;
	    
	    recalculateMatrices();	


	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		
		
		  
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT );
		
		FloatBuffer vertexData;
		int size;
		
		List<FloatBuffer> vertexDataList;
		List<Integer> sizeList;
		

		//GesureHandler
		//
		vertexData = strokeHandler.getVertexBuffer();
		size = strokeHandler.size();
		//		
		vertexData.position(0);
		GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, COORDS ,GLES20.GL_FLOAT, true, COORDS * BYTES_PER_FLOAT , vertexData);        
        GLES20.glUniform4fv(uColorLocation, 1, GESTURE_COLOR , 0);        
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, identityMatrix, 0);        
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, size);
        GLES20.glDisableVertexAttribArray(aPositionLocation);        

        //
        //Model3D
        //
        
        
        //selected points
        //
        vertexDataList = model3D.getSelectedPointsVertexBufferList();
        for (int i=0;i<vertexDataList.size();i++)
        {
        	vertexData = vertexDataList.get(i);
        	vertexData.position(0);
    		GLES20.glEnableVertexAttribArray(aPositionLocation);
            GLES20.glVertexAttribPointer(aPositionLocation, COORDS ,GLES20.GL_FLOAT, true, COORDS * BYTES_PER_FLOAT , vertexData);        
            GLES20.glUniform4fv(uColorLocation, 1, SELECTED_POINT_COLOR , 0);        
            GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);        
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, Vec3Renderable.VERTEX_PER_POINT);
            GLES20.glDisableVertexAttribArray(aPositionLocation);        	
        }
        
        //active points
        //
        vertexDataList = model3D.getActivePointsVertexBufferList();
        //fixed size
        for (int i=0;i<vertexDataList.size();i++)
        {
        	vertexData = vertexDataList.get(i);
        	vertexData.position(0);
    		GLES20.glEnableVertexAttribArray(aPositionLocation);
            GLES20.glVertexAttribPointer(aPositionLocation, COORDS ,GLES20.GL_FLOAT, true, COORDS * BYTES_PER_FLOAT , vertexData);        
            GLES20.glUniform4fv(uColorLocation, 1, ACTIVE_POINT_COLOR , 0);        
            GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);        
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, Vec3Renderable.VERTEX_PER_POINT);
            GLES20.glDisableVertexAttribArray(aPositionLocation);        	
        }
        
        //active curves
        //
        vertexDataList = model3D.getActiveCurvesVertexBufferList();
        sizeList =model3D.getActiveCurvesSizeList();
        for (int i=0;i<vertexDataList.size();i++)
        {
        	vertexData = vertexDataList.get(i);
        	size = sizeList.get(i);
        	vertexData.position(0);
    		GLES20.glEnableVertexAttribArray(aPositionLocation);
            GLES20.glVertexAttribPointer(aPositionLocation, COORDS ,GLES20.GL_FLOAT, true, COORDS * BYTES_PER_FLOAT , vertexData);        
            GLES20.glUniform4fv(uColorLocation, 1, ACTIVE_CURVE_COLOR , 0);        
            GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);        
            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, size);
            GLES20.glDisableVertexAttribArray(aPositionLocation);        	
        }
        
        //EXTRA POINTS
        vertexDataList = model3D.getExtraPointsVertexBufferList();
        //fixed size
        for (int i=0;i<vertexDataList.size();i++)
        {
        	vertexData = vertexDataList.get(i);
        	vertexData.position(0);
    		GLES20.glEnableVertexAttribArray(aPositionLocation);
            GLES20.glVertexAttribPointer(aPositionLocation, COORDS ,GLES20.GL_FLOAT, true, COORDS * BYTES_PER_FLOAT , vertexData);        
            GLES20.glUniform4fv(uColorLocation, 1, EXTRA_POINT_COLOR , 0);        
            GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);        
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, Vec3Renderable.VERTEX_PER_POINT);
            GLES20.glDisableVertexAttribArray(aPositionLocation);        	
        }
        
        
        
        //passive points
        //
        vertexDataList = model3D.getPassivePointsVertexBufferList();
        //fixed size
        for (int i=0;i<vertexDataList.size();i++)
        {
        	vertexData = vertexDataList.get(i);
        	vertexData.position(0);
    		GLES20.glEnableVertexAttribArray(aPositionLocation);
            GLES20.glVertexAttribPointer(aPositionLocation, COORDS ,GLES20.GL_FLOAT, true, COORDS * BYTES_PER_FLOAT , vertexData);        
            GLES20.glUniform4fv(uColorLocation, 1, PASSIVE_POINT_COLOR , 0);        
            GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);        
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, Vec3Renderable.VERTEX_PER_POINT);
            GLES20.glDisableVertexAttribArray(aPositionLocation);        	
        }
        
        //passive curves
        //
        vertexDataList = model3D.getPassiveCurvesVertexBufferList();
        sizeList =model3D.getPassiveCurvesSizeList();
        for (int i=0;i<vertexDataList.size();i++)
        {
        	vertexData = vertexDataList.get(i);
        	size = sizeList.get(i);
        	vertexData.position(0);
    		GLES20.glEnableVertexAttribArray(aPositionLocation);
            GLES20.glVertexAttribPointer(aPositionLocation, COORDS ,GLES20.GL_FLOAT, true, COORDS * BYTES_PER_FLOAT , vertexData);        
            GLES20.glUniform4fv(uColorLocation, 1, PASSIVE_CURVE_COLOR , 0);        
            GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);        
            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, size);
            GLES20.glDisableVertexAttribArray(aPositionLocation);        	
        }
        
        
        
        
		
		
		
		
		
	}
	
	
	public void setModel3D(Model3D m)
	{
		model3D = m;
	}
	
	public void setModelOverlay(ModelOverlay m)
	{
		modelOverlay = m;		
	}
	
	public void setStrokeHandler(StrokeHandler sh)
	{
		strokeHandler = sh;		
	}
	
	
	
	public void zoom(float ratio)
	{		
		Vec4 diff = Vec4.subtractAsNorm(new Vec4(eyeVec),new Vec4 (centerVec));
		diff = Vec4.multiplyAsNorm(diff, 1/ratio); 		
		eyeVec = Vec4.addAsNorm(new Vec4(centerVec), diff).toArray();
		recalculateMatrices();
	}
	
	public void move(float dx,float dy)
	{		
		recalculateRightVec();
		String s = (new Vec4(upVec)).toString()+" "+(new Vec4(rightVec)).toString();
		
		eyeVec = Vec4.addAsNorm(new Vec4(eyeVec), Vec4.multiplyAsNorm(new Vec4(rightVec), dx)).toArray();
		eyeVec = Vec4.addAsNorm(new Vec4(eyeVec), Vec4.multiplyAsNorm(new Vec4(upVec), dy)).toArray();
		centerVec = Vec4.addAsNorm(new Vec4(centerVec), Vec4.multiplyAsNorm(new Vec4(rightVec), dx)).toArray();
		centerVec = Vec4.addAsNorm(new Vec4(centerVec), Vec4.multiplyAsNorm(new Vec4(upVec), dy)).toArray();
		recalculateMatrices();		
	}
	
	public void rotate(float rx ,float ry)
	{
		rx*=ROTATION_COEFF;
		ry*=ROTATION_COEFF;
		
		recalculateRightVec();
		float[] scrollVec = Vec4.addAsNorm(Vec4.multiplyAsNorm(new Vec4(rightVec), rx), Vec4.multiplyAsNorm(new Vec4(upVec), ry)).toArray();
		float[] diff = Vec4.subtractAsNorm(new Vec4(eyeVec), new Vec4(centerVec)).toArray();
		float[] rotM = new float[16];		
		Matrix.setRotateM(rotM, 0, 90.0f, diff[0]/diff[3], diff[1]/diff[3], diff[2]/diff[3]);
		float[] rotVec = new float[4];
		Matrix.multiplyMV(rotVec, 0, rotM,0 , scrollVec, 0);
		Matrix.setRotateM(rotM, 0, Vec4.lengthAsNorm(new Vec4(scrollVec)), rotVec[0]/rotVec[3], rotVec[1]/rotVec[3], rotVec[2]/rotVec[3]);
		
		float[] oldEyeVec = (new Vec4(eyeVec)).toArray();
		float[] oldUpVec = (new Vec4(upVec)).toArray();
		
		Matrix.multiplyMV(eyeVec, 0, rotM,0 , oldEyeVec, 0);
		Matrix.multiplyMV(upVec, 0, rotM,0 , oldUpVec, 0);		
		recalculateMatrices();		
	}
	
	
	
	//calc
	
	void recalculateMatrices()
	{	

		Matrix.perspectiveM(projectionMatrix, 0, fovy, aspect, zNear, zFar);		
		Matrix.setLookAtM(viewMatrix, 0, eyeVec[0]/eyeVec[3], eyeVec[1]/eyeVec[3], eyeVec[2]/eyeVec[3],
				centerVec[0]/centerVec[3], centerVec[1]/centerVec[3], centerVec[2]/centerVec[3],
				upVec[0]/upVec[3], upVec[1]/upVec[3], upVec[2]/upVec[3]);
		Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);		
	}
	
	void recalculateRightVec()
	{	
		float[] diff = Vec4.subtractAsNorm(new Vec4(eyeVec), new Vec4(centerVec)).toArray();
		float[] rotM = new float[16];		
		Matrix.setRotateM(rotM, 0, -90.0f, diff[0]/diff[3], diff[1]/diff[3], diff[2]/diff[3]);
		Matrix.multiplyMV(rightVec, 0, rotM,0 , upVec, 0);		
	}
	
	
	public static String matToString(float[] m)
	{
		String res = "";
		res += Float.toString(m[ 0]) + " " +Float.toString(m[ 4]) + " " + Float.toString(m[ 8]) + " " + Float.toString(m[12]) + "\n";
		res += Float.toString(m[ 1]) + " " +Float.toString(m[ 5]) + " " + Float.toString(m[ 9]) + " " + Float.toString(m[13]) + "\n";
		res += Float.toString(m[ 2]) + " " +Float.toString(m[ 6]) + " " + Float.toString(m[10]) + " " + Float.toString(m[14]) + "\n";
		res += Float.toString(m[ 3]) + " " +Float.toString(m[ 7]) + " " + Float.toString(m[11]) + " " + Float.toString(m[15]);
		
		return res;
		
	}
	
	public float[] getMVP()
	{
		return MVPMatrix;
		
	}
	
	
}

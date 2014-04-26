package hu.kovand.sketch3d.graphics;

import java.util.ArrayList;
import java.util.List;

import hu.kovand.sketch3d.geometry.PolyLine;
import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.geometry.Vec4;
import hu.kovand.sketch3d.model.ModelCurve;
import hu.kovand.sketch3d.model.ModelPoint;
import hu.kovand.sketch3d.model.ModelSurface;

public class ModelOverlay {
	public static final String TAG = "ModelOverlay";
	
	public static final int POINT_METHOD_NOMERGE = 0;
	public static final int POINT_METHOD_MERGE_TO_CLOSEST = 1; //curve stays
	
	public static final int CURVE_METHOD_NOMERGE = 2;
	public static final int CURVE_METHOD_MERGE_STANDARD = 3;
	
	private static final int POINT_TO_CURVE_MERGE_RANGE = 50; //pixel
	private static final int CURVE_TO_POINT_MERGE_RANGE = 50; 
	private static final int CURVE_TO_CURVE_MERGE_LOWER = 100;
	private static final int CURVE_TO_CURVE_MERGE_UPPER = 300;
	
	
	
	
	
	
	private List<ModelPoint> points;
	private List<ModelCurve> curves;
	private ModelSurface surface;
	

	/**
	 * Constructor
	 */
	public ModelOverlay() {
		points = new ArrayList<ModelPoint>();
		curves = new ArrayList<ModelCurve>();
		surface = null;
	}
	
	/**
	 * Imports a 3d surface, and stores them in simmetric device pixel coords.
	 * @param s surface
	 * @param mvp modelview and projection matrix
	 * @param lx length of the x unit in pixels
	 * @param ly length of the y unit in pixels
	 */
	public void importSurface(ModelSurface s,float[] mvp,int lx,int ly)
	{
		
	}
	
	/**
	 * Exports the working surface to 3d model space.
	 * @param mvp modelview and projection matrix (must be the same as it was at the import)
	 * @param lx length of the x unit in pixels
	 * @param ly length of the y unit in pixels
	 * @return
	 */
	public ModelSurface exportSurface(float[] mvp,int lx,int ly)
	{
		return surface;		
	}

	/**
	 * Clears the working surface.
	 */
	public void clear()
	{
		points = new ArrayList<ModelPoint>();
		curves = new ArrayList<ModelCurve>();
		surface = null;				
	}
	
	/**
	 * Adds a point to the working surface.
	 * @param addr point address in pixel coords
	 * @param method Defines the method to add the point
	 */
	public void addPoint(Vec2 addr,int method)
	{
		if (method == POINT_METHOD_NOMERGE){
			//surface.addPoint(addr);
		}
		else if (method == POINT_METHOD_MERGE_TO_CLOSEST)
		{
			
		}
		
	}
	
	
	
	
	
	/**
	 * Adds a curve (polyline representation) to the working model.
	 * @param curve series of point addresses in pixel
	 * @param method Defines the method to add the curve
	 */
	public void addCurve(PolyLine curve,int method)
	{
		
	}
	
	
	
	
	
	
	
	
}

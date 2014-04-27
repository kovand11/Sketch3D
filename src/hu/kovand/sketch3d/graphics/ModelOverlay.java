package hu.kovand.sketch3d.graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.util.Log;

import hu.kovand.sketch3d.geometry.BSpline;
import hu.kovand.sketch3d.geometry.PolyLine;
import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.geometry.Vec4;
import hu.kovand.sketch3d.model.ModelCurve;
import hu.kovand.sketch3d.model.ModelElement;
import hu.kovand.sketch3d.model.ModelPoint;
import hu.kovand.sketch3d.model.ModelSurface;

public class ModelOverlay {
	public static final String TAG = "ModelOverlay";
	
	
	private static final int POINT_TO_CURVE_MERGE_RANGE = 50; //pixel
	private static final int CURVE_TO_POINT_MERGE_RANGE = 50; 
	private static final int CURVE_TO_CURVE_MERGE_LOWER = 100;
	private static final int CURVE_TO_CURVE_MERGE_UPPER = 300;
	
	
	
	
	
	
	private List<ModelElement> elements;
	Model3D model;
	ModelSurface surface;
	float[] mvp;
	float xLength;
	float yLength;
	

	/**
	 * Constructor
	 */
	public ModelOverlay() {
	}
	

	/**
	 * Imports a surface (list of all elements on it)
	 * @param model context model.
	 * @param surface working surface
	 * @param elems list of all elements
	 * @param mvp modelview and projection matrix
	 * @param lx length of x unit vec
	 * @param ly length of y unit vec
	 */
	public void importSurface(Model3D model,ModelSurface surface,List<ModelElement> elems,float[] mvp,float lx,float ly)
	{
		elements = elems;
		this.model = model;
		this.surface = surface;
		this.mvp = mvp;
		xLength = lx;
		yLength = ly;
	}
	
	/**
	 * Exports the surface
	 * @return
	 */
	public List<ModelElement> exportSurface()
	{
		return elements;		
	}

	
	/**
	 * Adds a point to the working surface. Connect them to close
	 * @param addr point address in normalized device coordinates
	 */
	public void addPoint(Vec2 addr)
	{
		Vec2 pos = surface.findRayIntersection(addr, mvp);
		ModelPoint p = new ModelPoint(model, surface.getId(), pos);
		elements.add(p);	
	}	
	
	/**
	 * Adds a curve (polyline representation) to the working model.
	 * @param curve series of point addresses in normalized device coordinates
	 */
	public void addCurve(List<Vec2> curve)
	{		
		Log.d(TAG + ".addCurve", Integer.toString(curve.size()));
		
		List<Vec2> list = new ArrayList<Vec2>();
		for (Vec2 v : curve)
		{
			Vec2 its = surface.findRayIntersection(v, mvp);
			list.add(its);
		}		
		ModelCurve c = new ModelCurve(model, surface.getId(), list);
		elements.add(c);
										
	}
	
	
	
	
	
	
	
	
}

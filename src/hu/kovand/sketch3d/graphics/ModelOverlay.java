package hu.kovand.sketch3d.graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.opengl.Matrix;
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
import hu.kovand.sketch3d.utility.MyMath;

public class ModelOverlay {
	public static final String TAG = "ModelOverlay";	
	
	private static final float POINT_TO_CURVE_MERGE_RANGE = 50.0f; //pixel
	private static final float CURVE_TO_POINT_MERGE_RANGE = 35.0f; 
	private static final float CURVE_TO_CURVE_MERGE = 75.0f;
	private static final float CURVE_TO_CURVE_EXTEND = 100.0f;	
	
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
		Log.d(TAG + ".addPoint_called", addr.toString());
		
		List<Vec2> startPoints = new ArrayList<Vec2>();
		List<Vec2> endPoints = new ArrayList<Vec2>();
		List<Integer> indexVec = new ArrayList<Integer>();
		for (int i=0; i< elements.size(); i++)
		{
			if (elements.get(i).getType() == ModelElement.TYPE_CURVE)
			{
				indexVec.add(i);
				ModelCurve c = (ModelCurve)elements.get(i);
				Vec3 modelStart = c.evaluateAt(0);
				Vec3 modelEnd = c.evaluateAt(c.size()-1);
				//TODO Use Vec4.multipleMV
				float[] screenStart3D = new float[4];
				Matrix.multiplyMV(screenStart3D, 0, mvp, 0,(new Vec4(modelStart)).toArray(), 0);
				float[] screenEnd3D = new float[4];
				Matrix.multiplyMV(screenEnd3D, 0, mvp, 0,(new Vec4(modelEnd)).toArray(), 0);
				Vec2 screenStart = new Vec2(screenStart3D[0]/screenStart3D[3],screenStart3D[1]/screenStart3D[3]);
				Vec2 screenEnd = new Vec2(screenEnd3D[0]/screenEnd3D[3],screenEnd3D[1]/screenEnd3D[3]);
				
				Log.d(TAG + ".startpoint", screenStart.toString());
				Log.d(TAG + ".endpoint", screenEnd.toString());
				
				startPoints.add(screenStart);
				endPoints.add(screenEnd);
			}
		}
		
		Vec2 finalPos = null;
		
		if (indexVec.size() == 0){
			finalPos = addr;			
		}
		else
		{			
			int startIndex = Vec2.findClosest(startPoints, addr, xLength, yLength);
			int endIndex = Vec2.findClosest(endPoints, addr, xLength, yLength);
			Vec2 startDiff = Vec2.subtract(startPoints.get(startIndex), addr);
			Vec2 endDiff = Vec2.subtract(endPoints.get(endIndex), addr);
			float startDist = (float)Math.sqrt( startDiff.getX()*startDiff.getX()*xLength*xLength + startDiff.getY()*startDiff.getY()*yLength*yLength );
			float endDist = (float)Math.sqrt( endDiff.getX()*endDiff.getX()*xLength*xLength + endDiff.getY()*endDiff.getY()*yLength*yLength );
			
			//TODO consider extract to vec2 operation

			
			if (startDist<endDist){
				if (startDist<POINT_TO_CURVE_MERGE_RANGE){
					finalPos = startPoints.get(startIndex);								
				}
				else{
					finalPos = addr;								
				}
			}
			else{
				if (endDist<POINT_TO_CURVE_MERGE_RANGE){
					finalPos = endPoints.get(endIndex);
				}
				else{
					finalPos = addr;
				}
			}
		}
		
		ModelPoint p = new ModelPoint(model, surface.getId(), surface.findRayIntersection(finalPos, mvp));
		
		elements.add(p);
		
		//TODO register to curve
		
		/*int elementIndex = indexVec.get(startIndex);
		ModelCurve c = (ModelCurve)elements.get(elementIndex);*/
		
	
	}	
	
	/**
	 * Adds a curve (polyline representation) to the working model.
	 * @param curve series of point addresses in normalized device coordinates
	 */
	public void addCurve(List<Vec2> curve)
	{		
		
		List <ModelPoint> modelPoints = new ArrayList<ModelPoint>();
		List <ModelCurve> modelCurves = new ArrayList<ModelCurve>();
		for (ModelElement elem : elements)
		{
			if (elem.getType() == ModelElement.TYPE_POINT){
				modelPoints.add((ModelPoint)elem);				
			}
			else if (elem.getType() == ModelElement.TYPE_CURVE){
				modelCurves.add((ModelCurve)elem);								
			}
		}
		
		Vec2 startPoint = curve.get(0);
		Vec2 endPoint = curve.get(curve.size()-1);
		
		List<Vec3> pointsModelCoord = new ArrayList<Vec3>();
		for (ModelPoint p : modelPoints)		{
			pointsModelCoord.add(p.evaluate());
		}
		List<Vec2> pointsNorm = Model3D.mapToScreenNorm(pointsModelCoord, mvp);
		
		boolean attachPointStart = false;
		boolean attachPointEnd = false;
		
		if (pointsNorm.size() != 0)
		{
			int closestStart = Vec2.findClosest(pointsNorm, startPoint,xLength, yLength);
			int closestEnd = Vec2.findClosest(pointsNorm, endPoint, xLength, yLength);
			if (Vec2.distance(startPoint, pointsNorm.get(closestStart),xLength,yLength)<CURVE_TO_POINT_MERGE_RANGE)
			{
				attachPointStart = true;
				curve.add(0, pointsNorm.get(closestStart));
				//TODO better attach
			}
			if (Vec2.distance(endPoint, pointsNorm.get(closestEnd),xLength,yLength)<CURVE_TO_POINT_MERGE_RANGE)
			{
				attachPointEnd = true;
				curve.add(pointsNorm.get(closestEnd));
				//TODO better attach
			}
		}
		
		boolean attachCurveStart = false;
		boolean attachCurveEnd = false;
		
		//find closest point of any curve
		float minStart = Float.MAX_VALUE;
		float minEnd = Float.MAX_VALUE;
		ModelCurve closestStartCurve = null;
		ModelCurve closestEndCurve = null;
		int closestStartCurveIndex = 0;
		int closestEndCurveIndex = 0;
		for (ModelCurve c : modelCurves)
		{
			List<Vec2> ps = Model3D.mapToScreenNorm(c.evaluate().getPoints(), mvp);
			int closestStart = Vec2.findClosest(ps, startPoint, xLength, yLength);
			int closestEnd = Vec2.findClosest(ps, endPoint, xLength, yLength);
			float distStart = Vec2.distance(startPoint, ps.get(closestStart), xLength, yLength);
			float distEnd = Vec2.distance(endPoint, ps.get(closestEnd), xLength, yLength);
			if (distStart < minStart)
			{
				minStart = distStart;
				closestStartCurve = c;
				closestStartCurveIndex = closestStart;
			}
			if (distEnd < minEnd)
			{
				minEnd = distEnd;
				closestEndCurve = c;
				closestEndCurveIndex = closestEnd;
			}
			
		}
		
		
		
		if (minStart < CURVE_TO_CURVE_MERGE && !attachPointStart)
		{
			attachCurveStart = true;
		}
		
		if (minEnd < CURVE_TO_CURVE_MERGE && !attachPointEnd)
		{
			attachCurveEnd = true;
		}
		
		boolean doOverSketchAndExtend = false;
		doOverSketchAndExtend |= closestStartCurve == closestEndCurve && attachCurveStart && attachCurveEnd;
		doOverSketchAndExtend |= attachCurveStart && !attachCurveEnd;
		
		
		if (doOverSketchAndExtend)
		{
			List<Vec2> closestNorm = Model3D.mapToScreenNorm(closestStartCurve.evaluate().getPoints(), mvp);
			List<Integer> closestIndex = new ArrayList<Integer>();
			List<Float> distVec = new ArrayList<Float>();
			
			for (Vec2 p : curve)
			{
				int id = Vec2.findClosest(closestNorm, p, xLength, yLength);
				closestIndex.add(id);
				distVec.add(Vec2.distance(p, closestNorm.get(id), xLength, yLength));		
			}
			
			int lastToWeight = -1;
			for (int i=0; i< distVec.size();i++)
			{
				if (distVec.get(i)<CURVE_TO_CURVE_EXTEND){
					lastToWeight = i;
				}
			}
			
			List<Vec2> newCurve = new ArrayList<Vec2>();
			
			
			if (closestIndex.get(lastToWeight) > closestIndex.get(0))
			{
				for (int i=0;i< closestStartCurveIndex ; i++){
					newCurve.add(closestNorm.get(i));					
				}			
				if (lastToWeight == curve.size()-1){
					
					Log.d(TAG+".type", "paralel oversketch");
					
					for (int i=0; i<lastToWeight;i++)
					{
						float t = (1.0f*i)/(lastToWeight-1);
						newCurve.add(Vec2.weightedAdd(curve.get(i), MyMath.weightFunction(t, 0.0f, 0.8f, 0.0f), closestNorm.get(closestIndex.get(i)), 1-MyMath.weightFunction(t, 0.0f, 0.8f, 0.0f)));				
					}
					Log.d(TAG+".ind", Integer.toString(closestEndCurveIndex)+" "+Integer.toString(closestNorm.size()));
					for (int i = closestEndCurveIndex+1; i<closestNorm.size();i++){
						newCurve.add(closestNorm.get(i));						
					}
				}
				else
				{
					Log.d(TAG+".type", "paralel extend");
					
					for (int i=0; i<lastToWeight;i++){
						float t = (1.0f*i)/(lastToWeight-1);
						newCurve.add(Vec2.weightedAdd(curve.get(i), MyMath.weightFunction(t, 0.0f, 0.5f, 1.0f), closestNorm.get(closestIndex.get(i)), 1.0f-MyMath.weightFunction(t, 0.0f, 0.5f, 1.0f)));				
					}
					for (int i=lastToWeight;i<curve.size();i++){
						newCurve.add(curve.get(i));
					}
				}
			}
			else
			{
				for (int i=closestNorm.size()-1;i > closestStartCurveIndex ; i--){
					newCurve.add(closestNorm.get(i));					
				}
				
				if (lastToWeight == curve.size()-1){
					for (int i=0; i<lastToWeight;i++)
					{
						float t = (1.0f*i)/(lastToWeight-1);
						newCurve.add(Vec2.weightedAdd(curve.get(i), MyMath.weightFunction(t, 0.0f, 0.8f, 0.0f), closestNorm.get(closestIndex.get(i)), 1-MyMath.weightFunction(t, 0.0f, 0.8f, 0.0f)));				
					}
					for (int i = closestStartCurveIndex-1; i>=0;i--){
						newCurve.add(closestNorm.get(i));						
					}
				}
				else
				{
					for (int i=0; i<lastToWeight;i++){
						float t = (1.0f*i)/(lastToWeight-1);
						newCurve.add(Vec2.weightedAdd(curve.get(i), MyMath.weightFunction(t, 0.0f, 0.5f, 1.0f), closestNorm.get(closestIndex.get(i)), 1.0f-MyMath.weightFunction(t, 0.0f, 0.5f, 1.0f)));				
					}
					for (int i=lastToWeight;i<curve.size();i++){
						newCurve.add(curve.get(i));
					}
				}
				
			}
			
			List<Vec3> curve3D = new ArrayList<Vec3>();
			for (Vec2 p : newCurve)
			{
				curve3D.add(new Vec3(p.getX(), p.getY(), 0.0f));
			}
			
			BSpline curveBspline = new BSpline();
			curveBspline.approximate(new PolyLine(curve3D), Model3D.DEFAULT_BSPLINE_P, closestStartCurve.getbSplineHint());
			PolyLine curveEval = curveBspline.evaluateN(100);
			
			List<Vec2> list = new ArrayList<Vec2>();
			for (Vec3 p : curveEval.getPoints())
			{
				Vec2 its = surface.findRayIntersection(new Vec2(p.getX(),p.getY()), mvp);
				list.add(its);
			}		
			ModelCurve c = new ModelCurve(model, surface.getId(), list,null,null,closestStartCurve.getbSplineHint(),closestStartCurve.getId());
			//ModelCurve c = new ModelCurve(model, surface.getId(), list,null,null,closestStartCurve.getbSplineHint());
			elements.add(c);
			elements.remove(closestStartCurve);
			/*List<Vec2> curveOnSurface = new ArrayList<Vec2>(); 
			for (Vec2 v : curve)
			{
				curveOnSurface.add(surface.findRayIntersection(v, mvp));				
			}
			elements.add(new ModelCurve(model, surface.getId(), curveOnSurface,null,null,Model3D.DEFAULT_BSPLINE_N));*/
			
			
			
		}
		
		
		
		//change condition
		if (!attachCurveStart && !attachCurveEnd)
		{
			List<Vec3> curve3D = new ArrayList<Vec3>();
			for (Vec2 p : curve)
			{
				curve3D.add(new Vec3(p.getX(), p.getY(), 0.0f));
			}
			
			BSpline curveBspline = new BSpline();
			curveBspline.approximate(new PolyLine(curve3D), Model3D.DEFAULT_BSPLINE_P, Model3D.DEFAULT_BSPLINE_N);
			PolyLine curveEval = curveBspline.evaluateN(100);	
			
			List<Vec2> list = new ArrayList<Vec2>();
			for (Vec3 p : curveEval.getPoints())
			{
				Vec2 its = surface.findRayIntersection(new Vec2(p.getX(),p.getY()), mvp);
				list.add(its);
			}		
			ModelCurve c = new ModelCurve(model, surface.getId(), list,null,null,Model3D.DEFAULT_BSPLINE_N);
			Log.d(TAG+".add", c.getId().toString());
			elements.add(c);				
		}
		
		
		
		
		
										
	}	
	
	void addDebugPoint(Vec2 addr)
	{
		ModelPoint p = new ModelPoint(model, surface.getId(), surface.findRayIntersection(addr, mvp));		
		elements.add(p);		
	}
	
	void addDebugLine(Vec2 a,Vec2 b)
	{
		List<Vec2> points= new ArrayList<Vec2>();
		points.add(surface.findRayIntersection(a, mvp));
		points.add(surface.findRayIntersection(b, mvp));
		ModelCurve c = new ModelCurve(model, surface.getId(), points, null, null,Model3D.DEFAULT_BSPLINE_N);
		elements.add(c);
	}
	
	
	
	
}

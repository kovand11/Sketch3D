package hu.kovand.sketch3d.graphics;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;
import android.util.Log;


import hu.kovand.sketch3d.geometry.BSpline;
import hu.kovand.sketch3d.geometry.PolyLine;
import hu.kovand.sketch3d.geometry.PolyLineRenderable;
import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.geometry.Vec3Renderable;
import hu.kovand.sketch3d.geometry.Vec4;
import hu.kovand.sketch3d.model.ModelCurve;
import hu.kovand.sketch3d.model.ModelElement;
import hu.kovand.sketch3d.model.ModelPoint;
import hu.kovand.sketch3d.model.ModelSurface;
import hu.kovand.sketch3d.model.ModelSurfaceByThreePoints;
import hu.kovand.sketch3d.model.ModelSurfaceByTwoPointsAndSurface;
import hu.kovand.sketch3d.model.ModelSurfaceConst;
import hu.kovand.sketch3d.model.ModelSurfaceOffset;
import hu.kovand.sketch3d.model.ModelWithOrigAndTwoBase;
import hu.kovand.sketch3d.utility.Constants;

public class Model3D  {
	public static final String TAG = "Model3D";
	
	public static final String DEFINE_BY_PARENT = "Parent";  //one point, one curve
	public static final String DEFINE_BY_2POINTS_AND_COMMON_PERPEDICULAR_SURFACE = "Po Po Perp(Su)"; //two points with common surface
	public static final String DEFINE_BY_3POINTS = "Po Po Po"; //3 points
	public static final String DEFINE_BY_PARENTSURFACE_AND_OFFSET_POINT = "Parent Offs(Po)"; //Parent of first elem and an offset point
	
	
	
	
	private ModelSurface activeSurface;
	private List<ModelSurface> surfaceList;
	private List<ModelElement> selectedList;
	
	List<FloatBuffer> selectedPointsVertexBufferList;
	List<FloatBuffer> selectedCurvesVertexBufferList;
	List<Integer> selectedCurvesSizeList;
	
	List<FloatBuffer> activePointsVertexBufferList;
	List<FloatBuffer> activeCurvesVertexBufferList;
	List<Integer> activeCurvesSizeList;
	
	List<FloatBuffer> passivePointsVertexBufferList;
	List<FloatBuffer> passiveCurvesVertexBufferList;
	List<Integer> passiveCurvesSizeList;
	
	public Model3D()
	{
		activeSurface = new ModelSurfaceConst(new Vec3(0.0f,0.0f,0.0f),new Vec3(1.0f,0.0f,0.0f),new Vec3(0.0f,1.0f,0.0f));
		surfaceList = new ArrayList<ModelSurface>();
		surfaceList.add(activeSurface);
		Log.d("newsurf", ((ModelWithOrigAndTwoBase)(activeSurface)).toString());
		selectedList = new ArrayList<ModelElement>();
		refreshAllBuffer();
	}
	
	public void addPoint(Vec2 addr)
	{
		activeSurface.addPoint(addr);
	}
	
	public void addCurve(List<Vec2> addrs)
	{
		List<Vec3> list = new ArrayList<Vec3>();
		for (int i=0;i<addrs.size();i++)
		{
			list.add(new Vec3(addrs.get(i).getX(), addrs.get(i).getY(), 0.0f));						
		}
		BSpline bspline = new BSpline();
		bspline.approximate(new PolyLine(list), 3, 10);
		PolyLine l = bspline.evaluateN(50);
		
		List<Vec2> bspline2d = new ArrayList<Vec2>();
		for (int i=0;i<l.size();i++){
			bspline2d.add(new Vec2(l.get(i).getX(), l.get(i).getY()));
		}
		//TODO
		activeSurface.addCurve(bspline2d);
	}
	
	//SELECTED
	
	public void refreshSelectedPointsVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		for (int i=0;i<selectedList.size();i++)
		{
			if (selectedList.get(i).getType() == ModelElement.TYPE_POINT){
				FloatBuffer buff = (new Vec3Renderable(((ModelPoint)(selectedList.get(i))).evaluate())).getVertexBuffer();
				list.add(buff);								
			}
			
		}

		selectedPointsVertexBufferList = list;
	}
	
	public List<FloatBuffer> getSelectedPointsVertexBufferList()
	{
		return selectedPointsVertexBufferList;
	}
	
	/*public List<FloatBuffer> refreshSelectedCurvesVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		List<ModelCurve> modelCurves = activeSurface.getCurves();
		for (int i=0;i<modelCurves.size();i++)
		{
			list.add((new PolyLineRenderable(modelCurves.get(i).evaluate())).getVertexBuffer());						
		}
		return list;
	}*/
	
	/*public List<Integer> refreshSelectedCurvesSizeList()
	{
		List<Integer> list = new ArrayList<Integer>();
		List<ModelCurve> modelCurves = activeSurface.getCurves();
		for (int i=0;i<modelCurves.size();i++)
		{
			list.add(modelCurves.get(i).size());						
		}
		return list;		
	}*/
	
	
	//ACTIVE
	public void refreshActivePointsVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		List<ModelPoint> modelPoints = activeSurface.getPoints();
		for (int i=0;i<modelPoints.size();i++){
			if (!selectedList.contains(modelPoints.get(i))){
				list.add((new Vec3Renderable(modelPoints.get(i).evaluate())).getVertexBuffer());
			}
		}
		activePointsVertexBufferList = list;
	}
	
	public List<FloatBuffer> getActivePointsVertexBufferList(){
		return activePointsVertexBufferList;
	}
	
	public void refreshActiveCurvesVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		List<ModelCurve> modelCurves = activeSurface.getCurves();
		for (int i=0;i<modelCurves.size();i++)
		{
			list.add((new PolyLineRenderable(modelCurves.get(i).evaluate())).getVertexBuffer());						
		}
		activeCurvesVertexBufferList = list;
	}
	
	public List<FloatBuffer> getActiveCurvesVertexBufferList(){
		return activeCurvesVertexBufferList;
	}
	
	public void refreshActiveCurvesSizeList()
	{
		List<Integer> list = new ArrayList<Integer>();
		List<ModelCurve> modelCurves = activeSurface.getCurves();
		for (int i=0;i<modelCurves.size();i++)
		{
			list.add(modelCurves.get(i).size());						
		}
		activeCurvesSizeList = list;		
	}
	
	public List<Integer> getActiveCurvesSizeList(){
		return activeCurvesSizeList;
	}
	
	
	//PASSIVE
	
	public void refreshPassivePointsVertexBufferList()
	{		
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		for (int i=0;i<surfaceList.size();i++)
		{
			if (surfaceList.get(i)!=activeSurface){
				List<ModelPoint> modelPoints = surfaceList.get(i).getPoints();
				for (int j=0;j<modelPoints.size();j++){
					if (!selectedList.contains(modelPoints.get(j))){
						list.add((new Vec3Renderable(modelPoints.get(j).evaluate())).getVertexBuffer());
					}
				}
			}
		}
		passivePointsVertexBufferList = list;	
	}
	
	public List<FloatBuffer> getPassivePointsVertexBufferList(){
		return passivePointsVertexBufferList;
	}
	
	public void refreshPassiveCurvesVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		for (int i=0;i<surfaceList.size();i++)
		{
			if (surfaceList.get(i)!=activeSurface){
				List<ModelCurve> modelCurves = surfaceList.get(i).getCurves();
				for (int j=0;j<modelCurves.size();j++)
				{
					list.add((new PolyLineRenderable(modelCurves.get(j).evaluate())).getVertexBuffer());						
				}
			}
		}
		passiveCurvesVertexBufferList = list;
	}
	
	public List<FloatBuffer> getPassiveCurvesVertexBufferList(){
		return passiveCurvesVertexBufferList;
	}
	
	public void refreshPassiveCurvesSizeList()
	{
		List<Integer> list = new ArrayList<Integer>();
		for (int i=0;i<surfaceList.size();i++)
		{
			if (surfaceList.get(i)!=activeSurface){
				List<ModelCurve> modelCurves = surfaceList.get(i).getCurves();
				for (int j=0;j<modelCurves.size();j++)
				{
					list.add(modelCurves.get(j).size());						
				}
			}
		}
		passiveCurvesSizeList = list;
	}
	
	public List<Integer> getPassiveCurvesSizeList()	{
		return passiveCurvesSizeList;		
	}
	
	
	
	public ModelSurface getActiveSurface()
	{
		return activeSurface;		
	}
	
	public ModelElement getElementByScreenPosition(Vec2 sp,float lx,float ly,float[] mvp)
	{
		//TODO only supports points on active surface
		List<ModelPoint> pointList = new ArrayList<ModelPoint>();
		for (int i=0;i<surfaceList.size();i++)
		{
			pointList.addAll(surfaceList.get(i).getPoints());			
		}
		List<Vec2> screenPointList = new ArrayList<Vec2>();
		for (int i=0;i<pointList.size();i++)
		{
			Vec3 pos = pointList.get(i).evaluate();
			Vec4 norm = new Vec4(pos);
			float [] scr = new float[4];
			Matrix.multiplyMV(scr, 0, mvp, 0, norm.toArray(), 0);
			screenPointList.add(new Vec2(scr[0]/scr[3],scr[1]/scr[3]));
		}		
		
		int closest = Vec2.findClosest(screenPointList, sp, lx, ly);
		if (closest>=0){
			Vec2 distVec = Vec2.subtract(screenPointList.get(closest),sp);
			//TODO simplify
			float dist = (float)Math.sqrt(distVec.getX()*distVec.getX()*lx*lx+distVec.getY()*distVec.getY()*ly*ly);
			if (dist<Constants.SELECT_DISTANCE)
				return pointList.get(closest);
			else return null;
		}
		else{
			return null;
		}
	}
	
	
	public void clear()
	{		
		activeSurface = new ModelSurfaceConst(new Vec3(0.0f,0.0f,0.0f),new Vec3(1.0f,0.0f,0.0f),new Vec3(0.0f,1.0f,0.0f));
		surfaceList = new ArrayList<ModelSurface>();
		selectedList = new ArrayList<ModelElement>();
		surfaceList.add(activeSurface);
	}
	
	public void select(ModelElement elem)
	{
		selectedList.add(elem);
	}
	
	public void unselect(ModelElement elem)
	{
		selectedList.remove(elem);
	}
	
	public boolean isSelected(ModelElement elem)
	{
		return selectedList.contains(elem);		
	}
	
	public List<String> getPossibleSurfaceDefinitions()
	{
		ArrayList<String> arr = new ArrayList<String>();
		if (selectedList.size() == 1)
		{
			arr.add(DEFINE_BY_PARENT);
		}
		else if (selectedList.size() == 2)
		{
			//test for DEFINE_BY_2POINTS_AND_COMMON_PERPEDICULAR_SURFACE 
			if (selectedList.get(0).getType()==ModelElement.TYPE_POINT && selectedList.get(1).getType()==ModelElement.TYPE_POINT)
			{
				ModelPoint p1 = (ModelPoint)selectedList.get(0);
				ModelPoint p2 = (ModelPoint)selectedList.get(1);				
				if (p1.getParent() == p2.getParent()){
					arr.add(DEFINE_BY_2POINTS_AND_COMMON_PERPEDICULAR_SURFACE);
				}			
			}
			
			if (selectedList.get(1).getType()==ModelElement.TYPE_POINT ){
				ModelElement m1 = selectedList.get(0);
				ModelPoint p1 = (ModelPoint)(selectedList.get(1));
				if (m1.getType() == ModelElement.TYPE_POINT)
				{
					if (((ModelPoint)(m1)).getParent() != p1.getParent()){
						arr.add(DEFINE_BY_PARENTSURFACE_AND_OFFSET_POINT);
					}
				}
				else if (m1.getType() == ModelElement.TYPE_CURVE)
				{
					if (((ModelCurve)(m1)).getParent() != p1.getParent()){
						arr.add(DEFINE_BY_PARENTSURFACE_AND_OFFSET_POINT);
					}					
				}
												
			}
		}
		else if (selectedList.size() == 3)
		{
			if (selectedList.get(0).getType()==ModelElement.TYPE_POINT && selectedList.get(1).getType()==ModelElement.TYPE_POINT && selectedList.get(2).getType()==ModelElement.TYPE_POINT){
				arr.add(DEFINE_BY_3POINTS);
			}		
		}
		return arr;
	}
	
	
	public void changeActiveSurface(String mode)
	{
		
		/*ModelSurfaceByTwoPointsAndSurface newSurf = new ModelSurfaceByTwoPointsAndSurface((ModelPoint)(selectedList.get(0)), (ModelPoint)(selectedList.get(1)),(ModelWithOrigAndTwoBase)(((ModelPoint)(selectedList.get(0))).getParent()) );
		surfaceList.add(newSurf);
		activeSurface = newSurf;*/
		

		if ( mode == DEFINE_BY_PARENT )
		{
			if (selectedList.get(0).getType() == ModelElement.TYPE_POINT)
			{
				activeSurface = ((ModelPoint)(selectedList.get(0))).getParent();				
			}
		}
		else if ( mode == DEFINE_BY_2POINTS_AND_COMMON_PERPEDICULAR_SURFACE )
		{
			ModelSurfaceByTwoPointsAndSurface newSurf = new ModelSurfaceByTwoPointsAndSurface((ModelPoint)(selectedList.get(0)),
					(ModelPoint)(selectedList.get(1)),(ModelWithOrigAndTwoBase)(((ModelPoint)(selectedList.get(0))).getParent()) );
			boolean selected = false;
			for (int i=0;i<surfaceList.size();i++)
			{				
				if (surfaceList.get(i).equals(newSurf))
				{
					activeSurface = surfaceList.get(i);
					selected = true;					
					break;
				}
				
			}
			if (!selected){
				surfaceList.add(newSurf);
				activeSurface = newSurf;					
			}
						
		}
		else if ( mode == DEFINE_BY_3POINTS )
		{
			ModelSurfaceByThreePoints newSurf = new ModelSurfaceByThreePoints((ModelPoint)(selectedList.get(0)),
					(ModelPoint)(selectedList.get(1)),(ModelPoint)(selectedList.get(2)));
			boolean selected = false;
			for (int i=0;i<surfaceList.size();i++)
			{				
				if (surfaceList.get(i).equals(newSurf))
				{
					activeSurface = surfaceList.get(i);
					selected = true;					
					break;
				}
				
			}
			if (!selected){
				surfaceList.add(newSurf);
				activeSurface = newSurf;					
			}
			
		}
		else if ( mode == DEFINE_BY_PARENTSURFACE_AND_OFFSET_POINT )
		{
			ModelElement e1 = selectedList.get(0);
			ModelElement e2 = selectedList.get(1);
			
			ModelSurfaceOffset newSurf = null;
			
			
			if (e1.getType() == ModelElement.TYPE_POINT)
			{
				ModelPoint p1 = (ModelPoint)e1;
				newSurf = new ModelSurfaceOffset((ModelWithOrigAndTwoBase)p1.getParent(), (ModelPoint)e2);				
			}
			
			//TODO for curve
			
			if (newSurf!=null)
			{
				boolean selected = false;
				for (int i=0;i<surfaceList.size();i++)
				{				
					if (surfaceList.get(i).equals(newSurf))
					{
						activeSurface = surfaceList.get(i);
						selected = true;					
						break;
					}
					
				}
				if (!selected){
					surfaceList.add(newSurf);
					activeSurface = newSurf;					
				}		
				
			}						
		}
		Log.d("newsurf", ((ModelWithOrigAndTwoBase)(activeSurface)).toString());
	}
	
	public void refreshAllBuffer()
	{
		refreshSelectedPointsVertexBufferList();
		//
		//
		
		refreshActivePointsVertexBufferList();
		refreshActiveCurvesVertexBufferList();
		refreshActiveCurvesSizeList();
		
		refreshPassivePointsVertexBufferList();
		refreshPassiveCurvesVertexBufferList();
		refreshPassiveCurvesSizeList();
		
		
	}
	
	
	
	

	
	
	
}

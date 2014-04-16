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
import hu.kovand.sketch3d.model.ModelSurfaceByTwoPointsAndSurface;
import hu.kovand.sketch3d.model.ModelSurfaceConst;
import hu.kovand.sketch3d.model.ModelWithOrigAndTwoBase;
import hu.kovand.sketch3d.utility.Constants;

public class Model3D  {
	public static final String TAG = "Model3D";
	
	private ModelSurface activeSurface;
	private List<ModelSurface> surfaceList;
	private float[] MvpMatrix;
	private List<ModelElement> selectedList;
	
	public Model3D()
	{
		activeSurface = new ModelSurfaceConst(new Vec3(0.0f,0.0f,0.0f),new Vec3(1.0f,0.0f,0.0f),new Vec3(0.0f,1.0f,0.0f));
		surfaceList = new ArrayList<ModelSurface>();
		surfaceList.add(activeSurface);	
		selectedList = new ArrayList<ModelElement>();
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
		
		//
		activeSurface.addCurve(bspline2d);
	}
	
	//SELECTED
	
	public List<FloatBuffer> getSelectedPointsVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		for (int i=0;i<selectedList.size();i++)
		{
			if (selectedList.get(i).getType() == ModelElement.TYPE_POINT){
				FloatBuffer buff = (new Vec3Renderable(((ModelPoint)(selectedList.get(i))).evaluate())).getVertexBuffer();
				list.add(buff);								
			}
			
		}

		return list;
	}
	
	/*public List<FloatBuffer> getSelectedCurvesVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		List<ModelCurve> modelCurves = activeSurface.getCurves();
		for (int i=0;i<modelCurves.size();i++)
		{
			list.add((new PolyLineRenderable(modelCurves.get(i).evaluate())).getVertexBuffer());						
		}
		return list;
	}*/
	
	/*public List<Integer> getSelectedCurvesSizeList()
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
	public List<FloatBuffer> getActivePointsVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		List<ModelPoint> modelPoints = activeSurface.getPoints();
		for (int i=0;i<modelPoints.size();i++){
			if (!selectedList.contains(modelPoints.get(i))){
				list.add((new Vec3Renderable(modelPoints.get(i).evaluate())).getVertexBuffer());
			}
		}
		return list;
	}
	
	public List<FloatBuffer> getActiveCurvesVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		List<ModelCurve> modelCurves = activeSurface.getCurves();
		for (int i=0;i<modelCurves.size();i++)
		{
			list.add((new PolyLineRenderable(modelCurves.get(i).evaluate())).getVertexBuffer());						
		}
		return list;
	}
	
	public List<Integer> getActiveCurvesSizeList()
	{
		List<Integer> list = new ArrayList<Integer>();
		List<ModelCurve> modelCurves = activeSurface.getCurves();
		for (int i=0;i<modelCurves.size();i++)
		{
			list.add(modelCurves.get(i).size());						
		}
		return list;		
	}
	
	//PASSIVE
	
	public List<FloatBuffer> getPassivePointsVertexBufferList()
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
		Log.d("tmp", Integer.toString(list.size()));
		return list;	
	}
	
	public List<FloatBuffer> getPassiveCurvesVertexBufferList()
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
		return list;
	}
	
	public List<Integer> getPassiveCurvesSizeList()
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
		return list;
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
		if (selectedList.size()==2){
			changeActiveSurface(0);
		}
	}
	
	public void unselect(ModelElement elem)
	{
		selectedList.remove(elem);	
		if (selectedList.size()==2){
			changeActiveSurface(0);
		}
	}
	
	public boolean isSelected(ModelElement elem)
	{
		return selectedList.contains(elem);		
	}
	
	
	public void changeActiveSurface(int ignore)
	{
		//TODO only supports 2 point and a common surface
		//unsafe
		
		ModelSurfaceByTwoPointsAndSurface newSurf = new ModelSurfaceByTwoPointsAndSurface((ModelPoint)(selectedList.get(0)), (ModelPoint)(selectedList.get(1)),(ModelWithOrigAndTwoBase)(((ModelPoint)(selectedList.get(0))).getParent()) );
		surfaceList.add(newSurf);
		activeSurface = newSurf;
		
		
		
		
	}
	
	
	
	

	
	
	
}

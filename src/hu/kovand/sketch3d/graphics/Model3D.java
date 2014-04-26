package hu.kovand.sketch3d.graphics;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import hu.kovand.sketch3d.utility.Constants;

public class Model3D {

	public static final String TAG = "Model3D";
	
	public static final String DEFINE_SURFACE_DEFAULT = "Select..";  
	public static final String DEFINE_SURFACE_PARENT = "Parent"; 
	public static final String DEFINE_SURFACE_2POINTS_AND_COMMON_PERPEDICULAR_SURFACE = "Po Po Perp(Su)";
	public static final String DEFINE_SURFACE_3POINTS = "Po Po Po";
	public static final String DEFINE_SURFACE_PARENTSURFACE_AND_OFFSET_POINT = "Parent Offs(Po)";
	
	public static final String DEFINE_ELEMENT_DEFAULT = "Select..";
	public static final String DEFINE_ELEMENT_LINE = "Line";
	
	
	
	
	List<FloatBuffer> selectedPointsVertexBufferList;
	List<FloatBuffer> selectedCurvesVertexBufferList;
	List<Integer> selectedCurvesSizeList;
	
	List<FloatBuffer> activePointsVertexBufferList;
	List<FloatBuffer> activeCurvesVertexBufferList;
	List<Integer> activeCurvesSizeList;
	
	List<FloatBuffer> extraPointsVertexBufferList;
	
	List<FloatBuffer> passivePointsVertexBufferList;
	List<FloatBuffer> passiveCurvesVertexBufferList;
	List<Integer> passiveCurvesSizeList;
	
	List<ModelElement> elementList; //holds all model element
	UUID activeSurface;
	List<UUID> selectedList;
	
	public Model3D()
	{		
		ModelSurface base = new ModelSurfaceConst(this,new Vec3(0.0f,0.0f,0.0f),new Vec3(1.0f,0.0f,0.0f),new Vec3(0.0f,1.0f,0.0f));
		elementList = new ArrayList<ModelElement>();
		elementList.add(base);
		activeSurface = base.getId();
		selectedList = new ArrayList<UUID>();
		
		refreshAllBuffer();
	}
	
	/**
	 * Copy constructor, does not build vertex buffer, and does not keep selections.
	 * @param m Model3D to copy
	 */
	public Model3D(Model3D m)
	{
		this.elementList = new ArrayList<ModelElement>();
		this.activeSurface = m.activeSurface;
		this.selectedList = new ArrayList<UUID>();
		for (ModelElement elem : m.elementList)
		{
			this.elementList.add(elem);						
		}
	}
	
	public void addPoint(Vec2 addr)
	{

		ModelPoint p = new ModelPoint(this,activeSurface ,addr);
		elementList.add(p);
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
		
		ModelCurve c = new ModelCurve(this, activeSurface, bspline2d);
		elementList.add(c);
		
	}
	
	//SELECTED (all in selectedList)
	
	public void refreshSelectedPointsVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();	
		for (UUID id:selectedList)
		{
			ModelElement elem = getElementById(id);
			if (elem.getType() == ModelElement.TYPE_POINT){
				FloatBuffer buff = (new Vec3Renderable(((ModelPoint)(elem)).evaluate())).getVertexBuffer();
				list.add(buff);								
			}
		}
		selectedPointsVertexBufferList = list;
		Log.d(TAG + "selected",Integer.toString(list.size()));
	}
	
	public List<FloatBuffer> getSelectedPointsVertexBufferList()
	{
		return selectedPointsVertexBufferList;
	}
	
	public void refreshSelectedCurvesVertexBufferAndSizeList()
	{
		List<FloatBuffer> buffArr =new ArrayList<FloatBuffer>();
		List<Integer> sizeArr = new ArrayList<Integer>();
		for (UUID id:selectedList)
		{
			ModelElement elem = getElementById(id);
			if (elem.getType() == ModelElement.TYPE_CURVE)
			{
				PolyLineRenderable pl = new PolyLineRenderable(((ModelCurve)elem).evaluate());
				buffArr.add(pl.getVertexBuffer());
				sizeArr.add(pl.size());
			}
		}
		selectedCurvesVertexBufferList = buffArr;
		selectedCurvesSizeList = sizeArr;
					
	}
	
	public List<FloatBuffer> getSelectedCurvesVertexBufferList()
	{
		return selectedCurvesVertexBufferList;
	}
	
	public List<Integer> getSelectedCurvesSizeList(){
		return activeCurvesSizeList;
	}
	
	//ACTIVE (all that has the active surface as parent but not selected)
	
	public void refreshActivePointsVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		for (ModelElement elem : elementList)
		{
			if (elem.getType() == ModelElement.TYPE_POINT)
			{
				ModelPoint p = (ModelPoint)elem;
				if (p.getParent().equals(activeSurface) && (!selectedList.contains(p.getId())) ){
					FloatBuffer buff = (new Vec3Renderable(p.evaluate())).getVertexBuffer();
					list.add(buff);					
				}
			}
		}
		activePointsVertexBufferList = list;
		Log.d(TAG + "active",Integer.toString(list.size()));
	}
	
	public List<FloatBuffer> getActivePointsVertexBufferList(){
		return activePointsVertexBufferList;
	}
	
	public void refreshActiveCurvesVertexBufferAndSizeList()
	{
		List<FloatBuffer> buffArr =new ArrayList<FloatBuffer>();
		List<Integer> sizeArr = new ArrayList<Integer>();
		for (ModelElement elem : elementList)
		{
			if (elem.getType() == ModelElement.TYPE_CURVE)
			{
				ModelCurve c = (ModelCurve)elem;
				if (c.getParent().equals(activeSurface))
				{
					PolyLineRenderable pl = new PolyLineRenderable(c.evaluate());
					buffArr.add(pl.getVertexBuffer());
					sizeArr.add(pl.size());					
				}				
			}
			
		}
		activeCurvesVertexBufferList = buffArr;
		activeCurvesSizeList = sizeArr;		
	}
	
	public List<FloatBuffer> getActiveCurvesVertexBufferList(){ 
		return activeCurvesVertexBufferList;
	}
	
	public List<Integer> getActiveCurvesSizeList(){
		return activeCurvesSizeList;
	}
	
	//EXTRA (all that belongs (extra) to the selected surface (only points now)
	
	public void refreshExtraPointsVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();	
		ModelSurface active = (ModelSurface)getElementById(activeSurface);
		List<UUID> extra = active.getExtraPoints();
		for (UUID id:extra)
		{
			ModelPoint p = (ModelPoint)getElementById(id);
			if (!selectedList.contains(p.getId()))
			{
				FloatBuffer buff = (new Vec3Renderable(p.evaluate())).getVertexBuffer();
				list.add(buff);			
			}
		}
		extraPointsVertexBufferList = list;
	}
	
	public List<FloatBuffer> getExtraPointsVertexBufferList(){
		return extraPointsVertexBufferList;
	}
	
	
	
	
	//PASSIVE (all except the ones on the selected surface, and the extras of selected surface)
	
	public void refreshPassivePointsVertexBufferList()
	{		
		List<FloatBuffer> arr =new ArrayList<FloatBuffer>();
		
		//collect active and extra points (selected trivial)
		List<UUID> activeExtraList = new ArrayList<UUID>();
		
		for (ModelElement elem:elementList)
		{
			if (elem.getType() == ModelElement.TYPE_POINT)
			{
				ModelPoint p = (ModelPoint)elem;
				ModelSurface  active = (ModelSurface)getElementById(activeSurface);
				if (p.getParent().equals(activeSurface) || active.getExtraPoints().contains(p.getId()) )
				{
					activeExtraList.add(p.getId());
				}
			}			
		}	
		
		for (ModelElement elem : elementList)
		{
			if (elem.getType() == ModelElement.TYPE_POINT)
			{
				if (!activeExtraList.contains(elem.getId()))
				{
					ModelPoint p = (ModelPoint)elem;
					FloatBuffer buff = (new Vec3Renderable(p.evaluate())).getVertexBuffer();
					arr.add(buff);										
				}				
			}			
		}
		passivePointsVertexBufferList = arr;	
	}
	
	public List<FloatBuffer> getPassivePointsVertexBufferList(){
		return passivePointsVertexBufferList;
	}
	
	public void refreshPassiveCurvesVertexBufferAndSizeList()
	{
		List<FloatBuffer> buffArr =new ArrayList<FloatBuffer>();
		List<Integer> sizeArr = new ArrayList<Integer>();
		for (ModelElement elem : elementList)
		{
			if (elem.getType() == ModelElement.TYPE_CURVE)
			{
				ModelCurve c = (ModelCurve)elem;
				if (!c.getParent().equals(activeSurface))
				{
					PolyLineRenderable pl = new PolyLineRenderable(c.evaluate());
					buffArr.add(pl.getVertexBuffer());
					sizeArr.add(pl.size());					
				}				
			}			
		}
		passiveCurvesVertexBufferList = buffArr;
		passiveCurvesSizeList = sizeArr;
	}
	
	public List<FloatBuffer> getPassiveCurvesVertexBufferList(){
		return passiveCurvesVertexBufferList;
	}
	
	public List<Integer> getPassiveCurvesSizeList()	{
		return passiveCurvesSizeList;		
	}
	
	public UUID getActiveSurface()
	{
		return activeSurface;		
	}
	
	public UUID getElementByScreenPosition(Vec2 sp,float lx,float ly,float[] mvp)
	{
		//TODO only supports points
		List<ModelPoint> pointList = new ArrayList<ModelPoint>();
		for (ModelElement elem : elementList)
		{	
			if (elem.getType() == ModelElement.TYPE_POINT){
				pointList.add((ModelPoint)elem);
			}
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
				return pointList.get(closest).getId();
			else return null;
		}
		else{
			return null;
		}
	}
		
	public void clear()
	{		
		ModelSurface base = new ModelSurfaceConst(this,new Vec3(0.0f,0.0f,0.0f),new Vec3(1.0f,0.0f,0.0f),new Vec3(0.0f,1.0f,0.0f));
		elementList = new ArrayList<ModelElement>();
		elementList.add(base);
		activeSurface = base.getId();
		selectedList = new ArrayList<UUID>();
	}
	
	public List<String> getPossibleSurfaceDefinitions()
	{
		ArrayList<String> arr = new ArrayList<String>();
		if (selectedList.size() == 1)
		{
			arr.add(DEFINE_SURFACE_PARENT);
		}
		else if (selectedList.size() == 2)
		{
			ModelElement elem1 = getElementById(selectedList.get(0));
			ModelElement elem2 = getElementById(selectedList.get(1));
			//test for DEFINE_BY_2POINTS_AND_COMMON_PERPEDICULAR_SURFACE 
			if (elem1.getType()==ModelElement.TYPE_POINT && elem2.getType()==ModelElement.TYPE_POINT)
			{
				ModelPoint p1 = (ModelPoint)elem1;
				ModelPoint p2 = (ModelPoint)elem2;				
				if (p1.getParent() == p2.getParent()){
					arr.add(DEFINE_SURFACE_2POINTS_AND_COMMON_PERPEDICULAR_SURFACE);
				}			
			}
			
			if (elem2.getType()==ModelElement.TYPE_POINT ){
				ModelPoint p1 = (ModelPoint)(elem2);
				if (elem1.getType() == ModelElement.TYPE_POINT)
				{
					if (!((ModelPoint)(elem1)).getParent().equals(p1.getParent())){
						arr.add(DEFINE_SURFACE_PARENTSURFACE_AND_OFFSET_POINT);
					}
				}
				else if (elem1.getType() == ModelElement.TYPE_CURVE)
				{
					if (!((ModelCurve)(elem1)).getParent().equals( p1.getParent())){
						arr.add(DEFINE_SURFACE_PARENTSURFACE_AND_OFFSET_POINT);
					}					
				}
												
			}
		}
		else if (selectedList.size() == 3)
		{
			ModelElement elem1 = getElementById(selectedList.get(0));
			ModelElement elem2 = getElementById(selectedList.get(1));
			ModelElement elem3 = getElementById(selectedList.get(2));
			if (elem1.getType()==ModelElement.TYPE_POINT && elem2.getType()==ModelElement.TYPE_POINT && elem3.getType()==ModelElement.TYPE_POINT){
				arr.add(DEFINE_SURFACE_3POINTS);
			}		
		}
		return arr;
	}
	
	public List<String> getPossibleElementDefinitions()
	{
		return null;
	}
		
	public void defineActiveSurface(String mode)
	{
		
		/*ModelSurfaceByTwoPointsAndSurface newSurf = new ModelSurfaceByTwoPointsAndSurface((ModelPoint)(selectedList.get(0)), (ModelPoint)(selectedList.get(1)),(ModelWithOrigAndTwoBase)(((ModelPoint)(selectedList.get(0))).getParent()) );
		surfaceList.add(newSurf);
		activeSurface = newSurf;*/
		

		if ( mode == DEFINE_SURFACE_PARENT )
		{
			ModelElement elem = getElementById(selectedList.get(0));
			if (elem.getType() == ModelElement.TYPE_POINT)
			{
				activeSurface = ((ModelPoint)(elem)).getParent();				
			}
		}
		else if ( mode == DEFINE_SURFACE_2POINTS_AND_COMMON_PERPEDICULAR_SURFACE )
		{
			ModelSurfaceByTwoPointsAndSurface newSurf = new ModelSurfaceByTwoPointsAndSurface(this, selectedList.get(0), selectedList.get(1),
					((ModelPoint)(getElementById(selectedList.get(0)))).getParent());
			boolean selected = false;

			for (ModelElement elem : elementList)
			{
				if (elem.getType() == ModelElement.TYPE_SURFACE)
				{
					ModelSurface s = (ModelSurface)elem;
					if (newSurf.equals(s))
					{
						activeSurface = s.getId();
						selected = true;
						break;
					}
				}
			}
			if (!selected){
				elementList.add(newSurf);
				activeSurface = newSurf.getId();					
			}
						
		}
		else if ( mode == DEFINE_SURFACE_3POINTS )
		{
			/*ModelSurfaceByThreePoints newSurf = new ModelSurfaceByThreePoints(this,(ModelPoint)(selectedList.get(0)),
					(ModelPoint)(selectedList.get(1)),(ModelPoint)(selectedList.get(2)));*/
			ModelSurfaceByThreePoints newSurf = new ModelSurfaceByThreePoints(this, selectedList.get(0), selectedList.get(1), selectedList.get(2));
			boolean selected = false;
			for (ModelElement elem : elementList)
			{
				if (elem.getType() == ModelElement.TYPE_SURFACE)
				{
					ModelSurface s = (ModelSurface)elem;
					if (newSurf.equals(s))
					{
						activeSurface = s.getId();
						selected = true;
						break;
					}
				}
			}
			if (!selected){
				elementList.add(newSurf);
				activeSurface = newSurf.getId();					
			}
			
		}
		else if ( mode == DEFINE_SURFACE_PARENTSURFACE_AND_OFFSET_POINT )
		{
			ModelElement elem1 = getElementById(selectedList.get(0));
			ModelSurfaceOffset newSurf = null;
			if (elem1.getType() == ModelElement.TYPE_POINT)
			{
				ModelPoint p = (ModelPoint)elem1;
				newSurf = new ModelSurfaceOffset(this,p.getParent() , selectedList.get(1));
			}
			
			if (newSurf!=null)
			{
				boolean selected = false;
				for (ModelElement elem : elementList)
				{
					if (elem.getType() == ModelElement.TYPE_SURFACE)
					{
						ModelSurface s = (ModelSurface)elem;
						if (newSurf.equals(s))
						{
							activeSurface = s.getId();
							selected = true;
							break;
						}
					}
				}
				if (!selected){
					elementList.add(newSurf);
					activeSurface = newSurf.getId();					
				}	
				
			}						
		}
	}
	
	public void refreshAllBuffer()
	{
		refreshSelectedPointsVertexBufferList();
		refreshSelectedCurvesVertexBufferAndSizeList();
		
		//
		//
		refreshExtraPointsVertexBufferList();
		
		
		refreshActivePointsVertexBufferList();
		refreshActiveCurvesVertexBufferAndSizeList();

		
		refreshPassivePointsVertexBufferList();
		refreshPassiveCurvesVertexBufferAndSizeList();

		
		
	}
	
	public void clearAllBuffer()
	{
		selectedPointsVertexBufferList = null;
		selectedCurvesVertexBufferList = null;
		selectedCurvesSizeList = null;
		
		activePointsVertexBufferList = null;
		activeCurvesVertexBufferList = null;
		activeCurvesSizeList = null;
		
		extraPointsVertexBufferList = null;
		
		passivePointsVertexBufferList = null;
		passiveCurvesVertexBufferList = null;
		passiveCurvesSizeList = null;		
	}
	
	public void selectElement(UUID id)
	{
		selectedList.add(id);
	}
	
	public boolean isSelected(UUID id)
	{
		return selectedList.contains(id);
	}
	
	public void unselectElement(UUID id)
	{
		selectedList.remove(id);

	}
	
	public void unselectAll()
	{
		selectedList.clear();		
	}
	
	
	
	
	void defineElement(String mode)
	{

	}
	
	public ModelElement getElementById(UUID id)
	{
		for (ModelElement elem: elementList)
		{
			if (elem.getId() == id){
				return elem;
			}
		}
		return null;
	}
	
	
	
	
}

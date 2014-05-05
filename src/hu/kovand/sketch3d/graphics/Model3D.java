package hu.kovand.sketch3d.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
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
import hu.kovand.sketch3d.utility.MyMath;

public class Model3D {

	public static final String TAG = "Model3D";
	
	public static final String DEFINE_SURFACE_DEFAULT = "Select..";  
	public static final String DEFINE_SURFACE_PARENT = "Parent"; 
	public static final String DEFINE_SURFACE_2POINTS_AND_COMMON_PERPEDICULAR_SURFACE = "P+P+Perp(S)";
	public static final String DEFINE_SURFACE_3POINTS = "P+P+P";
	public static final String DEFINE_SURFACE_PARENTSURFACE_AND_OFFSET_POINT = "Par(P|S)+Offs(Po)";
	
	public static final String DEFINE_ELEMENT_DEFAULT = "Select..";
	public static final String DEFINE_ELEMENT_LINE = "Line";
	
	public static final int REFRESH_BUFFER_ALL = 0;
	public static final int REFRESH_BUFFER_POINT_SELECT = 1;
	public static final int REFRESH_BUFFER_CURVE_SELECT = 2;
	public static final int REFRESH_BUFFER_POINT_ADD = 3;
	public static final int REFRESH_BUFFER_CURVE_ADD = 4;
	
	
	
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
	
	FloatBuffer boundingBoxBuffer;
	
	List<ModelElement> elementList; //holds all model element
	UUID activeSurface;
	List<UUID> selectedList;
	
	//EXTRA
	List<ModelPoint> bSplineKnots;
	List<FloatBuffer> knotPointsVertexBufferList;
	
	
	public Model3D()
	{		
		ModelSurface base = new ModelSurfaceConst(this,new Vec3(0.0f,0.0f,0.0f),new Vec3(1.0f,0.0f,0.0f),new Vec3(0.0f,1.0f,0.0f));
		elementList = new ArrayList<ModelElement>();
		elementList.add(base);
		activeSurface = base.getId();
		selectedList = new ArrayList<UUID>();
		
		//TMP
		
		knotPointsVertexBufferList = new ArrayList<FloatBuffer>();
		bSplineKnots = new ArrayList<ModelPoint>();
		
		refreshBuffer(REFRESH_BUFFER_ALL);
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
	
	
	
	
	
	//SELECTED (all in selectedList)
	
	public void refreshSelectedPointsVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();	
		for (UUID id:selectedList)
		{
			ModelElement elem = getElementById(id);
			if (elem.getType() == ModelElement.TYPE_POINT){
				FloatBuffer buff = (new Vec3Renderable(((ModelPoint)(elem)).evaluate(),Vec3Renderable.RADIUS_BIG)).getVertexBuffer();
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
		return selectedCurvesSizeList;
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
					FloatBuffer buff = (new Vec3Renderable(p.evaluate(),Vec3Renderable.RADIUS_BIG)).getVertexBuffer();
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
				if (c.getParent().equals(activeSurface) && (!selectedList.contains(c.getId())))
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
				FloatBuffer buff = (new Vec3Renderable(p.evaluate(),Vec3Renderable.RADIUS_BIG)).getVertexBuffer();
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
				if (!activeExtraList.contains(elem.getId()) && !selectedList.contains(elem.getId()) )
				{
					ModelPoint p = (ModelPoint)elem;
					FloatBuffer buff = (new Vec3Renderable(p.evaluate(),Vec3Renderable.RADIUS_BIG)).getVertexBuffer();
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
	
	//TODO fff
	public void refreshKnotPointsVertexBufferList()
	{
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		for (ModelPoint mp : bSplineKnots)
		{
			Vec3Renderable p = new Vec3Renderable(mp.evaluate(), Vec3Renderable.RADIUS_SMALL);
			list.add(p.getVertexBuffer());
		}		
		knotPointsVertexBufferList = list;
		Log.d(TAG + ".knotsize", Integer.toString(list.size()));
	}
	
	public List<FloatBuffer> getKnotPointsVertexBufferList()
	{
		return knotPointsVertexBufferList;
	}
	
	public List<FloatBuffer> getPassiveCurvesVertexBufferList(){
		return passiveCurvesVertexBufferList;
	}
	
	public List<Integer> getPassiveCurvesSizeList()	{
		return passiveCurvesSizeList;		
	}
	
	public void refreshBoundingBoxBuffer()
	{
		List<Float> xVec = new ArrayList<Float>();
		List<Float> yVec = new ArrayList<Float>();
		
		for (ModelElement elem : elementList)
		{
			if (elem.getType() == ModelElement.TYPE_POINT)
			{
				ModelPoint p = (ModelPoint)elem;
				Vec2 v = p.getAddress();
				xVec.add(v.getX());
				yVec.add(v.getX());
				
			}
			else if (elem.getType() == ModelElement.TYPE_CURVE)
			{
				ModelCurve c = (ModelCurve)elem;
				
			}
		}
		
		//get them
		
		float minX = Collections.min(xVec);
		float maxX = Collections.max(xVec);
		float minY = Collections.min(yVec);
		float maxY = Collections.max(yVec);		
		float xAvg =0.5f*(minX+maxX);
		float xDiff = minX + maxX;
		float yAvg = 0.5f*(minY+maxY);
		float yDiff =  minY + maxY;
		
		
		
		//final position
		ModelSurface surface = (ModelSurface)getElementById(activeSurface);
		Vec3 botleft = surface.evaluate(new Vec2(xAvg-xDiff, yAvg-yDiff));
		Vec3 topleft = surface.evaluate(new Vec2(xAvg-xDiff, yAvg+yDiff));
		Vec3 topright = surface.evaluate(new Vec2(xAvg+xDiff, yAvg+yDiff));
		Vec3 botright = surface.evaluate(new Vec2(xAvg+xDiff, yAvg-yDiff));
		
		ByteBuffer bb = ByteBuffer.allocateDirect(3*10);
		bb.order(ByteOrder.nativeOrder());
		boundingBoxBuffer = bb.asFloatBuffer();
		boundingBoxBuffer.position(0);	
		
		boundingBoxBuffer.put(botleft.getX()); boundingBoxBuffer.put(botleft.getY()); boundingBoxBuffer.put(botleft.getZ());
		boundingBoxBuffer.put(topleft.getX()); boundingBoxBuffer.put(topleft.getY()); boundingBoxBuffer.put(topleft.getZ());
		boundingBoxBuffer.put(topright.getX()); boundingBoxBuffer.put(topright.getY()); boundingBoxBuffer.put(topright.getZ());
		boundingBoxBuffer.put(botright.getX()); boundingBoxBuffer.put(botright.getY()); boundingBoxBuffer.put(botright.getZ());
		
		boundingBoxBuffer.put(botleft.getX()); boundingBoxBuffer.put(botleft.getY()); boundingBoxBuffer.put(botleft.getZ());
		boundingBoxBuffer.put(topleft.getX()); boundingBoxBuffer.put(topleft.getY()); boundingBoxBuffer.put(topleft.getZ());
		boundingBoxBuffer.put(topright.getX()); boundingBoxBuffer.put(topright.getY()); boundingBoxBuffer.put(topright.getZ());
		
		boundingBoxBuffer.put(topright.getX()); boundingBoxBuffer.put(topright.getY()); boundingBoxBuffer.put(topright.getZ());
		boundingBoxBuffer.put(botright.getX()); boundingBoxBuffer.put(botright.getY()); boundingBoxBuffer.put(botright.getZ());
		boundingBoxBuffer.put(botleft.getX()); boundingBoxBuffer.put(botleft.getY()); boundingBoxBuffer.put(botleft.getZ());
		
	}
	
	/**
	 * 
	 * @return 4 vertex for lines and 6 vertex for 2 triangles
	 */
	public FloatBuffer getBoundingBoxBuffer() {
		return boundingBoxBuffer;
	}
	
	public UUID getActiveSurface()
	{
		return activeSurface;		
	}
	
	public UUID getElementByScreenPosition(Vec2 sp,float lx,float ly,float[] mvp)
	{
		List<ModelPoint> pointList = new ArrayList<ModelPoint>();
		for (ModelElement elem : elementList)
		{	
			if (elem.getType() == ModelElement.TYPE_POINT){
				pointList.add((ModelPoint)elem);
			}
		}
		List<Vec2> screenPointList = new ArrayList<Vec2>();
		for (ModelPoint p : pointList){
			screenPointList.add(mapToScreenNorm(p.evaluate(), mvp));
		}	
		
		boolean isPointSelected = false;
		
		int closest = Vec2.findClosest(screenPointList, sp, lx, ly);
		if (closest>=0){
			float dist = Vec2.distance(screenPointList.get(closest), sp, lx, ly);
			if (dist<Constants.SELECT_DISTANCE)
			{
				isPointSelected = true;
				return pointList.get(closest).getId();
			}
		}
		
		if (!isPointSelected)
		{
			List<ModelCurve> curveList = new ArrayList<ModelCurve>();
			for (ModelElement elem : elementList){	
				if (elem.getType() == ModelElement.TYPE_CURVE){
					curveList.add((ModelCurve)elem);
				}
			}			
			if (curveList.size() == 0){
				return null;
			}
			
			List<List<Vec2>> screenCurveList = new ArrayList<List<Vec2>>();
			for (ModelCurve c : curveList){
				List<Vec3> eval = c.evaluate().getPoints();
				screenCurveList.add(mapToScreenNorm(eval, mvp));			
			}
			
			List<Float> closestList = new ArrayList<Float>();
			for (List<Vec2> c : screenCurveList)
			{
				closest = Vec2.findClosest(c, sp, lx, ly);
				float dist = Vec2.distance(c.get(closest), sp, lx, ly);
				closestList.add(dist);			
			}
			
			Float minDist = Collections.min(closestList);
			int minIndex = closestList.indexOf(minDist);
			if (minDist < Constants.SELECT_DISTANCE){
				return curveList.get(minIndex).getId();
			}
		}		
		return null;
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
		if ( mode == DEFINE_SURFACE_PARENT )
		{
			ModelElement elem = getElementById(selectedList.get(0));
			if (elem.getType() == ModelElement.TYPE_POINT)
			{
				activeSurface = ((ModelPoint)(elem)).getParent();				
			}
			if (elem.getType() == ModelElement.TYPE_CURVE)
			{
				activeSurface = ((ModelCurve)(elem)).getParent();				
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
	
	public void refreshBuffer(int mode)
	{
		if (mode == REFRESH_BUFFER_ALL){
			refreshSelectedPointsVertexBufferList();
			refreshSelectedCurvesVertexBufferAndSizeList();		
			refreshExtraPointsVertexBufferList();		
			refreshActivePointsVertexBufferList();
			refreshActiveCurvesVertexBufferAndSizeList();		
			refreshPassivePointsVertexBufferList();
			refreshPassiveCurvesVertexBufferAndSizeList();
		}
		else if (mode == REFRESH_BUFFER_POINT_SELECT){
			refreshSelectedPointsVertexBufferList();
			refreshExtraPointsVertexBufferList();
			refreshActivePointsVertexBufferList();
			refreshPassivePointsVertexBufferList();
			}
		else if (mode == REFRESH_BUFFER_POINT_ADD){
			refreshActivePointsVertexBufferList();						
		}
		else if (mode == REFRESH_BUFFER_CURVE_SELECT){
			refreshSelectedCurvesVertexBufferAndSizeList();
			refreshActiveCurvesVertexBufferAndSizeList();
			refreshPassiveCurvesVertexBufferAndSizeList();
		}
		else if (mode == REFRESH_BUFFER_CURVE_ADD){
			refreshActiveCurvesVertexBufferAndSizeList();			
		}
		
		
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
		ModelElement elem = getElementById(id);
		if (elem.getType() == ModelElement.TYPE_CURVE)
		{
			ModelCurve c = (ModelCurve)elem;
			PolyLine pl = new PolyLine(MyMath.listAddZ(c.getPoints(), 0.0f));
			BSpline bs = new BSpline();
			bs.approximate(pl, 3, c.getbSplineHint());
			List<Vec2> knotAddresses = MyMath.listRemoveZ(bs.evaluateAtKnots());
			for (Vec2 v : knotAddresses)
			{
				ModelPoint p = new ModelPoint(this, c.getParent(), v);
				p.setAttachedCurve(c.getId());
				bSplineKnots.add(p);
			}
			refreshKnotPointsVertexBufferList();
		}
		
		
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

	
	public List<ModelElement> exportActiveSurfaceAndDelete()
	{		
		List<ModelElement> arr = new ArrayList<ModelElement>();
		for (ModelElement elem : elementList)
		{
			if (elem.getType() == ModelElement.TYPE_POINT)
			{
				ModelPoint p = (ModelPoint)elem;
				if (p.getParent() == activeSurface){
					arr.add(elem);
				}
			}
			
			if (elem.getType() == ModelElement.TYPE_CURVE)
			{
				ModelCurve c = (ModelCurve)elem;
				if (c.getParent() == activeSurface){
					arr.add(elem);
				}
			}			
		}
		
		elementList.removeAll(arr);
		return arr;
	}
	
	public void importActiveSurface(List<ModelElement> elems)
	{
		elementList.addAll(elems);
	}
	
	
	
	
	public static Vec2 mapToScreenNorm(Vec3 modelPos,float [] mvp)
	{		
		float[] screen3D = new float[4];
		Matrix.multiplyMV(screen3D, 0, mvp, 0,(new Vec4(modelPos)).toArray(), 0);
		Vec2 screen = new Vec2(screen3D[0]/screen3D[3],screen3D[1]/screen3D[3]);
		return screen;
	}
	
	public static List<Vec2> mapToScreenNorm(List<Vec3> modelPosList,float [] mvp)
	{
		List<Vec2> arr = new ArrayList<Vec2>();
		for (Vec3 p : modelPosList){
			arr.add(mapToScreenNorm(p, mvp));
		}
		return arr;
	}
	
	
	
	
	
	
}

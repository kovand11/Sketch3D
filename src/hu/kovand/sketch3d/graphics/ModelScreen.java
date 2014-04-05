/*package hu.kovand.sketch3d.graphics;

import hu.kovand.sketch3d.geometry.BSpline;
import hu.kovand.sketch3d.geometry.HermiteSpline;
import hu.kovand.sketch3d.geometry.HybridCurve;
import hu.kovand.sketch3d.geometry.Point3D;
import hu.kovand.sketch3d.geometry.PointSet;
import hu.kovand.sketch3d.geometry.PolyLineDeprecated;
import hu.kovand.sketch3d.utility.IntersectionAddress;
import hu.kovand.sketch3d.utility.MyMath;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.graphics.Picture;
import android.util.Log;
import android.webkit.WebView.FindListener;

public class ModelScreen {
	public static final String TAG = "Model2D";
	
	public static final int TYPE_NONE = 0;
	public static final int TYPE_POINT = 1;
	public static final int TYPE_CURVE= 2;

	
	public static final int MERGE_DISABLED = 4;
	public static final int MERGE_POLYLINE = 5;
	public static final int MERGE_POLYLINE_POINT = 6;
	
	public static final float CURVE_TO_POINT_MERGE_THRESH = 50.0f;
	public static final float POINT_TO_CURVE_MERGE_THRESH = 50.0f;
	public static final int LINE_MERGE_THRESH = 200;
	
	
	
	PointSet pointSet;
	//ArrayList< HybridCurve > hybridCurveList;
	ArrayList<BSpline> bsplineList;
	int lastAddedType = TYPE_NONE;
	
	
	public ModelScreen()
	{
		pointSet = new PointSet(10000);
		//hybridCurveList = new ArrayList<HybridCurve>();
		bsplineList = new ArrayList<BSpline>();		
	}
	
	public void clear()
	{
		pointSet.clear();
		hybridCurveList.clear();
		bsplineList.clear();	
	}
	
	public void clearPoints()
	{
		pointSet.clear();	
	}
	
	public void clearHybridCurves()
	{
		hybridCurveList.clear();	
	}
	
	public void clearBSplines()
	{
		bsplineList.clear();	
	}
	
	public void addPoint(Point3D p)
	{
		if (hybridCurveList.size()!=0)
		{
			ArrayList<Point3D> endpoints = new ArrayList<Point3D>();
			endpoints.ensureCapacity(hybridCurveList.size());
			for (int i=0;i<hybridCurveList.size();i++)
			{
				endpoints.add(hybridCurveList.get(i).polyLine.get(0));
				endpoints.add(hybridCurveList.get(i).polyLine.get(hybridCurveList.get(i).polyLine.size()-1));
			}
			
			int closest = MyMath.findClosest(endpoints, p);
			if (Point3D.distance(endpoints.get(closest), p) < POINT_TO_CURVE_MERGE_THRESH)
			{
				p = new Point3D(endpoints.get(closest).getX(), endpoints.get(closest).getY(), endpoints.get(closest).getZ());
			}
		}
		
		lastAddedType = TYPE_POINT;		

		
		pointSet.add(p);
		
		
	}
	
	public void addHybridCurve(PolyLineDeprecated pl,int merge)
	{
		HybridCurve newHybrid = new HybridCurve(pl, 3, 10, 200);
		HybridCurve h = null;
		if (hybridCurveList.size()!=0)
		{
			ArrayList<PolyLineDeprecated> cutted = new ArrayList<PolyLineDeprecated>();
			ArrayList<IntersectionAddress> inters = MyMath.findIntersections(hybridCurveList.get(hybridCurveList.size()-1).polyLine, newHybrid.polyLine);
			if (inters.size() ==0){
				cutted.add(newHybrid.polyLine);
			}
			else
			{
				for (int i=0;i<inters.size()+1;i++)
				{
					if (i==0){
						cutted.add(newHybrid.polyLine.subPolyLine(0, inters.get(i).p2));										
					}
					else if (i==inters.size()){
						cutted.add(newHybrid.polyLine.subPolyLine(inters.get(i-1).p2,newHybrid.polyLine.size()-1));											
					}
					else{
						cutted.add(newHybrid.polyLine.subPolyLine(inters.get(i-1).p2,inters.get(i).p2));											
					}
				}				
			}
			
			Log.d("cutted",Integer.toString(cutted.size()));
			
			for (int i=0;i<cutted.size();i++)
			{
				if (i==0)
				{
					//ignore
					
				}
				else if (i==cutted.size()-1)
				{
					//ignore
					
				}
				else
				{
					if (cutted.get(i).size()>10)
					h = HybridCurve.merge(hybridCurveList.get(hybridCurveList.size()-1), cutted.get(i));		
				}				
			}	
			
		}
		
		
		if (h==null)
			h = new HybridCurve(pl,3,10,200);
		clearHybridCurves();
		hybridCurveList.add(h);		
		
		/*HybridCurve newHybrid = new HybridCurve(pl, 3, 10, 500);
		PolyLine newPoly = newHybrid.polyLine;
		HybridCurve newToAdd = null;
		ArrayList<Integer> curvesToRemove;
		
		
		//check for points
		if (pointSet.size()!=0)
		{
			int closest_to_start = MyMath.findClosest(pointSet.getPointList(), newPoly.get(0));
			int closest_to_end = MyMath.findClosest(pointSet.getPointList(), newPoly.get(newHybrid.polyLine.size()-1));
			float distance_to_start = Point3D.distance(pointSet.get(closest_to_start),newPoly.get(0) );
			float distance_to_end = Point3D.distance(pointSet.get(closest_to_end),newPoly.get(newPoly.size()-1) );
			
			if (distance_to_start<CURVE_TO_POINT_MERGE_THRESH){
				newPoly.getPoints().add(0, pointSet.get(closest_to_start));			
			}
			if (distance_to_end<CURVE_TO_POINT_MERGE_THRESH && closest_to_start != closest_to_end){
				newPoly.getPoints().add(newPoly.getPoints().size(), pointSet.get(closest_to_start));							
			}
			
		}
		
		
		//foreach curve
		for (int i=0;i<hybridCurveList.size();i++)
		{
			
		}
		////
		
		
		
		
		
		
	}
	
	PointSet getPointSet()
	{
		return pointSet;		
	}
	
	public ArrayList<FloatBuffer> getStoredCurvesVertexBufferList()
	{
		ArrayList<FloatBuffer> arr = new ArrayList<FloatBuffer>();
		
		for (int i=0;i<hybridCurveList.size();i++)
		{
			arr.add(hybridCurveList.get(i).polyLine.getVertexBuffer());
		}
		
		
		return arr;
	}
	
	public ArrayList<Integer> getStoredCurvesSizeList()
	{
		
		ArrayList<Integer> arr = new ArrayList<Integer>();
		
		for (int i=0;i<hybridCurveList.size();i++)
		{
			arr.add(hybridCurveList.get(i).polyLine.size());
		}
		return arr;
	}
}*/

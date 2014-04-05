/*package hu.kovand.sketch3d.geometry;

import java.util.ArrayList;

import android.util.Log;

import hu.kovand.sketch3d.graphics.ModelScreen;
import hu.kovand.sketch3d.utility.MyMath;


//both polyline and bspline representation
public class HybridCurve {
	
	public PolyLineDeprecated polyLine;
	public BSpline bspline;
	
	public static final int POS_START = 0;
	public static final int POS_END = 1;
	
	public static ModelScreen testoutput; 

	//polyline -> bspline (store) -> polyline (store)
	public HybridCurve(PolyLineDeprecated p,int p_param,int n_param,int points) {
		bspline = new BSpline();
		bspline.approximate(p,p_param,n_param);
		polyLine = bspline.evaluate(points);
	}
	
	public HybridCurve(BSpline b,int points) {
		bspline = b;
		polyLine = bspline.evaluate(points);
	}
	
	
	public static HybridCurve extend(HybridCurve curve1,int pos1,HybridCurve curve2,int pos2)
	{
		PolyLineDeprecated result = new PolyLineDeprecated(curve1.polyLine.size()+curve2.polyLine.size()+50);
		
		if (pos1 == POS_END && pos2 == POS_START)
		{
			PolyLineDeprecated p1 = curve1.polyLine;
			PolyLineDeprecated p2 = curve2.polyLine;
			int mapped1to2 = MyMath.findClosest(p2.getPoints(), p1.get(p1.size()-1));
			int mapped2to1 = MyMath.findClosest(p1.getPoints(), p2.get(0));
					
			float dist1 = Point3D.distance(p1.get(p1.size()-1), p2.get(mapped1to2));
			float dist2 = Point3D.distance(p2.get(0), p1.get(mapped2to1));
			
			int cut1 = MyMath.findSpan(p1.getPoints(), mapped2to1, dist1, MyMath.DIRECTION_BACKWARD);
			int cut2 = MyMath.findSpan(p1.getPoints(), mapped1to2, dist1, MyMath.DIRECTION_FORWARD);
			Point3D tan1;
			try{
				tan1 = MyMath.tangent(p1.get(cut1), p1.get(cut1-1));
			}
			catch(IndexOutOfBoundsException e){
				return null;				
			}
			tan1 = Point3D.multiply(tan1, -dist1);
			
			Point3D tan2;
			try{
				tan2 = MyMath.tangent(p2.get(cut2), p1.get(cut2+1));			
			}
			catch (IndexOutOfBoundsException e)
			{
				return null;
			}
			
			tan2 = Point3D.multiply(tan2, -dist2);			
			
			HermiteSpline hspline =  new HermiteSpline(p1.get(cut1), tan1, p2.get(cut2), tan2);			
			PolyLineDeprecated eval_hspline = hspline.evaluate(50);
			
			for (int i=0;i<cut1;i++)
			{
				result.append(p1.get(i));
			}
			
			for (int i=0;i<eval_hspline.size();i++)
			{
				result.append(eval_hspline.get(i));
			}
			
			for (int i=cut2+1;i<p2.size();i++)
			{
				result.append(p2.get(i));
			}			
			
		}		
		return new HybridCurve(result,3,curve1.bspline.n+curve2.bspline.n,curve1.polyLine.size()+curve2.polyLine.size());
	}
	
	public static HybridCurve merge(HybridCurve curve1,PolyLineDeprecated curve2)
	{
		
		
		PolyLineDeprecated result = new PolyLineDeprecated(curve1.polyLine.size()+curve2.size());
		
		//map all curve2 points to curve1
		ArrayList<Integer> mapped_indexes = new ArrayList<Integer>();
		
		
		for (int i = 0;i<curve2.size();i++)
		{
			int pos = MyMath.findClosest(curve1.polyLine.getPoints(), curve2.get(i));	
			mapped_indexes.add(pos);
		}
		
		//DIRECTION
		
		int cut1 =mapped_indexes.get(0);
		int cut2 =mapped_indexes.get(mapped_indexes.size()-1);
		
		int cut_min=Math.min(cut1, cut2);
		int cut_max=Math.max(cut1, cut2);
		
		Log.d("cutmin",Integer.toString(cut_min));
		Log.d("cutmax",Integer.toString(cut_max));
		
		for (int i=0;i<cut_min;i++)
		{
			result.append(curve1.polyLine.get(i));		
		}
		
		for (int i=0;i<curve2.size();i++)
		{			
			float t= (1.0f*i)/(curve2.size()-1);
			float weight = MyMath.weightFunction(t, 0.5f, 0.6f);
			Point3D p1 = curve1.polyLine.get(mapped_indexes.get(i));
			Point3D p2 = curve2.get(i);
			Point3D p = Point3D.weightedAdd(p1, 1-weight, p2, weight);			
			result.append(p);
		}
		
		
		for (int i=cut_max+1;i<curve1.polyLine.size();i++)
		{
			result.append(curve1.polyLine.get(i));
		}
		
		for (int i=0;i<result.size();i++)
		{
			Log.d("res",result.get(i).toString());
		}
		
		
		
		//can be improved
		return new HybridCurve(result,3,curve1.bspline.n ,curve1.polyLine.size());
	}
	
	

}*/

package hu.kovand.sketch3d.geometry;

import hu.kovand.sketch3d.utility.MyMath;

import java.util.ArrayList;
import java.util.List;

import android.util.FloatMath;

public class PolyLine {
	
	public static final String TAG = "PolyLine";
	private List<Vec3> points;

	public PolyLine() {
		points = new ArrayList<Vec3>();
	}
	
	public PolyLine(List<Vec3> ps) {
		points = ps;
	}
	
	public void add(Vec3 p){
		points.add(p);
	}
	
	public void add(List<Vec3> ps){
		points.addAll(ps);		
	}
	
	public Vec3 get(int i){
		return points.get(i);
	}
	
	public PolyLine subPolyLine(int start,int end){		
		return new PolyLine(points.subList(start, end));		
	}
	
	public List<Vec3> getPoints(){
		return points;
	}
	
	public int size(){
		return points.size();
	}
	
	public Vec3 evaluate(float u)
	{
		int floor = (int)Math.floor(u*(points.size()-1));
		int ceil = (int)Math.ceil(u*(points.size()-1));
		float frac = u - floor;
		Vec3 p1 = points.get(floor);
		Vec3 p2 = points.get(ceil);
		return Vec3.weightedAdd(p1, 1.0f-frac, p2, frac);
	}
	
	public static PolyLine merge(PolyLine a,PolyLine b)
	{		
		//TODO consider bspline wrap
		ArrayList<Integer> projected_indexes = new ArrayList<Integer>();
		
		for (int i = 0;i<b.size();i++)
		{
			int pos = MyMath.findClosest(a.getPoints(), b.get(i));	
			projected_indexes.add(pos);
		}
		
		int cutStart =projected_indexes.get(0);
		int cutEnd =projected_indexes.get(projected_indexes.size()-1);
		
		PolyLine weighted = new PolyLine();
		
		for (int i=0;i<b.size();i++)
		{			
			float t= (1.0f*i)/(b.size()-1);
			float weight = MyMath.weightFunction(t, 0.5f, 0.6f);
			Vec3 p1 = a.get(projected_indexes.get(i));
			Vec3 p2 = b.get(i);
			Vec3 p = Vec3.weightedAdd(p1, 1-weight, p2, weight);			
			weighted.add(p);
		}
		
		PolyLine result = new PolyLine();
		
		if (cutStart<cutEnd)
		{
			result.add(a.subPolyLine(0, cutStart-1).getPoints());
			result.add(weighted.getPoints());
			result.add(a.subPolyLine(cutEnd+1,a.size()-1).getPoints());
		}
		else
		{
			result.add(a.subPolyLine(0, cutStart-1).getPoints());
			for (int i=weighted.size()-1;i>=0;i--)
			{
				result.add(weighted.get(i));								
			}			
			result.add(a.subPolyLine(cutEnd+1,a.size()-1).getPoints());		
		}		
		
		return result;		
	}
}

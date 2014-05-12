package hu.kovand.sketch3d.geometry;

import hu.kovand.sketch3d.utility.MyMath;

import java.util.ArrayList;
import java.util.List;


public class PolyLine {
	
	public static final String TAG = "PolyLine";
	private List<Vec3> points;

	public PolyLine() {
		points = new ArrayList<Vec3>();
	}
	
	public PolyLine(List<Vec3> ps) {
		points = ps;
	}	
	
	public PolyLine(List<Vec2> ps,float defaultZ)
	{
		points = new ArrayList<Vec3>();
		for (Vec2 v : ps){
			points.add(new Vec3(v.getX(),v.getY(),defaultZ));
		}		
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
	
	public List<Vec2> getPointsIgnoreZ()
	{
		List<Vec2> list = new ArrayList<Vec2>();
		for (Vec3 v : points)
		{
			list.add(v.ignoreZ());
		}
		return list;		
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
	

}

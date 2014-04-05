package hu.kovand.sketch3d.geometry;

import java.util.ArrayList;
import java.util.List;

public class PolyLine {
	
	public static final String TAG = "PolyLine";
	private List<Point3D> points;

	public PolyLine() {
		points = new ArrayList<Point3D>();
	}
	
	public PolyLine(List<Point3D> ps) {
		points = ps;
	}
	
	public void add(Point3D p){
		points.add(p);
	}
	
	public void add(List<Point3D> ps){
		points.addAll(ps);		
	}
	
	public Point3D get(int i){
		return points.get(i);
	}
	
	public PolyLine subPolyLine(int start,int end){		
		return new PolyLine(points.subList(start, end));		
	}
	
	public List<Point3D> getPoints(){
		return points;
	}
	
	public int size(){
		return points.size();
	}

}

package hu.kovand.sketch3d.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;

public abstract class ModelSurface extends ModelElement {
	
	private List<ModelPoint> points;
	private List<ModelCurve> curves;
	UUID id;
	

	public ModelSurface() {
		super();
		points = new ArrayList<ModelPoint>();
		curves = new ArrayList<ModelCurve>();
		id = UUID.randomUUID();
	}
	
	public abstract Vec3 evaluate(Vec2 addr);
	
	public void addPoint(Vec2 addr)
	{
		
		//TODO change to modellpoint
		points.add(new ModelPoint(this,addr));
	}
	
	public List<ModelPoint> getPoints()
	{
		return points;		
	}
	
	public void addCurve(List<Vec2> addrs)
	{
		ModelCurve c= new ModelCurve(this, addrs);
		curves.add(c);
	}
	
	public List<ModelCurve> getCurves()
	{
		return curves;		
	}
	
	public abstract Vec2 findRayIntersection(Vec2 screenPoint,float[] mvp);

	public UUID getId(){
		return id;
	}
	
	@Override
	public int getType() {
		return TYPE_SURFACE;
	}

}

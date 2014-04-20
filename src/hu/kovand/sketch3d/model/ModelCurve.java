package hu.kovand.sketch3d.model;

import hu.kovand.sketch3d.geometry.PolyLine;
import hu.kovand.sketch3d.geometry.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.graphics.PointF;
import android.os.PowerManager;

public class ModelCurve extends ModelElement {
	
	
	
	ModelSurface parent;
	List<Vec2> points;
	
	//optional relations
	ModelPoint startPoint = null;
	ModelPoint endPoint = null;
	
	

	public ModelCurve(ModelSurface parent,List<Vec2> points) {
		super();
		this.parent = parent;
		this.points = points;
	}
	
	
	
	public PolyLine evaluate()
	{
		PolyLine result = new PolyLine();
		for (int i=0;i<points.size();i++)
		{
			result.add(parent.evaluate(points.get(i)));						
		}		
		return result;
	}
	
	public void setParent(ModelSurface p)
	{
		parent = p;
	}
	
	public ModelSurface getParent(){
		return parent;
	}
	
	public int size()
	{
		return points.size();
		
	}


	@Override
	public int getType() {
		return TYPE_CURVE;
	}
	
	@Override
	public int getSubType() {
		return SUBTYPE_CURVE_COMMON;
	}
	
	

}

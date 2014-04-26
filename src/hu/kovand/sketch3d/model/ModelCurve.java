package hu.kovand.sketch3d.model;

import hu.kovand.sketch3d.geometry.PolyLine;
import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.graphics.Model3D;


import java.util.List;
import java.util.UUID;



public class ModelCurve extends ModelElement {
	
	
	
	UUID parent;
	List<Vec2> points;
	
	//optional relations
	UUID attachedToStart;
	UUID attachedToEnd;
	
	

	public ModelCurve(Model3D m,UUID parent,List<Vec2> points) {
		super(m);
		this.parent = parent;
		this.points = points;
	}
	
	
	
	public PolyLine evaluate()
	{
		PolyLine result = new PolyLine();
		for (int i=0;i<points.size();i++)
		{
			result.add(((ModelSurface)getModel().getElementById(parent)).evaluate(points.get(i)));						
		}		
		return result;
	}
	
	public void setParent(UUID p)
	{
		parent = p;
	}
	



	public UUID getParent(){
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

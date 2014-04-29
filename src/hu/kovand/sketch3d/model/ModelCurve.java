package hu.kovand.sketch3d.model;

import hu.kovand.sketch3d.geometry.PolyLine;
import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.graphics.Model3D;


import java.util.List;
import java.util.UUID;



public class ModelCurve extends ModelElement {
	
	
	
	UUID parent;
	List<Vec2> points;
	
	//optional relations
	UUID attachedToStart;
	UUID attachedToEnd;
	
	

	public ModelCurve(Model3D m,UUID parent,List<Vec2> points,UUID startPoint,UUID endPoint) {
		super(m);
		this.parent = parent;
		this.points = points;
		attachedToStart = startPoint;
		attachedToEnd = endPoint;
	}
	
	public ModelCurve(ModelCurve c,UUID startPoint,UUID endPoint)
	{
		super(c.getModel(),c.getId());
		parent = c.getParent();
		points = c.getPoints();
		attachedToStart = startPoint;
		attachedToEnd = endPoint;
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
	
	public Vec3 evaluateAt(int index)
	{
		return ((ModelSurface)getModel().getElementById(parent)).evaluate(points.get(index));		
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
	
	public List<Vec2> getPoints()
	{
		return points;
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

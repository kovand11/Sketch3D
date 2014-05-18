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
	int bSplineHint;
	
	//optional relations
	UUID attachedToStart;
	UUID attachedToEnd;
	
	

	/**
	 * Main constuctor
	 * @param m
	 * @param parent
	 * @param points
	 * @param startPoint
	 * @param endPoint
	 * @param bSplineHint
	 */
	public ModelCurve(Model3D m,UUID parent,List<Vec2> points,UUID startPoint,UUID endPoint,int bSplineHint) {
		super(m);
		this.parent = parent;
		this.points = points;
		attachedToStart = startPoint;
		attachedToEnd = endPoint;
		this.bSplineHint = bSplineHint;
	}
	
	public ModelCurve(Model3D m,UUID parent,List<Vec2> points,UUID startPoint,UUID endPoint,int bSplineHint,UUID id) {
		super(m,id);
		this.parent = parent;
		this.points = points;
		attachedToStart = startPoint;
		attachedToEnd = endPoint;
		this.bSplineHint = bSplineHint;
	}
	
	/**
	 * Copy constructor with startpoint, endpoint change
	 * @param c
	 * @param startPoint
	 * @param endPoint
	 */
	public ModelCurve(ModelCurve c,UUID startPoint,UUID endPoint)
	{
		super(c.getModel(),c.getId());
		parent = c.getParent();
		points = c.getPoints();
		bSplineHint = c.getbSplineHint();
		attachedToStart = startPoint;
		attachedToEnd = endPoint;
	}
	
	/**
	 * Copy constructor with bSplineHint change and pointList change
	 * @param c
	 * @param newBSplineHint
	 */
	public ModelCurve(ModelCurve c,List<Vec2> points,int newBSplineHint)
	{
		super(c.getModel(),c.getId());
		parent = c.getParent();
		this.points = points;
		attachedToStart = c.getAttachedToStart();
		attachedToEnd = getAttachedToEnd();
		bSplineHint = newBSplineHint;		
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
	public int getbSplineHint() {
		return bSplineHint;
	}
	
	
	public UUID getAttachedToStart() {
		return attachedToStart;
	}
	
	public UUID getAttachedToEnd() {
		return attachedToEnd;
	}
	

}

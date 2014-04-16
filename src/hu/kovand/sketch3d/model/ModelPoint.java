package hu.kovand.sketch3d.model;

import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;

import java.util.UUID;


public class ModelPoint extends ModelElement {
	
	ModelSurface parent;
	Vec2 address;

	public ModelPoint(ModelSurface par ,Vec2 addr) {
		super();
		parent = par;
		address = addr;
	}
	
	public Vec3 evaluate()
	{
		return parent.evaluate(address);	
	}
	
	public void setParent(ModelSurface p)
	{
		parent = p;
	}
	
	
	@Override
	public int getType() {
		return TYPE_POINT;
	}
	
	public ModelSurface getParent(){
		return parent;
	}

}

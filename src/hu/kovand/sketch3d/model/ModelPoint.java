package hu.kovand.sketch3d.model;

import java.util.UUID;

import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.graphics.Model3D;

import android.os.Parcel;



public class ModelPoint extends ModelElement {
	
	UUID parent;
	Vec2 address;	

	public ModelPoint(Model3D m,UUID p ,Vec2 addr) {
		super(m);
		parent = p;
		address = addr;
	}
	
	public ModelPoint(ModelPoint p,Vec2 newAddress)	{
		super(p.getModel(),p.getId());
		parent = p.getParent();
		address = newAddress;
	}

	
	public Vec3 evaluate()
	{
		return ((ModelSurface)getModel().getElementById(parent)).evaluate(address);	
	}
	
	public void setParent(UUID p)
	{
		parent = p;
	}
	
	
	@Override
	public int getType() {
		return TYPE_POINT;
	}
	
	@Override
	public int getSubType() {
		return SUBTYPE_POINT_COMMON;
	}
	
	public UUID getParent(){
		return parent;
	}
	
	public Vec2 getAddress() {
		return address;
	}

}

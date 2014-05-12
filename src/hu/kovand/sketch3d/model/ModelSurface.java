package hu.kovand.sketch3d.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.graphics.Model3D;

public abstract class ModelSurface extends ModelElement{
	
	

	public ModelSurface(Model3D m) {
		super(m);
	}
	
	public abstract Vec3 evaluate(Vec2 addr);
	
	public abstract Vec2 findRayIntersection(Vec2 screenPoint,float[] mvp);

	
	@Override
	public int getType() {
		return TYPE_SURFACE;
	}
	
	public abstract List<UUID> getExtraPoints();


	
	
	
	


}

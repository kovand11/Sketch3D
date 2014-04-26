package hu.kovand.sketch3d.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.graphics.Model3D;

public class ModelSurfaceOffset extends ModelSurfaceWithOrigAndTwoBase {
	
	UUID surface;
	UUID point;

	public ModelSurfaceOffset(Model3D m,UUID s,UUID p) {
		super(m);
		surface = s;
		point = p;
	}

	@Override
	Vec3 getOrig() {
		ModelPoint p = (ModelPoint)(getModel().getElementById(point));
		return p.evaluate();
	}

	@Override
	Vec3 getBaseVec1() {

		ModelSurfaceWithOrigAndTwoBase s = (ModelSurfaceWithOrigAndTwoBase)(getModel().getElementById(surface));
		return s.getBaseVec1();
	}

	@Override
	Vec3 getBaseVec2() {

		ModelSurfaceWithOrigAndTwoBase s = (ModelSurfaceWithOrigAndTwoBase)(getModel().getElementById(surface));
		return s.getBaseVec2();
	}

	@Override
	public int getSubType() {
		return SUBTYPE_SURFACE_SURFACE_OFFSET;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ModelSurfaceOffset)
		{
			ModelSurfaceOffset s = (ModelSurfaceOffset)o;
			return (surface == s.surface && point == s.point);
		}
		else return false;
	}
	
	public java.util.List<UUID> getExtraPoints() 
	{
		List<UUID> arr = new ArrayList<UUID>();
		arr.add(point);
		return arr;		
	};
	
	

}

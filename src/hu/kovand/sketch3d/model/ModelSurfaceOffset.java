package hu.kovand.sketch3d.model;

import hu.kovand.sketch3d.geometry.Vec3;

public class ModelSurfaceOffset extends ModelWithOrigAndTwoBase {
	
	ModelWithOrigAndTwoBase surface;
	ModelPoint point;

	public ModelSurfaceOffset(ModelWithOrigAndTwoBase s,ModelPoint p) {
		super();
		surface = s;
		point = p;
	}

	@Override
	Vec3 getOrig() {
		return point.evaluate();
	}

	@Override
	Vec3 getBaseVec1() {
		return surface.getBaseVec1();
	}

	@Override
	Vec3 getBaseVec2() {
		return surface.getBaseVec2();
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

}

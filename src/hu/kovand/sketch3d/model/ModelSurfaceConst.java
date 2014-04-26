package hu.kovand.sketch3d.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.graphics.Model3D;

public class ModelSurfaceConst extends ModelSurfaceWithOrigAndTwoBase {
	
	Vec3 orig;
	Vec3 v1;
	Vec3 v2;

	public ModelSurfaceConst(Model3D m,Vec3 orig, Vec3 v1,Vec3 v2) {
		super(m);
		this.orig = orig;
		this.v1 = v1;
		this.v2 = v2;
	}

	

	@Override
	Vec3 getOrig() {
		return orig;

	}

	@Override
	Vec3 getBaseVec1() {
		return v1;
	}

	@Override
	Vec3 getBaseVec2() {
		// TODO Auto-generated method stub
		return v2;
	}
	
	@Override
	public int getSubType() {
		return SUBTYPE_SURFACE_CONST;
	}
	
	@Override
	public List<UUID> getExtraPoints() {
		List<UUID> arr = new ArrayList<UUID>();
		return arr;
	}
}

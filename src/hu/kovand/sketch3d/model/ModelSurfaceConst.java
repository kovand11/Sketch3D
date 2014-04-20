package hu.kovand.sketch3d.model;

import android.opengl.Matrix;
import android.util.Log;
import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.geometry.Vec4;
import hu.kovand.sketch3d.graphics.GLRenderer;

public class ModelSurfaceConst extends ModelWithOrigAndTwoBase {
	
	Vec3 orig;
	Vec3 v1;
	Vec3 v2;

	public ModelSurfaceConst(Vec3 orig, Vec3 v1,Vec3 v2) {
		super();
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
}

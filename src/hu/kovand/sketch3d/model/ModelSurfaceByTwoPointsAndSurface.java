package hu.kovand.sketch3d.model;

import android.opengl.Matrix;
import android.util.Log;
import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.graphics.GLRenderer;

public class ModelSurfaceByTwoPointsAndSurface extends ModelWithOrigAndTwoBase {
	
	//orig: point1 
	//base1: point2-point1
	//base2: parent.base1 cross pare
	
	ModelPoint point1;
	ModelPoint point2;
	ModelWithOrigAndTwoBase surface;

	public ModelSurfaceByTwoPointsAndSurface(ModelPoint p1,ModelPoint p2,ModelWithOrigAndTwoBase s) {
		super();
		point1 = p1;
		point2 = p2;
		surface = s;		
	}


	@Override
	Vec3 getOrig() {
		return point1.evaluate();
	}

	@Override
	Vec3 getBaseVec1() {
		return Vec3.subtract(point2.evaluate(), point1.evaluate());
	}

	@Override
	Vec3 getBaseVec2() {
		return Vec3.crossProduct(Vec3.subtract(point2.evaluate(), point1.evaluate()), surface.getBaseVec1());
	}

}

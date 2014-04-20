package hu.kovand.sketch3d.model;

import hu.kovand.sketch3d.geometry.Vec3;

public class ModelSurfaceByThreePoints extends ModelWithOrigAndTwoBase {
	
	ModelPoint point1;
	ModelPoint point2;
	ModelPoint point3;

	public ModelSurfaceByThreePoints(ModelPoint p1,ModelPoint p2,ModelPoint p3) {
		super();
		point1 = p1;
		point2 = p2;
		point3 = p3;
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
		return Vec3.subtract(point3.evaluate(), point1.evaluate());
	}

	@Override
	public int getSubType() {
		return SUBTYPE_SURFACE_SURFACE_BY_THREE_POINTS;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ModelSurfaceByThreePoints)
		{
			ModelSurfaceByThreePoints s = (ModelSurfaceByThreePoints)o;
			//assuming that p1 p2 and p3 never equal
			boolean b1 = this.point1 == s.point1 || this.point1 == s.point2 || this.point1 == s.point3;
			boolean b2 = this.point2 == s.point2 || this.point2 == s.point2 || this.point2 == s.point3;
			boolean b3 = this.point3 == s.point3 || this.point3 == s.point2 || this.point3 == s.point3;
			return b1 && b2 && b3;
		}
		else return false;
	}

}

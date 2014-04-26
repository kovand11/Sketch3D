package hu.kovand.sketch3d.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.graphics.Model3D;

public class ModelSurfaceByThreePoints extends ModelSurfaceWithOrigAndTwoBase {
	
	UUID point1;
	UUID point2;
	UUID point3;

	public ModelSurfaceByThreePoints(Model3D m,UUID p1,UUID p2,UUID p3) {
		super(m);
		point1 = p1;
		point2 = p2;
		point3 = p3;
	}

	@Override
	Vec3 getOrig() {
		ModelPoint p1 = (ModelPoint)(getModel().getElementById(point1));
		return p1.evaluate();
	}

	@Override
	Vec3 getBaseVec1() {
		ModelPoint p1 = (ModelPoint)(getModel().getElementById(point1));
		ModelPoint p2 = (ModelPoint)(getModel().getElementById(point2));
		return Vec3.subtract(p2.evaluate(), p1.evaluate());		
	}

	@Override
	Vec3 getBaseVec2() {
		ModelPoint p1 = (ModelPoint)(getModel().getElementById(point1));
		ModelPoint p3 = (ModelPoint)(getModel().getElementById(point3));
		return Vec3.subtract(p3.evaluate(), p1.evaluate());
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
			boolean b1 = this.point1.equals(s.point1) || this.point1.equals(s.point2) || this.point1.equals(s.point3);
			boolean b2 = this.point2.equals(s.point2) || this.point2.equals(s.point2) || this.point2.equals(s.point3);
			boolean b3 = this.point3.equals(s.point3) || this.point3.equals(s.point2) || this.point3.equals(s.point3);
			return b1 && b2 && b3;
		}
		else return false;
	}
	
	@Override
	public List<UUID> getExtraPoints() {
		List<UUID> arr = new ArrayList<UUID>();
		arr.add(point1);
		arr.add(point2);
		arr.add(point3);
		return arr;
	}

}

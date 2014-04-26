package hu.kovand.sketch3d.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.graphics.Model3D;


public class ModelSurfaceByTwoPointsAndSurface extends ModelSurfaceWithOrigAndTwoBase {
	
	//orig: point1 
	//base1: point2-point1
	//base2: parent.base1 cross parent.base1
	
	UUID point1;
	UUID point2;
	UUID surface;

	public ModelSurfaceByTwoPointsAndSurface(Model3D m,UUID p1,UUID p2,UUID s) {
		super(m);
		point1 = p1;
		point2 = p2;
		surface = s;
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
		ModelPoint p2 = (ModelPoint)(getModel().getElementById(point2));
		ModelSurfaceWithOrigAndTwoBase s = (ModelSurfaceWithOrigAndTwoBase)(getModel().getElementById(surface));
		return Vec3.crossProduct(Vec3.subtract(p2.evaluate(), p1.evaluate()), s.getBaseVec1());
	}
	@Override
	public int getSubType() {
		return SUBTYPE_SURFACE_SURFACE_BY_TWO_POINTS_AND_SURFACE;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ModelSurfaceByTwoPointsAndSurface)
		{
			ModelSurfaceByTwoPointsAndSurface s = (ModelSurfaceByTwoPointsAndSurface)o;
			return (this.surface == s.surface && (( this.point1 == s.point1 && this.point2 == s.point2 ) || ( this.point1 == s.point2 && this.point2 == s.point1 )));
		}
		else return false;
		
	}
	
	@Override
	public List<UUID> getExtraPoints() {
		List<UUID> arr = new ArrayList<UUID>();
		arr.add(point1);
		arr.add(point2);
		return arr;
	}
	
	

}

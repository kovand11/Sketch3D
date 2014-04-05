package hu.kovand.sketch3d.model;

import hu.kovand.sketch3d.geometry.Point3D;

public class ModelSurfaceByPoint2Surface extends ModelSurface {
	
	//orig: point1 
	//base1: point2-point1
	//base2: parent.base1 cross pare
	
	ModelPoint point1;
	ModelPoint point2;
	ModelSurface surface;

	public ModelSurfaceByPoint2Surface(ModelPoint p1,ModelPoint p2,ModelSurface s) {
		point1 = p1;
		point2 = p2;
		surface = s;		
	}

	@Override
	public Point3D evaluate(ModelSurfaceAddress addr) {
		
		return null;	
		
	}

}

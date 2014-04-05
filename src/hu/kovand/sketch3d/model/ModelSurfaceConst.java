package hu.kovand.sketch3d.model;

import hu.kovand.sketch3d.geometry.Point3D;

public class ModelSurfaceConst extends ModelSurface {
	
	Point3D orig;
	Point3D v1;
	Point3D v2;

	public ModelSurfaceConst() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Point3D evaluate(ModelSurfaceAddress addr) {
		Point3D offs = Point3D.add(Point3D.multiply(v1, addr.getU()), Point3D.multiply(v2, addr.getW()));		
		return Point3D.add(orig, offs);
	}

}

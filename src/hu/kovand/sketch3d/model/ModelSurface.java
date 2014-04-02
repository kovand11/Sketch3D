package hu.kovand.sketch3d.model;

import hu.kovand.sketch3d.geometry.Point3D;

public abstract class ModelSurface {

	public ModelSurface() {
		// TODO Auto-generated constructor stub
	}
	
	public abstract Point3D evaluate(float u,float w);
	
	

}

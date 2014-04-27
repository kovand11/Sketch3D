package hu.kovand.sketch3d.model;

import hu.kovand.sketch3d.graphics.Model3D;

import java.io.Serializable;
import java.util.UUID;

import android.R.string;

abstract public class ModelElement {
	
	
	public static final int TYPE_SURFACE = 0;
	public static final int TYPE_CURVE = 1;
	public static final int TYPE_POINT = 2;
	public static final int SUBTYPE_CURVE_COMMON = 3;
	public static final int SUBTYPE_POINT_COMMON = 4;
	public static final int SUBTYPE_SURFACE_CONST = 5;
	public static final int SUBTYPE_SURFACE_SURFACE_BY_TWO_POINTS_AND_SURFACE = 6;
	public static final int SUBTYPE_SURFACE_SURFACE_BY_THREE_POINTS= 7;
	public static final int SUBTYPE_SURFACE_SURFACE_OFFSET= 8;
	
	private UUID id;
	private Model3D model;
	
	public ModelElement(Model3D m) {
		model = m;
		id = UUID.randomUUID();		
	}
	
	public ModelElement(Model3D m,UUID id){
		model = m;
		this.id = id;
	}
	
	public UUID getId(){
		return id;
	}
	
	public Model3D getModel()
	{
		return model;
	}
	
	
	
	
	
	public abstract int getType();
	public abstract int getSubType();

	

}

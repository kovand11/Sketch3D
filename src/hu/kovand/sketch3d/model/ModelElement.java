package hu.kovand.sketch3d.model;

import java.util.UUID;

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
	
	UUID id;
	
	public ModelElement() {
		id = UUID.randomUUID();
	}
	
	public UUID getId(){
		return id;
	}
	
	public abstract int getType();
	public abstract int getSubType();

	

}

package hu.kovand.sketch3d.model;

import java.util.UUID;

abstract public class ModelElement {
	
	public static final int TYPE_SURFACE = 0;
	public static final int TYPE_CURVE = 1;
	public static final int TYPE_POINT = 3;
	
	UUID id;
	
	public ModelElement() {
		id = UUID.randomUUID();
	}
	
	public UUID getId(){
		return id;
	}
	
	public abstract int getType();

	

}

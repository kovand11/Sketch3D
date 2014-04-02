package hu.kovand.sketch3d.utility;

public class IntersectionAddress {
	public int p1;
	public int p2;
	public float t1;
	public float t2;
	
	boolean isValid;

	public IntersectionAddress(int p1,float t1,int p2,float t2) {
		this.p1 = p1;
		this.p2 = p2;
		this.t1 = t1;
		this.t2 = t2;
		isValid = true;
	}
	
	public IntersectionAddress() {
		isValid = false;
	}
	
	

}

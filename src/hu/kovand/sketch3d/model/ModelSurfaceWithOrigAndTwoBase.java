package hu.kovand.sketch3d.model;

import android.opengl.Matrix;
import android.util.Log;
import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.geometry.Vec4;
import hu.kovand.sketch3d.graphics.GLRenderer;
import hu.kovand.sketch3d.graphics.Model3D;



abstract public class ModelSurfaceWithOrigAndTwoBase extends ModelSurface {
	
	
	public ModelSurfaceWithOrigAndTwoBase(Model3D m) {
		super(m);
	}
	
	
	
	abstract Vec3 getOrig();
	abstract Vec3 getBaseVec1();
	abstract Vec3 getBaseVec2();
	
	@Override
	public Vec2 findRayIntersection(Vec2 screenPoint, float[] mvp)
	{
		float[] p1 = new float[4];
		p1[0] = screenPoint.getX();
		p1[1] = screenPoint.getY();
		p1[2] = 0.5f;
		p1[3] = 1.0f;
		
		
		float[] p2 = new float[4];
		p2[0] = screenPoint.getX();
		p2[1] = screenPoint.getY();
		p2[2] = -0.5f;
		p2[3] = 1.0f;
		
		
		
		float[] invmvp = new float[16];
		Matrix.invertM(invmvp, 0, mvp, 0);
		
		
		float[] p1tr = new float[4];
		Matrix.multiplyMV(p1tr, 0, invmvp, 0, p1, 0);
		float[] p2tr = new float[4];
		Matrix.multiplyMV(p2tr, 0, invmvp, 0, p2, 0);
		float[] p1trp2trdiff = Vec4.subtractAsNorm(new Vec4(p2tr),new Vec4(p1tr)).toArray();
		
		

		Vec3 v1 = getBaseVec1();
		Vec3 v2 = getBaseVec2();
		Vec3 orig = getOrig();
		
		float[] mat = new float[16];
		mat[0] = v1.getX();
		mat[1] = v1.getY();
		mat[2] = v1.getZ();
		mat[3] = 0.0f;
		mat[4] = v2.getX();//
		mat[5] = v2.getY();
		mat[6] = v2.getZ();
		mat[7] = 0.0f;
		mat[8] = p1trp2trdiff[0]/p1trp2trdiff[3];//
		mat[9] = p1trp2trdiff[1]/p1trp2trdiff[3];
		mat[10] = p1trp2trdiff[2]/p1trp2trdiff[3];
		mat[11] = 0.0f;
		mat[12] = 0.0f;//
		mat[13] = 0.0f;
		mat[14] = 0.0f;
		mat[15] = 1.0f;		
		
		float[] inv = new float[16];
		Matrix.invertM(inv, 0, mat, 0);
		
		
		float[] vec = new float[4];
		vec[0] = p1tr[0]/p1tr[3] - orig.getX();
		vec[1] = p1tr[1]/p1tr[3] - orig.getY();
		vec[2] = p1tr[2]/p1tr[3] - orig.getZ();
		vec[3] = 0.0f;		
		
		float[] solution = new float[4];
		Matrix.multiplyMV(solution, 0, inv, 0, vec, 0);
		
		
		return new Vec2(solution[0], solution[1]);
	}
	
	@Override
	public Vec3 evaluate(Vec2 addr) {
		Vec3 v1 = getBaseVec1();
		Vec3 v2 = getBaseVec2();
		Vec3 orig = getOrig();
		Vec3 offs = Vec3.add(Vec3.multiply(v1, addr.getX()), Vec3.multiply(v2, addr.getY()));		
		return Vec3.add(orig, offs);
	}
	
	@Override
	public String toString() {
		return "Surface: orig = " + getOrig().toString() + " v1 = " + getBaseVec1().toString() + " v2 = " + getBaseVec2().toString();
	}

}

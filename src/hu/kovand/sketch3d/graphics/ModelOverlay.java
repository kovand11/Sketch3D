package hu.kovand.sketch3d.graphics;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLSurfaceView;

import hu.kovand.sketch3d.geometry.PolyLine;
import hu.kovand.sketch3d.geometry.PolyLineRenderable;
import hu.kovand.sketch3d.geometry.Vec2;
import hu.kovand.sketch3d.geometry.Vec3;
import hu.kovand.sketch3d.geometry.Vec3Renderable;
import hu.kovand.sketch3d.geometry.Vec4;
import hu.kovand.sketch3d.model.ModelCurve;
import hu.kovand.sketch3d.model.ModelPoint;
import hu.kovand.sketch3d.model.ModelSurfaceConst;

public class ModelOverlay {
	
	private ModelSurfaceConst surface;	

	public ModelOverlay() {
		surface = new ModelSurfaceConst(new Vec3(0.0f,0.0f,0.0f),new Vec3(1.0f,0.0f,0.0f),new Vec3(0.0f,1.0f,0.0f));
	}
	
	public void addPoint(Vec2 addr)
	{
		surface.addPoint(addr);
	}
	
	/*public void addCurve(ModelCurve curve)
	{
		if (surface.getCurves().size()==0)
		{
			surface.addCurve(curve);
		}
		else
		{
			PolyLine old = surface.getCurves().get(0).evaluate();
			
			//interpret on the the surface
			curve.setParent(surface);
			PolyLine newCurve = curve.evaluate();
			
			PolyLine result = PolyLine.merge(old, newCurve);
			
			surface.getCurves().remove(0);
			
		}
	}*/
	
	
	
	
	//
	//Render
	
	public List<FloatBuffer> getPointsVertexBufferList()
	{
		
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		List<ModelPoint> modelPoints = surface.getPoints();
		for (int i=0;i<modelPoints.size();i++){
			list.add((new Vec3Renderable(modelPoints.get(i).evaluate())).getVertexBuffer());			
		}
		return list;
	}
	
	public List<FloatBuffer> getCurvesVertexBufferList()
	{
		
		List<FloatBuffer> list =new ArrayList<FloatBuffer>();
		List<ModelCurve> modelCurves = surface.getCurves();
		for (int i=0;i<modelCurves.size();i++){
			
		}
		return list;
	}
	
	
	
	public void clear()
	{
		surface = new ModelSurfaceConst(new Vec3(0.0f,0.0f,0.0f),new Vec3(1.0f,0.0f,0.0f),new Vec3(0.0f,1.0f,0.0f));				
	}
	
	public ModelSurfaceConst getBaseSurface()
	{
		return surface;
		
	}
	
	
	
	
	
}

package hu.kovand.sketch3d.model;

import hu.kovand.sketch3d.geometry.HybridCurve;
import hu.kovand.sketch3d.geometry.Point3D;

import java.util.ArrayList;

public class ModelCurve {
	
	int id;
	
	//Definition as a surface relative b-spline	
	ModelSurface parent;
	ArrayList<ModelSurfaceAddress> controlPoints;
	ArrayList<Float> knots;
	
	//optional relations
	ModelPoint startPoint = null;
	ModelPoint endPoint = null;
	
	
	//Evaluated
	HybridCurve evalCurve;

	public ModelCurve(ModelSurface parent,ArrayList<ModelSurfaceAddress> controlPoints,ArrayList<Float> knots) {
		this.parent = parent;
		this.controlPoints = controlPoints;
		this.knots = knots;
	}
	
	public void refreshEval()
	{
		//TODO
	}
	
	public HybridCurve getEval()
	{
		return evalCurve;
	}
	
	public Point3D evaluate(float t)
	{
		//TODO calc u,w based on t
		return parent.evaluate(0.0f, 0.0f);
	}
	
	

}

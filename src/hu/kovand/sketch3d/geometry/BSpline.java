package hu.kovand.sketch3d.geometry;

import java.util.ArrayList;

import android.util.Log;

public class BSpline {
	public static final String TAG = "BSpline";
	
	protected ArrayList<Float> knots;
	protected ArrayList<Point3D> controlPoints;
	protected boolean isValid;
	protected int p;
	protected int n;
	
	
	public BSpline()
	{
		knots = new ArrayList<Float>();
		controlPoints =new ArrayList<Point3D>();
		p = 0;
		n = 0;
		isValid = false; 

	}
	
	public void approximate(PolyLine curve,int p_param,int n_param)
	{
		p = p_param;
		n = n_param;
		
		ArrayList<Point3D> points = curve.getPoints();
		double [] px = new double[points.size()];
		double [] py = new double[points.size()];
		double [] pz = new double[points.size()];
		for (int i=0;i<points.size();i++)
		{			
			px[i] = points.get(i).getX();
			py[i] = points.get(i).getY();
			pz[i] = points.get(i).getZ();
		}
		
		double[] result = CurveLib.approximate(px, py, pz, p, n);
		
		//result must (n+p+2)*knots + (n+1)*controlPoints 		
		
		knots.clear();
		for (int i=0;i<n+p+2;i++)
		{
			knots.add((float)result[i]);
			
		}
		
		controlPoints.clear();
		for (int i=0;i<(n+1);i++)
		{
			Point3D cp = new Point3D((float)result[n+p+2+3*i],
					(float)result[n+p+2+3*i+1],
					(float)result[n+p+2+3*i+2]);
			
			controlPoints.add(cp);
			
		}	
	}
	
	public PolyLine evaluate(int points)
	{
		
		double[] knots_arr = new double[knots.size()];
		for (int i=0;i<knots.size();i++)
		{
			knots_arr[i]=(double)knots.get(i); 			
		}
		
		double[] cpx = new double[controlPoints.size()];
		double[] cpy = new double[controlPoints.size()];
		double[] cpz = new double[controlPoints.size()];
		//
		for (int i=0;i<controlPoints.size();i++)
		{
			cpx[i] = (double)controlPoints.get(i).getX();
			cpy[i] = (double)controlPoints.get(i).getY();
			cpz[i] = (double)controlPoints.get(i).getZ();
		}		
						
		double[] result = CurveLib.evaluateN(knots_arr, cpx, cpy, cpz, points);
		
		PolyLine result_line = new PolyLine(points);
		
		for (int i=0;i<points;i++)
		{
			Point3D point = new Point3D((float)result[3*i], (float)result[3*i+1], (float)result[3*i+2]);
			result_line.append(point);
		}		
		
		return result_line;		
	}
	
	
	public ArrayList<Point3D> getControlPoints(){
		return controlPoints;
	}
	
}

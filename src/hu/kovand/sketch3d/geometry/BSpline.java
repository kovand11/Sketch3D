package hu.kovand.sketch3d.geometry;

import hu.kovand.sketch3d.utility.MyMath;

import java.util.ArrayList;
import java.util.List;

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
	
	/** approximates a given polyline, with a target error
	 * 
	 * @param curve		the appriximated polyline
	 * @param p_param	degree of the	
	 * @param n_param	n+1 contoll points
	 */	
	public void approximate(PolyLine curve,int p_param,int n_param)
	{
		p = p_param;
		n = n_param;
		
		List<Point3D> points = curve.getPoints();
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
	
	/** approximates a given polyline, with a target error
	 * 
	 * @param curve		the appriximated polyline
	 * @param step		the step between the tried controll point count
	 * @param error		the upper limit of error for the bspline curve from the curve
	 */
	public void approximateAdaptive(PolyLine curve,int step,float error)
	{
		float length = MyMath.length(curve.getPoints());
		
		
						
	}
	
	/** Evaluates the b-spline with n points
	 * 
	 * @param points number of points with equal u distance
	 * @return the approximated polyline
	 */
	public PolyLine evaluateN(int points)
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
		
		PolyLine result_line = new PolyLine();
		
		for (int i=0;i<points;i++)
		{
			Point3D point = new Point3D((float)result[3*i], (float)result[3*i+1], (float)result[3*i+2]);
			result_line.add(point);
		}		
		
		return result_line;		
	}
	
	
		
	public Point3D evaluate(float u)
	{
		//TODO implement
		return null;				
	}
	
	public ProjectionResult projectPoint()
	{
		//TODO implement
		return null;		
	}
	
	
	
	
	public List<Point3D> getControlPoints(){
		return controlPoints;
	}
	
	
	
	
	public List<Float> getKnots()
	{
		return knots;		
	}
	
	
	
	
	
	public class ProjectionResult
	{
		private float u;
		private Point3D point;
		private float distance;
		
		public ProjectionResult(float u,Point3D p,float d) {
			this.u = u;
			point = p;
			distance = d;			
		}
		
		public float getU(){
			return u;
		}
		
		public Point3D getPoint(){
			return point;
		}
		
		public float getDistance(){
			return distance;			
		}	
				
	}
	
	
}

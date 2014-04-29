package hu.kovand.sketch3d.geometry;


import java.util.ArrayList;
import java.util.List;

public class BSpline {
	public static final String TAG = "BSpline";
	
	protected List<Float> knots;
	protected List<Vec3> controlPoints;
	protected int p;
	protected int n;
	
	
	/** creates an empty bspline
	 */	
	public BSpline()
	{
		knots = new ArrayList<Float>();
		controlPoints =new ArrayList<Vec3>();
		p = 0;
		n = 0;

	}
	/** construct with all data defined
	 */	
	public BSpline(List<Float> knots,List<Vec3> controlPoints,int p,int n)
	{
		this.knots = knots;
		this.controlPoints = controlPoints;
		this.p = p;
		this.n = n;		
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
		
		List<Vec3> points = curve.getPoints();
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
			Vec3 cp = new Vec3((float)result[n+p+2+3*i],
					(float)result[n+p+2+3*i+1],
					(float)result[n+p+2+3*i+2]);
			
			controlPoints.add(cp);
			
		}	
	}
	
	/** approximates a given polyline, with a target error
	 * 
	 * @param curve		the appriximated polyline
	 * @param step		the step between the tried controll point count
	 * @param error		the upper limit of error for the bspline curve from the original curve
	 */
	public void approximateAdaptive(PolyLine curve,int step,float error)
	{
		//TODO implement
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
			Vec3 point = new Vec3((float)result[3*i], (float)result[3*i+1], (float)result[3*i+2]);
			result_line.add(point);
		}		
		
		return result_line;		
	}
	
	
		
	public Vec3 evaluate(float u)
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
		
		double[] result = CurveLib.evaluate(knots_arr, cpx, cpy, cpz, u);
		
		return new Vec3((float)result[0],(float) result[1], (float)result[2]);			
	}
	
	public ProjectionResult projectPoint(Vec3 p,int resolution,float dist_tol,float cos_tol)
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
		
		double[] result = CurveLib.projectPoint(knots_arr, cpx, cpy, cpz, p.getX(), p.getY(), p.getZ() , resolution, dist_tol, cos_tol);
		
		
		//TODO
		
		return null;		
	}
	
	
	
	
	public List<Vec3> getControlPoints(){
		return controlPoints;
	}
	
	
	
	
	public List<Float> getKnots()
	{
		return knots;		
	}
	
	public int getP()
	{
		return p;
		
	}
	
	public int getN()
	{
		return n;		
	}
	
	
	
	
	
	public class ProjectionResult
	{
		private float u;
		private Vec3 point;
		private float distance;
		
		public ProjectionResult(float u,Vec3 p,float d) {
			this.u = u;
			point = p;
			distance = d;			
		}
		
		public float getU(){
			return u;
		}
		
		public Vec3 getPoint(){
			return point;
		}
		
		public float getDistance(){
			return distance;			
		}	
				
	}
	
	
}

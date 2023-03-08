package timo.jyu;
/*
	Implementation of concordance correlation coefficient
	http://en.wikipedia.org/wiki/Concordance_correlation_coefficient
	http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient
	Correlation taken from apache commongs math 3.0 git clone http://git-wip-us.apache.org/repos/asf/commons-math.git
*/
import java.util.List;
import java.util.ArrayList;
public class TimeVaryingConcordanceCorrelationCoefficient extends BaseConcordanceClass{
	public double[] coefficients;
	double[] t1;
	double[] t2;
	double tolerance;

	/**
		Calculate concordance correlation coefficient between input vectors sliding the second vector over all possible epochs in the first. First input should always be longer than the second. Both t1 and t2 need to start from 0
		@param vec1 vector one, should be longer than vector 2
		@param vec2 vector two, should be shorter than vector 1
		@param t1 time stamps for vector one
		@param t2 time stamps for vector two
		@param tolerance tolerance for the difference between time stamps
	*/
	
	public TimeVaryingConcordanceCorrelationCoefficient(double[] vec1,double[] vec2,double[] t1, double[] t2,double tolerance){
		anaDone = 0d;
		this.tolerance = tolerance;
		if (vec1.length > vec2.length){
			this.vec1 = vec1;
			this.vec2 = vec2;
			this.t1 = t1;
			this.t2 = t2;
		}else{
			//Should not get here!!			
			this.vec2 = vec1;
			this.vec1 = vec2;
			this.t2 = t1;
			this.t1 = t2;
		}
		coefficients = new double[vec1.length-vec2.length+1];
		run();
		
	}
	
	/*overload using the default tolerance*/
	public TimeVaryingConcordanceCorrelationCoefficient(double[] vec1,double[] vec2,double[] t1, double[] t2){
		this(vec1,vec2,t1,t2, Math.ulp(t1[t1.length-1])*10d); //use ulp(highest t1*10) as the default tolerance
	}
	
	@Override
	protected void run(){
		List<Thread> threads = new ArrayList<Thread>();
		List<ConcRunnable> concRunnables = new ArrayList<ConcRunnable>();
		/*Create 4 threads for the calculation*/
		int[] inits = new int[]{0, coefficients.length/4, coefficients.length*2/4, coefficients.length*3/4};
		int[] ends = new int[]{inits[1], inits[2], inits[3], coefficients.length};
		for (int i = 0;i<inits.length;++i){
			concRunnables.add(new ConcRunnable(vec1,vec2,t1,t2,inits[i],ends[i],tolerance));
			threads.add(new Thread(concRunnables.get(i)));
			threads.get(i).start();
			//System.out.println("Started thread "+i);
		}
		//join threads
		for (int i = 0;i<threads.size();++i){
			try{
				((Thread) threads.get(i)).join();
			}catch(Exception er){}
			//System.out.println("Joined thread "+i);
			for (int j = concRunnables.get(i).init;j<concRunnables.get(i).end;++j){
				coefficients[j] = concRunnables.get(i).coeff[j];
			}
			//System.out.println("Got coeffs "+i);
		}
		anaDone = 1d;
	}
	
	public static double getDone(){
		double doneA = 12;
		return doneA;
	}
	
	
	public class ConcRunnable implements Runnable{
		public int init;
		public int end;
		double[] vect1;
		double[] tempV1;
		double[] vect2;
		double[] t1;
		double[] t2;
		double tolerance;
		public double[] coeff;
		public ConcRunnable(double[] vect1,double[] vect2,double[] t1,double[] t2,int init,int end, double tolerance){
			this.vect1 = vect1;
			this.vect2 = vect2;
			this.t1 = t1;
			this.t2 = t2;
			this.init = init;
			this.end = end;
			this.tolerance = tolerance;
			coeff = new double[vect1.length-vect2.length+1];
		}
		public void run(){
			coeff = concCorrCoeff(vect1,vect2,t1,t2,coeff,init,end,tolerance);
		}
	}
	
	/**
		Calculate concordance correlation coefficient starting with varying indices of v1. v1.length > v2.length
		@param v1 vector one
		@param v2 vector two
		@param t1 vector one time stamps
		@param t2 vector two time stamps
		@param init initial index in v1 to start from
		@param end final index in v1 to calculate the coefficient for
		@param tolerance the tolerance for matching time stamps
		@return concordance correlation coefficients between v1, and v2
	*/
	public static double[] concCorrCoeff(double[] v1,double[] v2,double[] t1,double[] t2,double[] coeffs,int init, int end, double tolerance){
		int length = v2.length;
		
		//Go through v1 with the shorter v2. Use v2 timestamps for matching samples, increment v2 offset by v1 time stamps
		double offset = 0;
		//Go through the data
		for (int i = init;i<end;++i){
			ArrayList<ArrayList<Integer>> sharedIndices = getSharedIndices(t1, t2, t1[i],tolerance); //Get matching instances
			/*Get required descriptives for the shared indices*/
			double var1 = variance(v1,sharedIndices.get(0));
			double var2 = variance(v2,sharedIndices.get(1));
			double meanv1 = mean(v1,sharedIndices.get(0));
			double meanv2 = mean(v2,sharedIndices.get(1));
			double sumtop = 0;
			double top1;
			double top2;
			double lengthScale = 1d/(double) length;
			for (int j = 0;j<sharedIndices.get(0).size();++j){
				top1 = v1[sharedIndices.get(0).get(j)]-meanv1;
				top2 = v2[sharedIndices.get(1).get(j)]-meanv2;
				sumtop += top1*top2;
			}
			sumtop*=lengthScale;	//Normalise the covariance
			coeffs[i] = 2d*sumtop/(var1+var2+Math.pow(meanv1-meanv2,2d));
		}
		return coeffs;
	}
	
	
	/*
		Get common samples based on time stamps for time-varying signals. Allows using an offset for the second time series in order to enable sliding the signals with respect to each other. Have to have continuously increasing time stamps
		@param t1 time stamps of the first signals
		@param t2 time stamps of the second signal
		@param offset offset for t2
		@param tolerance the tolerance for matching time stamps
		@returns shared sample indices
	*/
	public static ArrayList<ArrayList<Integer>> getSharedIndices(double[] t1, double[] t2, double offset,double tolerance){
		ArrayList<ArrayList<Integer>> ret = new ArrayList<ArrayList<Integer>>();
		ret.add(new ArrayList<Integer>());	//t1 indices
		ret.add(new ArrayList<Integer>());	//t2 indices
		int prevT1Index = 0;
		for (int i = 0; i<t2.length; ++i){
			for (int j = prevT1Index; j < t1.length; ++j){
					prevT1Index = j;
				if (t1[j]+tolerance >= t2[i]+offset){ //t1 at or past t2
					if (t1[j] +tolerance >= t2[i]+offset && t1[j] - tolerance <= t2[i]+offset){	//match within tolerance
						ret.get(0).add(j);
						ret.get(1).add(i);
					}
					break;	//stop exploring the t1, we've gone past the sample or found the match.
				}
			}
		}
		return ret;
	}
}

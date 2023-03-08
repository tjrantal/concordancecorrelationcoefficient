
package timo.jyu;
/*
	Implementation of concordance correlation coefficient
	http://en.wikipedia.org/wiki/Concordance_correlation_coefficient
	http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient
	Correlation taken from apache commongs math 3.0 git clone http://git-wip-us.apache.org/repos/asf/commons-math.git
*/
import java.util.List;
import java.util.ArrayList;

public abstract class BaseConcordanceClass{

	public double[] coefficients;
	double[] vec1;
	double[] vec2;
	public double anaDone;
	
	/*
		Calculate mean
		@param data one dimensional array
		@return the mean of data
	*/
	public static double mean(double[] data){
		double sum = 0;
		for (int i = 0; i<data.length; ++i){
			sum+= data[i];
		}
		sum/=((double) data.length);
		return sum;
	}
	
	/*
		Calculate mean for a portion of a vector
		@param data one dimensional array
		@param init the index to start from
		@param length the length of the section to include
		@return the mean of data
	*/
	public static double mean(double[] data,int init, int length){
		double sum = 0;
		for (int i = init; i<init+length; ++i){
			sum+= data[i];
		}
		sum/=((double) length);
		return sum;
	}
	
	/*
		Calculate mean for the indices of a vector
		@param data one dimensional array
		@param indices ArrayList<Integer> of the indices to include in the consideration
		@return the mean of data
	*/
	public static double mean(double[] data,ArrayList<Integer> indices){
		double sum = 0;
		for (int i = 0; i<indices.size(); ++i){
			sum+= data[indices.get(i)];
		}
		sum/=((double) indices.size());
		return sum;
	}
	
	/*
		calculate variance
		@param data one dimensional array
		@return the variance of data
	*/
	public static double variance(double[] data){
		double variance = 0;
		double meanv = mean(data);
		double t;
		for (int i = 0; i<data.length; ++i){
			t = data[i]-meanv;
			variance+= t*t;
		}
		variance/=((double) data.length);
		return variance;
	}
	
	/*
		Calculate variance for a portion of a vector
		@param data one dimensional array
		@param init the index to start from
		@param length the length of the section to include
		@return the variance of data
	*/
	public static double variance(double[] data,int init, int length){
		double variance = 0;
		double meanv = mean(data,init,length);
		double t;
		for (int i = init; i<init+length; ++i){
			t = data[i]-meanv;
			variance+= t*t;
		}
		variance/=((double) length);
		return variance;
	}
	
	/*
		Calculate variance for the particular indices of a vector
		@param data one dimensional array
		@param indices ArrayList<Integer> of the indices to include in the consideration
		@return the variance of data
	*/
	public static double variance(double[] data, ArrayList<Integer> indices){
		double variance = 0;
		double meanv = mean(data, indices);
		double t;
		for (int i = 0; i<indices.size(); ++i){
			t = data[indices.get(i)]-meanv;
			variance+= t*t;
		}
		variance/=((double) indices.size());
		return variance;
	}
	
	
	abstract protected void run();
	
	public static double getDone(){
		double doneA = 12;
		return doneA;
	}
}
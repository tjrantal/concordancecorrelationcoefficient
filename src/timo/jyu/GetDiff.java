package timo.jyu;
import java.util.List;
import java.util.ArrayList;
public class GetDiff{
	public double[] diff;
	public GetDiff(double[] vect1,double[] vect2){
		List<Thread> threads = new ArrayList<Thread>();
		List<DiffRunnable> diffRunnables = new ArrayList<DiffRunnable>();
		diff = new double[vect1.length-vect2.length+1];
		/*Create 4 threads for the calculation*/
		int[] inits = new int[]{0, diff.length/4, diff.length*2/4, diff.length*3/4};
		int[] ends = new int[]{inits[1], inits[2], inits[3], diff.length};
		for (int i = 0;i<inits.length;++i){
			diffRunnables.add(new DiffRunnable(vect1,vect2,diff,inits[i],ends[i]));
			threads.add(new Thread(diffRunnables.get(i)));
			threads.get(i).start();
		}
		//join threads
		for (int i = 0;i<threads.size();++i){
			try{
				((Thread) threads.get(i)).join();
			}catch(Exception er){}
			for (int j = diffRunnables.get(i).init;j<diffRunnables.get(i).end;++j){
				diff[j] = diffRunnables.get(i).diff[j];
			}
		}

	}
	public class DiffRunnable implements Runnable{
		public int init;
		public int end;
		double[] vect1;
		double[] vect2;
		public double[] diff;
		public DiffRunnable(double[] vect1,double[] vect2,double[] diff,int init,int end){
			this.vect1 = vect1;
			this.vect2 = vect2;
			this.diff = diff;
			this.init = init;
			this.end = end;
		}
		public void run(){
			double temp;
			for (int i = init; i<end;++i){
				temp = 0;
				for (int j = 0;j<vect2.length;++j){
					temp+=Math.abs(vect1[i+j]-vect2[j]);
				}
				diff[i] = temp;
			}
		}
	}
	
}
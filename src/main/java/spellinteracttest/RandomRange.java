package spellinteracttest;

public class RandomRange {
	
	public static int range(int min, int max) {
		int r = 1;
		
		if(min==0 && max==0) return r;
		
		r = (int)(Math.random()*(max-min+1) +min);
		return r;
	}
}

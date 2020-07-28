package caits.utils;

public class Size {

	protected int x;
	protected int y;
	protected int z;
	protected boolean outSum;
	
	public Size() {
		clear();
		this.outSum = false;
	}
	
	public Size(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.outSum = false;
	}
	
	public Size(Size source) {
		set(source);
		this.outSum = false;
	}		

	public Size(String source) throws NumberFormatException {
		set(source);
		this.outSum = false;
	}		
	
	public void clear() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	public int getX() {
		return x; 
	}		
	
	public int getY() {
		return y; 
	}		
	
	public int getZ() {
		return z; 
	}			
	
	public int get(int param) {
		switch (param) {
		case 0: 
			return x;
		case 1: 
			return y;
		case 2: 
			return z;
		default:
			return 0;
		}		
	}

	public int get(String param) {
		if (param==null) return 0;
		else if (param.equalsIgnoreCase("x")) return x;
		else if (param.equalsIgnoreCase("y")) return y;
		else if (param.equalsIgnoreCase("z")) return z;
		else return 0;
	}
	
	public void set(Size source) {
		if (source==null) clear();
		else {
			this.x = source.x;
			this.y = source.y;
			this.z = source.z;
		}	
	}		
	
	public void set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}		
	
	public void set(int param, int val) {
		switch (param) {
		case 0: 
			this.x = val; 
			return;
		case 1: 
			this.y = val; 
			return;
		case 2: 
			this.z = val; 
			return;
		}
	}				
	
	public void set(String source) throws NumberFormatException {
		clear();
		if (source==null || source.isEmpty()) return;
		int p;
		for (int i = 0 ; i<3; i++) {
			p = source.indexOf("x");
			if (p<0) p = source.indexOf("X");
			if (p<0) {
				set(i, Integer.parseInt(source));
				break;
			}
			set(i, Integer.parseInt(source.substring(0,p).trim()));
			source = source.substring(p+1).trim();
		}	
	}		
	
	public void outSum() {
		outSum = true;
	}
	
	public void outMulti() {
		outSum = false;
	}
	
	public String toString() {
		String res = String.valueOf(x);
		if (y!=0) res = res + "x" + y;
		if (z!=0) res = res + "x" + z;
		return res;
	}

	public String toString(String unit) {
		if (unit==null) unit = new String();
		String res = String.valueOf(x) + unit;
		if (y!=0) res = res + " x " + y + unit;
		if (z!=0) res = res + " x " + z + unit;
		return res;
	}		
	
	public long sum() {
		return x + y + z;
	}

	public long multi() {
		return x * y * z;
	}
	
	public boolean isEmpty() {
		return x == 0 && y == 0 && z == 0; 
	}	
	
	public int intValue() {
		if (outSum) return (int)sum();
		else return (int)multi();
	}
	
	public long longValue() {
		if (outSum) return sum();
		else return multi();		
	}
	
	public double doubleValue() {
		if (outSum) return sum();
		else return multi();		
	}
	
	public boolean checkMinMax(int min, int max) {
		int val;
		for (int i = 0 ; i<3; i++) {
			val = get(i);
			if (val>=min && ((max<=min || val<=max))) continue;
			return false;
		}
		return true;
	}
	
	public boolean checkMin(int min) {
		int val;
		for (int i = 0 ; i<3; i++) {
			val = get(i);
			if (val>=min) continue;
			return false;
		}
		return true;
	}		

	public boolean checkMax(int max) {
		int val;
		for (int i = 0 ; i<3; i++) {
			val = get(i);
			if (val<=max) continue;
			return false;
		}
		return true;
	}	

}

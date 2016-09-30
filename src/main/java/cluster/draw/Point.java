package cluster.draw;

public final class Point {
	public final int x;
	public final int y;
	
	public Point(int x,int y){
		this.x=x;
		this.y=y;
	}
	public double distance(Point point) {
		int xD=Math.abs(this.x-point.x);
		int yD=Math.abs(this.y-point.y);
		return Math.sqrt(xD*xD+yD*yD);
	}
	public Point plus(Point point){
		return new Point(x+point.x, y+point.y);
	}
	public Point minus(Point point){
		return new Point(x-point.x, y-point.y);
	}

	@Override
	public String toString(){
		return "("+x+","+y+")";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}

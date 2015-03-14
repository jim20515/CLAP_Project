class PointCoordinates1 {

	private int x, y;
	public PointCoordinates1(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	// Custom toString() Method.
	public String toString() {
		return "X=" + x + " " + "Y=" + y;
	}
}

public class ToStringDemo1 {

	public static void main(String args[]) {
		PointCoordinates1 point = new PointCoordinates1(10, 10);
		System.out.println(point);
		String s = point + " testing";
		System.out.println(s);
	}
}
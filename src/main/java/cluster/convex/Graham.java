package cluster.convex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import cluster.draw.Point;

public final class Graham {

	private Graham(){
		throw new IllegalAccessError("You can not new an instance of this class");
	}

	/**
	 * 三点构成的偏转方向
	 */
	private static enum Turn {
		/**
		 * 顺时针
		 */
		CLOCKWISE,
		/**
		 * 逆时针
		 */
		COUNTER_CLOCKWISE,
		/**
		 * 共线
		 */
		COLLINEAR
	}

	/**
	 * 在<code>points</code>中的所有点是否共线
	 *
	 * @param 点集
	 * @return 
	 */
	@SuppressWarnings("unused")
	private static boolean areAllCollinear(List<Point> points) {
		if(points.size() < 2) {
			return true;
		}
		final Point a = points.get(0);
		final Point b = points.get(1);
		for(int i = 2; i < points.size(); i++) {
			Point c = points.get(i);
			if(getTurn(a, b, c) != Turn.COLLINEAR) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 获取凸包点集
	 * @param points 点集
	 * @return  凸包中的点
	 * @throws IllegalArgumentException 所有点共线或者少于三个点
	 */
	public static List<Point> getConvexHull(List<Point> points) throws IllegalArgumentException {
		List<Point> sorted = new ArrayList<Point>(sort(points));
		if(sorted.size() < 3) {
			throw new IllegalArgumentException("can only create a convex hull of 3 or more unique points");
		}
		/*
		if(areAllCollinear(sorted)) {
			throw new IllegalArgumentException("cannot create a convex hull from collinear points");
		}
		*/
		LinkedList<Point> stack = new LinkedList<Point>();
		stack.push(sorted.get(0));
		stack.push(sorted.get(1));
		for (int i = 2; i < sorted.size(); i++) {
			Point head = sorted.get(i);
			Point middle = stack.pop();
			Point tail = stack.peek();
			Turn turn = getTurn(tail, middle, head);
			switch(turn) {
			case COUNTER_CLOCKWISE:
				stack.push(middle);
				stack.push(head);
				break;
			case CLOCKWISE:
				i--;
				break;
			case COLLINEAR:
				stack.push(head);
				break;
			}
		}
		return stack;
	}
	/**
	 * 获取最低点
	 * @param points 点集
	 * @return 左下角的点
	 */
	protected static Point getLowestPoint(List<Point> points) {
		Point lowest = points.get(0);
		for(int i = 1; i < points.size(); i++) {
			Point temp = points.get(i);
			if(temp.y < lowest.y || (temp.y == lowest.y && temp.x < lowest.x)) {
				lowest = temp;
			}
		}
		return lowest;
	}
	/**
	 * 逆时针排序，角度相同时，距离最近的排在前面
	 * @param points 点集
	 * @return 逆时针排序好的点集
	 * @see Graham#getLowestPoint(java.util.List)
	 */
	protected static List<Point> sort(List<Point> points) {
		final Point lowest = getLowestPoint(points);
		return points.parallelStream()
				.sorted((a,b)->{
					if(a == b || a.equals(b)) {
						return 0;
					}
					double thetaA = Math.atan2(a.y - lowest.y, a.x - lowest.x);
					double thetaB = Math.atan2(b.y - lowest.y, b.x - lowest.x);
					if(thetaA == thetaB) {
						return lowest.distance(a) < lowest.distance(b)?-1:1;
					}
					return thetaA > thetaB?1:-1;
				})
				.collect(Collectors.toList());
	}
	/**
	 * 计算三个点<code>a</code>, <code>b</code> 和 <code>c</code>的向量积C：<br>
	 * <tt>C=(b.x-a.x)*(c.y-a.y)-(b.y-a.y)*(c.x-a.x)</tt>。<br>
	 * <tt>C&lt;0</tt>:顺时针<br>
	 * <tt>C&gt;0</tt>:逆时针<br>
	 * <tt>否则</tt>:共线<br>
	 *
	 * @param a 起始点
	 * @param b 第二个点
	 * @param c 结尾点
	 * @return 方向
	 */
	protected static Turn getTurn(Point a, Point b, Point c) {
		long crossProduct=(b.x-a.x)*(c.y-a.y)-(b.y-a.y)*(c.x-a.x);
		return crossProduct>0?Turn.COUNTER_CLOCKWISE:crossProduct<0?Turn.CLOCKWISE:Turn.COLLINEAR;
	}
}

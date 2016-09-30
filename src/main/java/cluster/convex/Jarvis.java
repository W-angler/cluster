package cluster.convex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cluster.draw.Point;

public final class Jarvis {

	private Jarvis(){
		throw new IllegalAccessError("You can not new an instance of this class");
	}
	/**
	 * jarvis获取凸包点集
	 * @param points 点集
	 * @return
	 */
	public static List<Point> getConvexHull(List<Point> points){
		int n = points.size();
		if (n < 3){
			return points;
		}
		int[] indexs = new int[n];
		Arrays.fill(indexs, -1);
		int leftMost = 0;
		for (int i = 1; i < n; i++){
			if (points.get(i).x < points.get(leftMost).x){
				leftMost = i;
			}
		}
		int p = leftMost, q;
		do{
			q = (p + 1) % n;
			for (int i = 0; i < n; i++){
				if (CCW(points.get(p), points.get(i), points.get(q))){
					q = i;
				}
			}
			indexs[p] = q;  
			p = q;
		} while (p != leftMost);
		return display(points, indexs);
	}
	/**
	 * 提取凸包点集
	 * @param points 点集
	 * @param indexs 凸包点集索引
	 * @return
	 */
	private static List<Point> display(List<Point> points, int[] indexs){
		List<Point> hull=new ArrayList<>();
		for (int i = 0; i < indexs.length; i++){
			if (indexs[i] != -1){
				hull.add(points.get(i));
			}
		}
		return sort(hull);
	}
	/**
	 * 计算偏移方向，左或直true，右false
	 * @param p1
	 * @param p2
	 * @param p3
	 * @return
	 */
	private static boolean CCW(Point p1, Point p2, Point p3){
		return (p2.y - p1.y) * (p3.x - p2.x) - (p2.x - p1.x) * (p3.y - p2.y)<0;
	}
	/**
	 * 获取最低点
	 * @param points
	 * @return
	 */
	private static Point getLowestPoint(List<Point> points) {
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
	 * 逆时针排序凸包点集
	 * @param points
	 * @return
	 */
	private static List<Point> sort(List<Point> points) {
		final Point lowest = getLowestPoint(points);
		return points.parallelStream()
				.sorted((a,b)->{
					if(a == b || a.equals(b)) {
						return 0;
					}
					double thetaA = Math.atan2((long)a.y - lowest.y, (long)a.x - lowest.x);
					double thetaB = Math.atan2((long)b.y - lowest.y, (long)b.x - lowest.x);
					if(thetaA == thetaB) {
						return lowest.distance(a) < lowest.distance(b)?-1:1;
					}
					return thetaA > thetaB?1:-1;
				})
				.collect(Collectors.toList());
	}
}

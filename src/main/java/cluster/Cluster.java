package cluster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import cluster.draw.Point;

public final class Cluster {
	private static final Random RANDOM=new Random();

	private Cluster(){
		throw new IllegalAccessError("You can not new an instance of this class");
	}
	/*------------------------------------------------------------------*/
	
	/**
	 * 最近邻规则
	 * @param points
	 * @return
	 */
	public static List<List<Point>> nearby(List<Point> points,int K){
		List<List<Point>> clusters=new ArrayList<>();
		List<Point> centers=new ArrayList<>();
		while(!points.isEmpty()){
			Point center=points.get(RANDOM.nextInt(points.size()));
			List<Point> list=points.parallelStream().filter(point->center.distance(point)<K).collect(Collectors.toList());
			points.removeAll(list);
			if(centers.size()!=0){
				for(Point p:list){
					Point max=centers.parallelStream().parallel().reduce((p1,p2)->p.distance(p1)<p.distance(p2)?p1:p2).get();
					if(p.distance(max)<p.distance(center)){
						clusters.get(centers.indexOf(max)).add(p);
						list.remove(p);
					}
				}
			}
			centers.add(center);
			clusters.add(list);
		}
		return clusters;
	}

	/*------------------------------------------------------------------*/
	
	/**
	 * 最大最小距离
	 * @param points
	 * @return
	 */
	public static List<List<Point>> biggestLeastDistance(List<Point> points,double k){
		List<List<Point>> clusters=new ArrayList<>();
		List<Point> centers=new ArrayList<>();
		Point first=points.remove(RANDOM.nextInt(points.size()));
		Point second=points.parallelStream()
				.reduce((p1,p2)->first.distance(p1)>first.distance(p2)?p1:p2).get();
		centers.add(first);
		centers.add(second);
		points.remove(second);
		double scale=first.distance(second)*k;
		double distance=0;
		do{
			Point point=biggestLeastDistance(points, centers);
			distance=minDistance(point, centers);
			if(distance>scale){
				points.remove(point);
				centers.add(point);
			}
		}while(distance>scale&&points.size()!=0);
		for(Point center:centers){
			List<Point> cluster=new ArrayList<>();
			cluster.add(center);
			clusters.add(cluster);
		}
		for(Point point:points){
			clusters.get(getIndex(point, centers)).add(point);
		}
		return clusters;
	}
	/**
	 * 获取最大最小距离的点
	 * @param points
	 * @param centers
	 * @return
	 */
	private static Point biggestLeastDistance(List<Point> points,List<Point> centers){
		return points.parallelStream()
			.reduce((p1,p2)->minDistance(p1,centers)>minDistance(p2,centers)?p1:p2)
			.get();
	}
	/**
	 * 获取最小距离
	 * @param p
	 * @param centers
	 * @return
	 */
	private static double minDistance(Point p,List<Point> centers){
		return centers.parallelStream()
				.mapToDouble(center->p.distance(center))
				.min()
				.getAsDouble();
	}
	/**
	 * 获取最近的聚类中心的索引
	 * @param p
	 * @param centers
	 * @return
	 */
	private static int getIndex(Point p,List<Point> centers){
		return centers.indexOf(centers.parallelStream()
				.reduce((p1,p2)->p.distance(p1)>p.distance(p2)?p2:p1)
				.get());
	}

	/*------------------------------------------------------------------*/
	
	/**
	 * K-means算法
	 * @param points
	 * @return
	 */
	public static List<List<Point>> kmeans(List<Point> points,int K){
		List<List<Point>> clusters=new ArrayList<>();
		Point[] centers=new Point[K];
		Set<Integer> indexs=new HashSet<>();
		while(indexs.size()!=K){
			indexs.add(RANDOM.nextInt(points.size()));
		}
		int i=0;
		for(int index:indexs){
			List<Point> temp=new ArrayList<>();
			Point center=points.get(index);
			centers[i++]=center;
			temp.add(center);
			clusters.add(temp);
		}
		for(Point center:centers){
			points.remove(center);
		}
		boolean changed=true;
		while(!points.isEmpty()){
			Point p=points.get(RANDOM.nextInt(points.size()));
			int index=getCenterIndex(p, centers);
			clusters.get(index).add(p);
			centers[index]=getCenter(clusters.get(index));
			points.remove(p);
		}
		while(changed){
			changed=false;
			Point adjusted=null;
			int index=0;
			l:
			for(int k=0;k<clusters.size();k++){
				List<Point> cluster=clusters.get(k);
				for(int j=0;j<cluster.size();j++){
					Point p=cluster.get(j);
					index=getCenterIndex(p, centers);
					if(index!=k){
						changed=true;
						cluster.remove(p);
						adjusted=p;
						break l;
					}
				}
			}
			if(changed){
				clusters.get(index).add(adjusted);
				centers[index]=getCenter(clusters.get(index));
			}
		}
		return clusters;
	}
	/**
	 * 获取聚类中心
	 * @param cluster
	 * @return
	 */
	private static Point getCenter(List<Point> cluster){
		int x=(int) Math.ceil(cluster.stream().mapToInt(point->point.x).average().orElse(0));
		int y=(int) Math.ceil(cluster.stream().mapToInt(point->point.y).average().orElse(0));
		return new Point(x, y);
	}
	/**
	 * 获取聚类中心的索引
	 * @param point
	 * @param centers
	 * @return
	 */
	private static int getCenterIndex(Point point,Point[] centers){
		int index=0;
		double distance=Double.MAX_VALUE;
		for(int i=0;i<centers.length;i++){
			double dis=point.distance(centers[i]);
			if(distance>dis){
				index=i;
				distance=dis;
			}
		}
		return index;
	}

	/*------------------------------------------------------------------*/
	
	/**
	 * 二分K-means算法
	 * @param points
	 * @return
	 */
	public static List<List<Point>> bikmeans(List<Point> points,int K){
		List<List<Point>> clusters=kmeans(points, 2);
		while(clusters.size()<K){
			double least=Double.MIN_VALUE;
			int index=0;
			for(int i=0;i<clusters.size();i++){
				List<Point> cluster=clusters.get(i).stream().collect(Collectors.toList());
				double before=singleSSE(cluster);
				double after=SSE(kmeans(cluster, 2));
				if(Math.abs(before-after)>least){
					least=before-after;
					index=i;
				}
			}
			List<Point> cluster=clusters.remove(index);
			for(List<Point> sub:kmeans(cluster,2)){
				clusters.add(sub);
			}
		}
		return clusters;
	}

	private static double singleSSE(List<Point> cluster){
		Point center=getCenter(cluster);
		return cluster.parallelStream().mapToDouble(p->Math.pow(p.distance(center), 2)).sum();
	}
	private static double SSE(List<List<Point>> clusters){
		return clusters.parallelStream().mapToDouble(Cluster::singleSSE).sum();
	}
	/*------------------------------------------------------------------*/
	
	/**
	 * ISODATA算法
	 * @param points
	 * @return
	 */
	public static List<List<Point>> isodata(List<Point> points,int K){
		List<List<Point>> cluster=new ArrayList<>();
		System.out.println(cluster);
		return cluster;
	}
}

package cluster.draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JPanel;

import cluster.convex.Graham;

public class DrawPanel extends JPanel{
	private static final long serialVersionUID = 1L;

	private List<Point> points;
	public DrawPanel(List<Point> points){
		this.points=points;
		this.setBackground(Color.WHITE);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				paintPoint(new Point(e.getX(), e.getY()));
			}
		});
	}
	/**
	 * 画一个16个像素的点点
	 * @param point
	 */
	private void paintPoint(Point point){
		Graphics g=getGraphics();
		g.setColor(Color.RED);
		g.fillRect(point.x-2,point.y-2,4,4);
		points.add(point);
	}

	/**
	 * 圈出聚类
	 * @param cluster
	 */
	public void drawCluster(List<List<Point>> clusters,Color color){
		System.out.println(clusters);
		clusters.stream().forEach(e->paintCluster(e,color));
	}
	/**
	 * 圈出聚类
	 * @param cluster
	 * @param color
	 */
	private void paintCluster(List<Point> cluster,Color color){
		Graphics g=getGraphics();
		g.setColor(color);
		Point center=getCenter(cluster);
		if(cluster.size()==1){
			g.drawOval(center.x-4, center.y-4, 8,8);
		}
		else if(cluster.size()==2){
			int radius=getRadius(cluster);
			g.drawOval(center.x-radius, center.y-radius, radius*2, radius*2);
		}
		else{
			List<Point> hull=Graham.getConvexHull(cluster);
			//List<Point> hull=Jarvis.getConvexHull(cluster);
			//首尾相连
			for(int i=0;i<hull.size();i++){
				Point p1=hull.get(i);
				Point p2=i==hull.size()-1?hull.get(0):hull.get(i+1);
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
			}
		}
	}
	/**
	 * 获取聚类半径
	 * @param cluster
	 * @return
	 */
	private int getRadius(List<Point> cluster){
		Point center=getCenter(cluster);
		return (int)Math.ceil(cluster.stream().mapToDouble(center::distance).max().orElse(10));
	}
	/**
	 * 获取聚类中心
	 * @param cluster
	 * @return
	 */
	private Point getCenter(List<Point> cluster){
		int x=(int) Math.ceil(cluster.stream().mapToInt(point->point.x).average().orElse(0));
		int y=(int) Math.ceil(cluster.stream().mapToInt(point->point.y).average().orElse(0));
		return new Point(x, y);
	}
	/**
	 * 导入点数据
	 * @param file
	 * @throws IOException
	 */
	public void importFile(File file) throws IOException{
		points.clear();
		white();
		Files.lines(file.toPath()).map((line)->{
			String[] data=line.split("\t");
			return new Point(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
		}).parallel().forEach(this::paintPoint);
	}
	/**
	 * 导出点数据
	 * @param file
	 * @throws IOException
	 */
	public void exportFile(File file) throws IOException{
		Files.write(file.toPath(),
				points.stream().map(e->e.x+"\t"+e.y).collect(Collectors.toList()),
				StandardOpenOption.APPEND,StandardOpenOption.CREATE);
	}
	/**
	 * 清除
	 */
	public void clear(){
		Graphics g=getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		points.forEach((point)->{
			g.setColor(Color.RED);
			g.fillRect(point.x-2,point.y-2,4,4);
		});
	}
	/**
	 * 清空
	 */
	public void empty(){
		white();
		points.clear();
	}
	public List<Point> getPoints() {
		ArrayList<Point> list=new ArrayList<>();
		points.forEach(list::add);
		return list;
	}
	private void white(){
		Graphics g=getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}

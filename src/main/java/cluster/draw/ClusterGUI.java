package cluster.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import cluster.Cluster;

public class ClusterGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private List<Point> points=new ArrayList<>();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClusterGUI frame = new ClusterGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ClusterGUI() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//居中显示
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int)screensize.getWidth();
		int height = (int)screensize.getHeight();
		setBounds(width/2-360, height/2-210,720, 420);
		
		getContentPane().setLayout(null);
		setResizable(false);
		
		DrawPanel drawPanel = new DrawPanel(points);
		drawPanel.setBounds(0, 0, 581, 349);
		getContentPane().add(drawPanel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBounds(580, 0, 134, 349);
		getContentPane().add(buttonPanel);
		buttonPanel.setLayout(new GridLayout(6, 1));
		
		JButton algorithm1=new JButton("最近邻规则");
		buttonPanel.add(algorithm1);
		algorithm1.addActionListener(e->{
			String in=JOptionPane.showInputDialog(null, "请输入K值", "最近邻规则",JOptionPane.INFORMATION_MESSAGE);
			int K=(in==null||in.equals(""))?1:Integer.parseInt(in);
			List<Point> points=drawPanel.getPoints();
			if(points.size()!=0){
				drawPanel.drawCluster(Cluster.nearby(points,K),Color.BLACK);
			}
		});
		
		JButton algorithm2=new JButton("最大最小距离");
		buttonPanel.add(algorithm2);
		algorithm2.addActionListener(e->{
			String in=JOptionPane.showInputDialog(null, "请输入比例", "最大最小距离",JOptionPane.INFORMATION_MESSAGE);
			double k=(in==null||in.equals(""))?0.5:Double.parseDouble(in);
			List<Point> points=drawPanel.getPoints();
			if(points.size()!=0){
				drawPanel.drawCluster(Cluster.biggestLeastDistance(points,k),Color.BLUE);
			}
		});
		
		JButton kmeans=new JButton("K-means");
		buttonPanel.add(kmeans);
		kmeans.addActionListener(e->{
			String in=JOptionPane.showInputDialog(null, "请输入K值", "K-means",JOptionPane.INFORMATION_MESSAGE);
			int K=(in==null||in.equals(""))?1:Integer.parseInt(in);
			List<Point> points=drawPanel.getPoints();
			if(points.size()!=0){
				drawPanel.drawCluster(Cluster.kmeans(points,K),Color.GREEN);
			}
		});
		
		JButton bikmeans=new JButton("二分K-means");
		buttonPanel.add(bikmeans);
		bikmeans.addActionListener(e->{
			String in=JOptionPane.showInputDialog(null, "请输入K值", "K-means",JOptionPane.INFORMATION_MESSAGE);
			int K=(in==null||in.equals(""))?1:Integer.parseInt(in);
			List<Point> points=drawPanel.getPoints();
			if(points.size()!=0){
				drawPanel.drawCluster(Cluster.bikmeans(points,K),Color.ORANGE);
			}
		});

		JButton clear=new JButton("清除");
		buttonPanel.add(clear);
		clear.addActionListener(e->{
			drawPanel.clear();
		});
		
		JButton empty=new JButton("清空");
		buttonPanel.add(empty);
		empty.addActionListener(e->{
			drawPanel.empty();
		});

		/*
		 * 我只是一条华丽丽的分界线(*^▽^*)
		 */
		//菜单
		JMenu menu=new JMenu("文件");
		menu.setFont(new Font("宋体", Font.PLAIN, 18));

		//菜单项
		JMenuItem importFile=new JMenuItem("导入");
		importFile.setBackground(Color.WHITE);
		importFile.setForeground(new Color(0, 0, 0));
		importFile.setFont(new Font("宋体", Font.PLAIN, 18));
		JMenuItem exportFile=new JMenuItem("导出");
		exportFile.setBackground(Color.WHITE);
		exportFile.setForeground(new Color(0, 0, 0));
		exportFile.setFont(new Font("宋体", Font.PLAIN, 18));

		//添加菜单点击监听器
		importFile.addActionListener(e->{
			JFileChooser chooser=new JFileChooser(".");
			FileFilter filter = new FileNameExtensionFilter(".txt","txt");
			chooser.setFileFilter(filter);//开始过滤
			int flag=chooser.showOpenDialog(getParent());
			if(flag==JFileChooser.APPROVE_OPTION){
				File file=chooser.getSelectedFile();
				if (file.exists()){
					try {
						drawPanel.importFile(file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		exportFile.addActionListener(e->{
			JFileChooser chooser=new JFileChooser(".");
			chooser.setSelectedFile(new File("points.txt"));
			FileFilter filter = new FileNameExtensionFilter(".txt","txt");
			chooser.setFileFilter(filter);//开始过滤
			int flag=chooser.showSaveDialog(getParent());
			if(flag==JFileChooser.APPROVE_OPTION){
				File file=chooser.getSelectedFile();
				String fileName=file.getAbsolutePath();
				if(!fileName.endsWith(".txt")){
					fileName+=".txt";
				}
				if (file.exists()){
					int copy = JOptionPane.showConfirmDialog(null,"文件已存在，是否要覆盖当前文件？",
							"保存", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
					if (copy == JOptionPane.YES_OPTION){
						try {
							drawPanel.exportFile(file);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				else{
					try {
						drawPanel.exportFile(file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		menu.add(importFile);
		menu.add(exportFile);

		JMenuBar bar=new JMenuBar();
		bar.setBackground(Color.WHITE);
		bar.add(menu);
		this.setJMenuBar(bar);
	}
}

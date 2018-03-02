package ca.lakeeffect.eventreader;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Field extends JComponent implements MouseMotionListener, ActionListener{
	
	private static final long serialVersionUID = 1L;
	private String title;
	private int height;
	private int width;
	
	private JFrame window;
	
	Reader reader;
	
	BufferedImage field;
	
	Path[] paths;
	
	float scale;
	
	InfoPanel info = new InfoPanel();
	
	Point[] locations = {
			new Point(105,65),
			new Point(120,360),
			new Point(105,839),
			new Point(380,450),
			new Point(495,315),
			new Point(495,598),
			new Point(890,265),
			new Point(890,639),
			new Point(1283,315),
			new Point(1283,598),
			new Point(1398,450),
			new Point(1673,65),
			new Point(1658,544),
			new Point(1673,839)
	};
//	Point[] locations = {
//			new Point(100,100),
//			new Point(100, 200),
//	};

	BufferedImage hover = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

	
	public Field(String title, int width, int height, String file){
		setBounds(0, 0, 5000, 5000);
		this.title = title;
		this.width = width;
		this.height = height;
		init();
		window.setVisible(true);
		reader = new Reader();
		changeRobot(file);
	}
	
	public void changeRobot(String path){
		paths = reader.read(path);
	}

	private void init() {
		window = new JFrame(title); 
		window.setLayout(null);
		window.add(this);
		window.pack();
		window.setSize(new Dimension(640, 480));
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addMouseMotionListener(this);
		
		window.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
			}
			
			@Override
			public void keyReleased(KeyEvent e){
			}
		});
		
		try {
			System.out.println("Loading");
			field = ImageIO.read(Field.class.getResourceAsStream("/ca/lakeeffect/eventreader/field.PNG"));
			System.out.println("Loaded");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed");
		}
	}
	
	@Override
	protected void paintComponent(Graphics g){
		g.setColor(Color.white);
		scale = (float) (window.getWidth()/(field.getWidth()*1.00));
		//I know it's redundant, but its there for the future
		g.drawImage(field, 0, 0, (int) (field.getWidth()*scale), (int) (field.getHeight()*scale), null);
		g.setColor(Color.green);
		for(Point p : locations){
			g.fillRect((int) (p.x*scale)-5, (int) (p.y*scale)-5, 10, 10);
		}
		Graphics2D g2d = (Graphics2D) g;
		for(Path p : paths){
			if(p.startLocation == -1 || p.endLocation == -1) continue;
			g2d.setStroke(new BasicStroke((int) (p.count/2)+1));
			g2d.drawLine((int)(locations[p.startLocation].x*scale), (int)(locations[p.startLocation].y*scale), (int)( locations[p.endLocation].x*scale), (int)( locations[p.endLocation].y*scale));
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	long time = -1;
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if(time == -1) time = System.currentTimeMillis();
		if(System.currentTimeMillis()-time<25){
			return;
		}
		time = System.currentTimeMillis();
		info.clearData();
		Point mouse = new Point((int)(e.getX()/scale), (int)(e.getY()/scale));
		boolean hovering = false;
		for(Path p : paths){
//			System.out.println(Math.abs(locations[p.startLocation].distance(locations[p.endLocation]) - (locations[p.startLocation].distance(mouse)+locations[p.endLocation].distance(mouse))));
			if(p.startLocation == -1 || p.endLocation==-1) continue;
			if(Math.abs(locations[p.startLocation].distance(locations[p.endLocation]) - (locations[p.startLocation].distance(mouse)+locations[p.endLocation].distance(mouse)))<15){
				System.out.println(p.startLocation+"=>"+p.endLocation);
				hovering = true;
				info.addData(new String[] {"Count","Avg. Time","Avg/Match"}, new double[] {p.count, p.averageTime, p.count/1}, new String[] {"","s",""}, p.startLocation+"=>"+p.endLocation);
			}
		}
		for(int i = 0; i < locations.length; i++){
			Point l = new Point((int)(locations[i].x*scale),(int) (locations[i].y*scale));
			if(l.distance(e.getX(), e.getY())<15){
				hovering=true;
				int scoreSuccess = 0, scoreFail = 0, pickupSuccess = 0, pickupFail = 0;
				for(Path p : paths){
					if(p.endLocation == i){
						scoreSuccess+=p.scoreSuccess;
						scoreFail+=p.scoreFail;
						pickupSuccess+=p.pickupSuccess;
						pickupFail+=p.pickupFail;
					}
				}
				int scorePercent = 0, pickupPercent = 0;
				if(scoreSuccess+scoreFail > 0) scorePercent = (scoreSuccess/(scoreSuccess+scoreFail))*100;
				if(pickupSuccess+pickupFail > 0) pickupPercent = (pickupSuccess/(pickupSuccess+pickupFail))*100;
				info.addData(new String[] {"Score Count","Score Success","Pickup Count","Pickup Success"}, new double[] {scoreSuccess+scoreFail, scorePercent, pickupSuccess+pickupFail, pickupPercent}, new String[] {"","%","","%"}, i+"");
			}
		}
		paintComponent(getGraphics());
		if(hovering){
			getGraphics().drawImage(info.render(), e.getX(), e.getY(), null);
		}
	}
}

class InfoPanel{
	
	private int width = 150, height = 100;
	private ArrayList<String> labels = new ArrayList<String>();
	private ArrayList<Double> data = new ArrayList<Double>();
	private ArrayList<String> suffix = new ArrayList<String>();
	
	
	public BufferedImage render(){
		height = labels.size()*15+5;
		BufferedImage panel = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = panel.createGraphics();
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.black);
		for(int i = 0; i < labels.size(); i++){
			String out;
			if(data.get(i)==-1) out = labels.get(i);
			else out = labels.get(i)+": "+data.get(i)+suffix.get(i);
			g.drawString(out, 15, i*15+15);
		}
		g.dispose();
		return panel;
	}
	
	public void addData(String[] labels, double[] data, String[] suffix, String head){
		this.labels.add("----"+head+"----");
		this.data.add(-1d);
		this.suffix.add("");
		for(int i = 0; i < labels.length; i++){
			this.labels.add(labels[i]);
			this.data.add(data[i]);
			this.suffix.add(suffix[i]);
		}
	}
	
	public void clearData(){
		labels.clear();
		data.clear();
		suffix.clear();
	}
	
}

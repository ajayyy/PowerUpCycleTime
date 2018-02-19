package ca.lakeeffect.eventreader;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
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
	
	public Field(String title, int width, int height, String file){
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
		window.add(this);
		window.pack();
		window.setSize(new Dimension(width, height));
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
		System.out.println(field);
		scale = (float) (window.getWidth()/(field.getWidth()*1.00));
		//I know it's redundant, but its there for the future
		g.drawImage(field, 0, 0, (int) (field.getWidth()*scale), (int) (field.getHeight()*scale), null);
		g.setColor(Color.green);
		for(Point p : locations){
			g.fillRect((int) (p.x*scale)-5, (int) (p.y*scale)-5, 10, 10);
		}
		System.out.println(paths);
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

	@Override
	public void mouseMoved(MouseEvent e) {
		Point mouse = new Point((int)(e.getX()/scale), (int)(e.getY()/scale));
		for(Path p : paths){
//			System.out.println(Math.abs(locations[p.startLocation].distance(locations[p.endLocation]) - (locations[p.startLocation].distance(mouse)+locations[p.endLocation].distance(mouse))));
			if(Math.abs(locations[p.startLocation].distance(locations[p.endLocation]) - (locations[p.startLocation].distance(mouse)+locations[p.endLocation].distance(mouse)))<15){
				System.out.println(p.startLocation+"=>"+p.endLocation);
			}
		}
	}
}
package ca.lakeeffect.eventreader;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

public class Selection extends JFrame implements MouseListener, ActionListener{

	private static final long serialVersionUID = 2L;

	private JPanel contentPane;
	
	Choice choice;
	Button selectRobot;
	DirectoryChooser dirChooser;
	private Button selectDir;
	String directory = "C:\\Users\\Ajay\\git\\Scouting-Event-Reader\\src\\ca\\lakeeffect\\eventreader\\EventData";
	private JSplitPane splitPane;
	private JLabel dirLabel;

	ArrayList<Path[]> allRobotPaths = new ArrayList<>();
	ArrayList<String> allRobotNames = new ArrayList<>();
	
	String[] locationNames = {
			"Ground",
			"Top Red Portal",
			"Red Vault",
			"Bottom Red Portal",
			"Red Pickup Location",
			"Top Red Switch",
			"Bottom Red Switch",
			"Top Scale",
			"Bottom Scale",
			"Top Blue Switch",
			"Bottom Blue Switch",
			"Blue Pickup Location",
			"Top Blue Portal",
			"Blue Vault",
			"Bottom Blue Portal"
	};
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Selection frame = new Selection();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Selection() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setSize(260, 120);
		setResizable(false);
		setTitle("Event Viewer");
		
		choice  = new Choice();
		contentPane.add(choice, BorderLayout.CENTER);
		
		selectRobot = new Button("Select");
		contentPane.add(selectRobot, BorderLayout.SOUTH);
		selectRobot.addMouseListener(this);
		
		splitPane = new JSplitPane();
		splitPane.setDividerSize(1);
		splitPane.setDividerLocation(200);//(int) (contentPane.getWidth()/4*3));
		contentPane.add(splitPane, BorderLayout.NORTH);
		
		selectDir = new Button("CD");
		splitPane.setRightComponent(selectDir);
		
		dirLabel = new JLabel(directory);
		changeDir(directory);
		splitPane.setLeftComponent(dirLabel);
		selectDir.addMouseListener(this);
		

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == selectDir){
			System.out.println("selectDir");
			DirectoryChooser frame = new DirectoryChooser(this, directory);
			frame.setVisible(true);
		}
		if(e.getSource() == selectRobot){
			
			for(int i = 0; i < choice.getItemCount(); i++) {
				Reader reader = new Reader();
				allRobotPaths.add(reader.read(directory + "\\" + choice.getItem(i) + ".csv"));
				allRobotNames.add(choice.getItem(i));
			}
			
			saveCycleTimes();
			
			saveCycleTimesSimplified();
			
		}
	}
	
	public float getAverageOfTwoLocations(Path path1, Path path2) {
		float timeSum = 0;
		float timeAmount = 0;
		
		for(float time : path1.times) {
			timeSum += time;
			timeAmount++;
		}
		for(float time : path2.times) {
			timeSum += time;
			timeAmount++;
		}
		
		return timeSum/timeAmount;
	}
	
	public void saveCycleTimesSimplified() {
		String csv = "";
		
		csv += "Robot Number,";
		
		for(int i=0;i<15;i++) {
			
			if(i == 0 || i == 1 || i == 3 || i == 4 || i == 11 || i == 12) {
				continue;
			}
			
			csv += "Anywhere => " + locationNames[i % 15] + ",";
			
			if(i == 5 || i == 7 || i == 9) {
				i++;
			}
		}
		
		for(int i=0;i<15 * 15;i++) {
			
			if(i % 15 == 0 || i % 15 == 1 || i % 15 == 3 || i % 15 == 4 || i % 15 == 11 || i % 15 == 12) {
				continue;
			}
			
			csv += locationNames[i/15] + " => " + locationNames[i % 15] + ",";

			if(i % 15 == 5 || i % 15 == 7 || i % 15 == 9) {
				i++;
			}
		}
		
		for(int s = 0; s < allRobotPaths.size(); s++) {
			float[] times = new float[15*15];
			
			for(int i=0;i<times.length;i++) {
				times[i] = 0;
			}
			
			
			for(int i=0;i<allRobotPaths.get(s).length;i++) {
				
				Path path = allRobotPaths.get(s)[i];
				
				if(path.endLocation == -1 || path.endLocation == 0 || path.endLocation == 2 || path.endLocation == 3 || path.endLocation == 10 || path.endLocation == 11) {
					continue;
				}
				
				if(path.endLocation == 4 || path.endLocation == 6 || path.endLocation == 8) {
					i++;
				}
				
//				System.out.println((path.endLocation+1) + (path.startLocation+1) * 15);
				times[(path.endLocation+1) + (path.startLocation+1) * 15] = path.averageTime;
//				System.out.println(window.getTitle() + "," + path.startLocation + "," + path.endLocation + "," + path.averageTime);
			}
		
			csv += "\n" + allRobotNames.get(s) + ",";
			
			for(int i=-1;i<14;i++) {
				
				if(i == -1 || i == 0 || i == 2 || i == 3 || i == 10 || i == 11) {
					continue;
				}
				
				csv += getAverageTimeToLocation(allRobotPaths.get(s), i) + ",";
				
				if(i == 4 || i == 6 || i == 8) {
					i++;
				}
			}
			
			for(int i=0;i<times.length;i++) {
				csv += times[i] + ",";
			}
		}
		
		File exportedCsv = new File(directory + "\\results\\CycleTimesSimplified.csv");
		
		exportedCsv.getParentFile().mkdirs();
        if (!exportedCsv.exists()) {
        	try {
				exportedCsv.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
        
        FileOutputStream f = null;
		try {
			f = new FileOutputStream(exportedCsv, true);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}

        OutputStreamWriter out = new OutputStreamWriter(f);

        try {
			out.write(csv);
			out.close();
            f.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void saveCycleTimes() {
		String csv = "";
		
		csv += "Robot Number,";
		
		for(int i=0;i<15;i++) {
			csv += "Anywhere => " + locationNames[i % 15] + ",";
		}
		
		for(int i=0;i<15 * 15;i++) {
			csv += locationNames[i/15] + " => " + locationNames[i % 15] + ",";
		}
		
		for(int s = 0; s < allRobotPaths.size(); s++) {
			float[] times = new float[15*15];
			
			for(int i=0;i<times.length;i++) {
				times[i] = 0;
			}
			
			
			for(Path path : allRobotPaths.get(s)) {
//				System.out.println((path.endLocation+1) + (path.startLocation+1) * 15);
				times[(path.endLocation+1) + (path.startLocation+1) * 15] = path.averageTime;
//				System.out.println(window.getTitle() + "," + path.startLocation + "," + path.endLocation + "," + path.averageTime);
			}
		
			csv += "\n" + allRobotNames.get(s) + ",";
			
			for(int i=-1;i<14;i++) {
				csv += getAverageTimeToLocation(allRobotPaths.get(s), i) + ",";
			}
			
			for(int i=0;i<times.length;i++) {
				csv += times[i] + ",";
			}
		}
		
		File exportedCsv = new File(directory + "\\results\\CycleTimes.csv");
		
		exportedCsv.getParentFile().mkdirs();
        if (!exportedCsv.exists()) {
        	try {
				exportedCsv.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
        
        FileOutputStream f = null;
		try {
			f = new FileOutputStream(exportedCsv, true);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}

        OutputStreamWriter out = new OutputStreamWriter(f);

        try {
			out.write(csv);
			out.close();
            f.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	//Returns average of anything to a location
	public float getAverageTimeToLocation(Path[] robotPaths, int endLocation) {
		
		float timeSum = 0;
		float timeAmount = 0;
		
		for(Path path : robotPaths) {
			if(path.endLocation == endLocation) {
				for(float time : path.times) {
					timeSum += time;
					timeAmount++;
				}
			}
		}
		
		return timeSum/timeAmount;
	}

	public void changeDir(String directory){
		this.directory = directory;
		System.out.println(directory);
		dirLabel.setText(directory);
		File dir = new File(directory);
		String[] children = dir.list();
		choice.removeAll();
		for(String c : children){
			if(c.endsWith(".csv")){
				choice.add(c.replace(".csv", ""));
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println();
	}

}

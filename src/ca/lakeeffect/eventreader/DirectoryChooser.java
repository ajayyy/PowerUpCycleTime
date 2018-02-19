package ca.lakeeffect.eventreader;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class DirectoryChooser extends JFrame implements ActionListener{

	private static final long serialVersionUID = 3L;
	private JPanel contentPane;
	JFileChooser fileChooser;
	String directory = "";
	boolean directoryChanged = false;
	Selection parent;

	/**
	 * Create the frame.
	 */
	public DirectoryChooser(Selection parent, String startDirectory) {
		this.parent = parent;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(startDirectory));
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		contentPane.add(fileChooser, BorderLayout.CENTER);
		fileChooser.addActionListener(this);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == fileChooser){
			directory = fileChooser.getSelectedFile().getAbsolutePath();
			parent.changeDir(directory);
			dispose();
		}
	}
	

}

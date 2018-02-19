package ca.lakeeffect.eventreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Reader {
	
	Scanner scanner;
	
	public Path[] read(String path){
		File file = new File(path);
		scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(scanner == null){
			System.out.println("Scanner null");
			return null;
		}
		
		//Remove header line
		scanner.nextLine();
		ArrayList<Event> events = new ArrayList<Event>();
		while(scanner.hasNextLine()){
			String[] line = scanner.nextLine().split(",");
			events.add(new Event(Integer.parseInt(line[0]), Integer.parseInt(line[1]), Float.parseFloat(line[2]), Integer.parseInt(line[3])));
			events.get(events.size()-1).print();
		}
		
		ArrayList<Path> paths = new ArrayList<Path>();
		for(int i = 1; i < events.size(); i ++){
			boolean exists = false;
			for(Path p : paths){
				if(p.startLocation == events.get(i-1).location && p.endLocation == events.get(i).location){
					p.addRun(events.get(i-1), events.get(i));
					exists = true;
				}
			}
			if(!exists){
				paths.add(new Path(events.get(i-1), events.get(i)));
			}
			
			
		}
		for(Path p : paths){
			System.out.println(p.startLocation + "->" + p.endLocation +" : "+ p.count+"\t"+p.averageTime);
		}
		return paths.toArray(new Path[paths.size()]);
	}
}

class Event{
	int location;
	int action;
	float time;
	int meta;
	
	public Event(int location, int action, float time, int meta){
		this.location = location;
		this.action = action;
		this.time = time;
		this.meta = meta;
	}
	
	//Temporary debugging function, remove when viable
	public void print(){
		System.out.println(location+","+action+","+time+","+meta);
	}
		
}
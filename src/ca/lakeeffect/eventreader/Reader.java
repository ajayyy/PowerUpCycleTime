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
		
		ArrayList<Event> events = new ArrayList<Event>();
		while(scanner.hasNextLine()){
			String fullLine = scanner.nextLine();
			String[] line = fullLine.split(",");
			
			if(line.length < 5 || fullLine.contains(":")) {
				continue;
			}
			
			events.add(new Event(Integer.parseInt(line[0]), Integer.parseInt(line[1]), Integer.parseInt(line[2]), Long.parseLong(line[3]), Integer.parseInt(line[4])));
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
				Event from = events.get(i-1);
				Event to = events.get(i);
				
				if(from.match == to.match) {
					paths.add(new Path(from, to));
				}
			}
			
			
		}
		for(Path p : paths){
			System.out.println(p.startLocation + "->" + p.endLocation +" : "+ p.count+"\t"+p.averageTime);
		}
		return paths.toArray(new Path[paths.size()]);
	}
}

class Event{
	public int match;
	int location;
	int action;
	long time;
	int meta;
	
	public Event(int match, int action, int location, long time, int meta){
		this.match = match;
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

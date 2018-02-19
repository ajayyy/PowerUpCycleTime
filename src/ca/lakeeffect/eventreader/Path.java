package ca.lakeeffect.eventreader;

import java.util.ArrayList;

public class Path {
	int startLocation;
	int endLocation;
	ArrayList<Float> times;
	float averageTime;
	int count;

	
	public Path(Event first, Event second){
		times = new ArrayList<Float>();
		startLocation = first.location;
		endLocation = second.location;
		addRun(first, second);
	}
	
	public void addRun(Event first, Event second){
		times.add(second.time-first.time);
		count++;
		float sum = 0;
		for(float f : times){
			sum+=f;
		}
		averageTime = sum/count;
	}
}

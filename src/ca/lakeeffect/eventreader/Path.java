package ca.lakeeffect.eventreader;

import java.util.ArrayList;

import javax.annotation.Generated;

public class Path {
	int startLocation;
	int endLocation;
	ArrayList<Float> times;
	float averageTime;
	int count;
	double scoreSuccessRate;
	int scoreSuccess;
	int scoreFail;
	double pickupSuccessRate;
	int pickupSuccess;
	int pickupFail;

	
	public Path(Event first, Event second){
		times = new ArrayList<Float>();
		startLocation = first.location;
		endLocation = second.location;
		addRun(first, second);
	}
	
	public void addRun(Event first, Event second){
		System.out.println((second.time - first.time) + "FDALKHASDLKJHASDLJKASD");
		times.add(((second.time - first.time) /1000f));
		count++;
		float sum = 0;
		for(float f : times){
			sum+=f;
		}
		averageTime = sum/count;
		if(second.action == 0){
			pickupSuccess ++;
		}
		if(second.action == 1){
			scoreSuccess ++;
		}
		if(second.action == 2){
			pickupFail ++;
		}
		if(second.action == 3){
			scoreFail ++;
		}
		if(scoreSuccess+scoreFail > 0) scoreSuccessRate = scoreSuccess/(scoreSuccess+scoreFail);
		else scoreSuccessRate = 0;
		if(pickupSuccess+pickupFail > 0) pickupSuccessRate = pickupSuccess/(pickupSuccess+pickupFail);
		else pickupSuccessRate = 0;
	}
}

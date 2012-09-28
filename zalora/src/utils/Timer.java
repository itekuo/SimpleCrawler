package utils;
import java.sql.Time;

/**
 * Timer application 
 * @author AmberG
 *
 */
public class Timer {
	
	public long durationOfTimeInMillis;
	
	private long startTimeInMillis;
	
	private long endTimeInMillis;
	
	
	public Timer() {
		this.durationOfTimeInMillis = 0l;
	}
	
	public void start() {
		startTimeInMillis = System.currentTimeMillis();
	}
	
	public void pause() {
		endTimeInMillis = System.currentTimeMillis();
		if (endTimeInMillis > startTimeInMillis) {
			durationOfTimeInMillis += endTimeInMillis - startTimeInMillis;
		}
	}
	
	public int getTotalDurationInSeconds() {
		return new Long(durationOfTimeInMillis / 1000).intValue();
	}
	
	
}

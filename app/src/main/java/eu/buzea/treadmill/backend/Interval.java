package eu.buzea.treadmill.backend;

/**
 * Created by Vlad on 02/01/2016.
 */
public class Interval {

    private int incline, speed;
    private int startTime,endTime;

    public Interval(int incline, int speed, int startTime, int endTime) {
        this.incline = incline;
        this.speed = speed;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getIncline() {
        return incline;
    }

    public void setIncline(int incline) {
        this.incline = incline;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}

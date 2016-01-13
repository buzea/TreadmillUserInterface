package eu.buzea.treadmill.backend;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Observable;

/**
 * Created by Vlad on 30/12/2015.
 */
public class Workout extends Observable implements Parcelable {
    private WorkoutGoal goal;
    private double speed, distance, inclination, calories;
    private int bpm;
    private long secondsElapsed;
    private Date dateFinished;

    public Workout() {
        speed = 0.5;
        goal = new WorkoutGoal(WorkoutGoal.Type.TIME, 30);
        distance = 0.0000001;
        calories = 0.0000001;
    }

    public WorkoutGoal getGoal() {
        return goal;
    }

    public void setGoal(WorkoutGoal goal) {
        this.goal = goal;
        setChanged();
        notifyObservers();
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Date dateFinished) {
        this.dateFinished = dateFinished;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        if (this.speed < 0.5) {
            this.speed = 0.5;
        }
        setChanged();
        notifyObservers();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
        setChanged();
        notifyObservers();
    }

    public double getInclination() {
        return inclination;
    }

    public void setInclination(double incline) {
        this.inclination = incline;
        if (inclination < 0) {
            inclination = 0;
        }
        setChanged();
        notifyObservers();
    }

    public int getCalories() {
        return (int) calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
        setChanged();
        notifyObservers();
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
        setChanged();
        notifyObservers();
    }

    public long getSecondsElapsed() {
        return secondsElapsed;
    }

    public void setSecondsElapsed(long secondsElapsed) {
        this.secondsElapsed = secondsElapsed;
        setChanged();
        notifyObservers();
    }

    public double getAverageSpeed() {
        return (distance * 3600) / (getSecondsElapsed());
    }

    public double getAveragePace() {
        return getSecondsElapsed() / (distance * 60);
    }

    /**
     * Calories/min
     *
     * @return
     */
    public double getAverageCalories() {
        return calories * 60 / getSecondsElapsed();
    }

    public void addOneSecondToElapsedTime() {
        secondsElapsed = secondsElapsed + 1;
        bpm = (int) (80 + speed * (inclination + 1) + 1.5) % 180;
        calories += getCaloriesPerSecond();
        distance = distance + (speed / 3600);
        setChanged();
        notifyObservers();
    }

    public long getSecondsLeft() {
        WorkoutGoal.Type type = goal.getType();
        double timeLeft;
        switch (type) {
            case TIME:
                timeLeft = (goal.getValue() * 60 - secondsElapsed);
                break;
            case DISTANCE:
                double distanceLeft = goal.getValue() - distance;
                timeLeft = (distanceLeft / speed) * 3600;
                break;
            case CALORIES:
                double caloriesLeft = goal.getValue() - calories;
                timeLeft = caloriesLeft / getCaloriesPerSecond();
                break;
            default:
                return 0;
        }
        if (timeLeft < 0)
            return 0;
        return (long) timeLeft;
    }

    public double getCaloriesPerSecond() {
        return (speed / 3600) * ((inclination + 1)) * 100;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(goal, flags);
        dest.writeDouble(speed);
        dest.writeDouble(distance);
        dest.writeDouble(inclination);
        dest.writeDouble(calories);
        dest.writeInt(bpm);
        dest.writeLong(secondsElapsed);
        if (dateFinished != null) {
            dest.writeInt(1);
            dest.writeLong(dateFinished.getTime());
        } else {
            dest.writeInt(0);
        }
    }

    public static final Parcelable.Creator<Workout> CREATOR = new Parcelable.Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel source) {
            Workout workout = new Workout();
            workout.setGoal((WorkoutGoal) source.readParcelable(WorkoutGoal.class.getClassLoader()));
            workout.setSpeed(source.readDouble());
            workout.setDistance(source.readDouble());
            workout.setInclination(source.readDouble());
            workout.setCalories(source.readDouble());
            workout.setBpm(source.readInt());
            workout.setSecondsElapsed(source.readLong());
            int hasDate = source.readInt();
            if (hasDate != 0) {
                workout.setDateFinished(new Date(source.readLong()));
            }
            return workout;
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };

    public boolean isFinished() {
        return getSecondsLeft() == 0;
    }
}

package eu.buzea.treadmill.backend;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vlad on 29/12/2015.
 */
public class WorkoutGoal implements Parcelable {


    public enum Type {
        TIME, DISTANCE, CALORIES
    }

    private Type type;
    private double value;

    public WorkoutGoal(Type type, double value) {
        this.type = type;
        this.value = value;
    }

    public WorkoutGoal() {
        this(Type.TIME, 60);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type.toString());
        dest.writeDouble(value);
    }

    public static final Parcelable.Creator<WorkoutGoal> CREATOR
            = new Parcelable.Creator<WorkoutGoal>() {


        @Override
        public WorkoutGoal createFromParcel(Parcel source) {
            Type type = Type.valueOf(source.readString());
            double val = source.readDouble();
            return new WorkoutGoal(type, val);
        }

        @Override
        public WorkoutGoal[] newArray(int size) {
            return new WorkoutGoal[size];
        }
    };
}

package eu.buzea.treadmill.backend;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vlad on 31/12/2015.
 */
public class PredefinedWorkout implements Parcelable {
    private String title;
    private long durationInSeconds;
    private int pictureId;

    public PredefinedWorkout(String title, long durationInSeconds, int pictureId) {
        this.title = title;
        this.durationInSeconds = durationInSeconds;
        this.pictureId = pictureId;
    }

    public String getTitle() {
        return title;
    }

    public long getDurationInSeconds() {
        return durationInSeconds;
    }

    public int getPictureId() {
        return pictureId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeLong(durationInSeconds);
        dest.writeInt(pictureId);
    }

    public static final Parcelable.Creator<PredefinedWorkout> CREATOR = new Creator<PredefinedWorkout>() {
        @Override
        public PredefinedWorkout createFromParcel(Parcel source) {
            String title = source.readString();
            long seconds = source.readLong();
            int picId = source.readInt();
            return new PredefinedWorkout(title, seconds, picId);
        }

        @Override
        public PredefinedWorkout[] newArray(int size) {
            return new PredefinedWorkout[size];
        }
    };
}

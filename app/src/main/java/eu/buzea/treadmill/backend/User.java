package eu.buzea.treadmill.backend;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vlad on 28/12/2015.
 */
public class User implements Parcelable {
    public enum Gender {
        MALE, FEMALE
    }

    private Gender gender = Gender.MALE;
    private String firstName, lastName, password, email;
    private int height, weight, age;
    private List<Workout> previousWorkouts;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gender.toString());
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(password);
        dest.writeString(email);
        dest.writeInt(height);
        dest.writeInt(weight);
        dest.writeInt(age);
        dest.writeTypedList(previousWorkouts);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(Parcel source) {
        gender = Gender.valueOf(source.readString());
        firstName = source.readString();
        lastName = source.readString();
        password = source.readString();
        email = source.readString();
        height = source.readInt();
        weight = source.readInt();
        age = source.readInt();
        previousWorkouts = new ArrayList<>();
        source.readTypedList(previousWorkouts, Workout.CREATOR);
    }

    public User(String firstName, int weight, int height, int age, String email, String password, String lastName) {
        this.firstName = firstName;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.email = email;
        this.password = password;
        this.lastName = lastName;
    }

    public User(String password, String email, String lastName, String firstName, int age) {
        this.password = password;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
    }

    public User(String password, String email) {
        this.password = password;
        this.email = email;
        firstName = "FirstName";
        lastName = "LastName";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public boolean addWorkout(Workout object) {
        return getPreviousWorkouts().add(object);
    }

    public List<Workout> getPreviousWorkouts() {
        if (previousWorkouts == null) previousWorkouts = new ArrayList<>();
        return previousWorkouts;
    }
}

package eu.buzea.treadmill.backend;

import java.util.ArrayList;

import eu.buzea.treadmill.R;

/**
 * Created by Vlad on 31/12/2015.
 */
public class PredefinedWorkoutList {
    private static PredefinedWorkoutList ourInstance = new PredefinedWorkoutList();

    public static PredefinedWorkoutList getInstance() {
        return ourInstance;
    }

    private ArrayList<PredefinedWorkout> workouts;

    private PredefinedWorkoutList() {
        workouts=new ArrayList<>();
        workouts.add(new PredefinedWorkout("Sprints",30*60, R.drawable.graph1));
        workouts.add(new PredefinedWorkout("Hills",30*60, R.drawable.graph2));
        workouts.add(new PredefinedWorkout("Hills2",30*60, R.drawable.graph3));
        workouts.add(new PredefinedWorkout("Insane",30*60, R.drawable.graph4));
    }

    public boolean add(PredefinedWorkout object) {
        return workouts.add(object);
    }

    public int size() {
        return workouts.size();
    }

    public PredefinedWorkout get(int index) {
        return workouts.get(index);
    }

    public PredefinedWorkout remove(int index) {
        return workouts.remove(index);
    }

    public boolean remove(Object object) {
        return workouts.remove(object);
    }

    public boolean contains(Object object) {
        return workouts.contains(object);
    }

    public boolean isEmpty() {
        return workouts.isEmpty();
    }
}

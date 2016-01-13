package eu.buzea.treadmill.backend;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Vlad on 28/12/2015.
 */
public class UserList {
    private Map<String, User> users;
    private static UserList instance;
    private static User guestUser = new User("guest", "guest", "guest", "User:", 0);

    private UserList() {
        users = new HashMap<>();
        User vlad = new User("Vlad", 85, 180, 22, "buzea.vlad@gmail.com", "admin", "Buzea");
        setWorkouts(vlad);
        users.put(vlad.getEmail(), vlad);
        users.put(guestUser.getEmail(), guestUser);
    }

    private void setWorkouts(User vlad) {
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            Workout workout = new Workout();
            workout.setDateFinished(new Date(random.nextLong()));
            double distance = random.nextInt(5) + random.nextDouble();
            workout.setDistance(distance);
            long seconds = (long) (60 * distance * (random.nextInt(12) + random.nextDouble()));
            workout.setSecondsElapsed(seconds);
            workout.setCalories(random.nextInt(500));
            vlad.addWorkout(workout);
        }
    }

    public static UserList getInstance() {
        if (instance == null) instance = new UserList();
        return instance;
    }

    public boolean login(String email, String password) {
        User u = users.get(email);
        return u != null && u.getEmail().equals(email) && u.getPassword().equals(password);
    }

    public User findByEmail(String email) {
        return users.get(email);
    }

    public void add(User object) {
        users.put(object.getEmail(), object);
    }

    public static User getGuestUser() {
        return guestUser;
    }

    public void update(User user) {
        users.put(user.getEmail(), user);
    }

    public List<String> getEmails() {
        return new ArrayList<>(users.keySet());
    }
}

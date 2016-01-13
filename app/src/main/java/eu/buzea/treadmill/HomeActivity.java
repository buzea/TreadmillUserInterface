package eu.buzea.treadmill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import eu.buzea.treadmill.backend.User;
import eu.buzea.treadmill.backend.UserList;
import eu.buzea.treadmill.backend.WorkoutGoal;

public class HomeActivity extends AppCompatActivity {
    private static final int REQUEST_GOAL = 0;
    private User user = null;
    private UserList userList = UserList.getInstance();
    private WorkoutGoal goal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent parentIntent = getIntent();
        Bundle extras = parentIntent.getExtras();
        if (extras != null) {
            String email = extras.getString(getString(R.string.email), null);
            user = userList.findByEmail(email);
        } else {
            // recall last user
            SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
            String email = preferences.getString(getString(R.string.email), null);
            user = userList.findByEmail(email);
        }
        if (user == null) user = UserList.getGuestUser();
        setUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (user == null) {
            SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
            String email = preferences.getString(getString(R.string.email), null);
            user = userList.findByEmail(email);
            if (user == null) user = UserList.getGuestUser();
        }
    }

    private void setUp() {
        TextView username = (TextView) findViewById(R.id.username_textView);
        username.setText(user.getFirstName() + " " + user.getLastName());
        TextView age = (TextView) findViewById(R.id.age_textView);
        age.setText("Age:\n" + user.getAge());
        TextView weight = (TextView) findViewById(R.id.weight_textView);
        weight.setText("Weight:\n" + user.getWeight());
        TextView height = (TextView) findViewById(R.id.height_textView);
        height.setText("Height:\n" + user.getHeight());
        Button logout = (Button) findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = UserList.getGuestUser();
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        Button workoutGoalButton = (Button) findViewById(R.id.workout_goal_button);
        workoutGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), WorkoutGoalActivity.class);
                startActivityForResult(intent, REQUEST_GOAL);
            }
        });
        Button quickStart = (Button) findViewById(R.id.start_button);
        quickStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RunningActivity.class);
                intent.putExtra(WorkoutGoalActivity.WORKOUT_GOAL, goal);
                intent.putExtra(getString(R.string.email), user.getEmail());
                startActivity(intent);
            }
        });
        Button editProfile = (Button) findViewById(R.id.edit_profile_button);
        Button viewPreviousWorkouts = (Button) findViewById(R.id.previous_workouts_button);
        if (user.equals(UserList.getGuestUser())) {
            editProfile.setEnabled(false);
            viewPreviousWorkouts.setEnabled(false);
        } else {
            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), EditProfileActivity.class);
                    intent.putExtra(getString(R.string.user), user);
                    startActivity(intent);
                }
            });
            viewPreviousWorkouts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(),CompareWorkoutsActivity.class);
                    intent.putExtra(getString(R.string.user),user);
                    startActivity(intent);
                }
            });
        }
        Button predefinedWorkoutsButton = (Button) findViewById(R.id.custom_workout_button);
        predefinedWorkoutsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PredefinedWorkoutActivity.class);
                startActivity(intent);
            }
        });
        getLastKnownLocation();
    }

    private void getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestSingleUpdate(new Criteria(), new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                FetchAddressIntentService.startService(getApplicationContext(), location, new AddressResultReceiver(new Handler()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        }, Looper.myLooper());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOAL:
                if (resultCode == RESULT_OK) {
                    goal = data.getExtras().getParcelable(WorkoutGoalActivity.WORKOUT_GOAL);
                }
                SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                String email = preferences.getString(getString(R.string.email), null);
                user = userList.findByEmail(email);
                break;
        }
    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.email), user.getEmail());
        editor.commit();
        user = null;
        super.onStop();
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string
            // or an error message sent from the intent service.
            if (resultCode == RESULT_OK) {
                String addressOutput = resultData.getString(FetchAddressIntentService.LOCATION);
                TextView location = (TextView) findViewById(R.id.location);
                location.setText(addressOutput);
            }
        }
    }
}

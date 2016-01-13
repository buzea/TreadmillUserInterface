package eu.buzea.treadmill;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import eu.buzea.treadmill.backend.User;
import eu.buzea.treadmill.backend.UserList;
import eu.buzea.treadmill.backend.Workout;
import eu.buzea.treadmill.backend.WorkoutGoal;

public class RunningActivity extends AppCompatActivity implements Observer {
    public static final int REQUEST_GOAL = 0;
    private Workout workout;
    private User user;
    private TextView chronometerView;
    private ToggleButton elapsedButton;
    private Button setGoalButton;
    private TextView calories;
    private TextView heartFrequency;
    private TextView distance;
    private TextView inclineView;
    private TextView speedView;
    private Button inclinePlus;
    private Button inclineMinus;
    private Button speedPlus;
    private Button speedMinus;
    private Button stopButton;
    private Button progressButton;
    private LinearLayout runningContainer;
    private PopupWindow popupWindow;
    private Handler mHandler;
    private Runnable countTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        Bundle extras = getIntent().getExtras();
        workout = new Workout();
        WorkoutGoal goal = extras.getParcelable(WorkoutGoalActivity.WORKOUT_GOAL);
        if (goal == null) {
            goal = new WorkoutGoal(WorkoutGoal.Type.TIME, 30);
        }
        workout.setGoal(goal);
        String mail = extras.getString(getString(R.string.email));
        user = UserList.getInstance().findByEmail(mail);
        initComponents();
        setUpButtonListeners();
        setUpChronometer();
        workout.addObserver(this);
    }

    private void setUpButtonListeners() {
        setGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), WorkoutGoalActivity.class);
                startActivityForResult(intent, REQUEST_GOAL);
            }
        });
        speedPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workout.setSpeed(workout.getSpeed() + 0.1);
            }
        });
        speedMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workout.setSpeed(workout.getSpeed() - 0.1);
            }
        });
        inclinePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workout.setInclination(workout.getInclination() + 0.5);
            }
        });
        inclineMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workout.setInclination(workout.getInclination() - 0.5);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            private int clicks = 0;

            @Override
            public void onClick(View v) {
                clicks++;
                if (clicks == 1) {
                    workout.setInclination(0);
                    runningContainer.setBackgroundResource(R.color.colorPrimaryDark);
                    new CountDownTimer(30 * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            workout.setSpeed(workout.getSpeed() - 0.5);
                        }

                        @Override
                        public void onFinish() {
                            clicks = 0;
                            runningContainer.setBackgroundResource(0);
                        }
                    }.start();
                }
                if (clicks == 2) {
                    finish();
                }
            }
        });
        progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressButton.setEnabled(false);
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.workout_progress, null);
                popupWindow = new PopupWindow(
                        popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.showAtLocation(stopButton, Gravity.TOP, 0, 25);
                TextView averageSpeed = (TextView) popupView.findViewById(R.id.average_speed);
                TextView averagePace = (TextView) popupView.findViewById(R.id.average_pace);
                TextView averageEnergy = (TextView) popupView.findViewById(R.id.average_energy);
                ProgressBar progressBar = (ProgressBar) popupView.findViewById(R.id.progressBar);
                TextView goalView = (TextView) popupView.findViewById(R.id.goal_min_textView);
                Button okButton = (Button) popupView.findViewById(R.id.ok_button);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        progressButton.setEnabled(true);
                    }
                });
                int maxMinutes = (int) ((workout.getSecondsElapsed() + workout.getSecondsLeft()) / 60);
                progressBar.setMax(maxMinutes);
                progressBar.setProgress((int) (workout.getSecondsElapsed() / 60));
                averageSpeed.setText(ConversionUtil.DECIMAL_FORMAT.format(workout.getAverageSpeed()));
                averagePace.setText(convertDoubleToMinutes(workout.getAveragePace()));
                averageEnergy.setText(ConversionUtil.DECIMAL_FORMAT.format(workout.getAverageCalories()));
                goalView.setText(maxMinutes + " minutes");
            }

            private String convertDoubleToMinutes(double number) {
                long integerPart = (long) number;
                double fractionalPart = number - integerPart;
                int seconds = (int) (60 * fractionalPart);
                return integerPart + ":" + seconds;
            }
        });
    }

    private void initComponents() {
        elapsedButton = (ToggleButton) findViewById(R.id.elapsed_remaining_toggleButton);
        chronometerView = (TextView) findViewById(R.id.chronometer);
        setGoalButton = (Button) findViewById(R.id.set_goal_button);
        inclinePlus = (Button) findViewById(R.id.incline_plus_button);
        inclineMinus = (Button) findViewById(R.id.inclinde_minus_button);
        speedPlus = (Button) findViewById(R.id.speed_plus_button);
        speedMinus = (Button) findViewById(R.id.speed_minus_button);
        progressButton = (Button) findViewById(R.id.view_progress_button);
        stopButton = (Button) findViewById(R.id.stop_button);
        calories = (TextView) findViewById(R.id.calories_textView);
        heartFrequency = (TextView) findViewById(R.id.freq_value_textView);
        distance = (TextView) findViewById(R.id.distance_value_textView);
        inclineView = (TextView) findViewById(R.id.incline_value_textView);
        speedView = (TextView) findViewById(R.id.speed_value_textView);
        runningContainer = (LinearLayout) findViewById(R.id.running_container);
    }

    private void setUpChronometer() {
        mHandler = new Handler();
        countTimer = new Runnable() {
            @Override
            public void run() {
                workout.addOneSecondToElapsedTime();
                if (workout.isFinished())
                    finishWorkout();
                else
                    mHandler.postDelayed(countTimer, 1000);
            }
        };
        mHandler.post(countTimer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOAL:
                if (resultCode == RESULT_OK) {
                    WorkoutGoal goal = data.getExtras().getParcelable(WorkoutGoalActivity.WORKOUT_GOAL);
                    workout.setGoal(goal);
                }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        calories.setText(workout.getCalories() + "");
        heartFrequency.setText(workout.getBpm() + "");
        distance.setText(ConversionUtil.formatTwoDigits(workout.getDistance()) + "");
        inclineView.setText(ConversionUtil.formatTwoDigits(workout.getInclination()) + "");
        speedView.setText(ConversionUtil.formatTwoDigits(workout.getSpeed()) + "");
        if (elapsedButton.isChecked()) {
            chronometerView.setText(ConversionUtil.toTimeString(workout.getSecondsLeft()));
        } else {
            chronometerView.setText(ConversionUtil.toTimeString(workout.getSecondsElapsed()));
        }
    }

    private void finishWorkout() {
        //mHandler.removeCallbacks(countTimer);
        workout.setDateFinished(new Date());
        user.addWorkout(workout);
        Intent intent = new Intent(getBaseContext(), WorkoutFinishedActivity.class);
        intent.putExtra(WorkoutFinishedActivity.WORKOUT, workout);
        intent.putExtra(getString(R.string.email), user.getEmail());
        startActivity(intent);
    }
}

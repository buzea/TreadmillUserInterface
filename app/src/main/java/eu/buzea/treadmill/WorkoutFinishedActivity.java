package eu.buzea.treadmill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

import eu.buzea.treadmill.backend.User;
import eu.buzea.treadmill.backend.UserList;
import eu.buzea.treadmill.backend.Workout;

public class WorkoutFinishedActivity extends Activity {

    public static final String WORKOUT = "WORKOUT";
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##0.00");
    private Workout workout;
    private User user;
    private Button okButton, shareButton;
    private TextView averageSpeed, averagePace, averageEnergy, duration, energy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_finished);
        Bundle extras = getIntent().getExtras();
        workout = extras.getParcelable(WORKOUT);
        String mail = extras.getString(getString(R.string.email));
        user = UserList.getInstance().findByEmail(mail);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        double width = dm.widthPixels * 0.95;

        getWindow().setLayout((int) width, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);

        shareButton = (Button) findViewById(R.id.share_button);
        okButton = (Button) findViewById(R.id.ok_button);
        averageSpeed = (TextView) findViewById(R.id.average_speed);
        averagePace = (TextView) findViewById(R.id.average_pace);
        averageEnergy = (TextView) findViewById(R.id.average_energy);
        duration = (TextView) findViewById(R.id.duration_textView);
        energy = (TextView) findViewById(R.id.energy_consumed_textView);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(getBaseContext(), HomeActivity.class);
                home.putExtra(getString(R.string.email), user.getEmail());
                startActivity(home);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String shareMessage = formShareMessage();
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,"Treadmill workout details");
                sendIntent.putExtra(Intent.EXTRA_TITLE,"\"Treadmill workout details\"");
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share)));
            }

            private String formShareMessage() {
                String message = "I just finished another workout:\r\n";
                message += "Time: " + toTimeString(workout.getSecondsElapsed()) + "\r\n";
                message += "Calories: " + DECIMAL_FORMAT.format(workout.getCalories())+ "\r\n";
                message += "Distance: "+ DECIMAL_FORMAT.format(workout.getDistance())+ "km\r\n";
                return message;
            }
        });


        averageSpeed.setText(DECIMAL_FORMAT.format(workout.getAverageSpeed()));
        averagePace.setText(convertDoubleToMinutes(workout.getAveragePace()));
        averageEnergy.setText(DECIMAL_FORMAT.format(workout.getAverageCalories()));
        energy.setText(DECIMAL_FORMAT.format(workout.getCalories()));
        duration.setText(toTimeString(workout.getSecondsElapsed()));


    }


    private CharSequence toTimeString(long seconds) {
        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        String value = "";
        if (hours > 0) {
            value = String.format("%1d:%02d:%02d", hours, minutes, seconds);
        } else {
            value = String.format("%02d:%02d", minutes, seconds);
        }

        return value;
    }


    private String convertDoubleToMinutes(double number) {
        long integerPart = (long) number;
        double fractionalPart = number - integerPart;
        int seconds = (int) (60 * fractionalPart);
        return integerPart + ":" + seconds;
    }
}

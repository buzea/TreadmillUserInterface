package eu.buzea.treadmill;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import eu.buzea.treadmill.backend.User;
import eu.buzea.treadmill.backend.Workout;

import static eu.buzea.treadmill.ConversionUtil.formatTwoDigits;
import static eu.buzea.treadmill.ConversionUtil.toTimeString;

public class CompareWorkoutsActivity extends AppCompatActivity {
    private User user;
    private GridView gridView;
    private PopupWindow popupWindow;
    private List<Workout> workoutList;
    private boolean selectedItems[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_workouts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = getIntent().getExtras().getParcelable(getString(R.string.user));
        workoutList = user.getPreviousWorkouts();
        selectedItems = new boolean[workoutList.size()];
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new MyGridAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compare_workouts, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.compare:
                showPopUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPopUp() {
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.compare_workouts, null);
        Button ok = (Button) popupView.findViewById(R.id.ok_button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        LinearLayout linearLayout = (LinearLayout) popupView.findViewById(R.id.workout_container);
        for (int i = 0; i < selectedItems.length; i++) {
            if (selectedItems[i]) {
                Workout workout = workoutList.get(i);
                View view = layoutInflater.inflate(R.layout.workout_stats, null);
                TextView label = (TextView) view.findViewById(R.id.time_textView);
                label.setText(toTimeString(workout.getSecondsElapsed()));
                label = (TextView) view.findViewById(R.id.calories_textView);
                label.setText(formatTwoDigits(workout.getCalories()));
                label = (TextView) view.findViewById(R.id.averageSpeed_textView);
                label.setText(formatTwoDigits(workout.getAverageSpeed()));
                linearLayout.addView(view);
            }
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = (int) (metrics.widthPixels * 0.8);
        popupWindow = new PopupWindow(
                popupView, width, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(gridView, Gravity.CENTER, 0, 25);
    }

    private class MyGridAdapter extends BaseAdapter {
        private Context baseContext;
        private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy\nHH:mm");

        public MyGridAdapter() {
            baseContext = getBaseContext();
        }

        @Override
        public int getCount() {
            return workoutList.size();
        }

        @Override
        public Object getItem(int position) {
            return workoutList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return workoutList.get(position).getDateFinished().getTime();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final TextView date = new TextView(baseContext);
            date.setGravity(Gravity.CENTER);
            date.setText(DATE_FORMAT.format(workoutList.get(position).getDateFinished()));
            date.setBackgroundResource(R.drawable.customborder);
            date.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    selectedItems[position] = !selectedItems[position];
                    if (selectedItems[position])
                        date.setBackgroundColor(R.color.colorPrimary);
                    else
                        date.setBackgroundResource(R.drawable.customborder);
                    return true;
                }
            });
            return date;
        }
    }
}

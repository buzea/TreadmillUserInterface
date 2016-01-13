package eu.buzea.treadmill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

import java.util.ArrayList;

import eu.buzea.treadmill.backend.Interval;
import eu.buzea.treadmill.backend.PredefinedWorkout;

public class AddWorkoutActivity extends AppCompatActivity {
    private Button addButton;
    private EditText name;
    private MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);
        ListView listView = (ListView) findViewById(R.id.listView);
        adapter = new MyListAdapter();
        listView.setAdapter(adapter);
        addButton = (Button) findViewById(R.id.add_button);
        name = (EditText) findViewById(R.id.workout_name);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = name.getText().toString();
                if (title.isEmpty()) {
                    name.setError("Name must not be empty");
                    name.requestFocus();
                } else {
                    PredefinedWorkout workout = new PredefinedWorkout(title, adapter.getWorkoutDuration(), R.drawable.question);
                    Intent intent = new Intent();
                    intent.putExtra(getString(R.string.workout), workout);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    public class MyListAdapter extends BaseAdapter {
        private ArrayList<Interval> intervals;

        public MyListAdapter() {
            intervals = new ArrayList<>();
            intervals.add(new Interval(0, 2, 0, 5));
        }

        public long getWorkoutDuration() {
            int index = intervals.size() - 2;
            return intervals.get(index).getEndTime() * 60;
        }

        @Override
        public int getCount() {
            return intervals.size();
        }

        @Override
        public Object getItem(int position) {
            return intervals.get(position);
        }

        @Override
        public long getItemId(int position) {
            return intervals.get(position).getStartTime();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // get a reference to the LayoutInflater service
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // check if we can reuse a previously defined cell which now is not visible anymore
            View myRow = (convertView == null) ?
                    inflater.inflate(R.layout.interval_item, parent, false) : convertView;
            final Interval item = intervals.get(position);
            int startMinute = 0;
            if (position > 0) {
                Interval previous = intervals.get(position - 1);
                startMinute = previous.getEndTime();
            }
            // get the visual elements and update them with the information from the model
            final NumberPicker incline = (NumberPicker) myRow.findViewById(R.id.incline_numberPicker);
            incline.setValue(item.getIncline());
            incline.setEnabled(true);
            incline.setMinValue(0);
            incline.setMaxValue(15);
            incline.setWrapSelectorWheel(true);
            final NumberPicker speed = (NumberPicker) myRow.findViewById(R.id.speed_numberPicker);
            speed.setValue(item.getSpeed());
            speed.setEnabled(true);
            speed.setMinValue(0);
            speed.setMaxValue(15);
            speed.setWrapSelectorWheel(true);
            final NumberPicker endTime = (NumberPicker) myRow.findViewById(R.id.interval_numberPicker);
            endTime.setValue(item.getEndTime());
            endTime.setEnabled(true);
            endTime.setMinValue(startMinute);
            endTime.setMaxValue(60);
            endTime.setWrapSelectorWheel(true);
            CheckBox checkBox = (CheckBox) myRow.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        item.setEndTime(endTime.getValue());
                        item.setSpeed(speed.getValue());
                        item.setIncline(incline.getValue());
                        Interval interval = new Interval(0, 0, item.getEndTime(), item.getEndTime() + 1);
                        intervals.add(interval);
                    } else {
                        if (intervals.size() <= position)
                            intervals.remove(position + 1);
                    }
                    notifyDataSetChanged();
                }
            });
            return myRow;
        }
    }
}

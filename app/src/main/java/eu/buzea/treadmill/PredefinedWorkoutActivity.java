package eu.buzea.treadmill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import eu.buzea.treadmill.backend.PredefinedWorkout;
import eu.buzea.treadmill.backend.PredefinedWorkoutList;

public class PredefinedWorkoutActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 0;
    private ViewPager mViewPager;
    private PredefinedWorkoutPagerAdapter adapter;
    private int selectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predefined_workout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PredefinedWorkoutPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setLongClickable(true);
        registerForContextMenu(mViewPager);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        menu.setHeaderTitle("Actions");
        selectedPosition = mViewPager.getCurrentItem();
        inflater.inflate(R.menu.context_menu_predefined_workout, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_workout:
                Toast.makeText(getBaseContext(), "Workout #" + (selectedPosition + 1) + " selected", Toast.LENGTH_LONG).show();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.remove_workout:
                adapter.remove(selectedPosition);
                adapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(0);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_predefined_workout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            PredefinedWorkout workout = data.getExtras().getParcelable(getString(R.string.workout));
            adapter.add(workout);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_add:
                Intent intent = new Intent(getBaseContext(), AddWorkoutActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
    public class PredefinedWorkoutPagerAdapter extends FragmentStatePagerAdapter {
        public PredefinedWorkoutPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private PredefinedWorkoutList list = PredefinedWorkoutList.getInstance();

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new PredefinedWorkoutFragment();
            Bundle args = new Bundle();
            args.putParcelable(PredefinedWorkoutFragment.ARG_OBJECT, list.get(i));
            fragment.setArguments(args);
            return fragment;
        }

        public void remove(int index) {
            list.remove(index);
        }

        @Override
        public int getItemPosition(Object object) {
            // refresh all fragments when data set changed
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Workout " + (position + 1);
        }

        public void add(PredefinedWorkout workout) {
            list.add(workout);
        }
    }

    public static class PredefinedWorkoutFragment extends Fragment {
        public static final String ARG_OBJECT = "workout_index";

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_predefined_workout, container, false);
            Bundle args = getArguments();
            PredefinedWorkout predefinedWorkout = args.getParcelable(ARG_OBJECT);
            TextView title = (TextView) rootView.findViewById(R.id.workout_title_textView);
            title.setText(predefinedWorkout.getTitle());
            TextView duration = (TextView) rootView.findViewById(R.id.duration_textView);
            duration.setText("Duration: " + toTimeString(predefinedWorkout.getDurationInSeconds()) + " min");
            ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
            imageView.setImageResource(predefinedWorkout.getPictureId());
            return rootView;
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
    }
}

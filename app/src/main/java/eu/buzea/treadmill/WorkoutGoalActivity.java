package eu.buzea.treadmill;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import eu.buzea.treadmill.backend.WorkoutGoal;

public class WorkoutGoalActivity extends AppCompatActivity {

    public static final String WORKOUT_GOAL = "WORKOUT_GOAL";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_goal);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            PlaceholderFragment instance = PlaceholderFragment.newInstance(position);
            return instance;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Time";
                case 1:
                    return "Distance";
                case 2:
                    return "Calories";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private EditText number;
        private int page;
        private boolean mAutoIncrement = false;
        private boolean mAutoDecrement = false;
        private Handler repeatUpdateHandler = new Handler();
        private double value;

        class RptUpdater implements Runnable {
            public void run() {
                if (mAutoIncrement) {
                    increment();
                    repeatUpdateHandler.postDelayed(new RptUpdater(), 100);
                } else if (mAutoDecrement) {
                    decrement();
                    repeatUpdateHandler.postDelayed(new RptUpdater(), 100);
                }
            }

            private void decrement() {
                if (page == 1) {
                    double value = Double.parseDouble(number.getText().toString());
                    value -= 0.01;
                    if (value < 0) {
                        value = 0;
                    }
                    number.setText(String.format("%.2f", value));
                } else {
                    int value = Integer.parseInt(number.getText().toString());
                    value--;
                    if (value < 0) {
                        value = 0;
                    }
                    number.setText(value + "");
                }
            }
            private void increment() {
                if (page == 1) {
                    double value = Double.parseDouble(number.getText().toString());
                    value += 0.01;
                    if (value < 0) {
                        value = 0;
                    }
                    number.setText(String.format("%.2f", value));
                } else {
                    int value = Integer.parseInt(number.getText().toString());
                    value++;
                    if (value < 0) {
                        value = 0;
                    }
                    number.setText(value + "");
                }
            }
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_workout_goal, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.units_textView);
            page = getArguments().getInt(ARG_SECTION_NUMBER);
            setDefaulValues(rootView, textView);
            addPlusListener(rootView);
            addMinusListener(rootView);


            Button cancel = (Button) rootView.findViewById(R.id.cancel_button);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    getActivity().setResult(RESULT_CANCELED, intent);
                    getActivity().finish();
                }
            });

            Button confirm = (Button) rootView.findViewById(R.id.confirm_button);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    WorkoutGoal.Type type = WorkoutGoal.Type.values()[page];
                    double value = Double.parseDouble(number.getText().toString());
                    WorkoutGoal goal = new WorkoutGoal(type, value);
                    intent.putExtra(WORKOUT_GOAL, goal);
                    getActivity().setResult(RESULT_OK, intent);
                    getActivity().finish();
                }
            });


            return rootView;
        }

        private void setDefaulValues(View rootView, TextView textView) {
            String unit = null;
            switch (page) {
                case 0:
                    unit = " minutes";
                    break;
                case 1:
                    unit = " km";
                    break;
                case 2:
                    unit = " calories";
                    break;
            }
            textView.setText(unit);

            number = (EditText) rootView.findViewById(R.id.number_editText);
            number.setText("0");
        }

        private void addMinusListener(View rootView) {
            Button minusButton = (Button) rootView.findViewById(R.id.minus_button);
            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (page == 1) {
                        double value = Double.parseDouble(number.getText().toString());
                        value -= 0.01;
                        if (value < 0) {
                            value = 0;
                        }
                        number.setText(String.format("%.2f", value));
                    } else {
                        int value = Integer.parseInt(number.getText().toString());
                        value--;
                        if (value < 0) {
                            value = 0;
                        }
                        number.setText(value + "");
                    }
                }
            });

            minusButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mAutoDecrement = true;
                    repeatUpdateHandler.post(new RptUpdater());
                    return false;
                }
            });

            minusButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mAutoDecrement = false;
                    }
                    return false;
                }
            });
        }

        private void addPlusListener(View rootView) {
            Button plusButton = (Button) rootView.findViewById(R.id.plus_button);
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (page == 1) {
                        double value = Double.parseDouble(number.getText().toString());
                        value += 0.01;
                        number.setText(String.format("%.2f", value));
                    } else {
                        int value = Integer.parseInt(number.getText().toString());
                        value++;
                        number.setText(value + "");
                    }

                }
            });
            plusButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mAutoIncrement = true;
                    repeatUpdateHandler.post(new RptUpdater());
                    return false;
                }
            });

            plusButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mAutoIncrement = false;
                    }
                    return false;
                }
            });
        }
    }
}

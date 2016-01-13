package eu.buzea.treadmill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Field;

import eu.buzea.treadmill.backend.User;
import eu.buzea.treadmill.backend.UserList;

public class EditProfileActivity extends AppCompatActivity {

    private User user;
    private TextView email;
    private EditText name;
    private ToggleButton gender;
    private SeekBar ageSpinner;
    private TextView ageText;
    private SeekBar weightSpinner;
    private TextView weightText;
    private UserList userList = UserList.getInstance();
    private SeekBar heightSpinner;
    private TextView heightText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //String emailString = getIntent().getExtras().getString(getString(R.string.email));
        user = getIntent().getExtras().getParcelable(getString(R.string.user));
        email = (TextView) findViewById(R.id.email_editText);
        email.setText(user.getEmail());
        name = (EditText) findViewById(R.id.name_editText);
        name.setText(user.getFirstName() + " " + user.getLastName());
        gender = (ToggleButton) findViewById(R.id.gender_toggleButton);
        if (user.getGender().equals(User.Gender.MALE)) {
            gender.setChecked(false);
        } else {
            gender.setChecked(true);
        }
        gender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    user.setGender(User.Gender.FEMALE);
                } else {
                    user.setGender(User.Gender.MALE);
                }
            }
        });

        ageSpinner = (SeekBar) findViewById(R.id.age_seekBar);
        ageSpinner.setProgress(user.getAge());

        ageText = (TextView) findViewById(R.id.age_value_textView);
        ageText.setText(user.getAge() + " years");

        weightSpinner = (SeekBar) findViewById(R.id.weight_seekBar);
        weightSpinner.setProgress(user.getWeight());

        weightText = (TextView) findViewById(R.id.weight_value_textView);
        weightText.setText(user.getWeight() + " kg");

        heightSpinner = (SeekBar) findViewById(R.id.height_seekBar);
        heightSpinner.setProgress(user.getHeight());

        heightText = (TextView) findViewById(R.id.height_value_textView);
        heightText.setText(user.getHeight() + " cm");

        try {
            ageSpinner.setOnSeekBarChangeListener(new MySeekBarListener(ageText, User.class.getDeclaredField("age"), " years"));
            weightSpinner.setOnSeekBarChangeListener(new MySeekBarListener(weightText, User.class.getDeclaredField("weight"), " kg"));
            heightSpinner.setOnSeekBarChangeListener(new MySeekBarListener(heightText, User.class.getDeclaredField("height"), " cm"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        Button cancel = (Button)findViewById(R.id.edit_cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                intent.putExtra(getString(R.string.email),user.getEmail());
                startActivity(intent);
            }
        });

        Button confirm = (Button) findViewById(R.id.edit_confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmUserData();
                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                intent.putExtra(getString(R.string.email),user.getEmail());
                startActivity(intent);
            }

            private void confirmUserData() {
                String nameString = name.getText().toString();
                int index = nameString.lastIndexOf(" ");
                String firstName = nameString.substring(0, index).trim();
                String lastName = nameString.substring(index+1).trim();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                userList.update(user);
            }
        });

    }

    public void modify(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.age_plus_button:
                user.setAge(user.getAge() + 1);
                ageSpinner.setProgress(user.getAge());
                break;
            case R.id.height_plus_button:
                user.setHeight(user.getHeight() + 1);
                heightSpinner.setProgress(user.getHeight());
                break;
            case R.id.weight_plus_button:
                user.setWeight(user.getWeight() + 1);
                weightSpinner.setProgress(user.getWeight());
                break;
            case R.id.age_minus_button:
                user.setAge(user.getAge() - 1);
                ageSpinner.setProgress(user.getAge());
                break;
            case R.id.height_minus_button:
                user.setHeight(user.getHeight() - 1);
                heightSpinner.setProgress(user.getHeight());
                break;
            case R.id.weight_minus_button:
                user.setWeight(user.getWeight() - 1);
                weightSpinner.setProgress(user.getWeight());
                break;
        }


    }

    public class MySeekBarListener implements SeekBar.OnSeekBarChangeListener {
        private TextView mTextView;
        private Field mField;
        private String suffix;

        public MySeekBarListener(TextView text, Field setter, String suffix) {
            mTextView = text;
            mField = setter;
            this.suffix = suffix;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mTextView.setText(progress + suffix);
            Toast.makeText(getBaseContext(), "" + progress, Toast.LENGTH_SHORT);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            try {
                boolean access = mField.isAccessible();
                mField.setAccessible(true);
                mField.set(user, seekBar.getProgress());
                mTextView.setText(mField.get(user) + suffix);
                mField.setAccessible(access);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }
}

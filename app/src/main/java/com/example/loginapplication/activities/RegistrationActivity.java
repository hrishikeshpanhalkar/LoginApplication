package com.example.loginapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginapplication.Model.Registration;
import com.example.loginapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class RegistrationActivity extends AppCompatActivity {
    private Button RegistrationBtn, login;
    private TextInputEditText Name, Password, Email, Age;
    RadioButton radioButton;
    RadioGroup radioGroup;
    AutoCompleteTextView City;
    ImageView logoimage;
    ProgressBar progressBar;
    ArrayList<String> spinnerData;
    TextView textView, tvNameError, tvEmailError, tvPasswordError, tvAgeError, tvCityError;

    Registration registration;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);
        Name = (TextInputEditText) findViewById(R.id.name);
        Password = (TextInputEditText) findViewById(R.id.password);
        Email = (TextInputEditText) findViewById(R.id.email);
        Age = (TextInputEditText) findViewById(R.id.age);
        City = (AutoCompleteTextView) findViewById(R.id.city);
        radioGroup = (RadioGroup) findViewById(R.id.gender_group);
        progressBar = findViewById(R.id.progressbar);
        tvNameError = findViewById(R.id.tvNameError);
        tvEmailError = findViewById(R.id.tvEmailError);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        tvAgeError = findViewById(R.id.tvAgeError);
        tvCityError = findViewById(R.id.tvCityError);
        RegistrationBtn = (Button) findViewById(R.id.btn3);
        login = (Button) findViewById(R.id.register1);
        logoimage = (ImageView) findViewById(R.id.registerlogo);
        textView = (TextView) findViewById(R.id.registerText);

        registration = new Registration();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Registration");

        spinnerData = new ArrayList<>();
        spinnerData.add("Mumbai");
        spinnerData.add("Chennai");
        spinnerData.add("Kolkata");
        spinnerData.add("Pune");
        spinnerData.add("Bengluru");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (RegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerData);
        City.setAdapter(adapter);

        RegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String NameValue = Name.getText().toString();
                final String EmailValue = Email.getText().toString();
                final String PasswordValue = Password.getText().toString();
                final String AgeValue = Age.getText().toString();
                final String CityValue = City.getText().toString();
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedId);
                if (nameCheck() && emailCheck() && passwordCheck() && ageCheck() && cityCheck()) {
                    RegistrationBtn.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(EmailValue, PasswordValue).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            registration.setName(NameValue);
                                            registration.setEmail(EmailValue);
                                            registration.setPassword(PasswordValue);
                                            registration.setGender(radioButton.getText().toString());
                                            registration.setAge(AgeValue);
                                            registration.setCity(CityValue);
                                            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(registration);
                                            Toast.makeText(RegistrationActivity.this, "User Created Successfully, Please verify your email address!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                            Pair[] pairs = new Pair[4];
                                            pairs[0] = new Pair<View, String>(logoimage, "transition_logo_image");
                                            pairs[1] = new Pair<View, String>(textView, "transition_title_text");
                                            pairs[2] = new Pair<View, String>(RegistrationBtn, "login_User");
                                            pairs[3] = new Pair<View, String>(login, "register_user");
                                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegistrationActivity.this, pairs);
                                            startActivity(intent, options.toBundle());
                                            finish();
                                        } else {
                                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            RegistrationBtn.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                RegistrationBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                Pair[] pairs = new Pair[4];
                pairs[0] = new Pair<View, String>(logoimage, "transition_logo_image");
                pairs[1] = new Pair<View, String>(textView, "transition_title_text");
                pairs[2] = new Pair<View, String>(RegistrationBtn, "login_User");
                pairs[3] = new Pair<View, String>(login, "register_user");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegistrationActivity.this, pairs);
                startActivity(intent, options.toBundle());
                finish();
            }
        });

        inputChange();
    }

    private void inputChange() {
        Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ageCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        City.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cityCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @SuppressLint("ResourceType")
    private boolean passwordCheck() {
        String password = Password.getText().toString();

        if (password.length() == 0) {
            tvPasswordError.setText("Password is Empty");
            tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else if (password.length() < 8) {
            tvPasswordError.setText("Password is too weak!");
            tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else if (!password.matches("(.*[A-Z].*)")) {
            tvPasswordError.setText("Password must one Uppercase letter!");
            tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else if (!password.matches("(.*[0-9].*)")) {
            tvPasswordError.setText("Password must one Number!");
            tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else if (!password.matches("(.*[!@#$%^&].*)")) {
            tvPasswordError.setText("Password must one Special Character!");
            tvPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvPasswordError.setText("");
            tvPasswordError.setVisibility(View.GONE);
            return true;
        }
    }

    @SuppressLint("ResourceType")
    private boolean nameCheck() {
        String name = Name.getText().toString();

        if (name.length() == 0) {
            tvNameError.setText("Name is Empty");
            tvNameError.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvNameError.setText("");
            tvNameError.setVisibility(View.GONE);
            return true;
        }
    }

    @SuppressLint("ResourceType")
    private boolean emailCheck() {
        String email = Email.getText().toString();

        if (email.length() == 0) {
            tvEmailError.setText("Email is Empty");
            tvEmailError.setVisibility(View.VISIBLE);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvEmailError.setText("Invalid Email Address!");
            tvEmailError.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvEmailError.setText("");
            tvEmailError.setVisibility(View.GONE);
            return true;
        }
    }

    @SuppressLint("ResourceType")
    private boolean ageCheck() {
        String age = Age.getText().toString();

        if (age.length() == 0) {
            tvAgeError.setText("Age is Empty");
            tvAgeError.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvAgeError.setText("");
            tvAgeError.setVisibility(View.GONE);
            return true;
        }
    }

    @SuppressLint("ResourceType")
    private boolean cityCheck() {
        String city = City.getText().toString();

        if (city.length() == 0) {
            tvCityError.setText("City is Empty");
            tvCityError.setVisibility(View.VISIBLE);
            return false;
        } else {
            tvCityError.setText("");
            tvCityError.setVisibility(View.GONE);
            return true;
        }
    }
}
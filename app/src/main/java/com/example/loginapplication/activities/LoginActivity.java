package com.example.loginapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginapplication.Model.SessionManager;
import com.example.loginapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout Email, Password;
    ImageView logoimage;
    TextView textView;
    EditText EmailEdittext, PasswordEdittext;
    private Button login, forgetPassword, register;
    CheckBox rememberMe;
    RelativeLayout relativeLayout;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        logoimage = findViewById(R.id.logoImage);
        textView = findViewById(R.id.logoname);
        relativeLayout = findViewById(R.id.login_layout2);
        login = (Button) findViewById(R.id.Login);
        register = (Button) findViewById(R.id.register);
        progressBar = (ProgressBar) findViewById(R.id.login_progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        forgetPassword = (Button) findViewById(R.id.forget_password);
        rememberMe = (CheckBox) findViewById(R.id.rememberme);
        EmailEdittext = (EditText) findViewById(R.id.emailid);
        PasswordEdittext = (EditText) findViewById(R.id.passwordid);
        firebaseAuth = FirebaseAuth.getInstance();
        SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememeberMedatails = sessionManager.getRememberMeDetailsFromSession();
            EmailEdittext.setText(rememeberMedatails.get(SessionManager.KEY_SESSIONEMAIL));
            PasswordEdittext.setText(rememeberMedatails.get(SessionManager.KEY_SESSIONPASSWORD));
        }
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                Pair[] pairs = new Pair[4];
                pairs[0] = new Pair<View, String>(logoimage, "transition_logo_image");
                pairs[1] = new Pair<View, String>(textView, "transition_title_text");
                pairs[2] = new Pair<View, String>(relativeLayout, "transition_next_btn");
                pairs[3] = new Pair<View, String>(register, "transition_login_btn");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
                    Dialog dialog = new Dialog(LoginActivity.this);
                    dialog.setContentView(R.layout.alert_dialog);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                    Button bttryagain = dialog.findViewById(R.id.bt_try_again);
                    bttryagain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recreate();
                        }
                    });
                    dialog.show();
                } else {
                    final String password = Password.getEditText().getText().toString().trim();
                    final String email = Email.getEditText().getText().toString().trim();
                    if ((Email.getEditText().getText().toString().equals(""))) {
                        Toast.makeText(LoginActivity.this, "Please enter Email", Toast.LENGTH_LONG).show();
                    } else if ((Password.getEditText().getText().toString().equals(""))) {
                        Toast.makeText(LoginActivity.this, "Please Enter Password!", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        login.setVisibility(View.INVISIBLE);
                        forgetPassword.setVisibility(View.INVISIBLE);
                        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                        Toast.makeText(LoginActivity.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                                        if (rememberMe.isChecked()) {
                                            SessionManager sessionManager = new SessionManager(LoginActivity.this, SessionManager.SESSION_REMEMBERME);
                                            sessionManager.createRememberMeSession(email, password);
                                        }
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("Email", email);
                                        intent.putExtras(bundle);
                                        Pair[] pairs = new Pair[1];
                                        pairs[0] = new Pair<View, String>(findViewById(R.id.Login), "transition_next_btn");
                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                                        startActivity(intent, options.toBundle());
                                        progressBar.setVisibility(View.INVISIBLE);
                                        login.setVisibility(View.VISIBLE);
                                        forgetPassword.setVisibility(View.VISIBLE);
                                        Password.getEditText().setText("");
                                        Email.getEditText().setText("");
                                        Email.requestFocus();
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        login.setVisibility(View.VISIBLE);
                                        forgetPassword.setVisibility(View.VISIBLE);
                                        Toast.makeText(LoginActivity.this, "Please Verify your email address!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    login.setVisibility(View.VISIBLE);
                                    forgetPassword.setVisibility(View.VISIBLE);
                                    Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Forget password Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you want to exit?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("no", null).show();
    }
}
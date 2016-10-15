package com.mobility.android.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobility.android.R;
import com.mobility.android.ui.map.MapActivity;
import com.mobility.android.util.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        FirebaseAuth.AuthStateListener {

    @BindView(R.id.login_form_email_layout) TextInputLayout emailLayout;
    @BindView(R.id.login_form_password_layout) TextInputLayout passwordLayout;

    @BindView(R.id.login_form_email) EditText email;
    @BindView(R.id.login_form_password) EditText password;

    @BindView(R.id.login_form_forgot_password) TextView forgotPassword;
    @BindView(R.id.login_form_submit) FloatingActionButton submit;
    @BindView(R.id.login_form_loading) ProgressBar loading;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);

        submit.setOnClickListener(this);

        emailLayout.setError("error");
        emailLayout.setError(null);

        passwordLayout.setError("error");
        passwordLayout.setError(null);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Timber.e("Got user: %s", user.getEmail());

            mAuth.removeAuthStateListener(this);

            startActivity(new Intent(this, MapActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_form_submit:
                login();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(this);
    }

    private void animateViews(boolean showProgress) {
        if (showProgress) {
            submit.setClickable(false);
            submit.animate()
                    .alpha(0)
                    .setDuration(250)
                    .start();

            loading.setVisibility(View.VISIBLE);
            ViewCompat.setAlpha(loading, 0);

            loading.animate()
                    .alpha(1)
                    .setDuration(250)
                    .setStartDelay(150)
                    .start();

            emailLayout.setError(null);
            passwordLayout.setError(null);
        } else {
            loading.animate()
                    .alpha(0)
                    .setDuration(250)
                    .start();

            submit.setVisibility(View.VISIBLE);
            ViewCompat.setAlpha(submit, 0);

            submit.setClickable(true);
            submit.animate()
                    .alpha(1)
                    .setDuration(250)
                    .setStartDelay(150)
                    .start();

        }
    }

    private void login() {
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();

        if (emailString.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            emailLayout.setError("Invalid email");
            return;
        }

        if (passwordString.isEmpty()) {
            passwordLayout.setError("Enter a valid password");
            return;
        }

        animateViews(true);

        emailLayout.setError(null);
        passwordLayout.setError(null);

        mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Timber.w("Login successful");
            } else {
                Timber.w(task.getException(), "Login failed");

                animateViews(false);
                UIUtils.okDialog(this, "Login failed",
                        "Could not complete login. Please retry in a few minutes.");
            }
        });
    }
}

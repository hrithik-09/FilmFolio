package com.rkdigital.filmfolio;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import android.os.CancellationSignal;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rkdigital.filmfolio.storage.SharedPreferenceObserver;
import com.rkdigital.filmfolio.storage.SharedPreferencesHelper;

import android.widget.LinearLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LoginScreen extends AppCompatActivity implements SharedPreferenceObserver.OnPreferenceChangeListener {
    private SignInClient signInClient;
    private ExecutorService executorService;
    private CredentialManager credentialManager;
    private MaterialCardView googleSignInButton;
    private FirebaseAuth mAuth;
    private boolean doubleBackToExitPressedOnce=false;
    private ActivityResultLauncher<IntentSenderRequest> googleSignInLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        SharedPreferencesHelper.getInstance(this);
        SharedPreferenceObserver.getInstance(this).addObserver(this);
        mAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(this);
        executorService = Executors.newSingleThreadExecutor();

        googleSignInButton = findViewById(R.id.googleLoginButton);
        googleSignInButton.setOnClickListener(v -> {
            googleSignInButton.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        googleSignInButton.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                        // Handle sign-in
                        signInWithGoogle();
                    })
                    .start();
        });

    }
    private void signInWithGoogle() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        CancellationSignal cancellationSignal = new CancellationSignal();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(new GetSignInWithGoogleOption.Builder(getString(R.string.default_web_client_id)).build())
                .build();

        CredentialManagerCallback<GetCredentialResponse, GetCredentialException> callback = new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
            @Override
            public void onResult(GetCredentialResponse response) {
                Credential credential = response.getCredential();
                if (credential instanceof GoogleIdTokenCredential) {
                    GoogleIdTokenCredential googleCredential = (GoogleIdTokenCredential) credential;
                    String idToken = googleCredential.getIdToken();

                    if (idToken != null) {
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);

                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        // Save user info locally (your own method)
                                        saveGoogleUserInfo(googleCredential);

                                        runOnUiThread(() -> {
                                            Toast.makeText(getApplicationContext(), "Google Sign-In Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginScreen.this, MainActivity.class));
                                            finish();
                                        });
                                    } else {
                                        runOnUiThread(() -> {
                                            Toast.makeText(getApplicationContext(), "Firebase Auth failed", Toast.LENGTH_SHORT).show();
                                        });
                                        Log.e("FirebaseAuth", "signInWithCredential failed", task.getException());
                                    }
                                });

                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Failed to retrieve ID Token", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Invalid credential type", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onError(GetCredentialException exception) {
                Log.e("GoogleSignIn", "Sign-In Failed", exception);
                runOnUiThread(() -> Toast.makeText(LoginScreen.this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show());
            }
        };


        credentialManager.getCredentialAsync(
                this,
                request,
                cancellationSignal,
                ContextCompat.getMainExecutor(this),
                callback
        );
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    private void saveGoogleUserInfo(GoogleIdTokenCredential credential) {

        SharedPreferencesHelper sharedPrefs = SharedPreferencesHelper.getInstance(getApplicationContext());

        sharedPrefs.putString(sharedPrefs.getUserPrefs(), SharedPrefsKeys.USER_ID, credential.getId());
        sharedPrefs.putBoolean(sharedPrefs.getAppPrefs(), SharedPrefsKeys.IS_LOGGED_IN, true);
        sharedPrefs.putString(sharedPrefs.getUserPrefs(), SharedPrefsKeys.USER_NAME, credential.getDisplayName());
    }
    @Override
    public void onPreferenceChanged(SharedPreferences sharedPreferences, String key) {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferenceObserver.getInstance(this).removeObserver(this);
        executorService.shutdown();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce){
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce=true;
        Toast.makeText(this,"Press back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(()->doubleBackToExitPressedOnce=false,5000);
    }

}
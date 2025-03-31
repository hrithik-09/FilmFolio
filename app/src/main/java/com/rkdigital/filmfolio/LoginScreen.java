package com.rkdigital.filmfolio;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.IntentSenderRequest;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import android.widget.LinearLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LoginScreen extends AppCompatActivity implements SharedPreferenceObserver.OnPreferenceChangeListener {
    private SignInClient signInClient;
    private ExecutorService executorService;
    LinearLayout googleSignInButton;
    private ActivityResultLauncher<IntentSenderRequest> googleSignInLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        SharedPreferencesHelper.getInstance(this);
        SharedPreferenceObserver.getInstance(this).addObserver(this);

        executorService = Executors.newSingleThreadExecutor();

        signInClient = Identity.getSignInClient(this);
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        try {
                            SignInCredential credential = signInClient.getSignInCredentialFromIntent(result.getData());
                            String idToken = credential.getGoogleIdToken();
                            if (idToken != null) {
                                saveGoogleUserInfo(credential);
                                Toast.makeText(this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginScreen.this, MainActivity.class));
                                finish();
                            }
                        } catch (ApiException e) {
                            Log.e("GoogleSignIn", "Sign-In Failed", e);
                            Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        googleSignInButton = findViewById(R.id.googleLoginButton);
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

    }
    private void signInWithGoogle() {
        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id)) // Ensure this is set in `strings.xml`
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
        googleSignInButton.setEnabled(false);
        signInClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                    googleSignInLauncher.launch(intentSenderRequest);
                })
                .addOnFailureListener(this, e ->
                {
                    googleSignInButton.setEnabled(true);
                    Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
                });
    }
    private void saveGoogleUserInfo(SignInCredential credential) {

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

}
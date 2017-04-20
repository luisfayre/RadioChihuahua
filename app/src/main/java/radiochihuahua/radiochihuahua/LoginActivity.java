package radiochihuahua.radiochihuahua;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    //Facebook
    private LoginButton loginButtonFB;
    private CallbackManager callbackManager;
    //Google
    private GoogleApiClient googleApiClient;
    private Button loginButtonG;
    public static final int SIN_IN_CODE = 777;
    //Registro
    private TextView textViewSinUp;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    //Login
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ImageView buttonSingIn;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Orientacion Vertical
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Quitar ActionBar
       // ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();


        //Login
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSingIn = (ImageView) findViewById(R.id.buttonSingIn);
        progressDialog = new ProgressDialog(this);


        textViewSinUp = (TextView) findViewById(R.id.textViewSinUp);
        textViewSinUp.setOnClickListener(this);



        /**<GOOGLE></GOOGLE>*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        loginButtonG = (Button) findViewById(R.id.loginButtonG);
        loginButtonG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIN_IN_CODE);
            }
        });

        /**<FACEBOOK></FACEBOOK>*/
        //loginButtonFacebook = (ImageView) findViewById(R.id.loginButtonFacebook);
        callbackManager = CallbackManager.Factory.create();
        loginButtonFB = (LoginButton) findViewById(R.id.loginButtonFB);
        loginButtonFB.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null); //Quitar ICONO FB

        //final LoginButton loginButtonFB = (LoginButton) findViewById(R.id.loginButtonFB);
        //loginButtonFB.setBackgroundResource(R.drawable.btn_facebook);
        loginButtonFB.setReadPermissions(Arrays.asList("email"));
        loginButtonFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                //Exitoso
               // goMainScreen();
                token(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                //Cancelar
                Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                //Error
                Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();

            }
        });


        /**<FIREBASE></FIREBASE>*/
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    goMainScreen();
                }
            }
        };
    }

    private void token(AccessToken accessToken) {


        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful())
                Toast.makeText(LoginActivity.this, "Error al iniciar con Facebook", Toast.LENGTH_SHORT).show();

            }

        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSingInResult(result);
        }
    }

    private void handleSingInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            //goMainScreeng();
            firebaseAuthWithGoogle(result.getSignInAccount());
        } else {
            Toast.makeText(this, R.string.not_log_in, Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Error al iniciar con Google", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void goMainScreen() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }

    @Override
    public void onClick(View v) {
        if(v == textViewSinUp){
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuthListener != null){
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    public void login(View view) {
        userLogin();
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Por favor ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Por favor ingrese su contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Entrando");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            goMainScreen();
                        }
                    }
                });

    }


}

package radiochihuahua.radiochihuahua;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ImageView buttonSingUp;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;
    private EditText editTextPasswordConfirm;
    private EditText editTextUser;
    public boolean isLoggedIn = false;
   // private ToggleButton toggleButton1;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private DatabaseReference databaseReference;


    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Quitar ActionBar
       // ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        progressDialog = new ProgressDialog(this);
        //Registro
        buttonSingUp = (ImageView) findViewById(R.id.buttonSingUp);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = (EditText) findViewById(R.id.editTextPasswordConfirm);
        editTextUser = (EditText) findViewById(R.id.editTextUser);
        //toggleButton1 = (ToggleButton) findViewById(R.id.toggleButton1);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();


        /**<FIREBASE></FIREBASE>*/
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


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
    private void saveUserInformation (){
        String name = editTextUser.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        UserInformation userInformation = new UserInformation(name, email);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child("Usuarios").child(user.getUid()).setValue(userInformation);
       // databaseReference.child("Usuarios").child(user.getUid()).setValue(userInformation);

        Toast.makeText(RegisterActivity.this, "Ingormacion guardada", Toast.LENGTH_SHORT).show();

    }

    public void register(View view) {
        registerUser();
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordConfirm = editTextPasswordConfirm.getText().toString().trim();
        String user = editTextUser.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Por favor ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(user)){
            Toast.makeText(this, "Ingrese su usuario", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Por favor ingrese su ", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(passwordConfirm)){
            Toast.makeText(this, "Confirme su contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

       // progressDialog.setMessage("Registrando Usuario");
       // progressDialog.show();
        if(password.equals(passwordConfirm)){ /**Confirmar contrasela*/

            Toast.makeText(RegisterActivity.this, "Las contraseñas coinciden", Toast.LENGTH_SHORT).show();

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //REGISTRAR USUSARIO
                               saveUserInformation();
                                Toast.makeText(RegisterActivity.this, "Se ha registrado correctamente", Toast.LENGTH_SHORT).show();
                                goMainScreen();

                            } else {
                                Toast.makeText(RegisterActivity.this, "No se ha registrado correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        }

    }
    private void goMainScreen() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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


    public void regCerrar(View view) {
        super.onBackPressed();
    }
}

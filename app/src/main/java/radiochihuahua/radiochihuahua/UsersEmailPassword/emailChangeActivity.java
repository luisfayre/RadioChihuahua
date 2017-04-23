package radiochihuahua.radiochihuahua.UsersEmailPassword;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import radiochihuahua.radiochihuahua.R;
import radiochihuahua.radiochihuahua.UserInformation;

/**
 * Created by Luis Angel on 28/03/2017.
 */
public class emailChangeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth firebaseAuth;
    private TextView emailTextView;
    private EditText changeemaileditText;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private GoogleApiClient googleApiClient;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email);

        emailTextView = (TextView) findViewById(R.id.emailTextView);
        changeemaileditText = (EditText) findViewById(R.id.changeemaileditText);


        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        if (user != null) {
            emailTextView.setText(user.getEmail());
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void contracam(View view) {

        String correo = changeemaileditText.getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.updateEmail(correo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        saveEmail();
                        Toast.makeText(emailChangeActivity.this, "Se ha cambiado exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(emailChangeActivity.this, "Algo paso mal", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void saveEmail() {
        String email = changeemaileditText.getText().toString().trim();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            databaseReference.child("Usuarios").child(user.getUid()).child("email/").setValue(email);
            Toast.makeText(emailChangeActivity.this, "Información guardada", Toast.LENGTH_SHORT).show();
        }
    }
}

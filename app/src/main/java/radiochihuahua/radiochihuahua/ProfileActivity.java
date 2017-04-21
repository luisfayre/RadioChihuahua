package radiochihuahua.radiochihuahua;

import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import radiochihuahua.radiochihuahua.UsersEmailPassword.emailChangeActivity;
import radiochihuahua.radiochihuahua.UsersEmailPassword.passwordChangeActivity;

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private TextView nameTextView;
    private TextView emailTextView;
    //private TextView idFBTextView;
    private ImageView photoFBImageView;
    private TextView passwordTextView;
    //Firbase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private GoogleApiClient googleApiClient;

    private String emailperdido;
    private TextView editTextPassword;
    private TextView locationTextView;
    private Switch switchGoogle;
    private Switch switchFacebook;

    //Facebook
    private CallbackManager callbackManager;

    //IMAGEN
    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri filePath;
    private StorageReference storageReference;
    //Toolbar
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locationTextView = (TextView) findViewById(R.id.locationTextView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        photoFBImageView = (ImageView) findViewById(R.id.photoFBImageView);
        switchGoogle = (Switch) findViewById(R.id.switchGoogle);
        switchFacebook = (Switch) findViewById(R.id.switchFacebook);
        passwordTextView = (TextView) findViewById(R.id.passwordTextView);
        editTextPassword = (TextView) findViewById(R.id.editTextPassword);

        callbackManager = CallbackManager.Factory.create();

        storageReference = FirebaseStorage.getInstance().getReference();

        passwordTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordTextView.setText("password");

        //Toast.makeText(ProfileActivity.this, "", Toast.LENGTH_SHORT).show();
        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();


        if (user != null) {

            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            // String uid = user.getUid();

            nameTextView.setText(user.getDisplayName());
            emailTextView.setText(user.getEmail());
            // idFBTextView.setText(uid);
            StorageReference riversRef = storageReference.child("images/").child("perfil/").child(user.getUid());
            Glide.with(this).load(photoUrl).into(photoFBImageView);

        } else {
            goLoginScreen();
        }
    }


    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /***
     * Cerrar cesion 2 plataformas
     **/
 /*  public void logOutFB(View view) {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    goLoginScreen();
                } else {
                    Toast.makeText(ProfileActivity.this, "No se pudo cerrar sesión", Toast.LENGTH_SHORT).show();
                }
            }
        });
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }  */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void fotoPerfirl(View view) {
        Toast.makeText(this, "Cambiar foto de perfir", Toast.LENGTH_SHORT).show();
        showFileChooser();
        upliadFile();

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void upliadFile() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (filePath != null) {
            StorageReference riversRef = storageReference.child("images/").child("perfil/").child(user.getUid());
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ProfileActivity.this, "Archivo subido perfectamente", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        }
                    });
        } else {
            Toast.makeText(ProfileActivity.this, "No se ha subido correctamente", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                photoFBImageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //switchFB con facebook
    public void switchFB(View view) {
        if (AccessToken.getCurrentAccessToken() != null) {
            switchFacebook.setChecked(true);
            salirFacebook();

        } else {
            switchFacebook.setChecked(false);
        }
    }

    //switchFB con GOOGLE
    public void switchG(View view) {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            Toast.makeText(ProfileActivity.this, "Logeado Con Google", Toast.LENGTH_SHORT).show();
            switchGoogle.setChecked(true);
            salirGoogle();
        } else {
            switchGoogle.setChecked(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Cuentas();

    }

    public void Cuentas() {

        //COMPROBAR GOOGLE
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            Toast.makeText(ProfileActivity.this, "Logeado Con Google", Toast.LENGTH_SHORT).show();
            switchGoogle.setChecked(true);

        } else {
            switchGoogle.setChecked(false);
        }

        //COMPROBAR FACEBOOK
        if (AccessToken.getCurrentAccessToken() != null) {
            Toast.makeText(ProfileActivity.this, "Logeado Con Facebook", Toast.LENGTH_SHORT).show();
            switchFacebook.setChecked(true);

            fotoFacebook();

        } else {
            switchFacebook.setChecked(false);
        }
    }

    private void fotoFacebook() {
        int dimensionPixelSize = getResources().getDimensionPixelSize(com.facebook.R.dimen.com_facebook_profilepictureview_preset_size_large);
        Profile profile = Profile.getCurrentProfile();
        Uri profilePictureUri = ImageRequest.getProfilePictureUri(profile.getId(), dimensionPixelSize, dimensionPixelSize);

        Glide.with(this).load(profilePictureUri)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(photoFBImageView);

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();


    }



    public void salirGoogle() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    goLoginScreen();
                } else {
                    Toast.makeText(ProfileActivity.this, "No se pudo cerrar sesión", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void salirFacebook() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    public void salirCorreo(View view) {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (AccessToken.getCurrentAccessToken() == null) {
            if (opr.isDone()) {
                Toast.makeText(ProfileActivity.this, "Usuario Logeado con Google", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseAuth.getInstance().signOut();
                goLoginScreen();
            }
        } else {
            Toast.makeText(ProfileActivity.this, "Usuario Logeado con Facebook", Toast.LENGTH_SHORT).show();
        }

    }

    public void cambiarCorreo(View view) {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (AccessToken.getCurrentAccessToken() == null) {
            if (opr.isDone()) {
                Toast.makeText(ProfileActivity.this, "Usuario registrado con Google, no puede cambiar correo", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(ProfileActivity.this, emailChangeActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else {
            Toast.makeText(ProfileActivity.this, "Usuario registrado con Facebook, no puede cambiar correo", Toast.LENGTH_SHORT).show();
        }

/*
        firebaseAuth = FirebaseAuth.getInstance();
        emailperdido = emailTextView.getText().toString();
        firebaseAuth.sendPasswordResetEmail(emailperdido).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                }

            }
        });*/

    }

    public void cambiarContraseña(View view) {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (AccessToken.getCurrentAccessToken() == null) {
            if (opr.isDone()) {
                Toast.makeText(ProfileActivity.this, "Usuario registrado con Google, no puede cambiar contraseña", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(ProfileActivity.this, passwordChangeActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else {
            Toast.makeText(ProfileActivity.this, "Usuario registrado con Facebook, no puede cambiar contraseña", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        if (res_id == R.id.action_back) {
            Toast.makeText(ProfileActivity.this, "Atras", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ReproductorActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        if (res_id == R.id.action_cancel) {
            Toast.makeText(ProfileActivity.this, "Cancelar", Toast.LENGTH_SHORT).show();
        }
        if (res_id == R.id.action_ok) {
            Toast.makeText(ProfileActivity.this, "Guardar", Toast.LENGTH_SHORT).show();
        }
        return true;
    }



}



package radiochihuahua.radiochihuahua;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInstaller;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.FirebaseDatabase;
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

    //Elementos Interfaz
    private TextView emailTextView;
    private TextView nameTextView;
    private TextView passwordTextView;
    private ImageView photoFBImageView;
    private TextView locationTextView;

    private TextView TextViewemail;
    private TextView TextViewepassword;
    private TextView TextViewenombre;
    private TextView TextViewlocation;
    private TextView TextViewgoogle;
    private TextView TextViewfacebook;
    private TextView textoperfil;

    private Switch switchGoogle;
    private Switch switchFacebook;
    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private GoogleApiClient googleApiClient;
    //Facebook
    private CallbackManager callbackManager;
    //Imagen
    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri filePath;
    private StorageReference storageReference;
    //Toolbar
    private Toolbar toolbar;
    private TextView TextView_toolbar_profile;
    //ProgressDialog
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Orientacion Vertical
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Elementos Interfaz
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        passwordTextView = (TextView) findViewById(R.id.passwordTextView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);

        TextViewemail = (TextView) findViewById(R.id.TextViewemail);
        TextViewepassword = (TextView) findViewById(R.id.TextViewepassword);
        TextViewenombre = (TextView) findViewById(R.id.TextViewenombre);
        TextViewlocation = (TextView) findViewById(R.id.TextViewlocation);
        TextViewgoogle = (TextView) findViewById(R.id.TextViewgoogle);
        TextViewfacebook = (TextView) findViewById(R.id.TextViewfacebook);
        textoperfil = (TextView) findViewById(R.id.textView_fotoperfil);
        //Imagen Perfil
        photoFBImageView = (ImageView) findViewById(R.id.photoFBImageView);
        //Switch Redes
        switchGoogle = (Switch) findViewById(R.id.switchGoogle);
        switchFacebook = (Switch) findViewById(R.id.switchFacebook);
        //Toolbar
        TextView_toolbar_profile = (TextView) findViewById(R.id.TextView_toolbar_profile);
        //ProgressDialog
        progressDialog = new ProgressDialog(this);


        //Tipo de letra
        Typeface Bold = Typeface.createFromAsset(getAssets(), "Montserrat-Bold.otf");
        Typeface Light = Typeface.createFromAsset(getAssets(), "Montserrat-Light.otf");
        TextView_toolbar_profile.setTypeface(Bold);

        emailTextView.setTypeface(Bold); //correo
        nameTextView.setTypeface(Bold); //nombre
        passwordTextView.setTypeface(Bold); // contraseña
        locationTextView.setTypeface(Bold); //ubicacion

        TextViewemail.setTypeface(Bold); //correo
        TextViewepassword.setTypeface(Bold); //nombre
        TextViewenombre.setTypeface(Bold); // contraseña
        TextViewlocation.setTypeface(Bold); //ubicacion
        TextViewgoogle.setTypeface(Bold);   //Google
        TextViewfacebook.setTypeface(Bold); //Facebook
        textoperfil.setTypeface(Light);
        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationIcon(R.drawable.perfil_atrasflecha);
        }

        callbackManager = CallbackManager.Factory.create();

        storageReference = FirebaseStorage.getInstance().getReference();

        //Contraseña oculta
        passwordTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordTextView.setText("password");

        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //Firebase
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
        fotodePerfil();
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void fotoPerfirl(View view) {
        showFileChooser(); // Selecionar archivo
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Foto de perfil"), PICK_IMAGE_REQUEST);
    }

    private void subirFotodeperfil() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            if (filePath != null) {
                progressDialog.setTitle("Foto de perfil");
                progressDialog.show();
                StorageReference riversRef = storageReference.child("images/").child("perfil/").child(user.getUid());
                riversRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Foto de perfil cambiada exitosamente", Toast.LENGTH_SHORT).show();
                                fotodePerfil();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "El cambio ha fallado", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                //displaying percentage in progress dialog
                                progressDialog.setMessage("Subiendo " + ((int) progress) + "%...");
                            }
                        });
            } else {
                Toast.makeText(ProfileActivity.this, "Su foto de perfil no se ha subido perfectamente", Toast.LENGTH_SHORT).show();
            }
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
                subirFotodeperfil();
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
        //Comprobar con google
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            // Toast.makeText(ProfileActivity.this, "Logeado Con Google", Toast.LENGTH_SHORT).show();
            switchGoogle.setChecked(true);
        } else {
            switchGoogle.setChecked(false);
        }
        //Comprobar con FACEBOOK
        if (AccessToken.getCurrentAccessToken() != null) {
            // Toast.makeText(ProfileActivity.this, "Logeado Con Facebook", Toast.LENGTH_SHORT).show();
            switchFacebook.setChecked(true);
            fotoFacebook();

        } else {
            switchFacebook.setChecked(false);
        }
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
        if (res_id == android.R.id.home) {
             Toast.makeText(ProfileActivity.this, "Atras", Toast.LENGTH_SHORT).show();
            reproductor();
        }
        if (res_id == R.id.action_cancel) {
            Toast.makeText(ProfileActivity.this, "Cancelar", Toast.LENGTH_SHORT).show();
        }
        if (res_id == R.id.action_ok) {
            Toast.makeText(ProfileActivity.this, "Guardar", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void reproductor() {
        Intent intent = new Intent(this, ReproductorActivity.class);
        startActivity(intent);
    }


    /**
     * Foto de Perfil
     */
    private void fotodePerfil() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            StorageReference islandRef = storageReference.child("images/").child("perfil/").child(user.getUid());
            islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(ProfileActivity.this)
                            .load(uri)
                            .animate(R.anim.fade_in)
                            .into(photoFBImageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
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

}



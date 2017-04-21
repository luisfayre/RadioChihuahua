package radiochihuahua.radiochihuahua;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class ReproductorActivity extends AppCompatActivity {

    //Texto
    private TextView ArtistatextView, CanciontextView, AlbumtextView;
    //Botones
    private ImageView play, siguiente, anterior, repetir, alternar;
    //Imagen
    private ImageView reproductor;
    //Toolbar
    private Toolbar toolbar;
    private TextView TextView_toolbar;
    //Reproductor
    private MediaPlayer mediaPlayer;
    private String STREAM_URL ="https://p-audio-4.radpog.com/play/15.mp3";  //Magia digital 93.3
    private ProgressBar progressBar;  //Progressbar
    //Checar internet
    private boolean connected = false;
    //Firebase
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);


        /**TEXTO-**/

        ArtistatextView = (TextView) findViewById(R.id.ArtistatextView);
        CanciontextView = (TextView) findViewById(R.id.CanciontextView);
        AlbumtextView = (TextView) findViewById(R.id.AlbumtextView);
        TextView_toolbar = (TextView) findViewById(R.id.TextView_toolbar);
        reproductor = (ImageView) findViewById(R.id.imageView_caratula);
        //Fuente Letras
        Typeface Bold = Typeface.createFromAsset(getAssets(), "Montserrat-Bold.otf");
        Typeface Light = Typeface.createFromAsset(getAssets(), "Montserrat-Light.otf");
        ArtistatextView.setTypeface(Bold);
        CanciontextView.setTypeface(Bold);
        AlbumtextView.setTypeface(Light);
        TextView_toolbar.setTypeface(Bold);

        /**BOTONES-*/
        play = (ImageView) findViewById(R.id.imageView_rep_play);
        anterior = (ImageView) findViewById(R.id.imageView_rep_anterior);
        siguiente = (ImageView) findViewById(R.id.imageView_rep_siguiente);
        alternar = (ImageView) findViewById(R.id.imageView_rep_shuffle);
        repetir = (ImageView) findViewById(R.id.imageView_rep_repetir);

        /**TOOLBAR-*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.getBackground().setAlpha(0);
        if(getSupportActionBar() != null){
           getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationIcon(R.drawable.rep_flechaizq);

        }

        /**REPRODUCTOR-*/
        mediaPlayer = new MediaPlayer();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        /**FIREBASE-*/
        storageReference = FirebaseStorage.getInstance().getReference();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pruebaConeccion();
                if (connected == true) {
                    if (mediaPlayer.isPlaying()) {
                        play.setImageResource(R.drawable.play);
                        mediaPlayer.stop();
                    } else {
                        try {
                            play.setImageResource(R.drawable.circulo);
                            progressBar.setVisibility(View.VISIBLE);
                            mediaPlayer.reset();
                            CanciontextView.setText("Cargando...");
                            AlbumtextView.setText("Cargando...");
                            ArtistatextView.setText("Cargando...");
                            mediaPlayer.setDataSource(STREAM_URL);
                            mediaPlayer.prepareAsync();

                            mediaPlayer.setOnPreparedListener(new MediaPlayer.
                                    OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    progressBar.setVisibility(View.GONE);
                                    play.setImageResource(R.drawable.stop);
                                    horachingona();
                                    mp.start();
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                }
            }


        });

        imagenEstacion();

    }

    private void imagenEstacion(){
        //StorageReference islandRef = storageReference.child("la_nortenita/6211_290.png");
        StorageReference islandRef = storageReference.child("magia/magiadigital.png");

        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
               Glide.with(ReproductorActivity.this).load(uri).into(reproductor);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void horachingona() {

        Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int dia = c.get(Calendar.DAY_OF_WEEK);
        String programa = "";
        String conductor= "";
        String estacion = "Magia digital 93.3";

        switch (dia) {
            case 1:
               // Toast.makeText(ReproductorActivity.this, "Lunes", Toast.LENGTH_SHORT).show();
                if(hora >= 1 && hora < 6){
                    programa = "Programación música normal";
                }else if(hora >= 6 && hora < 10){
                    programa = "Morning Show";
                    conductor = "Chavita de la Riva";
                }else if(hora >= 10 && hora < 11){
                    programa = "Musica con Mayra Franco";
                    conductor = "Mayra Franco";
                }else if(hora >= 11 && hora < 13){
                    programa = "De boca en boca";
                    conductor = "Mayra Franco";
                }else if(hora >= 13 && hora < 14){
                    programa = "De boca en boca";
                    conductor = "Mayra Franco";
                }else if(hora >= 14 && hora < 15){
                    programa = "Musica con Leif Parra";
                    conductor = "Leif Parra";
                }else if(hora >= 15 && hora < 16){
                    programa = "Las Trenzas de Vikingo";
                }else if(hora >= 16 && hora < 18){
                    programa = "Musica con Leif Parra";
                    conductor = "Leif Parra";
                }else if(hora >= 18 && hora < 22){
                    programa = "Música";
                    conductor = "Alejandro Richarte";
                }else if(hora >= 22 && hora < 23){
                    programa = "Programación músucal normal";
                }
                CanciontextView.setText(programa);
                AlbumtextView.setText(conductor);
                ArtistatextView.setText(estacion);
                break;
            case 2:
                //   Toast.makeText(ReproductorActivity.this, "Martes", Toast.LENGTH_SHORT).show();
                if(hora >= 1 && hora < 6){
                    programa = "Programación música normal";
                }else if(hora >= 6 && hora < 10){
                    programa = "Morning Show";
                    conductor = "Chavita de la Riva";
                }else if(hora >= 10 && hora < 11){
                    programa = "Musica con Mayra Franco";
                    conductor = "Mayra Franco";
                }else if(hora >= 11 && hora < 13){
                    programa = "De boca en boca";
                    conductor = "Mayra Franco";
                }else if(hora >= 13 && hora < 14){
                    programa = "De boca en boca";
                    conductor = "Mayra Franco";
                }else if(hora >= 14 && hora < 15){
                    programa = "Musica con Leif Parra";
                    conductor = "Leif Parra";
                }else if(hora >= 15 && hora < 16){
                    programa = "Las Trenzas de Vikingo";
                }else if(hora >= 16 && hora < 18){
                    programa = "Musica con Leif Parra";
                    conductor = "Leif Parra";
                }else if(hora >= 18 && hora < 22){
                    programa = "Música";
                    conductor = "Alejandro Richarte";
                }else if(hora >= 22 && hora < 23){
                    programa = "Programación músucal normal";
                }
                CanciontextView.setText(programa);
                AlbumtextView.setText(conductor);
                ArtistatextView.setText(estacion);
                break;
            case 3:
                //  Toast.makeText(ReproductorActivity.this, "Miercoles", Toast.LENGTH_SHORT).show();
                if(hora >= 1 && hora < 6){
                    programa = "Programación música normal";
                }else if(hora >= 6 && hora < 10){
                    programa = "Morning Show";
                    conductor = "Chavita de la Riva";
                }else if(hora >= 10 && hora < 11){
                    programa = "Musica con Mayra Franco";
                    conductor = "Mayra Franco";
                }else if(hora >= 11 && hora < 13){
                    programa = "De boca en boca";
                    conductor = "Mayra Franco";
                }else if(hora >= 13 && hora < 14){
                    programa = "De boca en boca";
                    conductor = "Mayra Franco";
                }else if(hora >= 14 && hora < 15){
                    programa = "Musica con Leif Parra";
                    conductor = "Leif Parra";
                }else if(hora >= 15 && hora < 16){
                    programa = "Las Trenzas de Vikingo";
                }else if(hora >= 16 && hora < 18){
                    programa = "Musica con Leif Parra";
                    conductor = "Leif Parra";
                }else if(hora >= 18 && hora < 22){
                    programa = "Música";
                    conductor = "Alejandro Richarte";
                }else if(hora >= 22 && hora < 23){
                    programa = "Programación músucal normal";
                }
                CanciontextView.setText(programa);
                AlbumtextView.setText(conductor);
                ArtistatextView.setText(estacion);
                break;
            case 4:
                //  Toast.makeText(ReproductorActivity.this, "Jueves", Toast.LENGTH_SHORT).show();
                if(hora >= 1 && hora < 6){
                    programa = "Programación música normal";
                }else if(hora >= 6 && hora < 10){
                    programa = "Morning Show";
                    conductor = "Chavita de la Riva";
                }else if(hora >= 10 && hora < 11){
                    programa = "Musica con Mayra Franco";
                    conductor = "Mayra Franco";
                }else if(hora >= 11 && hora < 13){
                    programa = "De boca en boca";
                    conductor = "Mayra Franco";
                }else if(hora >= 13 && hora < 14){
                    programa = "De boca en boca";
                    conductor = "Mayra Franco";
                }else if(hora >= 14 && hora < 15){
                    programa = "Musica con Leif Parra";
                    conductor = "Leif Parra";
                }else if(hora >= 15 && hora < 16){
                    programa = "Las Trenzas de Vikingo";
                }else if(hora >= 16 && hora < 18){
                    programa = "Musica con Leif Parra";
                    conductor = "Leif Parra";
                }else if(hora >= 18 && hora < 22){
                    programa = "Música";
                    conductor = "Alejandro Richarte";
                }else if(hora >= 22 && hora < 24){
                    programa = "Programación músucal normal";
                }
                CanciontextView.setText(programa);
                AlbumtextView.setText(conductor);
                ArtistatextView.setText(estacion);
                break;
            case 5:
                //   Toast.makeText(ReproductorActivity.this, "Viernes", Toast.LENGTH_SHORT).show();
                if(hora >= 1 && hora < 6){
                    programa = "Programación música normal";
                }else if(hora >= 6 && hora < 10){
                    programa = "Morning Show";
                    conductor = "Chavita de la Riva";
                }else if(hora >= 10 && hora < 11){
                    programa = "Musica con Mayra Franco";
                    conductor = "Mayra Franco";
                }else if(hora >= 11 && hora < 13){
                    programa = "De boca en boca";
                    conductor = "Mayra Franco";
                }else if(hora >= 13 && hora < 14){
                    programa = "De boca en boca";
                    conductor = "Mayra Franco";
                }else if(hora >= 14 && hora < 15){
                    programa = "Musica con Leif Parra";
                    conductor = "Leif Parra";
                }else if(hora >= 15 && hora < 16){
                    programa = "Las Trenzas de Vikingo";
                }else if(hora >= 16 && hora < 18){
                    programa = "Musica con Leif Parra";
                    conductor = "Leif Parra";
                }else if(hora >= 18 && hora < 22){
                    programa = "Música";
                    conductor = "Alejandro Richarte";
                }else if(hora >= 22 && hora < 24){
                    programa = "Programación músical normal";
                }
                CanciontextView.setText(programa);
                AlbumtextView.setText(conductor);
                ArtistatextView.setText(estacion);
                break;
            case 6:
                //    Toast.makeText(ReproductorActivity.this, "Sabado", Toast.LENGTH_SHORT).show();
                if(hora >= 1 && hora < 6){
                    programa = "Programación música normal";
                }else if(hora >= 6 && hora < 10){
                    programa = "Música con Chavita de la Riva";
                    conductor = "Chavita de la Riva";
                }else if(hora >= 14 && hora < 18){
                    programa = "Musica con Leif Parra";
                    conductor = "Leif Parra";
                }else if(hora >= 18 && hora < 19){
                    programa = "Musica";
                    conductor = "Alejandro Richarte";
                }else if(hora >= 19 && hora < 21){
                    programa = "T.N.C the Nashville conection";
                    conductor = "Armando Velazquez";
                }else if(hora >= 22 && hora < 23){
                    programa = "Programación músical normal";
                }
                CanciontextView.setText(programa);
                AlbumtextView.setText(conductor);
                ArtistatextView.setText(estacion);
                break;
            case 7:
                //    Toast.makeText(ReproductorActivity.this, "Domingo", Toast.LENGTH_SHORT).show();
                if(hora >= 22 && hora < 19){
                    programa = "Programación música normal";
                }else if(hora >= 19 && hora < 21) {
                    programa = "T.N.C the Nashville conection";
                    conductor = "Armando Velazquez";
                }else if(hora >= 19 && hora < 21) {
                    programa = "La Hora Nacional";
                }
                    CanciontextView.setText(programa);
                AlbumtextView.setText(conductor);
                ArtistatextView.setText(estacion);
                break;



        }
    }

    private boolean pruebaConeccion(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            //Toast.makeText(ReproductorActivity.this, "Conexion a internet", Toast.LENGTH_SHORT).show();
        }
        else{
            connected = false;
            Toast.makeText(ReproductorActivity.this, "Sin conexion a internet intente mas tarde", Toast.LENGTH_SHORT).show();

        }

        return connected;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_rep, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        if (res_id == android.R.id.home) {
            Toast.makeText(ReproductorActivity.this, "Atras", Toast.LENGTH_SHORT).show();
        }
        if (res_id == R.id.action_buscar) {
            Toast.makeText(ReproductorActivity.this, "Buscar", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }
}

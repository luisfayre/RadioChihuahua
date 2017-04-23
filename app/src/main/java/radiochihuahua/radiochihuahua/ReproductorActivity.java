package radiochihuahua.radiochihuahua;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class ReproductorActivity extends AppCompatActivity {

    //Texto
    private TextView ArtistatextView, CanciontextView, AlbumtextView;
    //Botones
    private ImageView play, siguiente, anterior, repetir, alternar;
    //Imagen
    private ImageView reproductor;
    private ProgressBar progressBarRep;
    //Toolbar
    private Toolbar toolbar;
    private TextView TextView_toolbar;
    //Reproductor
    private MediaPlayer mediaPlayer;
    private String STREAM_URL_MAGIA ="https://p-audio-4.radpog.com/play/15.mp3";  //Magia digital 93.3
    private String STREAM_URL_NORTENA ="https://p-audio-4.radpog.com/play/16.mp3";  //Magia digital 93.3
    private String STREAM_URL_ROMANCE ="https://p-audio-4.radpog.com/play/14.mp3";  //Magia digital 93.3
    private String DEFECTO = "";
    private ProgressBar progressBar;  //Progressbar
    //Checar internet
    private boolean connected = false;
    //Firebase
    private StorageReference storageReference;

    private Animation fade_in,fade_out;
    private Handler mHandler = new Handler();
    private Utilities utils;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private TextView tiempoInicio, tiempoFin;
    private SeekBar seekBar;
    private double timeStart = 0, finalTime =0;
    private double totalDuration = 0, currentDuration =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Orientacion Vertical
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        DEFECTO = STREAM_URL_MAGIA;
        /**TEXTO-**/

        ArtistatextView = (TextView) findViewById(R.id.ArtistatextView);
        CanciontextView = (TextView) findViewById(R.id.CanciontextView);
        AlbumtextView = (TextView) findViewById(R.id.AlbumtextView);
        TextView_toolbar = (TextView) findViewById(R.id.TextView_toolbar);

        tiempoInicio = (TextView) findViewById(R.id.TextView_tiempoinicio);  //Control de tiempo
        tiempoFin = (TextView) findViewById(R.id.TextView_tiempofin);        //Control de tiempo
        reproductor = (ImageView) findViewById(R.id.imageView_caratula);     //Caratula estacion

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        progressBarRep = (ProgressBar) findViewById(R.id.progressBarRep);
        progressBarRep.setVisibility(View.VISIBLE);//ProgressBar Carratula
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
        progressBar = (ProgressBar) findViewById(R.id.progressBar); //ProgressBar Play
        utils = new Utilities();


        progressBar.setVisibility(View.GONE);

        /**FIREBASE-*/
        storageReference = FirebaseStorage.getInstance().getReference();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pruebaConeccion();
                if (connected) {
                    if (mediaPlayer.isPlaying()) {
                        play.setImageResource(R.drawable.play);
                        mediaPlayer.stop();
                    } else {
                        try {
                            play.setImageResource(R.drawable.circulo);
                            progressBar.setVisibility(View.VISIBLE); //Progressbar play
                            mediaPlayer.reset();
                            CanciontextView.setText("Cargando...");
                            AlbumtextView.setText("Cargando...");
                            ArtistatextView.setText("Cargando...");
                            mediaPlayer.setDataSource(DEFECTO);
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.
                                    OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    progressBar.setVisibility(View.GONE);
                                    play.setImageResource(R.drawable.stop);
                                    horarioMagia();
                                    mp.start();
                                    updateProgressBar();
//                                    long currentDuration = mediaPlayer.getCurrentPosition();
//                                    timeStart = mediaPlayer.getCurrentPosition();
//                                    seekBar.setProgress((int) timeStart);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                }
            }


        });

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DEFECTO == STREAM_URL_MAGIA){
                    DEFECTO = STREAM_URL_NORTENA;
                    imagenEstacionNortenita();
                    radio(DEFECTO);
                }else if(DEFECTO == STREAM_URL_NORTENA){
                    DEFECTO = STREAM_URL_ROMANCE;
                    horarioRomance();
                    imagenEstacionRomance();
                    radio(DEFECTO);
                }else if(DEFECTO == STREAM_URL_ROMANCE) {
                    DEFECTO = STREAM_URL_MAGIA;
                    imagenEstacionMagia();
                    radio(DEFECTO);
                }
            }


        });

        anterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DEFECTO == STREAM_URL_MAGIA){
                    DEFECTO = STREAM_URL_ROMANCE;
                    imagenEstacionRomance();
                    horarioRomance();
                    radio(DEFECTO);
                }else if(DEFECTO == STREAM_URL_NORTENA){
                    DEFECTO = STREAM_URL_MAGIA;
                    imagenEstacionMagia();
                    radio(DEFECTO);
                }else if(DEFECTO == STREAM_URL_ROMANCE){
                    DEFECTO = STREAM_URL_NORTENA;
                    imagenEstacionNortenita();
                    radio(DEFECTO);
                }
            }


        });



        /*  ///ANIMACION
        fade_in = AnimationUtils.loadAnimation(ReproductorActivity.this, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(ReproductorActivity.this, R.anim.fade_out);
        reproductor.setAnimation(fade_in)d;
        */
        //updateProgressBar();
        imagenEstacionMagia();
    }

    private void imagenEstacionRomance() {
        StorageReference islandRef = storageReference.child("romance/EstacionRomance.png");
        //StorageReference islandRef = storageReference.child("la_nortenita/magiadigital.png");
        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                progressBarRep.setVisibility(View.GONE);
                Glide.with(ReproductorActivity.this)
                        .load(uri)
                        .animate(R.anim.fade_in)
                        .into(reproductor);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void radio(String defecto) {
        pruebaConeccion();
        if (connected) {
            if (mediaPlayer.isPlaying()) {
                play.setImageResource(R.drawable.play);
                mediaPlayer.stop();
                radio(defecto);
            } else {
                try {
                    play.setImageResource(R.drawable.circulo);
                    progressBar.setVisibility(View.VISIBLE); //Progressbar play
                    mediaPlayer.reset();
                   CanciontextView.setText("Cargando...");
                    AlbumtextView.setText("Cargando...");
                    ArtistatextView.setText("Cargando...");
                    mediaPlayer.setDataSource(defecto);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.
                            OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            progressBar.setVisibility(View.GONE);
                            play.setImageResource(R.drawable.stop);
                            horarioEstacion(DEFECTO);
                            mp.start();
                            updateProgressBar();
//                                    long currentDuration = mediaPlayer.getCurrentPosition();
//                                    timeStart = mediaPlayer.getCurrentPosition();
//                                    seekBar.setProgress((int) timeStart);
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }
    }

    private void horarioEstacion(String defecto) {
        if(DEFECTO == STREAM_URL_MAGIA){
            horarioMagia();
        }else if(DEFECTO == STREAM_URL_NORTENA){
            horarioNortenia();
        }else if(DEFECTO == STREAM_URL_ROMANCE){
            horarioRomance();
        }
    }


    private void imagenEstacionMagia(){
        //StorageReference islandRef = storageReference.child("la_nortenita/6211_290.png");
        StorageReference islandRef = storageReference.child("magia/magiadigital.png");
        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                progressBarRep.setVisibility(View.GONE);
                Glide.with(ReproductorActivity.this)
                        .load(uri)
                        .animate(R.anim.fade_in)
                        .into(reproductor);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    private void imagenEstacionNortenita(){
        StorageReference islandRef = storageReference.child("la_nortenita/6211_290.png");
        //StorageReference islandRef = storageReference.child("la_nortenita/magiadigital.png");
        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                progressBarRep.setVisibility(View.GONE);
                Glide.with(ReproductorActivity.this)
                        .load(uri)
                        .animate(R.anim.fade_in)
                        .into(reproductor);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

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
           // Toast.makeText(ReproductorActivity.this, "Atras", Toast.LENGTH_SHORT).show();
        }
        if (res_id == R.id.action_buscar) {
           // Toast.makeText(ReproductorActivity.this, "Buscar", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            tiempoFin.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            tiempoInicio.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };



    private void horarioRomance() {
        Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int dia = c.get(Calendar.DAY_OF_WEEK) - 1;
        String programa = "";
        String conductor= "";
        String estacion = "Romance 95.7";
        if (dia <= 5){
            if(hora >= 1 && hora < 6){
                programa = "Programación músical normal";
            }else if(hora >= 6 && hora < 10){
                programa = "Esta manaña Morning Show";
                conductor = "Fernando Rodríguez";
            }else if(hora >= 10 && hora < 12){
                programa = "A media mañana";
                conductor = "Mayra Franco";
            }else if(hora >= 12 && hora < 14){
                programa = "La hora del sabor";
                conductor = "Adriana Márquez";
            }else if(hora >= 14 && hora < 15) {
                programa = "Noticiero megactivo";
                conductor = "Mayra Franco";
            }else if(hora >= 15 && hora < 18) {
                programa = "Tarde Romanceleste";
                conductor = "Celeste Morales";
            }else if(hora >= 18 && hora < 21) {
                programa = "Programación músical normal";
                conductor = "Vera Hernández";
            }else if(hora >= 21 && hora < 23) {
                programa = "Noches de romance";
                conductor = "Música romantica actual y de siempre";
            }else if(hora >= 23 && hora < 24) {
                programa = "Noches de romance";
                conductor = "Música romantica actual y de siempre";
            }
            CanciontextView.setText(programa);
            AlbumtextView.setText(conductor);
            ArtistatextView.setText(estacion);
        }
        if(dia == 6){
            if(hora >= 1 && hora < 6){
                programa = "Programación músical normal";
            }else if(hora >= 6 && hora < 10){
                programa = "Música";
                conductor = "Adriana Márquez";
            }else if(hora >= 10 && hora < 12){
                programa = "Weekend";
                conductor = "Fernando Rodríguez";
            }else if(hora >= 12 && hora < 14){
                programa = "Love ten";
                conductor = "Fernando Rodríguez";
            }else if(hora >= 14 && hora < 18){
                programa = "Progama músical";
                conductor = "Celeste Morales";
            }else if(hora >= 18 && hora < 21){
                programa = "Programación músical";
                conductor = "Vera Hernádez";
            }else if(hora >= 21 && hora < 23) {
                programa = "Noches de romance";
                conductor = "Música romantica actual y de siempre";
            }else if(hora >= 23 && hora < 1) {
                programa = "Noches de romance";
                conductor = "Música romantica actual y de siempre";
            }
            CanciontextView.setText(programa);
            AlbumtextView.setText(conductor);
            ArtistatextView.setText(estacion);
        }
        if(dia == 7){
            if(hora >= 1 && hora < 21){
                programa = "Programación músical normal";
            }else if(hora >= 21 && hora < 22){
                programa = "La Hora Nacional";
            }else if(hora >= 22 && hora < 23){
                programa = "La Hora Nacional";
            }else if(hora >= 23 && hora < 1){
                programa = "La Hora Nacional";
            }
        }
    }
    private void horarioMagia() {

        Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int dia = c.get(Calendar.DAY_OF_WEEK) - 1;
        String programa = "";
        String conductor= "";
        String estacion = "Magia digital 93.3";

        if(dia <= 5){
            if(hora >= 1 && hora < 6){
                programa = "Programación música normal";
            }else if(hora >= 6 && hora < 10){
                programa = "Morning Show";
                conductor = "Chavita de la Riva";
            }else if(hora >= 10 && hora < 11){
                programa = "Música con Mayra Franco";
                conductor = "Mayra Franco";
            }else if(hora >= 11 && hora < 13){
                programa = "De boca en boca";
                conductor = "Mayra Franco";
            }else if(hora >= 13 && hora < 14){
                programa = "Música con Mayra Franco";
                conductor = "Mayra Franco";
            }else if(hora >= 14 && hora < 15){
                programa = "Música con Leif Parra";
                conductor = "Leif Parra";
            }else if(hora >= 15 && hora < 16){
                programa = "Las Trenzas de Vikingo";
            }else if(hora >= 16 && hora < 18){
                programa = "Música con Leif Parra";
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

        }
        if(dia == 6){
            if(hora >= 1 && hora < 6){
                programa = "Programación músical normal";
            }else if(hora >= 6 && hora < 14){
                programa = "Música con Chavita de la Riva";
                conductor = "Chavita de la Riva";
            }else if(hora >= 14 && hora < 18){
                programa = "Música con Leif Parra";
                conductor = "Leif Parra";
            }else if(hora >= 18 && hora < 19){
                programa = "Música con Alejandro Richarte";
                conductor = "Alejandro Richarte";
            }else if(hora >= 19 && hora < 21){
                programa = "T.N.C the Nashville conection";
                conductor = "Armando Velazquez";
            }else if(hora >= 22 && hora < 24){
                programa = "Programación músical normal";
            }
            CanciontextView.setText(programa);
            AlbumtextView.setText(conductor);
            ArtistatextView.setText(estacion);
        }
        if(dia == 7){
            if(hora >= 1 && hora < 19){
                programa = "Programación músical normal";
            }else if(hora >= 19 && hora < 21) {
                programa = "T.N.C the Nashville conection";
                conductor = "Armando Velazquez";
            }else if(hora >= 21 && hora < 22) {
                programa = "La Hora Nacional";
            }else if(hora >= 2 && hora < 24) {
                programa = "Programación músical normal";
            }
            CanciontextView.setText(programa);
            AlbumtextView.setText(conductor);
            ArtistatextView.setText(estacion);
        }
    }
    private void horarioNortenia() {
        Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int dia = c.get(Calendar.DAY_OF_WEEK) - 1;
        String programa = "";
        String conductor= "";
        String estacion = "La Norteñita 91.7";

        if(dia <= 5){
            if(hora >= 1 && hora < 6){
                programa = "Programación Música  normal";
            }else if(hora >= 6 && hora < 7){
                programa = "Amanecer norteño";
                conductor = "Dany Gaytán";
            }else if(hora >= 7 && hora < 9){
                programa = "Mega Radio Noticias";
                conductor = "Abel Salinas y Carlos Gonzáles";
            }else if(hora >= 9 && hora < 10){
                programa = "Recados a la Sierra";
                conductor = "Dany Gaytán";
            }else if(hora >= 9 && hora < 10){
                programa = "Mercado del aire clasificado Compra y Vende";
            }else if(hora >= 10 && hora < 11){
                programa = "'Amorcito Corazoón' Música de catalogo";
                conductor = "Alejandra Alvidrez";
            }else if(hora >= 1 && hora < 12){
                programa = "Música";
                conductor = "Alejandra Alvidrez";
            }else if(hora >= 12 && hora < 13){
                programa = "ARRIBA EL NORTE";
                conductor = "Luis Carlos Serrano";
            }else if(hora >= 13 && hora < 14){
                programa = "Música";
                conductor = "Alejandra Alvídrez";
            }else if(hora >= 14 && hora < 15){
                programa = "Mega Radio Noticias";
                conductor = "Able Salinas y Carlos González";
            }else if(hora >= 15 && hora < 18){
                programa = "Musica con Tony Banda";
                conductor = "Tony Banda";
            }else if(hora >= 18 && hora < 19){
                programa = "La hora de Juan Gabirl, Joan Sebastia e invitados";
                conductor = "Bernardo Ramirez";
            }else if(hora >= 19 && hora < 22){
                programa = "Música";
                conductor = "Bernardo Ramirez";
            }else if(hora >= 19 && hora < 24){
                programa = "Programción músical normal";
            }
            CanciontextView.setText(programa);
            AlbumtextView.setText(conductor);
            ArtistatextView.setText(estacion);
        }
        if(dia == 6){
            if(hora >= 1 && hora < 6){
                programa = "Programación Música  normal";
            }else if(hora >= 6 && hora < 9){
                programa = "Amanecer norteño";
                conductor = "Dany Gaytán";
            }else if(hora >= 9 && hora < 10){
                programa = "Recados a la Sierra";
                conductor = "Dany Gaytán";
            }else if(hora >= 10 && hora < 14){
                programa = "Música";
                conductor = "Alejandra Alvidrez";
            }else if(hora >= 14 && hora < 18){
                programa = "Música con Tony Banda";
                conductor = "Tony Banda";
            }else if(hora >= 18 && hora < 19){
                programa = "La hora de Juan Gabirl, Joan Sebastia e invitados";
                conductor = "Bernardo Ramirez";
            }else if(hora >= 19 && hora < 22){
                programa = "Música";
                conductor = "Bernardo Ramirez";
            }else if(hora >= 22 && hora < 24){
                programa = "Programción músical normal";
            }
            CanciontextView.setText(programa);
            AlbumtextView.setText(conductor);
            ArtistatextView.setText(estacion);
        }
        if(dia == 7){
            if(hora >= 1 && hora < 8){
                programa = "Programación músical normal";
            }else if(hora >= 8 && hora < 9) {
                programa = "Misa Dominical";
                conductor = "Armando Velazquez";
            }else if(hora >= 9 && hora < 21) {
                programa = "Programación músical normal";
            }else if(hora >= 21 && hora < 22) {
                programa = "La Hora Nacional";
            }else if(hora >= 22 && hora < 24) {
                programa = "La Hora Nacional";
            }
            CanciontextView.setText(programa);
            AlbumtextView.setText(conductor);
            ArtistatextView.setText(estacion);
        }
    }
}

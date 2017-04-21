package radiochihuahua.radiochihuahua;


import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
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

import java.io.IOException;
import java.util.Locale;

public class ReproductorActivity extends AppCompatActivity {

    //Texto
    private TextView ArtistatextView, CanciontextView, AlbumtextView;
    //Botones
    private ImageView play, siguiente, anterior, repetir, alternar;
    //Imagen
    private View reproductor;
    //Toolbar
    private Toolbar toolbar;
    private TextView TextView_toolbar;
    //Reproductor
    private MediaPlayer mediaPlayer;
    private String STREAM_URL ="https://p-audio-4.radpog.com/play/15.mp3";
    private ProgressBar progressBar;  //Progressbar


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);


        /**TEXTO-**/
        ArtistatextView = (TextView) findViewById(R.id.ArtistatextView);
        CanciontextView = (TextView) findViewById(R.id.CanciontextView);
        AlbumtextView = (TextView) findViewById(R.id.AlbumtextView);
        TextView_toolbar = (TextView) findViewById(R.id.TextView_toolbar);
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

        play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.stop();
                }else{
                    try{
                        play.setImageResource(R.drawable.circulo);
                        progressBar.setVisibility(View.VISIBLE);
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(STREAM_URL);
                        mediaPlayer.prepareAsync();

                        mediaPlayer.setOnPreparedListener(new MediaPlayer.
                                OnPreparedListener(){
                            @Override
                            public void onPrepared(MediaPlayer mp){
                                progressBar.setVisibility(View.GONE);
                                play.setImageResource(R.drawable.stop);
                                mp.start();
                            }
                        });

                    } catch (IOException e){
                        e.printStackTrace();

                    }
                }
                

            }

        });


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

}

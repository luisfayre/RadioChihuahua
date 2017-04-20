package radiochihuahua.radiochihuahua;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class ReproductorActivity extends AppCompatActivity {


    private TextView artista,album,cancion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        artista = (TextView) findViewById(R.id.artista_textView);
        album = (TextView) findViewById(R.id.album_textView);
        cancion = (TextView) findViewById(R.id.cancion_textView);

    }
}

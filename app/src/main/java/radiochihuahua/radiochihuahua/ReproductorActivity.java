package radiochihuahua.radiochihuahua;


import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class ReproductorActivity extends AppCompatActivity {


    private TextView ArtistatextView, CanciontextView, AlbumtextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        ArtistatextView = (TextView) findViewById(R.id.ArtistatextView);
        CanciontextView = (TextView) findViewById(R.id.CanciontextView);
        AlbumtextView = (TextView) findViewById(R.id.AlbumtextView);
        Typeface Bold = Typeface.createFromAsset(getAssets(), "Montserrat-Bold.otf");
        Typeface Light = Typeface.createFromAsset(getAssets(), "Montserrat-Light.otf");
        ArtistatextView.setTypeface(Bold);
        CanciontextView.setTypeface(Bold);
        AlbumtextView.setTypeface(Light);


    }
}

package radiochihuahua.radiochihuahua;


import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class ReproductorActivity extends AppCompatActivity {


    private TextView ArtistatextView, CanciontextView, AlbumtextView;
    private View reproductor;
    private Toolbar toolbar;
    private TextView TextView_toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        ArtistatextView = (TextView) findViewById(R.id.ArtistatextView);
        CanciontextView = (TextView) findViewById(R.id.CanciontextView);
        AlbumtextView = (TextView) findViewById(R.id.AlbumtextView);
        TextView_toolbar = (TextView) findViewById(R.id.TextView_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.getBackground().setAlpha(0);

        if(getSupportActionBar() != null){
           getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationIcon(R.drawable.rep_flechaizq);

        }

        Typeface Bold = Typeface.createFromAsset(getAssets(), "Montserrat-Bold.otf");
        Typeface Light = Typeface.createFromAsset(getAssets(), "Montserrat-Light.otf");
        ArtistatextView.setTypeface(Bold);
        CanciontextView.setTypeface(Bold);
        AlbumtextView.setTypeface(Light);
        TextView_toolbar.setTypeface(Bold);


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
            Toast.makeText(ReproductorActivity.this, "Regresar", Toast.LENGTH_SHORT).show();
        }
        if (res_id == R.id.action_buscar) {
            Toast.makeText(ReproductorActivity.this, "Buscar", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

}

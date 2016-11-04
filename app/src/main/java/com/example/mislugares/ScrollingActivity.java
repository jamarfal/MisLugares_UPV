package com.example.mislugares;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.mislugares.model.Lugares;
import com.example.mislugares.model.LugaresVector;

public class ScrollingActivity extends AppCompatActivity implements LocationListener {
    public static final String PLACE_ID = "place_id";
    public static Lugares lugares = new LugaresVector();
    private RecyclerView recyclerView;
    public PlaceAdapter placeAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private LocationManager manejador;
    private Location mejorLocaliz;
    private static final long DOS_MINUTOS = 2 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_scrolling);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        placeAdapter = new PlaceAdapter(lugares, this);
        recyclerView.setAdapter(placeAdapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        placeAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScrollingActivity.this, PlaceViewActivity.class);
                i.putExtra(PLACE_ID, (long) recyclerView.getChildAdapterPosition(v));
                startActivity(i);
            }
        });

        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            actualizaMejorLocaliz(manejador.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        }

        if (manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            actualizaMejorLocaliz(manejador.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        activarProveedores();
    }

    private void activarProveedores() {
        if (manejador.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            manejador.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20 * 1000, 5, this);
        }

        if (manejador.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            manejador.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 * 1000, 10, this);
        }
    }


    private void actualizaMejorLocaliz(Location localiz) {
        if (localiz != null && (mejorLocaliz == null
                || localiz.getAccuracy() < 2 * mejorLocaliz.getAccuracy()
                || localiz.getTime() - mejorLocaliz.getTime() > DOS_MINUTOS)) {
            Log.d(LugaresVector.TAG, "Nueva mejor localización");

            mejorLocaliz = localiz;
            LugaresVector.posicionActual.setLatitud(localiz.getLatitude());
            LugaresVector.posicionActual.setLongitud(localiz.getLongitude());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        manejador.removeUpdates(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.search_menu) {
            throwPlaceViewActivity(null);
            return true;
        }

        if (id == R.id.menu_mapa) {
            Intent intent = new Intent(this, MapaActivity.class);

            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void throwPlaceViewActivity(View view) {
        final EditText entrada = new EditText(this);
        entrada.setText("0");
        new AlertDialog.Builder(this)
                .setTitle("Selección de lugar")
                .setMessage("indica su id:")
                .setView(entrada)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        long id = Long.parseLong(entrada.getText().toString());
                        Intent i = new Intent(ScrollingActivity.this, PlaceViewActivity.class);
                        i.putExtra(PLACE_ID, id);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LugaresVector.TAG, "Nueva localización: " + location);
        actualizaMejorLocaliz(location);
    }

    @Override
    public void onProviderDisabled(String proveedor) {
        Log.d(LugaresVector.TAG, "Se deshabilita: " + proveedor);
        activarProveedores();

    }

    @Override
    public void onProviderEnabled(String proveedor) {
        Log.d(LugaresVector.TAG, "Se habilita: " + proveedor);
        activarProveedores();

    }

    @Override
    public void onStatusChanged(String proveedor, int estado, Bundle extras) {
        Log.d(LugaresVector.TAG, "Cambia estado: " + proveedor);
        activarProveedores();

    }
}

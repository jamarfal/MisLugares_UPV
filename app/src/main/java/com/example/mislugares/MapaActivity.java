package com.example.mislugares;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mislugares.model.GeoPunto;
import com.example.mislugares.model.Lugar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mapa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
        mapa.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setZoomControlsEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);
        }
        if (ScrollingActivity.lugares.tamanyo() > 0) {
            GeoPunto p = ScrollingActivity.lugares.elemento(0).getPosicion();
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(p.getLatitud(), p.getLongitud()), 12));
        }
        for (int n = 0; n < ScrollingActivity.lugares.tamanyo(); n++) {
            Lugar lugar = ScrollingActivity.lugares.elemento(n);
            GeoPunto p = lugar.getPosicion();
            if (p != null && p.getLatitud() != 0) {
                BitmapDrawable iconoDrawable = (BitmapDrawable) getResources()
                        .getDrawable(lugar.getTipo().getRecurso());
                Bitmap iGrande = iconoDrawable.getBitmap();
                Bitmap icono = Bitmap.createScaledBitmap(iGrande,
                        iGrande.getWidth() / 7, iGrande.getHeight() / 7, false);
                mapa.addMarker(new MarkerOptions()
                        .position(new LatLng(p.getLatitud(), p.getLongitud()))
                        .title(lugar.getNombre()).snippet(lugar.getDireccion())
                        .icon(BitmapDescriptorFactory.fromBitmap(icono)));
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        for (int id = 0; id < ScrollingActivity.lugares.tamanyo(); id++) {
            if (ScrollingActivity.lugares.elemento(id).getNombre()
                    .equals(marker.getTitle())) {
                Intent intent = new Intent(this, PlaceViewActivity.class);
                intent.putExtra("id", (long) id);
                startActivity(intent);

                break;
            }
        }
    }
}

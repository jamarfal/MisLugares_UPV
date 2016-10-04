package com.example.mislugares;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mislugares.model.Lugar;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by jamarfal on 3/10/16.
 */

public class PlaceViewActivity extends AppCompatActivity {
    private long id;
    private Lugar lugar;
    private ImageView imageView;
    final static int RESULTADO_EDITAR = 1;
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;
    private Uri uriFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_view_layout);

        Bundle extras = getIntent().getExtras();
        id = extras.getLong(ScrollingActivity.PLACE_ID, -1);
        lugar = ScrollingActivity.lugares.elemento((int) id);

        imageView = (ImageView) findViewById(R.id.foto);
        updateViews();
    }

    private void updateViews() {
        TextView nombre = (TextView) findViewById(R.id.place_name_text_view);
        nombre.setText(lugar.getNombre());

        ImageView logo_tipo = (ImageView) findViewById(R.id.logo_tipo);
        logo_tipo.setImageResource(lugar.getTipo().getRecurso());

        TextView tipo = (TextView) findViewById(R.id.type_text_view);
        tipo.setText(lugar.getTipo().getTexto());

        TextView direccion = (TextView) findViewById(R.id.address_text_view);

        if (direccion.getText().toString().isEmpty()) {
            direccion.setVisibility(View.GONE);
        } else {
            direccion.setText(lugar.getDireccion());
        }


        TextView telefono = (TextView) findViewById(R.id.phone_text_view);
        if (lugar.getTelefono() == 0) {
            telefono.setVisibility(View.GONE);
        } else {
            telefono.setText(Integer.toString(lugar.getTelefono()));
        }

        TextView url = (TextView) findViewById(R.id.map_url_text_view);
        if (url.getText().toString().isEmpty()) {
            url.setVisibility(View.GONE);
        } else {
            url.setText(lugar.getUrl());
        }

        TextView comentario = (TextView) findViewById(R.id.comment_text_view);
        if (comentario.getText().toString().isEmpty()) {
            comentario.setVisibility(View.GONE);
        } else {
            comentario.setText(lugar.getComentario());
        }


        TextView fecha = (TextView) findViewById(R.id.date_text_view);
        fecha.setText(DateFormat.getDateInstance().format(
                new Date(lugar.getFecha())));

        TextView hora = (TextView) findViewById(R.id.hour_text_view);
        hora.setText(DateFormat.getTimeInstance().format(
                new Date(lugar.getFecha())));

        RatingBar valoracion = (RatingBar) findViewById(R.id.valoracion);
        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar,
                                                float valor, boolean fromUser) {
                        lugar.setValoracion(valor);
                    }
                });

        ponerFoto(imageView, lugar.getFoto());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_action_menu:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        lugar.getNombre() + " - " + lugar.getUrl());
                startActivity(intent);
                return true;
            case R.id.how_go_action_menu:
                verMapa(null);
                return true;
            case R.id.edit_action_menu:
                goToEditPlaceActivity();
                return true;
            case R.id.delete_action_menu:
                deletePlace((int) id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULTADO_EDITAR) {
            updateViews();
            findViewById(R.id.scrollView1).invalidate();
        } else if (requestCode == RESULTADO_GALERIA
                && resultCode == Activity.RESULT_OK) {
            lugar.setFoto(data.getDataString());
            ponerFoto(imageView, lugar.getFoto());
        } else if (requestCode == RESULTADO_FOTO && resultCode == Activity.RESULT_OK && lugar != null && uriFoto != null) {
            lugar.setFoto(uriFoto.toString());
            ponerFoto(imageView, lugar.getFoto());
        }
    }

    public void verMapa(View view) {
        Uri uri;
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        if (lat != 0 || lon != 0) {
            uri = Uri.parse("geo:" + lat + "," + lon);
        } else {
            uri = Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void goToEditPlaceActivity() {
        Intent intent = new Intent(this, EditPlaceActivity.class);
        intent.putExtra(ScrollingActivity.PLACE_ID, id);
        startActivityForResult(intent, RESULTADO_EDITAR);
    }

    public void deletePlace(final int id) {
        new AlertDialog.Builder(this)
                .setTitle("Borrado de Lugar")
                .setMessage("¿Estás seguro que quieres eliminar este lugar?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ScrollingActivity.lugares.borrar(id);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void llamadaTelefono(View view) {
        startActivity(new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + lugar.getTelefono())));
    }

    public void pgWeb(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(lugar.getUrl())));
    }

    public void galeria(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULTADO_GALERIA);
    }

    protected void ponerFoto(ImageView imageView, String uri) {
        if (uri != null) {
            imageView.setImageBitmap(reduceBitmap(this, uri, 1024, 1024));
        } else {
            imageView.setImageBitmap(null); // No se representa ninguna imagen
        }
    }

    public void tomarFoto(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        uriFoto = Uri.fromFile(new File(
                Environment.getExternalStorageDirectory() + File.separator
                        + "img_" + (System.currentTimeMillis() / 1000) + ".jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(intent, RESULTADO_FOTO);
    }

    public void eliminarFoto(View view) {
        lugar.setFoto(null);
        ponerFoto(imageView, null);
    }

    public static Bitmap reduceBitmap(Context contexto, String uri, int maxAncho, int maxAlto) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contexto.getContentResolver()
                    .openInputStream(Uri.parse(uri)), null, options);
            options.inSampleSize = (int) Math.max(
                    Math.ceil(options.outWidth / maxAncho),
                    Math.ceil(options.outHeight / maxAlto));
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(contexto.getContentResolver().openInputStream(Uri.parse(uri)), null, options);
        } catch (FileNotFoundException e) {
            Toast.makeText(contexto, "Fichero/recurso no encontrado",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return null;
        }
    }
}
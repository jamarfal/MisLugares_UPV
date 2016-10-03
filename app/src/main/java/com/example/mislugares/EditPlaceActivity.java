package com.example.mislugares;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mislugares.model.Lugar;
import com.example.mislugares.model.TipoLugar;

import java.text.DateFormat;
import java.util.Date;

public class EditPlaceActivity extends AppCompatActivity {

    private long id;
    private Lugar lugar;
    private EditText name;
    private Spinner type;
    private EditText address;
    private EditText phone;
    private EditText url;
    private EditText comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_place);

        Bundle extras = getIntent().getExtras();
        id = extras.getLong(ScrollingActivity.PLACE_ID, -1);
        lugar = ScrollingActivity.lugares.elemento((int) id);

        name = (EditText) findViewById(R.id.name_edit_text);
        name.setText(lugar.getNombre());

        address = (EditText) findViewById(R.id.address_edit_text);
        address.setText(lugar.getDireccion());

//        phone = (EditText) findViewById(R.id.phone_number_edit_text);
//        phone.setText(lugar.getTelefono());

        url = (EditText) findViewById(R.id.url_edit_text);
        url.setText(lugar.getUrl());

        comment = (EditText) findViewById(R.id.comment_edit_text);
        comment.setText(lugar.getComentario());

        type = (Spinner) findViewById(R.id.type_spinner);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TipoLugar.getNombres());
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adaptador);
        type.setSelection(lugar.getTipo().ordinal());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_place_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel_edit_menu_button:
                return true;
            case R.id.save_edit_menu_button:
                lugar.setNombre(name.getText().toString());
                lugar.setTipo(TipoLugar.values()[type.getSelectedItemPosition()]);
                lugar.setDireccion(address.getText().toString());
//                lugar.setTelefono(Integer.parseInt(phone.getText().toString()));
                lugar.setUrl(url.getText().toString());
                lugar.setComentario(comment.getText().toString());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

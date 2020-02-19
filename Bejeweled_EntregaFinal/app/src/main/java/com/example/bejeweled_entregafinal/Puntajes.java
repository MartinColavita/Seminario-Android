package com.example.bejeweled_entregafinal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import java.lang.reflect.Array;


public class Puntajes extends AppCompatActivity {

    public static final Integer[] Vnomtext = {R.id.nom1, R.id.nom2, R.id.nom3, R.id.nom4, R.id.nom5, R.id.nom6, R.id.nom7, R.id.nom8, R.id.nom9, R.id.nom10};
    public static final Integer[] Vidpts = {R.id.pnts1, R.id.pnts2, R.id.pnts3, R.id.pnts4, R.id.pnts5, R.id.pnts6, R.id.pnts7, R.id.pnts8, R.id.pnts9, R.id.pnts10};

    TextView texnom;
    TextView texpts;
    int n;
    int pts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntajes);

        mostrarTabla();
    }


    // ___________________________________  MENU   __________________________________//
    //metodo para mostrar y ocultar el menu
    public boolean onCreateOptionsMenu(Menu m) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu, m);

        return super.onCreateOptionsMenu(m); // true indica q debe visualizarse
    }

    //metodo para asignar las opciones correspondientes a las opciones
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.id_menuCompartir) {

            // mandar un intent impliciti con las app q quiero q se comparta
            Intent intent = new Intent();
            // se establece la accion del intent
            intent.setAction(Intent.ACTION_SEND);
            // se establece el tipo de MIME de los datos q se enviaran
            intent.setType("text/plain");

            intent.putExtra(Intent.EXTRA_TEXT, generarCadena());

            //se verifica q exista alguna activity en el sistema q pueda resolver el intent antes de iniciarla, de lo contrario starActivity() fallara.
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }


    // _______________________________ ActionBar (flecha hacia atras )______________________//
    public boolean onSupportNavigateUp() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            // muestra el boton en el action bar
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //al presionar la flecha llama a este metodo y aparece el cuadro de dialogo . Y va hacia la actividad padre
        onBackPressed();

        return false;
    }

    public void onBackPressed() {
     /*   // crear el alerta de dialogo de esta actividad
        AlertDialog.Builder builder = new AlertDialog.Builder(Puntajes.this);

        //setear el titulo con los recursos strings q tengo guardado
        builder.setTitle(getResources().getString(R.string.volver));

        //setear msj q queres aparece en el dialogo
        builder.setMessage(getResources().getString(R.string.deseaMenuPcipal))

                //seteo el boton de si (cuando se aprieta va al main)
                .setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {*/
        // vuelve a la clase padre
        startActivity(getParentActivityIntent());
        finish();

/*                    }
                })
                //setea el boton de No ( cuando lo aprieta se queda en la activity)
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //descarta este cuadro de dialogo y lo elimina de la pantalla
                        dialogInterface.dismiss();
                    }
                })
                //cuando apretas afuera del cuadro de dialogo no pasa nada (sin esto se va el cuadro)
                .setCancelable(false)
                .show();*/
    }


    //___________________________________JSON______________________________________________//

    public void mostrarTabla() {
        String p;
        String nomJson ;

        String cadena = leerPreferencias();
        if (cadena != null) {
            try {
                // creo el json en esta activity
            final JSONArray jsonArray = new JSONArray(cadena);

            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject json = jsonArray.getJSONObject(i);

                //Recuperamos la cadena json
                nomJson =  json.getString("nombreJugador");
                p = json.getString("puntos");

                seteoJson(nomJson,p,i);
            }

        } catch (JSONException error) { }

        }

    }

    //_______________________________ SETEO LOS TEXT VIEW DE NOMBRE Y PUNTAJE_____________________________________________//
    public void seteoJson (String nomJson, String p, int k){
        //___________________________________ Setea nombre y puntaje ____________________________//
            //posicion del arreglo (q tine cada id) y lo instancio
            n = Vnomtext[k];
            texnom = findViewById(n);

            // seteo el texto , con el json con el string del nombre
            texnom.setText(nomJson);


            //posicion del arreglo (q tine cada id) y lo instancio
            pts = Vidpts[k];
            texpts = findViewById(pts);

            // seteo el texto , con el json con el string del nombre
            texpts.setText(p);

    }


    //_________________________________Leer SHARED PREFERENCE __________________________________________//
     /* Lee el JSON  */
    private String leerPreferencias(){
        SharedPreferences preferencias = getSharedPreferences("ranking",MODE_PRIVATE);
        String cadenaNombre = preferencias.getString("puntajes",null);
        return cadenaNombre;
    }

    //____________________________________Generar_Cadena________________________________
    public String generarCadena(){
        SharedPreferences preferencias = getSharedPreferences("ranking", MODE_PRIVATE);

        String cadenaJson = preferencias.getString("puntajes",null);

        String cadena ="Mi TOP 10 de Bejeweled: \n";

        try{

            JSONArray jsonArray = new JSONArray(cadenaJson);

            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                cadena= cadena+jsonObject.getString("nombreJugador")+":"+jsonObject.getInt("puntos")+"\n";
            }


        }catch (JSONException error){}

        return cadena;

    }





    //________________________________ FIN ___________________________________________//
}

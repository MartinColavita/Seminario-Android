package com.example.bejeweled_entregafinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    //____________________________ ON CREATE _________________________________________________//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    //______________________________________ BTN JUGAR ______________________________________________//
    // Al seleccionar el boton va a la activity del juego
    public void jugarBoton (View v){
        Intent intJugar = new Intent(this, Juego.class);
        startActivity(intJugar);
    }


    //______________________________________ BTN PUNTAJES ______________________________________________//
    // Al seleccionar el boton va a la activity de los puntajes
    public void rankingBoton (View v){
        Intent intRanking = new Intent(this, Puntajes.class);
        startActivity(intRanking);
    }


    //______________________________________ BTN SALIR ______________________________________________//
    // Termina de ejecutar el juego
    public void salirBoton (View v){
        finish();

    }



   //____________________________________ FIN __________________________________________//
}

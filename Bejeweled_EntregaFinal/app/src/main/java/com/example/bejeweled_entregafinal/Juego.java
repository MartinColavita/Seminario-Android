package com.example.bejeweled_entregafinal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Juego extends AppCompatActivity {

    private static final Integer[] Vimagenes = {R.drawable.blue, R.drawable.green, R.drawable.orange, R.drawable.purple, R.drawable.red, R.drawable.yellow};
    private static final Random random = new Random();
    private int[][] matriz = new int[8][8];
    private List<Pair<Integer, Integer>> juegos = new ArrayList<>();
    private ImageView img;
    private GridLayout tabla;
    private int num;
    private int n;
    private String tag;
    private TextView textoP;
    private List<Integer> listaResultados;
    private int puntos;
    private boolean estaEnRanking=true;
    private boolean banderaClicks=true;
    boolean hayJuego = false;
    boolean entro = false;
    boolean entro2 = false;

    //__________________________________ ON CREATE ____________________________________________//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        //
        SharedPreferences preferencias = getSharedPreferences("ranking", MODE_PRIVATE);
        String cadenaJson = preferencias.getString("puntajes",null);
        if ( cadenaJson == null){
            crearVacio();
        }


        //instancio el onclick del boton terminar
        findViewById(R.id.terminarID_BTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terminarBoton ();
            }
        });


        //instancio el id del grid layout
        tabla = (GridLayout) findViewById(R.id.tablero);

        //instancio el id del TEXVIEW  (los puntos)
        textoP = (TextView)findViewById(R.id.textoId);

        //recorro la matriz para crearla aletoriamente
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //hace el random del arreglo (de imagenes)
                num = random.nextInt(Vimagenes.length);

                //Obtengo la posicion del objeto drawable y lo asigno a n
                n = Vimagenes[num];

                // seteo la posicion del textview
                matriz[i][j] = num;

                //obtengo el imagview para modificarlo - y asignamos la imagen (obtenemos el numero con la formula de fila y columna)
                img = (ImageView) tabla.getChildAt(i * 8 + j);
                img.setImageResource(n);


                // creamos un objeto par , q contiene los indices de la cordenadas
                Pair<Integer, Integer> tag = new Pair<>(i, j);

                // seteamos  el tag  (guarda en forma de entero las coordenadas de las gemas)
                img.setTag(tag);
            }
        }

        //los inicializo en -1 para invocar a el buscar match y que sepa que no es invocado por gemas clickeadas
        primerGema = new Pair<>(-1, -1);
        segundaGema = new Pair<>(-1, -1);

        //verifico que no se hayan creado juegos asi inicia desde cero
        int aux = buscarMatch(primerGema, segundaGema,true);

        //mientras que devuelva -1 osea qeu se forma un juego en la matriz actualizo el tablero de nuevo y pregunto
        while (aux == -1) {
            //si hubo juego relleno los huecos con gemas aleatorias
            rellenarHuecos();
            //llamo de nuvo a buscar match;
            aux = buscarMatch(primerGema, segundaGema,true);
        }

        // inicializo el contador de puntos en 0, desp de q tengo el tablero limpio
        puntos=0;

        //actualizo el tablero
        actualizarTablero();


        //seteo el boton refrescar en deshabilitado
        Button botonrefrescar = (Button) findViewById(R.id.refrescarID_BTN);
        botonrefrescar.setEnabled(false);

        //generarTableroSinJuegos();

    }


    //_____________________________________Reocrrer horizonal____________________//
    public void recorrerHorizontal(int i, int j, Pair<Integer, Integer> gema1, Pair<Integer, Integer> gema2) {
        int c = j;
        int contJuegos = 0;
        int result = 0;

        // pongo un auxiliar a la columna , para q no se pise con la original, y recorro toda la fila
        while ((c < 7) && (matriz[i][c] != -1) && (matriz[i][c] == matriz[i][c + 1])) {
            contJuegos++;
            c++;
        }
        // si hay 3 coincidencias o mas horizontales . se para en esa posicion y le pone en -1.
        if (contJuegos >= 2) {
            result = -1;
            for (int x = 0; x <= contJuegos; x++) {
                //creo el pair con las coord de coincidencia
                Pair<Integer, Integer> coord = new Pair<>(i, j + x);
                // agrego a la lista el objeto par con las coordenadas de los juegos
                juegos.add(coord);
                //si se forma juego en alguno de los cliks devuelve 1
                if (gema1.first != -1) {
                    if ((matriz[i][j + x] == matriz[gema1.first][gema1.second]) || (matriz[i][j + x] == matriz[gema2.first][gema2.second]))
                        result = 1;
                }
            }
        }

        if (result != 0) {
            listaResultados.add(result);
        }

    }

    //_________________________________Recorrer Vertical____________________________//
    public void recorrerVertical(int i, int j, Pair<Integer, Integer> gema1, Pair<Integer, Integer> gema2) {
        int f = i;
        int contJuegos = 0;
        int result = 0;

        // pongo un auxiliar a la fila , para q no se pise con la original, y recorro toda la columna
        while ((f < 7) && (matriz[f][j] != -1) && (matriz[f][j] == matriz[f + 1][j])) {
            contJuegos++;
            f++;
        }
        // si hay 3 coincidencias o mas Verticales . se para en esa posicion y le pone en -1.
        if (contJuegos >= 2) {
            //si formo juego no importa como le asigno -1;
            result = -1;
            for (int x = 0; x <= contJuegos; x++) {

                //creo el pair con las coord de coincidencia
                Pair<Integer, Integer> coord = new Pair<>(i + x, j);

                // agrego a la lista el objeto par con las coordenadas de los juegos
                juegos.add(coord);

                //si se forma juego en alguno de los cliks devuelve 1
                int aux = gema1.first;
                if (aux != -1) {
                    if ((matriz[i + x][j] == matriz[gema1.first][gema1.second]) || (matriz[i + x][j] == matriz[gema2.first][gema2.second]))
                        result = 1;
                }
            }
        }
        //si encontro juego agrego a una lista cual es el estado( 1 si forma juego con las gemas clickeadas, -1 si forma en cualquier lado)
        listaResultados.add(result);
    }


    // ___________________________ BUSCAR MATCH _____________________________________________//
    // devuelve 0 si no encontro juego, 1 si encontro juego con las gemas clickeadas y -1 si encontro juego de cualquier forma
    public int buscarMatch(Pair<Integer, Integer> gema1, Pair<Integer, Integer> gema2, boolean sumarPuntos) {
        int resultH;
        int resultV;
        boolean juego = false, noHayJuegos = false, hayJuegoClick = false;
        int result = 0;//lo inicio en 0 como si no encontrara juego
        juegos = new ArrayList<>();
        listaResultados = new ArrayList<>();

        // recorro la matriz
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                recorrerHorizontal(i, j, gema1, gema2);     // guardo en una variable el  estado del metodo! Y
                recorrerVertical(i, j, gema1, gema2);       // si se formo juego devuelvo true.

            }
        }

        //recorre el array list  de resultados
        for (int x = 0; x < listaResultados.size(); x++) {
            if (listaResultados.get(x) == 0) {
                noHayJuegos = true;
            } else {
                noHayJuegos = false;
            }
            if (listaResultados.get(x) == 1) {
                hayJuegoClick = true;
            }
            if (listaResultados.get(x) == -1) {
                juego = true;
            }
        }

        //seteo los resultados
        if (noHayJuegos == true) {
            result = 0;
        }
        if (juego) {
            result = -1;
        }
        if (hayJuegoClick) {
            result = 1;
        }

        if (sumarPuntos) {
            //recorro la lista de objetos pares que contienen las coordenadas de los juegos, para ponerlo en -1
            for (int k = 0; k < juegos.size(); k++) {

                //si la gema es distinta a -1 entonces sumo punto, esto lo hago porque en juegos L o T suma el doble de puntos
                if (matriz[juegos.get(k).first][juegos.get(k).second] != -1) {
                    puntos++;
                }

                matriz[juegos.get(k).first][juegos.get(k).second] = -1;

                //a las gemas que forman juego las dejo destacadas como seleccionadas
                img = (ImageView) tabla.getChildAt(juegos.get(k).first * 8 + juegos.get(k).second);
                img.setSelected(true);
            }
        }

        //restablezco los array list
        listaResultados.clear();
        juegos.clear();

        return result;
    }

    //______________________________RELENAR HUECOS___________________________________________________//
    //rellena los huecos en la matriz ( los -1 osea gemas eliminadas) por una gema aleatoria
    public void rellenarHuecos() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (matriz[i][j] == -1) {
                    int aux = random.nextInt(Vimagenes.length);
                    matriz[i][j] = aux;
                }
            }
        }
    }


    //_________________________________ACTUALIZAR TABLERO _________________________________________//
    Handler handlerColor = new Handler();
    public void actualizarTablero() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                // obtengo  objeto  imagview en la posicion indicada
                img = (ImageView) tabla.getChildAt(i * 8 + j);
                img.setSelected(false);

                //seteo que sea visible en caso de que se haya cambiado antes
                img.setVisibility(View.VISIBLE);

                switch (matriz[i][j]) {
                    //si es -1 entonces desaparece
                    case -1:
                        // seteo para q se borren las gemas
                      //  img.setVisibility(View.GONE); // esto lo sacamos asi no quedan los huecos en la cascada!!!

                        // setea el contador de puntajes
                        textoP.setText(String.valueOf(puntos));
                        break;

                    //si es 0 lo pongo azul
                    case 0:
                        img.setImageResource(Vimagenes[0]);
                        break;

                    // si es 1 lo pongo verde
                    case 1:
                        img.setImageResource(Vimagenes[1]);
                        break;

                    // si es 2 lo pongo naranja
                    case 2:
                        img.setImageResource(Vimagenes[2]);
                        break;

                    // si es3 lo pongo purpura
                    case 3:
                        img.setImageResource(Vimagenes[3]);
                        break;

                    //si es 4 lo pongo rojo
                    case 4:
                        img.setImageResource(Vimagenes[4]);
                        break;

                    //si es 5 lo pongo amarillo
                    case 5:
                        img.setImageResource(Vimagenes[5]);
                        break;
                }
            }
        }
    }


    //________________________________________MANEJADOR DE CLIKS ______________________________________//
    int contadorDeClicks = 0;
    Pair<Integer, Integer> primerGema;
    Pair<Integer, Integer> segundaGema;

    public void manejador(View view) {

        if (banderaClicks){

            // contador por cada click
            contadorDeClicks++;

            if (contadorDeClicks == 1) {
                //guardar posicion de la primer gema que se selecciono
                primerGema = (Pair<Integer, Integer>) view.getTag();

                //asigno la imagen y la pongo como seleccionada
                img=(ImageView) tabla.getChildAt(primerGema.first*8+primerGema.second);
                img.setSelected(true);

            } else {
                if (contadorDeClicks == 2) {
                    //guardo la posicion de la segunda gema
                    segundaGema = (Pair<Integer, Integer>) view.getTag();

                    //guardo los estados de la gema para que en caso de no producirse juego no realizar el cambio
                    int primerEstado = matriz[primerGema.first][primerGema.second];
                    int segundoEstado = matriz[segundaGema.first][segundaGema.second];

                    //  HACE EL SWAP!  - cambia de posicion con la primera
                    matriz[primerGema.first][primerGema.second] = segundoEstado;
                    matriz[segundaGema.first][segundaGema.second] = primerEstado;

                    actualizarTablero();

                    //si no forman juego(si devuelve cualquier numero que no sea 1) entonces vuelvo a colocarlos como estaba, sino actualizo el tablero
                    int aux = buscarMatch(primerGema, segundaGema,true);
                    img=(ImageView) tabla.getChildAt(primerGema.first*8+primerGema.second);
                    if (aux != 1) {

                        Toast.makeText(getApplicationContext(), "CAMBIO INCORRECTO", Toast.LENGTH_SHORT).show();  //avisa q el movimiento no se realizo por movimiento incorrecto
                        //no muestra la gemma vecina con color (de coincidencia)
                        img.setSelected(false);

                        matriz[primerGema.first][primerGema.second] = primerEstado;
                        matriz[segundaGema.first][segundaGema.second] = segundoEstado;

                        actualizarTablero();


                        //* llama al metodo q rrecorre  el tablero y verifica q nop queden mas juegos por hacer */
                        if(!verificarPosiblesJuegos()) {
                            //pone visible el btn refrescar
                            Button botonrefrescar = (Button) findViewById(R.id.refrescarID_BTN);
                            botonrefrescar.setEnabled(true);
                            Toast.makeText(getApplicationContext(),"NO HAY JUEGOS",Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        //seteamos los btn
                        Button botonTerminar =(Button) findViewById(R.id.terminarID_BTN);
                        botonTerminar.setEnabled(false);


                        // bandera q indica cuando terminas de tocar el tablero espere, hasta q haga los juegos
                        banderaClicks=false;

                        // Invoca el Handler
                        esperar(primerGema);

                    }
                    // restablezco el contador de click, para volver a comparar los siguientes..
                    contadorDeClicks = 0;
                }
            }
        }

        else {
            Toast.makeText(getApplicationContext(),"Espere q termine la cascada",Toast.LENGTH_SHORT).show();
        }

    }


    //_________________________________________ CASCADA ____________________________________________________________//
    // array list de gemas eliminadas!!
    List<Pair<Integer, Integer>> listaDeGemasEliminadas = new ArrayList<>();

    //recorro una columna buscando si hay gema eliminada si hay la subo hasta arriba; recibe la columna en la q recorrer
    public void cascadaColumna() {
        int columna = 0;

        while (columna < 8) {
            int fila = 7;
            boolean encontro = false;
            Pair<Integer, Integer> pos = new Pair<>(fila, columna);
            List<Pair<Integer, Integer>> listaDeGemasEliminadas = new ArrayList<>();

            while (fila > 0) {
                if (matriz[fila][columna] == -1) {
                    pos = new Pair<>(fila, columna);
                    listaDeGemasEliminadas.add(pos);
                    encontro = true;

                }
                fila--;
            }

            if (encontro) {
                //recorro la lista de gemas eliminadas para
                for (int i = 0; i < listaDeGemasEliminadas.size(); i++) {
                    fila = listaDeGemasEliminadas.get(i).first;
                    columna = listaDeGemasEliminadas.get(i).second;
                    pos = listaDeGemasEliminadas.get(i);

                    while (fila != 0) {
                        // si la gema que esta arriba no es una eliminada entonces entro
                        if (matriz[fila - 1][columna] != -1) {
                            //a la primer gema a eliminar que encontro antes lo cambio de posicion por la otra gema que si tiene un estado activo
                            matriz[pos.first][pos.second] = matriz[fila - 1][columna];
                            matriz[fila - 1][columna] = -1;

                            //actualizo la posicion de la gema a eliminar
                            pos = new Pair<>(fila - 1, columna);
                        }
                        fila--;
                    }
                }
                //dejo vacia la lista  de gemas a eliminar
                listaDeGemasEliminadas.clear();
            }
            columna++;
        }
    }


    //__________________________________________________HANDLER ________________________________________________//
    boolean primeraVez;
    public void esperar (Pair<Integer,Integer> pos){
        // INICIALIZO clase handler
        final Handler handler = new Handler();

        //variable para avisar al handler que entro por primera vez y poder poner en false el selected de la gema
        primeraVez=true;

        final Pair<Integer,Integer> posInicial =pos;


        handler.postDelayed(new Runnable() {
            Pair<Integer,Integer> primerGema= new Pair<>(-1,-1);
            Pair<Integer,Integer> segundaGema= new Pair<>(-1,-1);

            //la primera vez que lo invoque estara en false para avisar que entra por primera vez
            boolean condicionCascada=false;

            ImageView imgAux;

            @Override
            public void run() {

                //si es la primera vez que entra entonces tengo que  poner en false el selected
                if (primeraVez){
                    imgAux = (ImageView)tabla.getChildAt(posInicial.first*8+posInicial.second);
                    imgAux.setSelected(false);
                }
                //muestro el hueco de el juego
                actualizarTablero();

                if (!condicionCascada){
                    //subo los juegos arriba
                    Juego.this.cascadaColumna();
                    //y lo muestro
                    Juego.this.actualizarTablero();
                    //le asigno una nueva gema
                }
                else {
                    //coloco gemas nuevas
                    Juego.this.rellenarHuecos();
                    //y lo muestro
                    Juego.this.actualizarTablero();

                }
                //si se formaron juegos en la actualizacion llamo inicia el bucle del handler
                if (condicionCascada==false){
                    //asigno true para que rellene huecos y actualize
                    condicionCascada=true;
                    //asigno false porque no es primera vez
                    primeraVez=false;
                    //invoco al handler de nuevo
                    handler.postDelayed(this , 350);

                }
                else {//sino entro a ver si no se formaron juegos durante la cascada

                    //asigno false porque no es primera vez
                    primeraVez=false;

                    verificarPosiblesJuegos();

                    // busca si hay juegos en el tablero, si da -1 es verdad
                    if (Juego.this.buscarMatch(primerGema,segundaGema,true) ==  -1){
                        //coloco en flase para que entre a realizar la cascada en la otra iteracion
                        condicionCascada = false;

                        //invoco el handler
                        handler.postDelayed(this , 350);
                    }
                    else{

                        //bandera para cuando se esta ejecutando juego no pueda tocar la pantalla
                        banderaClicks=true;

                        //mientras esta la cascada q no se vea el btn terminar
                        Button botonTerminar =(Button) findViewById(R.id.terminarID_BTN);
                        botonTerminar.setEnabled(true);

                        //* llama al metodo q rrecorre  el tablero y verifica q nop queden mas juegos por hacer */
                        if(!verificarPosiblesJuegos()) {
                            //pone visible el btn refrescar
                            Button botonrefrescar = (Button) findViewById(R.id.refrescarID_BTN);
                            botonrefrescar.setEnabled(true);
                            Toast.makeText(getApplicationContext(),"NO HAY JUEGOS",Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            }
        },350);


    }


    //___________________________ BTN REFRESCAR ________________________________________//
    /* Si no se pueden hacer mas juegos, mezcla el tablero nuevamente (sin hacer un nuevo random, con las gemas que hay!)  */
    public void refrescarBoton(View view){
            mezclarGemas();
            actualizarTablero();

            //vuelve a poner deshabilitado el boton
            Button botonrefrescar = (Button) findViewById(R.id.refrescarID_BTN);
            botonrefrescar.setEnabled(false);
    }


    //metodo que verifica si queda un tablero sin juegos posibles devuelve true si todavia hay posibles juegos, false si no hay mas
    //_____________________________________________VERIFICAR_____________________//
    public boolean verificarPosiblesJuegos(){
        int aux = 0;
        int auxAdyacente = 0;
        int i = 0;
        int resu;
        int j = 0;
        Pair <Integer,Integer> gemma1 = new Pair<>(-1, -1);
        Pair <Integer,Integer> gemma2 = new Pair<>(-1, -1);
        while ((i < 8) && (hayJuego == false)) {
            while ((j < 8) && (hayJuego == false)) {
                //solo hago el cambio horizontal si no es 7
                if (j < 7) {
                    entro = true;
                    //hago el cambio horizontal
                    aux = matriz[i][j];
                    auxAdyacente = matriz[i][j + 1];
                    matriz[i][j] = auxAdyacente;
                    matriz[i][j + 1] = aux;
                }
                if (entro) {
                    //pregunto si forma juego con alguna de las gemas que se movieron
                    resu = buscarMatch(gemma1,gemma2,false);
                    if (resu == -1) {
                        hayJuego = true;
                    }
                    //coloco las gemas como estaban
                    matriz[i][j] = aux;
                    matriz[i][j + 1] = auxAdyacente;
                    entro = false;
                }

                // hago el cambio vertical
                if (i < 7) {
                    entro2 = true;
                    //realixo el cambio vertical
                    aux= matriz[i][j];
                    auxAdyacente = matriz[i + 1][j];
                    matriz[i][j] = auxAdyacente;
                    matriz[i + 1][j] = aux;
                }
                if (entro2) {
                    //pregunto si se forma juego
                    resu=buscarMatch(gemma1,gemma2,false);
                    if (resu == -1) {
                        hayJuego = true;
                    }
                    matriz[i][j] = aux;
                    matriz[i + 1][j] = auxAdyacente;
                    entro2 = false;
                }
                //avanzo en las columnas
                j++;
            }
            //avanzo en las filas
            i++;
        }
        return hayJuego;
    }


    //___________________________________________MEZCLAR LAS GEMAS_______________________________________________//
    public void mezclarGemas(){
        int pntsAux=puntos;
        int aux;
        int c,f;
        boolean condicion=false;
        int num=7;
        //mezcla las gemas
        while (!condicion){
            for(int i=0;i<7;i++){
                for(int j=0;j<7;j++){
                    c=random.nextInt(matriz.length);
                    f=random.nextInt(matriz.length);
                    aux=matriz[i][j];
                    matriz[i][j]= matriz[c][f];
                    matriz[c][f]= aux;
                }
            }
            Pair <Integer,Integer> gemma1 = new Pair<>(-1, -1);
            Pair <Integer,Integer> gemma2 = new Pair<>(-1, -1);
            int hayJuego =buscarMatch(gemma1,gemma2,false);
            if (hayJuego==-1){
                condicion=false;
            }
            else{
                condicion=true;
                if (verificarPosiblesJuegos()){
                    condicion=true;
                }
                else{
                    condicion=false;
                }
            }

        }
    }


    //________________________________________________GENERA EL TABLERO SIN JUEGOS (PARA PROBAR EL REFRESCAR)_____________________________//
    /*public void generarTableroSinJuegos(){
        int valor=0;
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                if (valor==6){
                    valor=0;
                }
                matriz[i][j]=valor;
                valor++;
            }
        }
        actualizarTablero();
    }*/


    //____________________________ BTN TERMINAR __________________________________________//
    /* Termina el juego, y pregunta si deseas agregar tu nombre a los puntajes (si el puntaje esta entre los 10 mas altos) */
    public void terminarBoton () {

        final ActionBar actionBar = getSupportActionBar();

        //crea intent para enviar el jason
        final Intent intentJson = new Intent(this, Puntajes.class);

        // crea el intent para ir a la activity puntos
        final Intent intentfin = new Intent(this,Puntajes.class);

        // crear el alerta de dialogo de esta actividad
        AlertDialog.Builder builder = new AlertDialog.Builder(Juego.this);

        //setear el titulo con los recursos strings q tengo guardado
        builder.setTitle(getResources().getString(R.string.terminarJuego));
        //setear msj q queres aparece en el dialogo
        builder.setMessage(getResources().getString(R.string.deseaTerminarJuego))

                /////// BTN "SI" (cuando se aprieta va al main)  ///////
                .setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        estaEnRanking=entraAlRanking();

                        if (estaEnRanking){
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(Juego.this);

                            LayoutInflater inflater = getLayoutInflater();

                            View view = inflater.inflate(R.layout.cuadro_dialogo,null);

                            builder2.setView(view);

                            //setear el titulo con los recursos strings q tengo guardado
                            builder2.setTitle(getResources().getString(R.string.felicidades));

                            //setear msj q queres aparece en el dialogo
                            builder2.setMessage(getResources().getString(R.string.cadenaFelicidades));

                            final EditText txtnom = view.findViewById(R.id.text_dialog_nombre);
                            builder2.setView(view);

                            ///// BTN "ACEPTAR"  (envia el json y con el shared preferences guarda el nombre ingresado)
                            builder2.setPositiveButton(getResources().getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    String nom = txtnom.getText().toString();
                                    recorrerJson(nom);

                                    //envia a la actividad puntajes el json
                                    startActivity(intentJson);

                                    finish();
                                }
                            })
                                    /////// BTN "no figurar" (va a la activity puntos)
                                    .setNegativeButton(getResources().getString(R.string.nofigurar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(intentfin);

                                            finish();
                                        }
                                    })
                                    .setCancelable(false);
                            final AlertDialog dialog2 = builder2.create();
                            dialog2.show();
                        }

                        // si no esta en el top 10 va a la activity puntos
                        else {
                            startActivity(intentfin);

                            finish();
                        }
                    }
                })
                ////// BTN "NO " queda en la activity  ////////////
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //descarta este cuadro de dialogo y lo elimina de la pantalla
                        dialogInterface.dismiss();
                    }
                })
                //cuando apretas afuera del cuadro de dialogo no pasa nada (sin esto se va el cuadro)
                .setCancelable(false)
                .show();
    }


    //________________________________________ ORDENAR ARREGLO JSON____________________________//
/*Este método revisa y arregla el orden de cada elemento del arreglo comparándolo con el siguiente.
El proceso se repite varias veces hasta que se hayan verificado todos los elementos*/
    public void ordenarArregloJson (JSONArray jsonArray){
        //int a = jsonArray.length(); // dimension del arreglo
        for (int x = 0; x < jsonArray.length(); x++) {
            for (int i = 0; i < jsonArray.length()-1; i++) {

                try{
                    //guardo el puntaje de el user en la posicion i
                    JSONObject user = jsonArray.getJSONObject(i);
                    int puntajeActual=user.getInt("puntos");
                    //guardo el puntaje del sig
                    user =jsonArray.getJSONObject(i+1);
                    int puntajeSig = user.getInt("puntos");

                    if (puntajeActual<puntajeSig){
                        //guardo el siguiente para no perderlo al realizar el intercambio
                        JSONObject aux = jsonArray.getJSONObject(i+1);
                        jsonArray.put(i+1,jsonArray.getJSONObject(i));
                        jsonArray.put(i,aux);
                    }

                }catch (JSONException error){}

            }
        }
    }


//______________________________________-- CREA EL ARREGLO VACIO DEL JSON ________________________________________//

    public void  crearVacio (){
        //-------- JSON ARRAY -----//
        try{
            final JSONArray jsonArray = new JSONArray();
            final JSONObject json2 = new JSONObject();

            int  p = 0;
            String nomj = " - ";

            for (int k=0; k < 10; k++) {
                JSONObject json = new JSONObject();

                json.put("puntos", p);
                json.put("nombreJugador", nomj);

                jsonArray.put(k,json);
            }

            //ordenarArreglojson();
            guardarPreferencias(jsonArray.toString());

        }catch (JSONException error){ }
    }

    public boolean entraAlRanking(){
        boolean estado=false;

        SharedPreferences preferencias = getSharedPreferences("ranking", MODE_PRIVATE);

        String cadenaJson = preferencias.getString("puntajes",null);

        try{
            //creo el json array a partir de la cadenaJson obtenida del sharedPref
            JSONArray jsonArray = new JSONArray(cadenaJson);
            //obtengo el objeto json de la posicion 9 y se la asigno a jsonObject
            JSONObject jsonObject = jsonArray.getJSONObject(9);
            //obtengo el int con la clave puntos
            int menorPuntaje = jsonObject.getInt("puntos");

            //comparo si el puntaje actual entra en el juego
            if (puntos>menorPuntaje){
                estado=true;
            }
            else{
                estado=false;
            }
        }catch(JSONException error){}


        return estado;
    }

    public void recorrerJson(String nombre){
        SharedPreferences preferencias = getSharedPreferences("ranking", MODE_PRIVATE);

        String cadenaJson = preferencias.getString("puntajes",null);

        try{
            //creo el json array a partir de la cadenaJson obtenida del sharedPref
            JSONArray jsonArray = new JSONArray(cadenaJson);

            //creo objeto con la info actual
            JSONObject user = new JSONObject();
            user.put("puntos", puntos);
            user.put("nombreJugador",nombre);

            //lo guardo al final de json Array
            jsonArray.put(10,user);

            //ordeno el arreglo
            ordenarArregloJson(jsonArray);

            //actualizo el shared asi guardo los mejores 10
            actualizarShared(jsonArray);

        }catch (JSONException error){}



    }

    public void actualizarShared(JSONArray jsonArray){

        try{
            JSONArray arregloAux = new JSONArray();
            //guardo los 10 primeros en un jsonarray auxiliar para eliminar a los que no entran en el top
            for (int k=0; k < 10; k++) {

                JSONObject json = jsonArray.getJSONObject(k);
                String nom = json.getString("nombreJugador");
                int p = json.getInt("puntos");

                JSONObject jsonAux = new JSONObject();
                jsonAux.put("puntos", p);
                jsonAux.put("nombreJugador", nom);

                arregloAux.put(k,jsonAux);
            }

            //ordenarArreglojson();
            guardarPreferencias(arregloAux.toString());

        }catch (JSONException error){ }

    }



    //_________________________________SHARED PREFERENCE __________________________________________//
    /* Guarda el JSON en las preferencias */
    public void guardarPreferencias(String ranking){
        SharedPreferences preferencias = getSharedPreferences("ranking", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        // guarda la cadena json (tiene el nombre y puntos del jugador)
        editor.putString("puntajes", ranking);
        editor.commit();
    }


    // _______________________________ ActionBar (flecha hacia atras )______________________//
    public boolean onSupportNavigateUp(){
            // muestra el boton ( <-- )en el action bar
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            //al presionar la flecha llama a este metodo y aparece el cuadro de dialogo
            onBackPressed();

            return false;
    }

    public void onBackPressed(){
        // crear el alerta de dialogo de esta actividad
        AlertDialog.Builder builder = new AlertDialog.Builder(Juego.this);

        //setear el titulo con los recursos strings q tengo guardado
        builder.setTitle(getResources().getString(R.string.volver));

        //setear msj q queres aparece en el dialogo
        builder.setMessage(getResources().getString(R.string.deseaMenuPcipal))

                //seteo el boton de si (cuando se aprieta va al main)
                .setPositiveButton(getResources().getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();

                    }
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
                .show();
    }


    //______________________________________ FIN ___________________________________________//
}

package com.example.timetotrail.proyect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.timetotrail.R;
import com.example.timetotrail.proyect.adapter.AdaptadorRutas;
import com.example.timetotrail.proyect.model.Ruta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FiltrarActivity extends AppCompatActivity {
    private final ArrayList<String> ARRAYDIFICULTAD = new ArrayList<>();
    private final ArrayList<String> ARRAYDISTANCIA = new ArrayList<>();
    private final ArrayList<String> ARRAYPOBLACION = new ArrayList<>();
    private Spinner spDificultad, spDistancia, spPoblacion;
    private TextView tvSinResultados;
    private Button btBack, btFiltrar;
    static List<Ruta> listaRutas;
    private RecyclerView rvListaRutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtrar);

        listaRutas = (List<Ruta>) getIntent().getSerializableExtra("LISTARUTAS");

        initReferences();
        cargarDatosArrays();
        configurarSpinners();
        listeners();

    }



    private void initReferences() {
        spDistancia=findViewById(R.id.spDistanciaActivityFiltrar);
        spDificultad=findViewById(R.id.spDificultadActivityFiltrar);
        spPoblacion = findViewById(R.id.spPoblacionActivityFiltrar);
        btBack = findViewById(R.id.btVolverMainFiltrarActivity);
        btFiltrar = findViewById(R.id.btFiltrarActivityFiltrar);
        rvListaRutas = findViewById(R.id.rvRutasFiltrarActivity);
        tvSinResultados = findViewById(R.id.tvSinResultados);
    }

    private void listeners() {
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent irMain = new Intent(FiltrarActivity.this, MainActivity.class);
                startActivity(irMain);
            }
        });


        btFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Ruta> listaRutasFiltradas =  new ArrayList<>();
                String dificultadSpinner = spDificultad.getSelectedItem().toString();
                String distanciaSpinner = spDistancia.getSelectedItem().toString();
                String poblacionSpinner = spPoblacion.getSelectedItem().toString();

                for (Ruta ruta : listaRutas) {
                    int distanciaRutaInt = Integer.parseInt(ruta.getDistancia());

                    boolean pasaFiltroDificultad = dificultadSpinner.equalsIgnoreCase("todas") || ruta.getDificultad().equalsIgnoreCase(dificultadSpinner);
                    boolean pasaFiltroDistancia = distanciaSpinner.equalsIgnoreCase("todas") ||
                            (distanciaSpinner.equalsIgnoreCase("0-10") && distanciaRutaInt <= 10) ||
                            (distanciaSpinner.equalsIgnoreCase("10-20") && distanciaRutaInt > 10 && distanciaRutaInt <= 20) ||
                            (distanciaSpinner.equalsIgnoreCase("20-30") && distanciaRutaInt > 20 && distanciaRutaInt <= 30) ||
                            (distanciaSpinner.equalsIgnoreCase("+30") && distanciaRutaInt > 30);

                    boolean pasaFiltroPoblacion = poblacionSpinner.equalsIgnoreCase("todas") || ruta.getPoblacion().equalsIgnoreCase(poblacionSpinner);

                    if (pasaFiltroDificultad && pasaFiltroDistancia && pasaFiltroPoblacion) {
                        listaRutasFiltradas.add(ruta);
                    }
                }
                if (listaRutasFiltradas.isEmpty()){
                    tvSinResultados.setVisibility(View.VISIBLE);
                }else{
                    tvSinResultados.setVisibility(View.GONE);
                }
                crearAdaptador(listaRutasFiltradas);
            }
        });
    }

    private void cargarDatosArrays() {
        ARRAYDIFICULTAD.add("todas");
        ARRAYDIFICULTAD.add("Dificil");
        ARRAYDIFICULTAD.add("Medio");
        ARRAYDIFICULTAD.add("Facil");

        ARRAYDISTANCIA.add("todas");
        ARRAYDISTANCIA.add("0-10");
        ARRAYDISTANCIA.add("10-20");
        ARRAYDISTANCIA.add("20-30");
        ARRAYDISTANCIA.add("+30");

        ARRAYPOBLACION.add("todas");
        for (Ruta ruta :listaRutas) {
            if (!ARRAYPOBLACION.contains(ruta.getPoblacion())){
                ARRAYPOBLACION.add(ruta.getPoblacion());
                Log.d("Array poblacion", ruta.getPoblacion());
            }

        }

    }

    private void configurarSpinners() {
        ArrayAdapter<String> adapterDificultad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ARRAYDIFICULTAD);
        adapterDificultad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDificultad.setAdapter(adapterDificultad);

        ArrayAdapter<String> adapterDistancia = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ARRAYDISTANCIA);
        adapterDistancia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistancia.setAdapter(adapterDistancia);

        ArrayAdapter<String> adapterPoblacion = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ARRAYPOBLACION);
        adapterPoblacion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPoblacion.setAdapter(adapterPoblacion);
    }

    private void crearAdaptador(List<Ruta> listaRutas) {
        AdaptadorRutas adaptadorRutas = new AdaptadorRutas(listaRutas);
        rvListaRutas.setAdapter(adaptadorRutas);
        rvListaRutas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        adaptadorRutas.setOnItemClickListener(new AdaptadorRutas.OnItemClickListener() {
            @Override
            public void onItemClick(int posicion) {
                irDetalleRutaActivity(posicion, listaRutas);
            }
        });
    }

    private void irDetalleRutaActivity(int posicion, List<Ruta> listaRutaDetalleActivity) {
        Intent irDetalleRuta = new Intent(FiltrarActivity.this, DetalleRutaActivity.class);
        irDetalleRuta.putExtra("POSICIONRUTA", posicion);
        irDetalleRuta.putExtra("LISTARUTASDETALLEACTIVITY", (Serializable) listaRutaDetalleActivity);
        startActivity(irDetalleRuta);
    }

}
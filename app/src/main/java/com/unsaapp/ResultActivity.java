package com.unsaapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.unsaapp.preguntas.unidad6;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class ResultActivity extends AppCompatActivity {
    MaterialCardView home, reintentar;
    TextView tiempo, correctt, wrongt, resultinfo, resultscore;
    ImageView resultImage;

    AdView adView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        SharedPreferences almacen = this.getSharedPreferences("Puntajes", MODE_PRIVATE);

        //interticial();

        tiempo = findViewById(R.id.tempo);
        home = findViewById(R.id.returnHome);
        reintentar = findViewById(R.id.reintentar);
        correctt = findViewById(R.id.correctScore);
        wrongt = findViewById(R.id.wrongScore);
        resultinfo = findViewById(R.id.resultInfo);
        resultscore = findViewById(R.id.resultScore);
        resultImage = findViewById(R.id.resultImage);

        String nombre = getIntent().getStringExtra("nombre");
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialToolbar toolbar = findViewById(R.id.tolbari);
        toolbar.setTitle("Unidad " + nombre);

        //obtenemos el mejor tiempo.
        int tiempoS = getIntent().getIntExtra("tiempo", 0);

        int correct = getIntent().getIntExtra("correct", 0);
        int wrong = getIntent().getIntExtra("wrong", 0);
        int totalPreg = correct + wrong;
        int score = (correct * 100) / totalPreg;

        mostrarTiempoEnTextView(tiempo, tiempoS);

        correctt.setText("" + correct);
        wrongt.setText("" + wrong);
        resultscore.setText("" + score + " %");

        if (score >= 0 && score <= 30) {
            resultinfo.setText("Deberias seguir practicando");
            resultImage.setImageResource(R.drawable.ic_sad);
        } else if (score >= 30 && score <= 50) {
            resultinfo.setText("Te falta practicar");
            resultImage.setImageResource(R.drawable.ic_neutral);
        } else if (score >= 50 && score <= 100) {
            resultinfo.setText("Ya estas listo");
            resultImage.setImageResource(R.drawable.ic_smile);
        } else {
            resultinfo.setText("Ya estas listo");
            resultImage.setImageResource(R.drawable.ic_smile);
        }


        int valorActual = almacen.getInt("intentos", 20);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (valorActual > 1) {
                    String nombre = getIntent().getStringExtra("nombre");
                    Intent intent = new Intent(ResultActivity.this, unidad6.class);
                    intent.putExtra("nombre", nombre);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ResultActivity.this, "Te quedaste sin Ints", Toast.LENGTH_SHORT).show();

                }
            }
        });

        realizarAccionSegunNombre();

    }

    @SuppressLint("DefaultLocale")
    private void mostrarTiempoEnTextView(TextView textView, int segundos) {
        // Calcular horas, minutos y segundos
        int horas = segundos / 3600;
        int minutos = (segundos % 3600) / 60;
        int segundosRestantes = segundos % 60;

        // Mostrar tiempo en formato HH:MM:SS
        textView.setText(String.format("%02d:%02d:%02d", horas, minutos, segundosRestantes));
    }

    public void realizarAccionSegunNombre() {

        String nombre = getIntent().getStringExtra("nombre");
        SharedPreferences almacen = getSharedPreferences("Puntajes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = almacen.edit();
        //obtenemos los numeros correcto e incorrectos
        int correct = getIntent().getIntExtra("correct", 0);
        int wrong = getIntent().getIntExtra("wrong", 0);
        int totalPreg = correct + wrong;
        int puntajePorcentajeActual = (correct * 100) / totalPreg;
        int puntajePorcentajeGuardado = 0;

        puntajePorcentajeGuardado = almacen.getInt(nombre, 0);

        if (puntajePorcentajeActual > puntajePorcentajeGuardado) {
            editor.putInt(nombre, puntajePorcentajeActual);
            editor.apply();
        }

        // guardamos el tiempo o no
        String nombreMejorT = nombre + "ttt";
        String nombreUltimoT = nombre + "uuu";

        //obtenemos y guardamos el ultimo tiempo
        int tiempoS = getIntent().getIntExtra("tiempo", 0);
        editor.putInt(nombreUltimoT, tiempoS);
        editor.apply();

        int mejorT = almacen.getInt(nombreMejorT, 0);

        if (mejorT == 0 || tiempoS < mejorT && puntajePorcentajeActual >= puntajePorcentajeGuardado) {
            editor.putInt(nombreMejorT, tiempoS);
            editor.apply();
            Toast.makeText(this, "Mejoraste tu mejor tiempo", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean doubleBackToMainActivity = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToMainActivity) {
            finish();
        } else {
            this.doubleBackToMainActivity = true;
            Toast.makeText(this, ("Presione nuevamente para volver \uD83D\uDE0A"), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToMainActivity = false;
                }
            }, 2000); // Intervalo de tiempo en milisegundos para restablecer el estado de "doubleBackToMainActivity"
        }
    }
}
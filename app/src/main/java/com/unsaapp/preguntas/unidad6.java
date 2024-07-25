package com.unsaapp.preguntas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.unsaapp.modelosClases.QuesitionsItem;
import com.unsaapp.ResultActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.appbar.MaterialToolbar;
import com.unsaapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class unidad6 extends AppCompatActivity {

    //para el cronometro
    private Handler handler = new Handler();
    private int contador2 = 0;
    private Handler handler2 = new Handler();
    private long tiempoInicio = 0;
    //creamos objetos tipo Textview
    TextView pregunta, respA, respB, respC, respD,tiempos;
    List<QuesitionsItem> quesitionsItems; // y tambien tipo List
    int currentQuestions = 0, correct = 0, wrong = 0; // creamos 3 varibles int con valores
    //anucio
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        //anuncio

        // guardo el nombre de la unidad
        String nombre = getIntent().getStringExtra("nombre");
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Unidad "+ nombre);
        String tiempo=nombre+"uuu";
        iniciarCronometro();

        mAdView = findViewById(R.id.adView); // Reemplaza "R.id.adView" con el ID de tu vista de anuncio de banner en el diseño XML
        initLoadAds();
        iniciarContador();
        // se inician las variables declaradas anteriormente
        tiempos = findViewById(R.id.tiempo);
        pregunta = findViewById(R.id.quizText);
        respA = findViewById(R.id.aanswer);
        respB = findViewById(R.id.banswer);
        respC = findViewById(R.id.canswer);
        respD = findViewById(R.id.danswer);

        loadAllQuestions(nombre);// llamamos a ese metodo

        Collections.shuffle(quesitionsItems); // shuffle se utilza para mesclar el orden de la preguntas de una coleccion, en este caso del questionItems creado

        setQuestionScreen(currentQuestions);

        respA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quesitionsItems.get(currentQuestions).getAnswer1().equals(quesitionsItems.get(currentQuestions).getCorrect())) {
                    correct++;
                    respA.setBackgroundResource(R.color.green);
                    respA.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wrong++;
                    respA.setBackgroundResource(R.color.red);
                    respA.setTextColor(getResources().getColor(R.color.white));
                }

                if (currentQuestions < quesitionsItems.size() - 1) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuestions++;
                            setQuestionScreen(currentQuestions);
                            respA.setBackgroundResource(R.color.white);
                            respA.setTextColor(getResources().getColor(R.color.text_secondery_color));
                        }

                    }, 500);


                } else {
                    Intent intent = new Intent(unidad6.this, ResultActivity.class);
                    intent.putExtra("correct", correct);
                    intent.putExtra("wrong", wrong);
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("tiempo",contador2);

                    startActivity(intent);
                    finish();


                }
            }
        });
        respB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quesitionsItems.get(currentQuestions).getAnswer2().equals(quesitionsItems.get(currentQuestions).getCorrect())) {
                    correct++;
                    respB.setBackgroundResource(R.color.green);
                    respB.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wrong++;
                    respB.setBackgroundResource(R.color.red);
                    respB.setTextColor(getResources().getColor(R.color.white));
                }

                if (currentQuestions < quesitionsItems.size() - 1) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuestions++;
                            setQuestionScreen(currentQuestions);
                            respB.setBackgroundResource(R.color.white);
                            respB.setTextColor(getResources().getColor(R.color.text_secondery_color));
                        }
                    }, 500);

                } else {
                    Intent intent = new Intent(unidad6.this, ResultActivity.class);
                    intent.putExtra("correct", correct);
                    intent.putExtra("wrong", wrong);
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("tiempo",contador2);
                    startActivity(intent);
                    finish();
                }
            }
        });
        respC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quesitionsItems.get(currentQuestions).getAnswer3().equals(quesitionsItems.get(currentQuestions).getCorrect())) {
                    correct++;
                    respC.setBackgroundResource(R.color.green);
                    respC.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wrong++;
                    respC.setBackgroundResource(R.color.red);
                    respC.setTextColor(getResources().getColor(R.color.white));
                }

                if (currentQuestions < quesitionsItems.size() - 1) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuestions++;
                            setQuestionScreen(currentQuestions);
                            respC.setBackgroundResource(R.color.white);
                            respC.setTextColor(getResources().getColor(R.color.text_secondery_color));
                        }
                    }, 500);

                } else {
                    Intent intent = new Intent(unidad6.this, ResultActivity.class);
                    intent.putExtra("correct", correct);
                    intent.putExtra("wrong", wrong);
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("tiempo",contador2);
                    startActivity(intent);
                    finish();
                }
            }
        });
        respD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quesitionsItems.get(currentQuestions).getAnswer4().equals(quesitionsItems.get(currentQuestions).getCorrect())) {
                    correct++;
                    respD.setBackgroundResource(R.color.green);
                    respD.setTextColor(getResources().getColor(R.color.white));
                } else {
                    wrong++;
                    respD.setBackgroundResource(R.color.red);
                    respD.setTextColor(getResources().getColor(R.color.white));
                }

                if (currentQuestions < quesitionsItems.size() - 1) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentQuestions++;
                            setQuestionScreen(currentQuestions);
                            respD.setBackgroundResource(R.color.white);
                            respD.setTextColor(getResources().getColor(R.color.text_secondery_color));
                        }
                    }, 500);

                } else {
                    Intent intent = new Intent(unidad6.this, ResultActivity.class);
                    intent.putExtra("correct", correct);
                    intent.putExtra("wrong", wrong);
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("tiempo",contador2);

                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long tiempoActual = System.currentTimeMillis();
            long tiempoTranscurrido = tiempoActual - tiempoInicio;
            actualizarTiempo(tiempoTranscurrido);

            // Programa la ejecución del próximo ciclo después de 1000 ms (1 segundo)
            handler.postDelayed(this, 1000);
        }
    };

    private void iniciarContador() {
        // Ejecuta un Runnable cada segundo
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Incrementa el contador y actualiza el texto
                contador2++;

                // Programa la ejecución del próximo incremento después de 1000 ms (1 segundo)
                handler2.postDelayed(this, 1000);
            }
        }, 1000); // El primer incremento ocurre después de 1000 ms (1 segundo)
    }
    private void iniciarCronometro() {
        tiempoInicio = System.currentTimeMillis();
        handler.postDelayed(runnable, 0);
    }

    @SuppressLint("DefaultLocale")
    private void actualizarTiempo(long tiempo) {
        int segundos = (int) (tiempo / 1000);
        int horas = segundos / 3600;
        int minutos = segundos / 60;
        segundos = segundos % 60;

        tiempos.setText(String.format("%02d:%02d:%02d",horas, minutos, segundos));
    }
    private void initLoadAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void setQuestionScreen(int currentQuestions) {
        // Dentro de setQuestionScreen
        if (!quesitionsItems.get(currentQuestions).getAnswer1().isEmpty()) {
            respA.setText(quesitionsItems.get(currentQuestions).getAnswer1());
            respA.setVisibility(View.VISIBLE); // Mostrar el TextView
        } else {
            respA.setVisibility(View.GONE); // Ocultar el TextView
        }

        if (!quesitionsItems.get(currentQuestions).getAnswer2().isEmpty()) {
            respB.setText(quesitionsItems.get(currentQuestions).getAnswer2());
            respB.setVisibility(View.VISIBLE);
        } else {
            respB.setVisibility(View.GONE);
        }

        if (!quesitionsItems.get(currentQuestions).getAnswer3().isEmpty()) {
            respC.setText(quesitionsItems.get(currentQuestions).getAnswer3());
            respC.setVisibility(View.VISIBLE);
        } else {
            respC.setVisibility(View.GONE);
        }

        if (!quesitionsItems.get(currentQuestions).getAnswer4().isEmpty()) {
            respD.setText(quesitionsItems.get(currentQuestions).getAnswer4());
            respD.setVisibility(View.VISIBLE);
        } else {
            respD.setVisibility(View.GONE);
        }


        pregunta.setText(quesitionsItems.get(currentQuestions).getQuestions());
        respA.setText(quesitionsItems.get(currentQuestions).getAnswer1());
        respB.setText(quesitionsItems.get(currentQuestions).getAnswer2());
        respC.setText(quesitionsItems.get(currentQuestions).getAnswer3());
        respD.setText(quesitionsItems.get(currentQuestions).getAnswer4());

        TextView textTotalQuestions = findViewById(R.id.textTotalQuestions);
        TextView textCorrectAnswers = findViewById(R.id.textCorrectAnswers);
        TextView textWrongAnswers = findViewById(R.id.textWrongAnswers);

        // Actualizar los valores de los TextView
        textCorrectAnswers.setText(""+correct);
        textWrongAnswers.setText(""+wrong);
        textTotalQuestions.setText((quesitionsItems.size()-(wrong+correct))+"/ "+quesitionsItems.size());
        int x = quesitionsItems.size()-(quesitionsItems.size()-2);
        if(quesitionsItems.size() == x){
            if (mInterstitialAd != null) {
                mInterstitialAd.show(unidad6.this);
            }

        }

        // Calcular el progreso de la barra
        int totalQuestions = quesitionsItems.size();
        int answeredQuestions = correct + wrong;
        int progress = (answeredQuestions * 100) / totalQuestions;

        // Actualizar el progreso de la barra
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(progress);
        //////////////////////////////////
    }

    private void loadAllQuestions(String materia) {

        quesitionsItems = new ArrayList<>();

        String jsonFileName = materia + ".json";

        boolean jsonFileExists = fileExists(jsonFileName);
        if (!jsonFileExists) {
            // Mostrar mensaje y volver a la actividad anterior
            Toast.makeText(this, "No hay preguntas de esta unidad", Toast.LENGTH_SHORT).show();
            navigateBack();
            return;
        }


        // Cargar el JSON desde el almacenamiento interno (unidad6.json)
        String jsonquiz = loadJsonFromInternalStorage(jsonFileName);

        try {
            // Crear un objeto JSONObject a partir de la cadena JSON cargada
            JSONObject jsonObject = new JSONObject(jsonquiz);

            // Obtener el arreglo JSON "unidad6" del objeto JSONObject
            JSONArray questions = jsonObject.getJSONArray(materia);

            // Iterar a través de las preguntas en el arreglo JSON
            for (int i = 0; i < questions.length(); i++) {
                // Obtener el objeto JSON de la pregunta actual
                JSONObject question = questions.getJSONObject(i);

                // Crear una lista para almacenar las respuestas
                List<String> answers = new ArrayList<>();
                answers.add(question.getString("answer1"));
                answers.add(question.getString("answer2"));
                answers.add(question.getString("answer3"));
                answers.add(question.getString("answer4"));

                // Mezclar aleatoriamente el orden de las respuestas
                Collections.shuffle(answers);

                // Obtener el texto de la pregunta y la respuesta correcta
                String questionString = question.getString("question");
                String correctAnswer = question.getString("correct");

                // Crear un objeto QuesitionsItem con las respuestas mezcladas y los demás datos
                quesitionsItems.add(new QuesitionsItem(questionString, answers.get(0), answers.get(1), answers.get(2), answers.get(3), correctAnswer));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private boolean fileExists(String filename) {
        File file = getFileStreamPath(filename);
        return file.exists();
    }
    private String loadJsonFromInternalStorage(String filename) {
        String json = "";
        try {
            FileInputStream fileInputStream = openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            json = stringBuilder.toString();

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    //para volver atras
    private void navigateBack() {
        finish();
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
    protected void onDestroy() {
        super.onDestroy();
        // Detén el proceso cuando la actividad se destruye para evitar fugas de memoria
        handler.removeCallbacksAndMessages(null);
        handler2.removeCallbacksAndMessages(null);
    }
}
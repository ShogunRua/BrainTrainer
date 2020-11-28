package com.example.braintrainer1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textViewQuestion;
    private TextView textViewTimer;
    private TextView textViewScore;
    private TextView textViewOpinion0;
    private TextView textViewOpinion1;
    private TextView textViewOpinion2;
    private TextView textViewOpinion3;
    // текст у наших TextView, мы будем устанавливать в цикле, поэтому давайте занесем их в массив
    private ArrayList<TextView> options = new ArrayList<>();

    // 1)нам нужен метод, который будет генерировать вопрос и правильный ответ, для этого нам понадобятся переменные
    private String question; // хранит текст вопроса
    private int rightAnswer; // хранит правильный ответ
    private int rightAnswerPosition; // хранит позицию правильного ответа
    private boolean isPositive; // хранит знак выражения, будем складывать или вычитать
    // когда будем генерировать вопрос, нам нужно указать в каких пределах находиться это число
    private int min = 5;
    private int max = 30;
    private int countOfQuestions = 0;
    private int countOfRightAnswers = 0;
    private boolean gameOver = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewScore = findViewById(R.id.textViewScore);
        textViewOpinion0 = findViewById(R.id.textViewOpinion0);
        textViewOpinion1 = findViewById(R.id.textViewOpinion1);
        textViewOpinion2 = findViewById(R.id.textViewOpinion2);
        textViewOpinion3 = findViewById(R.id.textViewOpinion3);
        // в методе onCreate занесем в options все TextView с вариантами
        options.add(textViewOpinion0);
        options.add(textViewOpinion1);
        options.add(textViewOpinion2);
        options.add(textViewOpinion3);
        playNext();
        CountDownTimer timer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long l) {
                textViewTimer.setText(getTime(l));
                if (l < 10000){
                    textViewTimer.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }

            }

            @Override
            public void onFinish() {
                gameOver = true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int max = preferences.getInt("max", 0);
                if(countOfRightAnswers >= max){
                    preferences.edit().putInt("max", countOfRightAnswers).apply();
                }
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                // чтобы вторая активность могла отобразить наш результат
                intent.putExtra("result", countOfRightAnswers);
                startActivity(intent);

            }
        };
        timer.start();


    }

    private void playNext() {
        // теперь сгенерируем вопрос
        generateQuestion();
        for (int i = 0; i < options.size(); i++) {
            if (i == rightAnswerPosition) {
                options.get(i).setText(Integer.toString(rightAnswer));

            } else {
                options.get(i).setText(Integer.toString(generateWrongAnswer()));
            }
        }
        String score = String.format("%s / %s", countOfRightAnswers, countOfQuestions);
        textViewScore.setText(score);
    }

    // 1) этот метод:

    private void generateQuestion() {
        // первым делом получаем два случайных числа
        int a = (int) (Math.random() * (max - min + 1) + min);
        int b = (int) (Math.random() * (max - min + 1) + min);
        // теперь случайным образом отобразим значение знака, положительным или орицательным оно будет
        int mark = (int) Math.random() * 2;
        isPositive = mark == 1;
        // теперь сформируем правильный ответ
        if (isPositive) {
            rightAnswer = a + b;
            question = String.format("%s + %s", a, b);
        } else {
            rightAnswer = a - b;
            question = String.format("%s - %s", a, b);
        }
        textViewQuestion.setText(question);
        // в этом же методе сгенерируем индекс правильного ответа
        rightAnswerPosition = (int) (Math.random() * 4);


    }
    // второй метод, будет генерировать не правильный ответ, и возвращать результат. Мы этот метод используем при установке текста у TextView  с вариантами ответов

    private int generateWrongAnswer() {
        int result;
        // создаём это число до того как оно будет совпадать с правильным ответом
        do {
            result = (int) (Math.random() * max * 2 + 1) - (max - min);
        } while (result == rightAnswer);
        return result;

    }

    // создадим для таймера метод, который будет читать милисекунды и преобразовывать его в читаемый формат
    private String getTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public void onClickAnswer(View view) {
        if (!gameOver) {
            TextView textView = (TextView) view;
            String answer = textView.getText().toString();
            // чтобы нам сравнить правильный ответ с полученым ответом, нам необходимо чтобы эти типы совпадали
            int chosenAnswer = Integer.parseInt(answer);
            if (chosenAnswer == rightAnswer) {
                countOfRightAnswers++;
                Toast.makeText(this, "Верно", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Неверно", Toast.LENGTH_SHORT).show();
            }
            countOfQuestions++;
            playNext();
        }
    }
}

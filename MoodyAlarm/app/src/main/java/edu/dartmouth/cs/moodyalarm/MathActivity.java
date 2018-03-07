package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class MathActivity extends AppCompatActivity {

    private int answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math);
        //LinearLayout layout = (LinearLayout) findViewById(R.id.math_layout);
        Typewriter writer = (Typewriter) findViewById(R.id.typewriter); //new Typewriter(this);

        //layout.addView(writer);
        //setContentView(writer);
        writer.setTextSize(50);
        writer.setCenter();
        //writer.setMarginTop();

        //Add a character every 150ms
        writer.setCharacterDelay(150);
        String eq = new EquationGenerator().generateEquation();
        answer = (int) Double.parseDouble(eq.split("=")[1]);
        eq = eq.split("=")[0];

        eq = eq + " = ? ";
        eq = "Q. " + eq;
        writer.animateText(eq);
    }

    public static class Typewriter extends AppCompatTextView {

        private CharSequence mText;
        private int mIndex;
        private long mDelay = 500; //Default 500ms delay


        public Typewriter(Context context) {
            super(context);
        }

        public Typewriter(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public Typewriter(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        private Handler mHandler = new Handler();
        private Runnable characterAdder = new Runnable() {
            @Override
            public void run() {
                setText(mText.subSequence(0, mIndex++));
                if(mIndex <= mText.length()) {
                    mHandler.postDelayed(characterAdder, mDelay);
                }
            }
        };

        public void animateText(CharSequence text) {
            mText = text;
            mIndex = 0;

            setText("");
            mHandler.removeCallbacks(characterAdder);
            mHandler.postDelayed(characterAdder, mDelay);
        }

        public void setCharacterDelay(long millis) {
            mDelay = millis;
        }

        public void setTextSize(float size){
            super.setTextSize(size);
        }

        public void setCenter(){
            super.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        public void setMarginTop(){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(20, 20, 20, 20);
            super.setLayoutParams(params);
        }
    }

    public class EquationGenerator {

        SharedPreferences prefs = getSharedPreferences(SnoozeSettings.PREFS_NAME, 0);
        private double difficulty = prefs.getInt(SnoozeSettings.MATH_DIFF, 100) / 100.0;
        private Random rand = new Random();
        private int[] numbers = new int[ (int) (rand.nextInt(5) * difficulty + 3) ];
        private String[] operations = new String[] {"+", "-", "*"};
        private int[] operators = new int[numbers.length - 1];

        private EquationGenerator () {}

        public String generateEquation() {

            String eq = "";

            // generate random numbers first
            for (int i = 0; i < numbers.length; i ++) {
                int num_digit = 1;
                while (rand.nextDouble() < difficulty) {
                    num_digit ++;
                }
                numbers[i] = rand.nextInt((int) (Math.pow(10, num_digit))) + 1;
            }

            eq += Integer.toString(numbers[0]) + " ";
            ArrayList<Integer> priority = new ArrayList<Integer>();
            // generate operators and create the string
            for (int i = 0; i < numbers.length - 1; i++ ){
                operators[i] = rand.nextInt(3);
                eq += operations[operators[i]] + " " +Integer.toString(numbers[i + 1]) + " ";
                if (operators[i] >= 3)
                    priority.add(i);
            }

            ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
            // evaluate JavaScript code from String
            try {
                Object obj = engine.eval(eq);
                eq += "= " +  obj.toString();
                Log.d("TAG", obj.toString());
            } catch (Exception e) {

            }

            return eq;
        }
    }

    public void onSubmitCalc(View view){
        EditText ans = (EditText) findViewById(R.id.answer_box);
        Integer submitted = Integer.parseInt(ans.getText().toString());

        if (answer == submitted) {
            Toast.makeText(this, "correct!", Toast.LENGTH_SHORT).show();
            PopupActivity.alarm.stop_alert(getApplicationContext());
            finish();
        } else {
            Toast.makeText(this, "try again!", Toast.LENGTH_SHORT).show();
        }

        }
}

package com.example.appmclacpro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener
{
    private TextToSpeech tts;
    private double da;
    private double ra;
    private double la;
    public void onInit(int initStatus)
    {
        this.tts.setLanguage(Locale.US);
    }
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {

    }
    public void onSensorChanged(SensorEvent event)
    {
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];
        this.da = x;
        this.ra = y;
        this.la = z;
        double result = (x*x + y*y + z*z);
        double acc = Math.sqrt(result);

        if (acc > 20)
        {
            EditText p = (EditText) findViewById(R.id.pBox);
            p.setText(" ");
            EditText m = (EditText) findViewById(R.id.aBox);
            m.setText(" ");
            EditText i = (EditText) findViewById(R.id.iBox);
            i.setText(" ");
            TextView output = (TextView) findViewById(R.id.output);
            output.setText(" ");
        }

    }
    private void hideKeyboard()
    {
        View aa = this.getCurrentFocus();
        if (aa != null)
        {
            InputMethodManager bb = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            bb.hideSoftInputFromWindow(aa.getWindowToken(),0);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tts = new TextToSpeech(this,this);
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }



    @SuppressLint("DefaultLocale")
    public void onClick (View v)
    {
        try {

            MPro mp = new MPro();

            EditText principle = (EditText) findViewById(R.id.pBox);
            EditText amortization = (EditText) findViewById(R.id.aBox);
            EditText interest = (EditText) findViewById(R.id.iBox);

            String convertPrinciple = principle.getText().toString();
            String convertAmortization = amortization.getText().toString();
            String convertInterest = interest.getText().toString();

            mp.setPrinciple(convertPrinciple);
            mp.setAmortization(convertAmortization);
            mp.setInterest(convertInterest);

            String mPayment = mp.computePayment("%,.2f");
            StringBuilder result = new StringBuilder("Monthly Payment = " + mPayment);
            tts.speak(result,TextToSpeech.QUEUE_FLUSH,null,null);

            result.append("\n\n");
            result.append("By making this payments monthly for 20 years, the mortgage will be paid " + "in full. But if you terminate the mortgage on its nth anniversary, the balance " + "still owing depends on n as shown below:");

            result.append("\n\n\n");
            result.append("       n" + "         Balance");
            result.append("\n\n");

            int i;

            for (i=0;i<=5;i++)
            {
                if (i != 5)
                {
                    result.append(String.format("%8d", i)).append(mp.outstandingAfter(i, "%,16.0f"));
                    result.append("\n\n");
                }
                else if (i == 5)
                {
                    int j;
                    for (j=1;j<=4;j++)
                    {
                        int a = i*j;
                        if (j!=4)
                        {
                            result.append(String.format("%8d", a)).append(mp.outstandingAfter(a, "%,16.0f"));
                            result.append("\n\n");}
                        else
                        {
                            result.append(String.format("%8d", a)).append(mp.outstandingAfter(a, "%,16.0f"));
                        }
                    }
                }
            }
            result.append("\n" + "x:").append(this.da).append("\n").append("y:").append(this.ra).append("\n").append("z:").append(this.la);
            TextView show = (TextView) findViewById(R.id.output);
            show.setText(result.toString());
            hideKeyboard();
        }

        catch (Exception e)
        {
            Toast label = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            label.setMargin(0,100);
            label.show();
        }

    }
}

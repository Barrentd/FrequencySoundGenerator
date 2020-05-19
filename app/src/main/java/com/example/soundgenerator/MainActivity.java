package com.example.soundgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText freq;

    private Button play;
    private Button playMin;
    private Button playMax;
    private Button stop;

    private TextView viewFreq;

    private int duration = 10; // in seconds
    private int sampleRate = 8000;
    private int numSamples = duration * sampleRate;
    private double sample[] = new double[numSamples];
    private byte generatedSnd[] = new byte[2 * numSamples];
    private double freqOfTone;

    private SeekBar volSeekbar = null;
    private AudioManager audioMan = null;
    private AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, numSamples, AudioTrack.MODE_STATIC);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        freq = findViewById(R.id.editText);
        play = findViewById(R.id.button);
        playMax = findViewById(R.id.button2);
        playMin = findViewById(R.id.button3);
        stop = findViewById(R.id.button4);
        viewFreq = findViewById(R.id.textvaleur);

        initVolumeControl();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSound();
                freqOfTone = Double.parseDouble(freq.getText().toString());
                freqOfTone = freqOfTone*1000;
                generateTone();
                playSound();
                showFreq();
            }
        });

        playMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSound();
                freqOfTone = 19000;
                generateTone();
                playSound();
                showFreq();
            }
        });

        playMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSound();
                freqOfTone = 8000;
                generateTone();
                playSound();
                showFreq();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSound();
            }
        });
    }

    private void initVolumeControl(){
        try{
            volSeekbar = (SeekBar)findViewById(R.id.seekBarVol);
            audioMan = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            volSeekbar.setMax(audioMan.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volSeekbar.setProgress(audioMan.getStreamVolume(AudioManager.STREAM_MUSIC));
            volSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void generateTone(){
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }
        int idx = 0;
        for (final double dVal : sample) {
            final short val = (short) ((dVal * 32767));
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    private void playSound(){
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }

    private void stopSound(){
        try {
            audioTrack.stop();
            viewFreq.setText("--");
        }catch (Exception e){
            Log.d("Stop", e.toString());
        }
    }

    private void showFreq(){
        freqOfTone = freqOfTone/1000;
        viewFreq.setText(String.valueOf(freqOfTone)+"Hz");
    }
}

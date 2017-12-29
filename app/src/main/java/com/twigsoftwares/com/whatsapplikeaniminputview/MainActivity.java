package com.twigsoftwares.com.whatsapplikeaniminputview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;

public class MainActivity extends AppCompatActivity {
    RecordView recordView;
    RecordButton recordButton;
    hani.momanii.supernova_emoji_library.Helper.EmojiconEditText messageinputView;
    ImageButton sendButton;
    AllAngleExpandableButton aaebutton;
    RelativeLayout middleLayout;


    ImageView emojiButton;


    //Sound recorder
    private MediaRecorder mRecorder;
    private long mStartTime = 0;

    private int[] amplitudes = new int[100];
    private int i = 0;

    private Handler mHandler = new Handler();
    private Runnable mTickExecutor = new Runnable() {
        @Override
        public void run() {
            tick();
            mHandler.postDelayed(mTickExecutor, 100);
        }
    };
    private File mOutputFile;
    Button requestPermission;



    private EmojIconActions emojIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //emoji images
        emojiButton = (ImageView) findViewById(R.id.emojibutton);
        final View rootView = findViewById(R.id.root_view);

        aaebutton = (AllAngleExpandableButton) findViewById(R.id.button_expandable);
        recordView = (RecordView) findViewById(R.id.record_view);
        recordButton = (RecordButton) findViewById(R.id.record_button);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        messageinputView = (hani.momanii.supernova_emoji_library.Helper.EmojiconEditText) findViewById(R.id.messageinput);
        requestPermission = (Button) findViewById(R.id.permission);
        middleLayout = (RelativeLayout) findViewById(R.id.middlelayout);

        requestPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utility.checkAndRequestPermissions(MainActivity.this);
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                messageinputView.setText("");
            }
        });
        messageinputView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {

                }
            }
        });


        messageinputView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0) {
                    //recordButton.setVisibility(View.GONE);


                    recordButton.animate()
                            .translationY(recordButton.getHeight() - recordButton.getHeight())
                            .alpha(0.0f)
                            .setDuration(50)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    recordButton.setVisibility(View.GONE);
                                    sendButton.setVisibility(View.VISIBLE);
                                    aaebutton.setVisibility(View.GONE);
                                    RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                                            new RelativeLayout.LayoutParams(
                                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                                    RelativeLayout.LayoutParams.WRAP_CONTENT));
                                    relativeParams.setMargins(0, 0, 90, 0);
                                    middleLayout.setLayoutParams(relativeParams);
                                    middleLayout.requestLayout();

                                }
                            });

                } else {

                    sendButton.animate()
                            .translationY(sendButton.getHeight() - sendButton.getHeight())
                            .alpha(0.0f)
                            .setDuration(50)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    sendButton.setVisibility(View.GONE);
                                    recordButton.setVisibility(View.VISIBLE);
                                    aaebutton.setVisibility(View.VISIBLE);
                                    RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                                            new RelativeLayout.LayoutParams(
                                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                                    RelativeLayout.LayoutParams.WRAP_CONTENT));
                                    relativeParams.setMargins(100, 0, 90, 0);
                                    middleLayout.setLayoutParams(relativeParams);
                                    middleLayout.requestLayout();
                                }
                            });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.plus, R.drawable.place, R.drawable.gallery, R.drawable.camera};
        int[] color = {R.color.orange, R.color.red, R.color.green, R.color.blue};
        for (int i = 0; i < 4; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 15);
                buttonData.setBackgroundColorId(this, R.color.orange);
            } else {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 12);
                buttonData.setBackgroundColorId(this, color[i]);
            }

            buttonDatas.add(buttonData);
        }
        aaebutton.setButtonDatas(buttonDatas);

        aaebutton.setButtonEventListener(new ButtonEventListener() {

            @Override
            public void onButtonClicked(int index) {
                //do whatever you want,the param index is counted from startAngle to endAngle,
                //the value is from 1 to buttonCount - 1(buttonCount if aebIsSelectionMode=true)
                //dismissKeyboard();
                if (index >= 1) {
                   // dismissKeyboard();
                }

            }

            @Override
            public void onExpand() {


            }

            @Override
            public void onCollapse() {

            }
        });

        initRecordFunction();


        // emijo functionality
        emojIcon=new EmojIconActions(this,rootView,messageinputView,emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard","open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard","close");
            }
        });

    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }




    private void initRecordFunction() {
        //IMPORTANT
        recordButton.setRecordView(recordView);

        // if you want to click the button (in case if you want to make the record button a Send Button for example..)
//        recordButton.setListenForRecord(false);

        //ListenForRecord must be false ,otherwise onClick will not be called


        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 130
        recordView.setCancelBounds(130);


        recordView.setSmallMicColor(Color.parseColor("#c2185b"));


        //prevent recording under one Second
        recordView.setLessThanSecondAllowed(false);


        recordView.setSlideToCancelText("Slide To Cancel");


        recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);


        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                Log.d("RecordView", "onStart");
                recordView.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "OnStartRecord", Toast.LENGTH_SHORT).show();
                startRecording();

            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                recordView.setVisibility(View.GONE);
                Log.d("RecordView", "onCancel");
                if (mRecorder != null) {
                    stopRecording(false);


                }
            }

            @Override
            public void onFinish(long recordTime) {

                String time = getHumanTimeText(recordTime);
                Toast.makeText(MainActivity.this, "onFinishRecord - Recorded Time is: " + time + " File" + mOutputFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                Log.d("RecordView", "onFinish");
                recordView.setVisibility(View.GONE);
                Log.d("RecordTime", time);
                if (mRecorder != null) {
                    stopRecording(true);

                }
            }

            @Override
            public void onLessThanSecond() {
                Toast.makeText(MainActivity.this, "OnLessThanSecond", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onLessThanSecond");
                recordView.setVisibility(View.GONE);
                if (mRecorder != null) {
                    stopRecording(false);

                }
            }
        });
    }


    private void startRecording() {
        try {

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
                mRecorder.setAudioEncodingBitRate(48000);
            } else {
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mRecorder.setAudioEncodingBitRate(64000);
            }
            mRecorder.setAudioSamplingRate(16000);
            mOutputFile = getOutputFile();
            mOutputFile.getParentFile().mkdirs();
            mRecorder.setOutputFile(mOutputFile.getAbsolutePath());

            try {
                mRecorder.prepare();
                mRecorder.start();
                mStartTime = SystemClock.elapsedRealtime();
                mHandler.postDelayed(mTickExecutor, 100);
                Log.d("Voice Recorder", "started recording to " + mOutputFile.getAbsolutePath());
            } catch (IOException e) {
                Log.e("Voice Recorder", "prepare() failed " + e.getMessage());
            }


        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "Problem:" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void stopRecording(boolean saveFile) {
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mStartTime = 0;
            mHandler.removeCallbacks(mTickExecutor);
            if (!saveFile && mOutputFile != null) {
                mOutputFile.delete();
            }
        } catch (Exception ex) {

        }

    }

    private void tick() {
        long time = (mStartTime < 0) ? 0 : (SystemClock.elapsedRealtime() - mStartTime);
        int minutes = (int) (time / 60000);
        int seconds = (int) (time / 1000) % 60;
        int milliseconds = (int) (time / 100) % 10;
        getHumanTimeText(milliseconds);

        //mTimerTextView.setText(minutes+":"+(seconds < 10 ? "0"+seconds : seconds)+"."+milliseconds);
        if (mRecorder != null) {
            amplitudes[i] = mRecorder.getMaxAmplitude();
            //Log.d("Voice Recorder","amplitude: "+(amplitudes[i] * 100 / 32767));
            if (i >= amplitudes.length - 1) {
                i = 0;
            } else {
                ++i;
            }
        }
    }

    private File getOutputFile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                + "/Voice Recorder/RECORDING_"
                + dateFormat.format(new Date())
                + ".m4a");
    }

    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }


}

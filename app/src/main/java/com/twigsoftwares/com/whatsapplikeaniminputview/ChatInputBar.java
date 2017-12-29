package com.twigsoftwares.com.whatsapplikeaniminputview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * Created by Tushar on 29/12/17.
 */


@SuppressWarnings({"WeakerAccess", "unused"})
public class ChatInputBar extends RelativeLayout
        implements View.OnClickListener, TextWatcher {


    private EmojIconActions emojIcon;

    protected hani.momanii.supernova_emoji_library.Helper.EmojiconEditText messageInput;
    protected AllAngleExpandableButton expandableButton;
    protected ImageButton messageSendButton;
    protected ImageView emojiButton;
    protected RecordButton recordButton;
    protected com.devlomi.record_view.RecordView recordView;
    protected RelativeLayout middleLayout;

    private RelativeLayout rootView;

    private CharSequence input;
    private InputListener inputListener;
    private AllAngleButtonClickListener allAngleButtonClickListener;
    private EmojiKeyboardOpenCloseListener emojiKeyboardOpenCloseListener;
    private RecordingListener recordingListener;


    //recording Functionality
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


    public ChatInputBar(Context context) {
        super(context);
        init(context);
    }

    public ChatInputBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public ChatInputBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }


    public void setRootLayout(Activity activity, RelativeLayout rootView) {

        this.rootView = rootView;

        // emijo functionality
        emojIcon = new EmojIconActions(activity, this.rootView, messageInput, emojiButton);

        emojIcon.ShowEmojIcon();

        emojIcon.setIconsIds(R.drawable.keyboardnew, R.drawable.smiley);

        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
                emojiKeyboardOpenCloseListener.onEmojiKeyboardOpen();
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
                emojiKeyboardOpenCloseListener.onEmojiKeyboardClosed();
            }
        });
    }

    /**
     * Sets callback for 'submit' button.
     *
     * @param inputListener input callback
     */
    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public void setAllAngleButtonClickListener(AllAngleButtonClickListener allAngleButtonClickListener) {
        this.allAngleButtonClickListener = allAngleButtonClickListener;
    }

    public void setEmojiKeyboardOpenCloseListener(EmojiKeyboardOpenCloseListener emojiKeyboardOpenCloseListener) {
        this.emojiKeyboardOpenCloseListener = emojiKeyboardOpenCloseListener;
    }

    public void setRecordingListener(RecordingListener recordingListener) {
        this.recordingListener = recordingListener;
    }

    /**
     * Returns EditText for messages input
     *
     * @return EditText
     */
    public hani.momanii.supernova_emoji_library.Helper.EmojiconEditText getInputEditText() {
        return messageInput;
    }

    /**
     * Returns `submit` button
     *
     * @return ImageButton
     */
    public ImageButton getButton() {
        return messageSendButton;
    }

    public RecordButton getRecordButton() {
        return recordButton;
    }

    public AllAngleExpandableButton getAttachmentButton() {
        return expandableButton;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        input = charSequence;
        messageSendButton.setEnabled(input.length() > 0);

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
                            messageSendButton.setVisibility(View.VISIBLE);
                            expandableButton.setVisibility(View.GONE);
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

            messageSendButton.animate()
                    .translationY(messageSendButton.getHeight() - messageSendButton.getHeight())
                    .alpha(0.0f)
                    .setDuration(50)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            messageSendButton.setVisibility(View.GONE);
                            recordButton.setVisibility(View.VISIBLE);
                            expandableButton.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.sendButton) {
            boolean isSubmitted = onSubmit();
            if (isSubmitted) {
                messageInput.setText("");
            }
        }


    }

    private boolean onSubmit() {
        return inputListener != null && inputListener.onSubmit(input);
    }


    private void init(final Context context, AttributeSet attrs) {

        init(context);


        messageSendButton.setOnClickListener(this);
        expandableButton.setOnClickListener(this);
        messageInput.addTextChangedListener(this);
        messageInput.setText("");

        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.plus, R.drawable.place, R.drawable.gallery, R.drawable.camera};
        int[] color = {R.color.orange, R.color.red, R.color.green, R.color.blue};
        for (int i = 0; i < 4; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(context, drawable[i], 15);
                buttonData.setBackgroundColorId(context, R.color.orange);
            } else {
                buttonData = ButtonData.buildIconButton(context, drawable[i], 12);
                buttonData.setBackgroundColorId(context, color[i]);
            }

            buttonDatas.add(buttonData);
        }
        expandableButton.setButtonDatas(buttonDatas);


        //record button
        recordButton.setRecordView(recordView);
        recordView.setCancelBounds(130);
        recordView.setSmallMicColor(Color.parseColor("#c2185b"));
        //prevent recording under one Second
        recordView.setLessThanSecondAllowed(false);
        recordView.setSlideToCancelText("Slide To Cancel");
        recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);


        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

                recordView.setVisibility(View.VISIBLE);
                startRecording();
                recordingListener.onStarted();

            }

            @Override
            public void onCancel() {
                recordView.setVisibility(View.GONE);
                if (mRecorder != null) {
                    stopRecording(false);
                }
                recordingListener.onCancelled();

            }

            @Override
            public void onFinish(long recordTime) {

                String time = getHumanTimeText(recordTime);
                recordView.setVisibility(View.GONE);
                if (mRecorder != null) {
                    stopRecording(true);

                }

                recordingListener.onCompleted(mOutputFile.getAbsolutePath());

            }

            @Override
            public void onLessThanSecond() {
                recordView.setVisibility(View.GONE);
                recordingListener.onInvalid(false);
            }
        });


    }

    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    private void init(Context context) {

        inflate(context, R.layout.chat_input_bar, this);

        messageInput = (EmojiconEditText) findViewById(R.id.messageinput);
        messageSendButton = (ImageButton) findViewById(R.id.sendButton);
        expandableButton = (AllAngleExpandableButton) findViewById(R.id.button_expandable);
        emojiButton = (ImageView) findViewById(R.id.emojibutton);
        recordView = (RecordView) findViewById(R.id.record_view);
        recordButton = (RecordButton) findViewById(R.id.record_button);
        middleLayout = (RelativeLayout) findViewById(R.id.middlelayout);


        expandableButton.setButtonEventListener(new ButtonEventListener() {

            @Override
            public void onButtonClicked(int index) {
                //do whatever you want,the param index is counted from startAngle to endAngle,
                //the value is from 1 to buttonCount - 1(buttonCount if aebIsSelectionMode=true)
                //dismissKeyboard();
                if (index == 1) {
                    allAngleButtonClickListener.onLocationClicked();
                } else if (index == 2) {
                    allAngleButtonClickListener.onImageClicked();
                } else if (index == 3) {
                    allAngleButtonClickListener.onCameraClicked();
                }

            }

            @Override
            public void onExpand() {

                allAngleButtonClickListener.onExpand();
            }

            @Override
            public void onCollapse() {
                allAngleButtonClickListener.onCollapse();
            }
        });

    }


    private void setCursor(Drawable drawable) {
        if (drawable == null) return;

        try {
            final Field drawableResField = TextView.class.getDeclaredField("mCursorDrawableRes");
            drawableResField.setAccessible(true);

            final Object drawableFieldOwner;
            final Class<?> drawableFieldClass;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                drawableFieldOwner = this.messageInput;
                drawableFieldClass = TextView.class;
            } else {
                final Field editorField = TextView.class.getDeclaredField("mEditor");
                editorField.setAccessible(true);
                drawableFieldOwner = editorField.get(this.messageInput);
                drawableFieldClass = drawableFieldOwner.getClass();
            }
            final Field drawableField = drawableFieldClass.getDeclaredField("mCursorDrawable");
            drawableField.setAccessible(true);
            drawableField.set(drawableFieldOwner, new Drawable[]{drawable, drawable});
        } catch (Exception ignored) {
        }
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
            //Toast.makeText(con, "Problem:" + ex.getMessage(), Toast.LENGTH_SHORT).show();
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

    private File getOutputFile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()
                + "/Voice Recorder/RECORDING_"
                + dateFormat.format(new Date())
                + ".m4a");
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

    /**
     * Interface definition for a callback to be invoked when user pressed 'submit' button
     */
    public interface InputListener {

        /**
         * Fires when user presses 'send' button.
         *
         * @param input input entered by user
         * @return if input text is valid, you must return {@code true} and input will be cleared, otherwise return false.
         */
        boolean onSubmit(CharSequence input);
    }

    public interface EmojiKeyboardOpenCloseListener {
        void onEmojiKeyboardOpen();

        void onEmojiKeyboardClosed();
    }

    /**
     * Interface definition for a callback to be invoked when user pressed 'submit' button
     */
    public interface AllAngleButtonClickListener {

        void onLocationClicked();

        void onImageClicked();

        void onCameraClicked();

        void onExpand();

        void onCollapse();
    }

    /**
     * Interface definition for a callback to be invoked when user pressed 'submit' button
     */
    public interface RecordingListener {

        void onStarted();

        void onCancelled();

        void onInvalid(boolean isValid);

        void onCompleted(String recordedFileName);

    }
}

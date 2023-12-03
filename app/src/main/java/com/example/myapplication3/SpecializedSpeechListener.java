package com.example.myapplication3;
import android.content.Intent;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.text.MessageFormat;
import java.util.ArrayList;
import androidx.fragment.app.FragmentActivity;
import com.example.myapplication3.databinding.FragmentAct3Binding;
public class SpecializedSpeechListener implements android.speech.RecognitionListener {
    private FragmentActivity mCurrentActivity;
    private FragmentAct3Binding mBinding;

    private boolean mDetectedBeginningOfSpeech = false;
    private boolean mDetectedEndOfSpeech = false;
    private SpeechRecognizer mSR;
    private TextCollector mTC = null;


    public SpecializedSpeechListener(FragmentActivity a, FragmentAct3Binding binding, TextCollector tc, SpeechRecognizer sr) {
        this.mCurrentActivity = a;
        this.mBinding = binding;
        this.mTC = tc;
        this.mSR = sr;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
    }

    @Override
    public void onBeginningOfSpeech() {
        this.mDetectedBeginningOfSpeech = true;
    }

    @Override
    public void onRmsChanged(float v) {
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
    }

    @Override
    public void onEndOfSpeech() {
        this.mDetectedEndOfSpeech = true;
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.wtf(null, "FAILED: " + errorMessage);
    }

    @Override
    public void onResults(Bundle bundle) {
        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text;
        try {
            text = data.get(0);
        }catch(Exception e) {
            Log.wtf(null,e.getMessage());
            return;
        }

        if (this.mSR != null)
            this.mSR.stopListening();

        if (this.mTC != null)
            this.mTC.HandleAdditionalText(text);
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text;
        try {
            text = data.get(0);
        }catch(NullPointerException e) {
            Log.wtf(null,e.getMessage());
            return;
        }
        this.mBinding.textviewFirst.setText(MessageFormat.format("{0}{1}", (String) this.mBinding.textviewFirst.getText(), text));
    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    private String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                this.mCurrentActivity.setResult(-1);
                this.mCurrentActivity.finish();
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}

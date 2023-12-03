package com.example.myapplication3;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.myapplication3.databinding.FragmentAct3Binding;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Act3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Act3Fragment extends Fragment implements RecordPermissionListener, TextCollector{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentAct3Binding binding;
    private SpeechRecognizer mSpeechRecognizer;
    private boolean mBlnListening = false;
    private boolean mBlnAllowedToListen = false;
    private String mCollectedString;
    private AlertDialog.Builder mMsgBoxFactory = null;

    public Act3Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Act3Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Act3Fragment newInstance(String param1, String param2) {
        Act3Fragment fragment = new Act3Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mMsgBoxFactory = new AlertDialog.Builder(new ContextThemeWrapper(this.getContext(), R.style.AlertDialogCustom));
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.mCollectedString = "";

        // Inflate the layout for this fragment
        this.binding = FragmentAct3Binding.inflate(inflater, container, false);
        this.binding.voiceAction.setText(R.string.waiting_for_permission_to_listen);
        this.binding.voiceAction.setActivated(false);
        this.binding.voiceAction.setOnClickListener(currentView -> {
            if (!this.mBlnAllowedToListen)
                return;
            if (!this.mBlnListening){
                this.mSpeechRecognizer = this.InitializeSpeechRecognizer();
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                this.mSpeechRecognizer.startListening(i);
                this.binding.voiceAction.setText(R.string.click_to_finish_listening);
                this.mBlnListening = true;
            } else {
                this.mSpeechRecognizer.stopListening();
                FragmentActivity a = this.getActivity();
                if (a != null) {
                    Intent i = new Intent();
                    i.putExtra("voice_input", this.mCollectedString);
                    a.setResult(100, i);
                    a.finish();
                }
            }

        });

        return binding.getRoot();
        //return inflater.inflate(R.layout.fragment_act3, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        MainActivity3 a = (MainActivity3) this.getActivity();
        if (a != null) {
            try {
                a.SavePermissionHandler(this);
                a.RequestListenPermission();
            }catch(Exception e){
                AlertDialog msgBoxNullError = this.mMsgBoxFactory.create();
                msgBoxNullError.setTitle(R.string.error_getting_voice_text_title);
                msgBoxNullError.setMessage(e.getMessage());
                msgBoxNullError.show();
            }
        }
    }

    private SpeechRecognizer InitializeSpeechRecognizer(){
        SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(this.getContext());
        sr.setRecognitionListener(new SpecializedSpeechListener(this.getActivity(),this.binding, (TextCollector) this, sr));
        return sr;
    }

    @Override
    public void onDestroy() {
        this.binding = null;
        this.mSpeechRecognizer = null;
        this.mParam1 = null;
        this.mParam2 = null;
        this.mMsgBoxFactory = null;
        super.onDestroy();
    }


    @Override
    public void HandleRecordPermissionResult(boolean isAllowed) {
        if (isAllowed){
            this.binding.voiceAction.setText(R.string.click_to_start_recording);
            this.binding.voiceAction.setActivated(true);
            this.mBlnAllowedToListen = true;
        } else {
            AlertDialog msgBoxPermError = this.mMsgBoxFactory.create();
            msgBoxPermError.setTitle(R.string.permission_denied_title);
            msgBoxPermError.setMessage(
                    this.getResources().getString(R.string.permission_denied_message));
            msgBoxPermError.show();
        }
    }

    @Override
    public void HandleAdditionalText(String s) {
        if (this.mCollectedString.length() > 1)
            this.mCollectedString += ". ";
        this.mCollectedString += s;
        this.mSpeechRecognizer.stopListening();
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.mSpeechRecognizer.startListening(i);
    }
}
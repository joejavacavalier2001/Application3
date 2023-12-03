package com.example.myapplication3;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;
import android.content.Intent;
import com.example.myapplication3.databinding.FragmentSecondBinding;
import android.app.AlertDialog;
import androidx.activity.result.ActivityResult;

public class SecondFragment extends Fragment implements RogersActivityResultListener{

    private FragmentSecondBinding binding;

    private AlertDialog.Builder mMsgBoxFactory = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity fa = this.getActivity();
        com.example.myapplication3.MainActivity ma = (com.example.myapplication3.MainActivity) fa;
        if (ma != null) {
            ma.SaveResultListener(this);
        }
        Context c = this.getContext();

        this.mMsgBoxFactory = new AlertDialog.Builder(new ContextThemeWrapper(c, R.style.AlertDialogCustom));
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.startVoiceToText.setOnClickListener(view1 -> {
            Intent myIntent = new Intent(getContext(), MainActivity3.class);
            FragmentActivity fa = this.getActivity();
            com.example.myapplication3.MainActivity ma = (com.example.myapplication3.MainActivity) fa;
            if (ma != null) {
                ma.LaunchIntent(myIntent);
            }
        });
        binding.buttonSecond.setOnClickListener(view12 ->
                NavHostFragment.findNavController(SecondFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment));
    }

    @Override
    public void onDestroy() {
        this.mMsgBoxFactory = null;
        this.binding = null;
        super.onDestroy();
    }

    @Override
    public void HandleResult(ActivityResult result) {
        int resultCode = result.getResultCode();
        if (resultCode > 0) {
            Intent data = result.getData();
            String strResult = "";
            if (data != null) {
                strResult = data.getStringExtra("voice_input");
            } else {
                AlertDialog msgBoxNullError = this.mMsgBoxFactory.create();
                msgBoxNullError.setTitle(R.string.error_getting_voice_text_title);
                msgBoxNullError.setMessage(
                        this.getResources().getString(R.string.error_getting_voice_text_message));
                msgBoxNullError.show();
            }
            if ((strResult != null) && (strResult.length() > 0))
                this.binding.textviewSecond.setText(strResult);
        } else {
            AlertDialog msgBoxPermError = this.mMsgBoxFactory.create();
            msgBoxPermError.setTitle(R.string.permission_denied_title);
            msgBoxPermError.setMessage(
                    this.getResources().getString(R.string.permission_denied_message));
            msgBoxPermError.show();
        }
    }
}
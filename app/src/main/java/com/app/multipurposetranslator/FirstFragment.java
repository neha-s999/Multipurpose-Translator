package com.app.multipurposetranslator;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.Locale;


public class FirstFragment extends Fragment {

    private TextView mSourceLang;
    private EditText mSourceText;
    private Button mTranslateBtn;
    private TextView mTranslatedText;
    private String sourceText;
    private TextToSpeech mTTS;
    private DatabaseReference databaseReference;
    String url = "https://language-translator-5ad9b-default-rtdb.firebaseio.com/";
    private FloatingActionButton spkBtn, saveBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_first, container, false);
        mSourceLang = v.findViewById(R.id.sourcelanguage);
        mSourceText = v.findViewById(R.id.sourcetext);
        mTranslateBtn = v.findViewById(R.id.translate);
        mTranslatedText = v.findViewById(R.id.translatedtext);

        mTTS = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.getDefault());

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        spkBtn.setEnabled(true);
                        speak();
                    }
                } else {
                    Log.e("TextToSpeech", "Initialization failed");
                }
            }
        });


        spkBtn = v.findViewById(R.id.speak_button);
        spkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });


        mTranslateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                identifyLanguage();
            }
        });

        databaseReference = FirebaseDatabase.getInstance(url).getReference().child("Words");

        saveBtn = v.findViewById(R.id.save_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = mTranslatedText.getText().toString();
                databaseReference.setValue(word);
            }
        });
        return v;
    }


    private void identifyLanguage() {

        sourceText = mSourceText.getText().toString();
        FirebaseLanguageIdentification identifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
        mSourceLang.setText("Detecting...");


        identifier.identifyLanguage(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s.equals("und")) {
                    Toast.makeText(getContext(), "lANGUAGE NOT SUPPORTED", Toast.LENGTH_SHORT).show();
                } else {
                    getLanguageCode(s);
                }
            }
        });

    }

    private void getLanguageCode(String language) {
        int langCode;
        switch (language) {
            case "hi":
                langCode = FirebaseTranslateLanguage.HI;
                mSourceLang.setText("Hindi");
                break;
            case "ar":
                langCode = FirebaseTranslateLanguage.AR;
                mSourceLang.setText("Arabic");
                break;

            default:
                langCode = 0;
        }
        translateText(langCode);
    }

    private void translateText(final int langCode) {
        mTranslatedText.setText("Translating...");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(langCode)
                .setTargetLanguage(FirebaseTranslateLanguage.EN)
                .build();

        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translator.translate(sourceText).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        mTranslatedText.setText(s);

                    }
                });
            }
        });


    }


    @Override
    public void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    private void speak() {
        String message = mTranslatedText.getText().toString();
        mTTS.setSpeechRate(1);
        mTTS.setPitch(1);
        mTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null);
    }
}
package VoiceRecognition;

import android.util.Log;

import java.io.FileInputStream;
import java.io.InputStream;


import VoiceRecognition.fingerprint.FingerprintSimilarity;
import VoiceRecognition.wave.Wave;

public class Compare{
    private final String TAG = getClass().getName();
    float result;

    public float match(String filename1, String filename2){

//        String filename1 = "path to wav1";
//        String filename2 = "path to wav2";

        try {
            InputStream fis1 = null, fis2 = null;

            fis1 = new FileInputStream(filename1);
            fis2 = new FileInputStream(filename2);

            Wave wave1 = new Wave(fis1), wave2 = new Wave(fis2);
            FingerprintSimilarity similarity;

            similarity = wave1.getFingerprintSimilarity(wave2);
            result = similarity.getSimilarity() * 100;
            return result;
        } catch (Exception e){
            Log.e(TAG, e.getStackTrace().toString());

        }
        return -1;
    }
}

//new compare.match()
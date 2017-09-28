package VoiceRecorder;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import WavRecorder.omrecorder.AudioChunk;
import WavRecorder.omrecorder.AudioRecordConfig;
import WavRecorder.omrecorder.OmRecorder;
import WavRecorder.omrecorder.PullTransport;
import WavRecorder.omrecorder.PullableSource;
import WavRecorder.omrecorder.Recorder;
import WavRecorder.omrecorder.WriteAction;

/**
 * Created by Yuval on 06/09/2017.
 */

public class WavRecorder {

    private Recorder recorder;
    private String filepath;

    public WavRecorder(String path){
        filepath = path;
        setupRecorder();
    }

    public void startRecording(){
        recorder.startRecording();
    }

    public void stopRecording(){
        try {
            recorder.stopRecording();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setupRecorder() {
        recorder = OmRecorder.wav(
                new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
                    @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                        //animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                    }
                }), file());
    }


    private void setupNoiseRecorder() {
        recorder = OmRecorder.wav(
                new PullTransport.Noise(mic(),
                        new PullTransport.OnAudioChunkPulledListener() {
                            @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                                //animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                            }
                        },
                        new WriteAction.Default(),
                        new Recorder.OnSilenceListener() {
                            @Override public void onSilence(long silenceTime) {
                                Log.e("silenceTime", String.valueOf(silenceTime));
                            }
                        }, 200
                ), file()
        );
    }

    private PullableSource mic() {
        return new PullableSource.Default(
                new AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                        AudioFormat.CHANNEL_IN_MONO, 44100
                )
        );
    }

    private File file() {
        return new File(Environment.getExternalStorageDirectory(), this.filepath);
    }

    public void setFilepath(String newFilepath){
        this.filepath = newFilepath;
        setupRecorder();
    }


}

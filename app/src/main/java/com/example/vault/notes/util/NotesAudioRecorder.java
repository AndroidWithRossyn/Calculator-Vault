package com.example.vault.notes.util;

import android.media.AudioRecord;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class NotesAudioRecorder {
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER;
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_AUDIO_ENCODING = 2;
    private static final int RECORDER_BPP = 16;
    private static final int RECORDER_CHANNELS = 12;
    private static final int RECORDER_SAMPLERATE = 8000;
    private int bufferSize = 0;
    private boolean isRecording = false;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        sb.append("AudioRecorder/");
        AUDIO_RECORDER_FOLDER = sb.toString();
    }

    public void startRecording(final boolean z) {
        AudioRecord audioRecord = new AudioRecord(1, RECORDER_SAMPLERATE, 12, 2, this.bufferSize);
        this.recorder = audioRecord;
        this.recorder.startRecording();
        this.isRecording = true;
        this.recordingThread = new Thread(new Runnable() {
            public void run() {
                NotesAudioRecorder.this.writeAudioDataToFile(z);
            }
        }, "AudioRecorder Thread");
        this.recordingThread.start();
    }

    public void writeAudioDataToFile(boolean z) {
        FileOutputStream fileOutputStream;
        byte[] bArr = new byte[this.bufferSize];
        try {
            fileOutputStream = new FileOutputStream(getTempFilename(), z);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileOutputStream = null;
        }
        if (fileOutputStream != null) {
            while (this.isRecording) {
                if (-3 != this.recorder.read(bArr, 0, this.bufferSize)) {
                    try {
                        fileOutputStream.write(bArr);
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            try {
                fileOutputStream.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }
    }

    public String getFilename() {
        NotesCommon.GetDate();
        String Gettime = NotesCommon.Gettime();
        String replace = Gettime.replace(" ", "");
        String replace2 = Gettime.replace(":", "");
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append(AUDIO_RECORDER_FOLDER);
        String sb2 = sb.toString();
        File file = new File(sb2);
        if (!file.exists()) {
            file.mkdirs();
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Recording_");
        sb3.append(replace);
        sb3.append("_");
        sb3.append(replace2);
        sb3.append(AUDIO_RECORDER_FILE_EXT_WAV);
        File file2 = new File(sb2, sb3.toString());
        if (!file2.exists()) {
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file2.getAbsolutePath();
    }

    public String getTempFilename() {
        String path = Environment.getExternalStorageDirectory().getPath();
        File file = new File(path, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(path, AUDIO_RECORDER_TEMP_FILE);
        if (file2.exists()) {
            file2.delete();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(file.getAbsolutePath());
        sb.append("/");
        sb.append(AUDIO_RECORDER_TEMP_FILE);
        return sb.toString();
    }

    public void stopRecording(boolean z) {
        AudioRecord audioRecord = this.recorder;
        if (audioRecord != null) {
            this.isRecording = false;
            audioRecord.stop();
            this.recorder.release();
            this.recorder = null;
            this.recordingThread = null;
        }
        if (z) {
            CombineWaveFile(getTempFilename(), getFilename());
            deleteTempFile();
        }
    }

    private void deleteTempFile() {
        new File(getTempFilename()).delete();
    }

    public void CombineWaveFile(String str, String str2) {
        this.bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, 12, 2);
        long j = (long) 32000;
        byte[] bArr = new byte[this.bufferSize];
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "myMix.wav");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileInputStream fileInputStream = new FileInputStream(str);
            FileInputStream fileInputStream2 = new FileInputStream(str2);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            long size = fileInputStream2.getChannel().size() + fileInputStream.getChannel().size();
            WriteWaveFileHeader(fileOutputStream, size, size + 36, 8000, 2, j);
            while (fileInputStream.read(bArr) != -1) {
                fileOutputStream.write(bArr);
            }
            while (fileInputStream2.read(bArr) != -1) {
                fileOutputStream.write(bArr);
            }
            fileOutputStream.close();
            fileInputStream.close();
            fileInputStream2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void WriteWaveFileHeader(FileOutputStream fileOutputStream, long j, long j2, long j3, int i, long j4) throws IOException {
        FileOutputStream fileOutputStream2 = fileOutputStream;
        fileOutputStream.write(new byte[]{82, 73, 70, 70, (byte) ((int) (j2 & 255)), (byte) ((int) ((j2 >> 8) & 255)), (byte) ((int) ((j2 >> 16) & 255)), (byte) ((int) ((j2 >> 24) & 255)), 87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0, (byte) i, 0, (byte) ((int) (j3 & 255)), (byte) ((int) ((j3 >> 8) & 255)), (byte) ((int) ((j3 >> 16) & 255)), (byte) ((int) ((j3 >> 24) & 255)), (byte) ((int) (j4 & 255)), (byte) ((int) ((j4 >> 8) & 255)), (byte) ((int) ((j4 >> 16) & 255)), (byte) ((int) ((j4 >> 24) & 255)), 4, 0, 16, 0, 100, 97, 116, 97, (byte) ((int) (j & 255)), (byte) ((int) ((j >> 8) & 255)), (byte) ((int) ((j >> 16) & 255)), (byte) ((int) (255 & (j >> 24)))}, 0, 44);
    }
}

package com.example.vault.notes.util;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import org.apache.commons.io.FileUtils;

public class AudioRecorder {
    public static boolean isRecordStarted = false;
    public String Recording = "Recordings/";
    public String RecordingFileExtension = ".m4a";
    Context context;
    public String firstRecording = "firstRecording";
    public File firstRecordingFile;
    public boolean hasRecording = false;
    public MediaRecorder recorder;
    public String recordingFolderPath = "";
    public File recordingfolder;
    public String secondRecording = "secondRecording";
    public File secondRecordingFile;
    public String tempRecording = "tempRecording";
    public File tempRecordingFile;

    public AudioRecorder(Context context2) {
        this.context = context2;
        createRecordingFolder();
    }

    public void StartRecording() {
        try {
            this.recorder = new MediaRecorder();
            this.recorder.reset();
            this.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            this.recorder.setOutputFormat(2);
            this.recorder.setAudioEncoder(3);
            if (!this.hasRecording) {
                createFirstRecording();
                this.recorder.setOutputFile(this.firstRecordingFile.getAbsolutePath());
            } else {
                createSecondRecording();
                this.recorder.setOutputFile(this.secondRecordingFile.getAbsolutePath());
            }
            this.recorder.prepare();
            this.recorder.start();
            isRecordStarted = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("masl", e.toString());
        }
    }

    public String StopRecording() {
        if (isRecordStarted) {
            isRecordStarted = false;
            this.recorder.stop();
            this.recorder.reset();
            this.recorder.release();
            this.recorder = null;
            if (!this.hasRecording) {
                this.hasRecording = true;
            } else if (this.firstRecordingFile.exists() && this.firstRecordingFile != null && this.secondRecordingFile.exists() && this.secondRecordingFile != null) {
                return mergeM4Afiles(this.context);
            }
        }
        return this.firstRecordingFile.getAbsolutePath();
    }

    public String mergeM4Afiles(Context context2) {
        try {
            Movie movie = new Movie();
            Movie[] movieArr = {MovieCreator.build(this.firstRecordingFile.getAbsolutePath()), MovieCreator.build(this.secondRecordingFile.getAbsolutePath())};
            LinkedList linkedList = new LinkedList();
            for (Movie tracks : movieArr) {
                for (Track track : tracks.getTracks()) {
                    if (track.getHandler().equals("soun")) {
                        linkedList.add(track);
                    }
                }
            }
            if (linkedList.size() > 0) {
                movie.addTrack(new AppendTrack((Track[]) linkedList.toArray(new Track[linkedList.size()])));
            }
            Container build = new DefaultMp4Builder().build(movie);
            createTempRecording();
            FileChannel channel = new RandomAccessFile(this.tempRecordingFile, "rw").getChannel();
            build.writeContainer(channel);
            channel.close();
            deleteFirstRecording();
            createFirstRecording();
            FileUtils.copyFile(this.tempRecordingFile, this.firstRecordingFile);
            deleteTempRecording();
            return this.firstRecordingFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void createRecordingFolder() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.context.getFilesDir());
        sb.append(File.separator);
        sb.append(this.Recording);
        this.recordingFolderPath = sb.toString();
        this.recordingfolder = new File(this.context.getFilesDir(), this.Recording);
        this.recordingFolderPath = this.recordingfolder.getAbsolutePath();
        if (!this.recordingfolder.exists()) {
            this.recordingfolder.mkdirs();
        }
    }

    public void createFirstRecording() {
        try {
            String str = this.recordingFolderPath;
            StringBuilder sb = new StringBuilder();
            sb.append(this.firstRecording);
            sb.append(this.RecordingFileExtension);
            this.firstRecordingFile = new File(str, sb.toString());
            if (!this.firstRecordingFile.exists()) {
                this.firstRecordingFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createSecondRecording() {
        try {
            String str = this.recordingFolderPath;
            StringBuilder sb = new StringBuilder();
            sb.append(this.secondRecording);
            sb.append(this.RecordingFileExtension);
            this.secondRecordingFile = new File(str, sb.toString());
            if (!this.secondRecordingFile.exists()) {
                this.secondRecordingFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createTempRecording() {
        try {
            String str = this.recordingFolderPath;
            StringBuilder sb = new StringBuilder();
            sb.append(this.tempRecording);
            sb.append(this.RecordingFileExtension);
            this.tempRecordingFile = new File(str, sb.toString());
            if (this.tempRecordingFile.exists()) {
                this.tempRecordingFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFirstRecording() {
        File file = this.firstRecordingFile;
        if (file != null && file.exists()) {
            this.firstRecordingFile.delete();
        }
    }

    public void deleteSecondRecording() {
        File file = this.secondRecordingFile;
        if (file != null && file.exists()) {
            this.secondRecordingFile.delete();
        }
    }

    public void deleteTempRecording() {
        File file = this.tempRecordingFile;
        if (file != null && file.exists()) {
            this.tempRecordingFile.delete();
        }
    }
}

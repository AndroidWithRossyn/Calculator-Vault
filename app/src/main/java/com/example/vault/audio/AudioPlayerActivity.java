package com.example.vault.audio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


import com.example.vault.Flaes;
import com.example.vault.R;
import com.example.vault.audio.model.AudioEnt;
import com.example.vault.audio.util.AudioDAL;
import com.example.vault.BaseActivity;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Common;

public class AudioPlayerActivity extends BaseActivity implements OnCompletionListener, OnSeekBarChangeListener {
    public static ProgressDialog AudioDecrytionDialog;

    public String AppPath = "";
    private boolean IsStop = false;
    ArrayList<AudioEnt> audioEntList = new ArrayList<>();

    public SeekBar audioProgressBar;
    private LinearLayout btnPlayerForwardTrack;
    private LinearLayout btnPlayerPlayPause;
    private LinearLayout btnPlayerPreviousTrack;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                AudioPlayerActivity.this.hideAudioDecrytionProgress();
                File file = new File(AudioPlayerActivity.this.AppPath);
                StringBuilder sb = new StringBuilder();
                sb.append(StorageOptionsCommon.STORAGEPATH);
                sb.append("/");
                sb.append(StorageOptionsCommon.AUDIOS_TEMP_FOLDER);
                sb.append(((AudioEnt) AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex)).getAudioName());
                AudioPlayerActivity.this.DecrpytAudioFile(file, new File(sb.toString()), Common.CurrentTrackIndex);
            }
            super.handleMessage(message);
        }
    };

    public Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long duration = (long) Common.mediaplayer.getDuration();
            long currentPosition = (long) Common.mediaplayer.getCurrentPosition();
            TextView access$300 = AudioPlayerActivity.this.songTotalDurationLabel;
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(Common.milliSecondsToTimer(duration));
            access$300.setText(sb.toString());
            TextView access$400 = AudioPlayerActivity.this.songCurrentDurationLabel;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("");
            sb2.append(Common.milliSecondsToTimer(currentPosition));
            access$400.setText(sb2.toString());
            int progressPercentage = Common.getProgressPercentage(currentPosition, duration);
            AudioPlayerActivity.this.audioProgressBar.setProgress(progressPercentage);
            if (progressPercentage != 100) {
                AudioPlayerActivity.this.mHandler.postDelayed(this, 100);
            } else if (Common.CurrentTrackIndex < AudioPlayerActivity.this.audioEntList.size() - 1) {
                AudioPlayerActivity.this.PlaySong(Common.CurrentTrackIndex + 1);
                Common.CurrentTrackIndex++;
                if (((AudioEnt) AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex)).getAudioName().length() > 20) {
                    AudioPlayerActivity.this.txtSongName.setText(((AudioEnt) AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex)).getAudioName().substring(0, 19));
                } else {
                    AudioPlayerActivity.this.txtSongName.setText(((AudioEnt) AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex)).getAudioName());
                }
            } else {
                AudioPlayerActivity.this.PlaySong(0);
                Common.CurrentTrackIndex = 0;
                if (((AudioEnt) AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex)).getAudioName().length() > 20) {
                    AudioPlayerActivity.this.txtSongName.setText(((AudioEnt) AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex)).getAudioName().substring(0, 19));
                } else {
                    AudioPlayerActivity.this.txtSongName.setText(((AudioEnt) AudioPlayerActivity.this.audioEntList.get(Common.CurrentTrackIndex)).getAudioName());
                }
            }
        }
    };

    public TextView songCurrentDurationLabel;

    public TextView songTotalDurationLabel;
    private Toolbar toolbar;


    public TextView txtSongName;

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
    }

    private void showAudioDecryptionProgress() {
        AudioDecrytionDialog = ProgressDialog.show(this, null, "Audio Decryption, \nPlease wait your audio file is being decrypted...", true);
    }


    public void hideAudioDecrytionProgress() {
        ProgressDialog progressDialog = AudioDecrytionDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            AudioDecrytionDialog.dismiss();
        }
    }




    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_audioplayer);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(this.toolbar);
//        getSupportActionBar().setTitle((CharSequence) "Audio Player");
        // getSupportActionBar().setTitle("");

        this.toolbar.setNavigationIcon((int) R.drawable.back_top_bar_icon);
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.txtSongName = (TextView) findViewById(R.id.txtSongTitle);
        this.btnPlayerPreviousTrack = (LinearLayout) findViewById(R.id.llPlayerPreviousTrack);
        this.btnPlayerForwardTrack = (LinearLayout) findViewById(R.id.llPlayerForwardTrack);
        this.btnPlayerPlayPause = (LinearLayout) findViewById(R.id.llPlayerPlayPause);
        this.audioProgressBar = (SeekBar) findViewById(R.id.audioProgressbar);
        this.songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        this.songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        if (Common.voiceplayer.isPlaying()) {
            Common.voiceplayer.stop();
        }
        this.audioProgressBar.setOnSeekBarChangeListener(this);
        Common.mediaplayer.setOnCompletionListener(this);
        AudioDAL audioDAL = new AudioDAL(this);
        audioDAL.OpenRead();
        this.audioEntList = (ArrayList) audioDAL.GetAudiosByAlbumId(Common.FolderId, Common.sortType);
        audioDAL.close();
        if (Common.CurrentTrackId == ((AudioEnt) this.audioEntList.get(Common.CurrentTrackNextIndex)).getId() && Common.mediaplayer.isPlaying() && Common.CurrentTrackIndex == Common.CurrentTrackNextIndex) {
            if (((AudioEnt) this.audioEntList.get(Common.CurrentTrackIndex)).getAudioName().length() > 20) {
                this.txtSongName.setText(((AudioEnt) this.audioEntList.get(Common.CurrentTrackIndex)).getAudioName().substring(0, 19));
            } else {
                this.txtSongName.setText(((AudioEnt) this.audioEntList.get(Common.CurrentTrackIndex)).getAudioName());
            }
            updateProgressBar();
            return;
        }
        Common.CurrentTrackIndex = Common.CurrentTrackNextIndex;
        PlaySong(Common.CurrentTrackIndex);
    }

    public void btnPlayerPlayPauseClick(View view) {
        if (Common.mediaplayer.isPlaying()) {
            Common.mediaplayer.pause();
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_play);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_play);
            } else {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_play);
            }
        } else if (Common.mediaplayer.isPlaying() || !this.IsStop) {
            Common.mediaplayer.start();
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
            } else {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
            }
        } else {
            this.IsStop = false;
            PlaySong(Common.CurrentTrackIndex);
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
            } else {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
            }
        }
    }

    public void btnPlayerStopClick(View view) {
        if (Common.mediaplayer.isPlaying()) {
            Common.mediaplayer.stop();
            if (Common.isTablet10Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_play);
            } else if (Common.isTablet7Inch(getApplicationContext())) {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_play);
            } else {
                this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_play);
            }
            this.IsStop = true;
        }
    }

    public void btnPlayerPreviousTrackClick(View view) {
        this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
        if (Common.CurrentTrackIndex > 0) {
            PlaySong(Common.CurrentTrackIndex - 1);
            Common.CurrentTrackIndex--;
            return;
        }
        PlaySong(this.audioEntList.size() - 1);
        Common.CurrentTrackIndex = this.audioEntList.size() - 1;
    }

    public String FileName(String str) {
        String str2 = " /";
        for (int length = str.length() - 1; length > 0; length--) {
            if (str.charAt(length) == str2.charAt(1)) {
                return str.substring(length + 1, str.length());
            }
        }
        return "";
    }

    public void btnPlayerForwardTrackClick(View view) {
        this.btnPlayerPlayPause.setBackgroundResource(R.drawable.btn_pause);
        if (Common.CurrentTrackIndex < this.audioEntList.size() - 1) {
            PlaySong(Common.CurrentTrackIndex + 1);
            Common.CurrentTrackIndex++;
            return;
        }
        PlaySong(0);
        Common.CurrentTrackIndex = 0;
    }


    public void PlaySong(int i) {
        this.AppPath = ((AudioEnt) this.audioEntList.get(i)).getFolderLockAudioLocation();
        final File file = new File(this.AppPath);
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append("/");
        sb.append(StorageOptionsCommon.AUDIOS_TEMP_FOLDER);
        sb.append(((AudioEnt) this.audioEntList.get(i)).getAudioName());
        final File file2 = new File(sb.toString());
        File file3 = new File(file2.getParent());
        if (!file3.exists()) {
            file3.mkdirs();
        }
        Log.d("CurrentlyPlayFileName", ((AudioEnt) this.audioEntList.get(i)).getAudioName());
        if (!file.exists()) {
            return;
        }
        if (!file2.exists()) {
            try {
                file2.createNewFile();
                showAudioDecryptionProgress();
                new Thread() {
                    public void run() {
                        try {
                            Flaes.decryptUsingCipherStream_AES128(file, file2);
                            Message message = new Message();
                            message.what = 1;
                            AudioPlayerActivity.this.handle.sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file2);
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
            }
            Common.mediaplayer.stop();
            Common.mediaplayer.reset();
            try {
                Common.mediaplayer.setDataSource(fileInputStream.getFD());
            } catch (IllegalArgumentException e3) {
                e3.printStackTrace();
            } catch (IllegalStateException e4) {
                e4.printStackTrace();
            } catch (IOException e5) {
                e5.printStackTrace();
            }
            try {
                Common.mediaplayer.prepare();
            } catch (IllegalStateException e6) {
                e6.printStackTrace();
            } catch (IOException e7) {
                e7.printStackTrace();
            }
            Common.mediaplayer.start();
            updateProgressBar();
        }
    }


    public void DecrpytAudioFile(File file, File file2, int i) {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileInputStream = null;
        }
        Common.mediaplayer.stop();
        Common.mediaplayer.reset();
        try {
            Common.mediaplayer.setDataSource(fileInputStream.getFD());
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (IllegalStateException e3) {
            e3.printStackTrace();
        } catch (IOException e4) {
            e4.printStackTrace();
        }
        try {
            Common.mediaplayer.prepare();
        } catch (IllegalStateException e5) {
            e5.printStackTrace();
        } catch (IOException e6) {
            e6.printStackTrace();
        }
        Common.mediaplayer.start();
        if (((AudioEnt) this.audioEntList.get(i)).getAudioName().length() > 20) {
            this.txtSongName.setText(((AudioEnt) this.audioEntList.get(i)).getAudioName().substring(0, 19));
        } else {
            this.txtSongName.setText(((AudioEnt) this.audioEntList.get(i)).getAudioName());
        }
        Common.CurrentTrackId = ((AudioEnt) this.audioEntList.get(i)).getId();
        this.audioProgressBar.setProgress(0);
        this.audioProgressBar.setMax(100);
        updateProgressBar();
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        if (Common.CurrentTrackIndex < this.audioEntList.size() - 1) {
            PlaySong(Common.CurrentTrackIndex + 1);
            Common.CurrentTrackIndex++;
            return;
        }
        PlaySong(0);
        Common.CurrentTrackIndex = 0;
    }

    public void updateProgressBar() {
        this.mHandler.postDelayed(this.mUpdateTimeTask, 100);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        this.mHandler.removeCallbacks(this.mUpdateTimeTask);
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        this.mHandler.removeCallbacks(this.mUpdateTimeTask);
        Common.mediaplayer.seekTo(Common.progressToTimer(seekBar.getProgress(), Common.mediaplayer.getDuration()));
        updateProgressBar();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            Back();
        }
        return super.onKeyDown(i, keyEvent);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        Back();
        return true;
    }

    private void Back() {
        SecurityLocksCommon.IsAppDeactive = false;
        startActivity(new Intent(this, AudioActivity.class));
        finish();
    }


    public void onPause() {
        super.onPause();
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
        }
    }


    public void onResume() {
        super.onResume();
    }
}

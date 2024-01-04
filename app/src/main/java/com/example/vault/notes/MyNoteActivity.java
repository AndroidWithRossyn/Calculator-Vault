package com.example.vault.notes;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vault.notes.model.NotesFileDB_Pojo;
import com.example.vault.notes.util.AudioRecorder;
import com.example.vault.notes.util.LinedEditText;
import com.example.vault.notes.util.NotesCommon;
import com.example.vault.notes.util.NotesFilesDAL;
import com.example.vault.notes.util.ReadNoteFromXML;
import com.flask.colorpicker.ColorPickerView;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import com.example.vault.R;
import com.example.vault.common.Constants;
import com.example.vault.panicswitch.AccelerometerListener;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.storageoption.StorageOptionsCommon;
import com.example.vault.utilities.Utilities;
import org.apache.commons.math3.analysis.interpolation.MicrosphereInterpolator;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;
import pub.devrel.easypermissions.PermissionRequest;

public class MyNoteActivity extends AppCompatActivity implements AccelerometerListener, SensorEventListener, PermissionCallbacks {
    String NotesContent;
    HashMap<String, String> NotesHashMap;
    String[] PERMISSIONS = {"android.permission.RECORD_AUDIO"};

    AudioRecorder audioRecorder;
    String audioString = "";
    Chronometer chronometer;
    Constants constants;
    long currentDuration;
    LinedEditText et_NoteContent;
    EditText et_Notetitle;
    Handler handle = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                MyNoteActivity.this.et_Notetitle.setText((CharSequence) MyNoteActivity.this.NotesHashMap.get("Title"));
                MyNoteActivity.this.et_NoteContent.setText((CharSequence) MyNoteActivity.this.NotesHashMap.get("Text"));
                if (MyNoteActivity.this.audioString.length() > 0) {
                    MyNoteActivity.this.iv_NotesplayAudio.setVisibility(View.VISIBLE);
                }
                MyNoteActivity.this.hideProgress();
            } else if (message.what == 2) {
                MyNoteActivity.this.hideProgress();
            } else if (message.what == 3) {
                MyNoteActivity myNoteActivity = MyNoteActivity.this;
                Toast.makeText(myNoteActivity, myNoteActivity.getResources().getString(R.string.note_file_exists), Toast.LENGTH_SHORT).show();
                Utilities.hideKeyboard(MyNoteActivity.this.ll_main, MyNoteActivity.this);
            }
            super.handleMessage(message);
        }
    };
    boolean hasInsertedLines = false;
    boolean hasModified = false;
    boolean hasRecorded = false;
    boolean isPlayerVisible = false;
    boolean isPlaying = false;
    boolean isRecording = false;
    ImageView iv_NotesRecordAudio;
    ImageView iv_NotesplayAudio;
    ImageView iv_next;
    ImageView iv_play;
    ImageView iv_previous;
    ScrollView ll_main;
    LinearLayout ll_notesRecordingPlayer;
    Handler mHandler;
    MediaPlayer mPlayer;

    public Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            MyNoteActivity myNoteActivity = MyNoteActivity.this;
            myNoteActivity.totalDuration = (long) myNoteActivity.mPlayer.getDuration();
            MyNoteActivity myNoteActivity2 = MyNoteActivity.this;
            myNoteActivity2.currentDuration = (long) myNoteActivity2.mPlayer.getCurrentPosition();
            MyNoteActivity.this.seekbar.setProgress(MyNoteActivity.this.notesCommon.getProgressPercentage(MyNoteActivity.this.currentDuration, MyNoteActivity.this.totalDuration));
            TextView textView = MyNoteActivity.this.tv_recEndTime;
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(MyNoteActivity.this.notesCommon.milliSecondsToTimer(MyNoteActivity.this.totalDuration));
            textView.setText(sb.toString());
            TextView textView2 = MyNoteActivity.this.tv_recStartTime;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("");
            sb2.append(MyNoteActivity.this.notesCommon.milliSecondsToTimer(MyNoteActivity.this.currentDuration));
            textView2.setText(sb2.toString());
            MyNoteActivity.this.mHandler.postDelayed(this, 100);
        }
    };
    ProgressDialog myProgressDialog = null;
    String noteColor = "#33aac0ff";
    NotesCommon notesCommon;
    NotesFilesDAL notesFilesDAL;
    String notesTitle;
    String oldNoteTitle = "";
    ReadNoteFromXML readNoteFromXML;
    String recordingFilePath = "";
    SeekBar seekbar;
    private SensorManager sensorManager;
    private Toolbar toolbar;
    long totalDuration;
    TextView tv_recEndTime;
    TextView tv_recStartTime;



        public class AudioPlayerListener implements OnClickListener {
            public AudioPlayerListener() {
            }

            public void onClick(View view) {
                int id = view.getId();
                if (id == R.id.iv_next) { /*2131296552*/
                    MyNoteActivity.this.mPlayer.seekTo(MyNoteActivity.this.notesCommon.progressToTimer(MyNoteActivity.this.seekbar.getProgress(), MyNoteActivity.this.mPlayer.getDuration()) + MicrosphereInterpolator.DEFAULT_MICROSPHERE_ELEMENTS);
                    MyNoteActivity.this.updateProgressBar();

                } else if (id == R.id.iv_play) { /*2131296554*/
                    MyNoteActivity.this.PlayPause();

                } else if (id == R.id.iv_previous) { /*2131296555*/
                    MyNoteActivity.this.mPlayer.seekTo(MyNoteActivity.this.notesCommon.progressToTimer(MyNoteActivity.this.seekbar.getProgress(), MyNoteActivity.this.mPlayer.getDuration()) + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED);
                    MyNoteActivity.this.updateProgressBar();

                }
            }
        }

        public class EditTextChangeListener implements TextWatcher {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public EditTextChangeListener() {
            }

            public void afterTextChanged(Editable editable) {
                MyNoteActivity.this.hasModified = true;
                Log.i("hasModified", "true in textwatch");
            }
        }

        public class PlayAudioListeners implements OnClickListener {
            public PlayAudioListeners() {
            }

            public void onClick(View view) {
                ((InputMethodManager) MyNoteActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MyNoteActivity.this.et_NoteContent.getWindowToken(), 0);
                if (!MyNoteActivity.this.audioRecorder.hasRecording) {
                    Toast.makeText(MyNoteActivity.this, "No recording to play", Toast.LENGTH_SHORT).show();
                    Utilities.hideKeyboard(MyNoteActivity.this.ll_main, MyNoteActivity.this);
                } else if (MyNoteActivity.this.isPlaying || MyNoteActivity.this.isPlayerVisible) {
                    MyNoteActivity.this.ll_notesRecordingPlayer.setVisibility(View.GONE);
                    MyNoteActivity.this.isPlayerVisible = false;
                    MyNoteActivity.this.isPlaying = false;
                    MyNoteActivity.this.mPlayer.stop();
                    MyNoteActivity.this.mPlayer.release();
                    MyNoteActivity.this.mHandler.removeCallbacks(MyNoteActivity.this.mUpdateTimeTask);
                } else {
                    try {
                        MyNoteActivity.this.isPlaying = true;
                        MyNoteActivity.this.iv_play.setBackgroundResource(R.drawable.pause);
                        MyNoteActivity.this.ll_notesRecordingPlayer.setVisibility(View.VISIBLE);
                        MyNoteActivity.this.isPlayerVisible = true;
                        MyNoteActivity.this.mPlayer = new MediaPlayer();
                        MyNoteActivity.this.mPlayer.setDataSource(MyNoteActivity.this.recordingFilePath);
                        MyNoteActivity.this.mPlayer.prepare();
                        MyNoteActivity.this.mPlayer.start();
                        MediaPlayer mediaPlayer = MyNoteActivity.this.mPlayer;

                        mediaPlayer.setOnCompletionListener(new RecordingCompleteListener());
                        MyNoteActivity.this.seekbar.setProgress(0);
                        MyNoteActivity.this.seekbar.setMax(100);
                        MyNoteActivity.this.updateProgressBar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public class RecordAudioListeners implements OnClickListener {
            public RecordAudioListeners() {
            }

            public void onClick(View view) {
                ((InputMethodManager) MyNoteActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MyNoteActivity.this.et_NoteContent.getWindowToken(), 0);
                if (!MyNoteActivity.this.audioRecorder.hasRecording || MyNoteActivity.this.isRecording) {
                    MyNoteActivity.this.RecordOrStop();
                } else {
                    MyNoteActivity.this.showRecordingOverrideDialog();
                }
            }
        }

        public class RecordingCompleteListener implements OnCompletionListener {
            public RecordingCompleteListener() {
            }

            public void onCompletion(MediaPlayer mediaPlayer) {
                MyNoteActivity.this.isPlaying = false;
                MyNoteActivity.this.iv_play.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
            }
        }

        public class SeekBarListener implements OnSeekBarChangeListener {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            }

            public SeekBarListener() {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                MyNoteActivity.this.mHandler.removeCallbacks(MyNoteActivity.this.mUpdateTimeTask);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                MyNoteActivity.this.mHandler.removeCallbacks(MyNoteActivity.this.mUpdateTimeTask);
                MyNoteActivity.this.mPlayer.seekTo(MyNoteActivity.this.notesCommon.progressToTimer(seekBar.getProgress(), MyNoteActivity.this.mPlayer.getDuration()));
                MyNoteActivity.this.updateProgressBar();
            }
        }



    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onPermissionsGranted(int i, @NonNull List<String> list) {
    }

    private void showProgress() {
        this.myProgressDialog = new ProgressDialog(this);
        this.myProgressDialog.setTitle(getResources().getString(R.string.please_wait));
        this.myProgressDialog.setMessage(getResources().getString(R.string.processing));
        this.myProgressDialog.show();
    }


    public void hideProgress() {
        ProgressDialog progressDialog = this.myProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_my_note);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.et_Notetitle = (EditText) findViewById(R.id.et_Notetitle);
        this.et_NoteContent = (LinedEditText) findViewById(R.id.et_NoteContent);
        this.tv_recStartTime = (TextView) findViewById(R.id.tv_recStartTime);
        this.tv_recEndTime = (TextView) findViewById(R.id.tv_recEndTime);
        this.iv_NotesplayAudio = (ImageView) findViewById(R.id.iv_NotesplayAudio);
        this.iv_NotesRecordAudio = (ImageView) findViewById(R.id.iv_NotesRecordAudio);
        this.iv_previous = (ImageView) findViewById(R.id.iv_previous);
        this.iv_play = (ImageView) findViewById(R.id.iv_play);
        this.iv_next = (ImageView) findViewById(R.id.iv_next);
        this.seekbar = (SeekBar) findViewById(R.id.seekbar);
        this.ll_notesRecordingPlayer = (LinearLayout) findViewById(R.id.ll_notesRecordingPlayer);
        this.chronometer = (Chronometer) findViewById(R.id.chronometer);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        this.NotesHashMap = new HashMap<>();
        this.readNoteFromXML = new ReadNoteFromXML();
        this.notesCommon = new NotesCommon();
        this.audioRecorder = new AudioRecorder(this);
        this.mHandler = new Handler();
        this.ll_main = (ScrollView) findViewById(R.id.ll_main);
        this.notesFilesDAL = new NotesFilesDAL(this);
        this.constants = new Constants();
        setSupportActionBar(this.toolbar);
//        this.toolbar.setBackgroundColor(getResources().getColor(R.color.ColorAppTheme));
        this.toolbar.setNavigationIcon((int) R.drawable.ic_top_back_icon);
        SecurityLocksCommon.IsAppDeactive = true;
        this.iv_NotesplayAudio.setVisibility(View.GONE);
        ImageView imageView = this.iv_NotesplayAudio;

        imageView.setOnClickListener(new PlayAudioListeners());
        ImageView imageView2 = this.iv_NotesRecordAudio;

        imageView2.setOnClickListener(new RecordAudioListeners());
        ImageView imageView3 = this.iv_play;

        imageView3.setOnClickListener(new AudioPlayerListener());
        ImageView imageView4 = this.iv_next;

        imageView4.setOnClickListener(new AudioPlayerListener());
        ImageView imageView5 = this.iv_previous;

        imageView5.setOnClickListener(new AudioPlayerListener());
        SeekBar seekBar = this.seekbar;

        seekBar.setOnSeekBarChangeListener(new SeekBarListener());
        if (NotesCommon.isEdittingNote) {
            showProgress();
            new Thread() {
                public void run() {
                    try {
                        MyNoteActivity.this.initViewNote();
                        Message message = new Message();
                        message.what = 1;
                        MyNoteActivity.this.handle.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            initAddNote();
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    LinedEditText linedEditText = MyNoteActivity.this.et_NoteContent;

                    linedEditText.addTextChangedListener(new EditTextChangeListener());
                    EditText editText = MyNoteActivity.this.et_Notetitle;

                    editText.addTextChangedListener(new EditTextChangeListener());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);
        requestPermission(this.PERMISSIONS);
    }

    public void initAddNote() {
        this.et_NoteContent = this.notesCommon.setEditTextFullPage(this.et_NoteContent);
        this.audioRecorder.hasRecording = false;
    }

    public void initViewNote() {
        ReadNoteFromXML readNoteFromXML2 = this.readNoteFromXML;
        StringBuilder sb = new StringBuilder();
        sb.append(StorageOptionsCommon.STORAGEPATH);
        sb.append(StorageOptionsCommon.NOTES);
        sb.append(NotesCommon.CurrentNotesFolder);
        sb.append(File.separator);
        sb.append(NotesCommon.CurrentNotesFile);
        sb.append(StorageOptionsCommon.NOTES_FILE_EXTENSION);
        this.NotesHashMap = readNoteFromXML2.ReadNote(sb.toString());
        this.audioString = (String) this.NotesHashMap.get("audioData");
        this.oldNoteTitle = (String) this.NotesHashMap.get("Title");
        if (this.audioString.length() > 0) {
            this.audioRecorder.createRecordingFolder();
            this.audioRecorder.createFirstRecording();
            String absolutePath = this.audioRecorder.firstRecordingFile.getAbsolutePath();
            this.notesCommon.getDecodedAudio(this.audioString, absolutePath);
            this.recordingFilePath = absolutePath;
            this.audioRecorder.hasRecording = true;
            this.iv_NotesplayAudio.setVisibility(View.VISIBLE);
        }
        try {
            this.ll_main.setBackgroundColor(Color.parseColor((String) this.NotesHashMap.get("NoteColor")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void RecordOrStop() {
        if (!this.isRecording) {
            this.isRecording = true;
            this.iv_NotesRecordAudio.setImageResource(R.drawable.recorder_active_icon);
            this.audioRecorder.StartRecording();
            this.chronometer.setBase(SystemClock.elapsedRealtime());
            this.chronometer.setVisibility(View.VISIBLE);
            this.chronometer.start();
            Toast.makeText(this, getResources().getString(R.string.recording_started), Toast.LENGTH_SHORT).show();
            return;
        }
        this.isRecording = false;
        this.hasModified = true;
        this.iv_NotesRecordAudio.setImageResource(R.drawable.ic_recorder_icon);
        this.recordingFilePath = this.audioRecorder.StopRecording();
        this.chronometer.stop();
        this.chronometer.setVisibility(View.INVISIBLE);
        this.iv_NotesplayAudio.clearAnimation();
        this.iv_NotesplayAudio.setVisibility(View.VISIBLE);
        Toast.makeText(this, getResources().getString(R.string.recording_stoped), Toast.LENGTH_SHORT).show();
        this.iv_NotesplayAudio.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_bounce));
    }

    public void DeleteExistingRecording() {
        File file = new File(this.recordingFilePath);
        if (file.exists() ? file.delete() : false) {
            this.audioRecorder.hasRecording = false;
            this.recordingFilePath = "";
            this.iv_NotesplayAudio.clearAnimation();
            this.iv_NotesplayAudio.setVisibility(View.GONE);
            RecordOrStop();
            return;
        }
        Toast.makeText(this, getResources().getString(R.string.recording_not_deleted), Toast.LENGTH_SHORT).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_color, menu);
        this.toolbar.setTitle((CharSequence) "");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId != 16908332) {
            if (itemId == R.id.action_menu_add) { /*2131296289*/
                this.NotesContent = this.et_NoteContent.getText().toString();
                this.notesTitle = this.et_Notetitle.getText().toString().trim();
                if (!this.notesTitle.trim().equals("") && !this.notesCommon.hasNoData(this.NotesContent)) {
                    final String encodedAudio = this.notesCommon.getEncodedAudio(this.recordingFilePath);
                    final String currentDate = this.notesCommon.getCurrentDate();
                    if (this.notesTitle.equals("")) {
                        String[] split = this.NotesContent.trim().replaceAll("[!?,]", "").split("\\s+");
                        if (split.length > 0) {
                            this.notesTitle = split[0];
                        }
                        if (split.length > 1) {
                            String str = this.notesTitle;
                            StringBuilder sb = new StringBuilder();
                            sb.append(" ");
                            sb.append(split[1]);
                            this.notesTitle = str.concat(sb.toString());
                        }
                    }
                    if (!this.notesTitle.equals("") && this.notesCommon.isNoSpecialCharsInName(this.notesTitle)) {
                        showProgress();
                        new Thread() {
                            public void run() {
                                if (NotesCommon.isEdittingNote) {
                                    NotesFilesDAL notesFilesDAL = MyNoteActivity.this.notesFilesDAL;
                                    StringBuilder sb = new StringBuilder();
                                    MyNoteActivity.this.constants.getClass();
                                    sb.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
                                    MyNoteActivity.this.constants.getClass();
                                    sb.append("NotesFileName");
                                    sb.append(" = '");
                                    sb.append(MyNoteActivity.this.oldNoteTitle);
                                    sb.append("' AND ");
                                    MyNoteActivity.this.constants.getClass();
                                    sb.append("NotesFileIsDecoy");
                                    sb.append(" = ");
                                    sb.append(SecurityLocksCommon.IsFakeAccount);
                                    NotesFileDB_Pojo notesFileInfoFromDatabase = notesFilesDAL.getNotesFileInfoFromDatabase(sb.toString());
                                    NotesFilesDAL notesFilesDAL2 = MyNoteActivity.this.notesFilesDAL;
                                    StringBuilder sb2 = new StringBuilder();
                                    MyNoteActivity.this.constants.getClass();
                                    sb2.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
                                    MyNoteActivity.this.constants.getClass();
                                    sb2.append("NotesFileName");
                                    sb2.append(" = '");
                                    sb2.append(MyNoteActivity.this.notesTitle);
                                    sb2.append("' AND ");
                                    MyNoteActivity.this.constants.getClass();
                                    sb2.append("NotesFileIsDecoy");
                                    sb2.append(" = ");
                                    sb2.append(SecurityLocksCommon.IsFakeAccount);
                                    NotesFileDB_Pojo notesFileInfoFromDatabase2 = notesFilesDAL2.getNotesFileInfoFromDatabase(sb2.toString());
                                    int notesFileId = notesFileInfoFromDatabase.getNotesFileId();
                                    int notesFileId2 = notesFileInfoFromDatabase2.getNotesFileId();
                                    if (MyNoteActivity.this.oldNoteTitle.equals(MyNoteActivity.this.notesTitle)) {
                                        NotesCommon notesCommon = MyNoteActivity.this.notesCommon;
                                        MyNoteActivity myNoteActivity = MyNoteActivity.this;
                                        notesCommon.createNote(myNoteActivity, myNoteActivity.noteColor, MyNoteActivity.this.NotesContent, MyNoteActivity.this.notesTitle, MyNoteActivity.this.oldNoteTitle, encodedAudio, (String) MyNoteActivity.this.NotesHashMap.get("note_datetime_c"), currentDate, true);
                                    } else if (notesFileId == notesFileId2 || notesFileInfoFromDatabase2.getNotesFileName() == null) {
                                        NotesCommon notesCommon2 = MyNoteActivity.this.notesCommon;
                                        MyNoteActivity myNoteActivity2 = MyNoteActivity.this;
                                        notesCommon2.createNote(myNoteActivity2, myNoteActivity2.noteColor, MyNoteActivity.this.NotesContent, MyNoteActivity.this.notesTitle, MyNoteActivity.this.oldNoteTitle, encodedAudio, (String) MyNoteActivity.this.NotesHashMap.get("note_datetime_c"), currentDate, true);
                                    } else {
                                        Message message = new Message();
                                        message.what = 3;
                                        MyNoteActivity.this.handle.sendMessage(message);
                                    }
                                } else {
                                    NotesFilesDAL notesFilesDAL3 = MyNoteActivity.this.notesFilesDAL;
                                    StringBuilder sb3 = new StringBuilder();
                                    MyNoteActivity.this.constants.getClass();
                                    sb3.append("SELECT \t     * \t\t\t\t\t\t   FROM  TableNotesFile WHERE ");
                                    MyNoteActivity.this.constants.getClass();
                                    sb3.append("NotesFileName");
                                    sb3.append(" = '");
                                    sb3.append(MyNoteActivity.this.notesTitle);
                                    sb3.append("' AND ");
                                    MyNoteActivity.this.constants.getClass();
                                    sb3.append("NotesFileIsDecoy");
                                    sb3.append(" = ");
                                    sb3.append(SecurityLocksCommon.IsFakeAccount);
                                    if (!notesFilesDAL3.IsFileAlreadyExist(sb3.toString())) {
                                        NotesCommon notesCommon3 = MyNoteActivity.this.notesCommon;
                                        MyNoteActivity myNoteActivity3 = MyNoteActivity.this;
                                        String str = myNoteActivity3.noteColor;
                                        String str2 = MyNoteActivity.this.NotesContent;
                                        String str3 = MyNoteActivity.this.notesTitle;
                                        String str4 = MyNoteActivity.this.oldNoteTitle;
                                        String str5 = encodedAudio;
                                        String str6 = currentDate;
                                        notesCommon3.createNote(myNoteActivity3, str, str2, str3, str4, str5, str6, str6, false);
                                    } else {
                                        Message message2 = new Message();
                                        message2.what = 3;
                                        MyNoteActivity.this.handle.sendMessage(message2);
                                    }
                                }
                                Message message3 = new Message();
                                message3.what = 2;
                                MyNoteActivity.this.handle.sendMessage(message3);
                            }
                        }.start();
                    } else {
                        Utilities.hideKeyboard(this.ll_main, this);
                        Toast.makeText(this, R.string.empty_note_name, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Utilities.hideKeyboard(this.ll_main, this);
                    Toast.makeText(this, R.string.empty_note, Toast.LENGTH_SHORT).show();
                }
            } else if (itemId == R.id.action_menu_color) { /*2131296290*/
                setNoteColor();
            }
        } else if (this.audioRecorder.hasRecording && this.isPlaying && this.isPlayerVisible) {
            this.ll_notesRecordingPlayer.setVisibility(View.GONE);
            this.isPlayerVisible = false;
            this.isPlaying = false;
            this.mPlayer.stop();
            this.mPlayer.release();
            this.mHandler.removeCallbacks(this.mUpdateTimeTask);
        } else if (this.isRecording || this.hasModified) {
            showDiscardDialog();
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, NotesFilesActivity.class));
            finish();
            overridePendingTransition(17432576, 17432577);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void setNoteColor() {
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.dialog_note_color_picker);
        final ColorPickerView colorPickerView = (ColorPickerView) dialog.findViewById(R.id.color_picker_view);
        colorPickerView.setAlpha(0.5f);
        colorPickerView.setDensity(12);
        TextView textView = (TextView) dialog.findViewById(R.id.tv_yes);
        ((TextView) dialog.findViewById(R.id.tv_no)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        textView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    int selectedColor = colorPickerView.getSelectedColor();
                    Log.i("color", String.valueOf(selectedColor));
                    StringBuilder sb = new StringBuilder();
                    sb.append("#33");
                    sb.append(Integer.toHexString(selectedColor).substring(2));
                    String sb2 = sb.toString();
                    Log.i("scolor", sb2);
                    MyNoteActivity.this.ll_main.setBackgroundColor(Color.parseColor(sb2));
                    MyNoteActivity.this.noteColor = sb2;
                    MyNoteActivity.this.hasModified = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showDiscardDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.confirmation_message_box);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.ll_Ok);
        LinearLayout linearLayout2 = (LinearLayout) dialog.findViewById(R.id.ll_Cancel);
        ((TextView) dialog.findViewById(R.id.tvmessagedialogtitle)).setText(R.string.discard_changes);
        linearLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SecurityLocksCommon.IsAppDeactive = false;
                MyNoteActivity.this.startActivity(new Intent(MyNoteActivity.this, NotesFilesActivity.class));
                MyNoteActivity.this.finish();
                dialog.cancel();
            }
        });
        linearLayout2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (!NotesCommon.isEdittingNote && !this.hasInsertedLines) {
            this.notesCommon.setEditTextFullPage(this.et_NoteContent);
            this.hasInsertedLines = true;
        }
    }

    public void PlayPause() {
        if (this.mPlayer.isPlaying()) {
            this.mPlayer.pause();
            this.iv_play.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
        } else if (!this.mPlayer.isPlaying()) {
            this.mPlayer.start();
            this.iv_play.setBackgroundResource(R.drawable.pause);
        } else {
            this.mPlayer.start();
        }
    }

    public void updateProgressBar() {
        this.mHandler.postDelayed(this.mUpdateTimeTask, 100);
    }

    public void showRecordingOverrideDialog() {
        Builder builder = new Builder(this);
        builder.setMessage((CharSequence) getResources().getString(R.string.overwrite_audio));
        builder.setCancelable(true);
        builder.setPositiveButton((CharSequence) getResources().getString(R.string.yes), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                MyNoteActivity.this.iv_NotesplayAudio.clearAnimation();
                MyNoteActivity.this.iv_NotesplayAudio.setVisibility(View.GONE);
                MyNoteActivity.this.DeleteExistingRecording();
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton((CharSequence) getResources().getString(R.string.no), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                MyNoteActivity.this.RecordOrStop();
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    public void onBackPressed() {
        if (this.audioRecorder.hasRecording && this.isPlaying && this.isPlayerVisible) {
            this.ll_notesRecordingPlayer.setVisibility(View.GONE);
            this.isPlayerVisible = false;
            this.isPlaying = false;
            this.mPlayer.stop();
            this.mPlayer.release();
            this.mHandler.removeCallbacks(this.mUpdateTimeTask);
        } else if (this.isRecording || this.hasModified) {
            showDiscardDialog();
        } else {
            SecurityLocksCommon.IsAppDeactive = false;
            startActivity(new Intent(this, NotesFilesActivity.class));
            finish();
            overridePendingTransition(17432576, 17432577);
        }
    }


    public void onPause() {
        super.onPause();
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
        }
    }


    public void onResume() {
        super.onResume();
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
    }


    public void onStop() {
        super.onStop();
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && sensorEvent.values[0] == 0.0f && PanicSwitchCommon.IsPalmOnFaceOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    public void onShake(float f) {
        if (PanicSwitchCommon.IsFlickOn || PanicSwitchCommon.IsShakeOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    @AfterPermissionGranted(123)
    private void requestPermission(String[] strArr) {
        SecurityLocksCommon.IsAppDeactive = false;
        if (!EasyPermissions.hasPermissions(this, strArr)) {
            EasyPermissions.requestPermissions(new PermissionRequest.Builder((Activity) this, 123, strArr).setRationale("For the best Calculator Vault, please Allow Permission").setPositiveButtonText("ok").setNegativeButtonText("").build());
        }
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr.length > 0 && iArr[0] == 0) {
            Toast.makeText(getApplicationContext(), "Permission is granted ", Toast.LENGTH_SHORT).show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.RECORD_AUDIO")) {
            String[] strArr2 = {"android.permission.RECORD_AUDIO"};
            if (EasyPermissions.hasPermissions(this, strArr2)) {
                Toast.makeText(this, "Permission  again...", Toast.LENGTH_SHORT).show();
            } else {
                EasyPermissions.requestPermissions(new PermissionRequest.Builder((Activity) this, 123, strArr2).setRationale("For the best Calculator Vault experience, please Allow Permission").setPositiveButtonText("ok").setNegativeButtonText("").build());
            }
            Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
        }
    }

    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        if (EasyPermissions.somePermissionPermanentlyDenied((Activity) this, list)) {
            new AppSettingsDialog.Builder((Activity) this).build().show();
        }
    }


    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }
}

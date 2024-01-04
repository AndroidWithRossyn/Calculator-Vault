package com.example.vault.tryattempt;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.vault.tryattempt.model.TryAttemptEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class TryAttemptsSharedPreferences {
    private static String _fileName = "TryAttempts";
    private static String _tryAttemptObject = "TryAttemptObject";
    static Context context;
    private static TryAttemptsSharedPreferences tryAttemptsSharedPreferences;
    static SharedPreferences myPrefs;

    private TryAttemptsSharedPreferences() {
    }

    public static TryAttemptsSharedPreferences GetObject(Context context2) {
        if (tryAttemptsSharedPreferences == null) {
            tryAttemptsSharedPreferences = new TryAttemptsSharedPreferences();
        }
        context = context2;
        myPrefs = context.getSharedPreferences(_fileName, 0);
        return tryAttemptsSharedPreferences;
    }

    public void SetTryAttemptObject(ArrayList<TryAttemptEntity> arrayList) {
        Editor edit = myPrefs.edit();
        edit.putString("TryAttemptObject", new Gson().toJson((Object) arrayList));
        edit.commit();
    }

    public ArrayList<TryAttemptEntity> GetTryAttemptObject() {
        new Gson();
        return (ArrayList) new Gson().fromJson(myPrefs.getString("TryAttemptObject", "").toString(), new TypeToken<ArrayList<TryAttemptEntity>>() {
        }.getType());
    }
}

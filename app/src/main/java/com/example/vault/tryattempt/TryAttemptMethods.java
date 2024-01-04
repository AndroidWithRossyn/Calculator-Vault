package com.example.vault.tryattempt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.vault.tryattempt.model.TryAttemptEntity;
import com.example.vault.securitylocks.SecurityLocksSharedPreferences;

public class TryAttemptMethods {
    static ArrayList<TryAttemptEntity> tryAttemptEntities;

    public void AddTryAttempToSharedPreference(Context context, String str, String str2) {
        SecurityLocksSharedPreferences GetObject = SecurityLocksSharedPreferences.GetObject(context);
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy HH:edit_share_btn");
        Date date = new Date(currentTimeMillis);
        System.out.println(simpleDateFormat.format(date));
        TryAttemptEntity tryAttemptEntity = new TryAttemptEntity();
        tryAttemptEntity.SetLoginOption(GetObject.GetLoginType());
        tryAttemptEntity.SetWrongPassword(str);
        tryAttemptEntity.SetImagePath(str2);
        tryAttemptEntity.SetTryAttemptTime(date.toString());
        tryAttemptEntity.SetIsCheck(Boolean.valueOf(false));
        tryAttemptEntities = new ArrayList<>();
        TryAttemptsSharedPreferences GetObject2 = TryAttemptsSharedPreferences.GetObject(context);
        tryAttemptEntities = GetObject2.GetTryAttemptObject();
        ArrayList<TryAttemptEntity> arrayList = tryAttemptEntities;
        if (arrayList == null) {
            tryAttemptEntities = new ArrayList<>();
            tryAttemptEntities.add(tryAttemptEntity);
        } else {
            arrayList.add(tryAttemptEntity);
        }
        GetObject2.SetTryAttemptObject(tryAttemptEntities);
    }

    public static Bitmap DecodeFile(File file) {
        try {
            Options options = new Options();
            int i = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            while ((options.outWidth / i) / 2 >= 70 && (options.outHeight / i) / 2 >= 70) {
                i *= 2;
            }
            Options options2 = new Options();
            options2.inSampleSize = i;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, options2);
        } catch (FileNotFoundException unused) {
            return null;
        }
    }

    public static byte[] GetBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}

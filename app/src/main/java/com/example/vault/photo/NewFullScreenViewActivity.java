package com.example.vault.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.vault.R;
import com.example.vault.features.MainiFeaturesActivity;
import com.example.vault.gallery.GalleryActivity;
import com.example.vault.panicswitch.AccelerometerListener;
import com.example.vault.panicswitch.AccelerometerManager;
import com.example.vault.panicswitch.PanicSwitchActivityMethods;
import com.example.vault.panicswitch.PanicSwitchCommon;
import com.example.vault.photo.model.ImageData;
import com.example.vault.photo.model.Photo;
import com.example.vault.photo.util.PhotoDAL;
import com.example.vault.photo.util.TouchImageView;
import com.example.vault.securitylocks.SecurityLocksCommon;
import com.example.vault.utilities.Common;
import com.banrossyn.imageloader.core.DisplayImageOptions;
import com.banrossyn.imageloader.core.DisplayImageOptions.Builder;
import com.banrossyn.imageloader.core.ImageLoader;
import com.banrossyn.imageloader.core.assist.FailReason;
import com.banrossyn.imageloader.core.assist.ImageScaleType;
import com.banrossyn.imageloader.core.display.FadeInBitmapDisplayer;
import com.banrossyn.imageloader.core.listener.ImageLoadingListener;
import com.banrossyn.imageloader.core.listener.SimpleImageLoadingListener;
import com.banrossyn.imageloader.utils.LibCommonAppClass;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class NewFullScreenViewActivity extends Activity implements AccelerometerListener, SensorEventListener {
    public static final int INDEX = 2;
    int Backindex = 0;
    int _SortType = 0;
    int albumId;
    final Context context = this;
    List<ImageData> imList = new ArrayList();
    int index = 2;
    private ArrayList<String> mPhotosList = new ArrayList<>();
    int m_imagePosition;
    DisplayImageOptions options;
    List<Photo> photo;
    private SensorManager sensorManager;
    private ViewPager viewPager;

    private class ImageAdapter extends PagerAdapter {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private LayoutInflater inflater;

        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }

        public Parcelable saveState() {
            return null;
        }

//        static {
//            Class<NewFullScreenViewActivity> cls = NewFullScreenViewActivity.class;
//        }

        ImageAdapter() {
            this.inflater = LayoutInflater.from(NewFullScreenViewActivity.this.getApplicationContext());
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        public int getCount() {
            return NewFullScreenViewActivity.this.imList.size();
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View inflate = this.inflater.inflate(R.layout.item_pager_image, viewGroup, false);
            TouchImageView touchImageView = (TouchImageView) inflate.findViewById(R.id.image);
            final ProgressBar progressBar = (ProgressBar) inflate.findViewById(R.id.loading);
            touchImageView.setMaxZoom(6.0f);
            ImageLoader instance = ImageLoader.getInstance();
            StringBuilder sb = new StringBuilder();
            sb.append("file:///");
            sb.append(((ImageData) NewFullScreenViewActivity.this.imList.get(i)).getImgPath().toString());
            instance.displayImage(sb.toString(), (ImageView) touchImageView, NewFullScreenViewActivity.this.options, (ImageLoadingListener) new SimpleImageLoadingListener() {
                public void onLoadingStarted(String str, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                public void onLoadingFailed(String str, View view, FailReason failReason) {
                    switch (failReason.getType()) {
                    }
                    progressBar.setVisibility(View.GONE);
                }

                public void onLoadingComplete(String str, View view, Bitmap bitmap) {
                    progressBar.setVisibility(View.GONE);
                }
            });
            viewGroup.addView(inflate, 0);
            return inflate;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view.equals(obj);
        }
    }

    public void onAccelerationChanged(float f, float f2, float f3) {
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_fullscreen_view);
        Log.d("TAG", "NewFullScreenViewActivity");
        LibCommonAppClass.IsPhoneGalleryLoad = true;
        SecurityLocksCommon.IsAppDeactive = true;
        getWindow().addFlags(128);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.options = new Builder().showImageForEmptyUri((int) R.drawable.ic_photo_empty_icon).showImageOnFail((int) R.drawable.ic_photo_empty_icon).resetViewBeforeLoading(true).cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Config.RGB_565).considerExifParams(true).displayer(new FadeInBitmapDisplayer(HttpStatus.SC_MULTIPLE_CHOICES)).build();
        int i = 0;
        this.m_imagePosition = getIntent().getIntExtra("IMAGE_POSITION", 0);
        this._SortType = getIntent().getIntExtra("_SortBy", 0);
        this.albumId = getIntent().getIntExtra("ALBUM_ID", 0);
        this.mPhotosList = getIntent().getStringArrayListExtra("mPhotosList");
        if (Common.IsCameFromAppGallery || this.mPhotosList == null) {
            PhotoDAL photoDAL = new PhotoDAL(this);
            photoDAL.OpenRead();
            this.photo = photoDAL.GetPhotoByAlbumId(this.albumId, this._SortType);
            photoDAL.close();
            while (i < this.photo.size()) {
                ImageData imageData = new ImageData();
                imageData.setImgPath(((Photo) this.photo.get(i)).getFolderLockPhotoLocation());
                this.imList.add(imageData);
                i++;
            }
        } else {
            while (i < this.mPhotosList.size()) {
                ImageData imageData2 = new ImageData();
                imageData2.setImgPath((String) this.mPhotosList.get(i));
                this.imList.add(imageData2);
                i++;
            }
        }
        this.viewPager = (ViewPager) findViewById(R.id.pager);
        this.viewPager.setAdapter(new ImageAdapter());
        this.viewPager.setCurrentItem(this.m_imagePosition);
    }

    public void onShake(float f) {
        if (PanicSwitchCommon.IsFlickOn || PanicSwitchCommon.IsShakeOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && sensorEvent.values[0] == 0.0f && PanicSwitchCommon.IsPalmOnFaceOn) {
            PanicSwitchActivityMethods.SwitchApp(this);
        }
    }


    public void onPause() {
        this.sensorManager.unregisterListener(this);
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }
        if (SecurityLocksCommon.IsAppDeactive) {
            finish();
            System.exit(0);
        }
        super.onPause();
    }


    public void onResume() {
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
        SensorManager sensorManager2 = this.sensorManager;
        sensorManager2.registerListener(this, sensorManager2.getDefaultSensor(8), 3);
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            if (Common.IsCameFromAppGallery) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromAppGallery = false;
                startActivity(new Intent(this, Photos_Gallery_Actitvity.class));
                finish();
            } else if (Common.IsCameFromGalleryFeature) {
                SecurityLocksCommon.IsAppDeactive = false;
                Common.IsCameFromGalleryFeature = false;
                startActivity(new Intent(this, GalleryActivity.class));
                finish();
            } else {
                SecurityLocksCommon.IsAppDeactive = false;
                startActivity(new Intent(this, MainiFeaturesActivity.class));
                finish();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }
}
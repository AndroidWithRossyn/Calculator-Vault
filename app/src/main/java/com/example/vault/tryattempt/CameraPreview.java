package com.example.vault.tryattempt;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CameraPreview extends SurfaceView implements Callback {
    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder = getHolder();

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(3);
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            this.mCamera.setPreviewDisplay(surfaceHolder);


            Camera.Parameters param = mCamera.getParameters();
            Camera.Size bestSize = null;
            List<Camera.Size> sizeList = mCamera.getParameters().getSupportedPreviewSizes();
            bestSize = sizeList.get(0);
            for(int i = 1; i < sizeList.size(); i++){
                if((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)){
                    bestSize = sizeList.get(i);
                }
            }

            List<Integer> supportedPreviewFormats = param.getSupportedPreviewFormats();
            Iterator<Integer> supportedPreviewFormatsIterator = supportedPreviewFormats.iterator();
            while(supportedPreviewFormatsIterator.hasNext()){
                Integer previewFormat =supportedPreviewFormatsIterator.next();
                if (previewFormat == ImageFormat.YV12) {
                    param.setPreviewFormat(previewFormat);
                }
            }

            param.setPreviewSize(bestSize.width, bestSize.height);

            param.setPictureSize(bestSize.width, bestSize.height);

            mCamera.setParameters(param);


            this.mCamera.startPreview();
        } catch (IOException unused) {
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        try {
            if (this.mCamera != null) {
                getHolder().removeCallback(this);
                this.mCamera.stopPreview();
                this.mCamera.release();
            }
        } catch (Exception e) {
            Log.v("The Exception is:", e.toString());
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        try {
            this.mCamera.setPreviewDisplay(surfaceHolder);
            this.mCamera.startPreview();
        } catch (Exception unused) {
        }
    }
}

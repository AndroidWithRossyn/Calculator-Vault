package com.banrossyn.imageloader.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IoUtils {
    public static final int CONTINUE_LOADING_PERCENTAGE = 75;
    public static final int DEFAULT_BUFFER_SIZE = 32768;
    public static final int DEFAULT_IMAGE_TOTAL_SIZE = 512000;
    public static int EncryptBytesSize = 121;

    public interface CopyListener {
        boolean onBytesCopied(int i, int i2);
    }

    private IoUtils() {
    }

    public static boolean copyStream(InputStream inputStream, OutputStream outputStream, CopyListener copyListener) throws IOException {
        return copyStream(inputStream, outputStream, copyListener, 32768);
    }

    public static boolean copyStream(InputStream inputStream, OutputStream outputStream, CopyListener copyListener, int i) throws IOException {
        int available = inputStream.available();
        if (available <= 0) {
            available = DEFAULT_IMAGE_TOTAL_SIZE;
        }
        byte[] bArr = new byte[i];
        if (shouldStopLoading(copyListener, 0, available)) {
            return false;
        }
        boolean z = true;
        int i2 = 0;
        do {
            int read = inputStream.read(bArr, 0, i);
            if (read != -1) {
                if (!z || !LibCommonAppClass.IsPhoneGalleryLoad) {
                    outputStream.write(bArr, 0, read);
                } else {
                    outputStream.write(NSDecriptionViewImage(bArr), 0, read);
                    z = false;
                }
                i2 += read;
            } else {
                outputStream.flush();
                return true;
            }
        } while (!shouldStopLoading(copyListener, i2, available));
        return false;
    }

    public static byte[] NSDecriptionViewImage(byte[] bArr) throws IOException {
        byte[] bArr2 = new byte[bArr.length];
        byte[] bArr3 = (byte[]) bArr.clone();
        int i = 0;
        for (int i2 = EncryptBytesSize - 1; i2 >= 0; i2--) {
            bArr3[i] = bArr[i2];
            i++;
        }
        return bArr3;
    }

    private static boolean shouldStopLoading(CopyListener copyListener, int i, int i2) {
        return copyListener != null && !copyListener.onBytesCopied(i, i2) && (i * 100) / i2 < 75;
    }

    public static void readAndCloseStream(InputStream inputStream) throws IOException {
        do {
            try {
            } catch (Throwable th) {
                closeSilently(inputStream);
                throw th;
            }
        } while (inputStream.read(new byte[32768], 0, 32768) != -1);
        closeSilently(inputStream);
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception unused) {
            }
        }
    }
}

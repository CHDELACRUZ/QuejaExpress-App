package com.mundowebsolutions.quejaexpress;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.squareup.picasso.Transformation;

import java.io.IOException;

public class ImageOrientationTransform implements Transformation {
    private String imagePath;

    public ImageOrientationTransform(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    break;
            }

            Bitmap rotatedBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
            source.recycle(); // Liberar el bitmap original
            return rotatedBitmap;

        } catch (IOException e) {
            e.printStackTrace();
            return source;
        }
    }

    @Override
    public String key() {
        return "orientationTransform";
    }
}

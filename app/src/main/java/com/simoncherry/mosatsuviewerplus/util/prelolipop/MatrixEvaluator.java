package com.simoncherry.mosatsuviewerplus.util.prelolipop;

import android.animation.TypeEvaluator;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.widget.ImageView;

/**
 * This class is passed to ObjectAnimator in order to animate changes in ImageView image matrix
 */
public class MatrixEvaluator implements TypeEvaluator<Matrix> {

    private static final String TAG = MatrixEvaluator.class.getSimpleName();

    public static TypeEvaluator<Matrix> NULL_MATRIX_EVALUATOR = new TypeEvaluator<Matrix>() {
        @Override
        public Matrix evaluate(float fraction, Matrix startValue, Matrix endValue) {
            return null;
        }
    };

    /**
     * This property is passed to ObjectAnimator when we are animating image matrix of ImageView
     */
    public static final Property<ImageView, Matrix> ANIMATED_TRANSFORM_PROPERTY = new Property<ImageView, Matrix>(Matrix.class,
            "animatedTransform") {

        /**
         * This is copy-paste form ImageView#animateTransform - method is invisible in sdk
         */
        @Override
        public void set(ImageView imageView, Matrix matrix) {
            Drawable drawable = imageView.getDrawable();
            if (drawable == null) {
                return;
            }
            if (matrix == null) {
                drawable.setBounds(0, 0, imageView.getWidth(), imageView.getHeight());
            } else {

                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                Matrix drawMatrix = imageView.getImageMatrix();
                if (drawMatrix == null) {
                    drawMatrix = new Matrix();
                    imageView.setImageMatrix(drawMatrix);
                }
                imageView.setImageMatrix(matrix);
            }
            imageView.invalidate();
        }

        @Override
        public Matrix get(ImageView object) {
            return null;
        }
    };

    float[] mTempStartValues = new float[9];

    float[] mTempEndValues = new float[9];

    Matrix mTempMatrix = new Matrix();

    @Override
    public Matrix evaluate(float fraction, Matrix startValue, Matrix endValue) {
        startValue.getValues(mTempStartValues);
        endValue.getValues(mTempEndValues);
        for (int i = 0; i < 9; i++) {
            float diff = mTempEndValues[i] - mTempStartValues[i];
            mTempEndValues[i] = mTempStartValues[i] + (fraction * diff);
        }
        mTempMatrix.setValues(mTempEndValues);

        return mTempMatrix;
    }
}

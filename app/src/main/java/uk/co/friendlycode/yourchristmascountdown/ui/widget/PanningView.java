package uk.co.friendlycode.yourchristmascountdown.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import timber.log.Timber;
import uk.co.friendlycode.yourchristmascountdown.R;

public class PanningView extends ImageView {

    private PanningViewHelper mHelper;

    private int mPanningDurationInMs;

    public PanningView(Context context) {
        this(context, null);
    }

    public PanningView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public PanningView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        readStyleParameters(context, attr);
        super.setScaleType(ScaleType.MATRIX);
    }

    private void readStyleParameters(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.PanningView);
        try {
            mPanningDurationInMs = a.getInt(
                    R.styleable.PanningView_panningDurationInMs,
                    PanningViewHelper.DEFAULT_PANNING_DURATION_IN_MS);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Timber.w("onAttachedToWindow");
        if (!isInEditMode()) {
            mHelper = new PanningViewHelper(this, mPanningDurationInMs);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        Timber.w("onDetachedFromWindow");
        mHelper.cleanup();
        super.onDetachedFromWindow();
    }

    @Override
    // setImageBitmap calls through to this method
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        stopUpdateStartIfNecessary();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        stopUpdateStartIfNecessary();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        stopUpdateStartIfNecessary();
    }

    private void stopUpdateStartIfNecessary() {
        if (null != mHelper) {
            boolean wasPanning = mHelper.isPanning();
            mHelper.stopPanning();
            mHelper.update();
            if (wasPanning) {
                mHelper.startPanning();
            }
        }
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (!isInEditMode()) {
            throw new UnsupportedOperationException("only matrix scaleType is supported");
        } else {
            super.setScaleType(scaleType);
        }
    }

    public void startPanning() {
        mHelper.startPanning();
    }

    public void stopPanning() {
        mHelper.stopPanning();
    }
}
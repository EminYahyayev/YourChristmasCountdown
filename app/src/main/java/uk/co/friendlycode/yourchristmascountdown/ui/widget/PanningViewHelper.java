package uk.co.friendlycode.yourchristmascountdown.ui.widget;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import timber.log.Timber;

public class PanningViewHelper implements ViewTreeObserver.OnGlobalLayoutListener {

    public static final int DEFAULT_PANNING_DURATION_IN_MS = 5000;

    private enum Way {R2L, L2R, T2B, B2T}

    private WeakReference<ImageView> mImageView;

    private int mIvTop, mIvRight, mIvBottom, mIvLeft;

    private ViewTreeObserver mViewTreeObserver;

    private Matrix mMatrix;

    private RectF mDisplayRect = new RectF();

    private ValueAnimator mCurrentAnimator;

    private LinearInterpolator mLinearInterpolator;

    private boolean mIsPortrait;

    private long mDuration;

    private long mCurrentPlayTime;

    private long mTotalTime;

    private Way mWay;

    private boolean mIsPanning;

    public PanningViewHelper(@NonNull ImageView imageView, long duration) {
        if (!hasDrawable(imageView)) {
            throw new IllegalArgumentException("drawable must not be null");
        }

        mLinearInterpolator = new LinearInterpolator();
        mDuration = duration;
        mImageView = new WeakReference<>(imageView);

        mViewTreeObserver = imageView.getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(this);

        setImageViewScaleTypeMatrix(imageView);

        mMatrix = imageView.getImageMatrix();
        if (mMatrix == null) {
            mMatrix = new Matrix();
        }

        mIsPortrait = imageView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        update();
    }

    public void update() {
        mWay = null;
        mTotalTime = 0;
        mCurrentPlayTime = 0;
        getImageView().post(new Runnable() {
            @Override
            public void run() {
                scale();
                refreshDisplayRect();
            }
        });
    }

    public boolean isPanning() {
        return mIsPanning;
    }

    /**
     * scale and start to pan the image background
     */
    public void startPanning() {
        if (mIsPanning) {
            return;
        }
        mIsPanning = true;
        final Runnable panningRunnable = new Runnable() {
            @Override
            public void run() {
                animateInternal();
            }
        };
        getImageView().post(panningRunnable);
    }

    /**
     * stop current panning
     */
    public void stopPanning() {
        if (!mIsPanning) {
            return;
        }
        mIsPanning = false;
        Timber.d("panning animation stopped by user");
        if (mCurrentAnimator != null) {
            mCurrentAnimator.removeAllListeners();
            mCurrentAnimator.cancel();
            mCurrentAnimator = null;
        }
        mTotalTime += mCurrentPlayTime;
        Timber.d("mTotalTime : " + mTotalTime);
    }

    /**
     * Clean-up the resources attached to this object. This needs to be called
     * when the ImageView is no longer used. A good example is from
     * {@link android.view.View#onDetachedFromWindow()} or from {@link android.app.Activity#onDestroy()}.
     * This is automatically called if you are using {@link PanningView}.
     */
    public final void cleanup() {
        if (null != mImageView) {
            getImageView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
        mViewTreeObserver = null;

        stopPanning();

        // Finally, clear ImageView
        mImageView = null;
    }

    public final ImageView getImageView() {
        ImageView imageView = null;

        if (null != mImageView) {
            imageView = mImageView.get();
        }

        // If we don't have an ImageView, call cleanup()
        if (null == imageView) {
            //cleanup();
            throw new IllegalStateException("ImageView no longer exists. You should not use this PanningViewHelper any more.");
        }

        return imageView;
    }

    private int getDrawableIntrinsicHeight() {
        return getImageView().getDrawable().getIntrinsicHeight();
    }

    private int getDrawableIntrinsicWidth() {
        return getImageView().getDrawable().getIntrinsicWidth();
    }

    private int getImageViewWidth() {
        return getImageView().getWidth();
    }

    private int getImageViewHeight() {
        return getImageView().getHeight();
    }

    /**
     * Set's the ImageView's ScaleType to Matrix.
     */
    private static void setImageViewScaleTypeMatrix(ImageView imageView) {
        if (null != imageView && !(imageView instanceof PanningView)) {
            imageView.setScaleType(ImageView.ScaleType.MATRIX);
        }
    }

    /**
     * @return true if the ImageView exists, and it's Drawable exists
     */
    private static boolean hasDrawable(ImageView imageView) {
        return null != imageView && null != imageView.getDrawable();
    }

    @Override
    public void onGlobalLayout() {
        ImageView imageView = getImageView();

        if (null != imageView) {
            final int top = imageView.getTop();
            final int right = imageView.getRight();
            final int bottom = imageView.getBottom();
            final int left = imageView.getLeft();

            /**
             * We need to check whether the ImageView's bounds have changed.
             * This would be easier if we targeted API 11+ as we could just use
             * View.OnLayoutChangeListener. Instead we have to replicate the
             * work, keeping track of the ImageView's bounds and then checking
             * if the values change.
             */
            if (top != mIvTop || bottom != mIvBottom || left != mIvLeft || right != mIvRight) {
                update();

                // Update values as something has changed
                mIvTop = top;
                mIvRight = right;
                mIvBottom = bottom;
                mIvLeft = left;
            }
        }
    }

    private void animateInternal() {
        refreshDisplayRect();
        if (mWay == null) {
            mWay = mIsPortrait ? Way.R2L : Way.B2T;
        }

        Timber.d("mWay : " + mWay);
        Timber.d("mDisplayRect : " + mDisplayRect);

        long remainingDuration = mDuration - mTotalTime;
        if (mIsPortrait) {
            if (mWay == Way.R2L) {
                animate(mDisplayRect.left, mDisplayRect.left - (mDisplayRect.right - getImageViewWidth()), remainingDuration);
            } else {
                animate(mDisplayRect.left, 0.0f, remainingDuration);
            }
        } else {
            if (mWay == Way.B2T) {
                animate(mDisplayRect.top, mDisplayRect.top - (mDisplayRect.bottom - getImageViewHeight()), remainingDuration);
            } else {
                animate(mDisplayRect.top, 0.0f, remainingDuration);
            }
        }
    }

    private void changeWay() {
        if (mWay == Way.R2L) {
            mWay = Way.L2R;
        } else if (mWay == Way.L2R) {
            mWay = Way.R2L;
        } else if (mWay == Way.T2B) {
            mWay = Way.B2T;
        } else if (mWay == Way.B2T) {
            mWay = Way.T2B;
        }
        mCurrentPlayTime = 0;
        mTotalTime = 0;
    }

    private void reset() {
        mMatrix.reset();
        applyScaleOnMatrix();
        mMatrix.postTranslate(0, 0);
        refreshDisplayRect();

        mCurrentPlayTime = 0;
        mTotalTime = 0;
        setCurrentImageMatrix();
    }

    private void animate(float start, float end, long duration) {
        Timber.d("startPanning : " + start + " to " + end + ", in " + duration + "ms");

        mCurrentAnimator = ValueAnimator.ofFloat(start, end);
        mCurrentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                mMatrix.reset();
                applyScaleOnMatrix();
                if (mIsPortrait) {
                    mMatrix.postTranslate(value, 0);
                } else {
                    mMatrix.postTranslate(0, value);
                }
                refreshDisplayRect();
                mCurrentPlayTime = animation.getCurrentPlayTime();
                setCurrentImageMatrix();
            }
        });
        mCurrentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                //Timber.d("animation has finished, startPanning in the other way");
                Timber.d("animation has finished, starting animation again from the beginning");
                changeWay();
                animateInternal();
            }

            @Override public void onAnimationCancel(Animator animation) {
                Timber.d("panning animation canceled");
            }
        });
        mCurrentAnimator.setDuration(duration);
        mCurrentAnimator.setInterpolator(mLinearInterpolator);
        mCurrentAnimator.start();
    }

    private void setCurrentImageMatrix() {
        getImageView().setImageMatrix(mMatrix);
        getImageView().invalidate();
        getImageView().requestLayout();
    }

    private void refreshDisplayRect() {
        mDisplayRect.set(0, 0, getDrawableIntrinsicWidth(), getDrawableIntrinsicHeight());
        mMatrix.mapRect(mDisplayRect);
    }

    private void scale() {
        mMatrix.reset();
        applyScaleOnMatrix();
        setCurrentImageMatrix();
    }

    private void applyScaleOnMatrix() {
        int drawableSize = mIsPortrait ? getDrawableIntrinsicHeight() : getDrawableIntrinsicWidth();
        int imageViewSize = mIsPortrait ? getImageViewHeight() : getImageViewWidth();
        float scaleFactor = (float) imageViewSize / (float) drawableSize;

        mMatrix.postScale(scaleFactor, scaleFactor);
    }
}
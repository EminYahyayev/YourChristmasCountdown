package uk.co.friendlycode.yourchristmascountdown.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.ExecutorService;

import uk.co.friendlycode.yourchristmascountdown.R;


public final class ChristmasSurfaceView extends SurfaceView
        implements SurfaceHolder.Callback, Runnable {

    private final float mSpeed = 1.0f;

    private Bitmap mBackground;
    private boolean mRunning;
    private ExecutorService mService;

    public ChristmasSurfaceView(Context context) {
        super(context);
        init();
    }

    public ChristmasSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChristmasSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        mBackground = BitmapFactory.decodeResource(getResources(), R.drawable.countdown_background);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(mBackground, 0, 0, new Paint());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas(null);
            synchronized (holder) {
                draw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void run() {
        mRunning = true;
        while (mRunning) {

        }
    }
}

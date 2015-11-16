package uk.co.friendlycode.yourchristmascountdown.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import uk.co.friendlycode.yourchristmascountdown.R;

public final class ChristmasTextView extends TextView {
    public ChristmasTextView(Context context) {
        super(context);
        init(null);
    }

    public ChristmasTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ChristmasTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ChristmasTextView);
            String fontName = a.getString(R.styleable.ChristmasTextView_fontName);
            if (fontName != null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
                setTypeface(myTypeface);
            }
            a.recycle();
        }
    }
}

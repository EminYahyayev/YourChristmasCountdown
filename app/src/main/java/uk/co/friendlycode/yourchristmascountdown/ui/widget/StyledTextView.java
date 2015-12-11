package uk.co.friendlycode.yourchristmascountdown.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import uk.co.friendlycode.yourchristmascountdown.R;

public class StyledTextView extends TextView {

    public StyledTextView(final Context context) {
        super(context);
        init(null);
    }

    public StyledTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public StyledTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(final AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StyledTextView);
            String fontName = a.getString(R.styleable.StyledTextView_fontName);
            if (fontName != null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), fontName);
                setTypeface(myTypeface);
            }
            a.recycle();
        }
    }
}

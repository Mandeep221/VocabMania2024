package com.msarangal.vocabmania;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by Mandeep on 6/8/2015.
 */
public class MyAutoCompleteEditText extends AutoCompleteTextView {
    public MyAutoCompleteEditText(Context context) {
        super(context);
    }

    public MyAutoCompleteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyAutoCompleteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.MyAutoCompleteEditText);
            String fontName = a.getString(R.styleable.MyAutoCompleteEditText_FontAuto);
            if (fontName != null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext()
                        .getAssets(), "fonts/" + fontName);
                setTypeface(myTypeface);
            }
            a.recycle();
        }
    }

}

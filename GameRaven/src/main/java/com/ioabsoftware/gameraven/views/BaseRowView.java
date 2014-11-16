package com.ioabsoftware.gameraven.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

import com.ioabsoftware.gameraven.util.Theming;

import dreamers.graphics.RippleDrawable;

public abstract class BaseRowView extends LinearLayout {

    protected RowType myType = null;

    protected int myColor = 0;
    protected float myScale = 0;

    protected final int PX = TypedValue.COMPLEX_UNIT_PX;

    protected Drawable background;

    public BaseRowView(Context context) {
        super(context);
        preInit(context);
    }

    public BaseRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        preInit(context);
    }

    public BaseRowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        preInit(context);
    }

    private void preInit(Context c) {
        setOrientation(VERTICAL);

        RippleDrawable.makeFor(this, Theming.rippleStateList(), true);

        init(c);

        preRetheme();
    }

    private void preRetheme() {
        myColor = Theming.accentColor();
        myScale = Theming.textScale();
        retheme();
    }

    public void beginShowingView(BaseRowData data) {
        if (Theming.accentColor() != myColor || Theming.textScale() != myScale) {
            SelectorItemDrawable.rebuildColorFilter();
            preRetheme();
        }
        showView(data);
    }

    protected abstract void init(Context context);

    protected abstract void retheme();

    protected abstract void showView(BaseRowData data);

}

package com.hellw.camerapickercolor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * 圆形取色点 用于相机取色 显示屏幕中心点颜色
 * @author hellc
 * @describ TODO
 * @email wuqingyun828@163.com
 * @date 2019/2/12 14:52
 */
public class RoundPickerColorView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Drawable bgDrawable;
    private int color;
    private int width, height;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RoundPickerColorView(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RoundPickerColorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RoundPickerColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RoundPickerColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        bgDrawable = getBackground();
        color = getResources().getColor(R.color.defalut_pick_color);
        width = getWidth() == 0 ? 144 : getWidth();
        height = getHeight() == 0 ? 144 : getHeight();
        mPaint.setColor(color);
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        bgDrawable.setBounds(0, 0, 144, 144);
        bgDrawable.draw(canvas);
        int radius = (Math.min(width, height) / 2 - 18 * 2);
        canvas.drawCircle(width / 2, width / 2, radius, mPaint);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }
}

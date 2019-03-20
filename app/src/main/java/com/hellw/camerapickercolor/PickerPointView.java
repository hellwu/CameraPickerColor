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
import android.view.MotionEvent;
import android.view.View;

/**
 * 移動點取色view
 * @author hellc
 * @describ TODO
 *
 *
 *
 *
 *
 *
 * @email wuqingyun828@163.com
 * @date 2019/2/1 16:59
 */
public class PickerPointView extends View implements View.OnTouchListener {
    private Drawable bgDrawable;
    private int color;
    private int width, height;
    private int lastX, lastY;
    private PickdrPointMoveListener listener;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PickerPointView(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PickerPointView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PickerPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PickerPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.pickerpoint);
        bgDrawable = array.getDrawable(R.styleable.pickerpoint_background);
        color = array.getColor(R.styleable.pickerpoint_src, 0);
        width = getWidth() == 0 ? 144 : getWidth();
        height = getHeight() == 0 ? 144 : getHeight();
        mPaint.setColor(color);

        setOnTouchListener(this);
    }

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        bgDrawable.setBounds(0, 0, 144, 144);
        bgDrawable.draw(canvas);
        int radius = (Math.min(width, height) / 2 - 18 * 2);
        canvas.drawCircle(width / 2, width / 2, radius, mPaint);
    }

    public void setColor(boolean checked, int color) {
        mPaint.setColor(color);
        invalidate();
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //检测到触摸事件后 第一时间得到相对于父控件的触摸点坐标 并赋值给x,y
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            //触摸事件中绕不开的第一步，必然执行，将按下时的触摸点坐标赋值给 lastX 和 last Y
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            //触摸事件的第二步，这时候的x,y已经随着滑动操作产生了变化，用变化后的坐标减去首次触摸时的坐标得到 相对的偏移量
            case MotionEvent.ACTION_MOVE:
                int offsetX = x - lastX;
                int offsetY = y - lastY;
                if(listener != null) {
                    listener.move(getLeft() + offsetX + 72, getTop() + offsetY + 144);
                }
                //使用 layout 进行重新定位
                layout(getLeft() + offsetX, getTop() + offsetY, getRight() + offsetX, getBottom() + offsetY);

                break;
        }
        return true;

    }

    public void setPickdrPointMoveListener(PickdrPointMoveListener listener) {
        this.listener = listener;
    }

    public interface PickdrPointMoveListener {
        /**
         * PickdrPointView滑動時調用
         */
        void move(int x, int y);
    }
}

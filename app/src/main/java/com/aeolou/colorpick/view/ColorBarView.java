package com.aeolou.colorpick.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.aeolou.colorpick.LogUtils;
import com.aeolou.colorpick.R;


/**
 * Author: Aeolou
 * Date:2020/3/14 0014
 * Email:tg0804013x@gmail.com
 */

public class ColorBarView extends View {
    private static int[] colors;
    private OnColorChangeListener onColorChangeListener;

    private float width;
    private float height;

    //长条宽高
    private float barWidth;
    private float barHeight;

    //滑块
    private int thumbDrawable;
    private Bitmap thumbBitmap;
    //滑块宽高
    private float thumbWidth;
    private float thumbHeight;
    //滑块当前的位置
    private float currentThumbOffset;
    //彩色长条开始位置
    private float barStartX, barStartY;

    private static int STATUS;
    private static final int STATUS_INIT = 0;
    //移动了action bar
    private static final int STATUS_SEEK = 1;

    //长条画笔
    private Paint barPaint;
    //滑块画笔
    private Paint thumbPaint;

    private int currentColor;


    public interface OnColorChangeListener {
        void onColorChange(int color);
    }


    public ColorBarView(Context context) {
        this(context, null);
    }

    public ColorBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initCustomAttrs(context, attrs);
        initView();

    }

    private void initView() {
        thumbBitmap = BitmapFactory.decodeResource(getResources(), thumbDrawable);
    }


    private void init(Context context) {
        //初始化渐变色
        initColors();
        STATUS = STATUS_INIT;
        barPaint = new Paint();
        barPaint.setAntiAlias(true);
        barPaint.setStrokeCap(Paint.Cap.ROUND);
        thumbPaint = new Paint();
        thumbPaint.setAntiAlias(true);
        thumbPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void initColors() {
        int colorCount = 12;
        int colorAngleStep = 360 / colorCount;
        colors = new int[colorCount + 1];
        float[] hsv = new float[]{0f, 1f, 1f};
        for (int i = 0; i < colors.length; i++) {
            hsv[0] = 360 - (i * colorAngleStep) % 360;
            if (hsv[0] == 360) hsv[0] = 359;
            colors[i] = Color.HSVToColor(hsv);
        }
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorBarView);
        if (typedArray != null) {
            thumbDrawable = typedArray.getResourceId(R.styleable.ColorBarView_thumbDrawable, R.mipmap.color_icon_button);
            barHeight = typedArray.getDimension(R.styleable.ColorBarView_barHeight, 30);
            thumbHeight = typedArray.getDimension(R.styleable.ColorBarView_thumbHeight, 80);
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    /**
     * 测量高
     *
     * @param heightMeasureSpec
     * @return
     */
    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            //精确值模式
            result = Math.max(Math.max((int) thumbHeight, (int) barHeight), specSize);
        } else if (specMode == MeasureSpec.AT_MOST) {
            //最大值模式
            result = Math.max((int) thumbHeight, (int) barHeight + getPaddingTop() + getPaddingBottom());
        } else {
            result = specSize;
        }

        return result;
    }

    /**
     * 测量高
     *
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            //精确值模式
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            //最大值模式
            result = 200;
        }
        return result;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        thumbWidth = thumbHeight * ((float) thumbBitmap.getWidth() / (float) thumbBitmap.getHeight());
        barWidth = width - thumbWidth;
        barStartX = thumbWidth / 2;//不从0开始，左右边缘用于显示滑块
        barStartY = height / 2 - barHeight / 2;
        super.onSizeChanged(w, h, oldw, oldh);
    }


    /**
     * 处理点击和滑动事件
     *
     * @param event
     * @return
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {//点击时
            case MotionEvent.ACTION_DOWN:
                currentThumbOffset = (int) event.getX();
                if (currentThumbOffset <= thumbWidth / 2) currentThumbOffset = thumbWidth / 2 + 1;
                if (currentThumbOffset >= barWidth + thumbWidth / 2)
                    currentThumbOffset = barWidth + thumbWidth / 2;
                STATUS = STATUS_SEEK;
                break;
            //滑动时
            case MotionEvent.ACTION_MOVE:
                currentThumbOffset = (int) event.getX();
                if (currentThumbOffset <= thumbWidth / 2) currentThumbOffset = thumbWidth / 2 + 1;
                if (currentThumbOffset >= barWidth + thumbWidth / 2)
                    currentThumbOffset = barWidth + thumbWidth / 2;
                break;
        }
        changColor();
        if (onColorChangeListener != null) {
            onColorChangeListener.onColorChange(currentColor);
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBar(canvas);
        drawThumb(canvas);
        super.onDraw(canvas);
    }

    /**
     * 滑动滑块使颜色发生变化
     */
    private void changColor() {
        float position = currentThumbOffset - thumbWidth / 2.0f;//当前滑块中心的长度
        float colorH = 360 - position / barWidth * 360;
        currentColor = Color.HSVToColor(new float[]{colorH, 1.0f, 1.0f});
    }


    /**
     * 获取当前颜色
     *
     * @return
     */
    private int getCurrentColor() {
        return currentColor;
    }

    /**
     * 设置当前颜色
     *
     * @param currentColor
     */
    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
        if (onColorChangeListener != null) {
            onColorChangeListener.onColorChange(currentColor);
        }
        invalidate();
    }

    /**
     * 绘制底部颜色条
     *
     * @param canvas
     */
    private void drawBar(Canvas canvas) {
        barPaint.setShader(
                new LinearGradient(barStartX, barStartY + barHeight / 2,
                        barStartX + barWidth, barStartY + barHeight / 2,
                        colors, null, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(new RectF(barStartX, barStartY, barStartX + barWidth,
                barStartY + barHeight), barHeight / 2, barHeight / 2, barPaint);
    }


    /**
     * 绘制滑块
     * @param canvas
     */
    private void drawThumb(Canvas canvas) {
        float[] currentColorHSV = new float[3];
        Color.RGBToHSV(Color.red(currentColor), Color.green(currentColor), Color.blue(currentColor), currentColorHSV);
        //根据HSV计算颜色所在位置
        float position = barWidth * currentColorHSV[0] / 360.0f;
        currentThumbOffset = barWidth - position + thumbWidth / 2;
        canvas.drawBitmap(thumbBitmap, null, getThumbRect(), thumbPaint);
    }

    /**
     * 获取滑块所在的矩形区域
     */
    private RectF getThumbRect() {
        return new RectF(currentThumbOffset - thumbWidth / 2, height / 2 - thumbHeight / 2,
                currentThumbOffset + thumbWidth / 2, height / 2 + thumbHeight / 2);
    }

    public void setOnColorChangerListener(OnColorChangeListener onColorChangerListener) {
        this.onColorChangeListener = onColorChangerListener;
    }

}

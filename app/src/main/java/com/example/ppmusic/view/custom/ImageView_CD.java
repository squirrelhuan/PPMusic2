package com.example.ppmusic.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.example.ppmusic.R;

/**
 * Created by squirrel桓 on 2018/1/16.
 */

public class ImageView_CD extends AppCompatImageView {

    public ImageView_CD(Context context) {
        super(context);
    }

    public ImageView_CD(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageView_CD(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }
    private int width, height;
    private String text = "测 试 北 京 欢 迎 你";

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawColor(Color.GREEN);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);//设置为空心
        paint.setStrokeWidth(1);

        canvas.drawCircle(width/2, width/2, width/2, paint);
        Path path = new Path();
        int w1 = width/6;
        path.addCircle(width/2, width/2, w1, Path.Direction.CCW);
        Path path2 = new Path();
        int w2 = width*9/20;
        path2.addCircle(width/2, width/2, w2, Path.Direction.CCW);
        //绘图样式设置为描边
        //paint.setStyle(Paint.Style.STROKE);

        //canvas.drawPath(path, paint);
        //paint.setTextScaleX(1);
        Paint paint1 = new Paint();
        paint1.setColor(getResources().getColor(R.color.transparent_white_33));
        paint1.setTextSize(36);
        int v = 2;
        canvas.drawTextOnPath(text, path, w1/v, 0, paint1);
        Paint paint2 = new Paint();
        paint2.setColor(Color.WHITE);
        paint2.setColor(getResources().getColor(R.color.transparent_white_33));
        paint2.setTextSize(36);
        canvas.drawTextOnPath(text, path2, w2/v, 0, paint2);
      /*  Paint paint = new Paint();
        Path path = new Path();
        RectF rectF = new RectF();
        //粗略的计算文字弧度
        path.addArc();
        paint.setStrokeWidth(1);
        //canvas.drawPath(path2, paint);
        // 沿着路径绘制一段文本
        canvas.drawTextOnPath(text, path, -10, 20, paint);*/
    }
}

package com.huan.WaveView.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.huan.WaveView.R;

/**
 * -----------------------------------------------------------------------------------Author Info---
 * Company Name:          xjyy.
 * Author:                Liu Huan.
 * Email:                 771383629@qq.com.
 * Date:                  2016/11/13 19:39.
 * -----------------------------------------------------------------------------------Message-------
 * If the following code to run properly, it is coding by Liu Huan.
 * otherwise I don't know.
 * -----------------------------------------------------------------------------------Class Info----
 * ClassName:             WaveCircleView.
 * -----------------------------------------------------------------------------------Describe------
 * Function: 代码开源转载请注明出处谢谢
 * -----------------------------------------------------------------------------------Modify--------
 * 2016/11/15 23:06     Modified By liuhuan.
 * -----------------------------------------------------------------------------------End-----------
 */
public class WaveCircleView extends View {
    /**
     * 默认宽高
     */
    private static final int DEFAULT_SIZE = 800;

    private float mCurrentProgress = 90f;//当前值
    private int mTotal = 100;//总份数

    private int mCViewHeight;//布局的高
    private int mCViewWidth;//布局的宽


    //圆的画笔
    private Paint mCirclePaint;//圆的画笔
    private int mCircleColor = Color.parseColor("#00BFFF");//圆环颜色
    private int mCircleRadius = 300;//圆的半径
    private int mCircleStrokeWidth = 8;//圆的笔宽度

    //圆环的画笔
    private Paint mCircleRingPaint;//圆环的画笔
    private int mCircleRingColor = Color.parseColor("#F0F8FF");//外环颜色
    private int mCircleRingStrokeWidth = 20;//圆环的笔宽度
    private int mCircleRingRadius = mCircleRadius + mCircleStrokeWidth / 2 + mCircleRingStrokeWidth / 2;//圆环的半径

    private Paint mArcPaint;//扇形画笔
    private int mArcColor = Color.parseColor("#FFFFFF");

    //水波画笔
    private Paint mWavePaint;
    private int mWaveColor = Color.GREEN;// 波纹颜色
    // y = Asin(wx+b)+h
    private static final float STRETCH_FACTOR_A = 20;
    private static final int OFFSET_Y = 0;
    // 第一条水波移动速度
    private int mXSpeed1 = 8;
    // 第二条水波移动速度
    private int mXSpeed2 = 5;
    private float mCycleFactorW;

    private int mTotalWidth, mTotalHeight;
    private float[] mYPositions;
    private float[] mResetOneYPositions;
    private float[] mResetTwoYPositions;
    private int mXOffsetSpeedOne;
    private int mXOffsetSpeedTwo;
    private int mXOneOffset;
    private int mXTwoOffset;


    private float ovalLeft;//扇形左边距
    private float ovalTop;//扇形上边距
    private float ovalRight;//扇形右边距
    private float ovalBottom;//扇形低边距
    private RectF oval; //扇形

    private Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.icon_all_clear_sel_2);//用户传的图片
    private Paint imagePaint;//图片画笔


    private DrawFilter mDrawFilter;
    private float imageLeft;
    private float imageTop;
    private Bitmap bitmap;
    private float souceImageWidth;//原图的宽
    private float souceImageHeight;//原图的高
    private float heightRatio;//高度比
    private float widthRatio;//宽度比
    private int mCircleRingAlpha = 50;
    private Canvas cvs;
    private Canvas cvsmask;
    private Bitmap mask;
    private Bitmap bm;
    private Paint mEllipsePaint;
    private RectF mEllipseRectF;
    private RectF mEllipseRectF2;
    private RectF mEllipseRectF3;

    //
    public WaveCircleView(Context context) {
        this(context, null);
    }

    public WaveCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    private void init(Context context) {
        //初始化一些东西
        //圆画笔
        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.STROKE);//模式
        mCirclePaint.setAntiAlias(true);//抗锯齿
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);//笔宽
        mCirclePaint.setColor(mCircleColor);

        //椭圆画笔
        mEllipsePaint = new Paint();
        mEllipsePaint.setAntiAlias(true);
        mEllipsePaint.setStrokeWidth(4);//笔宽
        mEllipsePaint.setColor(mCircleColor);
        mEllipsePaint.setStyle(Paint.Style.STROKE);//模式


        //圆环画笔
        mCircleRingPaint = new Paint();
        mCircleRingPaint.setStyle(Paint.Style.STROKE);//模式
        mCircleRingPaint.setAntiAlias(true);//抗锯齿
        mCircleRingPaint.setStrokeWidth(mCircleRingStrokeWidth);//笔宽
        mCircleRingPaint.setColor(mCircleRingColor);
        mCircleRingPaint.setAlpha(mCircleRingAlpha);

        //扇形画笔
        mArcPaint = new Paint();
        mArcPaint.setStyle(Paint.Style.FILL);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(mArcColor);
        mArcPaint.setStrokeWidth(1);//
        // mArcPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));

        //图片画笔
        imagePaint = new Paint();
        imagePaint.setAntiAlias(true);
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        // 波纹画笔
        mWavePaint = new Paint();
        mWavePaint.setStrokeWidth(1.0F);
        mWavePaint.setAntiAlias(true);// 去除画笔锯齿
        mWavePaint.setStyle(Paint.Style.STROKE); // 设置风格为实线
        mWavePaint.setColor(mWaveColor); // 设置画笔颜色

        // 将dp转化为px，用于控制不同分辨率上移动速度基本一致
        mXOffsetSpeedOne = dipToPx(context, mXSpeed1);
        mXOffsetSpeedTwo = dipToPx(context, mXSpeed2);


        //得到源图的宽高

        souceImageWidth = image.getWidth();
        souceImageHeight = image.getHeight();
        //得到目标缩放比例

        heightRatio = 2 * mCircleRadius / souceImageHeight;
        widthRatio = 2 * mCircleRadius / souceImageWidth;
        //新的比例图
        bitmap = scaleBitmap(image, widthRatio, heightRatio);
        image.recycle();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mCViewWidth = measureDimension(DEFAULT_SIZE, widthMeasureSpec);
        mCViewHeight = measureDimension(DEFAULT_SIZE, heightMeasureSpec);
        setMeasuredDimension(mCViewWidth, mCViewHeight);
    }

    //测量
    public int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        Log.e("cv_", "" + specSize);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;   //UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private float percent;//百分比
    private float processValue;
    private float rightAngleSide;//直角边
    private double startAngle;//开始角度
    private double endAngle;//结束角度


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 从canvas层面去除绘制时锯齿
        //canvas.setDrawFilter(mDrawFilter);

        //左
//上
//右
//下

        canvas.drawOval(mEllipseRectF, mEllipsePaint);
        canvas.drawOval(mEllipseRectF2, mEllipsePaint);
        canvas.drawOval(mEllipseRectF3, mEllipsePaint);
        //------------------------------------------------------------------------------------------


        float percent = mCurrentProgress / mTotal;
        float percent2 = percent * 2;//百分比
        //用户设置的图片
        //他和扇形的左边距是一样的
        imageLeft = (float) (mCViewWidth / 2.0 - mCircleRadius);
        //和扇形的上边距是一样的
        imageTop = (float) (mCViewHeight / 2.0 - mCircleRadius);


        canvas.drawBitmap(bitmap, imageLeft, imageTop, mWavePaint);
        //扇形左边距
        ovalLeft = (float) (mCViewWidth / 2.0 - mCircleRadius);
        //扇形上边距
        ovalTop = (float) (mCViewHeight / 2.0 - mCircleRadius);
        //扇形右边距
        ovalRight = mCViewWidth - ovalLeft;
        //扇形下边距
        ovalBottom = mCViewHeight - ovalTop;

        //画一个扇形
        oval = new RectF(ovalLeft, ovalTop, ovalRight, ovalBottom);


        processValue = mCircleRadius * percent2;//
        rightAngleSide = mCircleRadius - processValue;//直角边
        startAngle = Math.asin(rightAngleSide / mCircleRadius) * 180.0 / Math.PI; //开始角度
        endAngle = 2 * (90 - startAngle);//结束角度


        cvs.drawArc(oval, (float) startAngle, (float) endAngle, false, mArcPaint);//画一个扇形
        resetPositonY();
//        Bitmap bm2 = Bitmap.createBitmap(mCViewWidth, mCViewHeight, Bitmap.Config.ARGB_8888);
//
//        Canvas cvs2 = new Canvas(bm2);

//        mWavePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        for (int i = 0; i < mTotalWidth; i++) {
            int value = i + (int) (mCViewWidth / 2.0 - mCircleRadius);
            //动态的改变percent2从而形成波纹上升下降效果

            // 绘制第一条水波纹
            cvsmask.drawLine(value,
                    mTotalHeight + (int) (mCViewWidth / 2.0 - mCircleRadius) - mResetOneYPositions[i] + 10 - mCircleRadius * percent2 + 12,
                    value,
                    (float) (mTotalHeight + (int) (mCViewWidth / 2.0 - mCircleRadius)),
                    mWavePaint);
            // 绘制第二条水波纹
            cvsmask.drawLine(value, mTotalHeight + (int) (mCViewWidth / 2.0 - mCircleRadius) - mResetTwoYPositions[i] - mCircleRadius * percent2 + 25, value,
                    mTotalHeight + (int) (mCViewWidth / 2.0 - mCircleRadius),
                    mWavePaint);
        }
        Paint paint = new Paint();
        //扇形与水纹层重合处理模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));

        //将水纹图层画到扇形图层上
        cvs.drawBitmap(mask, 0, 0, paint);

        // 改变两条波纹的移动点
        mXOneOffset += mXOffsetSpeedOne;
        mXTwoOffset += mXOffsetSpeedTwo;

        // 如果已经移动到结尾处，则重头记录
        if (mXOneOffset >= mTotalWidth) {
            mXOneOffset = 0;
        }
        if (mXTwoOffset > mTotalWidth) {
            mXTwoOffset = 0;
        }


        canvas.drawBitmap(bm, 0, 0, imagePaint);
//        canvas.drawBitmap(bm2, imageLeft, imageTop,imagePaint);
        mWavePaint.setXfermode(null);
        //开始画图
        //------------------------------------------------------------------------------------------
        canvas.drawCircle(mCViewWidth / 2, mCViewHeight / 2,
                mCircleRadius, mCirclePaint);
        //------------------------------------------------------------------------------------------
        canvas.drawCircle(mCViewWidth / 2, mCViewHeight / 2,
                mCircleRingRadius, mCircleRingPaint);
        //------------------------------------------------------------------------------------------

//
        cvs.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        cvsmask.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        postInvalidate();
    }

    private void drawWave(Canvas cvs, float percent2) {


    }


    /**
     * 缩放图片
     */
    private Bitmap scaleBitmap(Bitmap origin, float widthRatio, float heightRatio) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(widthRatio, heightRatio);
        Bitmap newBitmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBitmap.equals(origin)) {
            return newBitmap;
        }
        origin.recycle();
        origin = null;
        return newBitmap;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 关闭硬件加速，防止异常unsupported operation exception

        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        Message ms1 = Message.obtain();
        ms1.what = ELLIPSE_RECTF_REFRESH_1;
        ms1.arg1 = (int) value1;
        mHandler.sendMessage(ms1);

        Message ms2 = Message.obtain();
        ms2.what = ELLIPSE_RECTF_REFRESH_2;
        ms2.arg1 = (int) value2;
        mHandler.sendMessageDelayed(ms2, 0);

        Message ms3 = Message.obtain();
        ms3.what = ELLIPSE_RECTF_REFRESH_3;
        ms3.arg1 = (int) value3;
        mHandler.sendMessageDelayed(ms3, 0);
    }

    //----------------------------------------------------------------------------------------------
    float mERWidthOffsetMax = 100.0f;//椭圆左偏移量(加圆半径等于椭圆长半径)
    float mERHeightOffsetMax = 60.0f;//椭圆上偏移量//等于短半径


    //----------------------------------------------------------------------------------------------
    private void resetPositonY() {
        // mXOneOffset代表当前第一条水波纹要移动的距离
        int yOneInterval = mYPositions.length - mXOneOffset;
        // 使用System.arraycopy方式重新填充第一条波纹的数据
        System.arraycopy(mYPositions, mXOneOffset, mResetOneYPositions, 0, yOneInterval);
        System.arraycopy(mYPositions, 0, mResetOneYPositions, yOneInterval, mXOneOffset);

        int yTwoInterval = mYPositions.length - mXTwoOffset;
        System.arraycopy(mYPositions, mXTwoOffset, mResetTwoYPositions, 0,
                yTwoInterval);
        System.arraycopy(mYPositions, 0, mResetTwoYPositions, yTwoInterval, mXTwoOffset);
    }

    private float mErOffset;
    private int mErLeft;
    private int mErTop;
    private int mErRight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 记录下view的宽高
        mTotalWidth = 2 * mCircleRadius;
        mTotalHeight = 2 * mCircleRadius;
        // 用于保存原始波纹的y值
        mYPositions = new float[mTotalWidth];
        // 用于保存波纹一的y值
        mResetOneYPositions = new float[mTotalWidth];
        // 用于保存波纹二的y值
        mResetTwoYPositions = new float[mTotalWidth];

        // 将周期定为view总宽度
        mCycleFactorW = (float) (2 * Math.PI / mTotalWidth);

        // 根据view总宽度得出所有对应的y值
        for (int i = 0; i < mTotalWidth; i++) {
            mYPositions[i] = (float) (STRETCH_FACTOR_A * Math.sin(mCycleFactorW * i) + OFFSET_Y);
        }


        //底层
        bm = Bitmap.createBitmap(mCViewWidth, mCViewHeight, Bitmap.Config.ARGB_8888);

        cvs = new Canvas(bm);
        //上层
        mask = Bitmap.createBitmap(mCViewWidth, mCViewHeight, Bitmap.Config.ARGB_8888);

        cvsmask = new Canvas(mask);
//宽度等于mERWidthOffsetMax+圆的半径
        //高度等于高度的偏移量mERHeightOffsetMax
        mErOffset = (mERWidthOffsetMax + mCircleRadius) / mERHeightOffsetMax;//椭圆的长短半径的比例
        mErLeft = mCViewWidth / 2 - mCircleRadius;//这减掉一个圆的半径是为了让椭圆的左侧超出圆的半径。或者可以直接减一个值(该值大于圆的半径)
        mErTop = mCViewHeight / 2 + mCircleRadius;
        mErRight = mCViewWidth / 2 + mCircleRadius;
        //必须满足value+mERHeightOffsetMax>=0
        mEllipseRectF = new RectF(
                mErLeft - mERWidthOffsetMax - mErOffset * value1,//左-
                mErTop - mERHeightOffsetMax - value1,//上-
                mErRight + mERWidthOffsetMax + mErOffset * value1,//右+
                mErTop + mERHeightOffsetMax + value1);//下+


        mEllipseRectF2 = new RectF(
                mErLeft - mERWidthOffsetMax - mErOffset * value2,//左-
                mErTop - mERHeightOffsetMax - value2,//上-
                mErRight + mERWidthOffsetMax + mErOffset * value2,//右+
                mErTop + mERHeightOffsetMax + value2);//下+

        mEllipseRectF3 = new RectF(
                mErLeft - mERWidthOffsetMax - mErOffset * value3,//左-
                mErTop - mERHeightOffsetMax - value3,//上-
                mErRight + mERWidthOffsetMax + mErOffset * value3,//右+
                mErTop + mERHeightOffsetMax + value3);//下+
    }


    public void setWaveBackgroundRes(int icon) {
        image = BitmapFactory.decodeResource(getResources(), icon);//用户传的图片
    }

    public void setWaveProgress(float progress) {
        setWaveProgressSmooth(progress, false);
    }

    private int mRisingSpeedValue = 2;//上升速度
    private int mDescentSpeedValue = 3;//下降速度
    private static final int WAVE_SMOOTH_UP = 11;
    private static final int WAVE_SMOOTH_DOWN = 12;
    private static final int WAVE_SMOOTH_DOWN_UP = 13;
    private static final int WAVE_SMOOTH_DOWN_UP_2 = 14;
    private static final int ELLIPSE_RECTF_REFRESH_1 = 15;
    private static final int ELLIPSE_RECTF_REFRESH_2 = 16;
    private static final int ELLIPSE_RECTF_REFRESH_3 = 17;
    float value1 = 80;
    float value2 = 30;
    float value3 = 10;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WAVE_SMOOTH_UP:
                    if (msg.arg1 > mCurrentProgress) {
                        mCurrentProgress = mCurrentProgress + mRisingSpeedValue > 100 ? 100 : mCurrentProgress + mRisingSpeedValue;
                        Message ms = Message.obtain();
                        ms.what = WAVE_SMOOTH_UP;
                        ms.arg1 = msg.arg1;
                        mHandler.sendMessage(ms);
                    }
                    break;
                case WAVE_SMOOTH_DOWN:
                    if (msg.arg1 < mCurrentProgress) {
                        mCurrentProgress = mCurrentProgress - mDescentSpeedValue < 0 ? 0 : mCurrentProgress - mDescentSpeedValue;
                        Message ms = Message.obtain();
                        ms.what = WAVE_SMOOTH_DOWN;
                        ms.arg1 = msg.arg1;
                        mHandler.sendMessage(ms);
                    }
                    break;
                case WAVE_SMOOTH_DOWN_UP:
                    if (mCurrentProgress > 0) {
                        mCurrentProgress = mCurrentProgress - mDescentSpeedValue < 0 ? 0 : mCurrentProgress - mDescentSpeedValue;
                        Message ms = Message.obtain();
                        ms.what = WAVE_SMOOTH_DOWN_UP;
                        ms.arg1 = msg.arg1;
                        mHandler.sendMessage(ms);
                    } else {
                        Message ms = Message.obtain();
                        ms.what = WAVE_SMOOTH_DOWN_UP_2;
                        ms.arg1 = msg.arg1;
                        mHandler.sendMessage(ms);
                    }

                    break;
                case WAVE_SMOOTH_DOWN_UP_2:
                    if (msg.arg1 > mCurrentProgress) {
                        mCurrentProgress = mCurrentProgress + mRisingSpeedValue > 100 ? 100 : mCurrentProgress + mRisingSpeedValue;
                        Message ms = Message.obtain();
                        ms.what = WAVE_SMOOTH_UP;
                        ms.arg1 = msg.arg1;
                        mHandler.sendMessage(ms);
                    }

                    break;
                case ELLIPSE_RECTF_REFRESH_1:

                    if (msg.arg1 + mERHeightOffsetMax >= 0) {
                        Message ms = Message.obtain();
                        ms.what = ELLIPSE_RECTF_REFRESH_1;
                        mEllipseRectF = new RectF(
                                mErLeft - mERWidthOffsetMax - mErOffset * msg.arg1,//左-
                                mErTop - mERHeightOffsetMax - msg.arg1,//上-
                                mErRight + mERWidthOffsetMax + mErOffset * msg.arg1,//右+
                                mErTop + mERHeightOffsetMax + msg.arg1);//下+
                        ms.arg1 = (msg.arg1 - 2);
                        mHandler.sendMessageDelayed(ms, 10);
                    } else {
                        Message ms = Message.obtain();
                        ms.what = ELLIPSE_RECTF_REFRESH_1;
                        ms.arg1 = (int) value1;
                        mHandler.sendMessageDelayed(ms, 10);
                    }


                    break;
                case ELLIPSE_RECTF_REFRESH_2:

                    if (msg.arg1 + mERHeightOffsetMax >= 0) {
                        Message ms = Message.obtain();
                        ms.what = ELLIPSE_RECTF_REFRESH_2;
                        mEllipseRectF2 = new RectF(
                                mErLeft - mERWidthOffsetMax - mErOffset * msg.arg1,//左-
                                mErTop - mERHeightOffsetMax - msg.arg1,//上-
                                mErRight + mERWidthOffsetMax + mErOffset * msg.arg1,//右+
                                mErTop + mERHeightOffsetMax + msg.arg1);//下+
                        ms.arg1 = (msg.arg1 - 2);
                        mHandler.sendMessageDelayed(ms, 10);
                    } else {
                        Message ms = Message.obtain();
                        ms.what = ELLIPSE_RECTF_REFRESH_2;
                        ms.arg1 = (int) value2;
                        mHandler.sendMessageDelayed(ms, 10);
                    }

                    break;

                case ELLIPSE_RECTF_REFRESH_3:
                    if (msg.arg1 + mERHeightOffsetMax >= 0) {
                        Message ms = Message.obtain();
                        ms.what = ELLIPSE_RECTF_REFRESH_3;
                        mEllipseRectF3 = new RectF(
                                mErLeft - mERWidthOffsetMax - mErOffset * msg.arg1,//左-
                                mErTop - mERHeightOffsetMax - msg.arg1,//上-
                                mErRight + mERWidthOffsetMax + mErOffset * msg.arg1,//右+
                                mErTop + mERHeightOffsetMax + msg.arg1);//下+
                        ms.arg1 = (msg.arg1 - 2);
                        mHandler.sendMessageDelayed(ms, 10);
                    } else {
                        Message ms = Message.obtain();
                        ms.what = ELLIPSE_RECTF_REFRESH_3;
                        ms.arg1 = (int) value3;
                        mHandler.sendMessageDelayed(ms, 10);
                    }

                    break;


            }


        }
    };

    /**
     * 设置水波纹上升下降移动平滑
     *
     * @param progress
     * @param smooth   true 平滑  false 不平滑
     */
    public void setWaveProgressSmooth(float progress, boolean smooth) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("WaveCircleView Progress Parameters must be between 0 - 100 ");
        }
        mHandler.removeMessages(WAVE_SMOOTH_UP);
        mHandler.removeMessages(WAVE_SMOOTH_DOWN);
        Message message = Message.obtain();
        if (smooth) {//平滑
            if (progress > mCurrentProgress) {
                message.what = WAVE_SMOOTH_UP;
                message.arg1 = (int) progress;
                mHandler.sendMessage(message);
            } else if (progress < mCurrentProgress) {
                message.what = WAVE_SMOOTH_DOWN;
                message.arg1 = (int) progress;
                mHandler.sendMessage(message);
            }
        } else {//直接设置值
            mCurrentProgress = progress;
        }
    }

    /**
     * @param progress
     * @param isDownUp
     */
    public void setWaveProgressDownUpSmooth(float progress, boolean isDownUp) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("WaveCircleView Progress Parameters must be between 0 - 100 ");
        }
        mHandler.removeMessages(WAVE_SMOOTH_UP);
        mHandler.removeMessages(WAVE_SMOOTH_DOWN);
        mHandler.removeMessages(WAVE_SMOOTH_DOWN_UP);
        Message message = Message.obtain();
        if (isDownUp) {//平滑
            message.what = WAVE_SMOOTH_DOWN_UP;
            message.arg1 = (int) progress;
            mHandler.sendMessage(message);

        } else {//直接设置值
            setWaveProgressSmooth(progress, true);
        }
    }

    private int dipToPx(Context context, int dip) {
        return (int) (dip * getScreenDensity(context) + 0.5f);
    }

    private float getScreenDensity(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                    .getMetrics(dm);
            return dm.density;
        } catch (Exception e) {
            return DisplayMetrics.DENSITY_DEFAULT;
        }
    }
}

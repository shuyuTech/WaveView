package com.huan.WaveView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;

import com.huan.WaveView.view.WaveCircleView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {

    @Bind(R.id.cv_am_custom)
    WaveCircleView cvAmCustom;
    @Bind(R.id.sb_am_progress)
    SeekBar sbAmProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        cvAmCustom.setWaveBackgroundRes(R.drawable.icon_all_clear_sel_2);
        cvAmCustom.setWaveProgressDownUpSmooth(90f, true);


/**
 private int mCircleColor = Color.parseColor("#00BFFF");//圆环颜色
 private int mCircleRadius = 300;//圆的半径
 private int mCircleStrokeWidth = 8;//圆的笔宽度

 //圆环的画笔
 private Paint mCircleRingPaint;//圆环的画笔
 private int mCircleRingColor = Color.parseColor("#F0F8FF");//外环颜色
 private int mCircleRingStrokeWidth = 20;//圆环的笔宽度
 private int mCircleRingRadius = mCircleRadius + mCircleStrokeWidth / 2 + mCircleRingStrokeWidth / 2;//圆环的半径
 mCircleRingAlpha = 50;

 // 波纹颜色
 private int mWaveColor = Color.GRAY;

 private int mRisingSpeedValue=1;//上升速度
 private int mDescentSpeedValue=1;//下降速度
 */


        sbAmProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cvAmCustom.setWaveProgressDownUpSmooth(progress, true);
                // cvAmCustom.setWaveProgressSmooth(progress,true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
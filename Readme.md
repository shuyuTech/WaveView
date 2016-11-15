for (int i = 0; i < mTotalWidth; i++) {
            int value = i + (int) (mCViewWidth / 2.0 - mCircleRadius);

            double w = mCircleRadius - (i*1.0 / mTotalWidth) * 2 * mCircleRadius;

            double w2 = Math.pow(w, 2);
            double radius2 = Math.pow(mCircleRadius, 2);
            double x=Math.sqrt(radius2-w2);

            // 绘制第一条水波纹
            canvas.drawLine(value,
                    mTotalHeight + (int) (mCViewWidth / 2.0 - mCircleRadius) - mResetOneYPositions[i]+10-mCircleRadius * percent2+12,
                    value,
                    (float) (mTotalHeight + (int) (mCViewWidth / 2.0 - mCircleRadius)-mCircleRadius+x),
                    mWavePaint);
            // 绘制第二条水波纹
            canvas.drawLine(value, mTotalHeight + (int) (mCViewWidth / 2.0 - mCircleRadius) - mResetTwoYPositions[i]-mCircleRadius * percent2+25, value,
                    mTotalHeight + (int) (mCViewWidth / 2.0 - mCircleRadius),
                    mWavePaint);

        }
package com.prasoon.expense.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView


class CustomBottomNavigationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {
    private var mPath: Path? = null
    private var mPaint: Paint? = null

    /** the CURVE_CIRCLE_RADIUS represent the radius of the fab button  */
    val CURVE_CIRCLE_RADIUS = 256 / 3

    // the coordinates of the first curve
    var mFirstCurveStartPoint = Point()
    var mFirstCurveEndPoint = Point()
    var mFirstCurveControlPoint2 = Point()
    var mFirstCurveControlPoint1 = Point()

    //the coordinates of the second curve
    var mSecondCurveStartPoint = Point()
    var mSecondCurveEndPoint = Point()
    var mSecondCurveControlPoint1 = Point()
    var mSecondCurveControlPoint2 = Point()
    var mNavigationBarWidth = 0
    var mNavigationBarHeight = 0

    init {
        init()
    }

    private fun init() {
        mPath = Path()
        mPaint = Paint()
        mPaint!!.style = Paint.Style.FILL_AND_STROKE
        mPaint!!.color = Color.WHITE
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPath?.reset();
        mPath?.moveTo(0f, 0f);
        mPath?.lineTo(mFirstCurveStartPoint.x.toFloat(), mFirstCurveStartPoint.y.toFloat());

        mPath?.cubicTo(
            mFirstCurveControlPoint1.x.toFloat(), mFirstCurveControlPoint1.y.toFloat(),
            mFirstCurveControlPoint2.x.toFloat(), mFirstCurveControlPoint2.y.toFloat(),
            mFirstCurveEndPoint.x.toFloat(), mFirstCurveEndPoint.y.toFloat()
        );

        mPath?.cubicTo(
            mSecondCurveControlPoint1.x.toFloat(), mSecondCurveControlPoint1.y.toFloat(),
            mSecondCurveControlPoint2.x.toFloat(), mSecondCurveControlPoint2.y.toFloat(),
            mSecondCurveEndPoint.x.toFloat(), mSecondCurveEndPoint.y.toFloat()
        );

        mPath?.lineTo(mNavigationBarWidth.toFloat(), 0f);
        mPath?.lineTo(mNavigationBarWidth.toFloat(), mNavigationBarHeight.toFloat());
        mPath?.lineTo(0f, mNavigationBarHeight.toFloat());
        mPath?.close();

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(mPath!!, mPaint!!)
    }
}
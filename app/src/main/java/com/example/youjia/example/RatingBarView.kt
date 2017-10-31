package com.example.youjia.example

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.R.attr.radius
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.Bitmap
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.R.attr.radius
import com.example.youjia.myapplication.R


/**
 * author:CQE
 * Date: 2017/10/30.
 */
class RatingBarView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var starPadding: Float
    private var starImageSize: Int
    private var starCount: Int
    private var starEmptyDrawable: Drawable?
    private var starFillBitMap: Bitmap?
    private var mPaint: Paint
    private var starMark: Float = 0f

    private var isIntegerMark: Boolean = false
        get() = field
        set(isInt) {
            field = isInt
        }
    private var onRatingBarChangeListener: OnRatingBarChangeListener? = null
    fun setOnRatingListener(onRating: OnRatingBarChangeListener) {
        onRatingBarChangeListener = onRating
    }

    init {
        val mTypedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.MyRatingBar)
        starPadding = mTypedArray.getDimension(R.styleable.MyRatingBar_starPadding, 0f)
        starImageSize = mTypedArray.getDimension(R.styleable.MyRatingBar_starImageSize, 20f).toInt()
        starCount = mTypedArray.getInteger(R.styleable.MyRatingBar_starCount, 5)
        starEmptyDrawable = mTypedArray.getDrawable(R.styleable.MyRatingBar_starEmpty)
        starFillBitMap = drawableToBitMap(mTypedArray.getDrawable(R.styleable.MyRatingBar_starFill))
        mTypedArray.recycle()

        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.shader = BitmapShader(starFillBitMap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    private fun drawableToBitMap(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        val bitmap = Bitmap.createBitmap(starImageSize, starImageSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, starImageSize, starImageSize)
        drawable.draw(canvas)
        return bitmap
    }


    /**
     * 设置评分分数
     */
    fun setMark(mark: Float) {
        if (isIntegerMark) {
            starMark = Math.ceil(mark.toDouble()).toFloat()
        } else {
            starMark = mark
        }

        if (onRatingBarChangeListener != null) {
            onRatingBarChangeListener?.onRatingBarChange(starMark)
        }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension((starImageSize * starCount + starPadding * (starCount - 1)).toInt(), starImageSize)
    }

    fun drawShape(bmp:Bitmap?,radius: Int): Bitmap{
        val squareBitmap: Bitmap?// 根据传入的位图截取合适的正方形位图
        val scaledBitmap: Bitmap?// 根据diameter对截取的正方形位图进行缩放
        val diameter = radius * 2
        // 传入位图的宽高
        val w = bmp?.getWidth()?:0
        val h = bmp?.getHeight()?:0
        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        var squarewidth = 0
        var squareheight = 0// 矩形的宽高
        var x = 0
        var y = 0
        if (h > w) {// 如果高>宽
            squareheight = w
            squarewidth = squareheight
            x = 0
            y = (h - w) / 2
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squarewidth,
                    squareheight)
        } else if (h < w) {// 如果宽>高
            squareheight = h
            squarewidth = squareheight
            x = (w - h) / 2
            y = 0
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squarewidth,
                    squareheight)
        } else {
            squareBitmap = bmp
        }
        // 对squareBitmap进行缩放为diameter边长的正方形位图
        if (squareBitmap?.getWidth() != diameter
                || squareBitmap.getHeight() != diameter) {
            scaledBitmap = Bitmap.createScaledBitmap(squareBitmap, diameter,
                    diameter, true);
        } else {
            scaledBitmap = squareBitmap;
        }
        val outputbmp = Bitmap.createBitmap(scaledBitmap!!.getWidth(),
                scaledBitmap.getHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputbmp)// 创建一个相同大小的画布
        val paint = Paint()// 定义画笔
        paint.isAntiAlias = true// 设置抗锯齿
        paint.isFilterBitmap = true
        paint.isDither = true
        canvas.drawARGB(0, 0, 0, 0)

        val path = Path()
        val radian = degree2Radian(36)// 36为五角星的角度
        val radius_in = (radius * Math.sin((radian / 2).toDouble()) / Math
                .cos(radian.toDouble())).toFloat() // 中间五边形的半径

        path.moveTo((radius * Math.cos((radian / 2).toDouble())).toFloat(), 0f)// 此点为多边形的起点
        path.lineTo((radius * Math.cos((radian / 2).toDouble()) + radius_in * Math.sin(radian.toDouble())).toFloat(),
                (radius - radius * Math.sin((radian / 2).toDouble())).toFloat())
        path.lineTo((radius.toDouble() * Math.cos((radian / 2).toDouble()) * 2.0).toFloat(),
                (radius - radius * Math.sin((radian / 2).toDouble())).toFloat())
        path.lineTo((radius * Math.cos((radian / 2).toDouble()) + radius_in * Math.cos((radian / 2).toDouble())).toFloat(),
                (radius + radius_in * Math.sin((radian / 2).toDouble())).toFloat())
        path.lineTo((radius * Math.cos((radian / 2).toDouble()) + radius * Math.sin(radian.toDouble())).toFloat(),
                (radius + radius * Math.cos(radian.toDouble())).toFloat())
        path.lineTo((radius * Math.cos((radian / 2).toDouble())).toFloat(),
                (radius + radius_in).toFloat())
        path.lineTo(
                (radius * Math.cos((radian / 2).toDouble()) - radius * Math.sin(radian.toDouble())).toFloat(),
                (radius + radius * Math.cos(radian.toDouble())).toFloat())
        path.lineTo((radius * Math.cos((radian / 2).toDouble()) - radius_in * Math.cos((radian / 2).toDouble())).toFloat(),
                (radius + radius_in * Math.sin((radian / 2).toDouble())).toFloat())
        path.lineTo(0f, (radius - radius * Math.sin((radian / 2).toDouble())).toFloat())
        path.lineTo((radius * Math.cos((radian / 2).toDouble()) - radius_in * Math.sin(radian.toDouble())).toFloat(),
                (radius - radius * Math.sin((radian / 2).toDouble())).toFloat())

        path.close()// 使这些点构成封闭的多边形
        canvas.drawPath(path, paint)

        // 设置Xfermode的Mode
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)

        return outputbmp
    }

    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
        if (starFillBitMap  == null || starEmptyDrawable == null){
            return
        }

        for (i in 0 until starCount){
            starEmptyDrawable?.setBounds(((starPadding + starImageSize) * i).toInt(), 0,
                    ((starPadding + starImageSize) * i + starImageSize).toInt(),starImageSize)
            starEmptyDrawable?.draw(canvas)
        }

        if (starMark > 1){
            canvas?.drawRect(0f,0f,starImageSize.toFloat(),starImageSize.toFloat(),mPaint)
            if (starMark - starMark == 0f){
                for (i in 0 until  starMark.toInt()){
                    canvas?.translate(starPadding + starImageSize, 0f)
                    canvas?.drawRect(0f,0f,starImageSize.toFloat(),starImageSize.toFloat(),mPaint)
                }
            }else {
                for (i in 0 until  starMark.toInt()-1){
                    canvas?.translate(starPadding + starImageSize, 0f)
                    canvas?.drawRect(0f,0f,starImageSize.toFloat(),starImageSize.toFloat(),mPaint)
                }
                canvas?.translate(starPadding + starImageSize, 0f)
                canvas?.drawRect(0f,0f,
                        (starImageSize * (starMark - (starMark * 10)*1.0f/10)),
                        starImageSize.toFloat(),mPaint)
            }
        }else {
            canvas?.drawRect(0f,0f,(starImageSize * starMark),starImageSize.toFloat(),mPaint)
        }
    }

    private fun degree2Radian(degree: Int): Float {
        return (Math.PI * degree / 180).toFloat()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            var x: Int? = event.x.toInt()
            if (x != null) {
                if (x < 0) {
                    x = 0
                }
                if (x > measuredWidth) {
                    x = measuredWidth
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        setMark((x * 1 / (measuredWidth * 1 / starCount)).toFloat())
                    }

                    MotionEvent.ACTION_MOVE -> {
                        setMark((x * 1 / (measuredWidth * 1 / starCount)).toFloat())
                    }

                    MotionEvent.ACTION_UP -> {
                        return@let
                    }
                }
            }
        }
        invalidate()
        return super.onTouchEvent(event)
    }

}

/**
 * 星星的点击事件
 */
interface OnRatingBarChangeListener {
    fun onRatingBarChange(ratingCount: Float)
}

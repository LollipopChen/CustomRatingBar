package com.example.youjia.example

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.youjia.myapplication.R
import java.math.BigDecimal

/**
 * 自定义评分条
 * author:CQE
 * Date: 2017/10/30.
 */
class CustomRatingBar(context: Context,attrs: AttributeSet): LinearLayout(context,attrs) {

    private var starImageSize: Float       // 星星的尺寸
    private var starPadding: Float         // 星星的间距
    private var starStep: Float            // 当前进度
    var stepSize: StepSize                  // 进度的方式
    private var starCount: Int             // 数量
    private var starEmptyDrawable: Drawable     // 空的星星
    private var starFillDrawable: Drawable      // 填满的星星
    private var starHalfDrawable: Drawable      // 半填充的星星
    var mClickable: Boolean                      //  能否点击
    //点击回调接口
    private var onRatingChangeListener: OnRatingChangeListener? = null
    fun setOnRatingListener(onRating: OnRatingChangeListener){
        onRatingChangeListener = onRating
    }

    init {
        orientation = HORIZONTAL
        val mTypedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.MyRatingBar)
        starImageSize = mTypedArray.getDimension(R.styleable.MyRatingBar_starImageSize,20f)
        starPadding = mTypedArray.getDimension(R.styleable.MyRatingBar_starPadding,10f)
        starStep = mTypedArray.getFloat(R.styleable.MyRatingBar_starStep,1f)
        stepSize = StepSize.fromStep(mTypedArray.getInt(R.styleable.MyRatingBar_stepSize, 1))
        starCount = mTypedArray.getInteger(R.styleable.MyRatingBar_starCount,5)
        starEmptyDrawable = mTypedArray.getDrawable(R.styleable.MyRatingBar_starEmpty)
        starFillDrawable = mTypedArray.getDrawable(R.styleable.MyRatingBar_starFill)
        starHalfDrawable = mTypedArray.getDrawable(R.styleable.MyRatingBar_starHalf)
        mClickable = mTypedArray.getBoolean(R.styleable.MyRatingBar_clickable,true)
        mTypedArray.recycle()

        for (i in 0 until starCount){
            val imageView: ImageView = getStarImageView()
            imageView.setImageDrawable(starEmptyDrawable)
            imageView.setOnClickListener {
                if (mClickable){
                    //浮点数的整数部分
                    var fint: Int = starStep.toInt()
                    val b1 = BigDecimal(starStep.toString())
                    val b2 = BigDecimal(fint)

                    //浮点小数部分
                    val fPoint: Float = b1.subtract(b2).toFloat()
                    if (fPoint == 0f){
                        fint -= 1
                    }

                    if (indexOfChild(it) > fint){
                        setStar(indexOfChild(it) + 1f)
                    }else if (indexOfChild(it) == fint){
                        if (stepSize == StepSize.Full){
                            return@setOnClickListener
                        }
                        //点击之后默认每次增加一颗星，再点击变为半颗星
                        if (imageView.drawable.current.constantState.equals(starHalfDrawable.constantState)){
                            setStar(indexOfChild(it) + 1f)
                        }else {
                            setStar(indexOfChild(it) + 0.5f)
                        }
                    }else {
                        setStar(indexOfChild(it) + 1f)
                    }
                }
            }
            addView(imageView)
        }
        setStar(starStep)
    }

    //设置星星的数量
    fun setStar(rating: Float) {
        if (onRatingChangeListener != null){
            onRatingChangeListener!!.onRatingChange(rating)
        }
        starStep = rating
        //浮点数的整数部分
        val fint: Int = rating.toInt()
        val b1 = BigDecimal(rating.toString())
        val b2 = BigDecimal(fint)

        //浮点小数部分
        val fPoint: Float = b1.subtract(b2).toFloat()

        //设置没选中的星星
        for (i in 0 until starCount){
            (getChildAt(i) as ImageView).setImageDrawable(starEmptyDrawable)
        }

        //设置选中的星星
        for (i in 0 until fint){
            (getChildAt(i) as ImageView).setImageDrawable(starFillDrawable)
        }
        //小数点默认增加半颗星
        if (fPoint > 0f){
            (getChildAt(fint) as ImageView).setImageDrawable(starHalfDrawable)
        }
    }

    /**
     * 设置每个星星的参数
     */
    private fun getStarImageView(): ImageView{
        val imageView = ImageView(context)
        val layout: LinearLayout.LayoutParams = LinearLayout.LayoutParams(Math.round(starImageSize),
                Math.round(starImageSize))
        layout.setMargins(0,0,Math.round(starPadding),0)
        imageView.layoutParams = layout
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.maxHeight = 10
        imageView.minimumWidth = 10
        return imageView
    }

    /**
     * 星星增加的方式：0-半星，1-整星
     */
    enum class StepSize (val stepSize: Int){
        Half(0), Full(1);
        companion object {
            fun fromStep(stepSize: Int): StepSize {
                values()
                        .filter { it.stepSize == stepSize }
                        .forEach { return it }
                throw IllegalAccessException()
            }
        }
    }

}

/**
 * 星星的点击事件
 */
interface OnRatingChangeListener{
    fun onRatingChange(ratingCount: Float)
}

package com.example.youjia.example

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.youjia.myapplication.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ratingbar.mClickable = true     //设置能否点击
        ratingbar.setStar(2.5f)          //设置评分
        ratingbar.stepSize = CustomRatingBar.StepSize.Half
        ratingbar.setOnRatingListener(object : OnRatingChangeListener {
            override fun onRatingChange(ratingCount: Float) {
                Toast.makeText(this@MainActivity,"评分：$ratingCount",Toast.LENGTH_SHORT).show()
            }

        })

        starBar.setMark(2.3f)
    }
}

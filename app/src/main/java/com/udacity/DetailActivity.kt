package com.udacity

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)


        val filename = intent.getStringExtra("fileName").toString()
        val status = intent.getStringExtra("status").toString()
        val fileNameText = findViewById<TextView>(R.id.filename)
        fileNameText.text = filename
        val statusText = findViewById<TextView>(R.id.status)
        statusText.text = status
        val animator = ObjectAnimator.ofFloat(
            statusText, View.ROTATION,
            -360f, 0f
        )
        animator.duration = 2000
        animator.start()
//        rotate()
        findViewById<Button>(R.id.okBtn).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
    /*  private fun rotate(){
          val animator = ObjectAnimator.ofFloat(
              filenameTxt, View.ROTATION,
              -360f, 0f
          )
          animator.duration = 1000
          animator.start()
      }*/
}

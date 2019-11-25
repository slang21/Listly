package com.spencer.shoppinglist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var sendRight = AnimationUtils.loadAnimation(
            this@SplashActivity,
            R.anim.right_off_screen
        )
        var sendLeft = AnimationUtils.loadAnimation(
            this@SplashActivity,
            R.anim.left_off_screen
        )

        tvMotto1.startAnimation(sendLeft)
        tvMotto2.startAnimation(sendRight)

        sendRight.setAnimationListener(
            object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    val intent = Intent(this@SplashActivity, ScrollingActivity::class.java)
                    startActivity(intent)
                    this@SplashActivity.finish()
                }

                override fun onAnimationStart(p0: Animation?) {
                }
            }
        )

    }
}

package com.example.to_do_list

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist01.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ic_logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_in)) // frogmi logo animation start
        Handler().postDelayed({
            ic_logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_out)) // frogmi logo animation end
            Handler().postDelayed({
                ic_logo.visibility = View.GONE
                startActivity(Intent(this, DashboardActivity::class.java)) // DashboardActivity start
                finish()
            },500)
        },1500)


    }


}
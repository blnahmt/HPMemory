package com.example.hpmemory.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.hpmemory.R
import kotlinx.android.synthetic.main.activity_tek_oyuncu_bitis.*

class TekOyuncuBitis : AppCompatActivity() {
    override fun onBackPressed() {
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tek_oyuncu_bitis)
        val point = intent.getDoubleExtra("point",0.0)
        val tw4:TextView = findViewById(R.id.textView4)
        tw4.text = "Puan : " + String.format("%.2f",point)

        butonToMainTek.setOnClickListener{
            val main = Intent(this, HomeActivity::class.java)
            main.putExtra("isStart",false)
            startActivity(main)
        }
    }
}
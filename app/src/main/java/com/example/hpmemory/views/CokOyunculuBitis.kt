package com.example.hpmemory.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.hpmemory.R
import kotlinx.android.synthetic.main.activity_tek_oyuncu_bitis.*

class CokOyunculuBitis : AppCompatActivity() {
    override fun onBackPressed() {
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cok_oyunculu_bitis)

        val point1 = intent.getDoubleExtra("pointBir",0.0)
        val p1: TextView = findViewById(R.id.oyuncu1Puan)
        p1.text = "Oyuncu 1 Puanı : "+String.format("%.2f",point1)
        val point2 = intent.getDoubleExtra("pointIki",0.0)
        val p2: TextView = findViewById(R.id.oyuncu2Puan)
        p2.text ="Oyuncu 2 Puanı : "+ String.format("%.2f",point2)

        val kazanan: TextView = findViewById(R.id.kazananTxT)
        if (point1 >point2){
            kazanan.text = "Oyuncu 1 Kazandı"
        }else if (point1 == point2){
            kazanan.text = "Berabere"
        }else{
            kazanan.text = "Oyuncu 2 Kazandı"
        }

        butonToMainTek.setOnClickListener{
            val main = Intent(this, HomeActivity::class.java)
            main.putExtra("isStart",false)
            startActivity(main)
        }
    }
}
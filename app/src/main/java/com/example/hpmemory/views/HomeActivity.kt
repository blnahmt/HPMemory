package com.example.hpmemory.views

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.RequiresApi
import com.example.hpmemory.R
import com.example.hpmemory.extensions.Extensions.toast
import com.example.hpmemory.utils.FirebaseUtils.firebaseAuth
import com.example.hpmemory.utils.MediaManager
import kotlinx.android.synthetic.main.activity_home.*
import java.util.Base64


class HomeActivity : AppCompatActivity() {
    var isVolumeOpen:Boolean = true
    override fun onBackPressed() {

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        val volume = sharedPreferences.getFloat("volume", 0.5f)
        isVolumeOpen = volume == 0.5f
        if (volume == 0.5f){
            buttonVolume.text = "Sesi Kapat"
        }else{
            buttonVolume.text = "Sesi Aç"
        }

         val isStart :Boolean =  intent.getBooleanExtra("isStart",true)
        if (isStart){
            MediaManager.instance.mediaPlayer = MediaPlayer.create(this,R.raw.bg_music)
            MediaManager.instance.setVolume(volume)
            MediaManager.instance.mediaPlayer.isLooping = true
            MediaManager.instance.start()
        }


        buttonVolume.setOnClickListener{
            val editor = sharedPreferences.edit()
            if(isVolumeOpen){

                editor.putFloat("volume", 0.0f)
                editor.apply()
                MediaManager.instance.setVolume(0.0f)
                isVolumeOpen = false
                MediaManager.instance.pause()
                buttonVolume.text = "Sesi Aç"
            }else{

                editor.putFloat("volume",0.5f)
                editor.apply()
                MediaManager.instance.setVolume(0.5f)
                isVolumeOpen = true
                MediaManager.instance.start()
                buttonVolume.text = "Sesi Kapat"
            }

        }

        //TODO : firebase get cards fonksiyon yap

                btnTek.setOnClickListener {
                    val zorluk:Int = when(true){
                        rb2.isChecked -> 2
                        rb4.isChecked -> 4
                        rb6.isChecked -> 6
                        else -> {
                            2
                        }
                    }
                    val intent = Intent(this, TekOyunculu::class.java)
                    intent.putExtra("zorluk", zorluk)
                    startActivity(intent)
                }

        btnİki.setOnClickListener {
            val zorluk:Int = when(true){
                rb2.isChecked -> 2
                rb4.isChecked -> 4
                rb6.isChecked -> 6
                else -> {
                    2
                }
            }
            val intent = Intent(this, IkiOyunculu::class.java)
            intent.putExtra("zorluk", zorluk)
            startActivity(intent)
        }

                btnSignOut.setOnClickListener {
                    firebaseAuth.signOut()
                    startActivity(Intent(this, CreateUserActivity::class.java))
                    toast("signed out")
                    finish()
                }

    }
}


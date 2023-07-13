package com.example.hpmemory.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.hpmemory.R
import com.example.hpmemory.extensions.Extensions.decodePicString
import com.example.hpmemory.models.Card
import com.example.hpmemory.models.GridCard
import com.example.hpmemory.utils.BackImages
import com.example.hpmemory.utils.FirebaseUtils
import com.example.hpmemory.utils.GameTime
import com.example.hpmemory.utils.MediaManager
import kotlinx.android.synthetic.main.activity_tek_oyunculu.*
import java.util.*


class TekOyunculu : AppCompatActivity() {

    private var zorluk:Int = 2
    private var listCards : List<Card> = mutableListOf()
    private var listGameCards :List<GridCard> = mutableListOf<GridCard>()
    private lateinit var all_opened: MediaPlayer
    private lateinit var found_card: MediaPlayer
    private lateinit var time_over: MediaPlayer
    val backim = BackImages


    override fun onBackPressed() {

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tek_oyunculu)
        zorluk = intent.getIntExtra("zorluk",2)
        all_opened = MediaPlayer.create(this,R.raw.all_opened)
        found_card = MediaPlayer.create(this,R.raw.found_card)
        time_over = MediaPlayer.create(this,R.raw.time_over)


        val sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        val volume = sharedPreferences.getFloat("volume", 0.5f)
        if (volume ==0.5f){
            all_opened.setVolume(1.0f,1.0f)
            found_card.setVolume(1.0f,1.0f)
            time_over.setVolume(1.0f,1.0f)
        }else{
            all_opened.setVolume(0f,0f)
            found_card.setVolume(0f,0f)
            time_over.setVolume(0f,0f)
        }


        FirebaseUtils.firebaseDB.collection("Cards")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val card: Card = Card(
                        name = document["name"] as String?,
                        home = document["home"] as String?,
                        point = (document["point"] as String).toDouble(),
                        image = (document["image"] as String).decodePicString()
                    )
                    (listCards as MutableList).add(card);
                }

                var dagilim : List<Int> = listOf(1,1,0,0)
                when(zorluk){
                    2-> dagilim = listOf(1,1,0,0)
                    4->dagilim = listOf(2,2,2,2)
                    6->dagilim = listOf(4,4,5,5)
                }

                var index : Int = 0

                for (element in dagilim)
                {
                          val numbers = mutableListOf<Int>()
                          val random = Random()
                          for (j in 0 until element) {
                              var rand = random.nextInt(10)
                              while (numbers.contains(rand)){
                                  rand = random.nextInt(10)
                              }
                              numbers.add(rand)
                          }

                          for (number in numbers){
                             var card = Card(name = "", point = 0.0, image =backim.image, home = "")

                             when(index){0-> card = listCards.filter {
                                  it.home == "Gryffindor"
                              }[number]
                              1->card = listCards.filter {
                                  it.home == "Slytherin"
                              }[number]
                                 2->card = listCards.filter {
                                     it.home == "Hufflepuff"
                                 }[number]
                                 3->card = listCards.filter {
                                     it.home == "Ravenclaw"
                                 }[number]
                             }
                              (listGameCards as MutableList).add(GridCard(card,true,false))
                              (listGameCards as MutableList).add(GridCard(card,false,false))

                      }
                        index++;
                }



               var pointTxt:TextView = findViewById(R.id.txtPoint)
                pointTxt.text = "Puan :  0"
               val gridView = GridView(this)

                gridView.numColumns = zorluk
                val intent = Intent(this, TekOyuncuBitis::class.java)
                gridView.adapter = TekAdapter(this, listGameCards.shuffled(), pointTxt,intent,all_opened,found_card,time_over,zorluk)

                view1.addView(gridView)

                btnKartlarTek.setOnClickListener{
                    var  text : String = ""
                    val list : List<GridCard> = (gridView.adapter as TekAdapter).getItems()
                    list.forEach { e->
                        val index = list.indexOf(e) + 1
                        text += "$index .) ${e.card.name}\n"
                    }

                    val builder = AlertDialog.Builder(this)


                    builder.setTitle("Kartlar : ")
                    builder.setMessage(text)



                    builder.setNegativeButton("Kapat") { dialog, id ->
                         dialog.dismiss()
                    }


                    val dialog = builder.create()


                    dialog.show()
                }

                val textView:TextView = findViewById(R.id.txtSure)
                val handler = Handler()
                GameTime.gameOver = false
                GameTime.timePassed = 0
                GameTime.timeRemaining = 45
                val runnable = object : Runnable {
                    override fun run() {
                       if (!GameTime.gameOver){
                           GameTime.timePassed++
                           GameTime.timeRemaining = 45 - GameTime.timePassed
                           if ( GameTime.timePassed == 45){

                               (gridView.adapter as TekAdapter).finishGame(false)

                           }
                           textView.text = "Kalan Süre : ${GameTime.timeRemaining}"
                           handler.postDelayed(this, 1000) // wait for 1 second (1000 milliseconds)
                       }

                    }
                }

                handler.post(runnable)

            }
        }
}




class TekAdapter(context: Context, items: List<GridCard>,pointText:TextView,intent:Intent,opened:MediaPlayer,found:MediaPlayer,time:MediaPlayer,zorluk:Int) : BaseAdapter() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private val listGameCards: List<GridCard> = items
    private var vhList : List<ViewHolder> = mutableListOf()
    private val selectedItems:List<GridCard> = mutableListOf()
    private  var point : Double = 0.0
    private var matched : Int = 0
    private val pointTxt:TextView = pointText
    private val intent:Intent = intent
    private val context:Context = context
    private  val allopened: MediaPlayer = opened
    private  val foundcard: MediaPlayer = found
    private  val timeover: MediaPlayer = time
    private var isOver :Boolean = false
    private var zorluk : Int = zorluk


    override fun getCount(): Int {
        return listGameCards.size
    }


    override fun getItem(position: Int): Any {
        return listGameCards[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    fun getItems():
            List<GridCard> {
        return listGameCards
    }

    public fun finishGame(isFullOpened:Boolean){
       if (!isOver){
           GameTime.gameOver = true
           isOver = true
           var time :Int = 0
           if (isFullOpened){

               allopened.start()
               time = 9
           }else
           {

               timeover.start()
               time = 1
           }

           val handler = Handler()
           var timeElapsed = 0

           val runnable = object : Runnable {
               override fun run() {
                   timeElapsed++
                   if (timeElapsed == time){
                       intent.putExtra("point", point)
                       context.startActivity(intent)
                   }
                   handler.postDelayed(this, 1000) // wait for 1 second (1000 milliseconds)

               }
           }

           handler.post(runnable)
       }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val vh: ViewHolder
        val backim = BackImages
        if (convertView == null) {
            view = mInflater.inflate(R.layout.card_grid, parent, false)

            vh = ViewHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ViewHolder
        }


        vh.icon.setImageBitmap(backim.image)
        (vhList as MutableList).add(vh)
        pointTxt.text = "Puan :  " + point.toString()
        vh.icon.setOnClickListener{
            if (!listGameCards[position].isMatched && !listGameCards[position].isOpened && !isOver) {
                vhList[position+1].icon.setImageBitmap(listGameCards[position].card.image)
                listGameCards[position].isOpened = true
                (selectedItems as MutableList).add(listGameCards[position])
                if (selectedItems.size ==2){
                    val isMatch:Boolean = checkMatch(selectedItems[0].card,selectedItems[1].card)
                    if (isMatch){

                        foundcard.start()
                        listGameCards[listGameCards.indexOf( selectedItems[0])].isMatched = true
                        listGameCards[listGameCards.indexOf( selectedItems[1])].isMatched = true

                        puanHesapla(true,selectedItems[0].card,selectedItems[1].card)
                        selectedItems.clear()


                        matched += 2
                        if (listGameCards.size == matched){

                             finishGame(true)
                        }
                    }else{

                           puanHesapla(false,selectedItems[0].card,selectedItems[1].card)

                        val timer = Timer()
                        timer.schedule(object: TimerTask() {
                            override fun run() {
                                vhList[listGameCards.indexOf( selectedItems[0])+1].icon.setImageBitmap(backim.image)
                                vhList[listGameCards.indexOf( selectedItems[1])+1].icon.setImageBitmap(backim.image)
                               listGameCards[ listGameCards.indexOf( selectedItems[0])].isOpened = false
                                listGameCards[ listGameCards.indexOf( selectedItems[1])].isOpened = false
                                selectedItems.clear()
                            }
                        },1000)
                    }
                }
            }
        }
        return view
    }

    @SuppressLint("SuspiciousIndentation")
    fun puanHesapla(isIncrease:Boolean, card1:Card, card2:Card) {

        var evKatsayi1:Int = 0
        var evKatsayi2:Int = 0

              when  (card1.home){
                  "Gryffindor" -> evKatsayi1 = 2
                  "Slytherin" -> evKatsayi1 = 2
                  "Hufflepuff" -> evKatsayi1 = 1
                  "Ravenclaw" -> evKatsayi1 = 1
              }

        when  (card2.home){
            "Gryffindor" -> evKatsayi2 = 2
            "Slytherin" -> evKatsayi2 = 2
            "Hufflepuff" -> evKatsayi2 = 1
            "Ravenclaw" -> evKatsayi2 = 1
        }

        if (isIncrease){
            point += (2* card1.point!! *evKatsayi1) * (GameTime.timeRemaining.toDouble() / 10)
        }else{

            if (card1.home == card2.home){
                val toplam = card1.point?.plus(card2.point!!)
                point -= (toplam?.div(evKatsayi1))?.times((GameTime.timePassed.toDouble() /10)) ?: 10.0
            }else{
                val ortalama = card1.point?.plus(card2.point!!)?.div(2) ?: 1.0
                point -= (ortalama*evKatsayi1*evKatsayi2)*(GameTime.timePassed.toDouble()/10)
            }
        }
        pointTxt.text = "Puan :  " + String.format("%.2f", point)
    }

    class ViewHolder(view: View) {

        val icon: ImageView = view.findViewById(R.id.icon)
    }

    fun checkMatch(first:Card,second:Card ): Boolean {
        return first.name == second.name
    }
}





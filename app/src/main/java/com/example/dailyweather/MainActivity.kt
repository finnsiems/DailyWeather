package com.example.dailyweather

//import jdk.nashorn.internal.parser.JSONParser
import android.R.attr.bitmap
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyweather.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset


class Response(json: String) : JSONObject(json) {
    val type: String? = this.optString("type")
    val data = this.optJSONArray("data")
        ?.let { 0.until(it.length()).map { i -> it.optJSONObject(i) } } // returns an array of JSONObject
        ?.map { Foo(it.toString()) } // transforms each JSONObject of the array into Foo
}

class Foo(json: String) : JSONObject(json) {
    val id = this.optInt("id")
    val title: String? = this.optString("title")
}


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var temp = ""
    var tempMax = ""
    var tempMin = ""
    var tempFeelsLike = ""
    var humidity = ""
    var desc = ""
    var sunrise = ""
    var sunset = ""
    var color: Color? = null
    var city = "Paris"

    var timeInputList = arrayListOf<String>()
    var tempInputList = arrayListOf<String>()
    private lateinit var rocketAnimation: AnimationDrawable

    internal var x1: Float = 0.toFloat()
    internal var x2: Float = 0.toFloat()
    internal var y1: Float = 0.toFloat()
    internal var y2: Float = 0.toFloat()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timeInputList.addAll(0, listOf("14 Uhr", "15 Uhr", "16 Uhr", "17 Uhr"))
        tempInputList.addAll(0, listOf("20°C", "19°C", "18°C", "17°C"))

        load()
        updateData()
        weather(city)

        /*val rocketImage = binding.imageviewWeatherIcon.apply {
            setBackgroundResource()
            rocketAnimation = background as AnimationDrawable
        }
        rocketImage.setOnClickListener({ rocketAnimation.start() })*/


    /*var image = binding.imageviewWeatherIcon
    image.setBackgroundResource(R.drawable.animation)
    var frameAnimation: AnimationDrawable = image.background
    frameAnimation.start()

    val img: ImageView = binding.imageviewWeatherIcon
    //ContextCompat.getDrawable(this, )
    img.setBackgroundResource(getDrawable(R.drawable.animation))
    img.getBackground().start()*/



        binding.buttonUpdate.setOnClickListener {
            weather(city)
        }

        binding.buttonUpdateCity.setOnClickListener {
            var cityName = binding.edittextChangeCity.text
            var cityVar = ""
            if(cityName.contains(" ")){
                cityVar = cityName.toString().replace(' ' ,'_')
            }else{
                cityVar = cityName.toString()
            }
            try {
                weather(cityVar)
            }catch (e:Exception){
                Toast.makeText(this, "City doesn't exist",Toast.LENGTH_LONG).show()
            }

        }

    }

    fun weather(cityVar: String){
        Thread {
            try{
                val from =
                    "https://api.openweathermap.org/data/2.5/weather?q=" + cityVar.lowercase() + "&appid=c1e2120945a0c1d7a5ca12a57d5f4179&units=metric&lang=de"
                val url = URL(from)
                val builder = StringBuilder()

                val bufferedReader =
                    BufferedReader(InputStreamReader(url.openStream(), Charset.forName("UTF8")))
                var str: String
                while ((bufferedReader.readLine().also { str = it }) != null) {
                    builder.append(str)
                    val build = builder.toString()

                    try {
                        val json = Response(build)
                        var main = json["main"]
                        city = json["name"].toString()
                        var jsonMain = main as JSONObject
                        temp = jsonMain["temp"].toString()
                        humidity = jsonMain["humidity"].toString()
                        tempMin = jsonMain["temp_min"].toString()
                        tempMax = jsonMain["temp_max"].toString()
                        tempFeelsLike = jsonMain["feels_like"].toString()

                        var weather = json["weather"]
                        var jsonWeatherArray = weather as JSONArray
                        var jsonWeather = jsonWeatherArray[0] as JSONObject
                        desc = jsonWeather["description"].toString()

                        var sys = json["sys"]
                        var jsonSys = sys as JSONObject
                        sunrise = jsonSys["sunrise"].toString()
                        sunset = jsonSys["sunset"].toString()


                        Log.d("NoteThing", "\n" + "Temp: " + temp + "\n" + "Feels Like: " + tempFeelsLike + "\n" + "Desc: " + desc)
                        Log.d("NoteThing", json.toString())


                        //updateData()
                        try {
                            binding.textviewTemp.post(Runnable { binding.textviewTemp.text = temp })
                            binding.textviewTempMax.post(Runnable {
                                binding.textviewTempMax.text = tempMax
                            })
                            binding.textviewTempMin.post(Runnable {
                                binding.textviewTempMin.text = tempMin
                            })
                            binding.textviewCity.post(Runnable { binding.textviewCity.text = city })
                            binding.textviewHumidity.post(Runnable {
                                binding.textviewHumidity.text = humidity
                            })
                        }catch (e:Exception){
                            Log.d("NoteThing", "err: " + e)
                        }
                        save()



                    }catch (e:Exception){
                        Log.d("Notething", "Response Exeption: " + e)
                    }
                }


            }catch (e:Exception){
                Log.d("Notething", "Exeption: " + e)
            }

        }.start()
    }



    fun load(){
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return

        try {
            temp = sharedPref.getString("temp", "").toString()
            tempMax = sharedPref.getString("tempMax", "").toString()
            tempMin = sharedPref.getString("tempMin", "").toString()
            city = sharedPref.getString("city", "").toString()
            humidity = sharedPref.getString("humidity", "").toString()
        }catch (e:Exception) {
            Log.d("NoteThing", ("Loading impossible: " + e))
        }
    }

    fun save() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val editor = sharedPref.edit()
        try {
            editor.putString("temp", temp)
            editor.putString("tempMax", tempMax)
            editor.putString("tempMin", tempMin)
            editor.putString("city", city)
            editor.putString("humidity", humidity)
            editor.apply()
        } catch (e: Exception) {
            Log.d("NoteThing", ("Saving impossible: " + e))
        }
    }

    fun updateData(){
        binding.textviewTemp.text = temp
        binding.textviewTempMax.text = tempMax
        binding.textviewTempMin.text = tempMin
        binding.textviewCity.text = city
        binding.textviewHumidity.text = humidity
        save()
    }

    override fun onTouchEvent(tochevent: MotionEvent): Boolean {
        try{
        when (tochevent.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = tochevent.x
                y1 = tochevent.y
            }
            MotionEvent.ACTION_UP -> {
                x2 = tochevent.x
                y2 = tochevent.y
                if (x1 < x2) {
                    val i = Intent(this@MainActivity, SecondActivity::class.java)
                    startActivity(i)
                }
            }
        }
        }catch (e:Exception){
            Log.d("NoteThing", "help: " + e)
        }
        return false
    }


}






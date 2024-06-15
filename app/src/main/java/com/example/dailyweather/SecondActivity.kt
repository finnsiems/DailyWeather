package com.example.dailyweather

//import jdk.nashorn.internal.parser.JSONParser
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyweather.databinding.ActivitySecondBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset


class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
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
    var descInputList = arrayListOf<String>()


    private lateinit var rocketAnimation: AnimationDrawable

    internal var x1: Float = 0.toFloat()
    internal var x2: Float = 0.toFloat()
    internal var y1: Float = 0.toFloat()
    internal var y2: Float = 0.toFloat()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)



        load()
        updateData()
        //setRecyclerViewNotes()
        hourlyWeather(city)




        binding.buttonUpdate.setOnClickListener {
            hourlyWeather(city)
        }

       binding.buttonUpdateCity.setOnClickListener {
            var cityName = binding.edittextChangeCity.text
            var cityVar = ""
            if(cityName.contains(" ")){
                cityVar = cityName.toString().replace(' ' ,'+')
            }else{
                cityVar = cityName.toString()
            }
            try {
                hourlyWeather(cityVar)
            }catch (e:Exception){
                Toast.makeText(this, "City doesn't exist",Toast.LENGTH_LONG).show()
            }

        }

    }

    fun hourlyWeather(cityVar:String){
        Thread {
            try{
                val from =
                    "https://api.openweathermap.org/data/2.5/forecast?q=" + cityVar.lowercase() + "&appid=c1e2120945a0c1d7a5ca12a57d5f4179&units=metric"
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
                        var list = json["list"]
                        var jsonListArray = list as JSONArray
                        var jsonList = jsonListArray[0] as JSONObject
                        var main = jsonList["main"]
                        var jsonMain = main as JSONObject
                        temp = jsonMain["temp"].toString()
                        temp = temp.split(".")[0]
                        Log.d("NoteThing", "TempTest: " + temp)

                        humidity = jsonMain["humidity"].toString()
                        Log.d("NoteThing", "TempTest: " + humidity)

                        tempMin = jsonMain["temp_min"].toString()
                        tempMin = tempMin.split(".")[0]
                        Log.d("NoteThing", "TempTest: " + tempMin)

                        tempMax = jsonMain["temp_max"].toString()
                        tempMax = tempMax.split(".")[0]
                        Log.d("NoteThing", "TempTest: " + tempMax)

                        tempFeelsLike = jsonMain["feels_like"].toString()
                        tempFeelsLike = tempFeelsLike.split(".")[0]
                        Log.d("NoteThing", "TempTest: " + tempFeelsLike)


                        var weather = jsonList["weather"]
                        var jsonWeatherArray = weather as JSONArray
                        var jsonWeather = jsonWeatherArray[0] as JSONObject
                        desc = jsonWeather["description"].toString()
                        Log.d("NoteThing", "TempTest: " + desc)

                        var cityList = json["city"]
                        var jsonCity = cityList as JSONObject
                        city = jsonCity["name"].toString()
                        Log.d("NoteThing", "TempTest: " + city)


                        var hourTempList = arrayListOf<String>()
                        var hourTimeList = arrayListOf<String>()
                        var hourDescList = arrayListOf<String>()



                        for(hour in 9 downTo 1) {
                            Log.d("NoteThing", "Ahhh")

                            var jsonListHour = jsonListArray[hour] as JSONObject
                            var mainHour = jsonListHour["main"]
                            var jsonMainHour = mainHour as JSONObject
                            var hourTemp = jsonMainHour["temp"].toString()
                            hourTemp = hourTemp.split(".")[0] +"°C"
                            hourTempList.add(hourTemp)

                            var time = jsonListHour["dt_txt"].toString().split(" ")[1].split(":")[0] + ":00"
                            hourTimeList.add(time)

                            var weather = jsonListHour["weather"]
                            var jsonWeatherArray = weather as JSONArray
                            var jsonWeather = jsonWeatherArray[0] as JSONObject
                            var hourDesc = jsonWeather["description"].toString()
                            hourDescList.add(hourDesc)

                            Log.d("NoteThing", "HourTest: " + hourTemp + " " + time)

                        }

                        timeInputList = hourTimeList
                        tempInputList = hourTempList
                        descInputList = hourDescList


                        updateData()

                        //Log.d("NoteThing", json.toString())

                    } catch (e: Exception) {
                        Log.d("Notething", "Exeption: " + e)
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
            tempFeelsLike = sharedPref.getString("tempFeelsLike", "").toString()
            desc = sharedPref.getString("desc", "").toString()

            var tempInput = sharedPref.getString("tempList", "").toString()
            var timeInput = sharedPref.getString("timeList", "").toString()
            var descInput = sharedPref.getString("descList", "").toString()

            Log.d("NoteThing", ("TEmpInput: " + tempInput))

            tempInputList.addAll(tempInput.split(":"))
            timeInputList.addAll(timeInput.split(":"))
            descInputList.addAll(descInput.split(":"))

            tempInputList.clear()
            timeInputList.clear()
            descInputList.clear()




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
            editor.putString("tempFeelsLike", tempFeelsLike)
            editor.putString("desc", desc)
            editor.putString("tempList", tempInputList.joinToString(separator = ":"))
            editor.putString("timeList", timeInputList.joinToString(separator = ":"))
            editor.putString("descList", descInputList.joinToString(separator = ":"))
            Log.d("NoteThing", ("TEmpInput: " + tempInputList.joinToString(separator = ":")))
            editor.apply()
        } catch (e: Exception) {
            Log.d("NoteThing", ("Saving impossible: " + e))
        }
    }

    fun updateData(){
        try {
            binding.textviewTemp.post(Runnable { binding.textviewTemp.text = temp + "°C"})
            binding.textviewTempMax.post(Runnable {
                binding.textviewTempMax.text = tempMax + "°C"
            })
            binding.textviewTempMin.post(Runnable {
                binding.textviewTempMin.text = tempMin + "°C"
            })
            binding.textviewCity.post(Runnable { binding.textviewCity.text = city })
            binding.textviewHumidity.post(Runnable {
                binding.textviewHumidity.text = humidity + "%"
            })
            binding.textviewFeelsLikeText.post(Runnable {
                binding.textviewFeelsLikeText.text = "Feels like: " + tempFeelsLike + "°C"
            })
            binding.textviewDesc.post(Runnable {
                binding.textviewDesc.text = desc
            })

            binding.recyclerview.post(Runnable {
                setRecyclerViewNotes()
            })
            save()


        }catch (e:Exception){
            Log.d("NoteThing", "Update Error: " + e)
        }
    }


    fun setRecyclerViewNotes() {
        //if empty create one empty element
        try {
            val recyclerView_HourlyWeather: RecyclerView = binding.recyclerview

            recyclerView_HourlyWeather.layoutManager = LinearLayoutManager(this)
            recyclerView_HourlyWeather.setHasFixedSize(true)
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            linearLayoutManager.reverseLayout = true
            linearLayoutManager.stackFromEnd = true
            recyclerView_HourlyWeather.layoutManager = linearLayoutManager
            //adapter is filled with elements from Arraylist Notifications
            var adapterNotes: Hourly_Recycler? = null
            adapterNotes =
                Hourly_Recycler(this, timeInputList, tempInputList, descInputList,1)
            recyclerView_HourlyWeather.adapter = adapterNotes
        } catch (e: Exception) {
            Log.d("NoteThing", "Can't create Notification RecyclerView:" + e)
        }
    }







   /* override fun onTouchEvent(tochevent: MotionEvent): Boolean {
        try {
        when (tochevent.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = tochevent.x
                y1 = tochevent.y
            }
            MotionEvent.ACTION_UP -> {
                x2 = tochevent.x
                y2 = tochevent.y
                if (x1 < x2) {
                    val i = Intent(this@SecondActivity, MainActivity::class.java)
                    startActivity(i)
                }
            }
        }
        }catch (e:Exception){
            Log.d("NoteThing", "help: " + e)
        }
        return false
    }*/


}

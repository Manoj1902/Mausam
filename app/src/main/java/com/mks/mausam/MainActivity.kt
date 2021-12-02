package com.mks.mausam

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil


class MainActivity : AppCompatActivity() {

    val CITY: String = "Delhi,IN"
    val API: String = "6f0183f7099e010ba634973be5c6e1a3"
//    val URL: String = "https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metrics&appid=$API"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)

        val latitude = intent.getStringExtra("lat")
        val longitude = intent.getStringExtra("long")
//        Toast.makeText(this, latitude+ " " +longitude, Toast.LENGTH_LONG).show()

        window.statusBarColor= Color.parseColor("#8121ff")

        getJsonData(latitude, longitude)
        progressBar.visibility = View.VISIBLE


        button.setOnClickListener{
            getJsonData(latitude, longitude)
            progressBar.visibility = View.VISIBLE
        }

    }

    private fun getJsonData(latitude: String?, longitude: String?) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=${latitude}&lon=${longitude}&appid=${API}"

// Request a string response from the provided URL.
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                setValues(response)
            },
            Response.ErrorListener { Toast.makeText(this, "Connection Error!", Toast.LENGTH_LONG).show() })

// Add the request to the RequestQueue.
        queue.add(jsonRequest)
    }




    @SuppressLint("SetTextI18n")
    private fun setValues(response: JSONObject?) {
        address.text = response?.getString("name")
        country_code.text = response?.getJSONObject("sys")?.getString("country")
        var lat = response?.getJSONObject("coord")?.getString("lat")
        var long = response?.getJSONObject("coord")?.getString("lon")
        temp.text = "${lat} , ${long}"
        status.text = response?.getJSONArray("weather")?.getJSONObject(0)?.getString("main")
        val weatherStatus = response?.getJSONArray("weather")?.getJSONObject(0)?.getString("main").toString()





//        Temperature
        var tempr = response?.getJSONObject("main")?.getString("temp")
        tempr = ((((tempr)?.toFloat()?.minus(273.15)))?.toInt()).toString()
        temp.text = "${tempr}°C"

//       Minimum Temperature
        var mintemp = response?.getJSONObject("main")?.getString("temp_min")
        mintemp = ((((mintemp)?.toFloat()?.minus(273.15)))?.toInt()).toString()
        min_temp.text = "Min: ${mintemp}°C"

//        Maximum Temperature
        var maxtemp = response?.getJSONObject("main")?.getString("temp_max")
        maxtemp = (((maxtemp)?.toFloat()?.minus(273.15)?.let { ceil(it) })?.toInt()).toString()
        max_temp.text = "Max: ${maxtemp}°C"


        pressure.text = response?.getJSONObject("main")?.getString("pressure")
        humidity.text = response?.getJSONObject("main")?.getString("humidity")+"%"
        wind.text = response?.getJSONObject("wind")?.getString("speed")
        wind.text = response?.getJSONObject("wind")?.getString("speed")


//        Update At
        var updateAt: Long? = response?.getLong("dt")
        var updateAtText = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(
            updateAt!! * 1000))

        updated_at.text = "Update at: ${updateAtText.toString()}"
//        Am or Pm
        var amPm = updateAtText.toString()
        var time = amPm.substring(amPm.length - 2, amPm.length)
//        var time = "AM"
        Log.e("Time: ", time);

        weatherImage(weatherStatus, time)


//        Sunrise At
        var sunrise: Long? = response?.getJSONObject("sys")?.getLong("sunrise")
        var sunriseText = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(
            sunrise!! * 1000))

        sun_rise.text = sunriseText.toString()

//        Sunset At
        var sunset: Long? = response?.getJSONObject("sys")?.getLong("sunset")
        var sunsetText = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(
            sunset!! * 1000))

        sun_set.text = sunsetText.toString()

        info.setOnClickListener {

            val view = View.inflate(this, R.layout.custom_dialog, null)

            val builder = AlertDialog.Builder(this)
            builder.setView(view)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)

            if ((time == "PM") || (time == "pm") || (time == "Pm") || (time == "pM")){
                view.dialog_box.setBackgroundColor(Color.parseColor("#212121"))
                view.dialog_title.setTextColor(Color.parseColor("#FFFFFF"))
                view.second_line.setTextColor(Color.parseColor("#FFFFFF"))
                view.okBtn.setBackgroundColor(Color.parseColor("#212121"))
                view.okBtn.setTextColor(Color.parseColor("#FFFFFF"))

            }



            view.okBtn.setOnClickListener{
                dialog.dismiss()
            }


        }

        progressBar.visibility = View.GONE
    }

    private fun weatherImage(weatherStatus: String, time: String) {
//        Toast.makeText(this, weatherStatus, Toast.LENGTH_LONG).show()
        when (weatherStatus) {
            "Clouds" -> status_img.setImageResource(R.drawable.clouds)
            "Rain" -> status_img.setImageResource(R.drawable.heavy_rain)
            "Clear" -> status_img.setBackgroundResource(R.drawable.clear)
            "Mist" -> status_img.setBackgroundResource(R.drawable.mist)
            "Drizzle" -> status_img.setBackgroundResource(R.drawable.drizzle)
            "Thunderstorm" -> status_img.setBackgroundResource(R.drawable.thunderstorm)
            "Haze" -> status_img.setBackgroundResource(R.drawable.haze)
            "Fog" -> status_img.setBackgroundResource(R.drawable.fog)

            else -> {
                Toast.makeText(this, "Connection Error!", Toast.LENGTH_LONG).show()
            }
        }

        if ((time == "AM") || (time == "am") || (time == "Am") || (time == "aM")){
            activity_background.setBackgroundResource(R.drawable.day)
        }
        if ((time == "PM") || (time == "pm") || (time == "Pm") || (time == "pM")){
            activity_background.setBackgroundResource(R.drawable.night)
            window.statusBarColor= Color.parseColor("#2A0A57")
            sun_rise_box.setBackgroundColor(Color.parseColor("#4D305C"))
            sun_set_box.setBackgroundColor(Color.parseColor("#4D305C"))
            wind_box.setBackgroundColor(Color.parseColor("#4D305C"))
            pressure_box.setBackgroundColor(Color.parseColor("#4D305C"))
            humidity_box.setBackgroundColor(Color.parseColor("#4D305C"))
            info.setBackgroundColor(Color.parseColor("#4D305C"))
            button.setBackgroundColor(Color.parseColor("#3A0F73"))

        }


    }


}
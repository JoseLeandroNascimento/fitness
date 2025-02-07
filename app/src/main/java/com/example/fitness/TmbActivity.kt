package com.example.fitness

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitness.models.Calc

class TmbActivity : AppCompatActivity() {

    private lateinit var lifestyles: AutoCompleteTextView
    private lateinit var editWeight: EditText
    private lateinit var editHeight: EditText
    private lateinit var editAge: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tmb)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifestyles = findViewById(R.id.auto_lifestyle)
        editWeight = findViewById(R.id.edit_tmb_weight)
        editHeight = findViewById(R.id.edit_tmb_height)
        editAge = findViewById(R.id.edit_tmb_age)


        val items = resources.getStringArray(R.array.tmb_lifestyle)
        lifestyles.setText(items.first())
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)

        lifestyles.setAdapter(adapter)

        val btnSend = findViewById<Button>(R.id.btn_tmb_send)

        btnSend.setOnClickListener {

            if (!validate()) {
                Toast.makeText(this, R.string.fields_message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val weight = editWeight.text.toString().toInt()
            val height = editHeight.text.toString().toInt()
            val age = editAge.text.toString().toInt()

            val result = calculateTmb(weight, height, age)

            val response = tmbRequest(result)

            AlertDialog.Builder(this)
                .setMessage(getString(R.string.tmb_response, response))
                .setPositiveButton(
                    android.R.string.ok
                ) { dialog, which -> }
                .setNegativeButton(R.string.save) { dialog, which ->

                    Thread {
                        val app = (application as App)
                        val dao = app.db.calcDao()
                        dao.insert(Calc(type = "tmb", res = response))

                        runOnUiThread {
                            openListActivity()
                        }
                    }.start()

                }
                .show()

        }
    }

    private fun tmbRequest(tmb: Double): Double {
        val items = resources.getStringArray(R.array.tmb_lifestyle)

        return when (lifestyles.text.toString()) {
            items[0] -> tmb * 1.2
            items[1] -> tmb * 1.375
            items[2] -> tmb * 1.55
            items[3] -> tmb * 1.727
            items[4] -> tmb * 1.9
            else -> 0.0
        }


    }

    fun calculateTmb(weight: Int, height: Int, age: Int): Double {

        return 66 + (13.8 * weight) + (5 + height) - (6.8 * age)
    }

    private fun openListActivity() {
        val intent = Intent(this@TmbActivity, ListCalcActivity::class.java)
        intent.putExtra("type", "tmb")
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menu_search) {

            finish()
            openListActivity()

        }

        return super.onOptionsItemSelected(item)

    }

    private fun validate(): Boolean {

        val valueWeight = editWeight.text
        val valueHeight = editHeight.text
        val valueAge = editAge.text

        return (valueWeight.toString().isNotEmpty()
                && valueHeight.toString().isNotEmpty()
                && !valueWeight.toString().startsWith("0")
                && !valueHeight.toString().startsWith("0"))
                && valueAge.toString().isNotEmpty()
                && !valueAge.toString().startsWith("0")

    }
}
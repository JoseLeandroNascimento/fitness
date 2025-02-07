package com.example.fitness

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.models.Calc
import java.text.SimpleDateFormat
import java.util.Locale

class ListCalcActivity : AppCompatActivity() {

    private lateinit var rcListCac: RecyclerView
    private lateinit var listCalc: List<Calc>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_calc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = ListCalcAdapter()

        rcListCac = findViewById(R.id.list_historico_imc)
        rcListCac.adapter = adapter
        rcListCac.layoutManager = LinearLayoutManager(this)

        val type = intent.getStringExtra("type") ?: throw IllegalStateException("Type not found")

        Thread {

            val app = application as App
            val dao = app.db.calcDao()
            listCalc = dao.getRegisterByType(type)

            runOnUiThread {
                Log.i("Teste", "Resposta $listCalc")
            }

        }.start()

    }

    inner class ListCalcAdapter() : RecyclerView.Adapter<CalcItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalcItemHolder {

            val item = LayoutInflater.from(parent.context).inflate(R.layout.imc_item, parent, false)

            return CalcItemHolder(item)
        }

        override fun getItemCount(): Int {
            return listCalc.size
        }

        override fun onBindViewHolder(holder: CalcItemHolder, position: Int) {

            holder.bind(listCalc[position])
        }


    }

    class CalcItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var tvType: TextView
        private lateinit var tvRes: TextView
        private lateinit var tvDate: TextView

        fun bind(calc: Calc) {

            tvType = itemView.findViewById(R.id.item_imc_type)
            tvRes = itemView.findViewById(R.id.item_imc_res)
            tvDate = itemView.findViewById(R.id.item_imc_date)

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm",Locale("pt","BR"))

            tvType.text = calc.type
            tvRes.text = calc.res.toString()
            tvDate.text = sdf.format(calc.createdDate)

        }

    }
}
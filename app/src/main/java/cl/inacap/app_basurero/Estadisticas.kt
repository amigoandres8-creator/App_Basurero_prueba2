package cl.inacap.app_basurero

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Estadisticas : AppCompatActivity() {


    private lateinit var tvusuestadisticas: TextView
    private lateinit var tvactivaciones: TextView
    private lateinit var tvdesactivaciones: TextView
    private lateinit var tvobstaculos: TextView
    private lateinit var tvdistancia: TextView
    private lateinit var btnvolest: Button

    private var usu: Usuario? = null

    private lateinit var bdAcciones: DatabaseReference
    private var escuchaAcciones: ValueEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_estadisticas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvusuestadisticas = findViewById(R.id.tvusuestadisticas)
        tvactivaciones = findViewById(R.id.tvactivaciones)
        tvdesactivaciones = findViewById(R.id.tvdesactivaciones)
        tvobstaculos = findViewById(R.id.tvobstaculos)
        tvdistancia = findViewById(R.id.tvdistancia)
        btnvolest = findViewById(R.id.btnvolest)

        usu = intent.getSerializableExtra("usu", Usuario::class.java)

        tvusuestadisticas.setText(usu?.nombre + " " + usu?.apellido)

        bdAcciones = FirebaseDatabase.getInstance().getReference("acciones")

        mostrarEstadisticas()
        volver()
        desactivarFlecha()

    }



    fun mostrarEstadisticas() {
        escuchaAcciones = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                var activaciones = 0
                var desactivaciones = 0
                var obstaculos = 0
                var distancia = 0

                for (dato in snapshot.children) {
                    var acc = dato.getValue(Accion::class.java)

                    if (acc != null) {
                        if (acc.accion == "Circuito Activado") {
                            activaciones++
                        } else if (acc.accion == "Circuito Desactivado") {
                            desactivaciones++
                        } else if (acc.distancia > 0) {
                            obstaculos++
                            distancia += acc.distancia
                        }
                    }
                }

                tvactivaciones.setText("" + activaciones)
                tvdesactivaciones.setText("" + desactivaciones)
                tvobstaculos.setText("" + obstaculos)
                tvdistancia.setText("" + distancia + " cm")
            }

            override fun onCancelled(error: DatabaseError) {
                tvactivaciones.setText("-")
                tvdesactivaciones.setText("-")
                tvobstaculos.setText("-")
                tvdistancia.setText("-")
            }

        }

        bdAcciones.addValueEventListener(escuchaAcciones!!)
    }



    fun volver() {
        btnvolest.setOnClickListener {
            var intent = Intent(this@Estadisticas, Menu::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
            finish()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        if (escuchaAcciones != null) {
            bdAcciones.removeEventListener(escuchaAcciones!!)
        }
    }



    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {

        }
    }

}

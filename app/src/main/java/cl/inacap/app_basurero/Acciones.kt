package cl.inacap.app_basurero

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class Acciones : AppCompatActivity() {


    private lateinit var tvusuacciones: TextView
    private lateinit var lvacciones: ListView
    private lateinit var btnvolacc: Button

    private var usu: Usuario? = null

    private var lista = ArrayList<String>()
    private lateinit var adaptador: ArrayAdapter<String>

    private lateinit var consulta: Query
    private var escuchaAcciones: ValueEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acciones)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvusuacciones = findViewById(R.id.tvusuacciones)
        lvacciones = findViewById(R.id.lvacciones)
        btnvolacc = findViewById(R.id.btnvolacc)

        usu = intent.getSerializableExtra("usu", Usuario::class.java)

        tvusuacciones.setText(usu?.nombre + " " + usu?.apellido)

        adaptador = ArrayAdapter(this, R.layout.item_accion, lista)
        lvacciones.adapter = adaptador

        consulta = FirebaseDatabase.getInstance().getReference("acciones").limitToLast(50)

        cargarAcciones()
        volver()
        desactivarFlecha()

    }



    fun cargarAcciones() {
        escuchaAcciones = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()

                for (dato in snapshot.children) {
                    var acc = dato.getValue(Accion::class.java)

                    if (acc != null) {
                        var dis = if (acc.distancia > 0) "" + acc.distancia + " cm" else "-"

                        var texto = "Fecha       : " + acc.fecha + "\n" +
                                    "Hora         : " + acc.hora + "\n" +
                                    "Usuario    : " + acc.usuario + "\n" +
                                    "Acción      : " + acc.accion + "\n" +
                                    "Distancia : " + dis

                        lista.add(0, texto)
                    }
                }

                if (lista.isEmpty()) {
                    lista.add("No hay acciones registradas.")
                }

                adaptador.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                lista.clear()
                lista.add("¡ Error de Conexión Con La Base de Datos !")
                adaptador.notifyDataSetChanged()
            }

        }

        consulta.addValueEventListener(escuchaAcciones!!)
    }



    fun volver() {
        btnvolacc.setOnClickListener {
            var intent = Intent(this@Acciones, Menu::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
            finish()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        if (escuchaAcciones != null) {
            consulta.removeEventListener(escuchaAcciones!!)
        }
    }



    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {

        }
    }

}

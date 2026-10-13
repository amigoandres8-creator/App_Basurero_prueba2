package cl.inacap.app_basurero

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ManipularCircuito : AppCompatActivity() {


    private lateinit var tvusumanipular: TextView
    private lateinit var tvestado: TextView
    private lateinit var btnact: Button
    private lateinit var btndes: Button
    private lateinit var btnvolman: Button

    private var usu: Usuario? = null

    private lateinit var bdCircuito: DatabaseReference
    private lateinit var bdAcciones: DatabaseReference
    private var escuchaEstado: ValueEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manipular_circuito)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvusumanipular = findViewById(R.id.tvusumanipular)
        tvestado = findViewById(R.id.tvestado)
        btnact = findViewById(R.id.btnact)
        btndes = findViewById(R.id.btndes)
        btnvolman = findViewById(R.id.btnvolman)

        usu = intent.getSerializableExtra("usu", Usuario::class.java)

        tvusumanipular.setText(usu?.nombre + " " + usu?.apellido)

        bdCircuito = FirebaseDatabase.getInstance().getReference("circuito")
        bdAcciones = FirebaseDatabase.getInstance().getReference("acciones")

        escucharEstado()
        activar()
        desactivar()
        volver()
        desactivarFlecha()

    }



    fun escucharEstado() {
        escuchaEstado = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                var estado = snapshot.child("estado").getValue(String::class.java)
                var quien = snapshot.child("usuario").getValue(String::class.java)

                if (estado == "ACTIVADO") {
                    tvestado.setText("Estado : ACTIVADO\n(por " + quien + ")")
                    tvestado.setTextColor(Color.parseColor("#2E7D32"))
                } else if (estado == "DESACTIVADO") {
                    tvestado.setText("Estado : DESACTIVADO\n(por " + quien + ")")
                    tvestado.setTextColor(Color.parseColor("#C62828"))
                } else {
                    tvestado.setText("Estado : SIN DATOS")
                    tvestado.setTextColor(Color.BLACK)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                tvestado.setText("Estado : SIN CONEXIÓN")
                tvestado.setTextColor(Color.BLACK)
            }

        }

        bdCircuito.addValueEventListener(escuchaEstado!!)
    }



    fun activar() {
        btnact.setOnClickListener {
            cambiarEstado("ACTIVADO", "Circuito Activado")
        }
    }



    fun desactivar() {
        btndes.setOnClickListener {
            cambiarEstado("DESACTIVADO", "Circuito Desactivado")
        }
    }



    fun cambiarEstado(estado: String, textoAccion: String) {
        var rut = usu?.rut ?: ""

        var datos = HashMap<String, Any>()
        datos["estado"] = estado
        datos["usuario"] = rut

        bdCircuito.updateChildren(datos)
            .addOnSuccessListener {
                registrarAccion(textoAccion, rut)
                Toast.makeText(this@ManipularCircuito, "¡ " + textoAccion + " !", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@ManipularCircuito, "¡ No Se Pudo Cambiar El Estado !", Toast.LENGTH_SHORT).show()
            }
    }



    fun registrarAccion(textoAccion: String, rut: String) {
        var fecha = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        var hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        var acc = Accion(fecha, hora, rut, textoAccion, 0)

        bdAcciones.push().setValue(acc)
    }



    fun volver() {
        btnvolman.setOnClickListener {
            var intent = Intent(this@ManipularCircuito, Menu::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
            finish()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        if (escuchaEstado != null) {
            bdCircuito.removeEventListener(escuchaEstado!!)
        }
    }



    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {

        }
    }

}

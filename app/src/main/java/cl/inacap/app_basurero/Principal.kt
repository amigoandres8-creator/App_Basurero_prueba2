package cl.inacap.app_basurero

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
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

class Principal : AppCompatActivity() {


    private lateinit var txtrut: EditText
    private lateinit var txtcon: EditText
    private lateinit var btnini: Button
    private lateinit var btncrear: Button

    private lateinit var bd: DatabaseReference

    private var intentos = 0
    private var maxIntentos = 3
    private var tiempoBloqueo: Long = 30000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtrut = findViewById(R.id.txtrut)
        txtcon = findViewById(R.id.txtcon)
        btnini = findViewById(R.id.btnini)
        btncrear = findViewById(R.id.btncrear)

        bd = FirebaseDatabase.getInstance().getReference("usuarios")

        botonLogin()
        botonCrear()
        desactivarFlecha()

    }



    fun botonLogin() {
        btnini.setOnClickListener {
            ocultarTeclado()

            if (txtrut.text.toString().trim().isEmpty()
                || txtcon.text.toString().trim().isEmpty()) {

                Toast.makeText(
                    this@Principal,
                    "¡ Complete Los Campos Faltantes !",
                    Toast.LENGTH_LONG
                ).show()

            } else if (!Seguridad.rutValido(Seguridad.limpiarRut(txtrut.text.toString()))) {

                Toast.makeText(
                    this@Principal,
                    "¡ Formato de Rut Incorrecto (11111111-1) !",
                    Toast.LENGTH_LONG
                ).show()

            } else {

                var rut = Seguridad.limpiarRut(txtrut.text.toString())
                var con = txtcon.text.toString()

                btnini.isEnabled = false

                bd.child(rut).addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        btnini.isEnabled = true
                        var usuBD = snapshot.getValue(Usuario::class.java)

                        if (usuBD != null && usuBD.contrasena == Seguridad.cifrar(con)) {

                            intentos = 0

                            var usu = usuBD

                            txtrut.setText("")
                            txtcon.setText("")

                            var intent = Intent(this@Principal, Menu::class.java)
                            intent.putExtra("usu", usu)
                            startActivity(intent)

                        } else {

                            txtrut.setText("")
                            txtcon.setText("")

                            intentoFallido()

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        btnini.isEnabled = true
                        Toast.makeText(
                            this@Principal,
                            "¡ Error de Conexión Con La Base de Datos !",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })

            }

        }
    }



    fun intentoFallido() {
        intentos++

        if (intentos >= maxIntentos) {

            btnini.isEnabled = false

            Toast.makeText(
                this@Principal,
                "¡ Demasiados Intentos ! Espere 30 Segundos",
                Toast.LENGTH_LONG
            ).show()

            btnini.postDelayed({
                intentos = 0
                btnini.isEnabled = true
            }, tiempoBloqueo)

        } else {

            Toast.makeText(
                this@Principal,
                "¡ Error de Rut y/o Contraseña ! Intento " + intentos + " de " + maxIntentos,
                Toast.LENGTH_SHORT
            ).show()

        }
    }



    fun botonCrear() {
        btncrear.setOnClickListener {
            var intent = Intent(this@Principal, CrearCuenta::class.java)
            startActivity(intent)
        }
    }



    fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {

        }
    }



    private fun ocultarTeclado() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}

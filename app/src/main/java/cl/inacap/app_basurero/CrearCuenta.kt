package cl.inacap.app_basurero

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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

class CrearCuenta : AppCompatActivity() {


    private lateinit var txtrut: EditText
    private lateinit var txtnom: EditText
    private lateinit var txtape: EditText
    private lateinit var txtcor: EditText
    private lateinit var spcargo: Spinner
    private lateinit var txtcon: EditText
    private lateinit var txtcon2: EditText
    private lateinit var btnreg: Button
    private lateinit var btnvolcre: Button

    private lateinit var bd: DatabaseReference

    private var cargos = arrayOf("Seleccione Cargo", "Operador", "Supervisor", "Administrador")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_cuenta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtrut = findViewById(R.id.txtrut)
        txtnom = findViewById(R.id.txtnom)
        txtape = findViewById(R.id.txtape)
        txtcor = findViewById(R.id.txtcor)
        spcargo = findViewById(R.id.spcargo)
        txtcon = findViewById(R.id.txtcon)
        txtcon2 = findViewById(R.id.txtcon2)
        btnreg = findViewById(R.id.btnreg)
        btnvolcre = findViewById(R.id.btnvolcre)

        bd = FirebaseDatabase.getInstance().getReference("usuarios")

        cargarCargos()
        registrar()
        volver()
        desactivarFlecha()

    }



    fun cargarCargos() {
        var adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, cargos)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spcargo.adapter = adaptador
    }



    fun registrar() {
        btnreg.setOnClickListener {
            ocultarTeclado()

            var rut = Seguridad.limpiarRut(txtrut.text.toString())
            var nom = txtnom.text.toString().trim()
            var ape = txtape.text.toString().trim()
            var cor = txtcor.text.toString().trim().lowercase()
            var car = spcargo.selectedItem.toString()
            var con = txtcon.text.toString()
            var con2 = txtcon2.text.toString()

            if (rut.isEmpty() || nom.isEmpty() || ape.isEmpty()
                || cor.isEmpty() || con.isEmpty() || con2.isEmpty()) {

                mensaje("¡ Complete Los Campos Faltantes !")

            } else if (!Seguridad.rutValido(rut)) {

                mensaje("¡ Formato de Rut Incorrecto (11111111-1) !")

            } else if (!Patterns.EMAIL_ADDRESS.matcher(cor).matches()) {

                mensaje("¡ Correo Electrónico NO Válido !")

            } else if (spcargo.selectedItemPosition == 0) {

                mensaje("¡ Debe Seleccionar Un Cargo !")

            } else if (con.length < 6) {

                mensaje("¡ La Contraseña Debe Tener Al Menos 6 Caracteres !")

            } else if (con != con2) {

                txtcon.setText("")
                txtcon2.setText("")
                mensaje("¡ Las Contraseñas NO Coinciden !")

            } else {

                var usu = Usuario(rut, nom, ape, cor, car, Seguridad.cifrar(con))

                btnreg.isEnabled = false

                bd.child(rut).addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            btnreg.isEnabled = true
                            mensaje("¡ El Rut Ya Se Encuentra Registrado !")

                        } else {

                            bd.child(rut).setValue(usu)
                                .addOnSuccessListener {
                                    mensaje("¡ Usuario Registrado Correctamente !")
                                    var intent = Intent(this@CrearCuenta, Principal::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    btnreg.isEnabled = true
                                    mensaje("¡ No Se Pudo Registrar El Usuario !")
                                }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        btnreg.isEnabled = true
                        mensaje("¡ Error de Conexión Con La Base de Datos !")
                    }

                })

            }

        }
    }



    fun volver() {
        btnvolcre.setOnClickListener {
            var intent = Intent(this@CrearCuenta, Principal::class.java)
            startActivity(intent)
            finish()
        }
    }



    fun mensaje(texto: String) {
        Toast.makeText(this@CrearCuenta, texto, Toast.LENGTH_SHORT).show()
    }



    private fun desactivarFlecha() {
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

package cl.inacap.app_basurero

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Menu : AppCompatActivity() {


    private lateinit var tvusumenu: TextView
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button

    private var usu: Usuario? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvusumenu = findViewById(R.id.tvusumenu)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)

        usu = intent.getSerializableExtra("usu", Usuario::class.java)

        tvusumenu.setText(usu?.nombre + " " + usu?.apellido)

        boton1()
        boton2()
        boton3()
        boton4()
        boton5()
        desactivarFlecha()

    }



    fun boton1() {
        btn1.setOnClickListener {
            var intent = Intent(this@Menu, ManipularCircuito::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
            finish()
        }
    }



    fun boton2() {
        btn2.setOnClickListener {
            var intent = Intent(this@Menu, Acciones::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
            finish()
        }
    }



    fun boton3() {
        btn3.setOnClickListener {
            var intent = Intent(this@Menu, Estadisticas::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
            finish()
        }
    }



    fun boton4() {
        btn4.setOnClickListener {
            var intent = Intent(this@Menu, Perfil::class.java)
            intent.putExtra("usu", usu)
            startActivity(intent)
            finish()
        }
    }



    fun boton5() {
        btn5.setOnClickListener {
            var a = AlertDialog.Builder(this@Menu)

            a.setTitle("Pregunta")
            a.setMessage("¿Desea Cerrar La Sesión?")

            a.setNegativeButton("Cancelar") { dialog, wich ->
                dialog.dismiss()
            }

            a.setPositiveButton("Aceptar") { dialog, wich ->
                usu = null
                var intent = Intent(this@Menu, Principal::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            a.setIcon(R.drawable.pregunta)

            a.setCancelable(false)
            a.show()
        }
    }



    private fun desactivarFlecha() {
        onBackPressedDispatcher.addCallback(this) {

        }
    }

}

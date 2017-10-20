package ds.bindingtools.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import ds.bindingtools.bind
import ds.bindingtools.startActivity

class MainActivity : AppCompatActivity() {

    private lateinit var textLabel: TextView
    private lateinit var prefs: Prefs
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textLabel = findViewById(R.id.text)
        prefs = Prefs(this)

        bindViews()

        viewModel.sayHello()

        fillPrefs()

        findViewById<Button>(R.id.button).setOnClickListener { navigateNext() }

    }

    private fun bindViews() = with(viewModel) {
        bind(::text, textLabel::setText, textLabel::getText)
    }

    private fun fillPrefs() {
        prefs.age = 12
        prefs.userName = "Ani Lorak"
        prefs.isAdmin = true

        println("the name is ${prefs.userName}")
    }

    private fun navigateNext() {
        startActivity<SecondActivity> {
            SecondActivity::userName to "Ivo Bobul"
            SecondActivity::age to 99
            SecondActivity::code to 65536
        }
    }
}

package ds.bindingtools.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import ds.bindingtools.bundle
import ds.bindingtools.startActivity
import ds.bindingtools.withBindable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val leaksDetector = ByteArray(50_000_000) { 127 }

    private lateinit var prefs: Prefs

    private var viewModel = MainViewModel
    private val extraViewModel = ExtraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = Prefs(this)

        bindViews()

        viewModel.sayHello()

        fillPrefs()

        navigateButton.setOnClickListener { navigateNext() }
        bindButton.setOnClickListener {
            viewModel.onBindClick()
            extraViewModel.message = "toast!"
        }

    }

    override fun onResume() {
        super.onResume()
        withBindable(extraViewModel) {
            bind(::message, {
                it.isNotEmpty() || return@bind
                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                message = ""
            })
        }
    }

    private fun bindViews() = withBindable(viewModel) {
        bind(this::text, helloLabel::setText, helloLabel::getText)
        bind(::buttonText, { it: String -> navigateButton.text = it }, { navigateButton.text.toString() })
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
        val args = bundle {
            SecondActivity::userName to "Slavko Vakarchuk"
            SecondActivity::code to 100500
        }
    }
}

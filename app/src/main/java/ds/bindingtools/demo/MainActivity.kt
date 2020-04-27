package ds.bindingtools.demo

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        nestedButton.setOnClickListener {
            viewModel.assignNested()
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
        bind(::text, helloLabel::setText, helloLabel::getText)
        bind(::buttonText, { navigateButton.text = it }, { navigateButton.text.toString() })

        withBindable(nestedViewModel) {
            bind(::secondaryText, { text -> Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show() })
        }
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

package ds.bindingtools.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ds.bindingtools.arg
import ds.bindingtools.res

class SecondActivity : AppCompatActivity() {

    val userName: String by arg("")
    val age: Int by arg(0)
    val country: String? by arg()
    val code: Int? by arg()

    private val appName by res<String>(R.string.app_name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        println(appName)
        println("$userName $age $country $code")
    }
}

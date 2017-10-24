package ds.bindingtools.demo

import android.os.Handler
import ds.bindingtools.Bindable
import ds.bindingtools.binding

object MainViewModel : Bindable {
    var text: String by binding("")
    var buttonText: String by binding()

    fun sayHello() {
        if (text.isEmpty())
            text = "Hello, World!"
        buttonText = "navigate"
    }

    fun onBindClick() {
        Handler().postDelayed({
            buttonText = "ooops"
        }, 2000)
    }
}
package ds.bindingtools.demo

import android.os.Handler
import ds.bindingtools.Bindable
import ds.bindingtools.binding


object MainViewModel : Bindable {
    var text: String by binding()
    var buttonText: String by binding("...")

    val nestedViewModel = NestedViewModel

    fun sayHello() {
        if (text.isEmpty())
            text = "Hello, World!"
    }

    fun assignNested() {
        nestedViewModel.secondaryText = "this is nested"
    }

    fun onBindClick() {
        Handler().postDelayed({
            buttonText = "navigate"
        }, 2000)
    }

}

object NestedViewModel : Bindable {
    var secondaryText: String by binding("")
}
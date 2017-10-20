package ds.bindingtools.demo

import ds.bindingtools.Bindable
import ds.bindingtools.binding

class MainViewModel : Bindable {
    var text: String by binding("")

    fun sayHello() {
        text = "Hello, World!"
    }
}
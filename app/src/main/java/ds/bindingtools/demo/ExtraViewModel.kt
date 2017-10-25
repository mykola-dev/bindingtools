package ds.bindingtools.demo

import ds.bindingtools.Bindable
import ds.bindingtools.binding

object ExtraViewModel : Bindable {
    var message by binding<String>()
}
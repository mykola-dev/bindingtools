package ds.bindingtools.demo

import ds.bindingtools.Bindable
import ds.bindingtools.binding

class ExtraViewModel : Bindable {
    var imageUrl by binding<String>()
}
/**
 * Experimental databinding tool © 2017 Deviant Studio
 */
package ds.bindingtools

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.os.Looper
import android.util.Log
import android.widget.CompoundButton
import android.widget.TextView
import java.lang.ref.WeakReference
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

inline fun <reified T : Any> binding(initialValue: T): ReadWriteProperty<Bindable, T> = BindingProperty(initialValue, T::class.java)
inline fun <reified T : Any?> binding(): ReadWriteProperty<Bindable, T> = BindingProperty(null, T::class.java)

class BindingProperty<T : Any?>(private var value: T?, private val type: Class<T>) : ReadWriteProperty<Bindable, T> {

    override fun getValue(thisRef: Bindable, property: KProperty<*>): T {
        ensureUiThread()
        val getter = Binder.getAccessors<T>(thisRef, property)?.getter
        if (getter != null) {
            log("${property.name}.get: filling from getter")
            value = getter.invoke()
        }
        val value = value ?: default(type)
        log("get: value [$value]")
        return value
    }

    override fun setValue(thisRef: Bindable, property: KProperty<*>, value: T) {
        ensureUiThread()
        val oldValue = this.value
        if (oldValue != value) {
            log("${property.name}.set: internal value [$value] has been set")
            this.value = value
            Binder.getAccessors<T>(thisRef, property)?.setters?.forEach {
                log("set: value [$value]")
                it(value)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun default(cls: Class<T>): T = when (cls) {
        String::class.java -> "" as T
        CharSequence::class.java -> "" as T
        java.lang.Integer::class.java -> 0 as T
        java.lang.Boolean::class.java -> false as T
        java.lang.Float::class.java -> 0f as T
        java.lang.Double::class.java -> 0.0 as T
        java.lang.Byte::class.java -> 0b0 as T
        java.lang.Short::class.java -> 0 as T
        else -> cls.newInstance()
    }
}

/**
 * Binds [TextView] to the  [CharSequence] field
 */
inline fun <reified T : CharSequence> Bindable.bindCharSequence(prop: KProperty0<T>, view: TextView) =
        bind(prop, view::setText, view::getText)

/**
 * Binds [TextView] to the  [String] field
 */
@Suppress("FINAL_UPPER_BOUND")
inline fun <reified T : String> Bindable.bind(prop: KProperty0<T>, view: TextView) =
        bind(prop, view::setText, { view.text.toString() as T })

/**
 * Binds [CompoundButton] to the  [Boolean] field
 */
@Suppress("FINAL_UPPER_BOUND")
inline fun <reified T : Boolean> Bindable.bind(prop: KProperty0<T>, view: CompoundButton) =
        bind(prop, view::setChecked, view::isChecked)

/**
 * Binds [setter]/[getter] pair to the [prop]
 */
fun <T : Any?> Bindable.bind(prop: KProperty0<T>, setter: (T) -> Unit, getter: (() -> T)? = null) {
    Binder.getOrPutAccessors<T>(this, prop).let {
        log("bind ${prop.name}")
        log("set default for ${prop.name}: ${prop.get()}")
        setter(prop.get())  // initialize view

        it.setters += setter
        if (getter != null)
            if (it.getter == null)
                it.getter = getter
            else
                error("Only one getter per property allowed")

    }
}


/**
 * Binds any property to any property
 */
fun <T> Bindable.bind(prop: KProperty0<T>, mutableProp: KMutableProperty0<T>) =
        bind(prop, { mutableProp.set(it) }, { mutableProp.get() })

fun Bindable.unbind() {
    Binder.remove(this)
}

fun Bindable.debugBindings() {
    Binder[this]?.properties?.forEach { e ->
        log("bindings for ${e.value.name}: id=${e.key} getter=${e.value.getter} setters=${e.value.setters}")
    }
}

private val isUiThread: Boolean get() = Thread.currentThread() === Looper.getMainLooper().thread
private fun ensureUiThread() = isUiThread || error("UI thread expected")

private class Accessors<T : Any?, R : Any?>(val name: String) {
    var getter: (() -> R)? = null
    val setters = mutableListOf<(T) -> Unit>()
}

private class Binding(
        val view: WeakReference<Any>,
        val properties: MutableMap<String, Accessors<*, *>> = mutableMapOf()
)

fun <T : Bindable> Any.withBindable(bindable: T, block: T.() -> Unit) {
    val binding = Binder[bindable]
    if (binding != null && binding.view.get() == this) {
        log("Already binded to this view. rebinding...")
    }
    Binder[bindable] = Binding(WeakReference(this))
    block(bindable)
}

private object Binder {
    private val bindings = WeakHashMap<Bindable, Binding>()

    operator fun get(bindable: Bindable): Binding? = bindings[bindable]
    operator fun set(bindable: Bindable, data: Binding) = bindings.put(bindable, data)
    fun remove(bindable: Bindable) = bindings.remove(bindable)

    @Suppress("UNCHECKED_CAST")
    fun <T> getAccessors(bindable: Bindable, prop: KProperty<*>): Accessors<T, T>? {
        val binding = bindings[bindable]
        return if (binding != null) {
            val v = binding.view.get()
            if (v != null) {
                if (v is LifecycleOwner && v.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                    log("view lifecycle [${v.javaClass.simpleName}.${v.lifecycle.currentState}] state isn't appropriate for binding")
                    return null
                }
                binding.properties[prop.name] as Accessors<T, T>?
            } else {
                log("view is null. skip binding")
                remove(bindable)    // no need to store such binding
                null
            }
        } else {
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getOrPutAccessors(bindable: Bindable, prop: KProperty0<*>): Accessors<T, T> =
            bindings[bindable]!!
                    .properties
                    .getOrPut(prop.name) { Accessors<T, T>(prop.name) }
                    as Accessors<T, T>

}

private fun log(message: String) {
    /*if (BuildConfig.DEBUG) */Log.v("DATABINDING", message)
}

interface Bindable
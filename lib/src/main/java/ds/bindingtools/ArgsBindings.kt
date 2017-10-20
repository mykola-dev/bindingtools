package ds.bindingtools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import java.io.Serializable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Nullable version
 */
inline fun <reified T : Any?> Activity.arg(): ReadOnlyProperty<Activity, T?> = ActivityArgsDelegate(null, T::class)

inline fun <reified T : Any?> Activity.arg(default: T): ReadOnlyProperty<Activity, T> = ActivityArgsDelegate(default, T::class)

/**
 * Nullable version
 */
inline fun <reified T : Any?> Fragment.arg(): ReadOnlyProperty<Fragment, T?> = FragmentArgsDelegate(null, T::class)

inline fun <reified T : Any?> Fragment.arg(default: T): ReadOnlyProperty<Fragment, T> = FragmentArgsDelegate(default, T::class)

inline fun <reified T : Activity> Context.startActivity(b: Bundle? = null, flags: Int = 0) {
    val i = Intent(this, T::class.java).addFlags(flags)
    if (b != null)
        i.putExtras(b)
    startActivity(i)
}

inline fun <reified T : Activity> Activity.startActivityForResult(b: Bundle? = null, requestCode: Int, flags: Int = 0) {
    val i = Intent(this, T::class.java).addFlags(flags)
    if (b != null)
        i.putExtras(b)
    startActivityForResult(i, requestCode)
}

inline fun <reified T : Activity> Context.startActivity(flags: Int = 0, noinline f: BundleBuilder.() -> Unit) {
    val b = bundle(f)
    startActivity<T>(b, flags)
}

inline fun <reified T : Activity> Activity.startActivityForResult(requestCode: Int, flags: Int = 0, noinline f: BundleBuilder.() -> Unit) {
    val b = bundle(f)
    startActivityForResult<T>(b, requestCode, flags)
}

inline fun <reified T : Fragment> FragmentActivity.replaceFragment(@IdRes layoutId: Int, args: Bundle? = null) {
    val fragment = Fragment.instantiate(this, T::class.java.name, args)
    supportFragmentManager
            .beginTransaction()
            .replace(layoutId, fragment)
            .commitNow()
}

inline fun <reified T : Fragment> FragmentActivity.replaceFragment(@IdRes layoutId: Int, f: BundleBuilder.(T) -> Unit) {
    val fragment = Fragment.instantiate(this, T::class.java.name) as T
    val builder = BundleBuilder()
    f(builder, fragment)
    fragment.arguments = builder.build()

    supportFragmentManager
            .beginTransaction()
            .replace(layoutId, fragment)
            .commitNow()
}

@Suppress("unchecked_cast")
class ActivityArgsDelegate<out T : Any?>(private val default: T, private val cls: KClass<*>) : ReadOnlyProperty<Activity, T> {

    override fun getValue(thisRef: Activity, property: KProperty<*>): T {
        if (thisRef.intent?.extras == null)
            return default

        return parseExtras(property.name, thisRef.intent.extras, cls, default)
    }
}

class FragmentArgsDelegate<out T : Any?>(private val default: T, private val cls: KClass<*>) : ReadOnlyProperty<Fragment, T> {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (thisRef.arguments == null)
            return default

        return parseExtras(property.name, thisRef.arguments, cls, default)
    }
}

@Suppress("unchecked_cast")
private fun <T : Any?> parseExtras(key: String, extras: Bundle, cls: KClass<*>, default: T): T = with(extras) {
    when (cls) {
        String::class -> getString(key, default as String?) as T
        java.lang.Integer::class -> (if (default == null) getInt(key) else getInt(key, default as Int)) as T
        java.lang.Boolean::class -> (if (default == null) getBoolean(key) else getBoolean(key, default as Boolean)) as T
        java.lang.Float::class -> (if (default == null) getFloat(key) else getFloat(key, default as Float)) as T
        java.lang.Long::class -> (if (default == null) getLong(key) else getLong(key, default as Long)) as T
        java.lang.Double::class -> (if (default == null) getDouble(key) else getDouble(key, default as Double)) as T
        CharSequence::class -> (if (default == null) getCharSequence(key) else getCharSequence(key, default as CharSequence)) as T
        Char::class -> (if (default == null) getChar(key) else getChar(key, default as Char)) as T
        IntArray::class -> getIntArray(key) as T
        BooleanArray::class -> getBooleanArray(key) as T
        FloatArray::class -> getFloatArray(key) as T
        LongArray::class -> getLongArray(key) as T
        DoubleArray::class -> getDoubleArray(key) as T
        Parcelable::class -> getParcelable<Parcelable>(key) as T
        Serializable::class -> getSerializable(key) as T
        ArrayList::class -> getStringArrayList(key) as T
        Array<String>::class -> getStringArray(key) as T
        else -> error("Class ${cls.javaObjectType.simpleName} doesn't supported")
    }
}

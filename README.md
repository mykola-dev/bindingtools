# bindingtools
[![JitPack](https://img.shields.io/jitpack/v/deviant-studio/bindingtools.svg?style=flat-square)]()

Lightweight helper library for Android Kotlin development
- Shared Preferences delegates
- Bundle args delegates
- Resources delegates
- View<->Data Binding

## Quick Setup
Step 1. Add the JitPack repository to your build file
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency
```
dependencies {
    compile 'com.github.deviant-studio:bindingtools:{latest_version}'
}
```

## Documentation

### Data Binding
Let's say you have the Activity:
```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var textLabel: TextView
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textLabel = findViewById(R.id.text)
        bindViews()
    }

    private fun bindViews() {
        // ...
    }
}
```
and the ViewModel:
```kotlin
class MainViewModel {
    var text: String = ""

    fun sayHello() {
        text = "Hello, World!"
    }
}
```
It would be nice if we can bind `text` field to the view. Let's modify ViewModel:
```kotlin
class MainViewModel : Bindable {
    var text: String by binding("")

    fun sayHello() {
        text = "Hello, World!"
    }
}
```
and Activity:
```kotlin
private fun bindViews() = withBindable(viewModel) {
    bind(::text, textLabel::setText, textLabel::getText)
}
```
Thats it! Now we can set TextView's text like:
```kotlin
viewModel.sayHello()
```
Also library allows you to simplify `TextView`/`EditText` bindings to this:
```kotlin
withBindable(viewModel) {
    bind(::text, textLabel)
}
```
#### Comparison with Google Databinding library
\+ don't have to write BindingAdapters<br/> 
\+ no code generation (no more missed `BR.id`, don't have to force `clean` in any unclear situation)<br/> 
\+ great code completion (compared to XML)<br/> 
\+ clean XML files<br/> 
\+ can bind anything to anything, not only the View<br/> 
\+ can be used with Anko (and any programmatically created views)<br/> 
\+ much cleaner way to implement 2-way bindings<br/> 

\- some reflection<br/> 
\- not from Google<br/> 
\- only for Kotlin apps<br/> 
\- no observables (yet?)<br/> 

### Shared Preferences binding
It's so annoying to deal with SharedPreferences directly:
```java
final String ageKey = "age";
final String userNameKey = "userName";
final String adminKey = "admin";
SharedPreferences prefs = getSharedPreferences("main_prefs", Context.MODE_PRIVATE);
SharedPreferences.Editor editor = prefs.edit();
editor.putInt(ageKey, 12);
editor.putString(userNameKey, "Luke");
editor.putBoolean(adminKey,true);
editor.apply();
```
Fortunately now we have `Kotlin` and the `bindingtools`!
First, declare the `PreferencesAware` class
```kotlin
class Prefs(ctx: Context) : PreferencesAware {

    override val forcePersistDefaults = true
    override val sharedPreferences: SharedPreferences = ctx.getSharedPreferences("main_prefs", Context.MODE_PRIVATE)

    var age by pref(0)
    var userName by pref("")
    var isAdmin by pref(false)

}
```
Now you can use preferences like this:
```kotlin
val prefs = Prefs(this)
prefs.age = 12
prefs.userName = "Ani Lorak"
prefs.isAdmin = true

println("the name is ${prefs.userName}")
```

### Bundle arguments binding
Dealing with args bundle has never been such simple before. Let's declare another one activity:
```kotlin
class SecondActivity : AppCompatActivity() {

    val userName: String by arg("")
    val age: String by arg("")
    val country: String? by arg()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        println("$userName $age $country")  // that's it. just read the properties
    }
}

```
Start activity with convenient bundle builder:
```kotlin
startActivity<SecondActivity> {
    SecondActivity::userName to "Ivo Bobul"
    SecondActivity::age to 99
    SecondActivity::code to 65536
}
```
or build the bundle separately:
```kotlin
val args = bundle {
    SecondActivity::userName to "Slavko Vakarchuk"
    SecondActivity::code to 100500
}
```

### Resources binding
Same rules can be used when using resources:
 ```kotlin
private val appName: String by res(R.string.app_name)
...
println(appName)
 ```

### Projects using this lib
- https://github.com/deviant-studio/energy-meter-scanner

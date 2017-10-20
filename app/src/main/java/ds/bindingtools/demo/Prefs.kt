/*
 * Copyright (c) 2016. Deviant Studio
 */

package ds.bindingtools.demo

import android.content.Context
import android.content.SharedPreferences
import ds.bindingtools.PreferencesAware
import ds.bindingtools.pref

class Prefs(ctx: Context) : PreferencesAware {

    override val forcePersistDefaults = true
    override val sharedPreferences: SharedPreferences = ctx.getSharedPreferences("main_prefs", Context.MODE_PRIVATE)

    var age: Int by pref(0)
    var userName: String by pref("")
    var isAdmin by pref(true)

}
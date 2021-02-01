package com.codespair

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codespair.ui.main.MainFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Timber config
        if(BuildConfig.DEBUG){  Timber.plant(Timber.DebugTree()) }
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}
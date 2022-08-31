package com.wavecat.kotovstore

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.color.DynamicColors
import com.wavecat.kotovstore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.AppsFragment, R.id.AboutFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.included.navView.setupWithNavController(navController)

        binding.included.navView.setOnItemSelectedListener {
            val builder = NavOptions.Builder()
                .setEnterAnim(androidx.appcompat.R.anim.abc_fade_in)
                .setExitAnim(androidx.appcompat.R.anim.abc_fade_out)

            navController.popBackStack()
            navController.navigate(getMenu(it.itemId), null, builder.build())
            true
        }
    }

    private fun getMenu(id: Int) =
        when (id) {
            R.id.apps -> R.id.AppsFragment
            R.id.about -> R.id.AboutFragment
            else -> R.id.AppsFragment
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_licenses -> {
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
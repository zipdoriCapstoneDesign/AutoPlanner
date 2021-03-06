package com.zipdori.autoplanner

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.zipdori.autoplanner.databinding.ActivityMainBinding
import com.zipdori.autoplanner.modules.common.PermissionModule
import com.zipdori.autoplanner.modules.calendarprovider.CalendarProviderModule
import com.zipdori.autoplanner.modules.calendarprovider.CalendarsVO

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AutoPlanner_NoActionBar)

        super.onCreate(savedInstanceState)

        if (checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            init()
        } else {
            val NEED_PERMISSIONS = arrayOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
            )
            val NEED_PERMISSIONS_FLAGS = arrayOf(
                Consts.FLAG_PERM_CALENDAR,
                Consts.FLAG_PERM_CALENDAR
            )

            PermissionModule.requestPermissionsIfNotExists(NEED_PERMISSIONS, NEED_PERMISSIONS_FLAGS, this)
        }

        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        auth.signInAnonymously()
    }

    override fun onBackPressed() {
        // BackPressed ??? NavigationDrawer ??? ??????????????? ??????
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    // TODO: 2022-03-17 Delete 
    /*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    
     */

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Consts.FLAG_PERM_CALENDAR -> {
                if (!grantResults.contains(PackageManager.PERMISSION_DENIED)) {
                    init()
                } else {
                    Toast.makeText(this, "????????? ????????? ?????????????????? ?????? ????????? ??? ????????????. ??? ???????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                    finishAffinity()
                    System.runFinalization()
                    System.exit(0)
                }
            }
        }
    }

    private fun init() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = binding.drawerLayout

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_calendar, R.id.nav_trash_can
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        createCalendarIfNotExist()
        setCalendarIndex()
    }

    private fun createCalendarIfNotExist() {
        if (!isCalendarExist()) {
            val calendarProviderModule: CalendarProviderModule = CalendarProviderModule(applicationContext)

            calendarProviderModule.insertCalendar(
                "AutoPlanner",
                "AutoPlanner",
                1,
                "AutoPlanner",
                CalendarContract.ACCOUNT_TYPE_LOCAL,
                -10572033,
                700,
                "AutoPlanner",
                "UTC"
            )
        }
    }

    private fun isCalendarExist(): Boolean {
        val calendarProviderModule: CalendarProviderModule = CalendarProviderModule(applicationContext)
        val allCalendars: ArrayList<CalendarsVO> = calendarProviderModule.selectAllCalendars()
        allCalendars.forEach {
            if (it.name.equals("AutoPlanner") && it.accountName.equals("AutoPlanner") && it.accountType.equals("LOCAL") && it.ownerAccount.equals("AutoPlanner"))
                return true
        }

        return false
    }

    private fun setCalendarIndex() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)


        val calendarProviderModule: CalendarProviderModule = CalendarProviderModule(applicationContext)
        val allCalendars: ArrayList<CalendarsVO> = calendarProviderModule.selectAllCalendars()
        allCalendars.forEach {
            if (it.name.equals("AutoPlanner") && it.accountName.equals("AutoPlanner") && it.accountType.equals("LOCAL") && it.ownerAccount.equals("AutoPlanner")) {
                sharedPreferences.edit().putLong(getString(R.string.calendar_index), it.id).apply()
                return
            }
        }
    }
}
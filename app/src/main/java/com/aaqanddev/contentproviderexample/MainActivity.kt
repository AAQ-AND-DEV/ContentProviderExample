package com.aaqanddev.contentproviderexample

import android.Manifest.permission.READ_CONTACTS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val TAG = "MainActivity"
private const val REQUEST_CODE_READ_CONTACTS = 1

class MainActivity : AppCompatActivity() {

    //private var readGranted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val hasReadContactPermission = ContextCompat
            .checkSelfPermission(this, READ_CONTACTS)
        Log.d(TAG, "onCreate: checkselfPermission returned $hasReadContactPermission")

        if (hasReadContactPermission==PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onCreate: permission granted")
            //readGranted = true //TODO don't do this
        }  else{
            Log.d(TAG, "onCreate: requesting permission")
            ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)
        }

        fab.setOnClickListener { view ->
            Log.d(TAG, "fab onClick: starts")
            if (ContextCompat.checkSelfPermission(this, READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){

            val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

            val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

            val contacts = ArrayList<String>()
            cursor?.use{
                while(it.moveToNext()){
                    contacts.add(it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                }
            }
            val adapter = ArrayAdapter<String>(this,R.layout.contact_detail, R.id.name, contacts)
            content_names.adapter = adapter
            } else{
                Snackbar.make(view, "Permission for contacts must be granted for app to function",
                    Snackbar.LENGTH_INDEFINITE).setAction("Grant Access") {
                    Log.d(TAG, "SnackBar onClick: starts")
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_CONTACTS)){
                        Log.d(TAG, "Snackbar onClick: calling requestPermissions")
                        ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), REQUEST_CODE_READ_CONTACTS)
                    } else{
                        //the user has permanently denied permission, take to app settings
                        Log.d(TAG, "snackbar onClick: launching settings")
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", this.packageName, null)
                        Log.d(TAG, "SnackBar onClick: Uri is  $uri")
                        intent.data = uri
                        this.startActivity(intent)
                    }
                    Log.d(TAG, "snackbar onclick: ends")
                }.show()
            }

            Log.d(TAG, "fab onClick: ends")
        }
        Log.d(TAG, "onCreate: ends")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult: starts")
        when (requestCode){
            REQUEST_CODE_READ_CONTACTS -> {
                //readGranted = if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted, do contacts-related task
                    Log.d(TAG, "onRequestPermissionResult: permission granted")

                } else{
                    //permission denied. disable functionality dependent on permission
                    Log.d(TAG, "onRequestPermissionsResult: permission refused")

                }
                //fab.isEnabled = readGranted
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: ends")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

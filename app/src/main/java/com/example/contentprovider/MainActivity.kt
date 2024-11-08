package com.example.contentprovider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contentprovider.databinding.ActivityMainBinding
import com.example.contentprovider.model.Contact


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ContactsAdapter
    private val contactsList = mutableListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContactsAdapter(contactsList) { contact, action ->
            when (action) {
                ContactsAdapter.Action.CALL -> makeCall(contact.phoneNumber)
                ContactsAdapter.Action.MESSAGE -> sendMessage(contact.phoneNumber)
            }
        }
        binding.recyclerView.adapter = adapter

        if (!PermissionsHelper.hasPermission(this, Manifest.permission.READ_CONTACTS)) {
            PermissionsHelper.requestPermission(this, Manifest.permission.READ_CONTACTS, 1)
        } else {
            loadContacts()
        }
    }

    @SuppressLint("Range", "NotifyDataSetChanged")
    private fun loadContacts() {
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsList.add(Contact(name, phoneNumber))
            }
            cursor.close()
            adapter.notifyDataSetChanged()
        }
    }

    private fun makeCall(phoneNumber: String) {
        if (!PermissionsHelper.hasPermission(this, Manifest.permission.CALL_PHONE)) {
            PermissionsHelper.requestPermission(this, Manifest.permission.CALL_PHONE, 2)
        } else {
            val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            startActivity(callIntent)
        }
    }

    private fun sendMessage(phoneNumber: String) {
        if (!PermissionsHelper.hasPermission(this, Manifest.permission.SEND_SMS)) {
            PermissionsHelper.requestPermission(this, Manifest.permission.SEND_SMS, 3)
        } else {
            val messageIntent = Intent(this, SendMessageActivity::class.java).apply {
                putExtra("PHONE_NUMBER", phoneNumber)
            }
            startActivity(messageIntent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                Toast.makeText(this, "Разрешение на чтение контактов отклонено", Toast.LENGTH_SHORT)
                    .show()
            }

            2 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall(contactsList[0].phoneNumber)
            } else {
                Toast.makeText(
                    this,
                    "Разрешение на совершение звонков отклонено",
                    Toast.LENGTH_SHORT
                ).show()
            }

            3 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendMessage(contactsList[0].phoneNumber)
            } else {
                Toast.makeText(
                    this,
                    "Разрешение на отправку сообщений отклонено",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_exit -> {
                finishAffinity()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

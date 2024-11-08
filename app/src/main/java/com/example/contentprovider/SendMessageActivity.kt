package com.example.contentprovider

import com.example.contentprovider.databinding.ActivitySendMessageBinding
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SendMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val phoneNumber = intent.getStringExtra("PHONE_NUMBER")
        binding.phoneNumberText.text = phoneNumber

        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString()
            if (phoneNumber != null && message.isNotEmpty()) {
                sendSms(phoneNumber, message)
                binding.messageInput.text.clear()
            } else {
                Toast.makeText(this, "Пожалуйста, введите все данные.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendSms(phoneNumber: String, message: String) {
        try {
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.getSystemService(SmsManager::class.java)
            } else {
                SmsManager.getDefault()
            }

            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(applicationContext, "Сообщение отправлено", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "Ошибка отправки сообщения: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
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

package com.mirkamol.encryption.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import com.mirkamol.encryption.R
import com.mirkamol.encryption.model.Card
import com.mirkamol.encryption.model.User
import com.mirkamol.encryption.network.RetrofitHttp
import com.mirkamol.encryption.utils.Asymmetric
import com.mirkamol.encryption.utils.Asymmetric.Companion.decryptMessage
import com.mirkamol.encryption.utils.Asymmetric.Companion.encryptMessage
import com.mirkamol.encryption.utils.Symmetric.decrypt
import com.mirkamol.encryption.utils.Symmetric.encrypt
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    val privateServerKey = "MIIEoAIBADANBgkqhkiG9w0BAQEFAASCBIowggSGAgEAAoH8Nx/GJcFfILU7+Z8+zZ0YwIwTcVGjlopixDJkKdyYdTWyV+TPClNU00LPmOnOnv850YwRFHLd+LYL7bejdEKcOEgDlMhjEYfaUaPU6lK6lgwv7wg7GDPsW2IqSdCVoyBHBfBYmk1U5UU5kHrRi5hKX9hpZ73RQvGIvxsdEDCeF6cLPy7mDjN1VIaeJmrv9lbBM+8PplkarRgZv38FW4lzTkUFEBk7Go6YEZZ+v6peWAgXAErCaora6bTK3juZvqM8aErj2yjWG5pMj0itMsDbHpGqmKYoAuO+8hWFOnscLAtc1HQWpD5h1r5ZvCDwcyu4cqzgdNLaMotESeZVAgMBAAECgfwODTlzcmGPcuOA9EkMHNmsa4ihQFBVFZ2rqbTCvwrWBgQaR6dHWIqs98D3l9Il0aWpduz7q3RN7AoKZN9zyW8NIb5X9OTcGSdb3ElwGfqaOS0W36BAwSuk99cTzen3FUlFiEjtvHzBnO3ZmJVF4mJDIGVuHoLzb6KOJJk6AUpu53U9Nsvg2kkGCrDrKrUkkTx19KU0YoADloyoyP9xO/zPBJRlN7cJGt4Ajjg+lDQyO6QuFqwR3yWPMgRd1mezdMS3WAWdPKvGuD2ZeCS89YUlTgrtLdtXhlizId+TGEWZoHxOqRTHzg/O1t0lwAbmwGDwBR23PL3fuNpxAIECfnhUKf+kvJ57DjupzA1cbs6T0qhJ0ZTZeIaDDYloJ6EeLCPDKKYbH19E3wmsJj5gtSUprCrP2JoQZt7fUUFwBR6q/H32m4QVzDKfuWF5cuM9BAbdFW/PUJkRU6m3J6ZMhZBGsgkfzG+iB85wE5EwOocX7oPw5D2pp3CruQCWEQJ+dUbdIZDTiU8B3OOzouKNDscdYzRJurV5qe3z6volUU5oQAgDzKVRccQFSopAxYCpYDrYKIWy6oKdQhkfBStckonyMO4ehHWOMdvUleSWBa5JHrScGQsuAKTXsQWJqn7vlIi4qPuK81F8QIiwUmV/uaRrgD+m0113VvaG/3gFAn5PsQ4PrX7uRvlqMk5eGvWYAvblVd5kApN8Ipd4hW6Zmm4JUVs+h4ADjI1azpSVg171OeA4imcdwfcfbC9Yc2Qwp3WJxyXAGN+gN4CDOfgI34QnN4zW/CqY9Yy/PfoYfV2H7ApFWLYAyQL6ieerpJFen07sO0IuRIoMaO6hoMECfhuWJ74ViyzpYM7M4RyXngaz373ONumsdon6Zz4GyXBuuScWu44P9TCFo5j9HG9Y7H6uWNpvWL7BKqy5rApTQNtXh5jq6lLONDyWMVNxCbkcXibS5UUe1BtiqzoAy3lHkqOl5YoaYX0K1ed4P8GiDoFko8TvNBbUDZZvNyIw4QJ+YOoqxQPsluOZNxdY4GmZKEQCAwec+dcESq3G1CYb5P+ebmg5zjau+GtLHrlkVL8xQuV2NNkUGS6cfwOkOUcu3p6I1Qdyq4P80tfe1HNARwyPhQ4I846Z8hprbxX2ku8sZRWUGghSaA0UxQn9QOToCyST0BJdCihsmiHZT1kO"
    val publicMobileKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC7SrR7pvsp1rMDVn4R9XjZp4q6IVSit9XAKvAMgTIml7vAp2H81RA9V4jDyrFlASrvxq0uGi5W/bZj+AdYGu1ceTDeLK8OvH90GyKXaPiHjuPAeRCGyTBkn+7nogVxqpmrGVGUJfcslr4JawU/9Vou7LjdraNStCnldRJzKvcY+wIDAQAB"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        val b_save_user = findViewById<Button>(R.id.b_save_user)
        val b_load_user = findViewById<Button>(R.id.b_load_user)
        val b_save_card = findViewById<Button>(R.id.b_save_card)
        val b_load_card = findViewById<Button>(R.id.b_load_card)
        b_save_user.setOnClickListener {
            val user = User("Firdavs", "+998909998899")
            Log.d("Encrypt", user.toString())
            user.fullName = encrypt(user.fullName)!!
            user.phoneNumber = encrypt(user.phoneNumber)!!
            Log.d("Encrypt", user.toString())
            apiSaveUser(user)
        }
        b_load_user.setOnClickListener {
            apiLoadUser()
        }

        b_save_card.setOnClickListener {
            val card = Card("Kurbanov Begzodbek","8678761526789012")
            Log.d("encryptMessage",card.toString())
            card.cardNumber = encryptMessage(card.cardNumber, publicMobileKey)
            Log.d("encryptMessage",card.toString())
            apiSaveCard(card)
        }
        b_load_card.setOnClickListener {
            apiLoadCard()
        }

    }


    private fun apiSaveUser(user: User) {
        RetrofitHttp.userService.createUser(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.d("Encrypt",  response.toString())
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("Encrypt", t.toString())
            }
        })
    }

    private fun apiLoadUser() {
        RetrofitHttp.userService.getUserById(1).enqueue(object : Callback<User> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user = response.body()
                Log.d("Encrypt", decrypt(user!!.fullName).toString())
                Log.d("Encrypt", decrypt(user.phoneNumber).toString())
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("TAG", t.toString())
            }
        })
    }


    private fun apiSaveCard(card: Card) {
        RetrofitHttp.cardService.createCard(card).enqueue(object : Callback<Card> {
            override fun onResponse(call: Call<Card>, response: Response<Card>) {
                Log.d("TAG", response.toString())
            }

            override fun onFailure(call: Call<Card>, t: Throwable) {
                Log.d("TAG", t.toString())
            }
        })
    }

    private fun apiLoadCard() {
        RetrofitHttp.cardService.getCardById(1).enqueue(object : Callback<Card> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<Card>, response: Response<Card>) {
                val card = response.body()
                val cardNumber = decryptMessage(card!!.cardNumber, privateServerKey)
                Log.d("TAG", cardNumber)
            }

            override fun onFailure(call: Call<Card>, t: Throwable) {
                Log.d("TAG", t.toString())
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun testSymmetric() {
        // secret text
        val originalString = "PDP Academy"
        // Encryption
        val encryptedString = encrypt(originalString)
        // Decryption
        val decryptedString = decrypt(encryptedString)
        // Printing originalString,encryptedString,decryptedString
        Log.d("@@@", "Original String:$originalString")
        Log.d("@@@", "Encrypted value:$encryptedString")
        Log.d("@@@", "Decrypted value:$decryptedString")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testAsymmetric() {
        val secretText = "Omad yordur dovyuraklarga!!!"
        val keyPairGenerator = Asymmetric()
        // Generate private and public key
        val privateKey: String =
            Base64.getEncoder().encodeToString(keyPairGenerator.privateKey.encoded)
        val publicKey: String =
            Base64.getEncoder().encodeToString(keyPairGenerator.publicKey.encoded)
        Log.d("@@@", "Private Key: $privateKey")
        Log.d("@@@", "Public Key: $publicKey")
        // Encrypt secret text using public key
        val encryptedValue = encryptMessage(secretText, publicKey)
        Log.d("@@@", "Encrypted Value: $encryptedValue")
        // Decrypt
        val decryptedText = decryptMessage(encryptedValue, privateKey)
        Log.d("@@@", "Decrypted output: $decryptedText")
    }


}
package com.mspw.staythefuckathome.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import bolts.Task
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.internal.WebDialog
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.mspw.staythefuckathome.MainActivity
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.SharedPreferencesUtil
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        facebookLoginButton.setOnClickListener {
            facebookLogin()
        }

        val token = SharedPreferencesUtil(this).getToken()
        if (token != ""){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, listOf("public_profile", "email"))
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult?) {
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {
                    Toast.makeText(this@LoginActivity, "Sign in canceled", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(this@LoginActivity, "Sign in failed", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken?) {
        if (token != null) {
            val credential = FacebookAuthProvider.getCredential(token.token)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        //서버에 정보 던지기
                        user?.getIdToken(true)
                            ?.addOnCompleteListener {
                                it.result?.token?.let { token ->
                                    SharedPreferencesUtil(this@LoginActivity).setToken(token)
                                    sendToken(token)
                                }
                            }

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this@LoginActivity, "Sign in failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun sendToken(token: String) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        currentUser?.run {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

//    fun signOut() {
//        auth.signOut()
//        LoginManager.getInstance().logOut()
//        updateUI(null)
//    }

}

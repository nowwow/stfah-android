package com.mspw.staythefuckathome.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.mspw.staythefuckathome.*
import com.mspw.staythefuckathome.R
import com.mspw.staythefuckathome.data.ListResponse
import com.mspw.staythefuckathome.data.user.User
import com.mspw.staythefuckathome.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var appContainer: AppContainer
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        appContainer = (application as BaseApplication).appContainer
        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        facebookLoginButton.setOnClickListener {
            facebookLogin()
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
                        user?.getIdToken(true)
                            ?.addOnCompleteListener {
                                it.result?.token?.let { firebaseToken ->
                                    SharedPreferencesUtil(this@LoginActivity).setToken(firebaseToken)
                                    requestMe(token, firebaseToken)
                                }
                            }

                    } else {
                        Toast.makeText(this@LoginActivity, "Sign in failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        currentUser?.run {
            loading.visibility = View.VISIBLE
            this.getIdToken(true).addOnCompleteListener {
                it.result?.token?.let { firebaseToken ->
                    SharedPreferencesUtil(this@LoginActivity).setToken(firebaseToken)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }
    }

    private fun requestMe(token: AccessToken?, firebaseToken: String) {
        val graphRequest = GraphRequest.newMeRequest(token) { obj, response ->
            Log.e("result", obj.toString())
            val userId = obj.get("id").toString()
            val userName = obj.get("name").toString()
            val userProfile = "https://graph.facebook.com/${obj.get("id")}/picture?type=large"

            appContainer.userRepository.getUserExist(userId).enqueue(object :
                Callback<ListResponse<User>> {
                override fun onFailure(call: Call<ListResponse<User>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<ListResponse<User>>,
                    response: Response<ListResponse<User>>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.results?.isEmpty() == true) {
                            registerUser("$firebaseToken", userName, userProfile)
                            Log.e("result size", (response.body()?.results?.size ?: 0).toString())
                        } else {
                            Log.e("result size", (response.body()?.results?.size ?: 0).toString())
                            SharedPreferencesUtil(this@LoginActivity).setToken(firebaseToken)
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Network Error ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("Register network error", response.message())
                    }
                }

            })
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()
    }

    private fun registerUser(firebaseToken: String, userName: String, userProfile: String) {
        appContainer.userRepository.registerUser("Bearer $firebaseToken", userName, userProfile)
            .enqueue(object : Callback<Any> {
                override fun onFailure(call: Call<Any>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) {
                        SharedPreferencesUtil(this@LoginActivity).setToken(firebaseToken)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        Log.e("asd", response.code().toString())
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Sign up fail", Toast.LENGTH_SHORT)
                            .show()
                        Log.e("ddd", response.code().toString())
                    }
                }

            })
    }

    fun signOut() {
        auth.signOut()
        LoginManager.getInstance().logOut()
    }

}

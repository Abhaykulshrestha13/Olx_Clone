 package com.eample.olxapp.ui.login

 import com.example.olxapp.MainActivity


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.example.olxapp.R
import com.example.olxapp.baseActivity
import com.example.olxapp.utilites.Constants
import com.example.olxapp.utilites.SharedPref
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class LoginActivity : baseActivity() {
    private var callbackManager: CallbackManager? = null
    lateinit var google_login:LinearLayout
    lateinit var fb_login:LinearLayout
    lateinit var mAuth:FirebaseAuth
    var RC_SIGN_IN = 100
    lateinit var login_button:LoginButton
    private val EMAIL = "email"
    var TAG = LoginActivity::class.java.simpleName
    var googleSignInOption:GoogleSignInOptions? = null
    var googleSignInClient:GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        FacebookSdk.sdkInitialize(getApplicationContext())


        login_button = findViewById(R.id.login_button)
        login_button.setReadPermissions(Arrays.asList(EMAIL));
        callbackManager = CallbackManager.Factory.create();


        mAuth = FirebaseAuth.getInstance();
        
        google_login = findViewById(R.id.google_login)
        fb_login = findViewById(R.id.fb_login)
        googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOption!!)


        google_login.setOnClickListener {
            signIn()
        }

        fb_login.setOnClickListener {
            login_button.performClick()
        }
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    // App code
                    handleFacebookAccess(loginResult?.accessToken)
                }

                override fun onCancel() {
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    // App code
                }
            })



    }

    private fun handleFacebookAccess(accessToken: AccessToken?) {
        var credential = FacebookAuthProvider.getCredential(accessToken?.token!!)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val idToken = mAuth.currentUser
                    if(idToken?.email !=null){
                        SharedPref(this).setString(Constants.USER_EMAIL,idToken.email!!)
                    }
                    if(idToken?.uid !=null){
                        SharedPref(this).setString(Constants.USER_ID,idToken.uid!!)
                    }
                    if(idToken?.displayName !=null){
                        SharedPref(this).setString(Constants.USER_NAME,idToken.displayName!!)
                    }
                    if(idToken?.photoUrl !=null){
                        SharedPref(this).setString(Constants.USER_PHOTO,idToken.photoUrl.toString()!!)
                    }
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                var account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }else{
            callbackManager?.onActivityResult(requestCode, resultCode, data);
        }
    }

    private fun firebaseAuthWithGoogle(idToken: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(idToken.idToken,null)
        mAuth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                if(idToken.email!=null){
                    SharedPref(this).setString(Constants.USER_EMAIL,idToken.email!!)
                }
                if(idToken.id!=null){
                    SharedPref(this).setString(Constants.USER_ID,idToken.id!!)
                }
                if(idToken.displayName!=null){
                    SharedPref(this).setString(Constants.USER_NAME,idToken.displayName!!)
                }
                if(idToken.photoUrl!=null){
                    SharedPref(this).setString(Constants.USER_PHOTO,idToken.photoUrl.toString()!!)
                }
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this,"Google SignIn Failed",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
}
package com.jackahn.randomfoodgood.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.jackahn.randomfoodgood.R
import com.jackahn.randomfoodgood.dao.User
import com.jackahn.randomfoodgood.databinding.ActivityLoginBinding
import com.jackahn.randomfoodgood.dto.LoginDto
import com.jackahn.randomfoodgood.util.RetrofitUtil
import com.jackahn.randomfoodgood.util.SNSUtil
import com.jackahn.randomfoodgood.view.main.MainActivity
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    //Google 로그인
    private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.getResult(ApiException::class.java)

            // 구글 회원 가입
            val id = account.email.toString()
            val name = account.displayName.toString()
            createUser(id, name, 1)

        } catch (e: ApiException) {
            Log.e(LoginActivity::class.java.simpleName, e.stackTraceToString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 구글 로그인 요청
        binding.googleLoginBtn.setOnClickListener{
            requestGoogleLogin()
        }

        // 카카오 로그인 요청
        binding.kakaoLoginBtn.setOnClickListener {
            requestKakaoLogin()
        }

        // 네이버 로그인 요청
        binding.naverLoginBtn.setOnClickListener {
            requestNaverLogin()
        }

        // 최근 로그인 기록 확인
        checkRecentLogin()
    }

    // 구글 로그인 요청
    private fun requestGoogleLogin() {
        // 구글 클라이언트 초기화
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        SNSUtil.googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        val signInIntent = SNSUtil.googleSignInClient.signInIntent
        googleAuthLauncher.launch(signInIntent)
    }

    // 카카오 로그인 요청
    private fun requestKakaoLogin(){
        // 콜백 함수 선언
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            //에러 출력
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if (token != null) {
                //로그인 성공
                Log.i("KakaoLogin", "카카오 로그인 성공")
                getKakaoUserInfo()
            }
        }

        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        }else{
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    // 네이버 로그인 요청
    private fun requestNaverLogin(){
        val profileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(response: NidProfileResponse) {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                val profile = response.profile
                if(profile != null){
                    val email = profile.email.toString()
                    val name = profile.nickname.toString()
                    Log.i("login-naver", "이메일 : " + email + " / 닉네임 : " + name)
                    getUserData(email, name, 3)
                }
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(
                    applicationContext,
                    "errorCode:$errorCode, errorDesc:$errorDescription",
                    Toast.LENGTH_SHORT
                ).show()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                //로그인 유저 정보 가져오기
                NidOAuthLogin().callProfileApi(profileCallback)
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(
                    this@LoginActivity, "errorCode: ${errorCode}\n" +
                            "errorDescription: ${errorDescription}", Toast.LENGTH_SHORT
                ).show()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }
        NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
    }

    // 기존 로그인 검사
    private fun checkRecentLogin(){
        //구글 로그인 검사
        val gsa = GoogleSignIn.getLastSignedInAccount(this)
        if(gsa != null){
            //로그인 성공
            val id = gsa.email.toString()
            val name = gsa.displayName.toString()
            getUserData(id, name, 1)
        }
        // 카카오 로그인 검사
        else if (AuthApiClient.instance.hasToken()) {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error == null) {
                    // 로그인 성공
                    getKakaoUserInfo()
                }
                else {
                    if (error is KakaoSdkError && error.isInvalidTokenError()) {
                        //로그인 필요
                        Log.i("LoginCheck", "로그인 필요")
                    }
                    else {
                        //기타 에러
                        Log.e("KakaoLogin", error.stackTraceToString())
                    }
                }
            }
        }
    }

    private fun getKakaoUserInfo(){
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KakaoLogin", "사용자 정보 요청 실패", error)
            }
            else if (user != null) {
                Log.i(
                    "KakaoLogin", "사용자 정보 요청 성공" +
                            "\n회원번호: ${user.id}" +
                            "\n이메일: ${user.kakaoAccount?.email}" +
                            "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                            "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                )
                val id = user.kakaoAccount?.email.toString()
                val name = user.kakaoAccount?.profile?.nickname.toString()
                getUserData(id, name, 2)
            }
        }
    }


    // 유저 데이터 불러오기
    private fun getUserData(id: String, name: String, socialId: Int){
        var input = LoginDto()
        input.userId = id

        RetrofitUtil.userUtil.checkLogin(input).enqueue(object: retrofit2.Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val value = response.body()!!
                Log.i("login-result", value.toString())
                if(value.id != null){
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("user", value)
                    startActivity(intent)
                    finish()
                }
                else {
                    // 회원가입 시도
//                    Toast.makeText(this@LoginActivity, "회원가입 시도", Toast.LENGTH_SHORT).show()
                    createUser(id, name, socialId)
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("Retrofit", t.localizedMessage!!)
            }
        })
    }

    // 회원가입
    private fun createUser(id: String, name: String, socialId: Int){
        val input = User()
        input.id  = 0
        input.userId = id
        input.socialId = socialId
        input.userName = name

        Log.i("Create-User", "User Info : " + input)

        RetrofitUtil.userUtil.addUser(input).enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val value = response.body()!!

                if(value.id != null) {
//                    Toast.makeText(this@LoginActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("user", value)
                    startActivity(intent)
                    finish()
                }
                else {
                    Toast.makeText(this@LoginActivity, "회원가입 문제 발생", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("Retrofit", t.localizedMessage!!)
                Toast.makeText(this@LoginActivity, t.localizedMessage!!, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
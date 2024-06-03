package com.jackahn.randomfoodgood.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jackahn.randomfoodgood.R
import com.jackahn.randomfoodgood.view.login.LoginActivity
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 카카오 API 설정을 위한 Hash값 불러오기 (최초 1회만)
//        val keyHash = Utility.getKeyHash(this)
//        Log.d("Hash", keyHash)

        //카카오 네이티브 앱 키 초기화
        KakaoSdk.init(this, this.getString(R.string.kakao_native_key))

        //네아로 초기화
        NaverIdLoginSDK.initialize(this, getString(R.string.naver_client_id), getString(R.string.naver_client_secret), "니음쩔")

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            finish()
        }, 2000)
    }
}
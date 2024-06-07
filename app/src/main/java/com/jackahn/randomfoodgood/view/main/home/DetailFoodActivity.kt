package com.jackahn.randomfoodgood.view.main.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jackahn.randomfoodgood.R
import com.jackahn.randomfoodgood.dao.PlaceResult
import com.jackahn.randomfoodgood.databinding.ActivityDetailFoodBinding

class DetailFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFoodBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityDetailFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    // 데이터 초기화
    private fun init(){
        val intent = intent
        val data = intent.extras!!.get("food") as PlaceResult

        // 선택한 음식점 정보 텍스트 출력
        binding.detailNameText.text = data.place_name
        binding.detailPosText.text = data.road_address_name
        binding.detailPhoneText.text = data.phone

        // 공유하기 버튼
        binding.detailShareBtn.setOnClickListener {
            val textIntent = Intent(Intent.ACTION_SEND)
            textIntent.addCategory(Intent.CATEGORY_DEFAULT)
            textIntent.setType("text/plain")

            textIntent.putExtra(Intent.EXTRA_TEXT, data.place_name + "\n" + data.place_url)

            val shareIntent = Intent.createChooser(textIntent, "공유하기")
            startActivity(shareIntent)
        }

        binding.detailBackBtn.setOnClickListener {
            finish()
        }
    }
}
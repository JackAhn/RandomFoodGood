package com.jackahn.randomfoodgood.view.main.home

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jackahn.randomfoodgood.dto.OnDataPass
import com.jackahn.randomfoodgood.dao.PlaceResult
import com.jackahn.randomfoodgood.dao.ResultSearchKeyword
import com.jackahn.randomfoodgood.databinding.FragmentHomeBinding
import com.jackahn.randomfoodgood.service.KakaoService
import com.jackahn.randomfoodgood.view.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.streams.toList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    lateinit var dataPasser: OnDataPass

    private var mLocationManager: LocationManager? = null
    private var locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val longitude = location.longitude
            val latitude = location.latitude
            Log.d("Location", "Latitude : $latitude, Longitude : $longitude")
        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 377c3dd018b17885357d72c0852114cd"  // 카카오 REST API 키
        const val R = 6372.8 * 1000
    }

    private val result = ArrayList<PlaceResult>()
    private var adapter: BoardAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        mLocationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        val root: View = binding.root

        adapter = BoardAdapter(result)
        binding.searchList.adapter = adapter
        binding.searchList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.searchFoodBtn.setOnClickListener {
            if(binding.searchText.text!!.toString().trim().equals("")){
                passData(result)

                adapter = BoardAdapter(result)
                binding.searchList.adapter = adapter
            }
            else{
                val filterResult = ArrayList<PlaceResult>()

                result.forEach {
                    if(it.place_name.contains(binding.searchText.text!!.toString()))
                        filterResult.add(it)
                }

                passData(filterResult)

                adapter = BoardAdapter(filterResult)
                binding.searchList.adapter = adapter
            }

            adapter!!.notifyDataSetChanged()
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(result.isEmpty()){
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mLocationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10.0f, locationListener)

                val location: Location? = mLocationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                location?.let{
                    val latitude = location.latitude
                    val longitude = location.longitude

                    searchFoodData(latitude, longitude)
                }
            }
        }
        else{
            val recentData = (requireActivity() as MainActivity).getResult()
            adapter = BoardAdapter(recentData)
            binding.searchList.adapter = adapter
            adapter!!.notifyDataSetChanged()
        }
    }

    //근처 음식점 리스트 불러오기
    private fun searchFoodData(lat: Double, lng: Double){
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoService::class.java)

        // 검색 조건 입력
        val call = api.getSearchKeyword(API_KEY, "FD6", lng.toString(), lat.toString(), "1000")

        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                Log.d("Kakao-Result", "Raw: ${response.raw()}")
                Log.d("Kakao-Result", "Body: ${response.body()}")

                var body = response.body()!!.documents
                body = body.sortedBy {
                    getDistance(lat, lng, it.y.toDouble(), it.x.toDouble())
                }.toList()
                result.clear()

                body.forEach {
                    result.add(PlaceResult(it.place_name, it.road_address_name,
                        (getDistance(lat, lng, it.y.toDouble(), it.x.toDouble()).toString() + "m"), it.phone, it.place_url))
                }
                passData(result)
                adapter!!.notifyDataSetChanged()
                binding.searchText.setText("")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })
    }


    private fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
        val c = 2 * asin(sqrt(a))
        return (HomeFragment.R * c).toInt()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass
    }

    fun passData(data: ArrayList<PlaceResult>){
        dataPasser.onDataPass(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLocationManager!!.removeUpdates(locationListener)
        _binding = null
    }

    class BoardAdapter(val itemList: ArrayList<PlaceResult>) :
        RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {
        inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleText = itemView.findViewById<TextView>(com.jackahn.randomfoodgood.R.id.foodTitle)
            val placeText = itemView.findViewById<TextView>(com.jackahn.randomfoodgood.R.id.foodLocation)
            val distanceText = itemView.findViewById<TextView>(com.jackahn.randomfoodgood.R.id.foodDistance)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(com.jackahn.randomfoodgood.R.layout.food_detail, parent, false)
            return BoardViewHolder(view)
        }

        override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
            holder.titleText.text = itemList[position].place_name
            holder.placeText.text = itemList[position].road_address_name
            holder.distanceText.text = itemList[position].distance

            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, DetailFoodActivity::class.java)
                intent.putExtra("food", itemList[position])
                it.context.startActivity(intent)
            }
            holder.titleText.setOnClickListener {
                val intent = Intent(it.context, DetailFoodActivity::class.java)
                intent.putExtra("food", itemList[position])
                it.context.startActivity(intent)
            }
            holder.placeText.setOnClickListener {
                val intent = Intent(it.context, DetailFoodActivity::class.java)
                intent.putExtra("food", itemList[position])
                it.context.startActivity(intent)
            }
            holder.distanceText.setOnClickListener {
                val intent = Intent(it.context, DetailFoodActivity::class.java)
                intent.putExtra("food", itemList[position])
                it.context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return itemList.count()
        }
    }
}
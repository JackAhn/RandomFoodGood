package com.jackahn.randomfoodgood.view.main.history

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jackahn.randomfoodgood.R
import com.jackahn.randomfoodgood.dao.History
import com.jackahn.randomfoodgood.dao.PlaceResult
import com.jackahn.randomfoodgood.databinding.FragmentHistoryBinding
import com.jackahn.randomfoodgood.databinding.FragmentHomeBinding
import com.jackahn.randomfoodgood.databinding.FragmentMypageBinding
import com.jackahn.randomfoodgood.util.RetrofitUtil
import com.jackahn.randomfoodgood.view.main.MainActivity
import com.jackahn.randomfoodgood.view.main.home.DetailFoodActivity
import com.jackahn.randomfoodgood.view.main.home.HomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val result = ArrayList<History>()
    private var adapter: HistoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        adapter = HistoryAdapter(result)
        binding.historyList.adapter = adapter
        binding.historyList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        setHistoryList()

        return root
    }

    private fun setHistoryList(){
        val user = (requireActivity() as MainActivity).getUser()
        RetrofitUtil.historyUtil.getHistory(user.id!!).enqueue(object: Callback<List<History>>{
            override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                val body = response.body()

                if(body != null){
                    body.forEach {
                        result.add(it)
                    }
                    adapter!!.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<History>>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class HistoryAdapter(val itemList: ArrayList<History>) :
        RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
        inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameText = itemView.findViewById<TextView>(R.id.historyFoodNameText)
            val placeText = itemView.findViewById<TextView>(R.id.historyPlaceText)
            val phoneText = itemView.findViewById<TextView>(R.id.historyPhoneText)
            val createdText = itemView.findViewById<TextView>(R.id.historyCreatedText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.history_info, parent, false)
            return HistoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
            holder.nameText.text = itemList[position].foodName
            holder.placeText.text = itemList[position].foodPlace
            holder.phoneText.text = itemList[position].foodPhone
            holder.createdText.text = SimpleDateFormat("yyyy-MM-dd").format(itemList[position].created)
        }

        override fun getItemCount(): Int {
            return itemList.count()
        }
    }
}
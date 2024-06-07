package com.jackahn.randomfoodgood.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jackahn.randomfoodgood.R

/**
 * A simple [Fragment] subclass.
 * Use the [InsertNameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsertNameFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insert_name, container, false)
    }
}
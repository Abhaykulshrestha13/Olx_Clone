package com.example.olxapp.ui.sell

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.olxapp.R
import com.example.olxapp.databinding.ActivityLoginBinding.inflate
import com.example.olxapp.databinding.ActivitySplashBinding.inflate
import com.example.olxapp.model.CategoriesModel
import com.example.olxapp.ui.sell.adapter.SellAdapter
import com.google.firebase.firestore.FirebaseFirestore


import java.util.zip.Inflater

class SellFragment : Fragment(), SellAdapter.ItemClickListener {
    private lateinit var categoriesModel: MutableList<CategoriesModel>
    val db = FirebaseFirestore.getInstance()
    lateinit var rv_offerings:RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_sell, container, false)
        rv_offerings = root.findViewById(R.id.rv_offering)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getCategoryList()
    }
    private fun getCategoryList() {
        db.collection("Categories").get().addOnSuccessListener {
            categoriesModel = it.toObjects<CategoriesModel>(CategoriesModel::class.java)
            Log.d("data", CategoriesModel::class.java.toString())
            setAdapter()
        }
    }

    private fun setAdapter() {
        rv_offerings.layoutManager = GridLayoutManager(context,3)
        val sellAdapter = SellAdapter(categoriesModel,this)
        rv_offerings.adapter = sellAdapter
    }

    override fun onItemClick(position: Int) {

    }
}
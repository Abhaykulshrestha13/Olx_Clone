package com.example.olxapp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.olxapp.R
import com.example.olxapp.model.CategoriesModel
import com.example.olxapp.ui.home.adapter.CategoriesAdapter
import com.example.olxapp.utilites.Constants
import com.example.olxapp.utilites.SharedPref
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment(), CategoriesAdapter.ItemClickListener {
    private lateinit var categoriesAdapter: CategoriesAdapter
    lateinit var tvCityName:TextView
    lateinit var rv_categories:RecyclerView
    val db = FirebaseFirestore.getInstance()
    lateinit var edSearch:EditText
    private lateinit var categoriesModel: MutableList<CategoriesModel>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        tvCityName = root.findViewById(R.id.tvCityName)
        rv_categories = root.findViewById(R.id.rv_categories)
        edSearch = root.findViewById(R.id.edSearch)
        return root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvCityName.text = SharedPref(activity!!).getString(Constants.CITY_NAME)
        getCategoryList()
        textListener()
    }

    private fun textListener() {
        edSearch.addTextChangedListener (object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                fiterList(s.toString())
            }

        })
    }

    private fun fiterList(toString: String) {
        var temp:MutableList<CategoriesModel> = ArrayList()
        for(data in categoriesModel){
            if(data.key.contains(toString.capitalize())||data.key.contains(toString)){
                temp.add(data)
            }
        }
        categoriesAdapter.updateList(temp)
    }

    private fun getCategoryList() {
            db.collection("Categories").get().addOnSuccessListener {
            categoriesModel = it.toObjects<CategoriesModel>(CategoriesModel::class.java)
                Log.d("data", CategoriesModel::class.java.toString())
               setAdapter()
        }
    }

    private fun setAdapter() {
        rv_categories.layoutManager = GridLayoutManager(context,3)
        categoriesAdapter = CategoriesAdapter(categoriesModel,this)
        rv_categories.adapter = categoriesAdapter
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(context, "Hey $position",Toast.LENGTH_LONG).show()
    }
}
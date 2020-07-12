package com.example.templatebottomnavmenu.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.templatebottomnavmenu.R
import com.example.templatebottomnavmenu.ui.dashboard.DashboardFragment

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var importEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer { textView.text = it })
        initialize(root)
        return root
    }

    private fun initialize(root: View) {
        importEditText = root.findViewById(R.id.importText)
        importEditText.visibility = View.INVISIBLE
        saveButton = root.findViewById(R.id.buttonSave)
        saveButton.visibility = View.INVISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionListener(view)
    }

    private fun actionListener(view: View){
        saveButton.setOnClickListener {
            Toast.makeText(context,importEditText.text,Toast.LENGTH_SHORT).show()
            changeFragment(view)
        }
    }

    private fun changeFragment(view: View){
        parentFragmentManager.beginTransaction().replace(R.id.nav_host_fragment.toInt(), DashboardFragment()).commit()
        findNavController(view).navigate(R.id.navigation_dashboard) //val navController = findNavController(view)
    }
}

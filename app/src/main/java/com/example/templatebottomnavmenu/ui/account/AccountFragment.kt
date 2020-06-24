package com.example.templatebottomnavmenu.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.templatebottomnavmenu.MainActivity
import com.example.templatebottomnavmenu.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class AccountFragment : Fragment() {

    private val TAG = AccountFragment::class.java.simpleName
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var avatar: ImageView
    private lateinit var logoutButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_account, container, false)
        val textView: TextView = root.findViewById(R.id.text_account)
        accountViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        initialize(root)
        loadAvatar()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionListener(view)
    }

    private fun actionListener(view: View) {
        logoutButton.setOnClickListener {
            (activity as MainActivity?)!!.signOut()
        }
    }

    private fun initialize(root: View) {
        auth = Firebase.auth
        avatar = root.findViewById(R.id.avatar_profile)
        logoutButton = root.findViewById(R.id.logout)
    }

    private fun loadAvatar() {
        Picasso.get().load(auth.currentUser?.photoUrl.toString()).into(avatar)
    }
}

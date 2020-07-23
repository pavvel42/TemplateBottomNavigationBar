package com.example.templatebottomnavmenu.ui.circularfragment

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.templatebottomnavmenu.R
import com.leinardi.android.speeddial.SpeedDialView
import kotlin.math.hypot
import kotlin.math.roundToInt

class CircularFragmentA : Fragment() {

    private lateinit var constraintLayout: ConstraintLayout

    companion object {
        fun newInstance() = CircularFragmentA()
    }

    private lateinit var viewModel: CircularFragmentAViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_circular_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        constraintLayout = view.findViewById(R.id.circular_animate_background_a)
        constraintLayout.visibility = View.INVISIBLE //View widoczny w XML
        setAnimation(constraintLayout, true)
    }

    private fun setAnimation(view: View, isShow: Boolean) {

        val floatingActionButton = activity?.findViewById<SpeedDialView>(R.id.floatingActionButton)

        val cx = floatingActionButton!!.width / 2
        val cy = floatingActionButton.height / 2

        val width = requireContext().resources.displayMetrics.widthPixels
        val height = requireContext().resources.displayMetrics.heightPixels

        val finalRadius = hypot(width.toDouble(), height.toDouble()).toFloat()

        if (isShow) { var animation: Animator = ViewAnimationUtils.createCircularReveal(
                view,
                (floatingActionButton.x + cx).roundToInt(),
                (floatingActionButton.y + cy).roundToInt(),
                0f, finalRadius).setDuration(500)
            view.visibility = View.VISIBLE
            animation.start()
        }
    }
}
package com.grupo8.fullsound.ui.ajustes

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.grupo8.fullsound.databinding.FragmentAjustesBinding

class AjustesFragment : Fragment() {

    private var _binding: FragmentAjustesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAjustesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startRgbTitleAnimation()
    }

    private fun startRgbTitleAnimation() {
        val textView: TextView = binding.txtTitulo
        val colors = intArrayOf(
            Color.RED,
            Color.MAGENTA,
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.YELLOW,
            Color.RED
        )
        val animator = ValueAnimator.ofFloat(0f, (colors.size - 1).toFloat())
        animator.duration = 4000L
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener { animation ->
            val position = animation.animatedValue as Float
            val index = position.toInt()
            val fraction = position - index
            val color = ArgbEvaluator().evaluate(
                fraction,
                colors[index],
                colors[(index + 1) % colors.size]
            ) as Int
            textView.setTextColor(color)
        }
        animator.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


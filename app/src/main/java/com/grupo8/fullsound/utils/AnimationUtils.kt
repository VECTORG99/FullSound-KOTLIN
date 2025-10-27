package com.grupo8.fullsound.utils

import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Animation

/**
 * Utilidades para animaciones en la aplicación
 */
object AnimationHelper {

    /**
     * Anima la entrada de una vista con fade in
     */
    fun fadeIn(view: View, duration: Long = 300) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .start()
    }

    /**
     * Anima la salida de una vista con fade out
     */
    fun fadeOut(view: View, duration: Long = 300, onEnd: (() -> Unit)? = null) {
        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                view.visibility = View.GONE
                onEnd?.invoke()
            }
            .start()
    }

    /**
     * Anima una vista deslizándola desde arriba
     */
    fun slideDown(view: View, duration: Long = 400) {
        view.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(view.context, com.grupo8.fullsound.R.anim.slide_up)
        animation.duration = duration
        view.startAnimation(animation)
    }

    /**
     * Anima una vista deslizándola hacia abajo (ocultar)
     */
    fun slideUp(view: View, duration: Long = 400, onEnd: (() -> Unit)? = null) {
        val animation = AnimationUtils.loadAnimation(view.context, com.grupo8.fullsound.R.anim.slide_down)
        animation.duration = duration
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                view.visibility = View.GONE
                onEnd?.invoke()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        view.startAnimation(animation)
    }

    /**
     * Anima una vista con efecto bounce (rebote)
     */
    fun bounce(view: View) {
        val animation = AnimationUtils.loadAnimation(view.context, com.grupo8.fullsound.R.anim.bounce)
        view.startAnimation(animation)
    }

    /**
     * Anima una vista con scale up
     */
    fun scaleUp(view: View, duration: Long = 200) {
        view.visibility = View.VISIBLE
        view.scaleX = 0.8f
        view.scaleY = 0.8f
        view.alpha = 0f
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(duration)
            .start()
    }

    /**
     * Anima una vista con scale down (para ocultar)
     */
    fun scaleDown(view: View, duration: Long = 200, onEnd: (() -> Unit)? = null) {
        view.animate()
            .scaleX(0.8f)
            .scaleY(0.8f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                view.visibility = View.GONE
                view.scaleX = 1f
                view.scaleY = 1f
                onEnd?.invoke()
            }
            .start()
    }

    /**
     * Toggle de visibilidad con animación
     */
    fun toggleVisibility(view: View, show: Boolean, animationType: AnimationType = AnimationType.FADE) {
        if (show && view.visibility == View.VISIBLE) return
        if (!show && view.visibility == View.GONE) return

        when (animationType) {
            AnimationType.FADE -> {
                if (show) fadeIn(view) else fadeOut(view)
            }
            AnimationType.SLIDE -> {
                if (show) slideDown(view) else slideUp(view)
            }
            AnimationType.SCALE -> {
                if (show) scaleUp(view) else scaleDown(view)
            }
        }
    }

    /**
     * Anima un botón al hacer clic (efecto de presión)
     */
    fun animateClick(view: View) {
        view.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    /**
     * Anima una lista de vistas en secuencia
     */
    fun animateListSequentially(views: List<View>, delay: Long = 100, animationType: AnimationType = AnimationType.FADE) {
        views.forEachIndexed { index, view ->
            view.postDelayed({
                when (animationType) {
                    AnimationType.FADE -> fadeIn(view)
                    AnimationType.SLIDE -> slideDown(view)
                    AnimationType.SCALE -> scaleUp(view)
                }
            }, delay * index)
        }
    }

    enum class AnimationType {
        FADE,
        SLIDE,
        SCALE
    }
}


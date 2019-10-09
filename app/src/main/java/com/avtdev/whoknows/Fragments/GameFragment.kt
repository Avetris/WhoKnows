package com.avtdev.whoknows.Fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.avtdev.whoknows.Listeners.IMainListener
import com.avtdev.whoknows.Model.Question
import com.avtdev.whoknows.R
import com.avtdev.whoknows.Services.Constants
import com.avtdev.whoknows.Services.GameFactory
import org.w3c.dom.Text


class GameFragment(
    mGameType : Constants.Companion.GameType = Constants.Companion.GameType.NORMAL
) : Fragment(), View.OnClickListener {

    private var listener: IMainListener? = null
    private var mGameFactory = GameFactory(mGameType)

    private var accumulatorTextView: TextView? = null
    private var leftButton: Button? = null
    private var rightButton: Button? = null
    private var cardView: ImageView? = null

    private var currentQuestion: Question? = null

    private var accumulative: Int = 0

    private var onAnimation: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IMainListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement IMainListener")
        }
    }
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance(gameType: Constants.Companion.GameType) : GameFragment{
            val gameFragment = GameFragment(gameType)
            return gameFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accumulatorTextView = view.findViewById(R.id.tvAccumulator)
        leftButton = view.findViewById<Button>(R.id.btnLeft)
        rightButton = view.findViewById<Button>(R.id.btnRight)
        cardView = view.findViewById<ImageView>(R.id.ivCurrentCard)

        leftButton?.setOnClickListener(this)
        rightButton?.setOnClickListener(this)

        if(mGameFactory.mGameType in listOf(Constants.Companion.GameType.INVERSE_ACCUMULATIVE, Constants.Companion.GameType.ACCUMULATIVE)){
            accumulatorTextView?.visibility = View.VISIBLE
        }else{
            accumulatorTextView?.visibility = View.GONE
            if(mGameFactory.mGameType  == Constants.Companion.GameType.RANDOM_CARD){
                cardView?.setOnClickListener(this)
                view.findViewById<LinearLayout>(R.id.llButtons).visibility = View.GONE
            }
        }

        reset()
    }

    fun showButtons(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            leftButton?.setText(Html.fromHtml(currentQuestion?.question1, Html.FROM_HTML_MODE_LEGACY))
            rightButton?.setText(Html.fromHtml(currentQuestion?.question2, Html.FROM_HTML_MODE_LEGACY))
        }else{
            leftButton?.setText(Html.fromHtml(currentQuestion?.question1))
            rightButton?.setText(Html.fromHtml(currentQuestion?.question2))
        }
    }

    fun reset(){
        accumulative = 0
        cardView?.setImageResource(R.drawable.back)
        currentQuestion = null
        nextCard()
    }

    fun nextCard(){
        accumulative++
        currentQuestion = mGameFactory.nextQuestion(context!!, currentQuestion?.card)
        showButtons()
        accumulatorTextView?.setText(String.format(context?.getString(R.string.shots)!!, accumulative))
    }

    fun showCard(){
        val drawableResourceId = context?.resources?.getIdentifier(currentQuestion?.card?.path, "drawable", context?.getPackageName())

        val oa1 = ObjectAnimator.ofFloat(cardView!!, "scaleX", 1f, 0f)
        val oa2 = ObjectAnimator.ofFloat(cardView!!, "scaleX", 0f, 1f)
        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = AccelerateDecelerateInterpolator()
        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                onAnimation = true
            }
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                cardView?.setImageResource(drawableResourceId!!)
                oa2.start()
            }
        })
        oa2.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                onAnimation = false
            }
        })
        oa1.start()
    }

    fun showDialog(){
        val message: String?
        if(mGameFactory.mGameType in listOf(Constants.Companion.GameType.ACCUMULATIVE, Constants.Companion.GameType.INVERSE_ACCUMULATIVE)){
            if(accumulative > 1){
                message = String.format(context?.getString(R.string.drink_x_shots)!!, accumulative)
            }else{
                message = context?.getString(R.string.drink_1_shot)!!
            }
        }else{
            message = context?.getString(R.string.drink_1_shot)!!
        }


        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_game, null)
        dialogView.findViewById<TextView>(R.id.message).setText(message)

        AlertDialog.Builder(context, R.style.CustomDialogTheme)
        .setView(dialogView)
        .setPositiveButton(R.string.replay){ _, _ ->
            listener?.showInterstitialAd()
            reset()
        }
        .setNegativeButton(R.string.back_to_menu){ _, _ ->
            listener?.showInterstitialAd()
            listener?.changeFragment(MenuFragment.newInstance())
        }
        .setCancelable(false)
        .show()
    }

    fun validateCard(leftButton: Boolean){
        val correct = currentQuestion?.isCorrect(
            if(mGameFactory.mGameType in listOf(Constants.Companion.GameType.NORMAL, Constants.Companion.GameType.ACCUMULATIVE)) leftButton
            else !leftButton
        )
        showCard()
        if(!correct!!){
            showDialog()
        }else{
            nextCard()
        }
    }

    override fun onClick(v: View?) {
        if(!onAnimation){
            when(v?.id){
                R.id.btnLeft -> validateCard(true)
                R.id.btnRight -> validateCard(false)
                R.id.ivCurrentCard -> {
                    showCard()
                    nextCard()
                    listener?.showInterstitialAd()
                }
            }
        }
    }
}

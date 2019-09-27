package com.avtdev.whoknows.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.avtdev.whoknows.Listeners.IMainListener
import com.avtdev.whoknows.Model.Card
import com.avtdev.whoknows.Model.Question
import com.avtdev.whoknows.R
import com.avtdev.whoknows.Services.Constants
import com.avtdev.whoknows.Services.GameFactory


class GameFragment(
    mGameType : Constants.Companion.GameType = Constants.Companion.GameType.NORMAL
) : Fragment(), View.OnClickListener {

    private var listener: IMainListener? = null
    private var mGameFactory = GameFactory(mGameType)

    private var leftButton: Button? = null
    private var rightButton: Button? = null
    private var cardView: ImageView? = null

    private var currentQuestion: Question? = null

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

        leftButton = view.findViewById<Button>(R.id.btnLeft)
        rightButton = view.findViewById<Button>(R.id.btnLeft)
        cardView = view.findViewById<ImageView>(R.id.ivCurrentCard)

        cardView?.setImageResource(R.drawable.back)
    }

    fun showCard(){
        val drawableResourceId = context?.resources?.getIdentifier(currentQuestion?.card?.path, "drawable", context?.getPackageName())
        cardView?.setImageResource(drawableResourceId!!)
    }

    fun validateCard(leftButton: Boolean){
        val correct = currentQuestion?.isCorrect(
            if(mGameFactory.mGameType in listOf(Constants.Companion.GameType.NORMAL, Constants.Companion.GameType.ACCUMULATIVE)) leftButton
            else !leftButton
        )
        showCard()
        if(correct!!){

        }else{

        }

    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnLeft -> validateCard(true)
            R.id.btnRight -> validateCard(false)
        }
    }
}

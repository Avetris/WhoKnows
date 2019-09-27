package com.avtdev.whoknows.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.avtdev.whoknows.Listeners.IMainListener

import com.avtdev.whoknows.R
import com.avtdev.whoknows.Services.Constants


class MenuFragment : Fragment(), View.OnClickListener {

    private var listener: IMainListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IMainListener) {
            listener = context

        } else {
            throw RuntimeException(context.toString() + " must implement OnMenuFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = MenuFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.btnSettigns).setOnClickListener(this)
        view.findViewById<Button>(R.id.btnNormalGame).setOnClickListener(this)
        view.findViewById<Button>(R.id.btnAccumulativeGame).setOnClickListener(this)
        view.findViewById<Button>(R.id.btnInverseGame).setOnClickListener(this)
        view.findViewById<Button>(R.id.btnInverseAccumulativeGame).setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            R.id.btnSettigns -> activity?.let { SettingsDialog(it).showDialog() }
            R.id.btnNormalGame -> listener?.changeFragment(GameFragment.newInstance(Constants.Companion.GameType.NORMAL))
            R.id.btnAccumulativeGame -> listener?.changeFragment(GameFragment.newInstance(Constants.Companion.GameType.ACCUMULATIVE))
            R.id.btnInverseGame -> listener?.changeFragment(GameFragment.newInstance(Constants.Companion.GameType.INVERSE))
            R.id.btnInverseAccumulativeGame -> listener?.changeFragment(GameFragment.newInstance(Constants.Companion.GameType.INVERSE_ACCUMULATIVE))
        }
    }
}

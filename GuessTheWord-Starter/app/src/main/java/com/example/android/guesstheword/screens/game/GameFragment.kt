/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding
import timber.log.Timber

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private lateinit var binding: GameFragmentBinding

    /**
     * The model of this view
     */
    private lateinit var viewModel: GameViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_fragment,
                container,
                false
        )

        Timber.i("ViewModelProvider.get() called")
        /*
        We use a ViewModelProvider that's basically a factory. We instantiate the
        factory with the view and than we get the model associated.
        Now, the question is if I can use more models for my view or not, I could.
         */
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // Here, we set an observer that is attached to the LiveData "score"
        viewModel.score.observe(viewLifecycleOwner, Observer { newScore ->
            Timber.i("The new score is: $newScore")
            binding.scoreText.text = newScore.toString()
        })

        viewModel.word.observe(viewLifecycleOwner, Observer { newWord ->
            Timber.i("The new word is: $newWord")
            binding.wordText.text = newWord.toString()
        })

        viewModel.eventGameFinish.observe(viewLifecycleOwner, Observer<Boolean> { hasFinisched ->
            if(hasFinisched) {
                gameFinished()
                Timber.i("Game finished")
            }
            else
                Timber.i("Game not finished, yet")
        })

        // Initialize all the button listeners
        binding.apply {
            correctButton.setOnClickListener { onCorrect() }
            skipButton.setOnClickListener { onSkip() }
            endGameButton.setOnClickListener { onEndGame() }
        }

        return binding.root

    }

    private fun onEndGame() {
        gameFinished()
    }

    /**
     * Called when the game is finisched
     */
    private fun gameFinished() {
        Toast.makeText(activity,"Game has just finisched", Toast.LENGTH_SHORT).show()
        val action = GameFragmentDirections.actionGameToScore()
        action.score = viewModel.score.value?:0
        NavHostFragment.findNavController(this).navigate(action)
    }

    /** Methods for buttons presses **/
    private fun onSkip() {
        viewModel.onSkip()
    }

    private fun onCorrect() {
        viewModel.onCorrect()
    }

}


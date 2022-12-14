package com.example.android.guesstheword.screens.game

import android.content.IntentSender.OnFinished
import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel : ViewModel(){
    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }
    companion object{

        private const val DONE = 0L

        private const val ONE_SECOND = 1000L

        private const val COUNTDOWN_TIME = 60000L
        private const val COUNTDOWN_PANIC_SECONDS = 10L

    }

    private val timer : CountDownTimer

    // The current word
    private var _word = MutableLiveData<String>()
    val word : LiveData<String>
        get() = _word

    private val _currentTime = MutableLiveData<Long>()
    val currentTime : LiveData<Long>
        get() = _currentTime

    val currentTimeString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }

    // The current score
    private var _score = MutableLiveData<Int>()
    val score : LiveData<Int>
        get() = _score
    private var _eventGameFinish = MutableLiveData<Boolean>()

    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish
    private val _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz


    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>
    init {
        _eventGameFinish.value = false
        Log.i("GameViewModel", "GameViewModel created!")
        resetList()
        nextWord()
        _score.value = 0
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND){

            override fun onTick(millisUnitUntilFinished: Long) {
                _currentTime.value = millisUnitUntilFinished/ ONE_SECOND
                if (millisUnitUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _eventGameFinish.value = true
                _eventBuzz.value = BuzzType.GAME_OVER
            }
        }
        timer.start()
//        DateUtils.formatElapsedTime(newTime)

    }

    override fun onCleared() {
        super.onCleared()
//        timer.cancel()
        Log.i("GameViewModel", "GameViewModel Destroyed")
    }
    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }
    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
//            _eventGameFinish.value = true
        }
        //            else {
//            _word.value = wordList.removeAt(0)
//        }
        _word.value = wordList.removeAt(0)
    }
    /** Methods for buttons presses **/

    fun onSkip() {
        _score.value = (_score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = (_score.value)?.plus(1)
        _eventBuzz.value = BuzzType.CORRECT
        nextWord()
    }
    fun onGameFinishComplete(){

        _eventGameFinish.value = false
        timer.cancel()

    }
    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }

}
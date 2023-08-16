package com.example.audiocontrollerlibrary

import android.content.Context
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.audiocontrollerlibrary.databinding.AudiocontrollerBinding

class AudioController @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {
    private lateinit var binding: AudiocontrollerBinding
    private val seekBarAction: Runnable = object : Runnable {
        override fun run() {
            if (AudioPlayer.isPlaying) {
                binding.seekBar.progress = AudioPlayer.currentPosition
                handler.postDelayed(this, 1L)
            } else {
                binding.playButton.setImageResource(R.drawable.baseline_play_arrow_24)
                handler.removeCallbacks(this)
            }
        }
    }

    init {
        View.inflate(context, R.layout.audiocontroller, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = AudiocontrollerBinding.bind(this)

        setLayoutBackgroundColor(Color.rgb(3, 155, 229))
        initListeners()
    }

    private fun initListeners() {
        AudioPlayer.listener = MediaPlayer.OnPreparedListener {
            val mmr = MediaMetadataRetriever().apply {
                setDataSource(context, Uri.parse(AudioPlayer.filepath))
            }
            val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()!!

            binding.playButton.setImageResource(R.drawable.baseline_play_arrow_24)
            binding.timeStamp1.text = "00:00:00"
            binding.timeStamp2.text = getTimeStamp(duration / 1000)
            binding.seekBar.max = duration
            binding.seekBar.progress = 0
        }

        binding.backwardButton.setOnClickListener {
            val progress = binding.seekBar.progress
            binding.seekBar.progress = progress - 5000
            AudioPlayer.seekTo(binding.seekBar.progress)
        }

        binding.forwardButton.setOnClickListener {
            val progress = binding.seekBar.progress
            binding.seekBar.progress = progress + 5000
            AudioPlayer.seekTo(binding.seekBar.progress)
        }

        binding.playButton.setOnClickListener {
            if (!AudioPlayer.isPlaying) {
                binding.playButton.setImageResource(R.drawable.baseline_pause_24)
                AudioPlayer.start()
                handler.post(seekBarAction)
            } else {
                binding.playButton.setImageResource(R.drawable.baseline_play_arrow_24)
                AudioPlayer.pause()
                handler.removeCallbacks(seekBarAction)
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                binding.timeStamp1.text = getTimeStamp(progress / 1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                binding.timeStamp1.text = getTimeStamp(seekBar.progress / 1000)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                binding.timeStamp1.text = getTimeStamp(seekBar.progress / 1000)
                AudioPlayer.seekTo(seekBar.progress)
            }
        })
    }

    private fun getTimeStamp(countTimeSeconds: Int): String {
        val hour = countTimeSeconds / 3600
        val minute = (countTimeSeconds % 3600) / 60
        val second = countTimeSeconds % 60
        return "%02d:%02d:%02d".format(hour, minute, second)
    }

    /**
     * Sets the audio file to play.
     */
    fun setAudioSource(filepath: String) {
        AudioPlayer.ready(filepath)
    }

    /**
     * Sets the background color for the layout.
     */
    fun setLayoutBackgroundColor(color: Int) {
        binding.root.setBackgroundColor(color)
    }

    /*
    /**
     * Sets the text color for the timestamps.
     */
    fun setTextColor(color: Int) {
        binding.timeStamp1.setTextColor(color)
        binding.timeStamp2.setTextColor(color)
    }
    */
}
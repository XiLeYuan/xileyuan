package com.xly.business.square.view

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.xly.R
import com.xly.databinding.ActivityVideoPlayerBinding

class MomentVideoPlayerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVideoPlayerBinding
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 全屏显示
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val videoUrl = intent.getStringExtra("videoUrl") ?: return
        val videoThumbnail = intent.getIntExtra("videoThumbnail", 0)
        
        // 加载缩略图
        if (videoThumbnail != 0) {
            Glide.with(this)
                .load(videoThumbnail)
                .centerCrop()
                .into(binding.ivThumbnail)
        }
        
        // 设置返回按钮
        binding.ivBack.setOnClickListener {
            finish()
        }
        
        // 初始化播放器
        initializePlayer(videoUrl)
    }
    
    private fun initializePlayer(videoUrl: String) {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            
            // 创建媒体项
            // 支持本地assets视频和网络视频
            val uri = if (videoUrl.startsWith("http://") || videoUrl.startsWith("https://")) {
                // 网络视频
                Uri.parse(videoUrl)
            } else if (videoUrl.startsWith("file:///android_asset/")) {
                // assets视频（已包含前缀）
                Uri.parse(videoUrl)
            } else {
                // assets视频（添加前缀）
                Uri.parse("file:///android_asset/$videoUrl")
            }
            val mediaItem = MediaItem.fromUri(uri)
            exoPlayer.setMediaItem(mediaItem)
            
            // 恢复播放位置
            exoPlayer.playWhenReady = playWhenReady
            exoPlayer.seekTo(currentWindow, playbackPosition)
            
            // 准备播放
            exoPlayer.prepare()
            
            // 播放开始后隐藏缩略图
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY && exoPlayer.isPlaying) {
                        binding.ivThumbnail.visibility = View.GONE
                    }
                }
            })
        }
    }
    
    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentWindow = exoPlayer.currentWindowIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }
    
    override fun onStart() {
        super.onStart()
        if (player == null) {
            val videoUrl = intent.getStringExtra("videoUrl") ?: return
            initializePlayer(videoUrl)
        }
    }
    
    override fun onResume() {
        super.onResume()
        if (player == null) {
            val videoUrl = intent.getStringExtra("videoUrl") ?: return
            initializePlayer(videoUrl)
        }
    }
    
    override fun onPause() {
        super.onPause()
        releasePlayer()
    }
    
    override fun onStop() {
        super.onStop()
        releasePlayer()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }
}


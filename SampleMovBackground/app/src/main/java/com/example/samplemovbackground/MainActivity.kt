package com.example.samplemovbackground

import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.samplemovbackground.ui.theme.SampleMovBackgroundTheme
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.widget.VideoView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleMovBackgroundTheme {
                VideoBackgroundWithAnimatedContent()
            }
        }
    }
}

@Composable
fun VideoBackgroundWithAnimatedContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        // 動画をバックグラウンドとして表示
        VideoBackground(videoResId = R.raw.movie_test)
        
        // アニメーション付きのコンテンツ
        AnimatedContent()
    }
}

@Composable
fun VideoBackground(videoResId: Int) {
    val context = LocalContext.current
    
    AndroidView(
        factory = { ctx ->
            VideoView(ctx).apply {
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                setVideoURI(Uri.parse("android.resource://${context.packageName}/${videoResId}"))
                setOnPreparedListener { mp ->
                    // 動画のアスペクト比を維持しながら画面いっぱいに表示
                    val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
                    val screenRatio = width / height.toFloat()
                    
                    val scale = if (videoRatio > screenRatio) {
                        height.toFloat() / mp.videoHeight.toFloat()
                    } else {
                        width.toFloat() / mp.videoWidth.toFloat()
                    }
                    
                    // スケーリングを適用
                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                    
                    mp.isLooping = true
                    mp.start()
                }
                
                // システムUIを非表示にして全画面表示
                systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun AnimatedContent() {
    // アルファ値のアニメーション
    val alpha = remember { Animatable(0f) }
    
    // 3秒後にアニメーション開始
    LaunchedEffect(key1 = true) {
        // 3秒待機
        kotlinx.coroutines.delay(3000)
        // 1秒かけてフェードイン
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 四角形
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)  // 画面幅の80%
                .fillMaxHeight(0.4f) // 画面高さの40%
                .alpha(alpha.value)
                .background(
                    color = Color(0x80FFFFFF), // 半透明の白
                    shape = RoundedCornerShape(16.dp)
                )
        )
        
        // テキスト
        Text(
            text = "Welcome",
            fontSize = 40.sp,  // フォントサイズを大きく
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.alpha(alpha.value)
        )
    }
}
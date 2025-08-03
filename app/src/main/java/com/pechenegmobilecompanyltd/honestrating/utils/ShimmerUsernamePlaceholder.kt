package com.pechenegmobilecompanyltd.honestrating.utils

import com.valentinilk.shimmer.shimmer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

@Composable
fun ProfileShimmerPlaceholder() {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .shimmer(shimmerInstance)
                .background(Color.LightGray.copy(alpha = 0.4f))
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmer(shimmerInstance)
                .background(Color.LightGray.copy(alpha = 0.4f))
        )
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(34.dp)
                .clip(RoundedCornerShape(18.dp))
                .shimmer(shimmerInstance)
                .background(Color.LightGray.copy(alpha = 0.3f))
        )
        Spacer(Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(15.dp))
                .shimmer(shimmerInstance)
                .background(Color.LightGray.copy(alpha = 0.4f))
        )
    }
}
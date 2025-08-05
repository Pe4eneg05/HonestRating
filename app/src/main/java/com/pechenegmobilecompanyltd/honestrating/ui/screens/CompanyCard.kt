package com.pechenegmobilecompanyltd.honestrating.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pechenegmobilecompanyltd.honestrating.model.Company

@Composable
fun CompanyCard(
    company: Company,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp) // Уменьшили отступы
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp), // Упростили скругление
        elevation = CardDefaults.cardElevation(2.dp), // Уменьшили тень
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp) // Уменьшили внутренний паддинг
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Заменили градиент на простой цвет
            Box(
                modifier = Modifier
                    .size(42.dp) // Уменьшили размер
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = company.name.first().uppercase(),
                    style = MaterialTheme.typography.titleMedium, // Упростили типографику
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = company.name,
                    style = MaterialTheme.typography.bodyLarge, // Упрощенный стиль
                    maxLines = 1, // Важно для производительности
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = company.industry,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AnimatedRatingBar(rating = company.rating) // Упрощенный рейтинг
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${company.reviews}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "отзывов",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )
            }
        }
    }
}
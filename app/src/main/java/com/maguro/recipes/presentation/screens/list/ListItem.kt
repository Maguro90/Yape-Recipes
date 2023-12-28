package com.maguro.recipes.presentation.screens.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.maguro.recipes.R
import com.maguro.recipes.data.model.Recipe

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    recipe: Recipe,
    onClick: (Recipe) -> Unit
) {
    Row(
        modifier = modifier
            .clickable(onClick = { onClick(recipe) })
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .width(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            model = recipe.imageUrl,
            contentDescription = recipe.name,
            contentScale = ContentScale.FillWidth,
            placeholder = painterResource(id = R.drawable.image_placeholder)
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            text = recipe.name
        )
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = ""
        )
    }
}
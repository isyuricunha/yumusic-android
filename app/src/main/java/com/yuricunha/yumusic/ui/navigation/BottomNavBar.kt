package com.yuricunha.yumusic.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yuricunha.yumusic.ui.theme.BackgroundElevated
import com.yuricunha.yumusic.ui.theme.TextPrimary
import com.yuricunha.yumusic.ui.theme.TextTertiary

@Composable
fun BottomNavBar(
    navController: NavHostController,
    currentRoute: String?,
) {
    NavigationBar(
        containerColor = BackgroundElevated,
        contentColor = TextTertiary,
        tonalElevation = 0.dp,
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.labelResId),
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.labelResId),
                    )
                },
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TextPrimary,
                    unselectedIconColor = TextTertiary,
                    selectedTextColor = TextPrimary,
                    unselectedTextColor = TextTertiary,
                    indicatorColor = TextPrimary.copy(alpha = 0.08f),
                ),
            )
        }
    }
}

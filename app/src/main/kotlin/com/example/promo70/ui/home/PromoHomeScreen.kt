package com.example.promo70.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.promo70.model.PromoCategory
import com.example.promo70.model.PromoItem
import com.example.promo70.ui.components.CategoryFilterChips
import com.example.promo70.ui.components.PromoBottomBar
import com.example.promo70.ui.components.PromoCard
import com.example.promo70.ui.components.PromoSearchBar
import com.example.promo70.ui.theme.Promo70Theme
import kotlinx.coroutines.launch

/**
 * Pantalla principal conectada al ViewModel (Stateful)
 */
@Composable
fun PromoHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PromoHomeViewModel = viewModel { PromoHomeViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    PromoHomeScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onClearSearch = viewModel::onClearSearch,
        onCategorySelected = viewModel::onCategorySelected,
        onTabSelected = viewModel::onTabSelected,
        onToggleFavorite = viewModel::onToggleFavorite,
        onRefresh = viewModel::onRefresh,
        onPromoClick = { promo ->
            scope.launch {
                snackbarHostState.showSnackbar("Seleccionaste: ${promo.title}")
            }
        },
        modifier = modifier
    )
}

/**
 * Contenido desacoplado para facilitar testing y Previews (Stateless)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoHomeScreenContent(
    uiState: PromoHomeUiState,
    snackbarHostState: SnackbarHostState,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onCategorySelected: (PromoCategory) -> Unit,
    onTabSelected: (Int) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRefresh: () -> Unit,
    onPromoClick: (PromoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Promo70",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (uiState.currentTab) {
                                1 -> "Mis Promociones Guardadas"
                                2 -> "Mi Perfil"
                                else -> "Descuentos de hasta el 70%"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar ofertas"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            PromoBottomBar(
                currentTab = uiState.currentTab,
                favoritesCount = uiState.favoritesCount,
                onTabSelected = onTabSelected
            )
        },
        floatingActionButton = {
            if (uiState.currentTab == 0) {
                FloatingActionButton(
                    onClick = { /* Acción carrito */ },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Ver carrito de compras"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                2 -> ProfileTabContent()
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Buscador
                        PromoSearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = onSearchQueryChange,
                            onClearClick = onClearSearch,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        // Selector de Categorías (solo en pestaña Inicio)
                        if (uiState.currentTab == 0) {
                            CategoryFilterChips(
                                selectedCategory = uiState.selectedCategory,
                                onCategorySelected = onCategorySelected,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        // Banner Promocional
                        if (uiState.currentTab == 0 && uiState.searchQuery.isEmpty() && uiState.selectedCategory == PromoCategory.ALL) {
                            PromoBanner(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                        }

                        // Estados de UI (Loading, Error, Empty, Lista)
                        when {
                            uiState.isLoading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        CircularProgressIndicator()
                                        Text(
                                            text = "Cargando las mejores promociones...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            uiState.errorMessage != null -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Text(
                                            text = uiState.errorMessage,
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center
                                        )
                                        Button(onClick = onRefresh) {
                                            Text("Reintentar")
                                        }
                                    }
                                }
                            }

                            uiState.displayedItems.isEmpty() -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(54.dp)
                                        )
                                        Text(
                                            text = if (uiState.currentTab == 1) {
                                                "Aún no tienes promociones guardadas en favoritos."
                                            } else {
                                                "No se encontraron ofertas que coincidan con tu búsqueda."
                                            },
                                            style = MaterialTheme.typography.titleMedium,
                                            textAlign = TextAlign.Center
                                        )
                                        if (uiState.searchQuery.isNotEmpty() || uiState.selectedCategory != PromoCategory.ALL) {
                                            OutlinedButton(
                                                onClick = {
                                                    onClearSearch()
                                                    onCategorySelected(PromoCategory.ALL)
                                                }
                                            ) {
                                                Text("Limpiar filtros")
                                            }
                                        }
                                    }
                                }
                            }

                            else -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(
                                        items = uiState.displayedItems,
                                        key = { it.id }
                                    ) { promo ->
                                        PromoCard(
                                            promo = promo,
                                            onToggleFavorite = onToggleFavorite,
                                            onPromoClick = onPromoClick
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PromoBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "🔥 ESPECIAL 70% OFF",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Liquidación de temporada",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Aprovecha descuentos únicos en tecnología, moda y hogar antes de que se agoten.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ProfileTabContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(54.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Text(
            text = "Usuario Promo70",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "usuario@promo70.com",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Beneficios Miembro Gold",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• Notificaciones tempranas de ofertas flash\n• Envíos gratis en compras superiores a $30\n• Cupones exclusivos del 15% extra",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PromoHomeScreenPreview() {
    Promo70Theme {
        PromoHomeScreenContent(
            uiState = PromoHomeUiState(
                isLoading = false,
                items = listOf(
                    PromoItem(
                        id = "1",
                        title = "Auriculares Inalámbricos Pro",
                        description = "Cancelación activa de ruido y 30 horas de batería.",
                        price = 49.99,
                        originalPrice = 99.99,
                        discountPercent = 50,
                        category = PromoCategory.TECH,
                        isFavorite = true,
                        tag = "DESTACADO"
                    ),
                    PromoItem(
                        id = "2",
                        title = "Smartwatch Deportivo X",
                        description = "Monitor de ritmo cardíaco y GPS.",
                        price = 69.50,
                        originalPrice = 139.00,
                        discountPercent = 50,
                        category = PromoCategory.TECH
                    )
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onSearchQueryChange = {},
            onClearSearch = {},
            onCategorySelected = {},
            onTabSelected = {},
            onToggleFavorite = {},
            onRefresh = {},
            onPromoClick = {}
        )
    }
}

package com.example.c24productapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c24productapp.data.ProductService
import com.example.c24productapp.model.Product
import com.example.c24productapp.model.ProductResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


enum class ProductFilter(val key: String) {
    ALL("Alle"),
    AVAILABLE("Verfügbar"),
    FAVORITED("Vorgemerkt");

    companion object {
        fun fromKey(key: String): ProductFilter {
            return values().firstOrNull { it.key.equals(key, ignoreCase = true) } ?: ALL
        }
    }
}

class ProductListViewModel : ViewModel() {

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _filters = MutableStateFlow<List<String>>(emptyList())
    private val _selectedFilter = MutableStateFlow(ProductFilter.ALL.key)

    private val _headerTitle = MutableStateFlow("")
    private val _headerSubtitle = MutableStateFlow("")

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    private val _favoriteProducts = MutableStateFlow<Set<Product>>(emptySet())

    private val _isRefreshing = MutableStateFlow(false)
    private val _hasError = MutableStateFlow(false)

    val selectedProduct: StateFlow<Product?> = _selectedProduct
    val favoriteProducts: StateFlow<Set<Product>> = _favoriteProducts
    val isRefreshing: StateFlow<Boolean> = _isRefreshing
    val hasError: StateFlow<Boolean> = _hasError
    val filters: StateFlow<List<String>> = _filters
    val selectedFilter: StateFlow<String> = _selectedFilter
    val headerTitle: StateFlow<String> = _headerTitle
    val headerSubtitle: StateFlow<String> = _headerSubtitle

    // Products filtered by the current selected filter
    val filteredProducts: StateFlow<List<Product>> = combine(
        _allProducts, _selectedFilter, _favoriteProducts
    ) { products, selectedKey, favorites ->
        when (ProductFilter.fromKey(selectedKey)) {
            ProductFilter.ALL -> products
            ProductFilter.AVAILABLE -> products.filter { it.available }
            ProductFilter.FAVORITED -> products.filter { it in favorites }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    private var requestCount = 1
    // Sets the currently selected filter
    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    // Loads products from the API and updates UI state
    fun loadProducts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                if (requestCount ++% 4 == 0) {
                    throw Exception("Simulated API error")
                }
                val response = fetchProducts()
                updateStates(response)
                _hasError.value = false
            } catch (e: Exception) {
                _allProducts.value = emptyList()
                _hasError.value = true
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun refreshProducts() {
        loadProducts()
    }


    fun selectProduct(product: Product) {
        _selectedProduct.value = product
    }

    // Toggles favorite
    fun toggleFavorite(product: Product) {
        _favoriteProducts.update { current ->
            current.toMutableSet().apply {
                if (!add(product)) remove(product)
            }
        }
    }

    // Fetches products from API
    private suspend fun fetchProducts(): ProductResponse {
        return ProductService.api.getProducts()
    }

    // Updates all UI states based on the API response
    private fun updateStates(response: ProductResponse) {
        _allProducts.value = response.products
        _filters.value = response.filters
        _headerTitle.value = response.header.headerTitle
        _headerSubtitle.value = response.header.headerDescription
    }
}


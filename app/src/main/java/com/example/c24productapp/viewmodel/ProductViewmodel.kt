package com.example.c24productapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c24productapp.data.ProductService
import com.example.c24productapp.model.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductListViewModel : ViewModel() {

    // Raw data
    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _filters = MutableStateFlow<List<String>>(emptyList())
    private val _selectedFilter = MutableStateFlow("Alle")

    // Header data
    private val _headerTitle = MutableStateFlow("")
    private val _headerSubtitle = MutableStateFlow("")

    // State flags
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    private val _favoriteProducts = MutableStateFlow<Set<Product>>(emptySet())
    private val _isRefreshing = MutableStateFlow(false)
    private val _hasError = MutableStateFlow(false)

    // Public expose
    val selectedProduct: StateFlow<Product?> = _selectedProduct
    val favoriteProducts: StateFlow<Set<Product>> = _favoriteProducts
    val isRefreshing: StateFlow<Boolean> = _isRefreshing
    val hasError: StateFlow<Boolean> = _hasError
    val filters: StateFlow<List<String>> get() = _filters
    val selectedFilter: StateFlow<String> get() = _selectedFilter
    val headerTitle: StateFlow<String> get() = _headerTitle
    val headerSubtitle: StateFlow<String> get() = _headerSubtitle

    val filteredProducts: StateFlow<List<Product>> = combine(
        _allProducts, _selectedFilter, _favoriteProducts
    ) { products, filter, favorites ->
        when (filter.lowercase()) {
            "verfügbar" -> products.filter { it.available }
            "vorgemerkt" -> products.filter { it in favorites }
            else -> products
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var requestCount = 0 // For simulating error every 3rd request

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun loadProducts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                requestCount++
                if (requestCount % 4 == 0) {
                    throw Exception("Simulated API error")
                }

                val response = ProductService.api.getProducts()
                _allProducts.value = response.products
                _filters.value = response.filters
                _headerTitle.value = response.header.headerTitle
                _headerSubtitle.value = response.header.headerDescription
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
        viewModelScope.launch {
            _isRefreshing.value = true
            _hasError.value = false
            delay(800L)
            loadProducts()
            delay(600L)
            _isRefreshing.value = false
        }
    }

    fun selectProduct(product: Product) {
        _selectedProduct.value = product
    }

    fun toggleFavorite(product: Product) {
        val current = _favoriteProducts.value.toMutableSet()
        if (current.contains(product)) {
            current.remove(product)
        } else {
            current.add(product)
        }
        _favoriteProducts.value = current
    }
}

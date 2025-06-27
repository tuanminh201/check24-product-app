package com.example.c24productapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c24productapp.data.ProductService
import com.example.c24productapp.model.Product
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductListViewModel : ViewModel() {

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _filters = MutableStateFlow<List<String>>(emptyList())
    private val _selectedFilter = MutableStateFlow("Alle")

    private val _headerTitle = MutableStateFlow("")
    private val _headerSubtitle = MutableStateFlow("")
    private val _selectedProduct = MutableStateFlow<Product?>(null)

    private val _favoriteProducts = MutableStateFlow<Set<Product>>(emptySet())

    val selectedProduct: StateFlow<Product?> = _selectedProduct
    val favoriteProducts: StateFlow<Set<Product>> = _favoriteProducts

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

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                val response = ProductService.api.getProducts()
                _allProducts.value = response.products
                _filters.value =response.filters
                _headerTitle.value = response.header.headerTitle
                _headerSubtitle.value = response.header.headerDescription
            } catch (e: Exception) {
                _allProducts.value = emptyList()
            }
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

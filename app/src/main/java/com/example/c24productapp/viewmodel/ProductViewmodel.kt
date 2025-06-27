package com.example.c24productapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c24productapp.data.ProductService
import com.example.c24productapp.model.Product
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

    // Public exposures
    val filters: StateFlow<List<String>> get() = _filters
    val selectedFilter: StateFlow<String> get() = _selectedFilter
    val headerTitle: StateFlow<String> get() = _headerTitle
    val headerSubtitle: StateFlow<String> get() = _headerSubtitle

    // Filtered list based on selected filter
    val filteredProducts: StateFlow<List<Product>> =
        combine(_allProducts, _selectedFilter) { products, filter ->
            when (filter.lowercase()) {
                "verfügbar" -> products.filter { it.available }
                "vorgemerkt" -> products.filter { it.isFavorite }
                else -> products
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                val response = ProductService.api.getProducts()
                _allProducts.value = response.products
                _filters.value = response.filters
                _headerTitle.value = response.header.headerTitle
                _headerSubtitle.value = response.header.headerDescription
            } catch (e: Exception) {
                // You can add error handling here
                _allProducts.value = emptyList()
            }
        }
    }
}

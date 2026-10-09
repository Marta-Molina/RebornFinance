package com.example.rebornfinance.feature.projects

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.repository.RebornProjectRepositoryImpl
import com.example.rebornfinance.domain.model.ProjectStatus
import com.example.rebornfinance.domain.model.RebornProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ProjectsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = RebornProjectRepositoryImpl(database.rebornProjectDao(), database.projectCostDao())

    private val allProjects = repository.getAllProjects()

    val searchQuery = MutableStateFlow("")
    val selectedStatusFilter = MutableStateFlow<ProjectStatus?>(null)

    val filteredProjects: StateFlow<List<RebornProject>> = combine(
        allProjects,
        searchQuery,
        selectedStatusFilter
    ) { projects, query, statusFilter ->
        projects.filter { proj ->
            val matchesQuery = query.isBlank() ||
                    proj.name.contains(query, ignoreCase = true) ||
                    proj.kitName.contains(query, ignoreCase = true) ||
                    proj.sculptorName.contains(query, ignoreCase = true)

            val matchesStatus = statusFilter == null || proj.status == statusFilter

            matchesQuery && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setStatusFilter(status: ProjectStatus?) {
        selectedStatusFilter.value = status
    }
}

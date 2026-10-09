package com.example.rebornfinance.domain.repository

import com.example.rebornfinance.domain.model.ProjectCost
import com.example.rebornfinance.domain.model.RebornProject
import kotlinx.coroutines.flow.Flow

interface RebornProjectRepository {
    fun getAllProjects(): Flow<List<RebornProject>>
    suspend fun getProjectById(id: Long): RebornProject?
    suspend fun insertProject(project: RebornProject): Long
    suspend fun updateProject(project: RebornProject)
    suspend fun deleteProject(project: RebornProject)

    fun getCostsForProject(projectId: Long): Flow<List<ProjectCost>>
    suspend fun getCostById(id: Long): ProjectCost?
    suspend fun insertCost(cost: ProjectCost): Long
    suspend fun updateCost(cost: ProjectCost)
    suspend fun deleteCost(cost: ProjectCost)
}

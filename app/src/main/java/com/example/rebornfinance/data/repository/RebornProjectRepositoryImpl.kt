package com.example.rebornfinance.data.repository

import com.example.rebornfinance.data.local.dao.ProjectCostDao
import com.example.rebornfinance.data.local.dao.RebornProjectDao
import com.example.rebornfinance.data.local.entity.ProjectCostEntity
import com.example.rebornfinance.data.local.entity.RebornProjectEntity
import com.example.rebornfinance.domain.model.ProjectCost
import com.example.rebornfinance.domain.model.RebornProject
import com.example.rebornfinance.domain.repository.RebornProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RebornProjectRepositoryImpl(
    private val projectDao: RebornProjectDao,
    private val costDao: ProjectCostDao
) : RebornProjectRepository {

    override fun getAllProjects(): Flow<List<RebornProject>> {
        return projectDao.getAllProjects().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProjectById(id: Long): RebornProject? {
        return projectDao.getProjectById(id)?.toDomain()
    }

    override suspend fun insertProject(project: RebornProject): Long {
        return projectDao.insertProject(RebornProjectEntity.fromDomain(project))
    }

    override suspend fun updateProject(project: RebornProject) {
        projectDao.updateProject(RebornProjectEntity.fromDomain(project))
    }

    override suspend fun deleteProject(project: RebornProject) {
        projectDao.deleteProject(RebornProjectEntity.fromDomain(project))
    }

    override fun getCostsForProject(projectId: Long): Flow<List<ProjectCost>> {
        return costDao.getCostsForProject(projectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCostById(id: Long): ProjectCost? {
        return costDao.getCostById(id)?.toDomain()
    }

    override suspend fun insertCost(cost: ProjectCost): Long {
        return costDao.insertCost(ProjectCostEntity.fromDomain(cost))
    }

    override suspend fun updateCost(cost: ProjectCost) {
        costDao.updateCost(ProjectCostEntity.fromDomain(cost))
    }

    override suspend fun deleteCost(cost: ProjectCost) {
        costDao.deleteCost(ProjectCostEntity.fromDomain(cost))
    }
}

package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rebornfinance.data.local.entity.ProjectEyeAssignmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectEyeAssignmentDao {
    @Query("SELECT * FROM project_eye_assignments WHERE projectId = :projectId")
    fun getAssignmentsForProject(projectId: Long): Flow<List<ProjectEyeAssignmentEntity>>

    @Query("SELECT * FROM project_eye_assignments WHERE id = :id")
    suspend fun getAssignmentById(id: Long): ProjectEyeAssignmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: ProjectEyeAssignmentEntity): Long

    @Update
    suspend fun updateAssignment(assignment: ProjectEyeAssignmentEntity)

    @Delete
    suspend fun deleteAssignment(assignment: ProjectEyeAssignmentEntity)
}

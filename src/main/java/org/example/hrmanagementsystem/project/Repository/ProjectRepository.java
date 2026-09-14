package org.example.hrmanagementsystem.project.Repository;

import org.example.hrmanagementsystem.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository <Project , Long>  , JpaSpecificationExecutor <Project> {
    @Query(value = "SELECT * FROM Project p WHERE p.project_id = :id" , nativeQuery = true)
    Optional<Project> findByIncludingDeleted(@Param("id") Long id);

    List<Project> findTop5ByOrderByCreatedDateDesc();

    @Query("""
SELECT COUNT(p) FROM Project p WHERE p.manager.userId = :managerId
""")
    long countByManagerId(
            @Param("managerId") Long managerId);

    @Query("""
SELECT COUNT (DISTINCT e.employeeId)
FROM Project p
JOIN p.employees e
WHERE p.manager.userId = :managerId
""")
    long countEmployeesByManagerId(@Param("managerId") Long managerId);

    @Query(""" 
SELECT COUNT (DISTINCT p)
FROM Project p
JOIN p.employees e
WHERE e.employeeId = :employeeId
""")
    long countProjectsByEmployeesId(@Param("employeeId") Long employeeId);

}

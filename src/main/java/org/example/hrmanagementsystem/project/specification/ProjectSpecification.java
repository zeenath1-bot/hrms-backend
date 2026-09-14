package org.example.hrmanagementsystem.project.specification;

import org.example.hrmanagementsystem.project.model.Project;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecification {
    public static Specification<Project> nameLike(String searchKeyword) {
        return (root, query, criteriaBuilder) -> {
            if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction(); //no filter return all
            } else {
                String keyword = "%" + searchKeyword.trim().toLowerCase() + "%";
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("projectName")), keyword);

            }
        };
    }

    public static Specification<Project> hasManager(Long managerId){
        return (root, query, criteriaBuilder) -> {
            if(managerId == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("manager").get("userId") , managerId);
        };
    }

}

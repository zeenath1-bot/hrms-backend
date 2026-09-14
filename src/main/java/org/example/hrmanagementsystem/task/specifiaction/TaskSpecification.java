package org.example.hrmanagementsystem.task.specifiaction;

import org.example.hrmanagementsystem.task.entity.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {

    public static Specification<Task> nameLike(String searchKeyword) {
        return (root, query, cb) -> {
            if(searchKeyword == null || searchKeyword.trim().isEmpty()) {
                return cb.conjunction();
            }else {
                String keyword = "%" + searchKeyword.trim().toLowerCase() + "%" ;
                return cb.like(cb.lower(root.get("taskTitle")), keyword );
            }

        };
    }

    public static Specification<Task> inProject(Long projectId){
        return (root, query, criteriaBuilder) ->{
            if(projectId == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("project").get("projectId"), projectId);
        } ;
    }
}
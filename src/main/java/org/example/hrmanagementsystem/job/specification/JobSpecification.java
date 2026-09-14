package org.example.hrmanagementsystem.job.specification;

import org.example.hrmanagementsystem.job.model.Job;
import org.springframework.data.jpa.domain.Specification;

public class JobSpecification {
    public static Specification<Job> nameLike(String searchKeyword) {
        return (root, query, criteriaBuilder) -> {
            if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            } else {
                String keyword = "%" + searchKeyword.trim().toLowerCase() + "%";
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("jobTitle")), keyword);
            }
        };
    }
}

package org.example.hrmanagementsystem.department.specification;

import org.example.hrmanagementsystem.department.model.Department;
import org.springframework.data.jpa.domain.Specification;

public class DepartmentSpecification {
    public static Specification<Department> nameLike(String searchKeyword) {
        return (root, query, cb) -> {
            if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
                return cb.conjunction();
            } else {
                String keyword = "%" + searchKeyword.trim().toLowerCase() + "%";
                return cb.like(cb.lower(root.get("deptName")), keyword);

            }
        };
    }

    public static Specification<Department> hasAssignedManager(Boolean hasManager) {
        return (root, query, cb) -> {
            if (hasManager == null) {
                return cb.conjunction(); // an always-true predicate , meaning "skip the filter"
            }
            return hasManager
                    ?cb.isNotNull(root.get("manager"))
                    : cb.isNull(root.get("manager"));


        };
    }
}
package org.example.hrmanagementsystem.admin.specification;

import org.example.hrmanagementsystem.auth.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification <User> nameLike(String searchKeyword){
        return (root, query, criteriaBuilder) -> {
            if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }else {
                String keyword = "%" + searchKeyword.trim().toLowerCase() + "%" ;
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), keyword );
            }
        };
    }

    public static Specification<User> isActive (Boolean active){
        return (root, query, criteriaBuilder) -> {
            if( active == null){
                return criteriaBuilder.conjunction();
            }else {
                return criteriaBuilder.equal(root.get("active"), active);
            }

        };
    }
}

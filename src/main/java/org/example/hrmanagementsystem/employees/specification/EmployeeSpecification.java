package org.example.hrmanagementsystem.employees.specification;

import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.enums.StatusType;
import org.springframework.data.jpa.domain.Specification;


public class EmployeeSpecification {

    public static Specification<Employee> nameLike (String searchKeyword){
        return (root, query, cb) -> {
            if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
                return cb.conjunction();
            } else {
                String keyword = "%" + searchKeyword.trim().toLowerCase() + "%";
                return cb.or(cb.like(cb.lower(root.get("firstName")), keyword),
                        cb.like(cb.lower(root.get("lastName")), keyword));
            }
        };
    }

    public static Specification<Employee> hasStatus(StatusType statusType){
        return (root, query, criteriaBuilder) -> {
            if(statusType == null){
                return criteriaBuilder.conjunction();
            }return criteriaBuilder.equal(root.get("status") , statusType);

        };
    }


    public static Specification<Employee> inDepartment(Long departmentId){
        return (root, query, criteriaBuilder) -> {
            if(departmentId == null){
                return criteriaBuilder.conjunction();
            }return criteriaBuilder.equal(root.get("department").get("deptId"), departmentId);
        };
    }


    public static Specification<Employee> hasJob(Long jobId){
        return (root, query, criteriaBuilder) -> {
            if(jobId == null){
                return criteriaBuilder.conjunction();
            }return  criteriaBuilder.equal(root.get("job").get("id") , jobId);
        } ;
    }


}




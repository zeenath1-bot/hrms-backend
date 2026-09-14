package org.example.hrmanagementsystem.department.service;

import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.enums.StatusType;
import org.example.hrmanagementsystem.exception.BusinessException;
import org.example.hrmanagementsystem.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.department.dto.DepartmentRequestDTO;
import org.example.hrmanagementsystem.department.dto.DepartmentResponseDTO;
import org.example.hrmanagementsystem.department.model.Department;
import org.example.hrmanagementsystem.department.repository.DepartmentRepository;
import org.example.hrmanagementsystem.department.specification.DepartmentSpecification;
import org.example.hrmanagementsystem.employees.Repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    private Department toEntity (DepartmentRequestDTO dto){
        Department dept = new Department();
        dept.setDeptName(dto.getDeptName());
        if(dto.getManagerId() != null) {
            dept.setManager(validateManager(dto.getManagerId()));
        }

        return dept;
    }

    private DepartmentResponseDTO toDto(Department dept){
        return  DepartmentResponseDTO.builder()
                .deptId(dept.getDeptId())
                .deptName(dept.getDeptName())
                .managerId(dept.getManager() != null ?dept.getManager().getEmployeeId():null)
                .managerName(dept.getManager() != null ?dept.getManager().getFirstName()+ " " + dept.getManager().getLastName(): null)
                .build();
    }

    public DepartmentResponseDTO saveDepartment(DepartmentRequestDTO dto){
        Department dept = toEntity(dto);
        Department savedDept = departmentRepository.save(dept);
        return toDto(savedDept);
    }


    public DepartmentResponseDTO getDepartmentById(Long id){
        Department department = departmentRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Department not found with id: " + id
                ));
        return toDto(department);
    }


    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DepartmentResponseDTO updateDepartment(Long id , DepartmentRequestDTO dto){
        Department dept = findDepartmentById(id);
        dept.setDeptName(dto.getDeptName());
        if(dto.getManagerId() != null){
            dept.setManager(validateManager(dto.getManagerId()));
        }
        Department updatedDept = departmentRepository.save(dept);
        return toDto(updatedDept);
    }

    public DepartmentResponseDTO updateDepartmentbyFields(Long id , DepartmentRequestDTO dto){
        Department department = departmentRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Department not found"));
        if(dto.getDeptName() != null) department.setDeptName(dto.getDeptName());
        if(dto.getManagerId() != null) {
            department.setManager(validateManager(dto.getManagerId()));
        }
        return toDto(departmentRepository.save(department));

    }

    private Department findDepartmentById(Long id){
        return departmentRepository.findById(id)
                .orElseThrow(()->
                        new ResourceNotFoundException("Department not found with id: "+id));

    }

    public void deleteDept(Long id){
        Department dept = findDepartmentById(id);
        long employeeCount =
                employeeRepository.countByDepartmentDeptId(id);
        if (employeeCount > 0) {
            throw new BusinessException(
                    "Department cannot be deleted because employees are assigned to it."
            );
        }


        departmentRepository.delete(dept);
    }

    public Page<DepartmentResponseDTO> searchDepartments(String dname , Boolean hasManager ,Pageable pageable) {
        Specification<Department> spec = Specification
                .where(DepartmentSpecification.nameLike(dname))
                .and(DepartmentSpecification.hasAssignedManager(hasManager));

        Page<Department> departmentPage = departmentRepository.findAll(spec , pageable);
        return departmentPage.map(this::toDto);
    }

    private Employee validateManager (Long managerId){
        if (managerId == null){
            return null;
        }
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + managerId));
        if (manager.getStatus() != StatusType.ACTIVE){
            throw new BusinessException("Only active employees can be assigned as department managers."
            );
        }
        return manager;
    }
}

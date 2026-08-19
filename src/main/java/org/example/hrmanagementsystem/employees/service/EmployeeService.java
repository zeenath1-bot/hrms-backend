package org.example.hrmanagementsystem.employees.service;

import org.example.hrmanagementsystem.employees.specification.EmployeeSpecification;
import org.example.hrmanagementsystem.enums.StatusType;
import org.example.hrmanagementsystem.exception.BusinessException;
import org.example.hrmanagementsystem.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.department.model.Department;
import org.example.hrmanagementsystem.department.repository.DepartmentRepository;
import org.example.hrmanagementsystem.employees.Repository.EmployeeRepository;
import org.example.hrmanagementsystem.employees.dto.EmployeeRequestDTO;
import org.example.hrmanagementsystem.employees.dto.EmployeeResponseDTO;
import org.example.hrmanagementsystem.employees.model.Employee;
import org.example.hrmanagementsystem.job.model.Job;
import org.example.hrmanagementsystem.job.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private  final JobRepository jobRepository;

    private Employee toEntity(EmployeeRequestDTO dto){
        Employee employee = new Employee();
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setDateofbirth(dto.getDateofbirth());
        employee.setJoinDate(dto.getJoinDate());
        employee.setExitdate(dto.getExitDate());
        employee.setStatus(dto.getStatus());
        employee.setEmail(dto.getEmail());
        employee.setPhoneNumber(dto.getPhonenumber());
        employee.setSalary(dto.getSalary());

        if(dto.getDeptId() != null){
            Department department = departmentRepository.findById(dto.getDeptId())
                    .orElseThrow(()-> new ResourceNotFoundException("Department not found with id: "+ dto.getDeptId()));
            employee.setDepartment(department);
        }

        if(dto.getJobId() != null){
            Job job = jobRepository.findById(dto.getJobId())
                    .orElseThrow(()-> new ResourceNotFoundException("Job title not found with id: " + dto.getJobId()));

            if(dto.getSalary() != null && job.getMinSalary() != null && job.getMaxSalary() != null){
                if (dto.getSalary().compareTo(job.getMinSalary()) < 0) {
                    throw new BusinessException(
                            "Salary cannot be less than minimum salary: " + job.getMinSalary() + " for job: "+ job.getJobTitle()
                    );
                }
                if(dto.getSalary().compareTo(job.getMaxSalary()) > 0) {
                    throw new BusinessException(
                            "Salary cannot exceed maximum salary: "
                            + job.getMaxSalary() + " for job: " + job.getJobTitle()
                    );
                }
            }
            employee.setJob(job);
        }

        return employee;
    }

    private EmployeeResponseDTO toDto (Employee employee){
        return EmployeeResponseDTO.builder()
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .dateofbirth(employee.getDateofbirth())
                .joinDate(employee.getJoinDate())
                .exitDate(employee.getExitdate())
                .status(employee.getStatus())
                .email(employee.getEmail())
                .phonenumber(employee.getPhoneNumber())
                .salary(employee.getSalary())
                .deptId(employee.getDepartment().getDeptId())
                .jobId(employee.getJob().getId())
                .build();
    }

    public EmployeeResponseDTO saveEmployee(EmployeeRequestDTO dto){
        Employee employee = toEntity(dto);
        Employee savedemployee = employeeRepository.save(employee);
        return toDto(savedemployee);
    }

    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " +id
                ));
        return toDto(employee);
    }


    public List<EmployeeResponseDTO> getAllemployees() {
       return employeeRepository.findAll()
               .stream()
               .map(this::toDto)
               .collect(Collectors.toList());

    }
    public EmployeeResponseDTO updateEmployees(Long id , EmployeeRequestDTO dto){
        Employee employee = findEmployeeById(id);
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setDateofbirth(dto.getDateofbirth());
        employee.setJoinDate(dto.getJoinDate());
        employee.setExitdate(dto.getExitDate());
        employee.setStatus(dto.getStatus());
        employee.setEmail(dto.getEmail());
        employee.setPhoneNumber(dto.getPhonenumber());
        employee.setSalary(dto.getSalary());

        if(dto.getJobId() != null){
            employee.setJob(
                    jobRepository.findById(dto.getJobId())
                            .orElseThrow(()-> new IllegalArgumentException("Entered jobId not valid")));
        }

        if(dto.getDeptId() != null){
            employee.setDepartment(
                    departmentRepository.findById(dto.getDeptId())
                            .orElseThrow(()-> new IllegalArgumentException("Entered departmentId not valid")));
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return toDto(updatedEmployee);
    }

    public EmployeeResponseDTO updateEmployeeFields(Long id , EmployeeRequestDTO dto){

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Employee not found"));

        if(dto.getFirstName() != null) employee.setFirstName(dto.getFirstName());
        if(dto.getLastName() != null) employee.setLastName(dto.getLastName());
        if(dto.getDateofbirth() != null) employee.setDateofbirth(dto.getDateofbirth());
        if(dto.getJoinDate() != null) employee.setJoinDate(dto.getJoinDate());
        if(dto.getExitDate() != null) employee.setExitdate(dto.getExitDate());
        if(dto.getEmail() != null) employee.setEmail(dto.getEmail());
        if(dto.getPhonenumber() != null) employee.setPhoneNumber(dto.getPhonenumber());
        if(dto.getStatus() != null) employee.setStatus(dto.getStatus());
        if(dto.getSalary() != null) employee.setSalary(dto.getSalary());
        if(dto.getDeptId() != null){
           Department department = departmentRepository.findById(dto.getDeptId())
                   .orElseThrow(()-> new ResourceNotFoundException("Department not found"));
           employee.setDepartment(department);
        }
        if(dto.getJobId() != null){
            Job job = jobRepository.findById(dto.getJobId())
                    .orElseThrow(()-> new ResourceNotFoundException("JobId not found"));
            employee.setJob(job);
        }
        return toDto(employeeRepository.save(employee));
    }

    public Employee findEmployeeById(Long id){
        return employeeRepository.findById(id)
                .orElseThrow(()->
                        new ResourceNotFoundException("Employee not found with id: "+id));

    }
    public void deleteEmployee(Long id){
        Employee employee = findEmployeeById(id);
        employeeRepository.delete(employee);
    }

    public Page<EmployeeResponseDTO> searchEmployees(String ename , StatusType statusType , Long departmentId , Long jobId , Pageable pageable){
        Specification<Employee> spec = Specification
                .where(EmployeeSpecification.nameLike(ename))
                .and(EmployeeSpecification.hasStatus(statusType))
                .and(EmployeeSpecification.inDepartment(departmentId))
                .and(EmployeeSpecification.hasJob(jobId));


        Page<Employee> employeePage = employeeRepository.findAll(spec , pageable);
        return employeePage.map(this::toDto);
    }


}

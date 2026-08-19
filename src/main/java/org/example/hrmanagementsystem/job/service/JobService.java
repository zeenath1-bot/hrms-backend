package org.example.hrmanagementsystem.job.service;

import org.example.hrmanagementsystem.exception.BusinessException;
import org.example.hrmanagementsystem.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.employees.Repository.EmployeeRepository;
import org.example.hrmanagementsystem.job.dto.JobRequestDTO;
import org.example.hrmanagementsystem.job.dto.JobResponseDTO;
import org.example.hrmanagementsystem.job.model.Job;
import org.example.hrmanagementsystem.job.repository.JobRepository;
import org.example.hrmanagementsystem.job.specification.JobSpecification;
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
public class JobService {
    private final JobRepository jobRepository;


    private Job toEntity (JobRequestDTO dto){
        Job job = new Job();
        job.setJobTitle(dto.getJobTitle());
        job.setJobCode(dto.getJobCode());
        job.setMinSalary(dto.getMinSalary());
        job.setMaxSalary(dto.getMaxSalary());

        if(dto.getMinSalary().compareTo(dto.getMaxSalary())>0){
            throw new BusinessException(
                    "Minimum salary cannot be greater than maximum salary"
            );
        }

        return job;
    }

    private JobResponseDTO toDto(Job job) {
        return JobResponseDTO.builder()
                .jobId(job.getId())
                .jobTitle(job.getJobTitle())
                .jobCode(job.getJobCode())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .build();
    }

    public JobResponseDTO saveJob(JobRequestDTO dto){
        Job job = toEntity(dto);
        Job savedjob = jobRepository.save(job);
        return toDto(savedjob);
    }

    public JobResponseDTO getJobById(Long id){
        Job job = jobRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Job role not found with id: " + id
                ));
        return toDto(job);
    }
    public List<JobResponseDTO> getAllJob(){
        return jobRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    private Job findJobById(Long id){
        return jobRepository.findById(id)
                .orElseThrow(()->
                        new ResourceNotFoundException("Job not found with id: "+ id));
    }
    public JobResponseDTO updatejob(Long id , JobRequestDTO dto){
        Job job = findJobById(id);
        job.setJobTitle(dto.getJobTitle());
        job.setJobCode(dto.getJobCode());
        job.setMinSalary(dto.getMinSalary());
        job.setMaxSalary(dto.getMaxSalary());

        Job updatedJob = jobRepository.save(job);
        return toDto(updatedJob);
    }

    public JobResponseDTO updateJobByfields(Long id , JobRequestDTO dto){
        Job job = jobRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Job Id  not found"));
        if(dto.getJobTitle() != null) job.setJobTitle(dto.getJobTitle());
        if(dto.getJobCode() != null) job.setJobCode(dto.getJobCode());
        if(dto.getMinSalary() != null) job.setMinSalary(dto.getMinSalary());
        if(dto.getMaxSalary() != null) job.setMaxSalary(dto.getMaxSalary());

        return toDto(jobRepository.save(job));
    }

    public void deleteJob(Long id){
        Job job = findJobById(id);
        jobRepository.delete(job);
    }

    public Page<JobResponseDTO> searchJob (String jname , Pageable pageable){
        Specification<Job> spec = Specification
                .where(JobSpecification.nameLike(jname));

        Page<Job> jobPage = jobRepository.findAll(spec , pageable);
        return jobPage.map(this::toDto);
    }


}

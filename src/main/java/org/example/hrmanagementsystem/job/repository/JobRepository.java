package org.example.hrmanagementsystem.job.repository;

import org.example.hrmanagementsystem.job.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository <Job, Long> , JpaSpecificationExecutor<Job> {
}

package com.example.ZoomindAssignment.Repository;


import com.example.ZoomindAssignment.Enums.Priority;
import com.example.ZoomindAssignment.Enums.Status;
import com.example.ZoomindAssignment.Models.TestCaseModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



@Repository
public interface TestCaseRepository extends MongoRepository<TestCaseModel,String> {
    Page<TestCaseModel> findByStatusAndPriority(Status status, Priority priority, Pageable pageable);
    Page<TestCaseModel> findByStatus(Status status, Pageable pageable);
    Page<TestCaseModel> findByPriority(Priority priority, Pageable pageable);
}

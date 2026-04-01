package com.capstone.demo.ropositary;

import com.capstone.demo.model.IssueReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueReportRepository extends JpaRepository<IssueReport, Long> {

    List<IssueReport> findByReportedBy_Id(String userId);

    List<IssueReport> findByAssignedOfficer_Id(String officerId);

    List<IssueReport> findByStatus(String status);
    List<IssueReport> findByCategory(String category);
}

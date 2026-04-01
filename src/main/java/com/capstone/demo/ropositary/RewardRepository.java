package com.capstone.demo.ropositary;

import com.capstone.demo.model.Reward;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {

    @Transactional
    void deleteByExpiryDateBefore(LocalDate today);
    List<Reward> findByStatus(String status);
	List<Reward> findByStatusAndQuantityGreaterThan(String string, int i);
	List<Reward> findBySponsorId(String sponsorId);
}



package com.capstone.demo.ropositary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstone.demo.model.Sponsor;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, String> {
}

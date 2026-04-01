package com.capstone.demo.ropositary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capstone.demo.model.MunicipalOfficer;

@Repository
public interface MunicipalOfficerRepository extends JpaRepository<MunicipalOfficer, String> {

    

}


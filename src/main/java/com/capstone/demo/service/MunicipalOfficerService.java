package com.capstone.demo.service;

import com.capstone.demo.model.MunicipalOfficer;
import java.util.List;

public interface MunicipalOfficerService {

    MunicipalOfficer createOfficer(MunicipalOfficer officer);

    MunicipalOfficer getOfficerById(String id);

    List<MunicipalOfficer> getAllOfficers();

    MunicipalOfficer updateOfficer(String id, MunicipalOfficer updated);

    void deleteOfficer(String id);
}

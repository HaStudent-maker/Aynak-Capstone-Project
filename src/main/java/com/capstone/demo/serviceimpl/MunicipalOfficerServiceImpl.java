package com.capstone.demo.serviceimpl;

import com.capstone.demo.model.MunicipalOfficer;
import com.capstone.demo.ropositary.MunicipalOfficerRepository;
import com.capstone.demo.service.MunicipalOfficerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MunicipalOfficerServiceImpl implements MunicipalOfficerService {

    private final MunicipalOfficerRepository municipalOfficerRepository;

    public MunicipalOfficerServiceImpl(MunicipalOfficerRepository municipalOfficerRepository) {
        this.municipalOfficerRepository = municipalOfficerRepository;
    }

    @Override
    public MunicipalOfficer createOfficer(MunicipalOfficer officer) {
        return municipalOfficerRepository.save(officer);
    }

    @Override
    public MunicipalOfficer getOfficerById(String id) {
        Optional<MunicipalOfficer> officer = municipalOfficerRepository.findById(id);
        return officer.orElse(null);
    }

    @Override
    public List<MunicipalOfficer> getAllOfficers() {
        return municipalOfficerRepository.findAll();
    }

    @Override
    public MunicipalOfficer updateOfficer(String id, MunicipalOfficer updated) {
        MunicipalOfficer existing = getOfficerById(id);
        if (existing == null) return null;

        existing.setFullName(updated.getFullName());
        existing.setEmail(updated.getEmail());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setDepartmentName(updated.getDepartmentName());
        

        return municipalOfficerRepository.save(existing);
    }

    @Override
    public void deleteOfficer(String id) {
        municipalOfficerRepository.deleteById(id);
    }
}

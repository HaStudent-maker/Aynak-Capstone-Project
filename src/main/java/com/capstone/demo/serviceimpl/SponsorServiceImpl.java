package com.capstone.demo.serviceimpl;

import com.capstone.demo.model.Sponsor;
import com.capstone.demo.ropositary.SponsorRepository;
import com.capstone.demo.service.SponsorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SponsorServiceImpl implements SponsorService {

    private final SponsorRepository sponsorRepository;

    public SponsorServiceImpl(SponsorRepository sponsorRepository) {
        this.sponsorRepository = sponsorRepository;
    }

    @Override
    public Sponsor createSponsor(Sponsor sponsor) {
        return sponsorRepository.save(sponsor);
    }

    @Override
    public Sponsor updateSponsor(String id, Sponsor updatedSponsor) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));

        sponsor.setCompanyName(updatedSponsor.getCompanyName());
        sponsor.setAddress(updatedSponsor.getAddress());
        sponsor.setPhone(updatedSponsor.getPhone());

        return sponsorRepository.save(sponsor);}

    @Override
    public Sponsor getSponsorById(String id) {
        return sponsorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sponsor not found"));
    }

    @Override
    public List<Sponsor> getAllSponsors() {
        return sponsorRepository.findAll();
    }

    @Override
    public void deleteSponsor(String id) {
        sponsorRepository.deleteById(id);
    }
}

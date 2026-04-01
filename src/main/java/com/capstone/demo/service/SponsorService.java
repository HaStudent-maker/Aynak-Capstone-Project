package com.capstone.demo.service;

import com.capstone.demo.model.Sponsor;
import java.util.List;

public interface SponsorService {

    Sponsor createSponsor(Sponsor sponsor);

    Sponsor updateSponsor(String id, Sponsor sponsor);

    Sponsor getSponsorById(String id);

    List<Sponsor> getAllSponsors();

    void deleteSponsor(String id);
}

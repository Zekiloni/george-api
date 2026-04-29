package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.in.campaign.CampaignQueryUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampaignQueryService implements CampaignQueryUseCase {
    private final CampaignRepositoryPort repository;

    @Override
    public Optional<Campaign> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Page<Campaign> findAll(Pageable pageable) {
        // TODO: implement paginated query in repository
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

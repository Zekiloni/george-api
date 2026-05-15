package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.in.campaign.OutreachKickUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutreachKickService implements OutreachKickUseCase {

    private final OutreachRepositoryPort outreachRepository;

    @Override
    @Transactional
    public void kick(String token, String reason) {
        // Anonymous endpoint — look up across tenants, same as session create.
        outreachRepository.findBySessionTokenAcrossTenants(token).ifPresentOrElse(outreach -> {
            // Don't overwrite a prior kick — first verdict wins so retries
            // don't reset the diagnosis.
            if (outreach.getKickReason() == null) {
                outreach.setKickReason(reason);
                outreachRepository.save(outreach);
                log.info("Recorded kick on outreach {} with reason {}", outreach.getId(), reason);
            }
        }, () -> log.warn("Kick reported for unknown token (ignored)"));
    }
}

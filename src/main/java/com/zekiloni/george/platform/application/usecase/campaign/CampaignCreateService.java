package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.platform.application.port.in.campaign.CampaignCreateUseCase;
import com.zekiloni.george.platform.application.port.out.CampaignRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import com.zekiloni.george.platform.domain.service.campaign.TokenGenerationStrategy;
import com.zekiloni.george.platform.domain.util.PhoneNumberFileReader;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignCreateService implements CampaignCreateUseCase {
    private final CampaignRepositoryPort repository;

    @Override
    public Campaign handle(Campaign campaign, InputStream file) {
        try {
            // TODO: Later make generic resolver, for now just read phone numbers and create outreach
            campaign.setOutreach(getPhoneNumbers(file).stream()
                    .map(recipient -> buildOutreach(campaign, recipient))
                    .toList());

            return repository.save(campaign);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Outreach buildOutreach(Campaign campaign, String recipient) {
        String token = campaign.getTokenStrategy().generate(campaign.getTokenLength());
        return Outreach.builder()
                .recipient(recipient)
                // TODO: Make message template more generic, for now just replace {{token}} with generated token
                // if we want to support more complex templates in the future, we can use a templating engine like Thymeleaf or FreeMarker
                // token should be link to the external page with the token as a parameter or internal url to our page
                .message(campaign.getMessageTemplate().replace("{{token}}", token))
                .sessionToken(token)
                .status(OutreachStatus.SCHEDULED)
                .build();
    }

    private Set<String> getPhoneNumbers(InputStream file) throws IOException {
        return PhoneNumberFileReader.streamPhoneNumbers(file).collect(Collectors.toSet());
    }
}

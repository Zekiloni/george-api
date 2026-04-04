package com.zekiloni.george.infrastructure.input.web.billing.mapper;

import com.zekiloni.george.domain.billing.model.Subscription;
import com.zekiloni.george.infrastructure.input.web.billing.dto.SubscriptionCreateDto;
import com.zekiloni.george.infrastructure.input.web.billing.dto.SubscriptionDto;
import org.mapstruct.Mapper;

@Mapper
public interface SubscriptionDtoMapper {
    Subscription toDomain(SubscriptionCreateDto dto);

    SubscriptionDto toResponse(Subscription subscription);
}


package com.zekiloni.george.platform.infrastructure.out.persistence.page.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.page.entity.tracking.TrackingEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrackingEventJpaRepository extends JpaRepository<TrackingEventEntity, UUID> {
    List<TrackingEventEntity> findByTrackingLinkId(UUID trackingLinkId);
    List<TrackingEventEntity> findByTrackingLinkIdOrderByEventTimestampDesc(UUID trackingLinkId);
}


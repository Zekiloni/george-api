package com.zekiloni.george.platform.infrastructure.out.persistence.lead.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface LeadJpaRepository extends JpaRepository<LeadEntity, UUID>, JpaSpecificationExecutor<LeadEntity> {

    /**
     * Random-order selection of leads from the global pool, filtered by any
     * non-null characteristic. Used by lead provisioning when an order item
     * specifies country/area/carrier/etc.; null parameters are ignored.
     * Native because we rely on Postgres' {@code random()}.
     */
    @Query(value = """
            SELECT * FROM leads
            WHERE (:country     IS NULL OR LOWER(country)     = LOWER(CAST(:country AS text)))
              AND (:areaCode    IS NULL OR LOWER(area_code)   = LOWER(CAST(:areaCode AS text)))
              AND (:regionCode  IS NULL OR LOWER(region_code) = LOWER(CAST(:regionCode AS text)))
              AND (:carrier     IS NULL OR LOWER(carrier)     = LOWER(CAST(:carrier AS text)))
              AND (:location    IS NULL OR LOWER(location)    LIKE LOWER(CAST(:location AS text)))
            ORDER BY random()
            LIMIT :limit
            """, nativeQuery = true)
    List<LeadEntity> findRandom(@Param("country") String country,
                                @Param("areaCode") String areaCode,
                                @Param("regionCode") String regionCode,
                                @Param("carrier") String carrier,
                                @Param("location") String location,
                                @Param("limit") int limit);

    List<LeadEntity> findByPhoneNumberIn(Collection<String> phoneNumbers);
}

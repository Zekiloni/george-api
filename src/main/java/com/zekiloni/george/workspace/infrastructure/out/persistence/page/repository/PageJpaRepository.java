package com.zekiloni.george.workspace.infrastructure.out.persistence.page.repository;

import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PageJpaRepository extends JpaRepository<PageEntity, UUID> {
}

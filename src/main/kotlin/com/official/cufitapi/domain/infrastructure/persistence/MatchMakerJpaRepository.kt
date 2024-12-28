package com.official.cufitapi.domain.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface MatchMakerJpaRepository : JpaRepository<MatchMakerEntity, Long> {
    fun findByMemberId(memberId: Long) : MatchMakerEntity?
}
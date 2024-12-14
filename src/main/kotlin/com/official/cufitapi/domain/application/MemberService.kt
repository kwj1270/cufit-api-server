package com.official.cufitapi.domain.application

import com.official.cufitapi.common.exception.InvalidRequestException
import com.official.cufitapi.domain.api.dto.MemberInfoResponse
import com.official.cufitapi.domain.api.dto.MemberProfileRequest
import com.official.cufitapi.domain.enums.MemberType
import com.official.cufitapi.domain.infrastructure.repository.InvitationJpaRepository
import com.official.cufitapi.domain.infrastructure.repository.MemberJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberJpaRepository: MemberJpaRepository,
    private val invitationJpaRepository: InvitationJpaRepository
) {
    fun getMemberInfo(memberId: Long): MemberInfoResponse {
        val member = memberJpaRepository.findById(memberId)
            .orElseThrow { InvalidRequestException("존재하지 않는 사용자 id: $memberId") }
        val invitee = memberJpaRepository.findById(member.inviteeId)
            .orElseThrow { InvalidRequestException("존재하지 않는 사용자 id: $memberId") }
        val invitation = invitationJpaRepository.findByMemberId(memberId) ?: throw InvalidRequestException("존재하지 않는 사용자 id: $memberId")

        return MemberInfoResponse(
            name = member.name,
            email = member.email,
            inviteeName = invitee.name,
            relationWithInvitee = invitation.relationType
        )
    }

    // 사용자 정보 업데이트는 후보자만 진행함.
    fun updateMemberProfile(memberId: Long, request: MemberProfileRequest) {
        // Implementation for updating member profile
        val member = memberJpaRepository.findByIdOrNull(memberId) ?:
            throw InvalidRequestException("존재하지 않는 사용자 id: $memberId")

    }

    // 사용자 전화번호 인증
    fun validatePhoneNumber() {
        // TODO : 문자 메시지로 인증번호 요청
        // TODO : 응답 cache에 저장
    }
}
package com.official.cufitapi.domain.application

import com.official.cufitapi.common.exception.InvalidRequestException
import com.official.cufitapi.domain.api.dto.InvitationCodeRequest
import com.official.cufitapi.domain.api.dto.invitation.InvitationCodeResponse
import com.official.cufitapi.domain.infrastructure.entity.Invitation
import com.official.cufitapi.domain.infrastructure.repository.InvitationJpaRepository
import com.official.cufitapi.domain.infrastructure.repository.MemberJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
@Transactional(readOnly = true)
class InvitationService(
    private val invitationJpaRepository: InvitationJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) {

    companion object {
        private const val BASE_62_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    }

    // TODO : 주선자 후보자 구분
    fun validate(memberId: Long, request: InvitationCodeRequest) {
        if (invitationJpaRepository.existsByMemberIdAndCode(memberId, request.invitationCode)) {
            // 가입성공
        }
    }

    // TODO : 주선자랑 후보자 구분할 수 있는 초대코드 생성
    fun generateInvitationCode(memberId: Long) : InvitationCodeResponse {
        val member = memberJpaRepository.findByIdOrNull(memberId) ?: throw InvalidRequestException("잘못된 사용자 ID 요청 : ${memberId}")
        val invitationCode = generateRandomBase62String()
        val invitation = invitationJpaRepository.save(Invitation(
            code = invitationCode,
            memberId = member.id!!
        ))
        return InvitationCodeResponse(
            invitationCode = invitation.code
        )
    }

    private fun generateRandomBase62String(length: Int = 8): String {
        return (1..length)
            .map { Random.nextInt(0, BASE_62_CHARS.length) }
            .map(BASE_62_CHARS::get)
            .joinToString("")
    }
}
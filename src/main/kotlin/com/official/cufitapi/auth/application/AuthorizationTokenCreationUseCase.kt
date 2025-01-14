package com.official.cufitapi.auth.application

import com.official.cufitapi.auth.application.command.AuthorizationTokenCreationCommand
import com.official.cufitapi.auth.application.service.SecretKeyGenerator
import com.official.cufitapi.auth.domain.vo.AccessToken
import com.official.cufitapi.auth.domain.AuthorizationToken
import com.official.cufitapi.auth.domain.vo.RefreshToken
import com.official.cufitapi.common.config.property.AuthorizationProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import java.security.Key
import java.util.Date

interface AuthorizationTokenCreationUseCase {
    fun create(authorizationTokenCreationCommand: AuthorizationTokenCreationCommand): AuthorizationToken
}

@Service
class AuthorizationTokenCreationService(
    private val authorizationProperties: AuthorizationProperties,
    private val secretKeyGenerator: SecretKeyGenerator
): AuthorizationTokenCreationUseCase {
    
    override fun create(authorizationTokenCreationCommand: AuthorizationTokenCreationCommand): AuthorizationToken {
        val secretKey = secretKeyGenerator.generate(authorizationProperties.secretKey)
        val accessToken = createAccessToken(secretKey, authorizationTokenCreationCommand)
        val refreshToken = refreshToken(secretKey, authorizationTokenCreationCommand)
        return AuthorizationToken(accessToken, refreshToken)
    }

    private fun refreshToken(secretKey: Key, authorizationTokenCreationCommand: AuthorizationTokenCreationCommand): RefreshToken {
        val refreshClaims = Jwts.claims()
        refreshClaims.subject = authorizationTokenCreationCommand.memberId.toString() // 최소 정보만
        val refreshToken = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setClaims(refreshClaims)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + authorizationProperties.refreshTimeOut))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
        return RefreshToken(refreshToken)
    }

    private fun createAccessToken(secretKey: Key, authorizationTokenCreationCommand: AuthorizationTokenCreationCommand
    ): AccessToken {
        val claims = Jwts.claims()
        claims.subject = authorizationTokenCreationCommand.memberId.toString()
        claims["authority"] = authorizationTokenCreationCommand.authority.name
        val accessToken = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + authorizationProperties.accessTimeOut))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
        return AccessToken(accessToken)
    }
}

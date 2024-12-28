package com.official.cufitapi.domain.api

import com.official.cufitapi.common.annotation.Authorization
import com.official.cufitapi.common.annotation.AuthorizationType
import com.official.cufitapi.domain.api.docs.NotificationApiDocs
import com.official.cufitapi.domain.api.dto.notification.NotificationResponse
import com.official.cufitapi.domain.application.NotificationQueryService
import com.official.cufitapi.domain.application.NotificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping

@ApiV1Controller
class NotificationApi(
    private val notificationQueryService: NotificationQueryService,
    private val notificationService: NotificationService
) : NotificationApiDocs {

    @GetMapping("/notifications")
    fun getNotificationList(
        @Authorization(AuthorizationType.ALL) memberId: Long
    ) : ResponseEntity<List<NotificationResponse>> {
        return ResponseEntity.ok(notificationQueryService.findAll(memberId))
    }
}
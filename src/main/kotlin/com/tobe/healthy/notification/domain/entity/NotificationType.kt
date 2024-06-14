package com.tobe.healthy.notification.domain.entity

enum class NotificationType(
    val description: String
) {
    FEEDBACK("피드백"),
    RESERVE("예약완료"),
    CANCEL("예약취소"),
    WRITE("게시글 작성"),
    COMMENT("게시글 댓글"),
    REPLY("댓글 답글"),
    WAITING( "대기 예약 확정");
}
package com.tobe.healthy.common

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.tobe.healthy.schedule.domain.entity.ReservationStatus
import com.tobe.healthy.schedule.entity.out.LessonResponse.LessonDetailResponse

class LessonDetailResponseSerializer : JsonSerializer<LessonDetailResponse>() {
    override fun serialize(value: LessonDetailResponse, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartObject()
        value.scheduleId?.let { gen.writeNumberField("scheduleId", it) }
        value.duration?.let { gen.writeNumberField("duration", it) }
        gen.writeStringField("lessonStartTime", value.lessonStartTime.toString())
        gen.writeStringField("lessonEndTime", value.lessonEndTime.toString())
        gen.writeStringField("reservationStatus", value.reservationStatus?.name)

        if (value.reservationStatus != ReservationStatus.DISABLED) {
            gen.writeObjectField("applicantId", value.applicantId)
            gen.writeStringField("applicantName", value.applicantName)
            gen.writeObjectField("waitingStudentId", value.waitingStudentId)
            gen.writeStringField("waitingStudentName", value.waitingStudentName)
        }

        gen.writeEndObject()
    }
}
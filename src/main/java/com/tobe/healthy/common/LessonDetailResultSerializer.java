package com.tobe.healthy.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.tobe.healthy.schedule.domain.dto.out.RetrieveTrainerScheduleByLessonInfoResult.LessonDetailResult;
import com.tobe.healthy.schedule.domain.entity.ReservationStatus;

import java.io.IOException;

public class LessonDetailResultSerializer extends JsonSerializer<LessonDetailResult> {

    @Override
    public void serialize(LessonDetailResult value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        if (value.getScheduleId() != null) {
            gen.writeNumberField("scheduleId", value.getScheduleId());
        }
        if (value.getDuration() != null) {
            gen.writeNumberField("duration", value.getDuration());
        }
        gen.writeStringField("lessonStartTime", String.valueOf(value.getLessonStartTime()));
        gen.writeStringField("lessonEndTime", String.valueOf(value.getLessonEndTime()));
        gen.writeStringField("reservationStatus", value.getReservationStatus() != null ? value.getReservationStatus().name() : null);

        if (value.getReservationStatus() != ReservationStatus.DISABLED) {
            gen.writeObjectField("applicantId", value.getApplicantId());
            gen.writeStringField("applicantName", value.getApplicantName());
            gen.writeObjectField("waitingStudentId", value.getWaitingStudentId());
            gen.writeStringField("waitingStudentName", value.getWaitingStudentName());
        }

        gen.writeEndObject();
    }
}

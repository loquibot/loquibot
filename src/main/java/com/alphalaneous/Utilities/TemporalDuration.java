package com.alphalaneous.Utilities;

import java.text.ChoiceFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

public class TemporalDuration implements TemporalAccessor {
    private static final Temporal BASE_TEMPORAL = LocalDateTime.of(0, 1, 1, 0, 0);

    private final Duration duration;
    private final Temporal temporal;

    public TemporalDuration(Duration duration) {
        this.duration = duration;
        this.temporal = duration.addTo(BASE_TEMPORAL);
    }

    @Override
    public boolean isSupported(TemporalField field) {
        if(!temporal.isSupported(field)) return false;
        long value = temporal.getLong(field)-BASE_TEMPORAL.getLong(field);
        return value!=0L;
    }

    @Override
    public long getLong(TemporalField field) {
        if(!isSupported(field)) throw new UnsupportedTemporalTypeException(new StringBuilder().append(field.toString()).toString());
        return temporal.getLong(field)-BASE_TEMPORAL.getLong(field);
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public String toString() {

        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                .optionalStart() //second
                .optionalStart() //minute
                .optionalStart() //hour
                .optionalStart() //day
                .optionalStart() //month
                .optionalStart() //year
                .appendValue(ChronoField.YEAR).appendLiteral(" Years, ").optionalEnd()
                .appendValue(ChronoField.MONTH_OF_YEAR).appendLiteral(" Months, ").optionalEnd()
                .appendValue(ChronoField.DAY_OF_MONTH).appendLiteral(" Days, ").optionalEnd()
                .appendValue(ChronoField.HOUR_OF_DAY).appendLiteral(" Hours, ").optionalEnd()
                .appendValue(ChronoField.MINUTE_OF_HOUR).appendLiteral(" Minutes, ").optionalEnd()
                .appendValue(ChronoField.SECOND_OF_MINUTE).appendLiteral(" Seconds").optionalEnd()
                .toFormatter();

        return dtf.format(this);
    }

    public String toBasicString() {
        return duration.toString();
    }

}
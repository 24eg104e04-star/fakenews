package com.fakenews.detection.dto;

/**
 * Data Transfer Object for Dashboard Statistics.
 */
public class StatsResponse {

    private long total;
    private long fake;
    private long real;

    public StatsResponse() {
    }

    public StatsResponse(long total, long fake, long real) {
        this.total = total;
        this.fake = fake;
        this.real = real;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getFake() {
        return fake;
    }

    public void setFake(long fake) {
        this.fake = fake;
    }

    public long getReal() {
        return real;
    }

    public void setReal(long real) {
        this.real = real;
    }
}

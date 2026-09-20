package com.makaranda.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class BirthRequest {
    @NotNull
    public LocalDateTime dateTime;
    public String timeZone = "Asia/Kolkata";
    public Double tzOffsetHours = 5.5;
    public Double latitude = 26.5833;
    public Double longitude = 85.268;
    public String place = "Darbhanga, Bihar, India (KSDS अक्षांश २६।३५)";
    public String ayanamsa = "SURYA_SIDDHANTA_MAKARANDA";
    public String panchangMode = "SIDDHANTIC";
    public String houseSystem = "WHOLE_SIGN";
    public String name;
    public String gender;
}

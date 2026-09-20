package com.makaranda.dto;

import java.time.LocalDate;

public class MuhurtaRequest {
    public String purpose = "MARRIAGE";
    public LocalDate from;
    public int days = 40;
    public Double lat;
    public Double lon;
    public Double tzOffsetHours = 5.5;
    public String ayanamsa;
    public String panchangMode;
    /** Origin of travel (default Darbhanga). */
    public Double fromLat;
    public Double fromLon;
    public String fromPlace;
    /** Destination of travel. */
    public Double toLat;
    public Double toLon;
    public String toPlace;
    /** N, NE, E, SE, S, SW, W, NW — used when destination coords are absent. */
    public String direction;
}

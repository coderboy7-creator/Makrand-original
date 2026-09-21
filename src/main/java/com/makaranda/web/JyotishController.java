package com.makaranda.web;

import com.makaranda.dto.BirthRequest;
import com.makaranda.dto.MatchRequest;
import com.makaranda.report.PdfReportService;
import com.makaranda.service.JyotishService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/jyotish")
public class JyotishController {
    private final JyotishService jyotish;
    private final PdfReportService pdf;

    public JyotishController(JyotishService jyotish, PdfReportService pdf) {
        this.jyotish = jyotish;
        this.pdf = pdf;
    }

    @PostMapping("/kundali")
    public Map<String, Object> kundali(@RequestBody BirthRequest req) {
        return jyotish.chartPayload(req);
    }

    @PostMapping("/vargas")
    public Map<String, Object> vargas(@RequestBody BirthRequest req) {
        return Map.of("vargas", jyotish.chart(req).vargas(), "vimshopaka", jyotish.chart(req).vimshopaka());
    }

    @PostMapping("/ashtakavarga")
    public Map<String, Object> ashtakavarga(@RequestBody BirthRequest req) {
        return jyotish.ashtakavarga(req);
    }

    @PostMapping("/shadbala")
    public Map<String, Object> shadbala(@RequestBody BirthRequest req) {
        return jyotish.shadbala(req);
    }

    @PostMapping("/kp")
    public Map<String, Object> kp(@RequestBody BirthRequest req) {
        return jyotish.kp(req);
    }

    @PostMapping("/dasha")
    public Map<String, Object> dasha(@RequestBody BirthRequest req) {
        return jyotish.dashaTree(jyotish.chart(req));
    }

    @PostMapping("/yogas")
    public Map<String, Object> yogas(@RequestBody BirthRequest req) {
        return jyotish.chartPayload(req);
    }

    @PostMapping("/interpret")
    public Map<String, Object> interpret(@RequestBody BirthRequest req) {
        return jyotish.interpret(req);
    }

    @PostMapping("/match")
    public Map<String, Object> match(@RequestBody MatchRequest req) {
        return jyotish.match(req.boy, req.girl);
    }

    @PostMapping("/gochar")
    public Map<String, Object> gochar(@RequestBody BirthRequest req,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return jyotish.transits(req, date);
    }

    @PostMapping("/varshaphal")
    public Map<String, Object> varshaphal(@RequestBody BirthRequest req, @RequestParam(defaultValue = "0") int year) {
        int y = year == 0 ? LocalDate.now().getYear() : year;
        return jyotish.varshaphal(req, y);
    }

    @PostMapping("/prashna")
    public Map<String, Object> prashna(@RequestBody BirthRequest req, @RequestParam(defaultValue = "") String question) {
        return jyotish.prashna(req, question);
    }

    @PostMapping("/gemstones")
    public Map<String, Object> gems(@RequestBody BirthRequest req) {
        return (Map<String, Object>) jyotish.chartPayload(req).get("gemstones");
    }

    @GetMapping("/panchang")
    public Map<String, Object> panchang(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) Double tz,
            @RequestParam(required = false) String ayanamsa,
            @RequestParam(required = false) String mode) {
        return jyotish.panchang(date, lat, lon, tz, ayanamsa, mode);
    }

    @GetMapping("/panchang/month")
    public Object panchangMonth(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) Double tz,
            @RequestParam(required = false) String ayanamsa,
            @RequestParam(required = false) String mode) {
        return jyotish.panchangMonth(month, lat, lon, tz, ayanamsa, mode);
    }

    @GetMapping("/horoscope")
    public Map<String, Object> horoscope(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String ayanamsa,
            @RequestParam(required = false) String mode) {
        return jyotish.dailyHoroscope(date, lat, lon, ayanamsa, mode);
    }

    @GetMapping("/muhurta")
    public Object muhurta(
            @RequestParam(defaultValue = "MARRIAGE") String purpose,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(defaultValue = "40") int days,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) Double tz,
            @RequestParam(required = false) String ayanamsa,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) Double fromLat,
            @RequestParam(required = false) Double fromLon,
            @RequestParam(required = false) String fromPlace,
            @RequestParam(required = false) Double toLat,
            @RequestParam(required = false) Double toLon,
            @RequestParam(required = false) String toPlace,
            @RequestParam(required = false) String direction) {
        return jyotish.muhurta(purpose, from, days, lat, lon, tz, ayanamsa, mode,
                fromLat, fromLon, fromPlace, toLat, toLon, toPlace, direction);
    }

    @PostMapping("/muhurta")
    public Object muhurtaPost(@RequestBody com.makaranda.dto.MuhurtaRequest req) {
        return jyotish.muhurtaBundle(req);
    }

    @PostMapping(value = { "/report", "/report.pdf" }, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> report(@RequestBody BirthRequest req) {
        byte[] bytes = pdf.kundaliReport(req);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=makaranda-kundali.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(bytes.length)
                .body(bytes);
    }
}

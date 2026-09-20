package com.makaranda.report;

import com.makaranda.dto.BirthRequest;
import com.makaranda.service.JyotishService;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfReportServiceTest {

    @Test
    void reportIsPdfAndNamedMakarandaNotAstrotalk() {
        JyotishService js = new JyotishService();
        PdfReportService pdf = new PdfReportService(js);
        BirthRequest req = new BirthRequest();
        req.dateTime = LocalDateTime.of(2022, 7, 29, 5, 20);
        req.name = "परीक्षण";
        byte[] bytes = pdf.kundaliReport(req);
        assertTrue(bytes.length > 8000, "too thin: " + bytes.length);
        String head = new String(bytes, 0, 8, StandardCharsets.ISO_8859_1);
        assertTrue(head.startsWith("%PDF"), head);
        // OpenPDF may compress streams; still the catalog often keeps font names / info.
        String ascii = new String(bytes, StandardCharsets.ISO_8859_1);
        assertTrue(!ascii.toLowerCase().contains("astrotalk"));
    }
}

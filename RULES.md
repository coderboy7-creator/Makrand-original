# RULES — Makaranda Jyotish

Standing product and engineering rules. Do not drop an entry unless the product owner reversed it.

---

## 1. Architecture

1. **Monolith only.** One Spring Boot JAR. No microservices, no second calc API, no Next.js rewrite.
2. Stack is locked: **Spring Boot + Java 21**, **React 18 + TypeScript**, **React Native + TypeScript**, **H2 dev / PostgreSQL prod**, Nominatim, MUI. Swiss Ephemeris may be added **behind** `EphemerisEngine` for Drik.
3. Ganita lives in `com.makaranda.calc` with **no** `@Service` and **no** HTTP.
4. React **draws**; it does not recompute longitudes.
5. Application must stay **runnable and error-free** (`mvn test` + smoke panchang/kundali).

## 2. Defaults

6. Default place **Darbhanga**, KSDS **अक्षांश २६।३५, देशान्तर ०१।३५, पल्लभा ६** (26.5833°N, 85.268°E, IST). Not Delhi. Not the old 26.1542 / 85.8918 pair unless a test explicitly uses it.
7. Default ayanamsa **Surya Siddhanta (Makaranda) — Mithilanchal**.
8. Default panchang mode **SIDDHANTIC**. Drik is a comparison toggle.
9. Default language **Hindi**; English complete.
10. Default houses **whole sign**.

## 3. Panchang / ganita

11. **No hardcoding** of dates, offsets, or “if (year==2026)” patches. Must work for any past or future civil date.
12. **Gold = original KSDSU विश्वविद्यालय पञ्चांग**, not Mithila commercial panji (2025–27 colour books / 2027 Magha–Jyeshtha typeset). Those are Makaranda-named but **not** the same स्पष्ट.
13. Printed time = limb **lasts till** that time. Never present start as end.
14. User-facing clocks **12-hour** (not 24-hour). Panji display: **घं.मि.** with **दि. / सां. / रा.** plus घटिका from sunrise. All listed book times in the KSDSU sheets are o’clock, not दण्ड as the only column.
15. सिद्धान्तिक limbs use **Surya Siddhanta spashta + Makaranda bijas** in `EphemerisEngine.siddhantic`. Do **not** drive SIDDHANTIC panchang with Meeus. Do **not** add extra Meeus 47.A terms to “fix” KSDSU.
16. दृक् limbs use apparent Meeus/JPL. Do not apply the old **−8.2′ moon bija** (that was a date-shaped fudge).
17. Do **not** retune July/August KSDSU fixtures to chase winter rows or Mithila 2027.
18. Sunrise/sunset: apparent noon (equation of time) + **50′** horizon. Civil madhyāhna = 12:00 is **wrong** for KSDSU (29 Jul 2022 is 5:20 / 6:50).
19. Place search **prefers Indian** places (Delhi ≠ foreign Delhi).
20. Travel muhurta: ask **from and to** if calculable; otherwise travel **direction**.
21. Filename of a scanned panji is **not** the Gregorian range (`04-01-2026.jpg` was August content). Identify by weekday + nakshatra + SR.

## 4. Hindi / UX

22. When Hindi is selected, **inner page copy and kundali/lagna akshara** are Devanagari, not English Latin on the chart.
23. Do not call the rule-engine reading “AI” until an LLM client exists.
24. Gemstones: keep the warning — **no Neelam/Gomed** without full chart + dasha.
25. Matching “not recommended” is advisory, not a marriage ban.
26. Daily horoscope is **rashi-generic**, not the user’s kundali.
27. Prashna is the **question time**, not birth, unless the UI says otherwise.

## 5. Git and process

28. Do not silently change planetary degrees without a fixture test.
29. Never “fix” SS/Makaranda to equal Lahiri. They **must** differ.
30. No new secrets in git. JWT secret must move to env before public deploy.
31. If you touch `calc/**`, add or extend a **known** JD/date fixture.
32. Small PRs. Do not rewrite `Pages.tsx` and ganita in the same PR.

## 6. Ethics

33. No medical/legal claims.
34. Prefer mantra/dāna language over fear-based dosha copy.
35. If a pandit school disagrees (e.g. Mangal in 2nd), add a **school flag** — do not overwrite silently.

## 7. Dead ends (do not retry)

- Mapping upload **filenames** to Gregorian months.
- Extra Meeus lunar terms to close 0.5–1.2° vs KSDSU (pyephem already agrees with Drik; the book is not Drik).
- Treating Mithila 2027 Magha trayodashi 9:26 AM as KSDSU gold.
- Using घटिका checksum from a **misread** द.प. (e.g. May 21 सू.उ. ४।१८ vs नक्षत्र २२।४८).
- Date-hardcoded SR/SS tables.

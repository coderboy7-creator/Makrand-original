# MEMORY — Makaranda Jyotish

Internal continuity for humans and agents. Workspace files are the source of truth; this file stores **decisions, gold numbers, and dead ends** that are easy to lose.

**Last updated:** 2026-09-20 (P0 tests; bijas unchanged)  

---

## Current state

- App is a runnable monolith. Preview: Vite `:5173`, API `:8080`.
- SIDDHANTIC panchang = **SS spashta + Makaranda bijas** (not Meeus).
- DRIK = Meeus Sun/Moon **without** −8.2′ bija.
- Default place **KSDS 26.5833°N 85.268°E** (२६।३५ / ०१।३५ / पल्लभा ६).
- GitHub: `https://github.com/coderboy7-creator/Makrand-original` branch `main`.
- **Unfinished:** winter/Oct tithi early vs some printed rows; Mithila panji ≠ KSDSU; Swiss Eph not wired; mobile stub.

## Gold vs not-gold

**Gold:** original **KSDSU विश्वविद्यालय पञ्चांग**.

- 2016–17 PDF (workspace historically under uploads; do not map filenames to dates).
- Photo **29 Jul–12 Aug 2022** Shukla 1–15 (user confirmed original KSDSU).
- 20 Jul 2016 कृष्ण प्रतिपदा रा. 4:03; 24 Jul 2016 कृष्ण पंचमी रा. 10:16.

**Not gold:** Mithila panchang 2025–27 (colour books + 2027 ज्येष्ठ/चैत्र/माघ typeset). Owner: “Makaranda anusar but **not** original KSDSU.” Do not fit Feb 19 2027 9:26 AM Magha trayodashi into SIDDHANTIC.

User-typed clocks sometimes disagreed with photos (15 Dec 4:49 vs book दि. 3:49; 5 Jan Pushya 5:35 vs दि. 4:35). **Photos win.**

## Makaranda bijas in code

`EphemerisEngine` SIDDHANTIC:

```
MAKARANDA_SUN_MEAN_BIJA      = -0.19°
MAKARANDA_MOON_MEAN_BIJA     = +2.31°
MAKARANDA_MOON_APSIS_OFFSET  = -90°
MAKARANDA_SUN_APOGEE_OFFSET  = +174.3°
```

Fitted jointly to 2022-07-29…08-12 tithi+nakshatra ends **and** 2016-07-21 04:03 (elong 192°). School constants, not per-date.

Sunrise: NOAA EoT, depression **−0.8333°**.

## Verified clocks (SIDDHANTIC, KSDS place)

| Date | Book | App (last dump) |
|---|---|---|
| 2022-07-29 T1 | रा. 12:04 पुष्य 9:53 SR 5:20 SS 6:50 | 12:13 AM, 9:51 AM, 5:23 / 6:50 |
| 2022-08-12 T15 | दि. 7:27 SR 5:28 SS 6:42 | 7:47 AM, 5:30 / 6:40 |
| 2016-07-20 T16 | रा. 4:03 | 4:10 AM |
| 2016-07-24 T5 | रा. 10:16 पू.भा. दि. 3:04 SR 5:18 | **10:14 PM**, 2:53 PM, 5:21 / 6:52 |

Eight-row owner table (mixed seasons): names/vara match; **Jul good**; **5 Oct 2025 / 10 Jan 2026 tithi early** (−68 min / −2 h). Treat as seasonal residual, not a weekday bug.

Convention: दि. 11।55 = 11:55 AM; दि. 2।17 = 2:17 PM; रा. 2।43 = 2:43 AM; सां. 5।28 = 5:28 PM. घटिका checksum when द.प. is read right.

## Owner constraints (never infer away)

- Monolith; Java/Spring + React 18 TS + RN TS; H2/Postgres; Nominatim; MUI.
- Hindi default; Devanagari on kundali when Hindi.
- 12-hour clocks; limb **till** times.
- No date hardcoding; any past/future date.
- Travel muhurta: from/to or direction.
- India-first place search.
- Mithila users must not need manual correction — **more precise to KSDSU without breaking July fixtures**.

## Dead ends

- Filename → Gregorian map (`04-01-2026` file was August).
- Extra Meeus terms / −8.2′ bija to hit KSDSU (Drik already matches pyephem; book is not Drik).
- Forcing SS crude mean-only or flipping SIDDHANTIC to Meeus.
- May 21 book सू.उ. **०४।१८** vs नक्षत्र २२।४८ checksum fail — do not fit.
- Dec 15 २५।०० vs दि. 3:49 checksum fail on some Mithila rows.
- Lucky SS dEl ≈ 0 on 30 Aug / 20 Sep 2026 was coincidence before bijas.

## Key paths

| Path | Why |
|---|---|
| `src/main/java/com/makaranda/calc/ephemeris/EphemerisEngine.java` | SS bijas, EoT sunrise, Drik moon |
| `.../panchang/PanchangCalculator.java` | KSDS constants, limb bisection, दि./रा./सां. |
| `.../ephemeris/AyanamsaSystem.java` | Makaranda ≈ Lahiri for modern printed rashi |
| `frontend/src/pages/Pages.tsx` | Almost all screens |
| `src/test/java/com/makaranda/calc/SunriseMithilaTest.java` | KSDSU 2022 SR + T1 + purnima + 2016 T16 |
| `docs/PROJECT_STATUS.md` | Older snapshot (Aug 2026); prefer PRD/TASKS if conflict |
| `tmp_panji/` | Local OCR; gitignored |

## Next agent move

1. If a new **original KSDSU** winter page arrives, refit bijas against Jul **and** that page; no Mithila 2027.
2. Keep servers: `mvn spring-boot:run` + `frontend npm run dev`.
3. Do not revert SIDDHANTIC to Meeus to chase Drik-like websites (kundligpt, Deccan Chronicle).

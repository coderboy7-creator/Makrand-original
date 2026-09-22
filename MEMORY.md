# MEMORY — Makaranda Jyotish

Internal continuity for humans and agents. Workspace files are the source of truth; this file stores **decisions, gold numbers, and dead ends** that are easy to lose.

**Last updated:** 2026-09-22 (Chunk 6 SS four-step: Mangal rāśi vs Drik)  

---

## Current state

- App is a runnable monolith. Preview: Vite `:5173`, API `:8080`.
- SIDDHANTIC panchang = **SS spashta + Makaranda bijas** (not Meeus).
- DRIK = Swiss Ephemeris (`sepl_18.se1` + `semo_18.se1`, 1800–2400) when files load; else Meeus Sun/Moon **without** −8.2′ bija. SIDDHANTIC never calls SE.
- Default place **KSDS 26.5833°N 85.268°E** (२६।३५ / ०१।३५ / पल्लभा ६).
- GitHub: `https://github.com/coderboy7-creator/Makrand-original` branch `main`.
- **Unfinished:** original KSDSU winter tithi 45–140 min early; Śani panji−SIDD phase ~−10° (HOLD bija); `calc/p13-udayantara` not merged. Swiss Eph is Drik-only.
- Graha-spaṣṭa: Chunk 2 śīghra; Chunk 3 node +180°; Chunk 5 paridhi+mandocca; Chunk 6 SS four-step (6 Feb 2023 Mangal SIDD−DRIK **−1.1°**, same rāśi). Śani still **HOLD bija**. See `panji-accuracy/CHUNK6_FOURSTEP.md`.

## Gold vs not-gold

**Gold:** original **KSDSU विश्वविद्यालय पञ्चांग**.

- 2016–17 PDF (workspace historically under uploads; do not map filenames to dates).
- Photo **29 Jul–12 Aug 2022** Shukla 1–15 (user confirmed original KSDSU).
- 20 Jul 2016 कृष्ण प्रतिपदा रा. 4:03; 24 Jul 2016 कृष्ण पंचमी रा. 10:16.
- Original KSDSU scans: Kartika 17–30 Oct 2016, 31 Oct–14 Nov 2016, Pausha 30 Dec 2016–12 Jan 2017, Magha 13–27 Jan 2017; Kartika 26 Oct–8 Nov 2022, Pausha 24 Dec 2022–6 Jan 2023, Magha Krishna 7–21 Jan 2023.

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

Original winter vs app (SIDDHANTIC, KSDS): **tithi 45–140 min early**; **nakshatra often ≤30 min** (1 Nov 2022 UAs 7:34 vs 7:27; 1 Jan 2023 Ashwini 4:37 vs 4:38). Jan SR ~1–8 min; Kartika SR ~16–20 min early. Do not fit Mithila 2025–27.

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
- **2026-09-20 winter manda grid:** sun apogee 150–200° and manda circum 10–20° — only ~174°/14° (current) keeps July gold; winter tithi sum barely moves (394 vs ~400 min). Moon apsis only −90° passes July. NOAA EoT scale 0 fixes Kartika SR (~3 min) but July 29 SS becomes 6:43 vs book 6:50 (fails 4 min). **Do not retune apogee/manda/EoT for winter.** Next lever is not a school constant (full Makaranda manda table / true SS EoT), not a 1-D bija.
- **2026-09-21 P13:** Bhujāntara-only madhyāhna → 29 Jul 2022 SS **6:45** (fails 4 min). Applying bhujāntara to ahargana flipped July tithi identity. Sphuṭa even-paridhi 20′ pushed 12 Aug pūrṇimā to 7:50 (gate 22 min). Live manda stays frozen 14°/32°; sunrise stays NOAA. Code in `MakarandaSpashta`.
- **2026-09-21 P13 udayāntara:** SS EoT = 4×(mean trop − RA), ε=24°. 29 Jul 2022 SR **5:25 AM** vs book 5:20 (fails 4 min). Not merged. Branch `calc/p13-udayantara`.

## Key paths

| Path | Why |
|---|---|
| `src/main/java/com/makaranda/calc/ephemeris/EphemerisEngine.java` | SS bijas, EoT sunrise, Drik→SwissEphAdapter |
| `.../panchang/PanchangCalculator.java` | KSDS constants, limb bisection, दि./रा./सां. |
| `.../ephemeris/AyanamsaSystem.java` | Makaranda ≈ Lahiri for modern printed rashi |
| `frontend/src/pages/Pages.tsx` | Almost all screens |
| `src/test/java/com/makaranda/calc/SunriseMithilaTest.java` | KSDSU 2022 SR + T1 + purnima + 2016 T16 |
| `docs/PROJECT_STATUS.md` | Older snapshot (Aug 2026); prefer PRD/TASKS if conflict |
| `panji-accuracy/` | KSDSU graha-spaṣṭa CSV gold + baseline (Chunk 1) |
| `tmp_panji/` | Local OCR; gitignored |

## Next agent move

1. Do **not** retune apogee/manda/EoT. No Mithila 2027. No Śani constant (phase still elong-shaped).
2. Do **not** merge `calc/p13-udayantara`.
3. Keep servers: `mvn spring-boot:run` + `frontend npm run dev`.
4. Swiss Eph Java port is GPL dual-license (`third_party/swisseph`). Drik only.

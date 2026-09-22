# TASKS — Makaranda Jyotish

Legend: **Done** · **Now** (active) · **P0** beta · **P1** brief-complete · **P2** polish.

---

## Done

- [x] Monolith Spring Boot 3.3 / Java 21 + React 18 MUI SPA + Expo stub
- [x] Dual engine: SIDDHANTIC (SS + Makaranda bijas) and DRIK (Meeus/JPL)
- [x] Defaults: Hindi, Darbhanga KSDS 26°35′ / 85.268°E, Makaranda ayanamsa, SIDDHANTIC
- [x] Panchang limbs with bisection ends, दि./रा./सां. 12-hour clocks, घटिका
- [x] Sunrise via EoT + 50′ (KSDSU 29 Jul 2022 5:20/6:50 class)
- [x] Kundali N/S/E, 16 vargas, Vimshottari, milan, yogas, muhurta, gochar, gems, varshaphal, prashna
- [x] Nominatim proxy, India-biased search
- [x] JWT roles, CRM/consult/admin/learn spines, OpenPDF one-pager, Swagger
- [x] Tests: `AstroMathTest`, `PanchangLimbTest`, `SunriseMithilaTest` (2022-07-29 / 08-12 / 2016-07-20 tithi)
- [x] Repo on GitHub `coderboy7-creator/Makrand-original` (`main`)
- [x] Golden tests lock Jul 2016 / Jul–Aug 2022 KSDSU
- [x] Original KSDSU Kartika/Magha 2016–17 and 2022–23 pages transcribed; winter identity + residual floors in `KsdsuGoldenTest`
- [x] KSDSU graha-spaṣṭa CSV lock (2023 + 2025–26) — `panji-accuracy/`, `GrahaSpashtaBandGoldTest` (Chunk 1; no engine change)
- [x] Outer-graha śīghra epicycle (Chunk 2) — Śani/Guru leave Sūrya; July SR/SS unchanged
- [x] SIDDHANTIC Rāhu/Ketu +180° vs DRIK/panji (Chunk 3)
- [x] Chunk 4 measure: Śani panji−SIDD −10.1° ± 1.5° and elongation-shaped — **no bija**
- [x] Chunk 5 SS paridhi + mandocca (inferiors not śīghrocca-as-mandocca)
- [x] Chunk 6 SS four-step spaṣṭa — 6 Feb 2023 Mangal SIDD−DRIK −26° → −1° (same rāśi as Drik)

## Now / P0 — correctness

- [ ] **Śani panji−SIDD phase** (~−10° ± 1.4°, corr elong −0.82) — **HOLD bija** until a physical lever exists.
- [ ] **Do not merge** `calc/p13-udayantara` (29 Jul 2022 SR 5:25 vs 5:20).
- [ ] **Seasonal सिद्धान्तिक lock** — Kartika/Magha tithi 45–140 min early. **Do not** retune apogee/manda/EoT (winter grid dead). Next lever is not a school constant.
- [ ] Tighten 12 Aug 2022 purnima (app ~7:47 vs book 7:27). Even-paridhi 20′ missed the gate — do not reapply.
- [ ] Stop labelling interpretation “AI”; copy = परम्परा पाठ + disclaimer.
- [x] JWT secret / H2 console off in prod; Flyway + real Postgres test.
- [x] Gem / matching legal copy pass.

## P1 — finish the brief

- [ ] Month panchang UI on `GET /panchang/month` (shukla/krishna spread).
- [ ] Muhurta **intra-day clock windows**; travel from/to already specified.
- [ ] Extra dasha systems (Yogini, Char, Ashtottari, Kalachakra).
- [x] Multi-page bilingual PDF (P3) + ashtakavarga (P4) + shadbala/bhava bala (P5).
- [ ] Real booking slots + Razorpay/Cashfree.
- [ ] Admin ayanamsa/content actually driving the engine.
- [ ] Learning curriculum beyond 5 seed articles.
- [ ] Mobile: LAN API URL or WebView v1; birth datetime on device.
- [ ] Optional LLM behind flag; fallback `InterpretationEngine`.
- [ ] Maithili strings (hi/en exist).
- [ ] Horoscope notifications (none today).

## P2

- [ ] Shared TS types for web/mobile
- [ ] Placidus/KP if in scope
- [ ] True Chitra from a star catalogue
- [ ] OTP login, observability, varshaphal perf
- [ ] East-Indian chart vs scanned Maithil plate
- [ ] Store listings, privacy policy, account deletion

## Suggested next tickets (one at a time)

| ID | Ticket | Lane |
|---|---|---|
| T0 | Makaranda śīghra/paridhi for leftover Śani phase + Mangal/Budha/Śukra (Chunk 5) | `calc/graha-sighra-full` |
| T1 | Winter KSDSU page → spashta residual fit | Java `calc/ephemeris` |
| T2 | Golden tests for the 8-row validation table | `SunriseMithilaTest` |
| T3 | Month calendar UI | React |
| T4 | Honest “parampara” label | React copy — P12 done |
| T5 | CRM authZ | Spring Security |

**Do not start:** Mithila-2027 Magha fit, Meeus extra terms in SIDDHANTIC, monolith split, MUI 6 + ganita in one PR.

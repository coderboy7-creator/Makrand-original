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
- [x] Golden tests lock Jul 2016 / Jul–Aug 2022 KSDSU (do not retune bijas without a winter KSDSU page)

## Now / P0 — correctness

- [ ] **Seasonal सिद्धान्तिक lock** — Oct–Jan KSDSU rows run early (10 Jan 2026 tithi ~−2 h). Retune **manda/apsides only**; do not break Jul 2016 / Jul 2022. Need one original KSDSU Magha/Pausha page.
- [ ] More golden tests: 24 Jul 2016 10:14 PM; 27 Jul 2022 Chaturdashi; 12 Aug 2022 purnima Δ≤15 min.
- [ ] Tighten 12 Aug 2022 purnima (app ~7:47 vs book 7:27).
- [ ] Swiss Ephemeris adapter for **Drik only** (keep SS path).
- [ ] Stop labelling interpretation “AI”; copy = परम्परा पाठ + disclaimer.
- [ ] JWT secret / H2 console off in prod; Flyway + real Postgres test.
- [ ] CRM/consult **ownership** checks.
- [ ] Gem / matching legal copy pass.

## P1 — finish the brief

- [ ] Month panchang UI on `GET /panchang/month` (shukla/krishna spread).
- [ ] Muhurta **intra-day clock windows**; travel from/to already specified.
- [ ] Extra dasha systems (Yogini, Char, Ashtottari, Kalachakra).
- [ ] Multi-page bilingual PDF (kundali + D9 + dasha + milan).
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
| T1 | Winter KSDSU page → spashta residual fit | Java `calc/ephemeris` |
| T2 | Golden tests for the 8-row validation table | `SunriseMithilaTest` |
| T3 | Month calendar UI | React |
| T4 | Honest “parampara” label | React copy |
| T5 | CRM authZ | Spring Security |

**Do not start:** Mithila-2027 Magha fit, Meeus extra terms in SIDDHANTIC, monolith split, MUI 6 + ganita in one PR.

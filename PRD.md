# PRD — Makaranda Jyotish

**Product:** Mithilanchal-native Vedic astrology platform (AstroSage / AstroTalk class)  
**Codename / repo:** `makaranda-jyotish` (`Makrand-original`)  
**Version:** 1.0.0 (working MVP)  
**Primary users:** Maithil families, KSDSU / Makaranda panji readers, practising Jyotishis in Darbhanga–Madhubani  
**Default language:** Hindi (English available)  
**Architecture constraint:** **Monolith only** (one Spring Boot JAR)

---

## 1. Problem

Mainstream apps default to **Lahiri + Drik**. Mithila practice (Kameshwar Singh Darbhanga Sanskrit Vishwavidyalaya पञ्चांग, मकरन्द / सूर्य सिद्धान्त) uses different स्पष्ट गणित, Darbhanga coordinates, 12-hour घं.मि. with दि./रा./सां., and Devanagari on the kundali.

Users should not need a printed panji plus a calculator. The app must be **precise to original KSDSU**, for **any civil date**, with **no date hardcoding**.

## 2. Gold standard (acceptance source of truth)

| Source | Role |
|---|---|
| **Original KSDSU विश्वविद्यालय पञ्चांग** (e.g. 2016–17 PDF, 29 Jul–12 Aug 2022 sheet, 24 Jul 2016 row) | **Gold** for सिद्धान्तिक तिथि / नक्षत्र / योग / करण / सूर्योदय–अस्त |
| Mithila commercial panji (2025–27 colour / 2027 typeset Magha–Jyeshtha) | Same school *name*, **different गणित** — do **not** retune KSDSU to match it |
| Swiss / pyephem / Meeus | **दृक्** mode only |

Printed limb time = that tithi/nakshatra/yoga **lasts till** that clock. Do not show the start as if it were the end.

## 3. Goals

1. Cast a kundali for any place/time with Darbhanga as default.
2. Daily panchang whose सिद्धान्तिक clocks match KSDSU within minutes in the calibrated season, and keep improving other seasons without breaking July/August fixtures.
3. First-class **सिद्धान्तिक ↔ दृक्** toggle on the same birth data.
4. Hindi-first UI; kundali/lagna letters in Devanagari when Hindi is on.
5. Practice tools: milan, dasha, muhurta, CRM, consult (booking can stay mocked until payments).
6. Single deployable; web + API + (later) mobile against one `/api/v1`.

## 4. Non-goals (v1)

- Microservices, Next.js rewrite, Node calc engine.
- Marketing “NASA-level” or “AI predictions” until Swiss Eph / LLM are actually wired.
- Fitting Mithila 2027 Magha clocks by date hacks.
- Medical/legal certainty on gemstones or matching.

## 5. Personas

| Persona | Needs |
|---|---|
| गृहस्थ (Mithila) | आज का पंचांग, शुभ मुहूर्त, कुंडली in Hindi, 12-hour clocks |
| पञ्जीकार / KSDSU reader | Limb **तक** times vs the printed book; अक्षांश २६।३५ |
| ज्योतिषी | CRM, milan, dasha, PDF, consult slots |
| Admin | Users, articles, settings |

## 6. Defaults (must ship this way)

| Setting | Value |
|---|---|
| Place | Darbhanga — KSDS **अक्षांश २६।३५**, **देशान्तर ०१।३५** (from Ujjain), **पल्लभा ६** → 26.5833°N, 85.268°E, IST |
| Ayanamsa | `SURYA_SIDDHANTA_MAKARANDA` |
| Panchang mode | `SIDDHANTIC` |
| Houses | Whole sign (Equal / Sripati optional) |
| Language | Hindi; English toggle |
| Clock | 12-hour AM/PM for users; panji **घं.मि.** as दि./सां./रा. |
| Place search | Prefer **Indian** places (Delhi, India ≠ Delhi, USA) |

## 7. Functional requirements

### 7.1 Panchang (P0)

- Five limbs at sunrise: वार, तिथि, नक्षत्र, योग, करण — each with **start and end** (limb lasts **till** end).
- सूर्योदय / सूर्यास्त matching KSDSU dinamaan (apparent noon + 50′ horizon).
- घटिका–पल from sunrise (0–60) plus घं.मि.
- Month API exists; month calendar UI still pending.
- Travel muhurta: ask **from/to** if calculable, else travel **direction**.
- Mode toggle सिद्धान्तिक / दृक् without changing the selected date/place.

### 7.2 Kundali

- Any lat/lon/tz; Darbhanga prefilled.
- North / South / East (Mithila) chart styles.
- Hindi: graha and rashi **akshara in Devanagari** on the chart.
- 16 vargas, Vimshopaka, Vimshottari (M/A/P).
- PDF one-pager today; branded multi-page later.

### 7.3 Matching, dasha, yogas, muhurta, gochar

- Ashtakoota 36 + Mangal dosha (advisory copy, not a ban).
- Yogas / Kaal Sarp / Pitra as pattern engine (document school).
- Muhurta by purpose (vivah, business, travel, property).
- Gochar, Sade Sati, gemstones (no auto-Neelam), varshaphal, prashna.

### 7.4 Platform

- JWT roles: CLIENT, ASTROLOGER, ADMIN.
- CRM clients + saved charts.
- Consult book + mock pay + Jitsi.
- Admin counts / users / articles.
- Learning encyclopedia + articles.
- OSM Nominatim via **backend proxy** only.

## 8. Accuracy acceptance (सिद्धान्तिक)

No per-date offsets. Constants of the paddhati only.

| Fixture | Target |
|---|---|
| 20 Jul 2016 KSDSU कृष्ण प्रतिपदा | ~रा. 4:03 (app ~4:10, Δ ≤ 15 min) |
| 24 Jul 2016 कृष्ण पंचमी | ~रा. 10:16 (app ~10:14) |
| 29 Jul 2022 शुक्ल प्रतिपदा | रा. 12:04 (app ~12:13); पुष्य दि. 9:53 (~9:51) |
| 12 Aug 2022 पूर्णिमा | दि. 7:27 (app ~7:47, tighten) |
| 29 Jul 2022 सूर्योदय/अस्त | 5:20 / 6:50 (app ~5:23 / 6:50) |

Winter/Oct–Jan KSDSU rows (e.g. 10 Jan 2026, 5 Oct 2025) currently run **early** — backlog, must not break July fixtures.

दृक् mode: Meeus/JPL apparent; do not force it onto KSDSU clocks.

## 9. i18n & UX

- Default UI Hindi.
- English complete for all nav and forms.
- 12-hour clocks everywhere users see time.
- Honest labels: rule-engine reading is **परम्परा पाठ**, not “AI”, until LLM is wired.

## 10. Success metrics

- Jyotishi can use daily panchang without a paper panji for KSDSU season.
- Kundali + milan + PDF in one session.
- Zero date-hardcoded branches in `calc/`.
- `mvn test` green on KSDSU sunrise + limb fixtures.

## 11. Tech (locked)

Spring Boot 3.3 + **Java 21**, React 18 + TypeScript + MUI, React Native + TypeScript (Expo), H2 dev / PostgreSQL prod, Nominatim, OpenPDF. Swiss Ephemeris is the planned Drik upgrade behind `EphemerisEngine`, not a rewrite.

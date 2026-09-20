# Agent brief — recreate **Makaranda Jyotish** to current accuracy

This file is a **standalone prompt** for another AI agent. Goal: test whether that agent can reach the **same product, ganita, and accuracy** this repo already has (as of **2026-09-21**, `main` ≈ `058f2e6`).

Workspace / repo is the source of truth. If this brief and code conflict on a *number already locked in tests*, **tests win**. If they conflict on a *product rule the owner stated*, **this brief and `RULES.md` win** over stale README/handover.

Repo: `https://github.com/coderboy7-creator/Makrand-original`  
Branch to treat as target: **`main`** (never `master`).

You are **not** allowed to “improve” accuracy by date hacks, Mithila-2027 fitting, or retuning locked bijas.

---

## 0. Your mission

Build (or continue) a **Mithilanchal-native Vedic astrology monolith**:

- Printed **KSDSU विश्वविद्यालय पञ्चांग** (Makaranda / Sūrya Siddhānta) is the **gold** for सिद्धान्तिक clocks.
- Mainstream Lahiri+Drik apps are **comparison only**.
- Users in Darbhanga–Madhubani must not need a paper panji + calculator for the calibrated season.
- The app must work for **any civil date** (past and future). **No** `if (year == 2026)` patches.

**Definition of “same as we have so far”:**

1. All `mvn test` gold fixtures green (July 2016/2022 tight; winter *identity + residual floors* not “closed”).
2. Defaults: Hindi, KSDS 26.5833°N / 85.268°E, Makaranda ayanamsa, SIDDHANTIC, whole-sign houses.
3. Dual ganita: SIDDHANTIC = SS+Makaranda bijas; DRIK = Swiss Ephemeris when files present.
4. UI: 12-hour clocks, limb **till** times, Devanagari on kundali when Hindi, India-first place search, i18n on every sheet.
5. Kundali P1+P2: अवकहड़ा, fuller graha table, Chandra/Sūrya/Chalit/gochar charts — **without** changing engine bijas.
6. Preview runnable: API `:8080`, Vite `:5173` proxy `/api` → 8080.

If you only make a pretty AstroTalk clone with Lahiri defaults, **you failed**.

---

## 1. Product identity

| | |
|---|---|
| Name | मकरन्द ज्योतिष / Makaranda Jyotish |
| Users | Maithil families, KSDSU / Makaranda panji readers, practising Jyotishis (Darbhanga–Madhubani) |
| Differentiator | **Makaranda/SS default**, **सिद्धान्तिक ↔ दृक् toggle**, **Darbhanga KSDS place**, **East/Mithila kundali**, Hindi-first |
| Architecture | **One** Spring Boot JAR. No microservices, no Node calc API, no Next.js rewrite |
| Ethics | No medical/legal claims. Gems: never auto-prescribe नीलम/गोमेद. Matching “not recommended” is advisory. Interpretation is **परम्परा पाठ**, not “AI”, until an LLM is actually wired |

Astrotalk/AstroSage PDFs may be used as a **section checklist**, never as:

- branding, canned Hindi paragraphs, city lat/lon, or their Drik clocks
- a reason to change default ayanamsa or default place

---

## 2. Hard constraints (never drop)

Copy these into your working memory. Owner stated them; do not “infer away”.

### 2.1 Architecture & stack (locked)

- Monolith: **Spring Boot 3.3 + Java 21**, **React 18 + TypeScript + MUI 5**, **RN/Expo + TS**, **H2 dev / Postgres prod**, Nominatim (backend only), OpenPDF.
- **One module** for all paddhatis (Lahiri / Raman / KP / Drik / SS) as **settings**, not extra apps.
- Ganita in `com.makaranda.calc` — **no** `@Service`, **no** HTTP.
- React **draws**; it does **not** recompute longitudes. Wrong table → fix Java.
- MUI **v5** Grid (`item xs={12}`). Do not upgrade MUI blindly.
- No Lombok. Records for calc results. `LinkedHashMap` for stable JSON.

### 2.2 Defaults (must ship this way)

| Setting | Value |
|---|---|
| Place | **Darbhanga KSDS** अक्षांश **२६।३५**, देशान्तर **०१।३५** (east of Ujjain), पल्लभा **६** |
| Lat/lon | **26.5833°N, 85.268°E**, IST (`Asia/Kolkata`, +5.5) |
| Ayanamsa | `SURYA_SIDDHANTA_MAKARANDA` (Mithilanchal) |
| Panchang mode | `SIDDHANTIC` |
| Houses | `WHOLE_SIGN` (Equal / Sripati optional) |
| Language | **Hindi default**; English complete |
| Clocks | **12-hour** for users; panji **घं.मि.** as दि./सां./रा. |

**Do not** use city-gazetteer Darbhanga **26.1542 / 85.8918** as the product default (old pair; some stale docs still show it). Tests that explicitly use another pair are exceptions.

**Do not** switch default to Astrotalk PDF 26.1522 / 85.8971.

Not Delhi. Not tropical. Not Lahiri as default.

### 2.3 Ganita

- **No hardcoded dates/offsets.** Any past/future civil date.
- **Gold = original KSDSU विश्वविद्यालय पञ्चांग**, not Mithila commercial panji 2025–27 (colour books / 2027 माघ–ज्येष्ठ typeset). Those say “Makaranda anusar” but are **not** original KSDSU स्पष्ट.
- Printed time = limb **lasts till**. Never show start as if it were the printed column.
- सिद्धान्तिक limbs: **Sūrya Siddhānta spashta + Makaranda bijas** in `EphemerisEngine.siddhantic`. **Never** drive SIDDHANTIC with Meeus/Swiss.
- दृक्: Swiss Eph when `sepl_18.se1` + `semo_18.se1` load; else Meeus Sun/Moon **without** the old **−8.2′** moon bija.
- **Do not retune** sun apogee, manda circumference, moon apsis, NOAA EoT scale, or mean bijas for winter (grid **2026-09-20**). July gold is the gate.
- SS/Makaranda and Lahiri **must differ**. Never “fix” SS to equal Lahiri.
- **पुषा = पूर्वाषाढा only** (not Pushya unless the book actually says पुष्य).
- Filename of a scan is **not** the Gregorian range (`04-01-2026.jpg` was August). Identify by weekday + nakshatra + SR.

### 2.4 UX

- Hindi on ⇒ nav, forms, **kundali rashi/graha akshara Devanagari**.
- Language toggle must work on **every listed sheet**.
- Place search prefers **Indian** places (Delhi ≠ Ontario). Unmatched query must **not** keep showing Darbhanga.
- Travel muhurta: **from/to** if calculable, else **direction**. Do not silently use Darbhanga as destination.
- Dead nav / silent API failures are bugs (SPA must show errors).
- Restyle Astrotalk-inspired **sections**, do **not** clone.

### 2.5 Git / process

- Always **new branch**, then merge **`--no-ff`** to `main`.
- **Calc/backend on a separate branch from UI.** Do not rewrite `Pages.tsx` and ganita in the same PR.
- If you touch `calc/**`, add/extend a **known JD/date fixture**.
- Never silently change planetary degrees without a test.
- **No secrets in git.** PATs pasted in chat are **exposed** — use once, **never save** in repo / `.git/config` / credential helper, remind owner to **revoke**. JWT secret must move to env before public deploy.
- Small PRs. Honest commit messages.

### 2.6 Raise if data needed

If a panji photo/PDF is missing, **ask**. Do not invent book times.

---

## 3. Dual ganita (the accuracy core)

```
civil local time + tz
        → Julian Day UT  (AstroMath.julianDayUt)
        → EphemerisEngine.compute(jd, mode [, lonEast])
              SIDDHANTIC: SS mean + manda/sighra + Makaranda bijas  (uses ahargana vs Ujjain)
              DRIK:       SwissEphAdapter.tropicalSunMoon / planets; fallback Meeus+JPL
        → subtract AyanamsaSystem.ayanamsa(jd)  → sidereal
        → PanchangCalculator: sunrise, then bisection of spashta angles for limb ends
```

**Tithi is elongation (Moon−Sun).** Ayanamsa **cancels** for tithi. It still moves **nakshatra walls**.

Default ayanamsa `makaranda()` currently tracks **Lahiri/Chitrapaksha** for *printed spashta rashi* of modern KSDS volumes. Textbook 54″/year from 499 CE remains `SURYA_SIDDHANTA_REVATI`. Do not merge those two.

### 3.1 Locked SIDDHANTIC bijas (`EphemerisEngine`)

```
MAKARANDA_SUN_MEAN_BIJA      = -0.19°
MAKARANDA_MOON_MEAN_BIJA     = +2.31°
MAKARANDA_MOON_APSIS_OFFSET  = -90°
MAKARANDA_SUN_APOGEE_OFFSET  = +174.3°
SUN_APOGEE_KALI              = 77°16′  (+ 11.4″/year)
```

Fitted **jointly** to:

- 2022-07-29 … 08-12 tithi+nakshatra ends, **and**
- 2016-07-21 ~04:03 (elongation ~192°)

They are **school constants**, not per-date. **Do not retune.**

### 3.2 Sunrise / sunset

- Apparent noon via **NOAA equation of time** (civil 12:00 madhyāhna is **wrong**).
- Solar depression **−0.8333°** (34′ refraction + 16′ SD) — “50′ horizon”.
- Target class: **29 Jul 2022 KSDSU सू.उ. ५।२० सू.अ. ६।५०** (app ~5:23 / 6:50).

**Do not** scale EoT to fix Kartika SR: scale 0 made Kartika SR ~3 min but **29 Jul 2022 SS became 6:43 vs book 6:50** (fails 4 min). Dead end.

### 3.3 Limb ends

- Compute five limbs **at sunrise**.
- Each **end** by **bisection** on spashta angle (not linear ghati guess).
- UI: show **तक** (end). Optionally show आरम्भ as caption, never as the printed time.

### 3.4 Clock / watch labels (KSDSU)

Book times are **घं.मि. o’clock**, not घटिका as the only column.

| Watch | Local hour | Example |
|---|---|---|
| **दि.** | 06:00–15:59 | दि. 9।53 = 9:53 AM |
| **सां.** | 16:00–21:59 | सां. 5।28 = 5:28 PM |
| **रा.** | else | रा. 12।04 = 12:04 AM; रा. 4।03 = 4:03 AM (not प्रातः) |

Also घटिका–पल from sunrise (0–60), 1 घटिका = 24 min, displayed `घ।पप`.

**Convention:** दि. 11।55 = 11:55 AM; दि. 2।17 = 2:17 PM; रा. 2।43 = 2:43 AM.

User-typed clocks that disagree with **photos** → **photos win** (e.g. 15 Dec typed 4:49 vs book दि. 3:49; 5 Jan Pushya typed 5:35 vs दि. 4:35).

घटिका checksum is valid **only** when दण्ड–पल is read correctly. Do not fit rows that fail checksum (May 21 सू.उ. ०४।१८ vs नक्षत्र २२।४८).

---

## 4. KSDS place constants (`PanchangCalculator`)

```
KSDS_AKSHANSH_DEG     = 26 + 35/60          // 26.5833…°N
KSDS_DESHANTAR_GHATI  = 1 + 35/60           // 1 ghati 35 pala east of Ujjain
KSDS_UJJAIN_LON       = 75.768°
KSDS_LON              = Ujjain + desantara × 6° = 85.268°E
KSDS_PALABHA          = 6                   // φ ≈ arctan(6/12) ≈ 26°34′
```

Ahargana for SS uses local longitude vs Ujjain.

`application.yml`:

```
makaranda.default-lat: 26.5833
makaranda.default-lon: 85.268
makaranda.default-ayanamsa: SURYA_SIDDHANTA_MAKARANDA
makaranda.default-panchang-mode: SIDDHANTIC
```

Frontend `defaultBirth()` must match.

---

## 5. What “gold” is (and is not)

### 5.1 Gold — original KSDSU विश्वविद्यालय पञ्चांग

Use these as acceptance. Do **not** map scan **filenames** to dates.

| Source | Content |
|---|---|
| 2016–17 KSDSU PDF | Historical gold |
| Photo **29 Jul–12 Aug 2022** शुक्ल 1–15 | Owner confirmed original KSDSU |
| 20 Jul 2016 | कृष्ण प्रतिपदा रा. 4:03 |
| 24 Jul 2016 | कृष्ण पंचमी रा. 10:16 |
| Original scans locked | Kartika 17–30 Oct 2016; 31 Oct–14 Nov 2016; Pausha 30 Dec 2016–12 Jan 2017; Magha 13–27 Jan 2017 |
| | Kartika 26 Oct–8 Nov 2022; Pausha 24 Dec 2022–6 Jan 2023; Magha Krishna 7–21 Jan 2023 |

**2027 Magha / Chaitra / Jyeshtha colour/typeset pages are Mithila, not original KSDSU.** Do not fit them into SIDDHANTIC (e.g. do **not** chase Feb 19 2027 9:26 AM Magha trayodashi).

Drik-like websites (kundligpt, Deccan Chronicle) are **not** SIDDHANTIC gold.

### 5.2 July/August fixtures — **must stay tight** (gate)

Place = KSDS. Mode = SIDDHANTIC. Ayanamsa = Makaranda.

| Date | Book | App class (last locked) | Test tolerance |
|---|---|---|---|
| **2016-07-20** | कृष्ण प्रतिपदा रा. 4:03; उत्तराषाढा; विश्कम्भ; SR 5.16 SS 6.44 | ~4:10 AM; SR/SS ~5:16/6:44 | tithi ≤12 min vs 2016-07-21 04:03; SR/SS ≤12 |
| **2016-07-24** | कृष्ण पंचमी रा. 10:16; पूर्वाभाद्रपदा; पू.भा. दि. 3:04; SR 5:18 SS 6:42 | **~10:14 PM**; nak ~2:53 PM; SR/SS ~5:21/6:52 | tithi ≤8 min vs 22:16; nak ≤15 vs 15:04; SR/SS ≤12 |
| **2022-07-29** | शुक्ल प्रतिपदा रा. 12:04; पुष्य दि. 9:53; SR 5:20 SS 6:50 | ~12:13 AM, 9:51 AM, 5:23/6:50 | tithi ≤15 vs 2022-07-30 00:04; nak ≤8 vs 09:53; **SR/SS ≤4** |
| **2022-08-12** | पूर्णिमा दि. 7:27; SR 5:28 SS 6:42 | ~7:47 AM, 5:30/6:40 | tithi ≤**22** min (honest: bijas cannot close 20 min without moving 29 Jul T1); SR/SS ≤4 |
| **2025-07-11** | T1 द.प. 52-16 / 02:08N; पूर्वाषाढा 03-44 / 06:44N; SR 5:14 SS 6:46 | keep near book | tithi ≤25 vs 2025-07-12 02:08; nak Purva Ashadha; SR/SS ≤12 |

`KsdsuGoldenTest` + `SunriseMithilaTest` encode this. **If you break July, you failed.**

### 5.3 Winter / Kartika / Magha — gold for **identity**, not “closed clocks”

Winter तithi is **45–140 min early**. Nakshatra often **≤30 min**. Jan SR ~1–8 min; **Kartika SR ~16–20 min early**.

Tests use **floors** (“must not slip earlier than…”), not zero error.

| Date | Identity | Book vs app (honest residual) | Test |
|---|---|---|---|
| **2016-10-31** | शुक्ल 1, स्वाती | T1 book रा. 12:01 (1 Nov); app ~10:13 PM 31 Oct | T1 not before 22:00 31 Oct; nak ≤45 vs 11:45; SR 6:28 SS 5:32 ±25 |
| **2017-01-13** | कृष्ण 1, पुष्य | Book दि. 4:06; app ~3:04 PM | T1 not before 14:50; nak ≤40 vs 2017-01-14 01:45; SR 6:45 SS 5:35 ±12 |
| **2022-11-01** | शुक्ल 8, उत्तराषाढा | Book रा. 1:20 (2 Nov); app ~12:33 AM. Nak दि. 7:27 already close | T1 not before 2022-11-02 00:20; nak ≤15 vs 07:27; SR 6:29 SS 5:31 ±25 |
| **2023-01-01** | शुक्ल 10, अश्विनी | Book रा. 10:28; app ~8:52 PM. Nak दि. 4:38 ≈ exact | T10 not before 20:40; nak ≤10 vs 16:38; SR 6:49 SS 5:22 ±12 |
| **2023-01-10** | कृष्ण 3, आश्लेषा | Book दि. 9:48; app ~8:16 AM | T3 not before 08:00; nak ≤20 vs 07:22; SR 6:46 SS 5:36 ±12 |

**Next winter lever is fuller Makaranda spashta (true SS EoT / manda table), not a 1-D bija.** Do this only after honesty copy (plan P12→P13). July remains the gate.

### 5.4 Owner 8-row validation table (DD-MM-YYYY)

Columns: date · tithi no. · दण्ड–पल · घं:मि (N=रात्रि/D=दिन/E=evening) · nak · द.प. · घं:मि · SR/SS.

| Date | Tithi | द.प. / clock | Nak | द.प. / clock | SR / SS |
|---|---|---|---|---|---|
| 11-07-2025 | T1 | 52-16 / 02:08N | पूर्वाषाढा | 03-44 / 06:44N | 05:14 / 06:46 |
| 05-10-2025 | T13 | 17-25 / 01:07D | शतभिषा | 02-07 / 07:00D | 06:09 / 05:51 |
| 10-01-2026 | T7 | 11-18 / 11:17D | हस्त | 29-30 / 06:34N | 06:46 / 05:14 |
| 25-06-2026 | T11 | 40-28 / 09:21N | स्वाती | 33-22 / 06:31E | 05:10 / 06:50 |
| 24-07-2016 | T5 | 42-26 / 10:16N | पूर्वाभाद्रपदा | 24-26 / 03:4D | 05:18 / 06:42 |
| 18-09-2016 | T2 | 37-24 / 08:54N | उत्तराभाद्रपदा | 00-21 / 06:04D | 05:56 / 06:04 |
| 27-07-2022 | T14 | 37-50 / 08:39N | पुनर्वसु | Ahoratram | 05:19 / 06:41 |
| 01-11-2022 | T8 | 47-07 / 01:20N | उत्तराषाढा | 02-25 / 07:27D | 06:29 / 05:31 |

Winter rows in this table **will be early** today. Do not retune bijas to close them.

### 5.5 Extra owner SR/SS notes (Mithila vs KSDSU)

User: 01/01/2026 still ~5± min off.

| Date | SR / SS (stated) |
|---|---|
| 01/08/2026 | 05:21 / 06:39 |
| 29/08/2026 | 05:36 / 06:24 |
| 01/08/2025 | 05:22 / 06:38 |
| 10/09/2025 | 05:49 / 06:11 |
| 20 Jul 2016 | 5.16 / 6.44 |

Clock notes (typed; photos win if conflict):

- 30/08/2026 nak Night 04:50
- 20/09/2026 tithi 09 4:32 PM
- 05/01/2026 T02 Day 12:45, Pushya Day 05:35, SR 6:47 SS 5:13

### 5.6 Verified dump (SIDDHANTIC, KSDS) — MEMORY.md

| Date | Book | App (last dump) |
|---|---|---|
| 2022-07-29 T1 | रा. 12:04 पुष्य 9:53 SR 5:20 SS 6:50 | 12:13 AM, 9:51 AM, 5:23 / 6:50 |
| 2022-08-12 T15 | दि. 7:27 SR 5:28 SS 6:42 | 7:47 AM, 5:30 / 6:40 |
| 2016-07-20 T16 | रा. 4:03 | 4:10 AM |
| 2016-07-24 T5 | रा. 10:16 पू.भा. दि. 3:04 SR 5:18 | **10:14 PM**, 2:53 PM, 5:21 / 6:52 |

---

## 6. Dead ends — **do not retry**

1. Mapping upload **filenames** to Gregorian months.
2. Extra Meeus 47.A terms / −8.2′ bija to hit KSDSU (Drik already matches pyephem; **the book is not Drik**).
3. Forcing SS crude mean-only, or flipping SIDDHANTIC to Meeus/Swiss to chase Drik websites.
4. Fitting **Mithila 2027 Magha**.
5. Date-hardcoded SR/SS tables.
6. घटिका checksum from a **misread** द.प.
7. Lucky SS dEl ≈ 0 on 30 Aug / 20 Sep 2026 — coincidence **before** bijas.
8. **2026-09-20 winter manda grid:** sun apogee 150–200° × manda circum 10–20° — only **~174° / 14° (current)** keeps July gold; winter tithi sum barely moves (~394 vs ~400 min). Moon apsis only **−90°** passes July. NOAA EoT scale 0 fixes Kartika SR but **fails July 29 SS**. **Do not retune apogee / manda / EoT / mean bijas for winter.**
9. Changing default place to PDF city coordinates.
10. Cloning Astrotalk canned dasha/lagna Hindi.
11. Treating stripped PDF Devanagari (`pdftotext` / pypdf dropping combiners as `\u0000`) as source copy. Prefer `pypdf` awareness; do not treat mangled Hindi as gold text.

---

## 7. Swiss Ephemeris — **Drik only**

- Java port: Thomas Mack SwissEph (from C 2.00), **GPL-2 / Swiss Eph dual license**. Lives under `third_party/swisseph` (do not relicense as proprietary).
- Files: **`sepl_18.se1`** + **`semo_18.se1`** (1800–2400). Expected `sepl_18.se1` size **484061** bytes (raw from `aloistr/swisseph`).
- Load order: env `MAKARANDA_EPHE_PATH` / property `makaranda.ephe.path` → classpath `ephe/` → tmp extract.
- `SwissEphDrikTest` must pass: files load; J2000 Sun ≈ **280.37°** within 0.02°; Drik Sun **equals** Swiss; SIDDHANTIC Sun **differs by >0.2°**.
- Rahu/Ketu: Ketu = Rahu + 180°.

**Download (astro.com FTP is 404):**

- GitHub `aloistr/swisseph` `ephe/`
- `https://ephe.scryr.io/ephe/` (live index)
- Do **not** use `https://www.astro.com/ftp/pub/swisseph/ephe/` (404)

SIDDHANTIC must **never** call SwissEph.

---

## 8. Architecture map

```
Browser Vite :5173  (proxy /api → 8080)     Expo RN stub
                │
                ▼
        Spring Boot 3.3.3  Java 21  :8080  one JAR
        /api/v1/**   +  static/ (production Vite build)
                │
     JyotishService (orchestration, Jackson)
                │
     calc/  PURE ganita
       AstroMath, VedicConstants
       ephemeris/  EphemerisEngine, AyanamsaSystem, PanchangMode, SwissEphAdapter
       panchang/   PanchangCalculator
       vedic/      ChartBuilder, VargaCalculator, Avakahada, SpecialCharts
       dasha/ match/ yoga/ transit/ muhurta/ interpret/
                │
     H2 file ./data/makaranda   |  Postgres profile `prod`
```

### 8.1 Kundali flow

1. `BirthForm` → `frontend/src/state.tsx` `birth`.
2. `POST /api/v1/jyotish/kundali` (`BirthRequest`).
3. `ChartBuilder.build`: JD → tropical → −ayanamsa → lagna, houses, vargas, vimshopaka, **avakahada from Moon**.
4. Attach interpretation, yogas, gemstones, Vimshottari depth 3.
5. `KundaliChart.tsx` SVG from `house` + `signIndex`.

Null JSON fields fall back to Darbhanga / Makaranda / SIDDHANTIC.

### 8.2 API prefix `/api/v1`

Public ganita (product choice — do not silently require JWT on `/kundali`):

- `POST /jyotish/kundali` `/vargas` `/dasha` `/yogas` `/interpret` `/match` `/gochar` `/varshaphal` `/prashna` `/gemstones` `/report.pdf`
- `GET /jyotish/panchang` `/panchang/month` `/horoscope` `/muhurta`
- `GET /location/search` (Nominatim **server-side only**, proper User-Agent, `countrycodes=in`)
- Auth `/auth/login` `/register` `/me`
- CRM / consult JWT; admin `ROLE_ADMIN`
- Swagger `/api/swagger` (lock in prod)

Demo seed (when users table empty): `user@` / `astro@` / `kavita@` / `admin@makaranda.app` passwords `user123` / `astro123` / `admin123`.

### 8.3 Frontend

- Almost all screens: `frontend/src/pages/Pages.tsx`
- i18n: `frontend/src/i18n.tsx` (hi default)
- Chart labels: `jyotishLabels.ts`
- Place: `PlaceSearch.tsx` → `/api/v1/location/search`
- JWT key `makaranda.token`
- Theme: gold `#C9A227`, saffron `#E07A2F`, indigo `#0B1026`, ivory `#F7F1E3` — **not** neon AstroTalk

### 8.4 Key paths

| Path | Why |
|---|---|
| `src/main/java/com/makaranda/calc/ephemeris/EphemerisEngine.java` | SS bijas, EoT sunrise, Drik→Swiss |
| `.../panchang/PanchangCalculator.java` | KSDS constants, bisection, दि./रा./सां. |
| `.../ephemeris/AyanamsaSystem.java` | Makaranda ≈ Lahiri for modern printed rashi |
| `.../vedic/ChartBuilder.java` | PlanetBody, FullChart, avakahada |
| `.../vedic/Avakahada.java` | Moon अवकहड़ा + बालादि |
| `frontend/src/pages/Pages.tsx` | UI |
| `frontend/src/chartViews.ts` | Chandra/Sūrya remap (UI-only) |
| `src/test/java/com/makaranda/calc/KsdsuGoldenTest.java` | Winter identity + July gate |
| `SunriseMithilaTest.java` | 2022 SR + T1 + purnima + 2016 T16 |
| `SwissEphDrikTest.java` | Drik ≠ SIDDHANTIC |
| `AvakahadaTest.java` | Rohiṇī pāda 4 + baladi |
| `LocationServiceTest.java` | Delhi India; Darbhanga=KSDS; unknown ≠ Darbhanga |

---

## 9. Features that already exist (you must preserve)

### 9.1 Platform

- Runnable monolith; Vite + Spring; Expo stub (`mobile/` — `127.0.0.1` will not work on a physical phone).
- JWT roles CLIENT / ASTROLOGER / ADMIN.
- CRM clients + saved charts (ownership checks were locked in winter-manda branch — keep them).
- Consult book + **mock** pay + Jitsi.
- Admin counts; learning encyclopedia + seed articles.
- OpenPDF one-pager `/report.pdf` (thin vs 55-page checklist).
- Month panchang **API** exists; **calendar UI not built**.

### 9.2 Jyotish modules

- Kundali North / South / East (Mithila sketch).
- 16 vargas D1–D60 + vimshopaka.
- Vimshottari M/A/P (depth 3). No Yoginī yet.
- Ashtakoota 36 + Mangal (advisory).
- Yogas / Kaal Sarp / Pitra pattern engine.
- Muhurta by purpose; travel from/to or direction.
- Gochar, Sade Sati, Kantaka Śani, Guru gochar.
- Gems (warning stays), varshaphal, prashna.
- Daily horoscope is **rashi-generic**, not the user’s kundali.
- Prashna = **question time**, not birth, unless UI says so.

### 9.3 Place search (must keep)

- Gazetteer for common Indian names (Delhi → New Delhi, India 28.61/77.21, **not** Ontario).
- Nominatim `countrycodes=in`.
- Search `दरभंगा` → KSDS **26.5833 / 85.268**, not city 26.15.
- Unmatched (`Zzqxnotacity`) → **empty list**, never Darbhanga.
- Query `india` must not dump every gazetteer city.

### 9.4 i18n

Hindi/English on **every** sheet: Home, Kundali, Vargas, Panchang, Milan, Dasha, Horoscope, Yogas, Nakshatra, Rashi, Muhurta, Gochar, Gems, Varshaphal, Prashna, Learn, Consult, CRM, Admin, Login.

---

## 10. Kundali report work already done (P0–P2)

Plan file: `docs/KUNDALI_REPORT_PLAN.md`. Astrotalk PDF is a **checklist of sections**, not a clone.

### P0 — Frozen (always)

Calc gold, Swiss Eph Drik-only, Hindi/i18n, **no bija grid**.

### P1 — अवकहड़ा + graha table (done)

Branch style: `ui/kundali-avakahada-graha`.

**अवकहड़ा from Chandra** (classical tables, not fitted to commercial PDF numbers):

varṇa, vaśya, yoni, gaṇa, nāḍī, rāśi, rāśi-lord, nakṣatra, nak-lord, caraṇa, tattva, nāma-akṣara, pāyā, plus lagneśa.

Fixture: Moon **52° sidereal** = Taurus 22° = **Rohiṇī pāda 4**:

| Field | EN | HI |
|---|---|---|
| varṇa | Shudra | शूद्र |
| vaśya | Quadruped | चतुष्पद |
| yoni | Serpent | सर्प |
| gaṇa | Manushya | मानव |
| nāḍī | Antya | अन्त्य |
| rāśi / lord | Taurus / Venus | वृष / शुक्र |
| nak / lord | Rohini / Moon | रोहिणी / चन्द्र |
| tattva | Earth | पृथ्वी |
| nāma-akṣara | वू | (Rohiṇī akṣaras ओ वा वी **वू**) |
| pāyā | Iron | लोह (`nakIndex % 4`: 0 स्वर्ण 1 रजत 2 ताम्र 3 लोह) |

**बालादि avasthā** (Bṛhat Jātaka / Jātaka Pārijāta):

- Odd signs (Meṣa…): 0–6 Bāla, 6–12 Kumāra, 12–18 Yuva, 18–24 Vṛddha, 24–30 Mṛta.
- Even signs: **reverse**.
- Tests: Aries 2° Bala; Aries 25.7° Mrita; Taurus 6.1° Vriddha; Taurus 22.3° Kumara.

Graha table columns: graha, rāśi, rāśi-lord, bhāva, nak+pāda, nak-lord, DMS/signDegree, dignity (ucca/nīca), avasthā, vakra.

Keep **12-hour** clocks; do not adopt PDF 24h HMS as UI default.

### P2 — Charts on one sheet (done)

Branch style: `ui/kundali-chandra-surya-chalit`. **UI-only.**

- D1, **Chandra kuṇḍalī** (houses from Moon rashi), **Sūrya kuṇḍalī** (from Sun), **Bhāva chalit** via extra `houseSystem=SRIPATI` request (**do not change default WHOLE_SIGN**), **today’s gochar** overlay from `/gochar`, D9.
- Chandra/Sūrya: same grahas, recount houses; place birth lagna as a body in the house from Moon/Sun. Implemented in `frontend/src/chartViews.ts`.
- Vargas chips labelled: होरा — धन, द्रेष्काण — सहोदर, नवमांश — धर्म/दारा, etc.

**Do not** put Uranus/Neptune/Pluto on SIDDHANTIC kundali.

### P3+ (not done — do not start unless asked)

P3 printable Makaranda PDF → P4 aṣṭakavarga → P5 ṣaḍbala → P6 Yoginī/highlight dasha → P7 doṣa card → P8 interpretive essays (our maps, not Astrotalk prose) → P9 KP as mode → P10 upāya → P11 month panji UI → P12 winter honesty copy → P13 fuller spashta → P14 mobile.

---

## 11. Git history you are matching (work already merged)

Illustrative `main` line (oldest relevant → newest):

```
a48f32f Fix dead nav and silent API failures in the SPA
5ce5f55 Align birth-form fields and restyle remaining SPA pages
c972c9c Fix India place search so unmatched queries are not Darbhanga
e162a3f Lock KSDSU July fixtures; do not retune winter bijas yet
30d831d Lock original KSDSU Kartika/Magha 2016–17 and 2022–23
f47695f Make Hindi/English toggle apply on every sheet
7623e9c Record winter manda/EoT dead end; lock CRM/consult ownership
6a934e4 Wire Swiss Ephemeris for Drik only
0c8328c Show avakahada and a fuller graha table on kundali
c681b70 Show Chandra, Surya, Sripati chalit and gochar on the charts tab
```

Merges are `--no-ff`. Calc branches `p0/*`, UI branches `ui/*`.

---

## 12. How to run (acceptance environment)

Need **JDK 21**, **Maven 3.9**, **Node 20**.

```bash
# API
export JAVA_HOME=…/java-21…
mvn -q spring-boot:run          # :8080 bind 0.0.0.0

# Web
cd frontend && npm install && npm run dev
# vite --host 0.0.0.0 --port 5173  proxy /api → 127.0.0.1:8080
```

Smoke:

```bash
curl -s http://localhost:8080/api/v1/public/health
curl -s 'http://localhost:8080/api/v1/jyotish/panchang?date=2022-07-29'
mvn test
cd frontend && npx tsc --noEmit
```

Keep preview up while working.

**Stale docs:** `README.md` and `docs/INTERN_HANDOVER.md` still mention 26.1542/85.8918 and “Swiss not wired”. Prefer `PRD.md` `RULES.md` `MEMORY.md` `ARCHITECTURE.md` **and this file**. `docs/PROJECT_STATUS.md` is an Aug 2026 snapshot.

---

## 13. Tests that must stay green

| Test | Locks |
|---|---|
| `KsdsuGoldenTest` | July 2016/2022/2025 + Kartika/Magha floors |
| `SunriseMithilaTest` | 2022 SR/SS 4 min; T1; purnima 22 min; 2016 T16 |
| `SwissEphDrikTest` | SE files; J2000 Sun; Drik≠SS |
| `AvakahadaTest` | Rohiṇī 4; baladi odd/even |
| `LocationServiceTest` | Delhi India; KSDS Darbhanga; no false fallback |
| `AstroMathTest` | JD, norm360, ayanamsa range |
| `PanchangLimbTest` | limb structure |

**Never** weaken July assertions to pass a winter fit.

---

## 14. Verification checklist (agent self-test)

### Accuracy

- [ ] `mvn test` green.
- [ ] 2022-07-29 SIDDHANTIC: शुक्ल 1, पुष्य, tithi till ~रा. 12:04, nak ~दि. 9:53, SR/SS ~5:20/6:50.
- [ ] 2016-07-24 tithi ~रा. 10:16 (PM), not a morning clock.
- [ ] Switching mode to DRIK **changes** Sun/Moon; SIDDHANTIC Sun ≠ Swiss Sun.
- [ ] Changing ayanamsa changes nakshatra walls, not tithi number logic.
- [ ] A random date in 1800 and 2100 does not crash and does not use a hardcoded table.

### Defaults / UX

- [ ] Fresh load: Hindi, Darbhanga KSDS 26.5833/85.268, Makaranda, SIDDHANTIC, whole sign.
- [ ] Hindi kundali letters Devanagari; English toggle flips **all** listed pages.
- [ ] Panchang shows **तक**, not start-as-end.
- [ ] 12-hour clocks everywhere users see time.
- [ ] Search `delhi` → India; search garbage → not Darbhanga.
- [ ] Travel muhurta asks from/to or direction.

### Kundali P1/P2

- [ ] Basic tab: अवकहड़ा + lagneśa.
- [ ] Moon 52° → Rohiṇī 4, शूद्र, सर्प, लोह, वू.
- [ ] Charts tab: D1, Chandra, Sūrya, Sripati chalit, gochar, D9.
- [ ] Default house system still WHOLE_SIGN after viewing chalit.

### Process

- [ ] No bija constant changed.
- [ ] No PAT/JWT in git.
- [ ] Calc and UI not mixed in one commit if both moved.
- [ ] `tsc --noEmit` clean.

---

## 15. How to work if starting from an older commit

Do **in this order** (same as we did):

1. **Fix SPA** dead nav / silent APIs; India place search; Hindi i18n all sheets; restyle (not clone).
2. **Lock July KSDSU gold tests.** Implement SIDDHANTIC SS+bija + EoT sunrise until July fixtures pass.
3. Transcribe winter KSDSU pages; add **identity + floor** tests; **do not** retune bijas.
4. Record winter manda/EoT grid as a dead end in MEMORY.
5. Wire **Swiss Eph Drik-only**; commit ephe files or load path; tests prove SS unchanged.
6. **P1** अवकहड़ा + graha table (UI branch + small `Avakahada` class — no engine retune).
7. **P2** Chandra/Sūrya/Chalit/gochar (UI branch; Sripati via existing engine flag).

If the repo already contains this work, **do not redo it**. Your job is then to **preserve** it and only continue when asked (P3+).

---

## 16. What failure looks like (automatic fail of this agent test)

- Default place or ayanamsa changed.
- SIDDHANTIC Sun/Moon taken from Swiss/Meeus.
- July 2016/2022 tests skipped, loosened, or broken.
- Winter closed by a 1-D bija / EoT scale / date `if`.
- Mithila 2027 fitted.
- Darbhanga shown for unknown place search.
- English-only kundali in Hindi mode.
- 24-hour clocks as the user-facing default.
- Limb **start** shown as the panji time.
- Secrets committed.
- Astrotalk prose/latlon cloned.
- Microservices / Next rewrite.

---

## 17. Owner voice (constraints in their terms)

- Monolithic Spring Boot/Java, React 18+TS web, RN+TS mobile, H2/Postgres, Swiss Ephemeris, Nominatim, MUI. **One module** for all paddhatis.
- Default Darbhanga **KSDS 26.5833N/85.268E**, ayanamsa Surya Siddhanta (Makaranda)-Mithilanchal, Makaranda panchang; Drik vs Siddhantic toggle. Hindi default + English. Error-free.
- More precise to KSDSU **without losing** Jul 2016 / Jul–Aug 2022.
- Do not deviate from the winter/gold plan while adding report sections; **sequence them**; default remains Makaranda SIDDHANTIC.
- Raise if data needed.

---

## 18. Deliverable if you rebuild

A running app + green tests matching §13–14. Update `MEMORY.md` only with **new measured clocks**, never with a silent bija change.

If you cannot hit July gold without a date hack, **stop and report**. That is success of honesty, not failure.

---

*End of brief. Code and tests in the repo are the numeric source of truth; this file is the complete instruction set required to reach the same accuracy and product behaviour.*

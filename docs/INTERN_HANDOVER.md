# Intern handover — Makaranda Jyotish

Welcome. You are joining a **monolithic Vedic astrology product** aimed at Mithilanchal (Darbhanga / Madhubani) practice: Makaranda panchang, Surya Siddhanta ayanamsa by default, Drik comparison on a toggle, plus CRM and consultations.

Read this file first, then `PROJECT_STATUS.md`, then `README.md`. Do not start with Swiss Ephemeris or a rewrite.

---

## 1. What we are building (the “why”)

AstroSage-class tools exist, but they default to **Lahiri + Drik**. Maithil panjikars and many Mithila families still live in **Makaranda / Surya Siddhanta** timekeeping. This product’s differentiator is:

1. **Makaranda / SS ayanamsa is the default**, not a hidden dropdown.
2. **Siddhantic vs Drik** is a first-class toggle (same birth data, two ganitas).
3. **Darbhanga** is the default place, not Delhi.
4. **East-Indian / Mithila kundali** is a first-class chart style.
5. Later: CRM + booking so a working Jyotishi can run a practice, not only hobbyists.

If a change makes Lahiri-only code paths easier but breaks the Siddhantic default, it is the wrong change.

---

## 2. What is expected of you

### You own

- Small, reviewable PRs that **do not silently change planetary degrees** without a test.
- Clear commit messages (`fix(panchang): tithi rollover at 12° elongation`).
- Updating `PROJECT_STATUS.md` when you finish or abandon a row in the matrix.
- Asking a Jyotishi / mentor before “correcting” traditional rules (Nadi dosha, Mangal houses, Kaal Sarp names). There are schools; we document which one we implement.

### You do not own (without a written go-ahead)

- Rewriting React into Next.js, or Spring into Node, or the monolith into microservices.
- Marketing copy that says “NASA-level” or “AI predictions” until those are actually true.
- Prescribing gemstones as medical advice.
- Pushing to production with the demo JWT secret.

### Definition of done for your tickets

- [ ] Works on web against local API (`npm run dev` + `mvn spring-boot:run`).
- [ ] Happy path + one error path (invalid date, missing JWT, Nominatim down).
- [ ] If you touch `calc/**`, add or extend a unit test with a **known fixture**.
- [ ] No new secrets in git.
- [ ] Status file updated.

---

## 3. How to run the project (day 1)

Need **JDK 21**, **Maven 3.9**, **Node 20**.

```bash
# API — http://localhost:8080
cd makaranda
mvn spring-boot:run

# Web — http://localhost:5173  (proxies /api → 8080)
cd frontend
npm install
npm run dev
```

Checks that the ganita is alive:

```bash
curl -s http://localhost:8080/api/v1/public/health
curl -s http://localhost:8080/api/v1/jyotish/panchang | head
```

Demo logins (seeded once):

| Email | Password | Role |
|---|---|---|
| user@makaranda.app | user123 | CLIENT |
| astro@makaranda.app | astro123 | ASTROLOGER |
| admin@makaranda.app | admin123 | ADMIN |

Swagger: http://localhost:8080/api/swagger  
H2 console: http://localhost:8080/h2 (dev only).

If seed data vanished, delete `makaranda/data/` and restart — `DataSeeder` runs when the user table is empty.

Mobile (`mobile/`) is Expo. It calls `http://127.0.0.1:8080` — that **will not work on a physical phone**. You will need a LAN URL or env flag; that is a valid first ticket.

---

## 4. Mental model of the monolith

```
Browser (React)  ─┐
Expo app         ─┼──►  HTTP JSON  /api/v1/**  ──►  Spring Boot
                  ┘                                      │
                         JyotishService                  │
                           ├─ ChartBuilder               │
                           │    └─ EphemerisEngine       │
                           │         ├─ DRIK (JPL+Meeus) │
                           │         └─ SIDDHANTIC (SS)  │
                           ├─ Panchang / Dasha / Match   │
                           └─ InterpretationEngine       │
                                                         │
                         JPA  (H2 file / Postgres)       │
                         static/  (Vite production build)│
```

**One process, one database, one API.** Web is not a separate backend. Do not add a second Node API.

Request flow for a kundali:

1. UI `BirthForm` fills `birth` in `frontend/src/state.tsx`.
2. `POST /api/v1/jyotish/kundali` with `BirthRequest` (`dateTime`, `lat`, `lon`, `ayanamsa`, `panchangMode`, `houseSystem`, …).
3. `JyotishController` → `JyotishService.chartPayload`.
4. `ChartBuilder.build`:
   - local time → Julian Day UT (`AstroMath.julianDayUt`)
   - `EphemerisEngine.compute(jd, mode)` → **tropical** longitudes
   - subtract `AyanamsaSystem.ayanamsa(jd)` → **sidereal**
   - lagna from RAMC + latitude; houses; vargas; vimshopaka
5. Payload also attaches interpretation, yogas, gemstones, dasha tree.
6. `KundaliChart.tsx` only **draws** `house` + `signIndex`. It does not compute astrology.

If the drawing looks wrong but the table of grahas looks right, fix SVG. If the table is wrong, fix Java, not React.

---

## 5. Repository map

```
makaranda/
  pom.xml                          Spring Boot 3.3 / Java 21
  README.md                        how to run
  docs/PROJECT_STATUS.md           done vs remaining  ← keep current
  docs/INTERN_HANDOVER.md          this file
  src/main/java/com/makaranda/
    MithilaJyotishApplication.java
    calc/                          PURE ganita, no Spring
      AstroMath.java
      VedicConstants.java          rashis, nakshatras, dasha years, friendships
      ephemeris/                   planets + ayanamsa + Drik/SS
      vedic/                       ChartBuilder, Vargas, Varshaphal, Prashna
      dasha/                       Vimshottari only
      panchang/ match/ yoga/ transit/ muhurta/ interpret/
    dto/BirthRequest.java          the JSON the UI posts
    service/JyotishService.java    orchestration + Jackson
    web/*Controller.java           HTTP
    domain/ repo/                  JPA
    security/                      JWT
    report/PdfReportService.java
    seed/DataSeeder.java
  src/main/resources/application.yml
  src/test/java/.../AstroMathTest.java
  frontend/                        Vite React MUI
    src/pages/Pages.tsx            almost all screens (large file)
    src/components/KundaliChart.tsx
    src/components/BirthForm.tsx
    src/api.ts  state.tsx  theme.ts
  mobile/                          Expo, 3 screens, duplicate API calls
```

**Rule:** ganita lives in `calc/` with **no** `@Service` and **no** HTTP. Spring wrappers only in `service/` and `web/`. That is how we will unit-test charts without Tomcat.

---

## 6. Domain language (learn this before coding matching/panchang)

| Term | Meaning in this codebase |
|---|---|
| Tropical longitude | Angle on the ecliptic from the vernal equinox (Drik engine output) |
| Sidereal longitude | Tropical minus ayanamsa (what Kundali uses) |
| Ayanamsa | That difference. Default `SURYA_SIDDHANTA_MAKARANDA` |
| Drik | Apparent/ephemeris positions (`PanchangMode.DRIK`) |
| Siddhantic | Surya Siddhanta mean + corrections (`SIDDHANTIC`) |
| Lagna | Ascendant, house 1, whole-sign by default |
| Rashi / signIndex | 0 = Mesha … 11 = Meena |
| Nakshatra | 13°20′ slices; pada = 3°20′ |
| Vimshottari | 120-year dasha from Moon nakshatra lord |
| Ashtakoota | 36-guna milan from both Moons |
| Vimshopaka | 20-point strength from 16 vargas |
| Gochar | Transit chart vs natal Moon/Lagna |
| Prashna | Chart of the **question time**, not birth |
| Varshaphal | Solar return (Sun back to natal Sun) |

Defaults (must remain unless the request overrides them):

- Place **Darbhanga** 26.1542 N, 85.8918 E, `Asia/Kolkata` (+5.5)
- Ayanamsa **SURYA_SIDDHANTA_MAKARANDA**
- Mode **SIDDHANTIC**
- Houses **WHOLE_SIGN**

---

## 7. Coding conventions

### Java

- No Lombok. Records are used for calc results (`PlanetBody`, `FullChart`).
- Prefer `LinkedHashMap` in API maps so JSON field order is stable for debugging.
- Longitudes: always `AstroMath.norm360` before sign/nakshatra.
- Do not call Nominatim from the browser; use `/api/v1/location/search` (User-Agent is set server-side).

### React

- One birth object in context (`useApp()`). Pages should not invent a second default place.
- `fetch` wrapper in `api.ts`. JWT in `localStorage` key `makaranda.token`.
- MUI **v5** Grid still uses `item xs={12}`. Do not upgrade MUI without migrating Grid.

### What a “small PR” looks like

Good: “Add Yogini dasha next to Vimshottari, endpoint `/jyotish/dasha?system=YOGINI`, table on Dasha page, 2 fixture tests.”  
Bad: “Refactor entire Pages.tsx into 20 files + change ayanamsa formula + new UI kit.”

If `Pages.tsx` is painful, splitting **one** page out per PR is welcome.

---

## 8. First-week onboarding (do in order)

**Day 1 — Run and click**  
Cast a kundali for yourself (or 15 Aug 1992 06:12 Darbhanga). Toggle Siddhantic/Drik. Download PDF. Login as all three roles. Skim Swagger.

**Day 2 — Trace one number**  
Pick the Moon’s sidereal degree on that chart. Trace it on paper: JD → `EphemerisEngine.moon` or SS moon → minus ayanamsa → nakshatra index. Write the trace in your notes. If you cannot do this, do not edit `calc/`.

**Day 3 — Add a test**  
Add a fixture: “for JD 2451545.0 (J2000 noon UT), Drik Sun tropical longitude is between 280° and 281°” (already similar in `AstroMathTest`). Then add **one natal fixture** agreed with your mentor (lagna rashi + Moon nakshatra).

**Day 4–5 — Pick a P0/P1 ticket from the list below**, not a rewrite.

---

## 9. Good intern tickets (copy into your tracker)

Pick **one** at a time. Estimate is calendar days for a  intern who already ran the app.

| ID | Ticket | Lane | Est. | Acceptance |
|---|---|---|---|---|
| T1 | Golden kundali tests (5 charts, Lahiri + Makaranda) | Java test | 3–5d | CI fails if rashi/nakshatra drift |
| T2 | Month panchang calendar UI | React | 2–3d | Uses existing `GET /panchang/month` |
| T3 | Stop calling rule-engine “AI”; add “Parampara reading” label + disclaimer | React | 0.5d | Copy honest |
| T4 | CRM ownership checks (only owner/astrologer/admin) | Spring Security | 1–2d | Cannot delete someone else’s client |
| T5 | Expo API base URL from env / settings screen | RN | 1d | Phone on Wi-Fi hits laptop API |
| T6 | Wire optional LLM behind flag, fallback to `InterpretationEngine` | Java | 3–4d | No key → still returns reading |
| T7 | Yogini dasha | Java + UI | 4–6d | Parallel to Vimshottari, documented |
| T8 | Milan PDF | Java PDF + button | 2–3d | 36 gunas table in PDF |
| T9 | Astrologer weekly slots + overlap rejection | JPA + UI | 5–7d | Cannot double-book |
| T10 | Split `Pages.tsx` into `pages/*.tsx` **without** behaviour change | React | 2d | Routes identical |

**Do not start Swiss Ephemeris JNI as ticket 1.** If you want accuracy work, start with tests (T1). Adapter work is a mentor-scoped P0.

---

## 10. How to change ganita without breaking the product

1. Add a test with **input JD + expected range** (not a screenshot).
2. Change only `EphemerisEngine` / `AyanamsaSystem` / the specific calculator.
3. Run `mvn test`.
4. Hit `POST /kundali` for the same birth in **both** modes and paste a before/after of Sun, Moon, Lagna in the PR.
5. If Moon nakshatra changes, Vimshottari **will** change — call that out.

Never “fix” SS ayanamsa to match Lahiri. They **must** differ; that is the product.

---

## 11. Auth, roles, and what UI should hide

| Role | Intended access |
|---|---|
| CLIENT | Own CRM profiles, own bookings, all public ganita |
| ASTROLOGER | Clients assigned to them, own consultations, ganita |
| ADMIN | `/api/v1/admin/**`, user enable/disable, articles |

Ganita endpoints are **public on purpose** (hobbyists can cast without login). That is a product choice; rate-limit later, do not silently require JWT on `/kundali` without a product decision.

JWT: `Authorization: Bearer <token>`, HS256, secret in `application.yml` (must move to env before any deploy).

---

## 12. Product rules (Jyotish + ethics)

- Default location is Darbhanga even when the user is in Gurugram — until they search a place.
- Gemstone copy must keep the existing warning: **no Neelam/Gomed without full chart + dasha**.
- Matching “not recommended” is **not** a ban on marriage; wording should stay advisory.
- Prashna uses **now** (or the moment the astrologer clicks), not the birth chart, unless the UI says otherwise.
- Daily horoscope is **rashi-generic**. Do not imply it is the user’s kundali.
- Prefer mantra/dana language over fear-based dosha copy.

If a pandit reviewer disagrees with a rule (e.g. Mangal in 2nd house), add a **school flag** rather than overwriting silently.

---

## 13. How we communicate

- Questions on ganita: write the **JD, place, ayanamsa, mode**, and which number looks wrong.
- PR description template:

```
## What
## Why
## Chart fixture (if calc)
- Birth: ...
- Before: Moon ... Dasha ...
- After: ...
## How to test
```

- Update `docs/PROJECT_STATUS.md` in the same PR when a matrix cell changes Done/Partial.

---

## 14. People / accounts you will need (ask your manager)

- Mentoring Jyotishi for fixture charts (ideally one Makaranda panji + one Lahiri printout).
- Nominatim usage: fair-use; no tight loops in the browser.
- If you implement Razorpay/LLM: **keys in env**, never in the repo. Current LLM config:

```yaml
makaranda.llm.enabled: false
MAKARANDA_LLM_URL / MAKARANDA_LLM_KEY / MAKARANDA_LLM_MODEL
```

There is **no client code yet**. Enabling the flag does nothing until you write it.

---

## 15. When you are stuck

1. Reproduce with curl (bypass React).
2. Log Julian Day and tropical Sun/Moon in a unit test, not `System.out` in the controller.
3. Read `JyotishService.toInput` — nulls become Darbhanga defaults; many “bugs” are missing JSON fields.
4. Remember Jackson on Java records: field names in JSON follow record accessors (`signSa`, `siderealLon`).

You do not need to finish AstroSage in an internship. You need to leave the ganita **more tested**, the Mithila defaults **intact**, and one user-visible slice (calendar, milan PDF, slots, or honest copy) **actually better**.

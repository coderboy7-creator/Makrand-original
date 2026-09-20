# Makaranda Jyotish — Project Status

**Product:** Mithilanchal Vedic astrology platform (AstroSage / AstroTalk / KundaliPro class)  
**Architecture:** Monolith (one Spring Boot JAR + React SPA + Expo mobile client)  
**Date of this snapshot:** 29 August 2026  
**Version in repo:** `1.0.0` (foundation / MVP, not store-ready)

This file is the source of truth for **what is built**, **what is only sketched**, and **what is not started**. Pair it with `INTERN_HANDOVER.md` for how the code is organised.

---

## 1. Verdict in one paragraph

The **calculation + REST + web UI spine is real and runnable**. You can cast a Kundali (Darbhanga default), flip Drik vs Siddhantic, run 16 vargas, Vimshottari dasha, Ashtakoota milan, panchang, muhurta, gochar, varshaphal, prashna, a 1-page PDF, a rule-based reading, a demo CRM, mock paid video booking (Jitsi), and a thin admin/learning layer.

It is **not** yet a production clone of AstroSage: Swiss Ephemeris is not wired, mobile is a 3-screen Expo stub, payments/notifications/LLM are stubs, only one dasha system exists, tests are thin, and several “⭐” product features are CRUD-only.

**Honest completion: ~55–60% of the original feature list as working product; ~80% of the original feature list as “has an endpoint and a page”.**

---

## 2. Stack vs original recommendation

| Component | Recommended | In repo now | Gap |
|---|---|---|---|
| Backend | Spring Boot 3.2 + Java 17 | Spring Boot **3.3.3** + **Java 21** | Fine; intern should not downgrade |
| Frontend web | React 18 + TypeScript + MUI | React 18 + TS + **MUI 5** | MUI 6 not used (Grid API) |
| Mobile | React Native + TS | Expo 51 app, **3 screens only** | No 80% code share with web |
| DB dev | H2 | H2 file `./data/makaranda` | OK |
| DB prod | PostgreSQL | `application-prod.yml` exists, **untested in this workspace** | Need a real DB + migration strategy |
| Calculations | Swiss Ephemeris | **JPL Keplerian + Meeus Moon** (Drik) and **Surya Siddhanta** (Siddhantic) | Not NASA-level; interface ready to swap |
| Location | OSM Nominatim | Backend proxy `/api/v1/location/search` | Needs User-Agent discipline / rate limits |
| UI | MUI | Dark maroon/gold theme | No design system tokens file |
| Auth | (implied) | JWT + BCrypt, 3 seeded roles | Default JWT secret in yaml |
| Reports | PDF | OpenPDF 1-page kundali | No branded multi-page templates |
| LLM | Custom LLM | Config keys only (`makaranda.llm.*`) | **No `LlmClient` class; never called** |

---

## 3. Feature matrix (original brief)

Legend: **Done** = usable end-to-end · **Partial** = API and/or UI exists but incomplete · **Not started**.

### 3.1 Platform / architecture

| Item | Status | Notes |
|---|---|---|
| Monolithic deploy | **Done** | Single Spring Boot app; Vite build target is `src/main/resources/static` |
| Web app | **Done** | All major routes in `frontend/src/pages/Pages.tsx` |
| Android + iOS | **Partial** | `mobile/` Expo app: Home, Kundali (now/Darbhanga), Horoscope. No store configs beyond bundle ids |
| Shared 80% web/mobile code | **Not started** | Duplicate fetch logic; no shared package |
| PWA / installable mobile web | **Partial** | `manifest.json` only; no service worker, no push |
| OpenAPI / Swagger | **Done** | `/api/swagger` (springdoc) |

### 3.2 Ganita (calculation engine)

| Item | Status | Notes |
|---|---|---|
| Birth chart any location | **Done** | `BirthRequest` lat/lon/tz; Nominatim search |
| Darbhanga default | **Done** | `application.yml` + `defaultBirth()` in web |
| North / South / East Indian styles | **Done** | SVG in `KundaliChart.tsx` (East is a Mithila-style sketch, not a panjikar-certified layout) |
| All ayanamsas | **Partial** | 12 options in `AyanamsaSystem`. Formulas are **documented approximations**, not Swiss-Eph bit-identical |
| Default SS Makaranda ayanamsa | **Done** | 54″/year from 499 CE |
| Drik vs Siddhantic toggle | **Done** | `PanchangMode`; both produce graha longitudes |
| Swiss Ephemeris / NASA-level | **Not started** | `EphemerisEngine` is the swap point |
| House systems | **Partial** | Whole sign (default), Equal, Sripati. **No Placidus / Koch** |
| 16 Vargas D1–D60 | **Done** | `VargaCalculator` + `/vargas` UI |
| Vimshopaka strength | **Done** | Shodashavarga weights in `VedicConstants` |
| 5+ dasha systems | **Partial** | **Vimshottari only** (Maha / Antar / Pratyantar). No Yogini, Char, Kalachakra, Ashtottari, etc. |
| True vs mean Rahu | **Partial** | Mean node only |

### 3.3 Product modules (user-facing)

| Feature | Backend | Web UI | Mobile | Remaining to call it “finished” |
|---|---|---|---|---|
| Birth Kundali | Done | Done | Partial (now-cast only) | Birth datetime picker on mobile; save/share chart |
| Divisional charts | Done | Done | Not started | Per-varga PDF; D9 comparison view |
| Panchang (daily 5 limbs + muhurta windows) | Done | Done | Home snippet only | Month calendar UI (API `GET /panchang/month` unused in UI) |
| Kundali Milan 36 gunas + Mangal | Done | Done | Not started | Cancellation rules UI; Navamsa milan; PDF milan |
| Vimshottari + predictions | Done | Done | Not started | Current-period highlight; dasha-specific essays beyond templates |
| Rashi encyclopedia | Done | Done | Not started | Images, audio, intern quiz |
| Daily horoscope + lucky elements | Done | Done | Done (list) | **No notifications**; text is rule-templated, not LLM |
| Yogas & doshas | Done | Done | Not started | More yoga catalogue; Kaal Sarp types need chart-audit |
| 27 Nakshatras | Done | Done | Not started | Pada-level career/marriage notes |
| Muhurta (marriage/business/travel/property) | Done | Done | Not started | Clock-time slots inside the day, not only date scores |
| Transit / Sade Sati / Jupiter | Done | Done | Not started | Animated gochar; date-range Sade Sati timeline |
| Gemstone recommendations | Done | Done | Not started | Disclaimer workflow; never auto-sell Neelam |
| Varshaphal | Done | Done | Not started | Tajika yogas; Muntha house essays; faster search (brute-force now) |
| Prashna | Done | Done | Not started | Astrologer nadi (left/right breath) field; yes/no model is heuristic |
| Professional PDF reports | Partial | Download button | Not started | Multi-page branded templates, charts as images, bilingual |
| Astrologer CRM | Partial | Basic add/list | Not started | History of readings, attachments, follow-ups, search, permissions |
| Consultation & booking ⭐ | Partial | Book + mock pay + Jitsi link | Not started | Real slot calendar, Razorpay/Cashfree, reminders, recordings policy |
| Admin panel ⭐ | Partial | Counts only | Not started | Ayanamsa config UI (settings API exists), content CMS, refunds |
| Learning / Insight ⭐ | Partial | Encyclopedia + 5 articles | Not started | Curriculum, intern assignments, progress |
| Custom LLM interpretation ⭐ | Not started | Shows rule-engine text labelled “AI” | — | Wire `makaranda.llm` to an HTTP client; never send PII without consent |

---

## 4. What is completed (engineering checklist)

### Backend (`src/main/java/com/makaranda`)

- [x] Spring Boot monolith, JPA, H2, JWT filter, CORS, method security (`ROLE_ADMIN` / `ASTROLOGER` / `CLIENT`)
- [x] Seeded demo users, one client, one booking, five learning articles (`DataSeeder`)
- [x] Dual ephemeris: Drik (JPL+Meeus) and Siddhantic (SS mean + manda/sighra)
- [x] Ayanamsa catalogue with Makaranda default
- [x] Chart builder: lagna, 9 grahas, houses, vargas, vimshopaka
- [x] Panchang: tithi, nakshatra, yoga, karana, vara, sunrise/set, Rahu/Yamaganda/Gulika, Abhijit, Brahma muhurta
- [x] Ashtakoota + Mangal dosha heuristic
- [x] Yoga/dosha detector (Gajakesari, Pancha Mahapurusha, Dhana, Kaal Sarp, Pitra indicative, etc.)
- [x] Gochar + Sade Sati / Kantaka / Guru notes
- [x] Muhurta date search by purpose
- [x] Varshaphal (solar-return search) + Prashna + gemstone heuristic
- [x] Rule-based interpretation engine (houses, dasha themes, remedies)
- [x] Encyclopedia JSON (12 rashis, 9 grahas, 27 nakshatras, 12 bhavas)
- [x] Nominatim proxy
- [x] OpenPDF kundali report
- [x] REST under `/api/v1/**` (jyotish, auth, crm, consult, admin, learn, location, public)
- [x] Unit tests: Julian Day, Sun-at-J2000 sanity, Makaranda ayanamsa range, `norm360` (`AstroMathTest`)

### Web (`frontend/`)

- [x] Vite + React Router + MUI dark Mithila theme
- [x] Global birth form (place search, ayanamsa, Drik/Siddhantic, house system)
- [x] Pages: Home, Kundali, Vargas, Panchang, Milan, Dasha, Horoscope, Yogas, Nakshatra, Rashi, Muhurta, Gochar, Gems, Varshaphal, Prashna, Learn, Consult, CRM, Admin, Login
- [x] SVG kundali (3 styles)
- [x] PDF download from kundali page
- [x] JWT stored in `localStorage`

### Mobile (`mobile/`)

- [x] Expo app skeleton, dark theme, stack navigator
- [x] Panchang home, simple kundali cast, rashi horoscope list

### Ops

- [x] `README.md` run instructions
- [x] `application-prod.yml` PostgreSQL profile
- [x] Swagger UI

---

## 5. What is remaining (backlog, ordered)

Work is grouped so an intern can pick a lane. **P0 = needed before any real Jyotishi beta. P1 = product-complete vs brief. P2 = polish / scale.**

### P0 — Correctness, safety, beta

1. **Chart audit against known Kundalis**  
   Compare 10–20 published charts (Lahiri + SS) vs this engine. Log degree errors. Fix lagna/Moon first (they drive dasha + milan).
2. **Swiss Ephemeris adapter** (or astronomy-engine) behind `EphemerisEngine`  
   Keep Siddhantic path; make Drik the high-accuracy path.
3. **Expand automated tests**  
   Julian, ayanamsa at fixed JD, tithi on a known amavasya, Vimshottari balance for a known Moon, Ashtakoota score fixtures.
4. **Do not call the current reading “AI / LLM”** until an LLM is wired — or wire it properly with a system prompt + chart JSON (see handover).
5. **Secrets & prod hygiene**  
   Externalise JWT secret, disable H2 console, replace in-yaml password, add Flyway/Liquibase, test PostgreSQL profile.
6. **CRM/consult authZ**  
   Today delete-client and some status changes do not check ownership. Fix before real users.
7. **Disclaimer + medical/legal copy**  
   Gemstones, doshas, matching “not recommended” verdicts.

### P1 — Finish the original brief

8. **Dasha systems 5+** — Yogini, Char, Kalachakra, Ashtottari, Chara (Jaimini) as plugins next to `VimshottariDasha`.
9. **Month panchang UI** — consume `GET /api/v1/jyotish/panchang/month`.
10. **Muhurta intra-day** — return clock windows, not only good dates.
11. **PDF pack** — kundali + navamsa + dasha + yogas + milan; embed SVG; Hindi/English.
12. **Real booking** — astrologer weekly slots, conflict check, time zone, cancel/reschedule.
13. **Payment gateway** — Razorpay/Cashfree order + webhook; replace `MOCK-` refs.
14. **Admin ayanamsa/content config UI** — `AppSetting` is unused by the engine at runtime (engine reads `application.yml` / request body).
15. **Learning module** — structured courses, not only 5 seed articles.
16. **React Native feature parity** — or wrap the web app in a WebView for v1 mobile and be honest about it.
17. **Horoscope notifications** — FCM / web push; currently zero.
18. **Custom LLM** — implement client; feature-flag; fallback to `InterpretationEngine`.
19. **i18n** — English + Hindi + Maithili strings.

### P2 — Differentiation / scale

20. Shared TS types package for web + mobile.  
21. Placidus / KP houses if KP users are in scope.  
22. True Chitra using a real star catalogue.  
23. Email/SMS OTP login.  
24. Observability (request logs, calc timing, error IDs).  
25. Load test varshaphal (it brute-forces hours).  
26. Design QA on East-Indian chart vs a Maithil panji sample.  
27. App Store / Play listing, privacy policy, account deletion.

---

## 6. Known limitations (do not hide these from stakeholders)

- Planetary **degree accuracy in Drik is tens of arcminutes**, Moon maybe ~0.1–0.5°. Enough to be wrong on a rashi *boundary*. Never market as “NASA-level” until Swiss Eph is in.
- Siddhantic manda/sighra is a **simplified SS**, not a full Makaranda karana implementation with bija sanskar as used by living panjikars.
- Sunrise/sunset and Rahu Kalam are **civil approximations**.
- Yoga detection is **boolean pattern matching**, not a complete Parashara/Jaimini catalogue.
- Kaal Sarp “type” is derived from Rahu house — traditional names vary by school.
- Daily horoscope does **not** use the user’s birth chart; it is rashi-generic + today’s panchang.
- “AI interpretation” is **template composition**, not a model.
- Payments are **fake**. Video is a **public Jitsi URL**.
- Unit test suite does **not** lock kundali output. Refactors can silently change charts.
- `WebConfig` SPA fallback + `SecurityConfig` are good enough for demo; review before internet exposure.

---

## 7. API surface (implemented)

| Method | Path | Auth |
|---|---|---|
| GET | `/api/v1/public/health` | Public |
| GET | `/api/v1/public/config` | Public |
| GET | `/api/v1/public/astrologers` | Public |
| POST | `/api/v1/auth/register` `/login` | Public |
| GET | `/api/v1/auth/me` | JWT |
| POST | `/api/v1/jyotish/kundali` `/vargas` `/dasha` `/yogas` `/interpret` `/match` `/gochar` `/varshaphal` `/prashna` `/gemstones` `/report.pdf` | Public |
| GET | `/api/v1/jyotish/panchang` `/panchang/month` `/horoscope` `/muhurta` | Public |
| GET | `/api/v1/location/search` | Public |
| GET | `/api/v1/learn/encyclopedia` `/articles` `/articles/{slug}` | Public |
| CRUD | `/api/v1/crm/clients` `/charts` | JWT |
| GET/POST | `/api/v1/consult/**` | JWT (list astrologers also on public) |
| GET/POST | `/api/v1/admin/**` | `ROLE_ADMIN` |
| UI | `/api/swagger` | Public (lock in prod) |

---

## 8. Demo accounts (local seed)

| Role | Email | Password |
|---|---|---|
| Client | `user@makaranda.app` | `user123` |
| Astrologer | `astro@makaranda.app` | `astro123` |
| Astrologer | `kavita@makaranda.app` | `astro123` |
| Admin | `admin@makaranda.app` | `admin123` |

Seeder runs only when `users` table is empty. Deleting `./data/makaranda.mv.db` restates seed.

---

## 9. Suggested definition of “project finished”

Call the project **v1 complete** when all of these are true:

1. Drik longitudes within **1–2 arcminutes** of Swiss Eph / JPL for 1900–2050 (Sun, Moon, five tara grahas, mean/true node documented).
2. 20 golden-chart tests in CI (lagna rashi, Moon nakshatra, dasha lord at birth).
3. Web + one mobile ship (Expo or PWA) covering Kundali, Panchang, Milan, Dasha, Consult.
4. Real payments + slot booking + JWT secret from env.
5. LLM optional, rule-engine default, copy never claims medical/legal certainty.
6. PostgreSQL + migrations + one-command prod run.
7. PDF report a Jyotishi would actually email to a client.

Until then, treat this repo as a **working Mithilanchal MVP / intern training ground**, not an AstroSage replacement.

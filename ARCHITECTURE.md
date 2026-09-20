# ARCHITECTURE — Makaranda Jyotish

**Style:** Modular monolith. One process, one database, one `/api/v1` contract.  
**Do not** split into microservices or add a second Node calculation API.

---

## 1. Deployable

```
Browser (Vite / built SPA)     Expo RN
        │ HTTP JSON                     │
        └──────────────┬────────────────┘
                       ▼
            Spring Boot 3.3.3  Java 21
            port 8080  ·  one JAR
     ┌─────────────────┼──────────────────┐
     │ REST  /api/v1/**│  static/ (Vite)  │
     │ JyotishService  │  JWT + BCrypt    │
     │ calc/*  (pure)  │  JPA             │
     └────────┬────────┴────────┬─────────┘
              ▼                 ▼
     EphemerisEngine      H2 file ./data/makaranda
     PanchangCalculator   PostgreSQL (profile `prod`)
```

Production: `frontend && npm run build` emits into `src/main/resources/static`, then `mvn package` → `makaranda-jyotish-1.0.0.jar`.

Dev: API `:8080`, Vite `:5173` proxies `/api` → 8080.

## 2. Package map

```
com.makaranda
  MithilaJyotishApplication
  calc/                     PURE ganita — no Spring, no HTTP
    AstroMath, VedicConstants
    ephemeris/              EphemerisEngine, AyanamsaSystem, PanchangMode
    panchang/               PanchangCalculator (KSDS place constants)
    vedic/                  ChartBuilder, Varga, Varshaphal, Prashna
    dasha/                  VimshottariDasha
    match/ yoga/ transit/ muhurta/ interpret/
  dto/                      BirthRequest, MatchRequest, MuhurtaRequest
  service/                  JyotishService, LocationService, AuthService
  web/                      *Controller, ApiExceptionHandler
  domain/ repo/             JPA
  security/                 JwtService, JwtAuthFilter
  config/                   SecurityConfig, WebConfig (CORS, SPA fallback)
  report/                   PdfReportService
  seed/                     DataSeeder
```

**Rule:** degrees are computed in `calc/`. React only draws. If the table is wrong, fix Java.

## 3. Request flow — kundali

1. `BirthForm` → app context (`frontend/src/state.tsx`).
2. `POST /api/v1/jyotish/kundali` (`BirthRequest`: dateTime, lat, lon, tzHours, ayanamsa, panchangMode, houseSystem).
3. `JyotishService` → `ChartBuilder.build`:
   - civil time → JD UT (`AstroMath.julianDayUt`)
   - `EphemerisEngine.compute(jd, mode, lonEast)` → tropical-equivalent longitudes
   - subtract `AyanamsaSystem.ayanamsa(jd)` → sidereal
   - lagna from RAMC + φ; houses; vargas; vimshopaka
4. Attach interpretation, yogas, dasha, gemstones.
5. `KundaliChart.tsx` SVG from `house` + `signIndex`.

Missing JSON fields fall back to Darbhanga / Makaranda / SIDDHANTIC (`JyotishService` + `application.yml`).

## 4. Request flow — panchang

1. `GET /api/v1/jyotish/panchang?date=&lat=&lon=&ayanamsa=&mode=`
2. `PanchangCalculator.compute`:
   - **Dinamaan:** `EphemerisEngine.sunriseSunsetLocal` — 50′ depression (34′ refraction + 16′ SD) and **equation of time** so madhyāhna is apparent, not civil 12:00. Matches KSDSU 29 Jul 2022 सू.उ. ५।२० सू.अ. ६।५०.
   - Limbs at sunrise; each end by **bisection** on spashta angle (not linear ghati guess).
   - Clocks formatted 12-hour + दि./सां./रा. + घटिका from sunrise.

### Dual ganita

| Mode | Sun/Moon for limbs | Use |
|---|---|---|
| **SIDDHANTIC** (default) | Surya Siddhanta mean + manda/sighra + **Makaranda paddhati bijas** (constants, not per-date) | KSDSU printed panchang |
| **DRIK** | Meeus/NOAA Sun + truncated ELP Moon, tropical minus ayanamsa | Apparent sky / comparison |

SIDDHANTIC bijas live only in `EphemerisEngine.siddhantic()`:

- moon mean +2.31°
- sun mean −0.19°
- moon apsides −90°
- sun apogee +174.3°

Do not add Meeus terms into SIDDHANTIC. Do not date-switch.

Ayanamsa default `makaranda()` currently tracks **Lahiri/Chitrapaksha** for printed spashta rashi of the modern KSDS volumes (textbook 54″/year from 499 CE remains `SURYA_SIDDHANTA_REVATI`). Tithi is elongation, so ayanamsa cancels for tithi; it still moves nakshatra walls.

## 5. KSDS place constants

`PanchangCalculator`:

| Name | Value |
|---|---|
| `KSDS_AKSHANSH_DEG` | 26 + 35/60 |
| `KSDS_DESHANTAR_GHATI` | 1 + 35/60 east of Ujjain |
| `KSDS_UJJAIN_LON` | 75.768° |
| `KSDS_LON` | Ujjain + desantara × 6° = **85.268°E** |
| `KSDS_PALABHA` | 6 → φ ≈ arctan(6/12) ≈ 26°34′ |

Ahargana for SS uses local longitude vs Ujjain.

## 6. API surface

Prefix `/api/v1`.

| Area | Paths | Auth |
|---|---|---|
| Public | `/public/health` `/config` `/astrologers` | Open |
| Auth | `/auth/register` `/login` `/me` | login public |
| Jyotish | `POST /jyotish/kundali` vargas dasha yogas interpret match gochar varshaphal prashna gemstones `report.pdf` | Open (product choice) |
| Panchang | `GET /jyotish/panchang` `/panchang/month` `/horoscope` `/muhurta` | Open |
| Location | `GET /location/search` | Open (server Nominatim) |
| Learn | `/learn/encyclopedia` `/articles` | Open |
| CRM | `/crm/clients` `/charts` | JWT |
| Consult | `/consult/**` | JWT |
| Admin | `/admin/**` | `ROLE_ADMIN` |
| Docs | `/api/swagger` `/api/docs` | Lock in prod |

## 7. Data

- **Dev:** H2 file `jdbc:h2:file:./data/makaranda`, console `/h2`.
- **Prod:** `application-prod.yml` PostgreSQL via `DATABASE_URL` / user / password. **Flyway not yet.**
- Entities: `User`, `ClientProfile`, `SavedChart`, `Consultation`, `LearningArticle`, `AppSetting`.
- Seed when users table empty: `user@` / `astro@` / `kavita@` / `admin@makaranda.app`.

## 8. Frontend

- Vite + React 18 + TS + MUI 5 (Grid `item xs=` — do not jump to MUI 6 blindly).
- Routes in `frontend/src/App.tsx`; screens in `pages/Pages.tsx`.
- i18n: `i18n.tsx` (hi default). Chart labels: `jyotishLabels.ts`.
- JWT: `localStorage` key `makaranda.token`.
- Place search: `PlaceSearch.tsx` → `/api/v1/location/search` (India bias).

## 9. Mobile

Expo 51 stub (`mobile/`): Home panchang, now-cast kundali, horoscope list. Same API. Physical device needs LAN base URL (not `127.0.0.1`).

## 10. Security notes

- Ganita endpoints are public by design; rate-limit later.
- JWT HS256 secret still in `application.yml` — **env before internet**.
- CRM ownership checks incomplete (P0).
- Nominatim only from `LocationService` with a proper User-Agent.

## 11. Swap points (planned, not done)

- Drik accuracy: Swiss Ephemeris adapter **inside** `EphemerisEngine.drik` / `tropicalSunMoon`.
- LLM: `makaranda.llm.*` config exists; **no client class yet**.
- Payments: replace `MOCK-` consult refs with Razorpay/Cashfree webhooks.

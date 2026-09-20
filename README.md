# Makaranda Jyotish

Mithilanchal-native Vedic astrology platform (AstroSage / AstroTalk / KundaliPro class) — **monolithic** Spring Boot 3 + React 18 web + React Native (Expo) mobile.

Default ganita: **Surya Siddhanta (Makaranda)** ayanamsa, **Siddhantic** panchang, place **Darbhanga (26.1542°N, 85.8918°E)**. One-click **Drik vs Siddhantic** toggle.

## Architecture (monolith)

```
Browser / React Native
        │  HTTP JSON
        ▼
Spring Boot 3.3 (Java 21)  ── one JAR ──► H2 (dev) / PostgreSQL (prod)
   ├─ REST  /api/v1/**
   ├─ Calculation engine (no JNI)
   │     DRIK: JPL Keplerian 1800–2050 + Meeus lunar ELP
   │     SIDDHANTIC: Surya Siddhanta mean motions from Kali Yuga + manda/sighra
   ├─ JPA: users, CRM, consultations, settings, learning
   └─ static/  ← Vite production build of the React SPA
```

There is **one deployable**: `makaranda-jyotish-1.0.0.jar`. The React app is served from the same process. Mobile talks to the same API.

## Feature map

| Feature | Status |
|---|---|
| Birth chart any location + Darbhanga default | Yes — North / South / East (Mithila) |
| 16 Vargas D1–D60 + Vimshopaka | Yes |
| Dasha (Vimshottari M/A/P, expandable) | Yes |
| Ayanamsas (Makaranda default) | Lahiri, Raman, KP, SS Revati, True Chitra, Yukteshwar, Fagan, Pushya, Aryabhata, Tropical |
| Makaranda / Mithila panchang | Native default |
| Drik vs Siddhantic toggle | Yes |
| Web + Android + iOS | React SPA + Expo RN |
| AI interpretation | Rule-composed parampara engine; optional OpenAI-compatible LLM via env |
| CRM | Clients, notes, saved charts |
| Ashtakoota + Mangal dosha | Yes |
| Yogas / Kaal Sarp / Pitra | Yes |
| Muhurta, Gochar, Sade Sati | Yes |
| Gemstones, Varshaphal, Prashna | Yes |
| PDF reports | Branded OpenPDF |
| Consult + Jitsi video + mock UPI | Yes |
| Admin panel | Users, bookings, settings, articles |
| Learning module | Houses, grahas, rashis, nakshatras, articles |

## Run (development)

Prerequisites: JDK 21, Maven 3.9, Node 20.

```bash
# terminal 1 — API
cd makaranda
mvn -q spring-boot:run

# terminal 2 — Vite (proxies /api → :8080)
cd frontend
npm install
npm run dev
```

Open http://localhost:5173

### Demo logins

| Role | Email | Password |
|---|---|---|
| Client | user@makaranda.app | user123 |
| Astrologer | astro@makaranda.app | astro123 |
| Admin | admin@makaranda.app | admin123 |

## Production (single JAR)

```bash
cd frontend && npm install && npm run build   # emits into src/main/resources/static
cd .. && mvn -q -DskipTests package
java -jar target/makaranda-jyotish-1.0.0.jar
```

PostgreSQL: `SPRING_PROFILES_ACTIVE=prod DATABASE_URL=jdbc:postgresql://...`

## Mobile

```bash
cd mobile
npm install
# point API if not localhost
npx expo start
```

iOS/Android store builds: `npx expo prebuild` then open Xcode / Android Studio. The app consumes the same `/api/v1` contract.

## Optional LLM

Set `MAKARANDA_LLM_URL`, `MAKARANDA_LLM_KEY`, `MAKARANDA_LLM_MODEL`. The interpreter already returns structured JSON that can be passed as context.

## Accuracy note

Drik mode uses NASA JPL approximate Keplerian elements (typical error tens of arcminutes for 1900–2050; Moon ~0.1–0.5° with truncated ELP). This is sufficient for rashi/nakshatra work and is structured behind `EphemerisEngine` so Swiss Ephemeris files can be dropped in later for NASA-level arcseconds. Siddhantic mode is *intentionally* different — that is the Makaranda product.

## Product docs

| File | What |
|---|---|
| [PRD.md](PRD.md) | Product requirements, gold standard, defaults |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Monolith, ganita flow, API |
| [RULES.md](RULES.md) | Standing constraints (KSDSU vs Mithila, no date hacks) |
| [DESIGN.md](DESIGN.md) | Mithila UI, 12-hour घं.मि., Hindi kundali |
| [TASKS.md](TASKS.md) | Done / P0 / P1 backlog |
| [MEMORY.md](MEMORY.md) | Fitted bijas, verified clocks, dead ends |

## Layout

```
makaranda/
  pom.xml
  src/main/java/com/makaranda/
    calc/          astronomy + vedic ganita
    domain/ repo/  JPA
    web/           REST
    report/        PDF
  frontend/        React 18 + MUI + TypeScript
  mobile/          Expo React Native
```

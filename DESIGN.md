# DESIGN — Makaranda Jyotish

Visual, UX, and content design for a **Mithila / KSDSU** audience. Implementation today: MUI 5 dark theme in `frontend/src/theme.ts`.

---

## 1. Brand

| Token | Value | Use |
|---|---|---|
| Gold | `#C9A227` | Primary, titles, chart strokes |
| Saffron | `#E07A2F` | Secondary / CTAs |
| Night indigo | `#0B1026` | App background (like a palmyra-leaf panji on a desk at dusk) |
| Paper | `#141A33` | Cards |
| Ivory text | `#F7F1E3` | Primary text |
| Khadi | `#C4BBA8` | Secondary text |
| Radius | 14px paper / 10px buttons | Soft, not Material-3 playful |

Titles: Georgia / Palatino (grantha feel). Body/UI: system-ui. Buttons: no ALL CAPS (`textTransform: none`).

This is **not** a neon AstroTalk palette. Gold on indigo reads as “पञ्जी + temple lamp”.

## 2. Language

- **Default Hindi.** Toggle English in the chrome (Layout).
- Hindi on ⇒ nav, panchang labels, kundali **rashi/graha akshara Devanagari**.
- Numbers on clocks: Western digits are OK in 12-hour form (`5:23 AM`); panji end strings use **दि. 9।53** style from the API.
- Do not mix Latin graha names on an otherwise Hindi kundali.

## 3. Time display (critical)

Users in Mithila read **घंटा.मिनट**, not 24-hour IST.

| API / UI | Example |
|---|---|
| Sunrise/sunset | `5:23 AM` / `6:50 PM` |
| Limb end | `रा. 12।13  30 Jul 2022, 12:13 AM  (47।05 घटी)` |
| Watches | **दि.** 06:00–15:59 · **सां.** 16:00–21:59 · **रा.** else (book uses रा. for 4:03 AM, not प्रातः) |

Panchang page must show **तक** (end), not a start time that looks like the printed column.

## 4. Information architecture

| Route | Purpose |
|---|---|
| `/` | Today’s panchang strip + four doors (कुंडली, पंचांग, मिलान, परामर्श) |
| `/panchang` | Date + place + mode; five limbs with start/end; SR/SS; inauspicious windows |
| `/kundali` | Birth form, North/South/East SVG, graha table, PDF |
| `/vargas` `/dasha` `/yogas` `/milan` | Practice |
| `/muhurta` | Purpose + (travel) from/to or direction |
| `/gochar` `/gems` `/varshaphal` `/prashna` | Advanced |
| `/nakshatra` `/rashi` `/learn` | Encyclopedia |
| `/consult` `/crm` `/admin` `/login` | Practice ops |

Birth form is **global** (one `birth` in context). Pages must not invent a second default city.

## 5. Kundali chart

Three styles in `KundaliChart.tsx`:

- **NORTH** — diamond (North Indian).
- **SOUTH** — 12-house grid.
- **EAST** — Mithila/Bengal-style sketch (not a scanned panjikar plate; refine against a Maithil sample before calling it certified).

When `lang !== "en"`, labels from `jyotishLabels.ts` (मेष, चन्द्र, …). Occupants as Devanagari graha short names.

## 6. Panchang page layout (expected)

1. Place search (India-first) + date + सिद्धान्तिक/दृक् + ayanamsa.
2. Header: वार, पक्ष, तिथि **तक**, नक्षत्र **तक**, सूर्योदय, सूर्यास्त.
3. Table of limbs: name · घटिका · घं.मि. · next limb.
4. Note line: अक्षांश २६।३५, देशान्तर ०१।३५, पल्लभा ६.
5. Rahu / Yamaganda / Gulika / Abhijit / Brahma — 12-hour.

Month grid (API `GET /panchang/month`) is **not** built yet; design it as a printed panji spread (15-day shukla/krishna blocks), not a Gregorian Google Calendar.

## 7. Place search

- Backend Nominatim only.
- Rank **India** hits first; disambiguate “Delhi, India” vs foreign Delhis.
- Default chip: `Darbhanga, Bihar (KSDS २६।३५)`.

## 8. Muhurta / travel

If the purpose is travel and both places can be geocoded, ask **से / तक**. Else ask **दिशा** (E/W/N/S). Do not silently use Darbhanga as destination.

## 9. Voice and ethics in copy

- Parampara, not fear.
- Disclaimers on gems, dosha, matching.
- Demo payments labelled mock.
- “AI” forbidden on the interpretation card until LLM exists.

## 10. Density

Panji users tolerate **tables**. Prefer a dense KSDSU-like table over large empty cards. Gold rules and indigo paper should still keep contrast (WCAG-ish on text vs `#0B1026`).

## 11. Mobile

Expo screens are a stub. v1 honest option: PWA / WebView of the same SPA rather than a second visual language. If native proceeds, reuse gold/indigo tokens; Devanagari on charts still required.

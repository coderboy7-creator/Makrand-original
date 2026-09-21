# Kundali report plan (Astrotalk PDF as a checklist, not a clone)

Source: user PDF `Kundli-Report-ASDF.pdf` (Astrotalk, 55 pages, native ASDF, 10 Jan 1998 05:30, Darbhanga city 26.1522N / 85.8971E).

**Hard rules (never drop)**
- Do not retune Makaranda bijas / apogee / manda / EoT.
- SIDDHANTIC gold (July 2016/2022) stays the gate. Swiss Eph is Drik only.
- Restyle inspired by the PDF’s *sections*, not Astrotalk branding, canned Hindi, or their city lat/lon.
- Default place remains **KSDS 26.5833N / 85.268E**. PDF used gazetteer Darbhanga — do not switch.
- PDF ayanamsa is almost certainly **Lahiri** (lagna Dhanu 8°51′ Mūla, Chandra Vṛṣa Rohiṇī 22°17′). Ours default is **Sūrya Siddhānta (Makaranda)** ≈ Lahiri for modern dates. Compare with ayanamsa = Lahiri; do not change the default.
- Hindi default, Devanagari on kundali, 12-hour clocks, limb **till** times.
- Calc/backend on a **separate branch** from UI. New branch → merge `--no-ff` to `main`.
- Do not copy Astrotalk prose. Interpret from our yogas / houses / dasha lords.

---

## What we already have vs the PDF

| PDF section | We have | Gap |
|---|---|---|
| 01 Basic — birth + panchang | Birth card, tithi/nak/yoga/karana, SR/SS | No **अवकहड़ा**; no lagnesha line; SR not 24h HMS |
| 02 D1 + D9 charts | North/South/East D1, D9 tab, vargas page | No Chandra / Sūrya / Chalit on the kundali sheet |
| Graha table | Rashi, bhava, nak, pada, dignity, retro | No rashi-lord, nak-lord, **avasthā** (Bala…Mrita), DMS seconds |
| Outer planets | No | Uranus/Neptune/Pluto in PDF — **Drik-only optional**, never SIDDHANTIC default |
| Vimśottarī MD+AD | Yes (3 levels) | No “active” highlight, no pratyantar on kundali tab, no yoginī |
| षड्बल / भावबल | Vimśopaka only | Full Ṣaḍbala + Bhāva bala missing |
| 03 KP | KP as **ayanamsa setting** | No Placidus cusps, sub-lord, KP chalit |
| 04 अष्टकवर्ग | No | BAV + SAV |
| 05 All vargas | D1–D60 chips | Not laid out as a report chapter; no gochar overlay chart |
| 06 Dasha narratives | Thin interpret themes | House-based mahādaśā write-up; Yoginī daśā table |
| 07 Report | Summary + career/marriage + gems + some yogas; sade-sati on gochar; mangal on milan | Lagna essay, Budha/Śani essays, Viśeṣa yoga list, Rudrākṣa, Kālasarpa, printable 1-PDF |
| PDF download | `/report.pdf` exists | Too thin vs 55-page checklist |

---

## Sequence (do in this order)

Each row is one git branch, then `--no-ff` to `main`. UI branches do not retune engine bijas.

### P0 — Frozen (always)
Calc gold, Swiss Eph Drik-only, Hindi/i18n, no bija grid.

### P1 — Kundali “01 बेसिक” complete *(next)*
UI+small calc, no engine retune.
- अवकहड़ा from Chandra: varṇa, vaśya, yoni, gaṇa, nāḍī, rāśi, rāśi-lord, caraṇa, tattva, nāma-akṣara, pāyā.
- Basic card: lagneśa, nakṣatra-lord, gender if given.
- Graha table: rāśi-lord, nak-lord, DMS, vakra, bhāva, **avasthā** (Bala/Kumāra/Yuva/Vṛddha/Mṛta from sign degree), ucca/nīca.
- Keep 12-hour clocks; do not adopt PDF 6:35:25 24h as the UI default.

### P2 — Charts the PDF shows on one sheet
- Chandra kuṇḍalī, Sūrya kuṇḍalī, Bhāva chalit (Sripati already in engine).
- Gochar overlay (reuse `/gochar`).
- Vargas chapter labels (Hora = wealth, Drekkana = siblings, …) on existing D-chips.

### P3 — Printable Makaranda report
Expand `PdfReportService` to the same *chapter order* as the PDF (basic → charts → grahas → daśā → extras), **Makaranda / KSDSU branding**, Hindi+English. Not an Astrotalk clone.

### P4 — अष्टकवर्ग
Bhinnāṣṭakavarga per graha + Sarvāṣṭakavarga. Bind to vargas page + PDF. Classical 0–8 bindus; no “fit” to Astrotalk numbers.

### P5 — षड्बल + भावबल
Sthāna, Kāla, Dig, Ceṣṭā, Naisargika, Drik; rūpa + ratio. New calc class, golden tests against a published table (not the PDF’s truncated numbers).

### P6 — Daśā completeness
- Highlight current MD/AD on kundali + `/dasha`.
- Yoginī daśā (8 yoginīs, 36-year cycle).
- Optional pratyantar already in `VimshottariDasha` depth=3 — surface it.

### P7 — Doṣa panel on kundali
Mangal (already in milan), Kālasarpa, Sade-satī (already in gochar). One “दोष” card. Honest “not present / active” — no scare copy.

### P8 — Interpretive report (our paddhati) *(done on `calc/p8-interpret-pdf`)*
Lagna reading, 10th-house livelihood, Budha/Śani by bhāva, mahādaśā by house of lord, Viśeṣa yoga (extend `YogaDetector`: Budhāditya, Veśi, Vāsi, Ubhayachari). Hindi from our maps, not PDF paragraphs.
PDF Hindi is JDK `TextLayout` (HarfBuzz) → PNG — OpenPDF IDENTITY_H does not GSUB Devanagari. Chrome restyled (navy/maroon/gold). No bija retune.

### P9 — KP as a **mode**, not a second app *(calc on `calc/p9-kp-mode`)*
When house system = Placidus/KP and ayanamsa = KP: cusps, star-lord, sub-lord tables. Monolith setting. Do not make KP the default.

### P10 — Upāya
Rudrākṣa by nakṣatra (extend gems). Life-stone already exists. Never auto-sell; warning stays.

### P11 — Standing UI: month panchang sheet
`/panchang/month` already has an API. Calendar grid, till-times, Hindi.

### P12 — Standing copy: winter honesty
Kartika SR ~20m early, winter tithi 45–140m, 12 Aug 2022 pūrṇimā ~20m. «परम्परा» wording. **No** bija fudge.

### P13 — Standing calc (later): fuller Makaranda spaṣṭa
Only after P12. Not 1-D bija. July gold remains the gate.

### P14 — Mobile RN
After web report is usable. Same APIs.

### Out of scope / later
- Uranus/Neptune/Pluto on SIDDHANTIC kundali.
- Copying Astrotalk lat/lon, English “Mrita/Kumara” mixed into Hindi UI (we use देवनागरी avasthā).
- Consult CTA spam in the PDF.

---

## First branch to cut

`ui/kundali-avakahada-graha` — P1 only. Then merge `--no-ff`.

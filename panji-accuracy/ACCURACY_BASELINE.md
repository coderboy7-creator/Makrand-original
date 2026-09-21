# Achieved accuracy (locked 2026-09-21)

Measured on live `/api/v1/jyotish/kundali` at **06:30 IST**, KSDS 26.5833°N / 85.268°E.  
CSV: `graha-spashta-band-data-2023-2026.csv`.  
**Chunk 1 does not change the engine.** These numbers are the baseline you will see in the app today.

Circular delta in [−180°, +180°). Mean ± sd.

---

## A. Printed KSDSU vs DRIK (Swiss) — tradition vs sky

This is how close the **panji** is to Swiss, not how close our SIDDHANTIC is.

### Śani (usable pages only; Āṣāḍha 2023 out)

| Epoch | n | panji − DRIK |
|---|---|---|
| 2023 Phālguna / Chaitra / Jyeṣṭha | 45 | **−2.63° ± 0.16°** |
| 2025–26 Kārtika → Magha | 87 | **−2.98° ± 0.14°** |
| Pooled good | 132 | **−2.86° ± 0.22°** |

KSDSU Saturn is a stable **~3° behind Swiss**. That is the later SIDDHANTIC target, not a paste-on +5.27° from another engine.

### Guru (Āṣāḍha 2023 out; Phālguna 2023 still −9.3° — print-side)

| Epoch | n | panji − DRIK |
|---|---|---|
| 2023 Chaitra | 14 | **+0.76° ± 0.09°** |
| 2023 Jyeṣṭha | 16 | **+0.99° ± 0.06°** |
| 2023 Phālguna | 15 | **−9.29° ± 0.03°** (do not fit) |
| 2025–26 winter | 91 | **+1.17° ± 0.11°** |

### Mangal (Phālguna 2023 `bhāga>60` out)

| Epoch | panji − DRIK |
|---|---|
| 2023 Chaitra / Jyeṣṭha / Āṣāḍha | **−0.5° to −1.2°** |
| 2025–26 | **0.0° to −1.2°** |
| 2023 Phālguna | **+48.7°** (garbage) |

### Ketu

| Epoch | panji − DRIK |
|---|---|
| 2025–26 Kārtika → Magha | **+0.5° to +3.8°** (usable) |
| 2023 Phālguna / Chaitra / Jyeṣṭha | **+11° to +53°** (do not fit) |
| 2023 Āṣāḍha | **+0.8° to +1.1°** (page otherwise excluded for Guru/Śani) |

### Budha / Śukra

Not a constant. Elongation / print jumps. Magha Budha column defective vs `p25a`.

---

## B. App SIDDHANTIC vs DRIK today — what the screens show

Sun/Moon stay on July gold (not this dataset). Tara-graha SIDDHANTIC is **not** spaṣṭa yet: outer grahas follow Sūrya (śīghra `k` inverted). Nodes ~180° vs DRIK.

| Date (test this) | Graha | SIDDHANTIC | DRIK | SIDD − DRIK |
|---|---|---|---|---|
| 2023-02-06 | Śani | 293.5° Makara | 302.3° Makara | −8.8° |
| 2023-05-20 | Śani | 28.5° **Meṣa** | 312.4° **Kumbha** | **+76°** (wrong rāśi) |
| 2026-01-04 | Śani | 264.3° **Dhanu** | 332.1° **Kumbha** | **−68°** (wrong rāśi) |
| 2023-02-06 | Guru | 300.9° | 342.8° | −42° |
| 2026-01-04 | Guru | 258.1° Dhanu | 86.7° Mithuna | **~171°** |
| 2023-02-06 | Mangal | 332.9° | 47.3° | −74° |
| 2023-02-06 | Budha | 274.6° | 268.7° | **+5.9°** (order-of-magnitude OK) |
| 2023-02-06 | Śukra | 319.1° | 318.2° | **+1.0°** |
| 2023-02-06 | Ketu | 18.1° | 193.5° | **~−175°** (swap) |
| 2023-02-06 | Sūrya | 291.9° | 292.8° | −0.9° |
| 2023-02-06 | Candra | 115.9° | 115.7° | +0.2° |

Inferior grahas are in the right rāśi. Śani/Guru/Mangal/Ketu on SIDDHANTIC kundali are **not**.

DRIK kundali vs panji (good pages) is the table in §A (~2–3° Śani, ~1° Guru). Use DRIK as the visual check against the printed band until Chunk 2.

---

## C. Screen impact — Chunk 1 (this lock)

**No longitude, rāśi, or clock moves.** If a number changed, that is a different bug.

After **Chunk 2** (outer śīghra) you should see SIDDHANTIC Śani/Guru/Mangal leave Sūrya’s rāśi. After **Chunk 3**, Ketu should sit near DRIK Ketu, not DRIK Rāhu.

| Route | Hindi UI | Chunk 1 | Later graha chunks |
|---|---|---|---|
| `/kundali` tab मूल | name, place, lagna line | none | lagna unchanged; not this gold |
| `/kundali` tab कुंडली | graha table (rāśi, bhāva, nakṣatra, DMS) | **none** | **Śani/Guru/Mangal/Ketu rāśi + nakṣatra** |
| `/kundali` tab चार्ट | D1, Chandra, gochar drawings | **none** | house occupants for those grahas |
| `/kundali` tab दशा | Vimśottari | none (Moon-based) | none unless Moon changes (it must not) |
| `/kundali` tab KP | cusp + graha star/sub | **none** | graha star/sub for tara-grahas |
| `/vargas` | D9 etc. | **none** | varga signs of tara-grahas |
| `/gochar` | transits | **none** | transit rāśi of Śani/Guru/Mangal |
| `/yogas` | yoga list | **none** | yogas that need those grahas |
| `/gems` | graha gem | **none** | if lord/rāśi of a tara-graha flips |
| `/varshaphal` | annual chart | **none** | annual grahas |
| `/prashna` | query chart | **none** | same as kundali grahas |
| `/milan` | two charts | **none** | guna that use Mars/Jupiter/Saturn |
| PDF `/api/v1/jyotish/report` | graha table + varga | **none** | same as कुंडली table |
| `/panchang` | tithi, nakṣatra, SR/SS | **none, must stay** | **must stay** |
| `/muhurta` | windows | none | only if it reads tara-grahas |
| `/horoscope` | daily | none | Moon/Sun; not this gold |
| `/nakshatra` `/rashi` | encyclopedia | none | none |

**How to test today (baseline, not a gain):**

1. Mode **सिद्धान्तिक**. Place Darbhanga. Time **06:30**.
2. Date **20 May 2023** → कुंडली table: Śani should show **Meṣa** (~28°) in the app. Printed/Swiss is **Kumbha**. That is the current loss.
3. Toggle **दृक** on the same date: Śani **Kumbha**. That matches KSDSU within ~3°.
4. Date **4 Jan 2026**: सिद्धान्तिक Śani **Dhanu**; दृक **Kumbha**; panji Magha ~329.4° (Kumbha) ≈ दृक.
5. Open `/panchang` for **29 Jul 2022**: sunrise still ~**5:23 AM** / **6:50 PM**. If that moved, Chunk 1 failed.

Panchang is the **no-regression** screen. Kundali graha table is the **future-gain** screen.

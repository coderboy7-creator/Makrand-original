# Achieved accuracy (locked 2026-09-21)

Measured on live `/api/v1/jyotish/kundali` at **06:30 IST**, KSDS 26.5833°N / 85.268°E.  
CSV: `graha-spashta-band-data-2023-2026.csv`.  
Chunk 1 was dataset-only. **Chunk 2 (2026-09-21)** inverted superior śīghra so Śani/Guru/Mangal no longer collapse onto Sūrya. Sun/Moon/July clocks unchanged. Ketu still ~180° (Chunk 3). No planet bija applied.

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

## B. App SIDDHANTIC vs DRIK — after Chunk 2 (live 06:30 IST)

Sun/Moon stay on July gold. Outer grahas **no longer follow Sūrya**. Nodes still ~180° vs DRIK.

| Date | Graha | SIDDHANTIC now | DRIK | SIDD − DRIK now | was (Chunk 1) |
|---|---|---|---|---|---|
| 2023-02-06 | Śani | 307.3° Kumbha | 302.3° Kumbha | **+5.0°** | −8.8° |
| 2023-05-20 | Śani | 318.5° **Kumbha** | 312.4° Kumbha | **+6.1°** | **+76° Meṣa** |
| 2026-01-04 | Śani | 339.6° Mīna | 332.1° Mīna | **+7.4°** | **−68° Dhanu** |
| 2023-02-06 | Guru | 346.6° Mīna | 342.8° Mīna | **+3.8°** | −42° |
| 2023-05-20 | Guru | 8.2° Meṣa | 6.6° Meṣa | **+1.7°** | +23° |
| 2026-01-04 | Guru | 79.7° Mithuna | 86.7° Mithuna | **−7.1°** | **+171°** |
| 2023-05-20 | Mangal | 90.7° Karka | 95.5° Karka | **−4.8°** | −28° |
| 2026-01-04 | Mangal | 269.7° Dhanu | 260.8° Dhanu | **+8.9°** | +5.0° |
| 2023-02-06 | Mangal | 20.5° Meṣa | 47.3° Vṛṣabha | **−26.8°** | −74° (still a rāśi off) |
| 2023-02-06 | Budha | 274.6° | 268.7° | +5.9° | same |
| 2023-02-06 | Śukra | 319.1° | 318.2° | +1.0° | same |
| 2023-02-06 | Ketu | 18.1° | 193.5° | ~−175° | same (Chunk 3) |
| 2023-02-06 | Sūrya | 291.9° | 292.8° | −0.9° | same |
| 2023-02-06 | Candra | 115.9° | 115.7° | +0.2° | same |

Śani is **5–7° ahead of Swiss** on these dates (panji is ~3° behind Swiss → panji−SIDD ≈ **−8 to −10°**). That is a later bija question, not this chunk.

DRIK kundali vs panji (good pages) is still §A (~2–3° Śani, ~1° Guru).

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

## C. Screen impact — Chunk 2

| Route | Hindi UI | Chunk 2 (now) |
|---|---|---|
| `/kundali` tab कुंडली | graha table | **Śani/Guru/Mangal rāśi + nakṣatra** (positive on 20 May 2023 / 4 Jan 2026). Ketu unchanged. Sūrya/Candra unchanged. |
| `/kundali` tab चार्ट | D1 / Chandra / gochar | those three grahas move house if rāśi flipped |
| `/kundali` tab KP | graha star/sub | tara-graha subs |
| `/vargas` `/gochar` `/yogas` `/gems` `/varshaphal` `/prashna` `/milan` | | same grahas |
| PDF report | graha table | same |
| `/panchang` | tithi, SR/SS | **none — 29 Jul 2022 still 5:23 / 6:50** |
| दशा | Moon | **none** |

**How to see the gain:** Darbhanga, 06:30, **20 May 2023**, सिद्धान्तिक. Śani must be **Kumbha**, not Meṣa. Toggle दृक: also Kumbha (~6° apart). **4 Jan 2026** Śani **Mīna** (near Kumbha/Mīna cusp), not Dhanu.

**Still wrong:** Ketu ~opposite; 6 Feb 2023 Mangal still Meṣa vs Swiss Vṛṣabha.

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

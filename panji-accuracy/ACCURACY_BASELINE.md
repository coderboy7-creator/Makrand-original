# Achieved accuracy (locked 2026-09-21)

Measured on live `/api/v1/jyotish/kundali` at **06:30 IST**, KSDS 26.5833°N / 85.268°E.  
CSV: `graha-spashta-band-data-2023-2026.csv`.  
Chunk 1 dataset-only. **Chunk 2** superior śīghra. **Chunk 3** SS Rāhu +180° so nodes match DRIK/panji (were swapped). Sun/Moon/July clocks unchanged. No planet bija.

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

## B. App SIDDHANTIC vs DRIK — after Chunk 2+3 (live 06:30 IST)

Sun/Moon stay on July gold. Outer grahas **no longer follow Sūrya**. Nodes **no longer swapped**.

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
| 2023-02-06 | Ketu | **198.1°** | 193.5° | **+4.6°** | ~−175° |
| 2026-01-04 | Ketu | **141.7°** | 136.3° | **+5.5°** | ~−175° |
| 2025-10-08 | Ketu | **146.4°** | 144.0° | **+2.4°** | ~−175° |
| 2023-02-06 | Sūrya | 291.9° | 292.8° | −0.9° | same |
| 2023-02-06 | Candra | 115.9° | 115.7° | +0.2° | same |

Śani is **5–7° ahead of Swiss** on these dates (panji is ~3° behind Swiss → panji−SIDD ≈ **−8 to −10°**). That is a later bija question, not this chunk.

DRIK kundali vs panji (good pages) is still §A (~2–3° Śani, ~1° Guru).

---

## C. Screen impact — Chunk 3 (nodes) + Chunk 2 (śīghra)

| Route | Hindi UI | Chunk 2 (now) |
|---|---|---|
| `/kundali` tab कुंडली | graha table | **Śani/Guru/Mangal** (Chunk 2). **Rāhu/Ketu rāśi** (Chunk 3, ~180° flip). Sūrya/Candra unchanged. |
| `/kundali` tab चार्ट | D1 / Chandra / gochar | those three grahas move house if rāśi flipped |
| `/kundali` tab KP | graha star/sub | tara-graha subs |
| `/vargas` `/gochar` `/yogas` `/gems` `/varshaphal` `/prashna` `/milan` | | same grahas |
| PDF report | graha table | same |
| `/panchang` | tithi, SR/SS | **none — 29 Jul 2022 still 5:23 / 6:50** |
| दशा | Moon | **none** |

**How to see Chunk 2:** 20 May 2023, 06:30, सिद्धान्तिक — Śani **Kumbha**, not Meṣa.

**How to see Chunk 3:** **4 Jan 2026** 06:30 सिद्धान्तिक — Ketu ~**141° Siṃha** (panji 140°), not ~322°. Rāhu ~**322° Kumbha**.

**Must not move:** `/panchang` 29 Jul 2022 **5:23 / 6:50**. दशा (Moon).

**Still wrong:** 6 Feb 2023 Mangal Meṣa vs Swiss Vṛṣabha. Ignore 2023 Phālguna/Chaitra printed Ketu (garbage).

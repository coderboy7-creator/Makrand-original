# KSDSU graha-spaṣṭa gold

**Status:** dataset lock only. No engine change. Chunk 1 of the graha-spaṣṭa plan.

Canonical machine file:

`panji-accuracy/graha-spashta-band-data-2023-2026.csv`

Source: original KSDSU विश्वविद्यालय पञ्चांग graha-spaṣṭa bands.

| Book | Pages in CSV | Dates |
|---|---|---|
| 2022–23 (`pdf22-*`) | Phālguna k2, Chaitra k, Jyeṣṭha ś, Āṣāḍha k+ś | 6 Feb – 3 Jul 2023 |
| 2025–26 (`p25-*.jpg`, `p25a.json`) | Kārtika k+ś, Mārgaśīrṣa k+ś, Pauṣa k, Magha k | 8 Oct 2025 – 18 Jan 2026 |

`p25-*.jpg` numbers are already in the CSV. Do not re-OCR for calibration.

The 77-row `graha-spashta-band-data.csv` is the 2023 + 3-day `p25a` extract. Overlap with the canonical file is 0.000° on every filled cell. Prefer the 166-row file.

## How to read a row

Printed `rāśi|bhāga|kalā|vikala` (0-indexed rāśi) →

`deg = 30×rāśi + bhāga + kalā/60 + vikala/3600`

Separators in `*_raw` may be `|`, `.`, or `।`. `bhāga>60` is stored **as printed** (Phālguna Mangal/Ketu).

Epoch used when comparing engines: **06:30 IST**, KSDS 26.5833°N / 85.268°E.

## Do not fit these

| Flag / defect | What |
|---|---|
| `page_excluded=YES` | Āṣāḍha k+ś 2023 (29 rows). Guru +12°, Śani +7.5°, 4→5 Jun jumps ~10°/day. |
| Phālguna 2023 Mangal/Ketu `bhāga>60` | ~+48° / +53° vs Swiss. |
| Chaitra 2023 Ketu | ~+35° vs Swiss; rate unphysical. |
| Jyeṣṭha 2023 Ketu | ~+11° vs Swiss; page-boundary −10° into Āṣāḍha. |
| Magha Budha vs `p25a` (4–6 Jan 2026) | Magha column ~+10° on Budha; Guru/Śani/Ketu match `p25a`. Use **`p25a`** for those three days. |
| Blank `*_raw` / `*_deg` | Illegible cells — not zero. |
| Impossible °/day (Budha 11.8, Śukra 11.7, …) | Print or transcription; not a bija. |

## What this gold is for

- **Kundali tara-graha spaṣṭa** (Mangal → Śani, Ketu).
- **Not** tithi, nakṣatra, yoga, sunrise, sunset. Those stay July-gold Sun–Moon.

Accuracy numbers (panji vs DRIK, app SIDDHANTIC today) are in `ACCURACY_BASELINE.md`.

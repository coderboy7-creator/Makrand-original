import React, { useMemo, useState } from "react";
import { Box, Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material";
import { grahaName } from "./jyotishLabels";

const TINT: Record<string, string> = {
  Sun: "#E8C547", Moon: "#C5D4E8", Mars: "#E07050", Mercury: "#7DCEA0",
  Jupiter: "#F3D36A", Venus: "#E8A0C0", Saturn: "#8AA4C8", Rahu: "#C4A574", Ketu: "#B8A090",
};

const cell = {
  whiteSpace: "nowrap" as const,
  overflow: "hidden",
  textOverflow: "ellipsis",
  py: 0.7,
  px: 1.1,
  borderColor: "rgba(232,197,71,0.12)",
  fontVariantNumeric: "tabular-nums" as const,
  maxWidth: 160,
};

function fmtDay(v: any) {
  if (v == null) return "—";
  const s = String(v);
  const m = s.match(/^(\d{4})-(\d{2})-(\d{2})/);
  return m ? `${m[3]}-${m[2]}-${m[1]}` : s.slice(0, 10);
}

function elapsedPct(start: any, end: any) {
  const a = Date.parse(String(start || ""));
  const b = Date.parse(String(end || ""));
  if (!Number.isFinite(a) || !Number.isFinite(b) || b <= a) return 0;
  return Math.max(0, Math.min(100, ((Date.now() - a) / (b - a)) * 100));
}

function yoginiLabel(yog: any, name: string, hi: boolean) {
  if (!name) return "—";
  const i = (yog?.names || []).indexOf(name);
  if (hi && i >= 0) return yog.namesHi?.[i] || name;
  return name;
}

function lordOfYogini(yog: any, name: string) {
  const i = (yog?.names || []).indexOf(name);
  return i >= 0 ? yog.lords?.[i] : "";
}

function tintOf(lord: string) {
  return TINT[lord] || "#E8C547";
}

function Tile({ label, name, start, end, years, t }: {
  label: string; name: string; start?: any; end?: any; years?: any; t: (k: string) => string;
}) {
  const pct = elapsedPct(start, end);
  const color = tintOf(name);
  return (
    <Box sx={{
      minWidth: 0,
      overflow: "hidden",
      borderRadius: 2,
      p: 1.5,
      border: "1px solid rgba(232,197,71,0.28)",
      background: `linear-gradient(135deg, ${color}33 0%, rgba(123,30,58,0.35) 48%, rgba(8,6,14,0.85) 100%)`,
    }}>
      <Typography variant="caption" sx={{ letterSpacing: 1.4, textTransform: "uppercase", color: color, display: "block", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
        {label}
      </Typography>
      <Typography variant="h6" sx={{ color: "#FBF6EA", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis", lineHeight: 1.25, mt: 0.25 }}>
        {name || "—"}
      </Typography>
      <Typography variant="body2" sx={{ whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis", fontVariantNumeric: "tabular-nums", opacity: 0.9 }}>
        {fmtDay(start)} → {fmtDay(end)}
      </Typography>
      <Typography variant="caption" sx={{ whiteSpace: "nowrap" }}>
        {years != null && Number.isFinite(Number(years)) ? `${Number(years).toFixed(2)} ${t("yrs")}` : ""}
      </Typography>
      <Box sx={{ mt: 1, height: 6, borderRadius: 99, bgcolor: "rgba(0,0,0,0.35)", overflow: "hidden" }}>
        <Box sx={{ width: `${pct}%`, height: "100%", background: `linear-gradient(90deg, ${color}, #F3D36A)` }} />
      </Box>
    </Box>
  );
}

/** Headers passed as i18n so Hindi/English both stay on one line. */
function HeadedTable(props: {
  title: string;
  heads: [string, string, string, string] | [string, string, string, string, string];
  rows: any[];
  currentLord?: string;
  onPick?: (lord: string) => void;
  nameOf: (row: any) => string;
  extraCell?: (row: any) => React.ReactNode;
}) {
  const five = props.heads.length === 5;
  return (
    <Box sx={{
      borderRadius: 2,
      overflow: "hidden",
      border: "1px solid rgba(232,197,71,0.2)",
      background: "linear-gradient(180deg, rgba(255,200,90,0.07), rgba(10,8,16,0.45))",
    }}>
      <Typography sx={{ px: 1.5, pt: 1.25, pb: 0.5, color: "#F3D36A", fontWeight: 700, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
        {props.title}
      </Typography>
      <Box sx={{ overflowX: "auto", maxWidth: "100%" }}>
        <Table size="small" sx={{ minWidth: five ? 620 : 520, tableLayout: "fixed" }}>
          <TableHead>
            <TableRow sx={{ background: "linear-gradient(90deg, rgba(123,30,58,0.85), rgba(32,20,12,0.9))" }}>
              {props.heads.map((h, i) => (
                <TableCell key={h + i} sx={{
                  ...cell,
                  color: "#F3D36A",
                  fontWeight: 700,
                  textAlign: i === props.heads.length - 1 ? "right" : "left",
                }}>{h}</TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {props.rows.map((row) => {
              const on = row.lord === props.currentLord;
              const color = tintOf(row.tintLord || row.lord);
              return (
                <TableRow
                  key={String(row.lord) + String(row.start)}
                  hover
                  onClick={props.onPick ? () => props.onPick!(row.lord) : undefined}
                  sx={{
                    cursor: props.onPick ? "pointer" : "default",
                    background: on
                      ? `linear-gradient(90deg, ${color}38, rgba(123,30,58,0.2) 50%, transparent)`
                      : "transparent",
                  }}
                >
                  <TableCell sx={cell}>
                    <Box sx={{ display: "flex", alignItems: "center", gap: 1, minWidth: 0 }}>
                      <Box sx={{ width: 8, height: 8, borderRadius: "50%", bgcolor: color, flexShrink: 0 }} />
                      <Box component="span" sx={{ fontWeight: on ? 700 : 500, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
                        {props.nameOf(row)}
                      </Box>
                    </Box>
                  </TableCell>
                  {five && <TableCell sx={cell}>{props.extraCell?.(row)}</TableCell>}
                  <TableCell sx={cell}>{fmtDay(row.start)}</TableCell>
                  <TableCell sx={cell}>{fmtDay(row.end)}</TableCell>
                  <TableCell sx={{ ...cell, textAlign: "right" }}>
                    {Number(row.years).toFixed(Number(row.years) >= 1 ? 2 : 3)}
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </Box>
    </Box>
  );
}

export function DashaBoard({ dasha, hi, t }: { dasha: any; hi: boolean; t: (k: string) => string }) {
  const cur = dasha?.current || {};
  const periods: any[] = dasha?.periods || [];
  const [sel, setSel] = useState<string | null>(null);
  const mahaLord = sel || cur.mahadasha;
  const maha = useMemo(() => periods.find((p) => p.lord === mahaLord) || periods[0], [periods, mahaLord]);
  const antars: any[] = maha?.children || [];
  const antarLord = maha?.lord === cur.mahadasha ? cur.antardasha : antars[0]?.lord;
  const antar = antars.find((a) => a.lord === antarLord) || antars[0];
  const prat: any[] = antar?.children || [];
  const yog = dasha?.yogini;

  if (!periods.length) return null;

  return (
    <Box sx={{ minWidth: 0 }}>
      <Box sx={{
        display: "grid",
        gridTemplateColumns: { xs: "minmax(0,1fr)", sm: "repeat(3, minmax(0,1fr))" },
        gap: 1.25,
        mb: 2,
      }}>
        <Tile label={t("mahadasha")} name={grahaName(cur.mahadasha, hi)} start={cur.mahaStart} end={cur.mahaEnd} years={cur.mahaYears} t={t} />
        <Tile label={t("antar")} name={grahaName(cur.antardasha, hi)} start={cur.antarStart} end={cur.antarEnd} years={cur.antarYears} t={t} />
        <Tile label={t("pratyantar")} name={grahaName(cur.pratyantardasha, hi)} start={cur.pratyantarStart} end={cur.pratyantarEnd} years={cur.pratyantarYears} t={t} />
      </Box>

      <Box sx={{ display: "grid", gridTemplateColumns: { xs: "minmax(0,1fr)", md: "minmax(0,1fr) minmax(0,1fr)" }, gap: 1.5 }}>
        <HeadedTable
          title={t("dasha_title")}
          heads={[t("graha"), t("from"), t("until"), t("yrs")]}
          rows={periods}
          currentLord={cur.mahadasha}
          onPick={setSel}
          nameOf={(r) => grahaName(r.lord, hi)}
        />
        <HeadedTable
          title={`${grahaName(maha?.lord, hi)} · ${t("antar")}`}
          heads={[t("graha"), t("from"), t("until"), t("yrs")]}
          rows={antars}
          currentLord={maha?.lord === cur.mahadasha ? cur.antardasha : undefined}
          nameOf={(r) => grahaName(r.lord, hi)}
        />
      </Box>

      {prat.length > 0 && (
        <Box sx={{ mt: 1.5 }}>
          <HeadedTable
            title={`${grahaName(antar?.lord, hi)} · ${t("pratyantar")}`}
            heads={[t("graha"), t("from"), t("until"), t("yrs")]}
            rows={prat}
            currentLord={maha?.lord === cur.mahadasha && antar?.lord === cur.antardasha ? cur.pratyantardasha : undefined}
            nameOf={(r) => grahaName(r.lord, hi)}
          />
        </Box>
      )}

      {yog && (
        <Box sx={{ mt: 2 }}>
          <HeadedTable
            title={t("yogini_title")}
            heads={[t("yogini"), t("lord"), t("from"), t("until"), t("yrs")]}
            rows={(yog.periods || []).map((p: any) => ({ ...p, tintLord: lordOfYogini(yog, p.lord) }))}
            currentLord={yog.current?.mahadasha}
            nameOf={(r) => yoginiLabel(yog, r.lord, hi)}
            extraCell={(r) => grahaName(lordOfYogini(yog, r.lord), hi)}
          />
          <Typography variant="caption" color="text.secondary" sx={{ display: "block", mt: 0.75 }}>
            {t("yogini_sub")}
          </Typography>
        </Box>
      )}
    </Box>
  );
}

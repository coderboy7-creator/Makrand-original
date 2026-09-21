import React, { useEffect, useMemo, useState } from "react";
import { Alert, Box, Button, Typography } from "@mui/material";
import { api } from "../api";
import { useI18n } from "../i18n";

/** Printed panchang clock is END (until). Pull the 12-hour part; never use start. */
export function tillClock(s?: string) {
  if (!s) return "";
  const m = String(s).match(/(\d{1,2}:\d{2}\s*[AP]M)/i);
  return m ? m[1] : String(s);
}

function padMonth(days: any[]) {
  if (!days.length) return [];
  const first = new Date(days[0].date + "T12:00:00");
  const lead = first.getDay(); // 0 = Sunday, matches engine vara
  const cells: (any | null)[] = [];
  for (let i = 0; i < lead; i++) cells.push(null);
  days.forEach((d) => cells.push(d));
  while (cells.length % 7) cells.push(null);
  return cells;
}

export default function PanchangMonthGrid({
  birth, date, onPick,
}: {
  birth: any;
  date: string;
  onPick: (iso: string, day: any) => void;
}) {
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const [days, setDays] = useState<any[]>([]);
  const [err, setErr] = useState("");
  const [busy, setBusy] = useState(false);
  const monthIso = (date || "").slice(0, 7) + "-01";

  const load = (iso: string) => {
    setBusy(true);
    setErr("");
    const q = `/api/v1/jyotish/panchang/month?month=${iso}&lat=${birth.latitude}&lon=${birth.longitude}&ayanamsa=${birth.ayanamsa}&mode=${birth.panchangMode}`;
    return api.get(q).then((rows: any) => {
      setDays(Array.isArray(rows) ? rows : []);
    }).catch((e) => setErr(e.message || t("pdf_fail"))).finally(() => setBusy(false));
  };

  useEffect(() => { load(monthIso); }, [monthIso, birth.latitude, birth.longitude, birth.ayanamsa, birth.panchangMode]);

  const shift = (delta: number) => {
    const [y, m] = monthIso.split("-").map(Number);
    const d = new Date(y, m - 1 + delta, 1);
    const next = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-01`;
    onPick(next, null);
  };

  const cells = useMemo(() => padMonth(days), [days]);
  const title = useMemo(() => {
    const [y, m] = monthIso.split("-").map(Number);
    return new Date(y, m - 1, 1).toLocaleDateString(hi ? "hi-IN" : "en-IN", { month: "long", year: "numeric" });
  }, [monthIso, hi]);
  const heads = hi
    ? ["रवि", "सोम", "मंगल", "बुध", "गुरु", "शुक्र", "शनि"]
    : ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

  return (
    <Box sx={{ mb: 2 }}>
      <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 1, flexWrap: "wrap" }}>
        <Button size="small" onClick={() => shift(-1)}>{t("month_prev")}</Button>
        <Typography variant="h6" color="primary" sx={{ minWidth: 180, textAlign: "center" }}>{title}</Typography>
        <Button size="small" onClick={() => shift(1)}>{t("month_next")}</Button>
        {busy && <Typography variant="caption">{t("loading")}</Typography>}
      </Box>
      {err && <Alert severity="error" sx={{ mb: 1 }}>{err}</Alert>}
      <Box sx={{
        display: "grid",
        gridTemplateColumns: "repeat(7, minmax(0, 1fr))",
        gap: 0.5,
        border: "1px solid rgba(232,197,71,0.22)",
        borderRadius: 2,
        p: 0.75,
        overflowX: "auto",
      }}>
        {heads.map((h) => (
          <Typography key={h} variant="caption" sx={{ textAlign: "center", fontWeight: 700, color: "primary.main" }}>{h}</Typography>
        ))}
        {cells.map((d, i) => {
          if (!d) return <Box key={"e" + i} sx={{ minHeight: 72 }} />;
          const selected = d.date === date;
          const krishna = String(d.paksha || d.pakshaHi || "").toLowerCase().includes("krishna")
            || String(d.pakshaHi || "") === "कृष्ण";
          return (
            <Box
              key={d.date}
              onClick={() => onPick(d.date, d)}
              sx={{
                cursor: "pointer",
                minHeight: 88,
                p: 0.6,
                borderRadius: 1,
                border: selected ? "1px solid #e8c547" : "1px solid rgba(255,255,255,0.06)",
                background: selected
                  ? "rgba(232,197,71,0.16)"
                  : krishna ? "rgba(80,40,90,0.28)" : "rgba(20,18,28,0.45)",
              }}
            >
              <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>{Number(String(d.date).slice(8))}</Typography>
              <Typography variant="caption" display="block" sx={{ lineHeight: 1.25 }}>
                {hi ? d.pakshaHi : d.paksha} {hi ? d.tithiHi : d.tithi}
              </Typography>
              <Typography variant="caption" display="block" color="text.secondary">
                {t("until")} {tillClock(d.tithiEnd)}
              </Typography>
              <Typography variant="caption" display="block" sx={{ lineHeight: 1.25 }}>
                {hi ? d.nakshatraHi : d.nakshatra} · {t("until")} {tillClock(d.nakshatraEnd)}
              </Typography>
              <Typography variant="caption" display="block" color="text.secondary">
                {d.sunrise} / {d.sunset}
              </Typography>
            </Box>
          );
        })}
      </Box>
    </Box>
  );
}

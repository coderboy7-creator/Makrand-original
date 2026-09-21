import React from "react";
import { Box, Typography } from "@mui/material";

function statusLabel(status: string, t: (k: string) => string) {
  if (status === "active") return t("status_active");
  if (status === "present") return t("status_present");
  if (status === "cancelled") return t("status_cancelled");
  return t("status_absent");
}

function tone(status: string) {
  if (status === "absent") return { bar: "#7DCEA0", bg: "linear-gradient(135deg, rgba(125,206,160,0.22), rgba(8,6,14,0.85))" };
  if (status === "cancelled") return { bar: "#8AA4C8", bg: "linear-gradient(135deg, rgba(138,164,200,0.25), rgba(8,6,14,0.85))" };
  return { bar: "#E8C547", bg: "linear-gradient(135deg, rgba(232,197,71,0.28), rgba(123,30,58,0.28) 55%, rgba(8,6,14,0.9))" };
}

export function DoshaBoard({ panel, hi, t }: { panel: any; hi: boolean; t: (k: string) => string }) {
  if (!panel) return null;
  const items: any[] = panel.items || [panel.mangal, panel.kaalsarpa, panel.sadeSati].filter(Boolean);
  return (
    <Box sx={{ minWidth: 0 }}>
      <Typography variant="h6" color="primary" sx={{ mb: 0.5 }}>{t("dosa_title")}</Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 1.25 }}>
        {hi ? panel.noteHi : panel.note}
      </Typography>
      <Box sx={{
        display: "grid",
        gridTemplateColumns: { xs: "minmax(0,1fr)", sm: "repeat(3, minmax(0,1fr))" },
        gap: 1.25,
      }}>
        {items.map((it) => {
          const st = String(it.status || (it.active ? "active" : it.present ? "present" : "absent"));
          const c = tone(st);
          const extra = it.id === "mangal" && it.house
            ? `${t("bhava")} ${it.house}`
            : it.id === "kaalsarpa" && it.type
              ? (hi ? it.typeHi : it.type)
              : it.id === "sadesati"
                ? (hi ? it.phaseHi : it.phase)
                : "";
          return (
            <Box key={it.id} sx={{
              minWidth: 0, overflow: "hidden", borderRadius: 2, p: 1.5,
              border: "1px solid rgba(232,197,71,0.22)", background: c.bg,
            }}>
              <Box sx={{ display: "flex", alignItems: "center", gap: 1, minWidth: 0 }}>
                <Box sx={{ width: 8, height: 8, borderRadius: "50%", bgcolor: c.bar, flexShrink: 0 }} />
                <Typography sx={{ fontWeight: 700, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
                  {hi ? it.nameHi : it.name}
                </Typography>
              </Box>
              <Typography sx={{ mt: 0.5, color: c.bar, fontWeight: 700, whiteSpace: "nowrap" }}>
                {statusLabel(st, t)}
              </Typography>
              {extra && (
                <Typography variant="caption" sx={{ display: "block", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
                  {extra}
                </Typography>
              )}
              <Typography variant="body2" sx={{ mt: 0.75, display: "-webkit-box", WebkitLineClamp: 3, WebkitBoxOrient: "vertical", overflow: "hidden" }}>
                {hi ? it.textHi : it.text}
              </Typography>
            </Box>
          );
        })}
      </Box>
    </Box>
  );
}

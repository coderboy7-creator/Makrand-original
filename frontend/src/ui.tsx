import React from "react";
import { Box, Card, CardContent, Typography } from "@mui/material";

export function PageHero({ title, sub, children }: { title: React.ReactNode; sub?: string; children?: React.ReactNode }) {
  return (
    <Box sx={{ mb: 3, textAlign: { xs: "left", md: "center" }, pt: { xs: 1, md: 2 } }}>
      <Typography variant="h3" sx={{ color: "#F3D36A", mb: 0.5 }}>{title}</Typography>
      {sub && <Typography color="text.secondary" sx={{ maxWidth: 720, mx: { md: "auto" } }}>{sub}</Typography>}
      {children}
    </Box>
  );
}

export function GoldTitle({ children, sub }: { children: React.ReactNode; sub?: string }) {
  return (
    <Box sx={{ mb: 2.5 }}>
      <Typography variant="h4" sx={{ color: "#F3D36A" }}>{children}</Typography>
      {sub && <Typography color="text.secondary">{sub}</Typography>}
    </Box>
  );
}

export function GlassCard({ children, sx }: { children: React.ReactNode; sx?: object }) {
  return (
    <Card sx={{ height: "100%", ...sx }}>
      <CardContent sx={{ p: { xs: 2, md: 2.5 } }}>{children}</CardContent>
    </Card>
  );
}

export function MetaRow({ k, v }: { k: string; v: React.ReactNode }) {
  return (
    <Box sx={{ display: "flex", justifyContent: "space-between", gap: 2, py: 1, borderBottom: "1px solid rgba(232,197,71,0.1)" }}>
      <Typography variant="caption" sx={{ letterSpacing: 1.2, color: "text.secondary", textTransform: "uppercase" }}>{k}</Typography>
      <Typography sx={{ fontWeight: 700, textAlign: "right" }}>{v}</Typography>
    </Box>
  );
}

export function LimbTile({ label, value, until, extra }: { label: string; value: React.ReactNode; until?: React.ReactNode; extra?: React.ReactNode }) {
  return (
    <Card sx={{ height: "100%" }}>
      <CardContent>
        <Typography variant="caption" sx={{ letterSpacing: 1.6, color: "primary.main", textTransform: "uppercase" }}>{label}</Typography>
        <Typography variant="h6" sx={{ mt: 0.5, lineHeight: 1.3 }}>{value}</Typography>
        {until && <Typography sx={{ mt: 0.75, fontWeight: 700 }}>{until}</Typography>}
        {extra}
      </CardContent>
    </Card>
  );
}

export function ScoreHero({ score, max, label }: { score: number; max: number; label: React.ReactNode }) {
  const pct = Math.max(0, Math.min(100, (score / max) * 100));
  return (
    <Box sx={{ textAlign: "center", py: 1 }}>
      <Typography variant="h2" sx={{ color: "#F3D36A", lineHeight: 1 }}>{score}<Typography component="span" variant="h5" color="text.secondary"> / {max}</Typography></Typography>
      <Typography sx={{ mt: 1 }}>{label}</Typography>
      <Box sx={{ mt: 2, height: 10, borderRadius: 99, bgcolor: "rgba(232,197,71,0.12)", overflow: "hidden" }}>
        <Box sx={{ width: `${pct}%`, height: "100%", background: "linear-gradient(90deg,#F3D36A,#E0A020)" }} />
      </Box>
    </Box>
  );
}

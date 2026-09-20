import React from "react";
import { Box, Card, CardContent, Typography } from "@mui/material";

export function PageHero({ title, sub, children }: { title: React.ReactNode; sub?: string; children?: React.ReactNode }) {
  return (
    <Box sx={{ mb: 3, textAlign: { xs: "left", md: "center" }, pt: { xs: 1, md: 2 } }}>
      <Typography variant="h3" sx={{
        background: "linear-gradient(90deg,#FFF6D0,#E8C547 40%,#FF9A4A)",
        WebkitBackgroundClip: "text",
        WebkitTextFillColor: "transparent",
        mb: 0.5,
      }}>{title}</Typography>
      {sub && <Typography color="text.secondary" sx={{ maxWidth: 720, mx: { md: "auto" } }}>{sub}</Typography>}
      {children}
    </Box>
  );
}

export function GoldTitle({ children, sub }: { children: React.ReactNode; sub?: string }) {
  return (
    <Box sx={{ mb: 2.5 }}>
      <Typography variant="h4" sx={{
        background: "linear-gradient(90deg,#FFF1C2,#E8C547)",
        WebkitBackgroundClip: "text",
        WebkitTextFillColor: "transparent",
      }}>{children}</Typography>
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

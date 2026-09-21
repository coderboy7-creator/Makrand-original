import React from "react";
import { Alert, Typography } from "@mui/material";
import { useI18n } from "../i18n";

/** P12 — winter honesty + Drik vs Siddhantic. Copy only; never retune bijas. */
export default function HonestyNote({ mode }: { mode?: string }) {
  const { t } = useI18n();
  const drik = String(mode || "SIDDHANTIC").toUpperCase() === "DRIK";
  return (
    <Alert severity="info" sx={{ mb: 2, alignItems: "flex-start" }}>
      <Typography variant="body2" sx={{ fontWeight: 700, mb: 0.5 }}>
        {t("honesty_title")}
      </Typography>
      <Typography variant="body2">
        {drik ? t("honesty_drik") : t("honesty_ss")}
      </Typography>
      <Typography variant="caption" display="block" sx={{ mt: 0.75 }} color="text.secondary">
        {t("honesty_parampara")}
      </Typography>
    </Alert>
  );
}

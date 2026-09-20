import React from "react";
import { Box, Button, MenuItem, TextField, ToggleButton, ToggleButtonGroup, Typography } from "@mui/material";
import { useApp } from "../state";
import { useI18n } from "../i18n";
import PlaceSearch from "./PlaceSearch";

const cell = { minWidth: 0, display: "flex", flexDirection: "column" as const };

export default function BirthForm({ onSubmit, submitLabel }: { onSubmit?: () => void; submitLabel?: string }) {
  const { birth, setBirth, config, loadChart, loading } = useApp();
  const { t } = useI18n();
  const set = (k: string, v: any) => setBirth({ ...birth, [k]: v });

  return (
    <Box sx={{
      p: { xs: 1.5, md: 2.25 }, mb: 1, borderRadius: 3,
      border: "1px solid rgba(232,197,71,0.16)",
      background: "linear-gradient(160deg, rgba(255,200,90,0.08), rgba(10,8,16,0.45))",
    }}>
      <Box sx={{
        display: "grid",
        gridTemplateColumns: { xs: "1fr 1fr", md: "repeat(12, minmax(0, 1fr))" },
        gap: 2,
        alignItems: "start",
        "& .MuiOutlinedInput-root": { height: 42 },
        "& input[type=number]": { MozAppearance: "textfield" },
        "& input[type=number]::-webkit-outer-spin-button, & input[type=number]::-webkit-inner-spin-button": { WebkitAppearance: "none", margin: 0 },
      }}>
        <Box sx={{ ...cell, gridColumn: { xs: "1 / -1", md: "span 4" } }}>
          <TextField fullWidth label={t("name")} value={birth.name} onChange={(e) => set("name", e.target.value)} />
        </Box>
        <Box sx={{ ...cell, gridColumn: { xs: "1 / -1", md: "span 4" } }}>
          <TextField fullWidth type="datetime-local" label={t("birth_dt")} InputLabelProps={{ shrink: true }}
            value={birth.dateTime} onChange={(e) => set("dateTime", e.target.value)} />
        </Box>
        <Box sx={{ ...cell, gridColumn: { xs: "1 / -1", md: "span 4" } }}>
          <PlaceSearch helper="" value={birth.place} onChange={(place, lat, lon) => setBirth({ ...birth, place, latitude: lat, longitude: lon })} />
        </Box>
        <Box sx={{ ...cell, gridColumn: { xs: "span 1", md: "span 3" } }}>
          <TextField fullWidth type="number" label={t("lat")} value={birth.latitude}
            inputProps={{ step: "any" }} onChange={(e) => set("latitude", Number(e.target.value))} />
        </Box>
        <Box sx={{ ...cell, gridColumn: { xs: "span 1", md: "span 3" } }}>
          <TextField fullWidth type="number" label={t("lon")} value={birth.longitude}
            inputProps={{ step: "any" }} onChange={(e) => set("longitude", Number(e.target.value))} />
        </Box>
        <Box sx={{ ...cell, gridColumn: { xs: "1 / -1", md: "span 3" } }}>
          <TextField fullWidth select label={t("ayanamsa")} value={birth.ayanamsa} onChange={(e) => set("ayanamsa", e.target.value)}>
            {(config.ayanamsas || [{ id: birth.ayanamsa, label: "Makaranda" }]).map((a: any) => (
              <MenuItem key={a.id} value={a.id}>{a.label}</MenuItem>
            ))}
          </TextField>
        </Box>
        <Box sx={{ ...cell, gridColumn: { xs: "1 / -1", md: "span 3" } }}>
          <TextField fullWidth select label={t("houses")} value={birth.houseSystem} onChange={(e) => set("houseSystem", e.target.value)}>
            <MenuItem value="WHOLE_SIGN">{t("house_whole")}</MenuItem>
            <MenuItem value="EQUAL">{t("house_equal")}</MenuItem>
            <MenuItem value="SRIPATI">{t("house_sripati")}</MenuItem>
          </TextField>
        </Box>
      </Box>
      <Typography variant="caption" color="text.secondary" sx={{ display: "block", mt: 1.25 }}>{t("place_help")}</Typography>
      <Box sx={{ mt: 1.5, display: "flex", flexWrap: "wrap", alignItems: "center", gap: 2 }}>
        <ToggleButtonGroup exclusive value={birth.panchangMode} onChange={(_, v) => v && set("panchangMode", v)} size="small">
          <ToggleButton value="SIDDHANTIC">{t("mode_ss")}</ToggleButton>
          <ToggleButton value="DRIK">{t("mode_drik")}</ToggleButton>
        </ToggleButtonGroup>
        <Button variant="contained" disabled={loading} onClick={() => (onSubmit ? onSubmit() : loadChart())}>
          {loading ? t("calculating") : (submitLabel || t("cast"))}
        </Button>
      </Box>
    </Box>
  );
}

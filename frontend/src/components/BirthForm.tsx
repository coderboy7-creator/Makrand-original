import React from "react";
import { Box, Button, Grid, MenuItem, TextField, ToggleButton, ToggleButtonGroup } from "@mui/material";
import { useApp } from "../state";
import { useI18n } from "../i18n";
import PlaceSearch from "./PlaceSearch";

export default function BirthForm({ onSubmit, submitLabel }: { onSubmit?: () => void; submitLabel?: string }) {
  const { birth, setBirth, config, loadChart, loading } = useApp();
  const { t } = useI18n();
  const set = (k: string, v: any) => setBirth({ ...birth, [k]: v });

  return (
    <Box>
      <Grid container spacing={2}>
        <Grid item xs={12} md={4}>
          <TextField fullWidth label={t("name")} value={birth.name} onChange={(e) => set("name", e.target.value)} />
        </Grid>
        <Grid item xs={12} md={4}>
          <TextField fullWidth type="datetime-local" label={t("birth_dt")} InputLabelProps={{ shrink: true }}
            value={birth.dateTime} onChange={(e) => set("dateTime", e.target.value)} />
        </Grid>
        <Grid item xs={12} md={4}>
          <PlaceSearch value={birth.place} onChange={(place, lat, lon) => setBirth({ ...birth, place, latitude: lat, longitude: lon })} />
        </Grid>
        <Grid item xs={6} md={3}>
          <TextField fullWidth type="number" label={t("lat")} value={birth.latitude} onChange={(e) => set("latitude", Number(e.target.value))} />
        </Grid>
        <Grid item xs={6} md={3}>
          <TextField fullWidth type="number" label={t("lon")} value={birth.longitude} onChange={(e) => set("longitude", Number(e.target.value))} />
        </Grid>
        <Grid item xs={6} md={3}>
          <TextField fullWidth select label={t("ayanamsa")} value={birth.ayanamsa} onChange={(e) => set("ayanamsa", e.target.value)}>
            {(config.ayanamsas || [{ id: birth.ayanamsa, label: "Makaranda" }]).map((a: any) => (
              <MenuItem key={a.id} value={a.id}>{a.label}</MenuItem>
            ))}
          </TextField>
        </Grid>
        <Grid item xs={6} md={3}>
          <TextField fullWidth select label={t("houses")} value={birth.houseSystem} onChange={(e) => set("houseSystem", e.target.value)}>
            <MenuItem value="WHOLE_SIGN">{t("house_whole")}</MenuItem>
            <MenuItem value="EQUAL">{t("house_equal")}</MenuItem>
            <MenuItem value="SRIPATI">{t("house_sripati")}</MenuItem>
          </TextField>
        </Grid>
        <Grid item xs={12}>
          <ToggleButtonGroup exclusive value={birth.panchangMode} onChange={(_, v) => v && set("panchangMode", v)} size="small">
            <ToggleButton value="SIDDHANTIC">{t("mode_ss")}</ToggleButton>
            <ToggleButton value="DRIK">{t("mode_drik")}</ToggleButton>
          </ToggleButtonGroup>
        </Grid>
      </Grid>
      <Button sx={{ mt: 2 }} variant="contained" disabled={loading} onClick={() => (onSubmit ? onSubmit() : loadChart())}>
        {loading ? t("calculating") : (submitLabel || t("cast"))}
      </Button>
    </Box>
  );
}

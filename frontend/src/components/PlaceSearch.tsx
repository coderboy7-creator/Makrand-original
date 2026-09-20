import React, { useEffect, useRef, useState } from "react";
import { Box, Button, TextField } from "@mui/material";
import { api } from "../api";
import { useI18n } from "../i18n";

export default function PlaceSearch({
  label,
  value,
  onChange,
  helper,
}: {
  label?: string;
  value: string;
  onChange: (place: string, lat: number, lon: number) => void;
  helper?: string;
}) {
  const { t } = useI18n();
  const [text, setText] = useState(value || "");
  const [places, setPlaces] = useState<any[]>([]);
  const timer = useRef<number | undefined>(undefined);

  useEffect(() => { setText(value || ""); }, [value]);

  const search = (q: string) => {
    setText(q);
    window.clearTimeout(timer.current);
    if (q.trim().length < 2) {
      setPlaces([]);
      return;
    }
    timer.current = window.setTimeout(async () => {
      try {
        const r = await api.get("/api/v1/location/search?q=" + encodeURIComponent(q.trim()));
        setPlaces(Array.isArray(r) ? r : []);
      } catch {
        setPlaces([]);
      }
    }, 250);
  };

  return (
    <Box>
      <TextField fullWidth label={label || t("place")} value={text} onChange={(e) => search(e.target.value)}
        helperText={helper || t("place_help")} autoComplete="off" />
      {places.length > 0 && (
        <Box sx={{ mt: 0.5, maxHeight: 180, overflow: "auto", border: "1px solid rgba(201,162,39,0.25)", borderRadius: 1 }}>
          {places.map((p, i) => (
            <Button key={i} fullWidth sx={{ justifyContent: "flex-start", color: "#F7F1E3", textTransform: "none" }}
              onClick={() => {
                onChange(p.displayName, Number(p.lat), Number(p.lon));
                setText(p.displayName);
                setPlaces([]);
              }}>{p.displayName}</Button>
          ))}
        </Box>
      )}
    </Box>
  );
}

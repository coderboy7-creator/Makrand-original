import React, { useEffect, useRef, useState } from "react";
import { Box, Button, TextField, Typography } from "@mui/material";
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
  const { t, lang } = useI18n();
  const [text, setText] = useState(value || "");
  const [places, setPlaces] = useState<any[]>([]);
  const [open, setOpen] = useState(false);
  const [empty, setEmpty] = useState(false);
  const timer = useRef<number | undefined>(undefined);
  const typing = useRef(false);

  useEffect(() => {
    if (!typing.current) setText(value || "");
  }, [value]);

  const search = (q: string) => {
    typing.current = true;
    setText(q);
    window.clearTimeout(timer.current);
    if (q.trim().length < 2) {
      setPlaces([]);
      setEmpty(false);
      setOpen(false);
      return;
    }
    timer.current = window.setTimeout(async () => {
      try {
        const r = await api.get("/api/v1/location/search?q=" + encodeURIComponent(q.trim()));
        const list = Array.isArray(r) ? r : [];
        setPlaces(list);
        setEmpty(list.length === 0);
        setOpen(true);
      } catch {
        setPlaces([]);
        setEmpty(true);
        setOpen(true);
      }
    }, 220);
  };

  const pick = (p: any) => {
    typing.current = false;
    onChange(p.displayName, Number(p.lat), Number(p.lon));
    setText(p.displayName);
    setPlaces([]);
    setEmpty(false);
    setOpen(false);
  };

  return (
    <Box sx={{ position: "relative" }}>
      <TextField fullWidth label={label || t("place")} value={text} onChange={(e) => search(e.target.value)}
        helperText={helper === "" ? undefined : (helper || t("place_help"))} autoComplete="off"
        onBlur={() => { window.setTimeout(() => { typing.current = false; setOpen(false); }, 180); }}
        onFocus={() => { if (places.length || empty) setOpen(true); }}
        sx={{ "& .MuiOutlinedInput-root": { height: 42 } }} />
      {open && (places.length > 0 || empty) && (
        <Box sx={{
          position: "absolute", zIndex: 20, left: 0, right: 0, mt: 0.5,
          maxHeight: 220, overflow: "auto",
          border: "1px solid rgba(232,197,71,0.28)", borderRadius: 1,
          bgcolor: "rgba(12,8,16,0.97)",
        }}>
          {empty && (
            <Typography variant="body2" sx={{ px: 1.5, py: 1 }} color="text.secondary">
              {lang === "hi" ? "कोई स्थान नहीं मिला" : "No places found"}
            </Typography>
          )}
          {places.map((p, i) => (
            <Button key={i} fullWidth sx={{ justifyContent: "flex-start", color: "#F7F1E3", textTransform: "none", px: 1.5 }}
              onMouseDown={(e) => e.preventDefault()}
              onClick={() => pick(p)}>{p.displayName}</Button>
          ))}
        </Box>
      )}
    </Box>
  );
}

import React, { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import {
  AppBar, Box, Button, Drawer, IconButton, List, ListItemButton, ListItemText,
  Toolbar, Typography, useMediaQuery, Divider, ToggleButton, ToggleButtonGroup
} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import { useApp } from "../state";
import { useI18n } from "../i18n";

const NAV = [
  { to: "/", key: "nav_home" },
  { to: "/kundali", key: "nav_kundali" },
  { to: "/vargas", key: "nav_vargas" },
  { to: "/panchang", key: "nav_panchang" },
  { to: "/milan", key: "nav_milan" },
  { to: "/dasha", key: "nav_dasha" },
  { to: "/horoscope", key: "nav_horoscope" },
  { to: "/yogas", key: "nav_yogas" },
  { to: "/nakshatra", key: "nav_nakshatra" },
  { to: "/rashi", key: "nav_rashi" },
  { to: "/muhurta", key: "nav_muhurta" },
  { to: "/gochar", key: "nav_gochar" },
  { to: "/gems", key: "nav_gems" },
  { to: "/varshaphal", key: "nav_varshaphal" },
  { to: "/prashna", key: "nav_prashna" },
  { to: "/learn", key: "nav_learn" },
  { to: "/consult", key: "nav_consult" },
  { to: "/crm", key: "nav_crm" },
  { to: "/admin", key: "nav_admin" },
];

export default function Layout({ children }: { children: React.ReactNode }) {
  const loc = useLocation();
  const nav = useNavigate();
  const { user, logout } = useApp();
  const { t, lang, setLang } = useI18n();
  const isMobile = useMediaQuery("(max-width:900px)");
  const [open, setOpen] = useState(false);
  const drawer = (
    <Box sx={{ width: 260, height: "100%", bgcolor: "#0E1430", background: "linear-gradient(180deg,#12081A,#0B1026)" }} onClick={() => setOpen(false)}>
      <Box sx={{ p: 2.5 }}>
        <Typography variant="h6" color="primary">{t("brand")}</Typography>
        <Typography variant="caption" color="text.secondary">{t("tagline")}</Typography>
      </Box>
      <Divider sx={{ borderColor: "rgba(201,162,39,0.2)" }} />
      <List dense>
        {NAV.map((n) => (
          <ListItemButton key={n.to} component={Link} to={n.to} selected={loc.pathname === n.to}>
            <ListItemText primary={t(n.key)} />
          </ListItemButton>
        ))}
      </List>
    </Box>
  );
  return (
    <Box sx={{ minHeight: "100vh", display: "flex", bgcolor: "background.default" }}>
      {!isMobile && (
        <Box component="nav" sx={{ width: 260, flexShrink: 0, borderRight: "1px solid rgba(201,162,39,0.18)" }}>
          {drawer}
        </Box>
      )}
      <Box sx={{ flex: 1, minWidth: 0 }}>
        <AppBar position="sticky" elevation={0} sx={{ bgcolor: "rgba(11,16,38,0.92)", borderBottom: "1px solid rgba(201,162,39,0.2)" }}>
          <Toolbar>
            {isMobile && (
              <IconButton color="inherit" onClick={() => setOpen(true)} sx={{ mr: 1 }}><MenuIcon /></IconButton>
            )}
            <Typography sx={{ flex: 1, fontWeight: 700, color: "#C9A227" }}>
              {t("brand")}
            </Typography>
            <ToggleButtonGroup exclusive size="small" value={lang} onChange={(_, v) => v && setLang(v)} sx={{ mr: 2 }}>
              <ToggleButton value="hi">{t("lang_hi")}</ToggleButton>
              <ToggleButton value="en">{t("lang_en")}</ToggleButton>
            </ToggleButtonGroup>
            {user ? (
              <>
                <Typography variant="body2" sx={{ mr: 2 }}>{user.name}</Typography>
                <Button color="primary" onClick={logout}>{t("logout")}</Button>
              </>
            ) : (
              <Button color="primary" onClick={() => nav("/login")}>{t("login")}</Button>
            )}
          </Toolbar>
        </AppBar>
        <Box sx={{ p: { xs: 2, md: 3 }, pb: 8 }}>{children}</Box>
      </Box>
      <Drawer open={open} onClose={() => setOpen(false)}>{drawer}</Drawer>
    </Box>
  );
}

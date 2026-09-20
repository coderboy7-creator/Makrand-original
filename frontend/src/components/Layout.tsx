import React, { useState } from "react";
import { Link, NavLink, useLocation, useNavigate } from "react-router-dom";
import {
  Alert, AppBar, Box, Button, Drawer, IconButton, List, ListItemButton, ListItemText,
  Toolbar, Typography, useMediaQuery, Divider,
} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import { useApp } from "../state";
import { useI18n } from "../i18n";

type NavItem = { to: string; key: string };

const LINKS: NavItem[] = [
  { to: "/", key: "nav_home" },
  { to: "/kundali", key: "nav_kundali" },
  { to: "/panchang", key: "nav_panchang" },
  { to: "/vargas", key: "nav_vargas" },
  { to: "/milan", key: "nav_milan" },
  { to: "/dasha", key: "nav_dasha" },
  { to: "/muhurta", key: "nav_muhurta" },
  { to: "/gochar", key: "nav_gochar" },
  { to: "/yogas", key: "nav_yogas" },
  { to: "/horoscope", key: "nav_horoscope" },
  { to: "/nakshatra", key: "nav_nakshatra" },
  { to: "/rashi", key: "nav_rashi" },
  { to: "/varshaphal", key: "nav_varshaphal" },
  { to: "/prashna", key: "nav_prashna" },
  { to: "/gems", key: "nav_gems" },
  { to: "/learn", key: "nav_learn" },
  { to: "/consult", key: "nav_consult" },
  { to: "/crm", key: "nav_crm" },
  { to: "/admin", key: "nav_admin" },
];

function Logo() {
  const { lang } = useI18n();
  return (
    <Box sx={{ display: "flex", alignItems: "center", gap: 1.2, textDecoration: "none", color: "inherit", flexShrink: 0 }} component={Link} to="/">
      <Box sx={{
        width: 36, height: 36, borderRadius: "50%",
        background: "linear-gradient(135deg,#F6E27A,#C47A12)",
        display: "grid", placeItems: "center",
        boxShadow: "0 0 18px rgba(232,197,71,0.45)",
        fontSize: 18, fontWeight: 800, color: "#1A1208",
      }}>म</Box>
      <Box>
        <Typography sx={{ fontFamily: "Georgia, 'Noto Serif Devanagari', serif", fontWeight: 700, lineHeight: 1, fontSize: 20, color: "#F6E27A" }}>
          {lang === "hi" ? "मकरन्द" : "Makaranda"}
        </Typography>
        <Typography variant="caption" sx={{ letterSpacing: 2, color: "text.secondary", display: "block", mt: -0.2 }}>
          {lang === "hi" ? "ज्योतिष" : "JYOTISH"}
        </Typography>
      </Box>
    </Box>
  );
}

const linkSx = {
  color: "inherit",
  minWidth: "auto",
  px: 1.25,
  fontWeight: 600,
  opacity: 0.78,
  whiteSpace: "nowrap" as const,
  "&.active": { opacity: 1, color: "#F6E27A" },
};

export default function Layout({ children }: { children: React.ReactNode }) {
  const loc = useLocation();
  const nav = useNavigate();
  const { user, logout, apiOk, error } = useApp();
  const { t, lang, setLang } = useI18n();
  const isMobile = useMediaQuery("(max-width:900px)");
  const [open, setOpen] = useState(false);

  const drawer = (
    <Box sx={{ width: 280, height: "100%", background: "linear-gradient(180deg,#1A1018,#07060F)" }} onClick={() => setOpen(false)}>
      <Box sx={{ p: 2.5 }}><Logo /></Box>
      <Divider sx={{ borderColor: "rgba(232,197,71,0.15)" }} />
      <List dense>
        {LINKS.map((n) => (
          <ListItemButton key={n.to} component={Link} to={n.to} selected={loc.pathname === n.to}>
            <ListItemText primary={t(n.key)} />
          </ListItemButton>
        ))}
      </List>
    </Box>
  );

  return (
    <Box sx={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
      <AppBar position="sticky" elevation={0} sx={{
        bgcolor: "rgba(7,6,15,0.92)",
        backdropFilter: "blur(18px)",
        borderBottom: "1px solid rgba(232,197,71,0.14)",
        zIndex: 1200,
      }}>
        <Toolbar sx={{ gap: 1, minHeight: 64, flexWrap: "nowrap" }}>
          {isMobile && (
            <IconButton color="inherit" onClick={() => setOpen(true)} aria-label="menu"><MenuIcon /></IconButton>
          )}
          <Logo />
          {!isMobile && (
            <Box sx={{ display: "flex", alignItems: "center", gap: 0.25, flex: 1, overflowX: "auto", mx: 1, py: 0.5 }}>
              {LINKS.map((n) => (
                <Button
                  key={n.to}
                  component={NavLink}
                  to={n.to}
                  end={n.to === "/"}
                  color="inherit"
                  sx={linkSx}
                >
                  {t(n.key)}
                </Button>
              ))}
            </Box>
          )}
          <Box sx={{ ml: "auto", display: "flex", alignItems: "center", gap: 1, flexShrink: 0 }}>
            <Button
              size="small"
              variant="outlined"
              onClick={() => setLang(lang === "hi" ? "en" : "hi")}
              sx={{ borderColor: "rgba(232,197,71,0.35)", color: "#F6E27A", borderRadius: 999 }}
            >
              {lang === "hi" ? "English" : "हिन्दी"}
            </Button>
            {user ? (
              <>
                <Typography variant="body2" sx={{ display: { xs: "none", sm: "block" } }}>{user.name}</Typography>
                <Button color="inherit" onClick={logout}>{t("logout")}</Button>
              </>
            ) : (
              <Button variant="contained" onClick={() => nav("/login")}>{t("login")}</Button>
            )}
          </Box>
        </Toolbar>
      </AppBar>
      {apiOk === false && (
        <Alert severity="error" sx={{ borderRadius: 0 }}>
          {lang === "hi"
            ? "गणना सर्वर (पोर्ट 8080) बंद है — कुंडली/पंचांग बटन काम नहीं करेंगे जब तक API चालू न हो।"
            : "Calculation API is down (port 8080). Cast / Compute will do nothing until the server is running."}
        </Alert>
      )}
      {error && loc.pathname !== "/kundali" && (
        <Alert severity="warning" sx={{ borderRadius: 0 }}>{error}</Alert>
      )}
      <Box sx={{ flex: 1, p: { xs: 2, md: 3.5 }, pb: 8, maxWidth: 1280, mx: "auto", width: "100%" }}>{children}</Box>
      <Box sx={{ py: 2, textAlign: "center", borderTop: "1px solid rgba(232,197,71,0.1)", color: "text.secondary" }}>
        <Typography variant="caption">{t("tagline")}</Typography>
      </Box>
      <Drawer open={open} onClose={() => setOpen(false)}>{drawer}</Drawer>
    </Box>
  );
}

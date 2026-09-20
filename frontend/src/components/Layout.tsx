import React, { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import {
  AppBar, Box, Button, Drawer, IconButton, List, ListItemButton, ListItemText,
  Menu, MenuItem, Toolbar, Typography, useMediaQuery, Divider,
} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import { useApp } from "../state";
import { useI18n } from "../i18n";

type NavItem = { to: string; key: string };
type NavGroup = { key: string; items: NavItem[] };

const GROUPS: NavGroup[] = [
  { key: "navg_kundali", items: [{ to: "/kundali", key: "nav_kundali" }, { to: "/vargas", key: "nav_vargas" }] },
  { key: "navg_panchang", items: [{ to: "/panchang", key: "nav_panchang" }, { to: "/muhurta", key: "nav_muhurta" }] },
  {
    key: "navg_tools",
    items: [
      { to: "/milan", key: "nav_milan" }, { to: "/dasha", key: "nav_dasha" }, { to: "/yogas", key: "nav_yogas" },
      { to: "/gochar", key: "nav_gochar" }, { to: "/varshaphal", key: "nav_varshaphal" },
      { to: "/prashna", key: "nav_prashna" }, { to: "/gems", key: "nav_gems" },
    ],
  },
  {
    key: "navg_learn",
    items: [
      { to: "/horoscope", key: "nav_horoscope" }, { to: "/nakshatra", key: "nav_nakshatra" },
      { to: "/rashi", key: "nav_rashi" }, { to: "/learn", key: "nav_learn" },
    ],
  },
  { key: "navg_practice", items: [{ to: "/consult", key: "nav_consult" }, { to: "/crm", key: "nav_crm" }, { to: "/admin", key: "nav_admin" }] },
];

const FLAT: NavItem[] = [{ to: "/", key: "nav_home" }, ...GROUPS.flatMap((g) => g.items)];

function Logo() {
  return (
    <Box sx={{ display: "flex", alignItems: "center", gap: 1.2, textDecoration: "none", color: "inherit" }} component={Link} to="/">
      <Box sx={{
        width: 36, height: 36, borderRadius: "50%",
        background: "linear-gradient(135deg,#F6E27A,#C47A12)",
        display: "grid", placeItems: "center",
        boxShadow: "0 0 18px rgba(232,197,71,0.45)",
        fontSize: 18, fontWeight: 800, color: "#1A1208",
      }}>म</Box>
      <Box>
        <Typography sx={{ fontFamily: "'Cormorant Garamond', serif", fontWeight: 700, lineHeight: 1, fontSize: 20, color: "#F6E27A" }}>
          Makaranda
        </Typography>
        <Typography variant="caption" sx={{ letterSpacing: 2, color: "text.secondary", display: "block", mt: -0.2 }}>JYOTISH</Typography>
      </Box>
    </Box>
  );
}

function NavMenus({ t, path }: { t: (k: string) => string; path: string }) {
  const [openKey, setOpenKey] = useState<string | null>(null);
  const [anchor, setAnchor] = useState<HTMLElement | null>(null);
  return (
    <Box sx={{ display: "flex", alignItems: "center", gap: 0.5, flex: 1, justifyContent: "center" }}>
      <Button component={Link} to="/" color="inherit" sx={{ opacity: path === "/" ? 1 : 0.75, fontWeight: 700 }}>{t("nav_home")}</Button>
      {GROUPS.map((g) => (
        <Box key={g.key}>
          <Button
            color="inherit"
            onClick={(e) => { setAnchor(e.currentTarget); setOpenKey(g.key); }}
            sx={{ opacity: g.items.some((i) => i.to === path) ? 1 : 0.75, fontWeight: 600 }}
          >
            {t(g.key)} ▾
          </Button>
          <Menu
            open={openKey === g.key}
            anchorEl={anchor}
            onClose={() => setOpenKey(null)}
            PaperProps={{ sx: { mt: 1, minWidth: 200, bgcolor: "rgba(18,12,22,0.96)" } }}
          >
            {g.items.map((i) => (
              <MenuItem key={i.to} component={Link} to={i.to} selected={path === i.to} onClick={() => setOpenKey(null)}>
                {t(i.key)}
              </MenuItem>
            ))}
          </Menu>
        </Box>
      ))}
    </Box>
  );
}

export default function Layout({ children }: { children: React.ReactNode }) {
  const loc = useLocation();
  const nav = useNavigate();
  const { user, logout } = useApp();
  const { t, lang, setLang } = useI18n();
  const isMobile = useMediaQuery("(max-width:1100px)");
  const [open, setOpen] = useState(false);

  const drawer = (
    <Box sx={{ width: 280, height: "100%", background: "linear-gradient(180deg,#1A1018,#07060F)" }} onClick={() => setOpen(false)}>
      <Box sx={{ p: 2.5 }}><Logo /></Box>
      <Divider sx={{ borderColor: "rgba(232,197,71,0.15)" }} />
      <List dense>
        {FLAT.map((n) => (
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
        bgcolor: "rgba(7,6,15,0.72)",
        backdropFilter: "blur(18px)",
        borderBottom: "1px solid rgba(232,197,71,0.14)",
      }}>
        <Toolbar sx={{ gap: 1, minHeight: 72 }}>
          {isMobile && (
            <IconButton color="inherit" onClick={() => setOpen(true)}><MenuIcon /></IconButton>
          )}
          <Logo />
          {!isMobile && <NavMenus t={t} path={loc.pathname} />}
          <Box sx={{ ml: "auto", display: "flex", alignItems: "center", gap: 1 }}>
            <Button
              size="small"
              variant="outlined"
              onClick={() => setLang(lang === "hi" ? "en" : "hi")}
              sx={{ borderColor: "rgba(232,197,71,0.35)", color: "#F6E27A", borderRadius: 999 }}
            >
              {lang === "hi" ? "हिन्दी" : "ENG"}
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
      <Box sx={{ flex: 1, p: { xs: 2, md: 3.5 }, pb: 8, maxWidth: 1280, mx: "auto", width: "100%" }}>{children}</Box>
      <Box sx={{ py: 2, textAlign: "center", borderTop: "1px solid rgba(232,197,71,0.1)", color: "text.secondary" }}>
        <Typography variant="caption">{t("tagline")}</Typography>
      </Box>
      <Drawer open={open} onClose={() => setOpen(false)}>{drawer}</Drawer>
    </Box>
  );
}

import { createTheme } from "@mui/material/styles";

export const theme = createTheme({
  palette: {
    mode: "dark",
    primary: { main: "#C9A227", contrastText: "#1A1208" },
    secondary: { main: "#E07A2F" },
    background: { default: "#0B1026", paper: "#141A33" },
    error: { main: "#E06C75" },
    success: { main: "#7BC67E" },
    text: { primary: "#F7F1E3", secondary: "#C4BBA8" },
  },
  typography: {
    fontFamily: "Georgia, 'Palatino Linotype', Palatino, 'Times New Roman', serif",
    h1: { fontWeight: 700, letterSpacing: 1 },
    h2: { fontWeight: 700 },
    h3: { fontWeight: 700 },
    h4: { fontWeight: 700, letterSpacing: 0.4 },
    h5: { fontWeight: 700 },
    h6: { fontWeight: 700 },
    button: { fontFamily: "system-ui, Segoe UI, sans-serif", fontWeight: 700, letterSpacing: 0.6 },
    body1: { fontFamily: "system-ui, Segoe UI, sans-serif" },
    body2: { fontFamily: "system-ui, Segoe UI, sans-serif" },
    caption: { fontFamily: "system-ui, Segoe UI, sans-serif" },
  },
  shape: { borderRadius: 14 },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: "none",
          border: "1px solid rgba(201,162,39,0.18)",
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: { textTransform: "none", borderRadius: 10 },
      },
    },
  },
});

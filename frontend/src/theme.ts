import { createTheme } from "@mui/material/styles";

export const theme = createTheme({
  palette: {
    mode: "dark",
    primary: { main: "#E8C547", contrastText: "#1A1208" },
    secondary: { main: "#FF8A3D" },
    background: { default: "#07060F", paper: "rgba(22, 16, 28, 0.72)" },
    error: { main: "#FF6B7A" },
    success: { main: "#7DCEA0" },
    text: { primary: "#FBF6EA", secondary: "rgba(251,246,234,0.72)" },
    divider: "rgba(232,197,71,0.16)",
  },
  typography: {
    fontFamily: "'Plus Jakarta Sans', 'Noto Sans Devanagari', system-ui, sans-serif",
    h1: { fontFamily: "'Cormorant Garamond', Georgia, serif", fontWeight: 600, letterSpacing: 0.4 },
    h2: { fontFamily: "'Cormorant Garamond', Georgia, serif", fontWeight: 600 },
    h3: { fontFamily: "'Cormorant Garamond', Georgia, serif", fontWeight: 600 },
    h4: { fontFamily: "'Cormorant Garamond', Georgia, serif", fontWeight: 600, letterSpacing: 0.3 },
    h5: { fontFamily: "'Cormorant Garamond', Georgia, serif", fontWeight: 600 },
    h6: { fontFamily: "'Cormorant Garamond', Georgia, serif", fontWeight: 600 },
    button: { fontWeight: 700, letterSpacing: 0.4, textTransform: "none" },
  },
  shape: { borderRadius: 18 },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          background:
            "radial-gradient(1200px 600px at 12% -10%, rgba(232,197,71,0.16), transparent 55%)," +
            "radial-gradient(900px 500px at 110% 0%, rgba(180,60,40,0.22), transparent 50%)," +
            "#07060F",
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage:
            "linear-gradient(165deg, rgba(255,214,120,0.08), rgba(20,12,18,0.4) 42%, rgba(8,6,14,0.55))",
          backdropFilter: "blur(16px)",
          border: "1px solid rgba(232,197,71,0.18)",
          boxShadow: "0 18px 50px rgba(0,0,0,0.35)",
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundImage:
            "linear-gradient(160deg, rgba(255,200,90,0.12), rgba(28,14,20,0.55) 40%, rgba(10,8,16,0.7))",
          border: "1px solid rgba(232,197,71,0.2)",
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: { borderRadius: 999, paddingLeft: 18, paddingRight: 18, fontWeight: 700 },
        containedPrimary: {
          background: "linear-gradient(90deg,#F3D36A,#E0A020)",
          color: "#1A1208",
          boxShadow: "0 8px 24px rgba(224,160,32,0.28)",
          "&:hover": { background: "linear-gradient(90deg,#FFE08A,#E8B84A)" },
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: { backgroundImage: "none" },
      },
    },
    MuiTextField: {
      defaultProps: { variant: "outlined", size: "small" },
    },
    MuiOutlinedInput: {
      styleOverrides: {
        root: {
          background: "rgba(0,0,0,0.22)",
          borderRadius: 12,
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        head: { color: "#E8C547", fontWeight: 700, borderColor: "rgba(232,197,71,0.16)" },
        body: { borderColor: "rgba(232,197,71,0.08)" },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: { borderRadius: 999 },
      },
    },
  },
});

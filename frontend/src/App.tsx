import { Route, Routes } from "react-router-dom";
import Layout from "./components/Layout";
import {
  AdminPage, ConsultPage, CrmPage, DashaPage, GemsPage, GocharPage, HomePage, HoroscopePage,
  KundaliPage, LearnPage, LoginPage, MilanPage, MuhurtaPage, NakshatraPage, PanchangPage,
  PrashnaPage, RashiPage, VargasPage, VarshaPage, YogasPage
} from "./pages/Pages";

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/kundali" element={<KundaliPage />} />
        <Route path="/vargas" element={<VargasPage />} />
        <Route path="/panchang" element={<PanchangPage />} />
        <Route path="/milan" element={<MilanPage />} />
        <Route path="/dasha" element={<DashaPage />} />
        <Route path="/horoscope" element={<HoroscopePage />} />
        <Route path="/yogas" element={<YogasPage />} />
        <Route path="/nakshatra" element={<NakshatraPage />} />
        <Route path="/rashi" element={<RashiPage />} />
        <Route path="/muhurta" element={<MuhurtaPage />} />
        <Route path="/gochar" element={<GocharPage />} />
        <Route path="/gems" element={<GemsPage />} />
        <Route path="/varshaphal" element={<VarshaPage />} />
        <Route path="/prashna" element={<PrashnaPage />} />
        <Route path="/learn" element={<LearnPage />} />
        <Route path="/consult" element={<ConsultPage />} />
        <Route path="/crm" element={<CrmPage />} />
        <Route path="/admin" element={<AdminPage />} />
        <Route path="/login" element={<LoginPage />} />
      </Routes>
    </Layout>
  );
}

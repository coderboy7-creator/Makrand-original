import React, { useEffect, useState } from "react";
import { Link as RouterLink, useSearchParams } from "react-router-dom";
import {
  Alert, Box, Button, Card, CardContent, Chip, Grid, LinearProgress, MenuItem,
  Tab, Tabs, Table, TableBody, TableCell, TableHead, TableRow, TextField,
  ToggleButton, ToggleButtonGroup, Typography
} from "@mui/material";
import BirthForm from "../components/BirthForm";
import HonestyNote from "../components/HonestyNote";
import KundaliChart from "../components/KundaliChart";
import PanchangMonthGrid from "../components/PanchangMonthGrid";
import PlaceSearch from "../components/PlaceSearch";
import { api, defaultBirth, fetchPdf, objectUrl, triggerDownload } from "../api";
import { useApp } from "../state";
import { useI18n } from "../i18n";
import { grahaName, nakName, dignityName, rashiName, yogaName, yogaText, yogaType, gemPhrase, prashnaVerdict, articleTitle, articleBody, astroBio } from "../jyotishLabels";
import { GlassCard, GoldTitle, LimbTile, MetaRow, PageHero, ScoreHero } from "../ui";
import { chartFromOrigin, gocharAsChart, VARGA_MEANING } from "../chartViews";
import { DashaBoard } from "../dashaViews";
import { DoshaBoard } from "../doshaViews";

export function HomePage() {
  const { config } = useApp();
  const { t, lang } = useI18n();
  const [panch, setPanch] = useState<any>(null);
  useEffect(() => { api.get("/api/v1/jyotish/panchang").then(setPanch).catch(() => {}); }, []);
  return (
    <Box>
      <PageHero title={t("home_title")} sub={t("home_sub")}>
        <Typography sx={{ maxWidth: 680, mx: { md: "auto" }, mt: 1.5 }}>{t("home_blurb")}</Typography>
        <Button component={RouterLink} to="/kundali" variant="contained" sx={{ mt: 2 }}>{t("cast")}</Button>
      </PageHero>
      <Grid container spacing={2}>
        {[
          [t("home_k"), "/kundali", t("home_k_d")],
          [t("home_p"), "/panchang", t("home_p_d")],
          [t("home_m"), "/milan", t("home_m_d")],
          [t("home_c"), "/consult", t("home_c_d")],
        ].map(([title, href, d]) => (
          <Grid item xs={12} md={3} key={href}>
            <RouterLink to={href} style={{ textDecoration: "none", color: "inherit", display: "block", height: "100%" }}>
              <Card sx={{ height: "100%", transition: "transform .2s", "&:hover": { transform: "translateY(-4px)" } }}>
                <CardContent>
                  <Typography variant="h6" color="primary">{title}</Typography>
                  <Typography variant="body2" color="text.secondary">{d}</Typography>
                </CardContent>
              </Card>
            </RouterLink>
          </Grid>
        ))}
      </Grid>
      {panch && (
        <Card sx={{ mt: 3 }}>
          <CardContent>
            <Typography variant="h6" color="primary">{t("today_panchang")} — {panch.date}</Typography>
            <Typography>
              {lang === "hi" ? panch.varaHi : panch.vara} · {lang === "hi" ? panch.pakshaHi : panch.paksha}{" "}
              {lang === "hi" ? panch.tithiHi : panch.tithi}
              {panch.tithiEnd ? ` (${t("until")} ${panch.tithiEnd})` : ""} · {lang === "hi" ? panch.nakshatraHi : panch.nakshatra}
              {panch.nakshatraEnd ? ` (${t("until")} ${panch.nakshatraEnd})` : ""}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {t("sunrise")} {panch.sunrise} · {t("sunset")} {panch.sunset} · {panch.makarandaNote}
            </Typography>
          </CardContent>
        </Card>
      )}
      <HonestyNote mode={config.defaultPanchangMode} />
      <Typography variant="caption" display="block" sx={{ mt: 2 }}>
        {config.defaultAyanamsa} · {config.defaultPanchangMode} · {config.defaultPlace}
      </Typography>
    </Box>
  );
}

export function KundaliPage() {
  const { chart, loadChart, error, birth } = useApp();
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const [style, setStyle] = useState("NORTH");
  const [searchParams, setSearchParams] = useSearchParams();
  const [tab, setTab] = useState(() => searchParams.get("tab") === "dasha" ? 3 : 0);
  const [panch, setPanch] = useState<any>(null);
  const [chalit, setChalit] = useState<any>(null);
  const [gochar, setGochar] = useState<any>(null);
  const [pdfBusy, setPdfBusy] = useState(false);
  const [pdfErr, setPdfErr] = useState("");
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const makePdf = async (mode: "preview" | "download") => {
    setPdfBusy(true);
    setPdfErr("");
    try {
      const blob = await fetchPdf("/api/v1/jyotish/report", birth);
      if (pdfUrl) URL.revokeObjectURL(pdfUrl);
      if (mode === "download") {
        const url = triggerDownload(blob, "makaranda-kundali.pdf");
        setPdfUrl(url);
      } else {
        setPdfUrl(objectUrl(blob));
      }
    } catch (e: any) {
      setPdfErr(e.message || t("pdf_fail"));
    } finally {
      setPdfBusy(false);
    }
  };
  const pdfBar = (
    <Box sx={{ mt: 2 }}>
      <Box sx={{ display: "flex", flexWrap: "wrap", gap: 1 }}>
        <Button variant="outlined" disabled={pdfBusy} onClick={() => makePdf("preview")}>
          {pdfBusy ? t("pdf_wait") : t("pdf_preview")}
        </Button>
        <Button variant="contained" disabled={pdfBusy} onClick={() => makePdf("download")}>
          {pdfBusy ? t("pdf_wait") : t("pdf_download")}
        </Button>
      </Box>
      {pdfErr && <Alert severity="error" sx={{ mt: 1 }}>{t("pdf_fail")}: {pdfErr}</Alert>}
      {pdfUrl && (
        <Box sx={{ mt: 2 }}>
          <Button href={pdfUrl} target="_blank" rel="noopener" sx={{ mb: 1 }}>{t("pdf_open")}</Button>
          <Box component="iframe" title="makaranda-pdf" src={pdfUrl}
            sx={{ width: "100%", height: 640, border: "1px solid rgba(232,197,71,0.25)", borderRadius: 1, bgcolor: "#111" }} />
        </Box>
      )}
    </Box>
  );
  useEffect(() => { if (!chart) loadChart().catch(() => {}); }, []);
  useEffect(() => {
    const d = String(birth.dateTime || "").slice(0, 10);
    if (!d) return;
    api.get(`/api/v1/jyotish/panchang?date=${d}&lat=${birth.latitude}&lon=${birth.longitude}&ayanamsa=${birth.ayanamsa}&mode=${birth.panchangMode}`)
      .then(setPanch).catch(() => {});
  }, [birth.dateTime, birth.latitude, birth.longitude, birth.ayanamsa, birth.panchangMode]);
  useEffect(() => {
    if (!chart) return;
    api.post("/api/v1/jyotish/kundali", { ...birth, houseSystem: "SRIPATI" }).then(setChalit).catch(() => setChalit(null));
    api.post("/api/v1/jyotish/gochar", birth).then(setGochar).catch(() => setGochar(null));
  }, [chart, birth.dateTime, birth.latitude, birth.longitude, birth.ayanamsa, birth.panchangMode]);
  const v9 = chart?.vargas?.[9];
  const v9Chart = v9 ? {
    lagna: { signIndex: v9.bodies?.[0]?.signIndex || 0 },
    planets: Object.fromEntries((v9.bodies || []).filter((b: any) => b.name !== "Lagna").map((b: any) => [b.name, b])),
  } : null;
  return (
    <Box>
      <GoldTitle sub={t("kundali_sub")}>{t("kundali_title")}</GoldTitle>
      <BirthForm />
      {error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
      {chart && (
        <>
          <Tabs value={tab} onChange={(_, v) => {
            setTab(v);
            if (v === 3) setSearchParams({ tab: "dasha" }, { replace: true });
            else if (searchParams.get("tab")) setSearchParams({}, { replace: true });
          }} variant="scrollable" sx={{ mt: 2, mb: 2, borderBottom: "1px solid rgba(232,197,71,0.16)" }}>
            <Tab label={t("tab_basic")} /><Tab label={t("tab_kundali")} /><Tab label={t("tab_charts")} /><Tab label={t("tab_dasha")} /><Tab label={t("tab_kp")} />
          </Tabs>
          {tab === 0 && (
            <Grid container spacing={2}>
              <Grid item xs={12} md={6}>
                <GlassCard>
                  <Typography variant="h6" color="primary" sx={{ mb: 1 }}>{t("birth_details")}</Typography>
                  <MetaRow k={t("name")} v={birth.name} />
                  <MetaRow k={t("date")} v={String(birth.dateTime || "").replace("T", " ")} />
                  <MetaRow k={t("place")} v={birth.place} />
                  <MetaRow k={t("lat")} v={`${Number(birth.latitude).toFixed(4)}°`} />
                  <MetaRow k={t("lon")} v={`${Number(birth.longitude).toFixed(4)}°`} />
                  <MetaRow k={t("timezone")} v="GMT +05:30" />
                  <MetaRow k={t("ayanamsa")} v={chart.ayanamsaLabel} />
                  <MetaRow k={t("lagna")} v={`${hi ? (chart.lagna?.signHi || chart.lagna?.signSa) : chart.lagna?.sign} ${chart.lagna?.signDegree || ""}`} />
                  <MetaRow k={t("lagnesh")} v={grahaName(chart.avakahada?.lagnesh || chart.lagna?.rashiLord, hi)} />
                </GlassCard>
              </Grid>
              <Grid item xs={12} md={6}>
                <GlassCard>
                  <Typography variant="h6" color="primary" sx={{ mb: 1 }}>{t("panchang_card")}</Typography>
                  {panch ? (
                    <>
                      <MetaRow k={t("tithi")} v={`${hi ? panch.pakshaHi : panch.paksha} ${hi ? panch.tithiHi : panch.tithi}${panch.tithiEnd ? ` · ${t("until")} ${panch.tithiEnd}` : ""}`} />
                      <MetaRow k={t("nakshatra")} v={`${hi ? panch.nakshatraHi : panch.nakshatra}${panch.nakshatraEnd ? ` · ${t("until")} ${panch.nakshatraEnd}` : ""}`} />
                      <MetaRow k={t("yoga")} v={hi ? panch.yogaHi : panch.yoga} />
                      <MetaRow k={t("karana")} v={hi ? panch.karanaHi : panch.karana} />
                      <MetaRow k={t("sunrise")} v={panch.sunrise} />
                      <MetaRow k={t("sunset")} v={panch.sunset} />
                    </>
                  ) : <Typography variant="body2">{t("loading")}</Typography>}
                </GlassCard>
              </Grid>
              {chart.doshaPanel && (
                <Grid item xs={12}>
                  <GlassCard>
                    <DoshaBoard panel={chart.doshaPanel} hi={hi} t={t} />
                  </GlassCard>
                </Grid>
              )}
              {chart.avakahada && (
                <Grid item xs={12}>
                  <GlassCard>
                    <Typography variant="h6" color="primary" sx={{ mb: 1 }}>{t("avakahada")}</Typography>
                    <Grid container spacing={1}>
                      {[
                        [t("varna"), hi ? chart.avakahada.varnaHi : chart.avakahada.varna],
                        [t("vashya"), hi ? chart.avakahada.vashyaHi : chart.avakahada.vashya],
                        [t("yoni"), hi ? chart.avakahada.yoniHi : chart.avakahada.yoni],
                        [t("gana"), hi ? chart.avakahada.ganaHi : chart.avakahada.gana],
                        [t("nadi"), hi ? chart.avakahada.nadiHi : chart.avakahada.nadi],
                        [t("rashi"), hi ? chart.avakahada.rashiHi : chart.avakahada.rashi],
                        [t("rashi_lord"), grahaName(chart.avakahada.rashiLord, hi)],
                        [t("nakshatra"), hi ? chart.avakahada.nakshatraHi : chart.avakahada.nakshatra],
                        [t("nak_lord"), grahaName(chart.avakahada.nakLord, hi)],
                        [t("charan"), chart.avakahada.pada],
                        [t("tattva"), hi ? chart.avakahada.tattvaHi : chart.avakahada.tattva],
                        [t("namakshara"), chart.avakahada.namakshara],
                        [t("paya"), hi ? chart.avakahada.payaHi : chart.avakahada.paya],
                      ].map(([k, v]) => (
                        <Grid item xs={6} sm={4} md={3} key={String(k)}>
                          <Typography variant="caption" color="text.secondary">{k}</Typography>
                          <Typography variant="body2">{v}</Typography>
                        </Grid>
                      ))}
                    </Grid>
                  </GlassCard>
                </Grid>
              )}
              <Grid item xs={12}>{pdfBar}</Grid>
            </Grid>
          )}
          {tab === 1 && (
            <Grid container spacing={2}>
              <Grid item xs={12} md={6}>
                <Box sx={{ display: "flex", gap: 1, mb: 1 }}>
                  {[["NORTH", t("style_n")], ["SOUTH", t("style_s")], ["EAST", t("style_e")]].map(([s, lab]) => (
                    <Chip key={s} label={lab} onClick={() => setStyle(s)} color={style === s ? "primary" : "default"} />
                  ))}
                </Box>
                <KundaliChart chart={chart} style={style} />
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography variant="h6" color="primary">{t("lagna")} {hi ? (chart.lagna?.signHi || chart.lagna?.signSa) : chart.lagna?.sign} — {chart.lagna?.signDegree}</Typography>
                <Typography variant="body2" sx={{ mb: 1 }}>
                  {t("ayanamsa")} {chart.ayanamsaLabel} = {Number(chart.ayanamsaDeg).toFixed(4)}° · {chart.panchangMode}
                </Typography>
                <Box sx={{ overflowX: "auto" }}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>{t("graha")}</TableCell>
                      <TableCell>{t("rashi")}</TableCell>
                      <TableCell>{t("rashi_lord")}</TableCell>
                      <TableCell>{t("bhava")}</TableCell>
                      <TableCell>{t("nakshatra")}</TableCell>
                      <TableCell>{t("nak_lord")}</TableCell>
                      <TableCell>{t("dms")}</TableCell>
                      <TableCell>{t("dignity")}</TableCell>
                      <TableCell>{t("avastha")}</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {[chart.lagna, ...Object.values(chart.planets || {})].filter(Boolean).map((p: any) => (
                      <TableRow key={p.name}>
                        <TableCell>{p.glyph} {grahaName(p.name, hi)}{p.retrograde ? ` ${t("vakra")}` : ""}</TableCell>
                        <TableCell>{hi ? (p.signHi || p.signSa) : p.sign}</TableCell>
                        <TableCell>{grahaName(p.rashiLord, hi)}</TableCell>
                        <TableCell>{p.house}</TableCell>
                        <TableCell>{nakName(p.nakshatra, hi)} {p.pada}</TableCell>
                        <TableCell>{grahaName(p.nakLord, hi)}</TableCell>
                        <TableCell>{p.signDegree || p.dms}</TableCell>
                        <TableCell>{dignityName(p.dignity, hi)}</TableCell>
                        <TableCell>{hi ? (p.avasthaHi || p.avastha) : p.avastha}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
                </Box>
                {pdfBar}
              </Grid>
              {chart.interpretation && (
                <Grid item xs={12}>
                  <Card><CardContent>
                    <Typography variant="h6" color="primary">{t("reading")}</Typography>
                    <Typography>{chart.interpretation.summary}</Typography>
                    <Typography sx={{ mt: 1 }}>{chart.interpretation.personality}</Typography>
                  </CardContent></Card>
                </Grid>
              )}
            </Grid>
          )}
          {tab === 2 && (
            <Grid container spacing={2}>
              <Grid item xs={12}><Typography variant="body2" color="text.secondary">{t("chalit_help")}</Typography></Grid>
              <Grid item xs={12} md={6}>
                <Typography color="primary" sx={{ mb: 1 }}>{t("chart_d1")}</Typography>
                <KundaliChart chart={chart} style={style} />
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography color="primary" sx={{ mb: 1 }}>{t("chart_chandra")}</Typography>
                <KundaliChart chart={chartFromOrigin(chart, "Moon")} style={style} />
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography color="primary" sx={{ mb: 1 }}>{t("chart_surya")}</Typography>
                <KundaliChart chart={chartFromOrigin(chart, "Sun")} style={style} />
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography color="primary" sx={{ mb: 1 }}>{t("chart_chalit")}</Typography>
                {chalit ? <KundaliChart chart={chalit} style={style} /> : <Typography variant="body2">{t("loading")}</Typography>}
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography color="primary" sx={{ mb: 1 }}>{t("chart_gochar")}</Typography>
                {gochar ? <KundaliChart chart={gocharAsChart(chart, gochar)} style={style} /> : <Typography variant="body2">{t("loading")}</Typography>}
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography color="primary" sx={{ mb: 1 }}>D9</Typography>
                {v9Chart ? <KundaliChart chart={v9Chart} style={style} /> : <Typography variant="body2">{t("vargas_missing")}</Typography>}
              </Grid>
            </Grid>
          )}
          {tab === 3 && <DashaBoard dasha={chart.dasha} hi={hi} t={t} />}
          {tab === 4 && (
            <Box>
              <Typography variant="h6" color="primary">{t("kp_title")}</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>{t("kp_help")}</Typography>
              <Typography variant="subtitle1" color="primary" sx={{ mb: 1 }}>{t("kp_cusps")}</Typography>
              <Box sx={{ overflowX: "auto", mb: 3 }}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>{t("bhava")}</TableCell>
                      <TableCell>{t("rashi")}</TableCell>
                      <TableCell>{t("rashi_lord")}</TableCell>
                      <TableCell>{t("nakshatra")}</TableCell>
                      <TableCell>{t("star_lord")}</TableCell>
                      <TableCell>{t("sub_lord")}</TableCell>
                      <TableCell>{t("dms")}</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {(chart.kp?.cusps || []).map((r: any) => (
                      <TableRow key={r.house}>
                        <TableCell>{r.house}</TableCell>
                        <TableCell>{hi ? r.signHi : r.sign}</TableCell>
                        <TableCell>{grahaName(r.signLord, hi)}</TableCell>
                        <TableCell>{hi ? r.nakshatraHi : r.nakshatra}</TableCell>
                        <TableCell>{grahaName(r.starLord, hi)}</TableCell>
                        <TableCell>{grahaName(r.subLord, hi)}</TableCell>
                        <TableCell>{r.dms}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </Box>
              <Typography variant="subtitle1" color="primary" sx={{ mb: 1 }}>{t("kp_bodies")}</Typography>
              <Box sx={{ overflowX: "auto" }}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>{t("graha")}</TableCell>
                      <TableCell>{t("bhava")}</TableCell>
                      <TableCell>{t("rashi")}</TableCell>
                      <TableCell>{t("rashi_lord")}</TableCell>
                      <TableCell>{t("nakshatra")}</TableCell>
                      <TableCell>{t("star_lord")}</TableCell>
                      <TableCell>{t("sub_lord")}</TableCell>
                      <TableCell>{t("dms")}</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {(chart.kp?.bodies || []).map((r: any) => (
                      <TableRow key={r.name}>
                        <TableCell>{grahaName(r.name, hi)}</TableCell>
                        <TableCell>{r.house}</TableCell>
                        <TableCell>{hi ? r.signHi : r.sign}</TableCell>
                        <TableCell>{grahaName(r.signLord, hi)}</TableCell>
                        <TableCell>{hi ? r.nakshatraHi : r.nakshatra}</TableCell>
                        <TableCell>{grahaName(r.starLord, hi)}</TableCell>
                        <TableCell>{grahaName(r.subLord, hi)}</TableCell>
                        <TableCell>{r.dms}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </Box>
            </Box>
          )}
        </>
      )}
    </Box>
  );
}

export function VargasPage() {
  const { chart, loadChart } = useApp();
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const [div, setDiv] = useState(9);
  useEffect(() => { if (!chart) loadChart().catch(() => {}); }, []);
  const v = chart?.vargas?.[div];
  return (
    <Box>
      <GoldTitle sub={t("vargas_sub")}>{t("vargas_title")}</GoldTitle>
      <BirthForm />
      <Box sx={{ display: "flex", flexWrap: "wrap", gap: 1, my: 2 }}>
        {[1, 2, 3, 4, 7, 9, 10, 12, 16, 20, 24, 27, 30, 40, 45, 60].map((d) => (
          <Chip
            key={d}
            label={`D${d} · ${hi ? VARGA_MEANING[d].hi : VARGA_MEANING[d].en}`}
            onClick={() => setDiv(d)}
            color={div === d ? "primary" : "default"}
          />
        ))}
      </Box>
      {v && (
        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <KundaliChart chart={{
              lagna: { signIndex: v.bodies?.[0]?.signIndex || 0 },
              planets: Object.fromEntries((v.bodies || []).filter((b: any) => b.name !== "Lagna").map((b: any) => [b.name, b])),
            }} />
          </Grid>
          <Grid item xs={12} md={6}>
            <Typography variant="h6">{v.name}</Typography>
            {(v.bodies || []).map((b: any) => (
              <Typography key={b.name} variant="body2">{grahaName(b.name, hi)} — {hi ? (b.signHi || b.signSa) : b.sign} · {t("bhava")} {b.house} · {dignityName(b.dignity, hi)}</Typography>
            ))}
            <Typography sx={{ mt: 2 }} variant="subtitle2">{t("vimshopaka")}</Typography>
            {Object.entries(chart.vimshopaka || {}).map(([k, val]: any) => (
              <Box key={k} sx={{ mb: 0.5 }}>
                <Typography variant="caption">{grahaName(k, hi)} {val}/20</Typography>
                <LinearProgress variant="determinate" value={(Number(val) / 20) * 100} />
              </Box>
            ))}
          </Grid>
        </Grid>
      )}
      {chart?.ashtakavarga && (
        <Box sx={{ mt: 3, overflowX: "auto" }}>
          <Typography variant="h6" color="primary">{t("ashtakavarga")}</Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>{t("ashtaka_help")}</Typography>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>{t("graha")}</TableCell>
                {(chart.ashtakavarga.signs || []).map((s: any) => (
                  <TableCell key={s.signIndex} align="center">{hi ? s.signHi : s.sign}</TableCell>
                ))}
                <TableCell align="center">{t("score")}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {["Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn"].map((g) => {
                const row = chart.ashtakavarga.bav?.[g];
                if (!row) return null;
                return (
                  <TableRow key={g}>
                    <TableCell>{grahaName(g, hi)}</TableCell>
                    {(row.bySign || []).map((n: number, i: number) => (
                      <TableCell key={i} align="center" sx={{ fontWeight: n >= 4 ? 700 : 400, color: n <= 3 ? "warning.main" : "inherit" }}>{n}</TableCell>
                    ))}
                    <TableCell align="center">{row.total}</TableCell>
                  </TableRow>
                );
              })}
              <TableRow>
                <TableCell sx={{ fontWeight: 700 }}>{t("sav")}</TableCell>
                {(chart.ashtakavarga.sav?.bySign || []).map((n: number, i: number) => (
                  <TableCell key={i} align="center" sx={{ fontWeight: 700 }}>{n}</TableCell>
                ))}
                <TableCell align="center" sx={{ fontWeight: 700 }}>{chart.ashtakavarga.sav?.total}</TableCell>
              </TableRow>
            </TableBody>
          </Table>
        </Box>
      )}
      {chart?.shadbala && (
        <Box sx={{ mt: 3, overflowX: "auto" }}>
          <Typography variant="h6" color="primary">{t("shadbala")}</Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>{t("shadbala_help")}</Typography>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>{t("graha")}</TableCell>
                <TableCell align="right">{t("sthana")}</TableCell>
                <TableCell align="right">{t("dig")}</TableCell>
                <TableCell align="right">{t("kala_bala")}</TableCell>
                <TableCell align="right">{t("chesta")}</TableCell>
                <TableCell align="right">{t("naisargika")}</TableCell>
                <TableCell align="right">{t("drik_bala")}</TableCell>
                <TableCell align="right">{t("score")}</TableCell>
                <TableCell align="right">{t("rupa")}</TableCell>
                <TableCell align="right">{t("required")}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {["Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn"].map((g) => {
                const row = chart.shadbala.grahas?.[g];
                if (!row) return null;
                const full = !!row.full;
                return (
                  <TableRow key={g}>
                    <TableCell>{grahaName(g, hi)}</TableCell>
                    <TableCell align="right">{row.sthana}</TableCell>
                    <TableCell align="right">{row.dig}</TableCell>
                    <TableCell align="right">{row.kala}</TableCell>
                    <TableCell align="right">{row.chesta}</TableCell>
                    <TableCell align="right">{row.naisargika}</TableCell>
                    <TableCell align="right">{row.drik}</TableCell>
                    <TableCell align="right" sx={{ fontWeight: 700, color: full ? "success.main" : "warning.main" }}>{row.totalVirupa}</TableCell>
                    <TableCell align="right">{row.rupa}</TableCell>
                    <TableCell align="right">{row.requiredVirupa}</TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
          <Typography variant="h6" color="primary" sx={{ mt: 3 }}>{t("bhava_bala")}</Typography>
          <Table size="small" sx={{ mt: 1 }}>
            <TableHead>
              <TableRow>
                <TableCell>{t("bhava")}</TableCell>
                <TableCell>{t("rashi")}</TableCell>
                <TableCell>{t("lord")}</TableCell>
                <TableCell align="right">{t("score")}</TableCell>
                <TableCell align="right">{t("rupa")}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {(chart.shadbala.bhavas || []).map((b: any) => (
                <TableRow key={b.house}>
                  <TableCell>{b.house}</TableCell>
                  <TableCell>{hi ? b.signHi : b.sign}</TableCell>
                  <TableCell>{grahaName(b.lord, hi)}</TableCell>
                  <TableCell align="right">{b.totalVirupa}</TableCell>
                  <TableCell align="right">{b.rupa}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Box>
      )}
    </Box>
  );
}

export function PanchangPage() {
  const { birth } = useApp();
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const [data, setData] = useState<any>(null);
  const [err, setErr] = useState("");
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));
  const load = (iso?: string) => {
    const d = iso || date;
    setErr("");
    return api.get(`/api/v1/jyotish/panchang?date=${d}&lat=${birth.latitude}&lon=${birth.longitude}&ayanamsa=${birth.ayanamsa}&mode=${birth.panchangMode}`)
      .then(setData).catch((e) => setErr(e.message));
  };
  useEffect(() => { load(); }, [date, birth.latitude, birth.longitude, birth.ayanamsa, birth.panchangMode]);
  /** Printed panchang: the clock is END (until). Start is previous sandhi. */
  const limb = (start?: string, until?: string, next?: string) => (
    <>
      {until && (
        <Typography variant="subtitle1" sx={{ fontWeight: 600, mt: 0.5 }}>
          {t("until")} {until}
        </Typography>
      )}
      <Typography variant="caption" display="block" color="text.secondary">
        {start ? `${t("begins")} ${start}` : ""}
        {next ? ` · ${t("then")} ${next}` : ""}
      </Typography>
    </>
  );
  return (
    <Box>
      <PageHero title={t("panchang_title")} sub={t("panchang_sub")} />
      <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>{t("panchang_time_help")}</Typography>
      <Box sx={{ mb: 2, display: "flex", flexWrap: "wrap", alignItems: "center", gap: 1 }}>
        <ToggleButtonGroup exclusive size="small" value={birth.panchangMode}
          onChange={(_, v) => v && setBirth({ ...birth, panchangMode: v })}>
          <ToggleButton value="SIDDHANTIC">{t("mode_ss")}</ToggleButton>
          <ToggleButton value="DRIK">{t("mode_drik")}</ToggleButton>
        </ToggleButtonGroup>
        <Typography variant="caption" color="text.secondary">{t("mode_help")}</Typography>
      </Box>
      <HonestyNote mode={birth.panchangMode} />
      <GlassCard sx={{ mb: 2 }}>
        <Box sx={{ display: "flex", gap: 2, flexWrap: "wrap", alignItems: "center" }}>
          <TextField type="date" value={date} onChange={(e) => setDate(e.target.value)} />
          <Button variant="contained" onClick={() => load()}>{t("compute")}</Button>
        </Box>
      </GlassCard>
      <PanchangMonthGrid
        birth={birth}
        date={date}
        onPick={(iso, day) => {
          setDate(iso);
          if (day) setData(day);
        }}
      />
      {err && <Alert severity="error" sx={{ mb: 2 }}>{err}</Alert>}
      {data && (
        <Grid container spacing={2}>
          <Grid item xs={6} md={4}><LimbTile label={t("tithi")} value={`${hi ? data.pakshaHi : data.paksha} ${hi ? data.tithiHi : data.tithi}`} until={limb(data.tithiStart, data.tithiEnd, hi ? data.tithiNextHi : data.tithiNext)} /></Grid>
          <Grid item xs={6} md={4}><LimbTile label={t("nakshatra")} value={`${hi ? data.nakshatraHi : data.nakshatra} ${data.nakshatraPada || ""}`} until={limb(data.nakshatraStart, data.nakshatraEnd, hi ? data.nakshatraNextHi : data.nakshatraNext)} /></Grid>
          <Grid item xs={6} md={4}><LimbTile label={t("yoga")} value={hi ? data.yogaHi : data.yoga} until={limb(data.yogaStart, data.yogaEnd, hi ? data.yogaNextHi : data.yogaNext)} /></Grid>
          <Grid item xs={6} md={4}><LimbTile label={t("karana")} value={hi ? data.karanaHi : data.karana} until={limb(data.karanaStart, data.karanaEnd, hi ? data.karanaNextHi : data.karanaNext)} /></Grid>
          <Grid item xs={6} md={4}><LimbTile label={t("vara")} value={hi ? data.varaHi : data.vara} /></Grid>
          <Grid item xs={6} md={4}><LimbTile label={t("ritu")} value={hi ? data.rituHi : data.ritu} extra={<Typography variant="caption" color="text.secondary">{hi ? data.ayanaHi : data.ayana}</Typography>} /></Grid>
          <Grid item xs={12} md={6}>
            <GlassCard>
              <Typography variant="h6" color="primary" sx={{ mb: 1 }}>{t("sunrise")} / {t("sunset")}</Typography>
              <MetaRow k={t("sunrise")} v={data.sunrise} />
              <MetaRow k={t("noon")} v={data.solarNoon} />
              <MetaRow k={t("sunset")} v={data.sunset} />
            </GlassCard>
          </Grid>
          <Grid item xs={12} md={6}>
            <GlassCard>
              <Typography variant="h6" color="primary" sx={{ mb: 1 }}>{t("kala")}</Typography>
              <MetaRow k={t("rahu")} v={`${data.muhurta?.rahuKalam?.start || "—"}–${data.muhurta?.rahuKalam?.end || ""}`} />
              <MetaRow k={t("yamaganda")} v={`${data.muhurta?.yamaganda?.start || "—"}–${data.muhurta?.yamaganda?.end || ""}`} />
              <MetaRow k={t("gulika")} v={`${data.muhurta?.gulika?.start || "—"}–${data.muhurta?.gulika?.end || ""}`} />
              <MetaRow k={t("abhijit")} v={`${data.abhijit?.start || "—"}–${data.abhijit?.end || ""}`} />
              <MetaRow k={t("brahma")} v={`${data.brahmaMuhurta?.start || "—"}–${data.brahmaMuhurta?.end || ""}`} />
            </GlassCard>
          </Grid>
          <Grid item xs={12}>
            <Typography variant="body2" color="text.secondary">{data.makarandaNote}</Typography>
            {(data.akshansh || data.deshantar) && (
              <Typography variant="caption" display="block" color="text.secondary">
                {t("lat")} {data.akshansh} · {t("lon")} {data.deshantar} · {t("palabha")} {data.palabha}
                {data.placeLat != null ? ` · ${Number(data.placeLat).toFixed(4)}°N ${Number(data.placeLon).toFixed(4)}°E` : ""}
              </Typography>
            )}
          </Grid>
        </Grid>
      )}
    </Box>
  );
}

export function MilanPage() {
  const { t } = useI18n();
  const [boy, setBoy] = useState(defaultBirth());
  const [girl, setGirl] = useState({ ...defaultBirth(), name: "" });
  const [res, setRes] = useState<any>(null);
  const [err, setErr] = useState("");
  const run = () => {
    setErr("");
    return api.post("/api/v1/jyotish/match", { boy, girl }).then(setRes).catch((e) => setErr(e.message));
  };
  return (
    <Box>
      <PageHero title={t("milan_title")} sub={t("milan_sub")} />
      <Grid container spacing={2}>
        <Grid item xs={12} md={6}>
          <GlassCard>
            <Typography variant="h6" color="primary" sx={{ mb: 1 }}>{t("var")}</Typography>
            <TextField fullWidth sx={{ my: 1 }} type="datetime-local" value={boy.dateTime} onChange={(e) => setBoy({ ...boy, dateTime: e.target.value })} />
            <TextField fullWidth label={t("place")} value={boy.place} onChange={(e) => setBoy({ ...boy, place: e.target.value })} />
          </GlassCard>
        </Grid>
        <Grid item xs={12} md={6}>
          <GlassCard>
            <Typography variant="h6" color="primary" sx={{ mb: 1 }}>{t("vadhu")}</Typography>
            <TextField fullWidth sx={{ my: 1 }} type="datetime-local" value={girl.dateTime} onChange={(e) => setGirl({ ...girl, dateTime: e.target.value })} />
            <TextField fullWidth label={t("place")} value={girl.place} onChange={(e) => setGirl({ ...girl, place: e.target.value })} />
          </GlassCard>
        </Grid>
      </Grid>
      <Button sx={{ my: 2 }} variant="contained" onClick={run}>{t("match")}</Button>
      {err && <Alert severity="error" sx={{ mb: 2 }}>{err}</Alert>}
      {res && (
        <GlassCard>
          <ScoreHero score={Number(res.total) || 0} max={36} label={res.verdict} />
          <Table size="small" sx={{ mt: 2 }}>
            <TableBody>
              {(res.kootas || []).map((k: any) => (
                <TableRow key={k.name}><TableCell>{k.name}</TableCell><TableCell>{k.score}/{k.max}</TableCell><TableCell>{k.meaning}</TableCell></TableRow>
              ))}
            </TableBody>
          </Table>
          <Alert sx={{ mt: 2 }} severity={res.mangalDosha?.cancelled ? "success" : res.mangalDosha?.boyManglik || res.mangalDosha?.girlManglik ? "warning" : "info"}>
            {res.mangalDosha?.note}
          </Alert>
        </GlassCard>
      )}
    </Box>
  );
}

export function DashaPage() {
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const { chart, loadChart } = useApp();
  useEffect(() => { if (!chart) loadChart().catch(() => {}); }, []);
  return (
    <Box>
      <GoldTitle sub={t("dasha_sub")}>{t("dasha_title")}</GoldTitle>
      <BirthForm />
      {chart?.dasha && <DashaBoard dasha={chart.dasha} hi={hi} t={t} />}
    </Box>
  );
}

export function HoroscopePage() {
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const [data, setData] = useState<any>(null);
  useEffect(() => { api.get("/api/v1/jyotish/horoscope").then(setData); }, []);
  return (
    <Box>
      <PageHero title={t("horoscope_title")} sub={t("horoscope_sub")} />
      <Grid container spacing={2}>
        {(data?.rashis || []).map((r: any) => (
          <Grid item xs={12} md={4} key={r.sign}>
            <Card><CardContent>
              <Typography variant="h6" color="primary">{hi ? r.hindi : (r.sign || r.sanskrit)}</Typography>
              <Typography variant="caption">{hi ? r.hindi : r.sign} · {t("lucky_colour")} {hi ? (r.luckyColourHi || r.luckyColour) : r.luckyColour} · {t("lucky_no")} {r.luckyNumber}</Typography>
              <Typography sx={{ mt: 1 }} variant="body2">{hi ? (r.predictionHi || r.prediction) : r.prediction}</Typography>
            </CardContent></Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}

export function YogasPage() {
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const { chart, loadChart } = useApp();
  useEffect(() => { if (!chart) loadChart().catch(() => {}); }, []);
  const y = chart?.yogas || {};
  return (
    <Box>
      <PageHero title={t("yogas_title")} sub={t("yogas_sub")} />
      <BirthForm />
      <Grid container spacing={2} sx={{ mt: 1 }}>
        <Grid item xs={12} md={6}>
          {(y.yogas || []).map((g: any) => (
            <Card key={g.name} sx={{ mb: 1 }}><CardContent>
              <Chip size="small" color="success" label={yogaType(g.type, hi)} />
              <Typography variant="h6">{yogaName(g.name, hi)}</Typography>
              <Typography variant="body2">{yogaText(g.name, g.text, hi)}</Typography>
            </CardContent></Card>
          ))}
        </Grid>
        <Grid item xs={12} md={6}>
          {(y.doshas || []).map((g: any) => (
            <Card key={g.name} sx={{ mb: 1 }}><CardContent>
              <Chip size="small" color="warning" label={yogaType(g.type, hi)} />
              <Typography variant="h6">{yogaName(g.name, hi)}</Typography>
              <Typography variant="body2">{yogaText(g.name, g.text, hi)}</Typography>
            </CardContent></Card>
          ))}
        </Grid>
      </Grid>
    </Box>
  );
}

export function NakshatraPage() {
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const [enc, setEnc] = useState<any>(null);
  useEffect(() => { api.get("/api/v1/learn/encyclopedia").then(setEnc); }, []);
  return (
    <Box>
      <PageHero title={t("nak_title")} sub={t("nak_sub")} />
      <Grid container spacing={2}>
        {(enc?.nakshatras || []).map((n: any) => (
          <Grid item xs={12} md={4} key={n.name}>
            <Card><CardContent>
              <Typography variant="h6" color="primary">{n.index}. {hi ? (n.nameHi || n.name) : n.name}</Typography>
              <Typography variant="body2">{t("lord")} {hi ? (n.lordHi || grahaName(n.lord, true)) : n.lord} · {t("deity")} {hi ? (n.deityHi || n.deity) : n.deity}</Typography>
              <Typography variant="caption">{hi ? (n.ganaHi || n.gana) : n.gana} · {hi ? (n.yoniHi || n.yoni) : n.yoni} · {hi ? (n.nadiHi || n.nadi) : n.nadi} · {n.span}</Typography>
              <Typography sx={{ mt: 1 }} variant="body2">{hi ? (n.characterHi || n.character) : n.character}</Typography>
            </CardContent></Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}

export function RashiPage() {
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const [enc, setEnc] = useState<any>(null);
  useEffect(() => { api.get("/api/v1/learn/encyclopedia").then(setEnc); }, []);
  return (
    <Box>
      <PageHero title={t("rashi_title")} />
      <Grid container spacing={2}>
        {(enc?.rashis || []).map((r: any) => (
          <Grid item xs={12} md={4} key={r.name}>
            <Card><CardContent>
              <Typography variant="h6" color="primary">{hi ? r.hindi : (r.name || r.sanskrit)}</Typography>
              <Typography variant="body2">{hi ? (r.lordHi || grahaName(r.lord, true)) : r.lord} · {hi ? (r.elementHi || r.element) : r.element} · {hi ? (r.qualityHi || r.quality) : r.quality} · {hi ? (r.bodyHi || r.body) : r.body}</Typography>
              <Typography sx={{ mt: 1 }}>{hi ? (r.natureHi || r.nature) : r.nature}</Typography>
            </CardContent></Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}

export function MuhurtaPage() {
  const { t, lang } = useI18n();
  const [purpose, setPurpose] = useState("MARRIAGE");
  const [rows, setRows] = useState<any[]>([]);
  const [meta, setMeta] = useState<any>(null);
  const [fromPlace, setFromPlace] = useState("Darbhanga, Bihar, India");
  const [fromLat, setFromLat] = useState(26.1542);
  const [fromLon, setFromLon] = useState(85.8918);
  const [toPlace, setToPlace] = useState("");
  const [toLat, setToLat] = useState<number | null>(null);
  const [toLon, setToLon] = useState<number | null>(null);
  const [direction, setDirection] = useState("");
  const run = async () => {
    if (purpose === "TRAVEL") {
      const body: any = {
        purpose: "TRAVEL", days: 21, fromPlace, fromLat, fromLon, direction: direction || undefined,
      };
      if (toLat != null && toLon != null) {
        body.toPlace = toPlace;
        body.toLat = toLat;
        body.toLon = toLon;
      }
      const r = await api.post("/api/v1/jyotish/muhurta", body);
      setMeta(r);
      setRows(r.days || []);
    } else {
      setMeta(null);
      setRows(await api.get("/api/v1/jyotish/muhurta?purpose=" + purpose + "&days=40"));
    }
  };
  useEffect(() => { run(); }, []);
  const dirs = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"];
  return (
    <Box>
      <PageHero title={t("muhurta_title")} sub={t("muhurta_sub")} />
      <Box sx={{ display: "flex", flexWrap: "wrap", gap: 1, mb: 2 }}>
        {["MARRIAGE", "BUSINESS", "TRAVEL", "PROPERTY", "EDUCATION", "NAMAKARANA", "GENERAL"].map((p) => (
          <Chip key={p} label={t(p)} color={purpose === p ? "primary" : "default"} onClick={() => setPurpose(p)} />
        ))}
      </Box>
      {purpose === "TRAVEL" && (
        <Box sx={{ mb: 2 }}>
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <PlaceSearch label={t("travel_from")} value={fromPlace} onChange={(p, la, lo) => { setFromPlace(p); setFromLat(la); setFromLon(lo); }} />
            </Grid>
            <Grid item xs={12} md={6}>
              <PlaceSearch label={t("travel_to")} value={toPlace} onChange={(p, la, lo) => { setToPlace(p); setToLat(la); setToLon(lo); setDirection(""); }} />
            </Grid>
          </Grid>
          <Typography variant="body2" sx={{ mt: 2, mb: 1 }}>{t("travel_or")}</Typography>
          <Box sx={{ display: "flex", flexWrap: "wrap", gap: 1 }}>
            {dirs.map((d) => (
              <Chip key={d} label={t("dir_" + d)} color={direction === d ? "primary" : "default"}
                onClick={() => { setDirection(d); setToLat(null); setToLon(null); setToPlace(""); }} />
            ))}
          </Box>
          <Typography variant="caption" display="block" sx={{ mt: 1 }}>{t("travel_help")}</Typography>
        </Box>
      )}
      <Button variant="contained" onClick={() => run().catch((e) => alert(e.message))}>{t("find_days")}</Button>
      {meta?.directionHi && (
        <Alert sx={{ mt: 2 }}>{t("direction")}: {lang === "hi" ? (meta.directionHi || meta.direction) : (meta.direction || meta.directionHi)}
          {meta.bearingDeg != null ? ` · ${meta.bearingDeg}°` : ""} — {meta.fromPlace} → {meta.toPlace || meta.directionHi}</Alert>
      )}
      <Card sx={{ mt: 2 }}>
        <Table size="small">
          <TableHead><TableRow>
            <TableCell>{t("date")}</TableCell><TableCell>{t("grade")}</TableCell>
            <TableCell>{t("tithi")}</TableCell><TableCell>{t("nakshatra")}</TableCell>
            <TableCell>{t("score")}</TableCell>
            {purpose === "TRAVEL" && <TableCell>{t("note")}</TableCell>}
          </TableRow></TableHead>
          <TableBody>
            {rows.map((r) => (
              <TableRow key={r.date} sx={{ opacity: r.dishaShool ? 0.55 : 1 }}>
                <TableCell>{r.date}</TableCell>
                <TableCell>{r.grade}{r.dishaShool ? " ⚠" : ""}</TableCell>
                <TableCell>{r.tithi}{r.tithiEnd ? ` · ${t("until")} ${r.tithiEnd}` : ""}</TableCell>
                <TableCell>{r.nakshatra}{r.nakshatraEnd ? ` · ${t("until")} ${r.nakshatraEnd}` : ""}</TableCell>
                <TableCell>{r.score}</TableCell>
                {purpose === "TRAVEL" && <TableCell>{r.reason}</TableCell>}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Card>
    </Box>
  );
}

export function GocharPage() {
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const { birth } = useApp();
  const [data, setData] = useState<any>(null);
  const run = () => api.post("/api/v1/jyotish/gochar", birth).then(setData);
  useEffect(() => { run(); }, []);
  return (
    <Box>
      <PageHero title={t("gochar_title")} sub={t("gochar_sub")} />
      <BirthForm onSubmit={run} submitLabel={t("compute")} />
      {data && (
        <>
          <Alert sx={{ my: 2 }} severity={data.sadeSati?.active ? "warning" : "success"}>{hi ? (data.sadeSati?.phaseHi || data.sadeSati?.phase) : data.sadeSati?.phase} — {hi ? (data.sadeSati?.remedyHi || data.sadeSati?.remedy) : data.sadeSati?.remedy}</Alert>
          <Alert severity={data.jupiter?.auspicious ? "success" : "info"}>{hi ? (data.jupiter?.noteHi || data.jupiter?.note) : data.jupiter?.note}</Alert>
          <Card sx={{ mt: 2 }}>
            <Table size="small">
              <TableHead><TableRow>
                <TableCell>{t("graha")}</TableCell><TableCell>{t("rashi")}</TableCell><TableCell>{t("from_moon")}</TableCell><TableCell>{t("from_lagna")}</TableCell><TableCell>{t("effect")}</TableCell>
              </TableRow></TableHead>
              <TableBody>
                {(data.planets || []).map((p: any) => (
                  <TableRow key={p.planet}>
                    <TableCell>{grahaName(p.planet, hi)}{p.retrograde ? (hi ? " वक्र" : " R") : ""}</TableCell>
                    <TableCell>{typeof p.signIndex === "number" ? rashiName(p.signIndex, hi) : (hi ? nakName(p.sign, hi) : p.sign)}</TableCell>
                    <TableCell>{p.houseFromMoon}</TableCell>
                    <TableCell>{p.houseFromLagna}</TableCell>
                    <TableCell>{hi ? (p.effectHi || p.effect) : p.effect}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Card>
        </>
      )}
    </Box>
  );
}

export function GemsPage() {
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const { chart, loadChart } = useApp();
  useEffect(() => { if (!chart) loadChart().catch(() => {}); }, []);
  const g = chart?.gemstones;
  return (
    <Box>
      <PageHero title={t("gems_title")} />
      <BirthForm />
      {g && (
        <Grid container spacing={2} sx={{ mt: 1 }}>
          <Grid item xs={12} md={4}><LimbTile label={t("life_stone")} value={gemPhrase(g.lifeStone, hi)} extra={<Typography variant="caption">{t("lagnesh")} {grahaName(g.lagnesh, hi)}</Typography>} /></Grid>
          <Grid item xs={12} md={4}><LimbTile label={t("support_stone")} value={gemPhrase(g.luckyStone, hi)} extra={<Typography variant="caption">{grahaName(g.weakestPlanet, hi)} · {g.weakestScore}</Typography>} /></Grid>
          <Grid item xs={12} md={4}><LimbTile label={t("metal_colour")} value={`${gemPhrase(g.metal, hi)} · ${gemPhrase(g.colour, hi)}`} /></Grid>
          {g.rudraksha && (
            <Grid item xs={12} md={6}>
              <LimbTile
                label={t("rudraksha")}
                value={hi ? (g.rudraksha.nameHi || g.rudraksha.name) : g.rudraksha.name}
                extra={
                  <Typography variant="caption" display="block">
                    {t("nakshatra")} {hi ? g.rudraksha.nakshatraHi : g.rudraksha.nakshatra}
                    {" · "}{t("lord")} {grahaName(g.rudraksha.lord, hi)}
                    {" · "}{hi ? g.rudraksha.deityHi : g.rudraksha.deity}
                    {" · "}{hi ? g.rudraksha.mantraHi : g.rudraksha.mantra}
                    <br />{hi ? g.rudraksha.noteHi : g.rudraksha.note}
                  </Typography>
                }
              />
            </Grid>
          )}
          <Grid item xs={12}><Alert severity="warning">{gemPhrase(g.warning, hi)}</Alert></Grid>
        </Grid>
      )}
    </Box>
  );
}

export function VarshaPage() {
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const { birth } = useApp();
  const [year, setYear] = useState(new Date().getFullYear());
  const [data, setData] = useState<any>(null);
  const run = () => api.post("/api/v1/jyotish/varshaphal?year=" + year, birth).then(setData);
  return (
    <Box>
      <PageHero title={t("varsha_title")} sub={t("varsha_sub")} />
      <BirthForm onSubmit={run} submitLabel={t("cast_return")} />
      <TextField type="number" label={t("year")} value={year} onChange={(e) => setYear(Number(e.target.value))} sx={{ ml: 2, mt: 2 }} />
      {data && (
        <Card sx={{ mt: 2 }}><CardContent>
          <Typography>{t("return_time")}: {data.solarReturnTime}</Typography>
          <Typography>{t("varsha_lagna")}: {data.varshaLagna}</Typography>
          <Typography>{t("muntha")}: {data.muntha}</Typography>
          <Typography>{hi ? (data.yearThemeHi || data.yearTheme) : data.yearTheme}</Typography>
          <Box sx={{ mt: 2 }}><KundaliChart chart={data.chart} /></Box>
        </CardContent></Card>
      )}
    </Box>
  );
}

export function PrashnaPage() {
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const { birth, setBirth } = useApp();
  const [q, setQ] = useState("");
  const [data, setData] = useState<any>(null);
  const now = () => {
    const d = defaultBirth();
    setBirth({ ...birth, dateTime: d.dateTime });
  };
  const run = () => api.post("/api/v1/jyotish/prashna?question=" + encodeURIComponent(q), birth).then(setData);
  return (
    <Box>
      <PageHero title={t("prashna_title")} sub={t("prashna_sub")} />
      <TextField fullWidth label={t("question")} value={q} onChange={(e) => setQ(e.target.value)}
        placeholder={hi ? "क्या यह कार्य सिद्ध होगा?" : "Will this work succeed?"} sx={{ mb: 2 }} />
      <Button onClick={now} sx={{ mr: 1 }}>{t("use_now")}</Button>
      <Button variant="contained" onClick={run}>{t("cast_prashna")}</Button>
      {data && (
        <Card sx={{ mt: 2 }}><CardContent>
          <Typography variant="h6">{prashnaVerdict(data.verdict, hi)}</Typography>
          <Typography>{t("lagna")} {data.lagna} · {t("moon_in_house")} {data.moonHouse}</Typography>
          <Typography variant="body2">{hi
            ? "चन्द्र प्रश्न का कारक; लग्न प्रश्नकर्ता; सप्तम दूसरा पक्ष। मिथिला प्रश्न में ज्योतिषी की श्वास (वाम/दक्षिण नाड़ी) भी टिप्पणी में लिखें।"
            : data.tajikaNote}</Typography>
          <Box sx={{ mt: 2 }}><KundaliChart chart={data.chart} style="EAST" /></Box>
        </CardContent></Card>
      )}
    </Box>
  );
}

export function LearnPage() {
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const [tab, setTab] = useState(0);
  const [enc, setEnc] = useState<any>({});
  const [arts, setArts] = useState<any[]>([]);
  useEffect(() => {
    api.get("/api/v1/learn/encyclopedia").then(setEnc);
    api.get("/api/v1/learn/articles").then(setArts);
  }, []);
  const keys = ["houses", "planets", "rashis", "nakshatras"];
  const list = enc[keys[tab]] || [];
  const heading = (item: any) => {
    if (tab === 0) return `${t("house_n")} ${item.number} · ${hi ? (item.titleHi || item.title) : item.title}`;
    if (tab === 1) return hi ? (item.hindi || item.sanskrit || item.name) : (item.name || item.sanskrit);
    if (tab === 2) return hi ? (item.hindi || item.sanskrit) : (item.name || item.sanskrit);
    return hi ? (item.nameHi || item.name) : item.name;
  };
  const body = (item: any) => {
    if (tab === 0) return hi ? (item.textHi || item.text) : item.text;
    if (tab === 1) return hi ? (item.karakaHi || item.karaka) : item.karaka;
    if (tab === 2) return hi ? (item.natureHi || item.nature) : item.nature;
    return hi ? (item.characterHi || item.character) : item.character;
  };
  return (
    <Box>
      <PageHero title={t("learn_title")} sub={t("learn_sub")} />
      <Tabs value={tab} onChange={(_, v) => setTab(v)} variant="scrollable">
        <Tab label={t("tab_bhavas")} /><Tab label={t("tab_grahas")} /><Tab label={t("tab_rashis")} /><Tab label={t("tab_naks")} />
      </Tabs>
      <Grid container spacing={2} sx={{ mt: 1 }}>
        {list.map((item: any, i: number) => (
          <Grid item xs={12} md={6} key={i}>
            <Card><CardContent>
              <Typography variant="h6" color="primary">{heading(item)}</Typography>
              <Typography variant="body2">{body(item)}</Typography>
            </CardContent></Card>
          </Grid>
        ))}
      </Grid>
      <Typography variant="h5" color="primary" sx={{ mt: 4 }}>{t("articles")}</Typography>
      {arts.map((a) => (
        <Card key={a.id} sx={{ mt: 1 }}><CardContent>
          <Chip size="small" label={a.category} />
          <Typography variant="h6">{articleTitle(a, hi)}</Typography>
          <Typography>{articleBody(a, hi)}</Typography>
        </CardContent></Card>
      ))}
    </Box>
  );
}

export function ConsultPage() {
  const { t, lang } = useI18n();
  const hi = lang !== "en";
  const { user } = useApp();
  const [astros, setAstros] = useState<any[]>([]);
  const [mine, setMine] = useState<any[]>([]);
  const [topic, setTopic] = useState("");
  useEffect(() => {
    api.get("/api/v1/public/astrologers").then(setAstros);
    if (user) api.get("/api/v1/consult/mine").then(setMine).catch(() => {});
  }, [user]);
  const book = async (id: number) => {
    if (!user) { alert(t("login_first_user")); return; }
    const c = await api.post("/api/v1/consult/book", { astrologerId: id, topic, mode: "VIDEO" });
    await api.post(`/api/v1/consult/${c.id}/pay`, { paymentRef: "UPI-DEMO" });
    setMine(await api.get("/api/v1/consult/mine"));
  };
  const bio = (a: any) => hi ? (a.bioHi || a.bio) : (a.bioEn || a.bio);
  return (
    <Box>
      <PageHero title={t("consult_title")} sub={t("consult_sub")} />
      <TextField fullWidth label={t("topic")} value={topic} onChange={(e) => setTopic(e.target.value)}
        placeholder={hi ? "कर्म व विवाह" : "Career and marriage"} sx={{ mb: 2 }} />
      <Grid container spacing={2}>
        {astros.map((a) => (
          <Grid item xs={12} md={6} key={a.id}>
            <Card><CardContent>
              <Typography variant="h6">{a.name}</Typography>
              <Typography variant="body2">{bio(a)}</Typography>
              <Typography sx={{ my: 1 }}>₹{a.consultationFeeInr} · {a.experienceYears} {t("yrs")} · ★ {a.rating}</Typography>
              <Button variant="contained" onClick={() => book(a.id)}>{t("book_pay")}</Button>
            </CardContent></Card>
          </Grid>
        ))}
      </Grid>
      <Typography variant="h5" sx={{ mt: 3 }} color="primary">{t("my_sessions")}</Typography>
      {mine.map((c) => (
        <Card key={c.id} sx={{ mt: 1 }}><CardContent>
          <Typography>{c.topic} · {c.status} · {c.slotStart}</Typography>
          <Button href={c.meetUrl} target="_blank">{t("join_video")}</Button>
        </CardContent></Card>
      ))}
    </Box>
  );
}

export function CrmPage() {
  const { t } = useI18n();
  const { user, birth } = useApp();
  const [rows, setRows] = useState<any[]>([]);
  const [name, setName] = useState("");
  const load = () => api.get("/api/v1/crm/clients").then(setRows);
  useEffect(() => { if (user) load().catch(() => {}); }, [user]);
  if (!user) return <Alert severity="info">{t("login_astro")}</Alert>;
  return (
    <Box>
      <PageHero title={t("crm_title")} sub={t("crm_sub")} />
      <Box sx={{ display: "flex", gap: 1, mb: 2 }}>
        <TextField label={t("client_name")} value={name} onChange={(e) => setName(e.target.value)} />
        <Button variant="contained" onClick={async () => {
          await api.post("/api/v1/crm/clients", { name, birthDateTime: birth.dateTime, place: birth.place, latitude: birth.latitude, longitude: birth.longitude, notes: t("add") });
          setName(""); load();
        }}>{t("add")}</Button>
      </Box>
      {rows.map((c) => (
        <Card key={c.id} sx={{ mb: 1 }}><CardContent>
          <Typography variant="h6">{c.name} · {c.place}</Typography>
          <Typography variant="body2">{c.birthDateTime} · {c.notes}</Typography>
        </CardContent></Card>
      ))}
    </Box>
  );
}

export function AdminPage() {
  const { t } = useI18n();
  const { user } = useApp();
  const [dash, setDash] = useState<any>(null);
  useEffect(() => { if (user) api.get("/api/v1/admin/dashboard").then(setDash).catch(() => {}); }, [user]);
  if (!user) return <Alert>{t("login_admin")}</Alert>;
  if (!dash) return <Alert severity="info">{t("admin_loads")}</Alert>;
  return (
    <Box>
      <PageHero title={t("admin_title")} />
      <Grid container spacing={2}>
        {[[t("users"), dash.users], [t("astrologers"), dash.astrologers], [t("consultations"), dash.consultations], [t("articles"), dash.articles]].map(([k, v]) => (
          <Grid item xs={6} md={3} key={k as string}><Card><CardContent><Typography color="primary">{k}</Typography><Typography variant="h4">{v}</Typography></CardContent></Card></Grid>
        ))}
      </Grid>
    </Box>
  );
}

export function LoginPage() {
  const { login } = useApp();
  const { t } = useI18n();
  const [email, setEmail] = useState("user@makaranda.app");
  const [password, setPassword] = useState("user123");
  const [err, setErr] = useState("");
  return (
    <Box sx={{ maxWidth: 440, mx: "auto", mt: 4 }}>
      <GlassCard>
        <PageHero title={t("signin")} sub={t("demo_accounts")} />
        <TextField fullWidth label={t("email")} value={email} onChange={(e) => setEmail(e.target.value)} sx={{ mb: 2 }} />
        <TextField fullWidth type="password" label={t("password")} value={password} onChange={(e) => setPassword(e.target.value)} sx={{ mb: 2 }} />
        {err && <Alert severity="error" sx={{ mb: 2 }}>{err}</Alert>}
        <Button fullWidth variant="contained" onClick={() => login(email, password).catch((e) => setErr(e.message))}>{t("enter")}</Button>
        <Typography variant="body2" sx={{ mt: 2 }} color="text.secondary">user123 · astro123 · admin123</Typography>
      </GlassCard>
    </Box>
  );
}

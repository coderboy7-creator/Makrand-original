import React from "react";
import { useI18n } from "../i18n";
import { grahaShort, rashiShort } from "../jyotishLabels";

function occupants(chart: any, hi: boolean): { houses: Record<number, string[]>; lagnaSign: number } {
  const houses: Record<number, string[]> = {};
  for (let i = 1; i <= 12; i++) houses[i] = [];
  if (!chart) return { houses, lagnaSign: 0 };
  const lagnaSign = chart.lagna?.signIndex ?? 0;
  if (chart.markLagnaInHouse1 !== false) {
    houses[1].push(grahaShort("Lagna", hi));
  }
  if (typeof chart.birthLagnaSignIndex === "number") {
    const h = ((chart.birthLagnaSignIndex - lagnaSign + 12) % 12) + 1;
    houses[h].push(grahaShort("Lagna", hi));
  }
  const rows = Array.isArray(chart.planets) ? chart.planets : Object.values(chart.planets || {});
  rows.forEach((p: any) => {
    const name = p?.name || p?.planet || "";
    if (!name) return;
    const h = Number(p.house ?? p.houseFromLagna ?? 0);
    const house = h >= 1 && h <= 12 ? h : 1;
    houses[house].push(grahaShort(name, hi) + (p.retrograde ? (hi ? "व" : "R") : ""));
  });
  return { houses, lagnaSign };
}

const FONT = '"Noto Sans Devanagari","Nirmala UI","Mangal",system-ui,sans-serif';

export default function KundaliChart({ chart, style = "NORTH", size = 420 }: { chart: any; style?: string; size?: number }) {
  const { lang } = useI18n();
  const hi = lang !== "en";
  if (style === "SOUTH") return <South chart={chart} size={size} hi={hi} />;
  if (style === "EAST") return <East chart={chart} size={size} hi={hi} />;
  return <North chart={chart} size={size} hi={hi} />;
}

function North({ chart, size, hi }: { chart: any; size: number; hi: boolean }) {
  const { houses, lagnaSign } = occupants(chart, hi);
  const s = size;
  const m = 8;
  const pos: Record<number, { x: number; y: number }> = {
    1: { x: s / 2, y: s * 0.22 },
    2: { x: s * 0.28, y: s * 0.12 },
    3: { x: s * 0.12, y: s * 0.28 },
    4: { x: s * 0.22, y: s / 2 },
    5: { x: s * 0.12, y: s * 0.72 },
    6: { x: s * 0.28, y: s * 0.88 },
    7: { x: s / 2, y: s * 0.78 },
    8: { x: s * 0.72, y: s * 0.88 },
    9: { x: s * 0.88, y: s * 0.72 },
    10: { x: s * 0.78, y: s / 2 },
    11: { x: s * 0.88, y: s * 0.28 },
    12: { x: s * 0.72, y: s * 0.12 },
  };
  return (
    <svg width="100%" viewBox={`0 0 ${s} ${s}`} style={{ maxWidth: s, background: "transparent" }}>
      <defs>
        <linearGradient id="goldStrokeN" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0%" stopColor="#F6E27A" /><stop offset="100%" stopColor="#C47A12" />
        </linearGradient>
      </defs>
      <rect x={m} y={m} width={s - 2 * m} height={s - 2 * m} rx="10" fill="#05040A" stroke="url(#goldStrokeN)" strokeWidth="2.4" />
      <line x1={m} y1={m} x2={s - m} y2={s - m} stroke="#E8C547" strokeWidth="1.15" />
      <line x1={s - m} y1={m} x2={m} y2={s - m} stroke="#E8C547" strokeWidth="1.15" />
      <line x1={s / 2} y1={m} x2={m} y2={s / 2} stroke="#E8C547" strokeWidth="1.15" />
      <line x1={s / 2} y1={m} x2={s - m} y2={s / 2} stroke="#E8C547" strokeWidth="1.15" />
      <line x1={s / 2} y1={s - m} x2={m} y2={s / 2} stroke="#E8C547" strokeWidth="1.15" />
      <line x1={s / 2} y1={s - m} x2={s - m} y2={s / 2} stroke="#E8C547" strokeWidth="1.15" />
      {Object.entries(pos).map(([h, p]) => (
        <g key={h}>
          <text x={p.x} y={p.y - 14} textAnchor="middle" fill="#E07A2F" fontSize="12" fontFamily={FONT}>
            {rashiShort(lagnaSign + Number(h) - 1, hi)}
          </text>
          <text x={p.x} y={p.y + 4} textAnchor="middle" fill="#F7F1E3" fontSize="13" fontFamily={FONT} fontWeight="700">
            {(houses[Number(h)] || []).join(" ")}
          </text>
        </g>
      ))}
    </svg>
  );
}

function South({ chart, size, hi }: { chart: any; size: number; hi: boolean }) {
  const { houses, lagnaSign } = occupants(chart, hi);
  const cells: { sign: number; col: number; row: number }[] = [
    { sign: 11, col: 0, row: 0 }, { sign: 0, col: 1, row: 0 }, { sign: 1, col: 2, row: 0 }, { sign: 2, col: 3, row: 0 },
    { sign: 10, col: 0, row: 1 }, { sign: 3, col: 3, row: 1 },
    { sign: 9, col: 0, row: 2 }, { sign: 4, col: 3, row: 2 },
    { sign: 8, col: 0, row: 3 }, { sign: 7, col: 1, row: 3 }, { sign: 6, col: 2, row: 3 }, { sign: 5, col: 3, row: 3 },
  ];
  const cell = size / 4;
  const houseOfSign = (sign: number) => ((sign - lagnaSign + 12) % 12) + 1;
  return (
    <svg width="100%" viewBox={`0 0 ${size} ${size}`} style={{ maxWidth: size, background: "transparent" }}>
      <rect x="1" y="1" width={size - 2} height={size - 2} rx="10" fill="#05040A" stroke="#E8C547" strokeWidth="2.2" />
      {[1, 2, 3].map((i) => (
        <g key={i}>
          <line x1={i * cell} y1={0} x2={i * cell} y2={size} stroke="#E8C547" strokeWidth="1" />
          <line x1={0} y1={i * cell} x2={size} y2={i * cell} stroke="#E8C547" strokeWidth="1" />
        </g>
      ))}
      {cells.map((c) => {
        const h = houseOfSign(c.sign);
        const isLagna = h === 1;
        return (
          <g key={c.sign}>
            <text x={c.col * cell + 8} y={c.row * cell + 16} fill="#E07A2F" fontSize="12" fontFamily={FONT}>
              {rashiShort(c.sign, hi)}{isLagna ? (hi ? " लग्न" : " Asc") : ""}
            </text>
            <text x={c.col * cell + cell / 2} y={c.row * cell + cell / 2 + 4} textAnchor="middle" fill="#F7F1E3" fontSize="13" fontFamily={FONT} fontWeight="700">
              {(houses[h] || []).join(" ")}
            </text>
          </g>
        );
      })}
    </svg>
  );
}

function East({ chart, size, hi }: { chart: any; size: number; hi: boolean }) {
  const { houses, lagnaSign } = occupants(chart, hi);
  const cell = size / 4;
  return (
    <svg width="100%" viewBox={`0 0 ${size} ${size}`} style={{ maxWidth: size, background: "transparent" }}>
      <rect x="2" y="2" width={size - 4} height={size - 4} rx="10" fill="#05040A" stroke="#E8C547" strokeWidth="2.2" />
      <line x1={cell} y1={0} x2={cell} y2={size} stroke="#E8C547" />
      <line x1={3 * cell} y1={0} x2={3 * cell} y2={size} stroke="#E8C547" />
      <line x1={0} y1={cell} x2={size} y2={cell} stroke="#E8C547" />
      <line x1={0} y1={3 * cell} x2={size} y2={3 * cell} stroke="#E8C547" />
      <line x1={cell} y1={cell} x2={3 * cell} y2={3 * cell} stroke="#E8C547" />
      <line x1={3 * cell} y1={cell} x2={cell} y2={3 * cell} stroke="#E8C547" />
      {[
        { h: 1, x: size / 2, y: cell * 0.55 },
        { h: 2, x: cell * 3.5, y: cell * 0.55 },
        { h: 3, x: cell * 3.5, y: cell * 1.5 },
        { h: 4, x: cell * 3.5, y: cell * 2.5 },
        { h: 5, x: cell * 3.5, y: cell * 3.5 },
        { h: 6, x: size / 2, y: cell * 3.5 },
        { h: 7, x: cell * 0.5, y: cell * 3.5 },
        { h: 8, x: cell * 0.5, y: cell * 2.5 },
        { h: 9, x: cell * 0.5, y: cell * 1.5 },
        { h: 10, x: cell * 0.5, y: cell * 0.55 },
        { h: 11, x: cell * 1.5, y: cell * 1.7 },
        { h: 12, x: cell * 2.5, y: cell * 1.7 },
      ].map((p) => (
        <g key={p.h}>
          <text x={p.x} y={p.y - 12} textAnchor="middle" fill="#E07A2F" fontSize="12" fontFamily={FONT}>
            {rashiShort(lagnaSign + p.h - 1, hi)}
          </text>
          <text x={p.x} y={p.y + 6} textAnchor="middle" fill="#F7F1E3" fontSize="12" fontWeight="700" fontFamily={FONT}>
            {(houses[p.h] || []).join(" ")}
          </text>
        </g>
      ))}
      <text x={size / 2} y={size / 2 + 6} textAnchor="middle" fill="#C9A227" fontSize="12" fontFamily={FONT}>
        {hi ? "मिथिला" : "Mithila"}
      </text>
    </svg>
  );
}

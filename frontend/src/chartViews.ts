/** UI-only remaps. Does not change engine houses or bijas. */

export function houseFrom(originSign: number, bodySign: number): number {
  return ((bodySign - originSign + 12) % 12) + 1;
}

/** Chandra / Sūrya kuṇḍalī: same rashis, bhāvas counted from Moon or Sun. */
export function chartFromOrigin(natal: any, originKey: "Moon" | "Sun") {
  const origin = natal?.planets?.[originKey];
  const originSign = origin?.signIndex ?? 0;
  const birthLagna = natal?.lagna?.signIndex ?? 0;
  const planets: Record<string, any> = {};
  Object.entries(natal?.planets || {}).forEach(([k, p]: [string, any]) => {
    planets[k] = { ...p, house: houseFrom(originSign, p.signIndex ?? 0) };
  });
  return {
    lagna: { ...(natal?.lagna || {}), signIndex: originSign, house: 1 },
    planets,
    birthLagnaSignIndex: birthLagna,
    markLagnaInHouse1: false,
  };
}

export function gocharAsChart(natal: any, gochar: any) {
  const planets: Record<string, any> = {};
  (gochar?.planets || []).forEach((p: any) => {
    planets[p.planet] = {
      name: p.planet,
      house: p.houseFromLagna || 1,
      retrograde: p.retrograde,
      signIndex: p.signIndex,
    };
  });
  return {
    lagna: natal?.lagna,
    planets,
    markLagnaInHouse1: true,
  };
}

export const VARGA_MEANING: Record<number, { hi: string; en: string }> = {
  1: { hi: "लग्न — देह", en: "Rasi — body" },
  2: { hi: "होरा — धन", en: "Hora — wealth" },
  3: { hi: "द्रेष्काण — सहोदर", en: "Drekkana — siblings" },
  4: { hi: "चतुर्थांश — संपत्ति", en: "Chaturthamsa — property" },
  7: { hi: "सप्तांश — संतान", en: "Saptamsa — children" },
  9: { hi: "नवमांश — धर्म / दारा", en: "Navamsa — dharma / spouse" },
  10: { hi: "दशमांश — कर्म", en: "Dasamsa — career" },
  12: { hi: "द्वादशांश — माता-पिता", en: "Dwadasamsa — parents" },
  16: { hi: "षोडशांश — सुख / यान", en: "Shodasamsa — comforts" },
  20: { hi: "विंशांश — उपासना", en: "Vimsamsa — upasana" },
  24: { hi: "सिद्धांश — विद्या", en: "Siddhamsa — learning" },
  27: { hi: "भांश — बल", en: "Bhamsa — strength" },
  30: { hi: "त्रिंशांश — अरिष्ट", en: "Trimsamsa — evils" },
  40: { hi: "खवेदांश — मातृसुख", en: "Khavedamsa — maternal" },
  45: { hi: "अक्षवेदांश — चरित्र", en: "Akshavedamsa — character" },
  60: { hi: "षष्ट्यांश — कर्मशेष", en: "Shashtiamsa — past karma" },
};

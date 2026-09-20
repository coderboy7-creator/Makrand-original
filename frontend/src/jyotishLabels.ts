export const GRAHA_HI: Record<string, string> = {
  Sun: "सूर्य", Moon: "चन्द्र", Mars: "मंगल", Mercury: "बुध", Jupiter: "गुरु",
  Venus: "शुक्र", Saturn: "शनि", Rahu: "राहु", Ketu: "केतु", Lagna: "लग्न",
};
export const GRAHA_SHORT_HI: Record<string, string> = {
  Sun: "सू", Moon: "चं", Mars: "मं", Mercury: "बु", Jupiter: "गु",
  Venus: "शु", Saturn: "शनि", Rahu: "रा", Ketu: "के", Lagna: "ल",
};
export const GRAHA_SHORT_EN: Record<string, string> = {
  Sun: "Su", Moon: "Mo", Mars: "Ma", Mercury: "Me", Jupiter: "Ju",
  Venus: "Ve", Saturn: "Sa", Rahu: "Ra", Ketu: "Ke", Lagna: "ल",
};
export const RASHI_HI = ["मेष", "वृषभ", "मिथुन", "कर्क", "सिंह", "कन्या", "तुला", "वृश्चिक", "धनु", "मकर", "कुम्भ", "मीन"];
export const RASHI_SHORT_HI = ["मेष", "वृष", "मिथु", "कर्क", "सिंह", "कन्या", "तुला", "वृश्च", "धनु", "मकर", "कुम्भ", "मीन"];
export const RASHI_SHORT_EN = ["Ar", "Ta", "Ge", "Cn", "Le", "Vi", "Li", "Sc", "Sg", "Cp", "Aq", "Pi"];
export const NAK_HI: Record<string, string> = {
  "Ashwini": "अश्विनी", "Bharani": "भरणी", "Krittika": "कृत्तिका", "Rohini": "रोहिणी",
  "Mrigashira": "मृगशिरा", "Ardra": "आर्द्रा", "Punarvasu": "पुनर्वसु", "Pushya": "पुष्य",
  "Ashlesha": "आश्लेषा", "Magha": "मघा", "Purva Phalguni": "पूर्वा फाल्गुनी", "Uttara Phalguni": "उत्तरा फाल्गुनी",
  "Hasta": "हस्त", "Chitra": "चित्रा", "Swati": "स्वाती", "Vishakha": "विशाखा",
  "Anuradha": "अनुराधा", "Jyeshtha": "ज्येष्ठा", "Mula": "मूल", "Purva Ashadha": "पूर्वाषाढ़ा",
  "Uttara Ashadha": "उत्तराषाढ़ा", "Shravana": "श्रवण", "Dhanishta": "धनिष्ठा", "Shatabhisha": "शतभिषा",
  "Purva Bhadrapada": "पूर्वा भाद्रपद", "Uttara Bhadrapada": "उत्तरा भाद्रपद", "Revati": "रेवती",
};
export const DIGNITY_HI: Record<string, string> = {
  Exalted: "उच्च", Debilitated: "नीच", "Own Sign": "स्वगृह", Moolatrikona: "मूलत्रिकोण",
  Friendly: "मित्र", Enemy: "शत्रु", Neutral: "सम", "—": "—",
};
export const GANA_HI: Record<string, string> = { Deva: "देव", Manushya: "मनुष्य", Rakshasa: "राक्षस" };
export const NADI_HI: Record<string, string> = { Adi: "आदि", Madhya: "मध्य", Antya: "अन्त्य" };
export const YONI_HI: Record<string, string> = {
  Horse: "अश्व", Elephant: "गज", Sheep: "मेष", Serpent: "सर्प", Dog: "श्वान", Cat: "मार्जार",
  Rat: "मूषक", Cow: "गौ", Buffalo: "महिष", Tiger: "व्याघ्र", Deer: "मृग", Monkey: "वानर",
  Mongoose: "नकुल", Lion: "सिंह",
};

export function grahaName(name: string, hi: boolean) {
  if (!name) return "";
  return hi ? (GRAHA_HI[name] || name) : name;
}
export function grahaShort(name: string, hi: boolean) {
  const m = hi ? GRAHA_SHORT_HI : GRAHA_SHORT_EN;
  return m[name] || name;
}
export function rashiShort(i: number, hi: boolean) {
  const arr = hi ? RASHI_SHORT_HI : RASHI_SHORT_EN;
  return arr[((i % 12) + 12) % 12];
}
export function rashiName(i: number, hi: boolean) {
  return hi ? RASHI_HI[((i % 12) + 12) % 12] : ["Aries","Taurus","Gemini","Cancer","Leo","Virgo","Libra","Scorpio","Sagittarius","Capricorn","Aquarius","Pisces"][((i%12)+12)%12];
}
export function nakName(n: string, hi: boolean) {
  if (!n) return "";
  return hi ? (NAK_HI[n] || n) : n;
}
export function dignityName(d: string, hi: boolean) {
  if (!d) return "";
  return hi ? (DIGNITY_HI[d] || d) : d;
}

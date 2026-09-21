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

const GEM_HI: Record<string, string> = {
  "Ruby (Manikya)": "माणिक्य",
  "Pearl (Moti)": "मोती",
  "Red Coral (Moonga)": "मूँगा",
  "Emerald (Panna)": "पन्ना",
  "Yellow Sapphire (Pukhraj)": "पुखराज",
  "Diamond (Heera) / White Sapphire": "हीरा / श्वेत नीलम",
  "Blue Sapphire (Neelam)": "नीलम",
  "Hessonite (Gomed)": "गोमेद",
  "Cat's Eye (Lehsunia)": "लहसुनिया",
  Gold: "स्वर्ण", Silver: "रजत", Copper: "ताम्र", Bronze: "कांस्य",
  Iron: "लोह", Lead: "सीसा",
  White: "श्वेत", Red: "रक्त", Green: "हरित", Yellow: "पीत",
  "Copper-red / Orange": "ताम्र-रक्त / नारंगी",
  "White / Pastel": "श्वेत / मृदु",
  "Blue / Black": "नील / कृष्ण",
  "Smoke / Brown": "धूम्र / भूरा",
  "Grey / Flag": "धूसर",
};

export function gemPhrase(s: string, hi: boolean) {
  if (!hi || !s) return s;
  let out = s;
  Object.entries(GEM_HI).sort((a, b) => b[0].length - a[0].length).forEach(([en, h]) => {
    out = out.split(en).join(h);
  });
  return out
    .replace(" — supports the lagna lord.", " — लग्नेश का रत्न।")
    .replace(" — supports the weakest of Shodashavarga vimshopaka.", " — षोडशवर्ग विंशोपक में दुर्बल ग्रह का रत्न।")
    .replace("Never prescribe Neelam (Saturn) or Gomed (Rahu) without seeing dasha, lagna and current gochar. Prefer mantra and dana first. We do not sell gems or rudraksha.",
      "दशा, लग्न और वर्तमान गोचर देखे बिना नीलम (शनि) या गोमेद (राहु) न दें। पहले मन्त्र और दान श्रेयस्कर हैं। हम रत्न या रुद्राक्ष नहीं बेचते।")
    .replace("Never prescribe Neelam (Saturn) or Gomed (Rahu) without seeing dasha, lagna and current gochar. Prefer mantra and dana first.",
      "दशा, लग्न और वर्तमान गोचर देखे बिना नीलम (शनि) या गोमेद (राहु) न दें। पहले मन्त्र और दान श्रेयस्कर हैं।");
}

const YOGA_NAME_HI: Record<string, string> = {
  "Gajakesari Yoga": "गजकेसरी योग",
  "Budhaditya Yoga": "बुधादित्य योग",
  "Malavya Yoga (Panch Mahapurusha)": "मालव्य योग (पञ्च महापुरुष)",
  "Ruchaka Yoga (Panch Mahapurusha)": "रुचक योग (पञ्च महापुरुष)",
  "Bhadra Yoga (Panch Mahapurusha)": "भद्र योग (पञ्च महापुरुष)",
  "Hamsa Yoga (Panch Mahapurusha)": "हंस योग (पञ्च महापुरुष)",
  "Sasa Yoga (Panch Mahapurusha)": "शश योग (पञ्च महापुरुष)",
  "Kemadruma Yoga": "केमद्रुम योग",
  "Adhi Yoga": "अधि योग",
  "Dharma-Karmadhipati Yoga": "धर्म-कर्माधिपति योग",
  "Dhana Yoga": "धन योग",
  "Lakshmi Yoga (partial)": "लक्ष्मी योग (आंशिक)",
  "Chandra-Mangala Yoga": "चन्द्र-मंगल योग",
  "Viparita Raja Yoga": "विपरीत राजयोग",
  "Amala Yoga": "अमल योग",
  "Shubha Kartari Yoga": "शुभ कर्तरी योग",
  "Papa Kartari Yoga": "पाप कर्तरी योग",
  "Mangal / Kuja Dosha": "मंगल / कुज दोष",
  "Pitra Dosha (indicative)": "पितृ दोष (संकेत)",
  "Surya Grahan Yoga": "सूर्य ग्रहण योग",
  "Chandra Grahan Yoga": "चन्द्र ग्रहण योग",
};
const YOGA_TEXT_HI: Record<string, string> = {
  "Gajakesari Yoga": "चन्द्र से केन्द्र में गुरु। प्रज्ञा, यश और स्थिरता।",
  "Budhaditya Yoga": "सूर्य-बुध युति। बुद्धि, वाणी और कौशल।",
  "Malavya Yoga (Panch Mahapurusha)": "केन्द्र में बलवान शुक्र। विलास, वाहन, कला।",
  "Ruchaka Yoga (Panch Mahapurusha)": "केन्द्र में बलवान मंगल। साहस, आज्ञा, भूमि-बल।",
  "Bhadra Yoga (Panch Mahapurusha)": "केन्द्र में बलवान बुध। बुद्धि और वाणिज्य।",
  "Hamsa Yoga (Panch Mahapurusha)": "केन्द्र में बलवान गुरु। धर्म, अनुग्रह, उच्च पद।",
  "Sasa Yoga (Panch Mahapurusha)": "केन्द्र में बलवान शनि। अधिकार, आयु, उद्योग।",
  "Kemadruma Yoga": "चन्द्र से २/१२ में ग्रह नहीं। एकाकीपन — चन्द्र-केन्द्र से भंग संभव।",
  "Adhi Yoga": "चन्द्र से ६/७/८ में शुभ। नेतृत्व और सुख।",
  "Dharma-Karmadhipati Yoga": "९वें और १०वें स्वामी जुड़े। धर्म से उत्थान।",
  "Dhana Yoga": "धन भाव जुड़े। संचय की क्षमता।",
  "Lakshmi Yoga (partial)": "९वें स्वामी सुस्थित। भाग्य और अनुग्रह।",
  "Chandra-Mangala Yoga": "चन्द्र-मंगल युति। उद्यम और द्रव्य, भाव-ताप सहित।",
  "Viparita Raja Yoga": "दुःस्थान स्वामी दुःस्थान में। विपत्ति के बाद उदय।",
  "Amala Yoga": "लग्न या चन्द्र से १०वें में शुभ। निर्मल यश।",
  "Shubha Kartari Yoga": "लग्न को शुभ घेरे। रक्षा और सरलता।",
  "Papa Kartari Yoga": "लग्न को पाप घेरे। दबाव और विघ्न।",
  "Mangal / Kuja Dosha": "मंगल मंगलिक भाव में। मिलान से पहले भंग नियम तौलें।",
  "Pitra Dosha (indicative)": "सूर्य पर ग्रहण/९वें पर दबाव। पितृ कर्म; श्राद्ध व सूर्य उपाय।",
  "Surya Grahan Yoga": "सूर्य-पाप ग्रहण। आत्मा और पिता के विषय।",
  "Chandra Grahan Yoga": "चन्द्र-पाप ग्रहण। मन और माता के विषय।",
};
const TYPE_HI: Record<string, string> = {
  Raja: "राज", Dosha: "दोष", Dhana: "धन", Shubha: "शुभ", "Dhana / Buddhi": "धन / बुद्धि",
};

export function yogaName(name: string, hi: boolean) {
  if (!name) return "";
  if (!hi) return name;
  if (YOGA_NAME_HI[name]) return YOGA_NAME_HI[name];
  if (name.startsWith("Neecha Bhanga")) {
    const p = name.split("—")[1]?.trim() || "";
    return "नीच भंग राजयोग" + (p ? " — " + grahaName(p, true) : "");
  }
  if (name.startsWith("Kaal Sarp")) {
    const p = name.split("—")[1]?.trim() || "";
    return "कालसर्प दोष" + (p ? " — " + p : "");
  }
  return name;
}
export function yogaText(name: string, text: string, hi: boolean) {
  if (!hi) return text;
  if (YOGA_TEXT_HI[name]) return YOGA_TEXT_HI[name];
  if (name.startsWith("Neecha Bhanga")) return "ग्रह नीच है पर भंग लागू। पतन उदय बनता है।";
  if (name.startsWith("Kaal Sarp")) return "सभी ग्रह राहु-केतु के बीच। तीव्रता, विलम्ब, भीतरी दबाव; असामान्य उदय भी।";
  if (name.startsWith("Mangal")) return text.replace("Mars occupies a manglik house", "मंगल मंगलिक भाव में है");
  return text;
}
export function yogaType(type: string, hi: boolean) {
  if (!hi || !type) return type;
  return TYPE_HI[type] || type;
}

export function prashnaVerdict(v: string, hi: boolean) {
  if (!hi || !v) return v;
  if (v.startsWith("Shubha")) return "शुभ — प्रश्न पूर्ति की ओर झुकता है।";
  if (v.startsWith("Kashta")) return "कष्ट — विलम्ब या 'नहीं जब तक उपाय न हो'। शुभ तिथि पर पुनः पूछें।";
  if (v.startsWith("Mishra")) return "मिश्र — मिश्रित; चन्द्र के अगले दृष्टि और प्रश्न की दशा पर काल निर्भर।";
  return v;
}

const ARTICLE_HI: Record<string, { title: string; body: string }> = {
  "bhava-chakra": {
    title: "बारह भाव",
    body: "प्रत्येक भाव कालापुरुष की एक भूमिका है। केन्द्र (१,४,७,१०) स्तम्भ हैं; त्रिकोण (१,५,९) लक्ष्मी-स्थान; दुःस्थान (६,८,१२) कर्म को घर्षण से पकाते हैं। मिथिला पद्धति में लग्न पहले पूर्वी हीरक में, फिर नवांश, फिर चन्द्र कुंडली पढ़ी जाती है।",
  },
  "nava-graha": {
    title: "नव ग्रह",
    body: "ग्रह = पकड़ने वाला। सूर्य आत्मा, चन्द्र मन, मंगल ऊर्जा, बुध बुद्धि, गुरु धर्म, शुक्र रस, शनि काल, राहु भविष्य की भूख, केतु अतीत की भस्म।",
  },
  "pancha-mahapurusha": {
    title: "पञ्च महापुरुष योग",
    body: "जब मंगल, बुध, गुरु, शुक्र या शनि केन्द्र में स्व/उच्च हों तो महापुरुष योग जन्म लेता है: रुचक, भद्र, हंस, मालव्य, शश — पाँच शास्त्रीय महत्ता।",
  },
  "makaranda-paddhati": {
    title: "मिथिला का मकरन्द पंचांग",
    body: "मकरन्द १५वीं शताब्दी का करण ग्रन्थ है, सूर्य सिद्धान्त पर आधारित, मैथिल पञ्जीकारों द्वारा प्रयुक्त। यह मंच सूर्य सिद्धान्त (मकरन्द) अयनांश और सिद्धान्तिक गणित को मूल रखता है, तुलना के लिए दृक् स्पष्ट का एक-क्लिक विकल्प सहित।",
  },
  "ashtakoota": {
    title: "अष्टकूट मिलान",
    body: "आठ कूट, कुल ३६ गुण। नाड़ी (८) और भकूट (७) सबसे भारी। गुण मिलान आवश्यक पर पर्याप्त नहीं — मंगल दोष, दशा सन्धि, शुक्र/गुरु का नवांश साथ पढ़ें; मैथिल परिवार गोत्र और ग्राम भी तौलते हैं।",
  },
};

const ASTRO_BIO_HI: Record<string, string> = {
  "Acharya Sumanth Mishra": "मैथिल ब्राह्मण। कुंडली, मिलान, मुहूर्त। दरभंगा व मधुबनी परम्परा।",
  "Acharya Kavita Jha": "प्रश्न, स्त्री जातक, रत्न परामर्श। मैथिली, हिन्दी, अंग्रेज़ी में परामर्श।",
  "Pandit Rajesh Jha": "मंच संरक्षक। मिथिला ज्योतिष, मकरन्द पद्धति।",
};
export function astroBio(name: string, bio: string, hi: boolean) {
  if (!hi) return bio;
  return ASTRO_BIO_HI[name] || bio;
}

export function articleTitle(a: any, hi: boolean) {
  if (hi && a?.slug && ARTICLE_HI[a.slug]) return ARTICLE_HI[a.slug].title;
  return a?.title || "";
}
export function articleBody(a: any, hi: boolean) {
  if (hi && a?.slug && ARTICLE_HI[a.slug]) return ARTICLE_HI[a.slug].body;
  return a?.body || "";
}

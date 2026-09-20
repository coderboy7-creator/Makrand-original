const TOKEN_KEY = "makaranda.token";

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || "";
}
export function setToken(t: string) {
  if (t) localStorage.setItem(TOKEN_KEY, t);
  else localStorage.removeItem(TOKEN_KEY);
}

async function req(path: string, init: RequestInit = {}) {
  const headers: any = { "Content-Type": "application/json", ...(init.headers || {}) };
  const tok = getToken();
  if (tok) headers.Authorization = "Bearer " + tok;
  let res: Response;
  try {
    res = await fetch(path, { ...init, headers });
  } catch {
    throw new Error("API unreachable — start Spring Boot on port 8080");
  }
  if (res.status === 401) {
    setToken("");
  }
  const ct = res.headers.get("content-type") || "";
  if (ct.includes("application/pdf")) return res;
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.error || data.message || res.statusText || `HTTP ${res.status}`);
  return data;
}

export const api = {
  get: (p: string) => req(p),
  post: (p: string, body?: any) => req(p, { method: "POST", body: JSON.stringify(body || {}) }),
  del: (p: string) => req(p, { method: "DELETE" }),
};

export function defaultBirth() {
  const now = new Date();
  const pad = (n: number) => String(n).padStart(2, "0");
  const dateTime = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}:00`;
  return {
    dateTime,
    timeZone: "Asia/Kolkata",
    tzOffsetHours: 5.5,
    latitude: 26.5833,
    longitude: 85.268,
    place: "Darbhanga, Bihar, India (KSDS अक्षांश २६।३५)",
    ayanamsa: "SURYA_SIDDHANTA_MAKARANDA",
    panchangMode: "SIDDHANTIC",
    houseSystem: "WHOLE_SIGN",
    name: "",
    gender: "",
  };
}

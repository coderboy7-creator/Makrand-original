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

/** POST a birth payload and return a PDF blob. Throws if the API is down or returns JSON error. */
export async function fetchPdf(path: string, body: any): Promise<Blob> {
  const headers: any = { "Content-Type": "application/json" };
  const tok = getToken();
  if (tok) headers.Authorization = "Bearer " + tok;
  let res: Response;
  const payload = { ...(body || {}) };
  if (typeof payload.dateTime === "string" && /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(payload.dateTime)) {
    payload.dateTime = payload.dateTime + ":00";
  }
  try {
    res = await fetch(path, { method: "POST", headers, body: JSON.stringify(payload) });
  } catch {
    throw new Error("API unreachable — start Spring Boot on port 8080");
  }
  const ct = res.headers.get("content-type") || "";
  if (!res.ok || !ct.toLowerCase().includes("pdf")) {
    const data = await res.json().catch(() => ({} as any));
    throw new Error(data.error || data.message || `PDF HTTP ${res.status}`);
  }
  return res.blob();
}

export function triggerDownload(blob: Blob, filename: string): string {
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  a.rel = "noopener";
  a.style.display = "none";
  document.body.appendChild(a);
  a.click();
  a.remove();
  return url;
}

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

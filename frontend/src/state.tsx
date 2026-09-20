import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import { api, defaultBirth, getToken, setToken } from "./api";

type Birth = ReturnType<typeof defaultBirth>;

type Ctx = {
  birth: Birth;
  setBirth: (b: Birth | ((p: Birth) => Birth)) => void;
  config: any;
  user: any;
  setUser: (u: any) => void;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  chart: any;
  loadChart: () => Promise<any>;
  loading: boolean;
  error: string;
};

const C = createContext<Ctx>(null as any);

export function AppStateProvider({ children }: { children: React.ReactNode }) {
  const [birth, setBirth] = useState<Birth>(defaultBirth());
  const [config, setConfig] = useState<any>({});
  const [user, setUser] = useState<any>(null);
  const [chart, setChart] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    api.get("/api/v1/public/config").then(setConfig).catch(() => {});
    if (getToken()) api.get("/api/v1/auth/me").then(setUser).catch(() => setToken(""));
  }, []);

  const loadChart = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await api.post("/api/v1/jyotish/kundali", birth);
      setChart(data);
      return data;
    } catch (e: any) {
      setError(e.message);
      throw e;
    } finally {
      setLoading(false);
    }
  };

  const login = async (email: string, password: string) => {
    const r = await api.post("/api/v1/auth/login", { email, password });
    setToken(r.token);
    setUser(r.user);
  };
  const logout = () => {
    setToken("");
    setUser(null);
  };

  const value = useMemo(
    () => ({ birth, setBirth, config, user, setUser, login, logout, chart, loadChart, loading, error }),
    [birth, config, user, chart, loading, error]
  );
  return <C.Provider value={value}>{children}</C.Provider>;
}

export const useApp = () => useContext(C);

import React, { useCallback, useEffect, useRef, useState } from "react";
import { api, setInMemoryToken } from "../services/api";
import {
  AuthContext,
  LoginCredentials,
  LoginResponse,
  User,
} from "./auth-context";

interface JwtPayload {
  sub: string;
  username?: string;
  role?: "ADMIN" | "USER";
}

// Helper decode JWT
const decodeJwt = (token: string): JwtPayload | null => {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join(""),
    );
    return JSON.parse(jsonPayload);
  } catch {
    return null;
  }
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const initStartedRef = useRef(false);

  const logout = useCallback(() => {
    void api.post("/auth/logout").catch(() => undefined);
    localStorage.removeItem("workspace_id");
    setInMemoryToken(null);
    setUser(null);
  }, []);

  const initAuth = useCallback(async () => {
    try {
      // Access token không được persist; khi app khởi động sẽ dùng refresh cookie để xin token mới.
      const response = await api.post<LoginResponse>("/auth/refresh");

      setInMemoryToken(response.accessToken);

      const payload = decodeJwt(response.accessToken);
      if (payload) {
        setUser({
          id: parseInt(payload.sub, 10),
          username: payload.username || "User",
          role: payload.role || "USER",
        });
      }
    } catch (error) {
      console.warn("Auto login failed. Session expired.", error);
      setInMemoryToken(null);
      setUser(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!initStartedRef.current) {
      initStartedRef.current = true;
      void initAuth();
    }

    // Lắng nghe event logout từ api client
    const handleLogoutEvent = () => logout();
    window.addEventListener("auth-logout", handleLogoutEvent);
    return () => window.removeEventListener("auth-logout", handleLogoutEvent);
  }, [initAuth, logout]);

  const login = useCallback(async (credentials: LoginCredentials) => {
    const response = await api.post<LoginResponse>("/auth/login", credentials);

    // Access token chỉ lưu trên RAM; refresh token nằm trong HttpOnly cookie của backend.
    setInMemoryToken(response.accessToken);

    const payload = decodeJwt(response.accessToken);
    if (payload) {
      setUser({
          id: parseInt(payload.sub, 10),
          username: payload.username || "User",
          role: payload.role || "USER",
      });
    }
    return response;
  }, []);

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

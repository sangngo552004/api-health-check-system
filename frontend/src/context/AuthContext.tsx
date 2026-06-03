import React, { useCallback, useEffect, useState } from "react";
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
  roles?: string[];
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

  const logout = useCallback(() => {
    localStorage.removeItem("refresh_token");
    localStorage.removeItem("workspace_id");
    setInMemoryToken(null);
    setUser(null);
  }, []);

  const initAuth = useCallback(async () => {
    const refreshToken = localStorage.getItem("refresh_token");
    if (!refreshToken) {
      setLoading(false);
      return;
    }

    try {
      // Dùng Refresh Token để đổi lấy Access Token mới đẩy vào Memory
      const response = await api.post<LoginResponse>("/auth/refresh", {
        refreshToken,
      });

      setInMemoryToken(response.accessToken);
      localStorage.setItem("refresh_token", response.refreshToken); // Cuộn token nếu có

      const payload = decodeJwt(response.accessToken);
      if (payload) {
        setUser({
          id: parseInt(payload.sub, 10),
          username: payload.username || "User",
          role: payload.roles?.[0] || "MEMBER",
        });
      }
    } catch (error) {
      console.warn("Auto login failed. Session expired.", error);
      localStorage.removeItem("refresh_token");
      setInMemoryToken(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    initAuth();

    // Lắng nghe event logout từ api client
    const handleLogoutEvent = () => logout();
    window.addEventListener("auth-logout", handleLogoutEvent);
    return () => window.removeEventListener("auth-logout", handleLogoutEvent);
  }, [initAuth, logout]);

  const login = useCallback(async (credentials: LoginCredentials) => {
    const response = await api.post<LoginResponse>("/auth/login", credentials);

    // Bảo mật: Access Token lưu trên RAM, Refresh Token lưu LocalStorage
    setInMemoryToken(response.accessToken);
    localStorage.setItem("refresh_token", response.refreshToken);

    const payload = decodeJwt(response.accessToken);
    if (payload) {
      setUser({
        id: parseInt(payload.sub, 10),
        username: payload.username || "User",
        role: payload.roles?.[0] || "MEMBER",
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

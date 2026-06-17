import { createContext } from "react";

export interface User {
  id: number;
  username: string;
  role: "SUPER_ADMIN" | "USER";
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string | null;
  role: "SUPER_ADMIN" | "USER";
  requiresPasswordChange: boolean;
}

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (credentials: LoginCredentials) => Promise<LoginResponse>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(
  undefined,
);

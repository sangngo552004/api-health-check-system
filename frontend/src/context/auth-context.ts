import { createContext } from "react";

export type AppRole = "ADMIN" | "USER";

export interface User {
  id: number;
  username: string;
  role: AppRole;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string | null;
  role: AppRole;
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

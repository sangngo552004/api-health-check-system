import { createContext } from "react";

export type ToastVariant = "success" | "error" | "info";

export type ToastOptions = {
  title: string;
  description?: string;
  duration?: number;
  variant?: ToastVariant;
};

export type ToastContextType = {
  showToast: (options: ToastOptions) => void;
};

export const ToastContext = createContext<ToastContextType | undefined>(
  undefined,
);

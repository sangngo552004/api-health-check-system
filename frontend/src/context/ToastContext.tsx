import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { CheckCircle2, Info, X, XCircle } from "lucide-react";
import {
  ToastContext,
  ToastOptions,
  ToastVariant,
} from "./toast-context";

type ToastItem = Required<ToastOptions> & {
  id: number;
};

const variantStyles: Record<
  ToastVariant,
  {
    accent: string;
    background: string;
    icon: React.ReactNode;
  }
> = {
  success: {
    accent: "var(--success-color)",
    background: "rgba(16, 185, 129, 0.14)",
    icon: <CheckCircle2 size={18} />,
  },
  error: {
    accent: "var(--error-color)",
    background: "rgba(239, 68, 68, 0.14)",
    icon: <XCircle size={18} />,
  },
  info: {
    accent: "var(--accent-color)",
    background: "rgba(56, 189, 248, 0.14)",
    icon: <Info size={18} />,
  },
};

export const ToastProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [toasts, setToasts] = useState<ToastItem[]>([]);
  const timeoutRefs = useRef<Map<number, number>>(new Map());
  const nextIdRef = useRef(1);

  const dismissToast = useCallback((id: number) => {
    const timeout = timeoutRefs.current.get(id);
    if (timeout) {
      window.clearTimeout(timeout);
      timeoutRefs.current.delete(id);
    }
    setToasts((current) => current.filter((toast) => toast.id !== id));
  }, []);

  const showToast = useCallback(
    ({
      title,
      description,
      duration = 3200,
      variant = "info",
    }: ToastOptions) => {
      const id = nextIdRef.current++;

      setToasts((current) => [
        ...current,
        { id, title, description: description ?? "", duration, variant },
      ]);

      const timeout = window.setTimeout(() => {
        dismissToast(id);
      }, duration);
      timeoutRefs.current.set(id, timeout);
    },
    [dismissToast],
  );

  const contextValue = useMemo(
    () => ({
      showToast,
    }),
    [showToast],
  );

  useEffect(() => {
    return () => {
      timeoutRefs.current.forEach((timeout) => window.clearTimeout(timeout));
      timeoutRefs.current.clear();
    };
  }, []);

  return (
    <ToastContext.Provider value={contextValue}>
      {children}
      <div
        style={{
          position: "fixed",
          top: "20px",
          right: "20px",
          zIndex: 1000,
          display: "grid",
          gap: "12px",
          width: "min(360px, calc(100vw - 32px))",
          pointerEvents: "none",
        }}
      >
        {toasts.map((toast) => {
          const style = variantStyles[toast.variant];
          return (
            <div
              key={toast.id}
              style={{
                pointerEvents: "auto",
                display: "grid",
                gridTemplateColumns: "auto 1fr auto",
                gap: "12px",
                alignItems: "start",
                padding: "14px 16px",
                borderRadius: "18px",
                border: `1px solid ${style.accent}`,
                background: "var(--card-bg)",
                boxShadow: "var(--card-shadow)",
                backdropFilter: "blur(14px)",
                animation: "fadeIn 0.25s ease-out",
              }}
            >
              <div
                style={{
                  color: style.accent,
                  background: style.background,
                  borderRadius: "999px",
                  width: "32px",
                  height: "32px",
                  display: "grid",
                  placeItems: "center",
                }}
              >
                {style.icon}
              </div>
              <div style={{ minWidth: 0 }}>
                <div
                  style={{
                    color: "var(--text-primary)",
                    fontWeight: 700,
                    fontSize: "0.95rem",
                  }}
                >
                  {toast.title}
                </div>
                {toast.description && (
                  <div
                    style={{
                      color: "var(--text-secondary)",
                      fontSize: "0.85rem",
                      marginTop: "4px",
                    }}
                  >
                    {toast.description}
                  </div>
                )}
              </div>
              <button
                type="button"
                onClick={() => dismissToast(toast.id)}
                aria-label="Đóng thông báo"
                style={{
                  border: "none",
                  background: "transparent",
                  color: "var(--text-muted)",
                  cursor: "pointer",
                  padding: 0,
                  display: "grid",
                  placeItems: "center",
                }}
              >
                <X size={16} />
              </button>
            </div>
          );
        })}
      </div>
    </ToastContext.Provider>
  );
};

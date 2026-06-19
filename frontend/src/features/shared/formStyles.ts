import type React from "react";

export const formOverlayStyle: React.CSSProperties = {
  position: "fixed",
  inset: 0,
  background:
    "linear-gradient(180deg, rgba(15,23,42,0.72), rgba(15,23,42,0.58))",
  backdropFilter: "blur(8px)",
  display: "flex",
  alignItems: "flex-start",
  justifyContent: "center",
  padding: "24px",
  zIndex: 100,
  overflowY: "auto",
};

export const formModalStyle: React.CSSProperties = {
  width: "100%",
  maxHeight: "90vh",
  overflowY: "auto",
  position: "relative",
  padding: "32px",
};

export const formCloseButtonStyle: React.CSSProperties = {
  position: "absolute",
  top: "22px",
  right: "22px",
  background: "transparent",
  border: "none",
  color: "var(--text-muted)",
  cursor: "pointer",
};

export const formTitleStyle: React.CSSProperties = {
  fontSize: "1.5rem",
  fontWeight: 700,
  margin: "0 0 24px 0",
  color: "var(--text-primary)",
};

export const formLabelStyle: React.CSSProperties = {
  display: "block",
  fontSize: "0.85rem",
  fontWeight: 600,
  color: "var(--text-secondary)",
  marginBottom: "8px",
};

export const formInputStyle = (
  hasError = false,
): React.CSSProperties => ({
  width: "100%",
  padding: "12px 14px",
  background: "var(--bg-secondary)",
  border: `1px solid ${
    hasError ? "var(--error-color)" : "var(--card-border)"
  }`,
  borderRadius: "12px",
  color: "var(--text-primary)",
  outline: "none",
});

export const formTextareaStyle = (
  hasError = false,
): React.CSSProperties => ({
  ...formInputStyle(hasError),
  minHeight: "104px",
  resize: "vertical",
});

export const formErrorStyle: React.CSSProperties = {
  color: "var(--error-color)",
  fontSize: "0.75rem",
  marginTop: "4px",
  display: "flex",
  alignItems: "center",
  gap: "4px",
};

export const formActionsStyle: React.CSSProperties = {
  display: "flex",
  gap: "16px",
  marginTop: "16px",
  justifyContent: "flex-end",
  flexWrap: "wrap",
};

export const formSecondaryButtonStyle: React.CSSProperties = {
  padding: "12px 24px",
  background: "transparent",
  border: "1px solid var(--card-border)",
  color: "var(--text-primary)",
  borderRadius: "12px",
  fontWeight: 600,
  cursor: "pointer",
};

export const formPrimaryButtonStyle: React.CSSProperties = {
  display: "inline-flex",
  alignItems: "center",
  gap: "8px",
  padding: "12px 24px",
  background: "linear-gradient(135deg, #f97316, #fb7185)",
  border: "none",
  color: "#fff",
  borderRadius: "12px",
  fontWeight: 700,
  cursor: "pointer",
};

export const formTwoColumnGridStyle: React.CSSProperties = {
  display: "grid",
  gridTemplateColumns: "repeat(2, minmax(0, 1fr))",
  gap: "20px",
};

export const formCheckboxRowStyle: React.CSSProperties = {
  display: "flex",
  alignItems: "center",
  gap: "12px",
  marginTop: "8px",
};

import React from "react";

export const inputStyle: React.CSSProperties = {
  width: "100%",
  padding: "12px 14px",
  borderRadius: "12px",
  border: "1px solid var(--card-border)",
  background: "var(--bg-secondary)",
  color: "var(--text-primary)",
  fontFamily: "inherit",
  fontSize: "0.95rem",
};

export const primaryButton: React.CSSProperties = {
  display: "inline-flex",
  alignItems: "center",
  gap: "8px",
  padding: "12px 18px",
  borderRadius: "12px",
  border: "none",
  background: "linear-gradient(135deg, #f97316, #fb7185)",
  color: "#fff",
  fontWeight: 700,
  fontFamily: "inherit",
  fontSize: "0.95rem",
  cursor: "pointer",
};

export const secondaryButton: React.CSSProperties = {
  display: "inline-flex",
  alignItems: "center",
  gap: "8px",
  padding: "12px 16px",
  borderRadius: "12px",
  border: "1px solid var(--card-border)",
  background: "var(--bg-secondary)",
  color: "var(--text-primary)",
  fontFamily: "inherit",
  fontSize: "0.95rem",
  cursor: "pointer",
};

export const iconButton: React.CSSProperties = {
  display: "inline-flex",
  alignItems: "center",
  justifyContent: "center",
  width: "34px",
  height: "34px",
  borderRadius: "10px",
  border: "1px solid var(--card-border)",
  background: "var(--bg-secondary)",
  color: "var(--text-primary)",
  cursor: "pointer",
};

export const overlayStyle: React.CSSProperties = {
  position: "fixed",
  inset: 0,
  background: "rgba(15,23,42,0.45)",
  backdropFilter: "blur(6px)",
  display: "flex",
  alignItems: "flex-start",
  justifyContent: "center",
  padding: "24px",
  zIndex: 100,
  overflowY: "auto",
};

export const modalStyle: React.CSSProperties = {
  width: "100%",
  maxHeight: "90vh",
  overflowY: "auto",
  position: "relative",
};

export const closeButtonStyle: React.CSSProperties = {
  position: "absolute",
  top: "20px",
  right: "20px",
  border: "none",
  background: "transparent",
  color: "var(--text-muted)",
  cursor: "pointer",
};

export const twoColumnGridStyle: React.CSSProperties = {
  display: "grid",
  gridTemplateColumns: "repeat(2, minmax(0, 1fr))",
  gap: "16px",
};

export const thStyle: React.CSSProperties = {
  padding: "12px",
  textAlign: "left",
  color: "var(--text-muted)",
  fontSize: "0.8rem",
  textTransform: "uppercase",
  letterSpacing: "0.08em",
};

export const tdStyle: React.CSSProperties = {
  padding: "14px 12px",
};

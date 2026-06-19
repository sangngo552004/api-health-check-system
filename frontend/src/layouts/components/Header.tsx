import React from "react";
import { useTheme } from "../../context/useTheme";
import { Moon, Sun } from "lucide-react";

export const Header: React.FC = () => {
  const { theme, toggleTheme } = useTheme();

  return (
    <header
      style={{
        height: "70px",
        background: "var(--header-bg)",
        backdropFilter: "blur(12px)",
        borderBottom: "1px solid var(--card-border)",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        padding: "0 40px",
        position: "sticky",
        top: 0,
        zIndex: 5,
        transition: "background 0.3s ease, border-color 0.3s ease",
      }}
      className="top-header"
    >
      <div />

      <div style={{ display: "flex", alignItems: "center", gap: "16px" }}>
        <button
          onClick={toggleTheme}
          style={{
            background: "var(--accent-bg)",
            border: "1px solid var(--card-border)",
            color: "var(--accent-color)",
            cursor: "pointer",
            padding: "8px",
            borderRadius: "50%",
            display: "flex",
            alignItems: "center",
            transition: "all 0.2s",
          }}
          title="Toggle Dark/Light Mode"
        >
          {theme === "dark" ? <Sun size={20} /> : <Moon size={20} />}
        </button>
      </div>
    </header>
  );
};

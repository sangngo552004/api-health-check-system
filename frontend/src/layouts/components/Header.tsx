import React from "react";
import { useAuth } from "../../context/useAuth";
import { useTheme } from "../../context/useTheme";
import { useTranslation } from "react-i18next";
import { Moon, Sun, Globe } from "lucide-react";

export const Header: React.FC = () => {
  const { user } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const { t, i18n } = useTranslation();

  const toggleLanguage = () => {
    const nextLang = i18n.language === "vi" ? "en" : "vi";
    i18n.changeLanguage(nextLang);
  };

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
      <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
        <span
          style={{
            fontSize: "1rem",
            fontWeight: 600,
            color: "var(--text-primary)",
          }}
        >
          {t("common.welcome", { name: user ? user.username : "Guest" })}
        </span>
      </div>

      <div style={{ display: "flex", alignItems: "center", gap: "16px" }}>
        {/* Nút chuyển đổi Ngôn ngữ */}
        <button
          onClick={toggleLanguage}
          style={{
            background: "none",
            border: "none",
            color: "var(--text-muted)",
            cursor: "pointer",
            padding: "8px",
            borderRadius: "50%",
            display: "flex",
            alignItems: "center",
            transition: "all 0.2s",
          }}
          title={
            i18n.language === "vi" ? "Switch to English" : "Đổi sang Tiếng Việt"
          }
        >
          <Globe size={20} />
          <span
            style={{
              marginLeft: "4px",
              fontSize: "0.8rem",
              fontWeight: 600,
              textTransform: "uppercase",
            }}
          >
            {i18n.language}
          </span>
        </button>

        {/* Nút chuyển đổi Theme */}
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

        <div
          style={{
            fontSize: "0.8rem",
            padding: "6px 12px",
            borderRadius: "20px",
            background: "var(--accent-bg)",
            color: "var(--accent-color)",
            border: "1px solid var(--accent-hover)",
            fontWeight: 600,
          }}
        >
          Environment: Local
        </div>
      </div>
    </header>
  );
};

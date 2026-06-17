import React from "react";
import { Search, UserPlus } from "lucide-react";

export const MembersToolbar: React.FC<{
  searchTerm: string;
  onSearchTermChange: (value: string) => void;
  onOpenInvite: () => void;
  addLabel: string;
  subtitle: string;
  title: string;
  searchPlaceholder: string;
}> = ({
  searchTerm,
  onSearchTermChange,
  onOpenInvite,
  addLabel,
  subtitle,
  title,
  searchPlaceholder,
}) => (
  <>
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "flex-end",
        marginBottom: "32px",
      }}
    >
      <div>
        <p className="eyebrow">{subtitle}</p>
        <h1
          style={{
            color: "var(--text-primary)",
            fontSize: "2rem",
            fontWeight: 700,
            margin: "8px 0 0 0",
          }}
        >
          {title}
        </h1>
      </div>
      <button
        onClick={onOpenInvite}
        style={{
          display: "flex",
          alignItems: "center",
          gap: "8px",
          background: "linear-gradient(135deg, #a855f7 0%, #c084fc 100%)",
          border: "none",
          color: "#fff",
          padding: "12px 20px",
          borderRadius: "12px",
          fontWeight: 600,
          cursor: "pointer",
          boxShadow: "0 4px 15px rgba(168, 85, 247, 0.3)",
          transition: "all 0.2s",
        }}
        onMouseOver={(e) =>
          (e.currentTarget.style.transform = "translateY(-2px)")
        }
        onMouseOut={(e) => (e.currentTarget.style.transform = "none")}
      >
        <UserPlus size={18} />
        {addLabel}
      </button>
    </div>

    <div
      className="card"
      style={{
        marginBottom: "24px",
        padding: "16px 24px",
        display: "flex",
        gap: "16px",
      }}
    >
      <div style={{ position: "relative", flex: 1, maxWidth: "400px" }}>
        <Search
          size={18}
          style={{
            position: "absolute",
            left: "14px",
            top: "50%",
            transform: "translateY(-50%)",
            color: "var(--text-muted)",
          }}
        />
        <input
          type="text"
          placeholder={searchPlaceholder}
          value={searchTerm}
          onChange={(e) => onSearchTermChange(e.target.value)}
          style={{
            width: "100%",
            padding: "10px 16px 10px 42px",
            background: "var(--bg-secondary)",
            border: "1px solid var(--card-border)",
            borderRadius: "10px",
            color: "var(--text-primary)",
            outline: "none",
          }}
        />
      </div>
    </div>
  </>
);

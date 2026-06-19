import React from "react";
import { Plus, Search } from "lucide-react";

export const EndpointsToolbar: React.FC<{
  title: string;
  subtitle: string;
  addLabel: string;
  searchPlaceholder: string;
  searchTerm: string;
  onSearchTermChange: (value: string) => void;
  onCreate: () => void;
  filters?: React.ReactNode;
}> = ({
  title,
  subtitle,
  addLabel,
  searchPlaceholder,
  searchTerm,
  onSearchTermChange,
  onCreate,
  filters,
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
        onClick={onCreate}
        style={{
          display: "flex",
          alignItems: "center",
          gap: "8px",
          background: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
          border: "none",
          color: "#fff",
          padding: "12px 20px",
          borderRadius: "12px",
          fontWeight: 600,
          cursor: "pointer",
          boxShadow: "0 4px 15px rgba(79, 172, 254, 0.3)",
          transition: "all 0.2s",
        }}
        onMouseOver={(e) =>
          (e.currentTarget.style.transform = "translateY(-2px)")
        }
        onMouseOut={(e) => (e.currentTarget.style.transform = "none")}
      >
        <Plus size={18} />
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
        flexWrap: "wrap",
      }}
    >
      <div style={{ position: "relative", flex: 1, minWidth: "240px" }}>
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
      {filters}
    </div>
  </>
);

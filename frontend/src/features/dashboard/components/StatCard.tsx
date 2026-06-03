import React from "react";

interface StatCardProps {
  title: string;
  value: string | number;
  icon: React.ReactNode;
  color: string;
  bgColor: string;
  loading?: boolean;
}

export const StatCard: React.FC<StatCardProps> = ({
  title,
  value,
  icon,
  color,
  bgColor,
  loading,
}) => {
  return (
    <div
      className="card"
      style={{
        display: "flex",
        alignItems: "center",
        gap: "20px",
        animation: "fadeIn 0.5s ease-out",
        borderColor: bgColor,
      }}
    >
      <div
        style={{
          width: "56px",
          height: "56px",
          borderRadius: "16px",
          background: bgColor,
          color: color,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        {icon}
      </div>
      <div>
        <h3
          style={{
            color: "var(--text-muted)",
            fontSize: "0.9rem",
            margin: "0 0 4px 0",
            fontWeight: 500,
          }}
        >
          {title}
        </h3>
        <div
          style={{
            color: "var(--text-primary)",
            fontSize: "1.8rem",
            fontWeight: 700,
            lineHeight: 1,
          }}
        >
          {loading ? "..." : value}
        </div>
      </div>
    </div>
  );
};

import React from "react";
import { AlertTriangle, CheckCircle2, Siren, Zap } from "lucide-react";

const SummaryCard: React.FC<{
  title: string;
  value: number;
  icon: React.ReactNode;
  tone: string;
  color: string;
}> = ({ title, value, icon, tone, color }) => (
  <div
    className="card"
    style={{
      padding: "18px 20px",
      display: "flex",
      justifyContent: "space-between",
      alignItems: "center",
      gap: "16px",
    }}
  >
    <div>
      <div style={{ fontSize: "0.85rem", color: "var(--text-secondary)" }}>
        {title}
      </div>
      <div style={{ fontSize: "1.8rem", fontWeight: 700 }}>{value}</div>
    </div>
    <div
      style={{
        width: "44px",
        height: "44px",
        borderRadius: "14px",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        background: tone,
        color,
      }}
    >
      {icon}
    </div>
  </div>
);

export const IncidentSummaryCards: React.FC<{
  open: number;
  resolved: number;
  critical: number;
  totalItems: number;
}> = ({ open, resolved, critical, totalItems }) => (
  <div
    style={{
      display: "grid",
      gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
      gap: "16px",
    }}
  >
    <SummaryCard
      title="Đang mở"
      value={open}
      icon={<Siren size={20} />}
      tone="rgba(239, 68, 68, 0.12)"
      color="var(--error-color)"
    />
    <SummaryCard
      title="Đã phục hồi"
      value={resolved}
      icon={<CheckCircle2 size={20} />}
      tone="rgba(16, 185, 129, 0.12)"
      color="var(--success-color)"
    />
    <SummaryCard
      title="Critical"
      value={critical}
      icon={<Zap size={20} />}
      tone="rgba(245, 158, 11, 0.12)"
      color="var(--warning-color)"
    />
    <SummaryCard
      title="Tổng đang lọc"
      value={totalItems}
      icon={<AlertTriangle size={20} />}
      tone="rgba(56, 189, 248, 0.12)"
      color="var(--accent-color)"
    />
  </div>
);

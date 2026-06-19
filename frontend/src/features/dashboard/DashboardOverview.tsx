import React, { useCallback, useEffect, useState } from "react";
import { useWorkspace } from "../../context/useWorkspace";
import { api } from "../../services/api";
import {
  Globe,
  CheckCircle,
  Clock,
  AlertTriangle,
  RefreshCw,
} from "lucide-react";
import { useTranslation } from "react-i18next";
import { StatCard } from "./components/StatCard";
import { LatencyChart } from "./components/LatencyChart";
import { ActiveIncidentsBoard } from "./components/ActiveIncidentsBoard";
import {
  DashboardActiveIncidentsDto,
  DashboardLatencyChartDto,
  DashboardStatsSummaryDto,
  LatencyChartLine,
  LatencyChartPoint,
} from "./types";
import { getErrorMessage } from "../../utils/error";

export const DashboardOverview: React.FC = () => {
  const { activeWorkspace } = useWorkspace();
  const { t } = useTranslation();

  const [stats, setStats] = useState<DashboardStatsSummaryDto | null>(null);
  const [incidents, setIncidents] = useState<DashboardActiveIncidentsDto | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [chartData, setChartData] = useState<LatencyChartPoint[]>([]);
  const [chartLines, setChartLines] = useState<LatencyChartLine[]>([]);

  const fetchDashboardData = useCallback(async () => {
    if (!activeWorkspace) return;
    setLoading(true);
    setError(null);
    try {
      const [statsData, incidentsData, chartResponse] = await Promise.all([
        api.get<DashboardStatsSummaryDto>("/dashboard/summary"),
        api.get<DashboardActiveIncidentsDto>("/dashboard/active-incidents"),
        api.get<DashboardLatencyChartDto>("/dashboard/latency-chart"),
      ]);
      setStats(statsData);
      setIncidents(incidentsData);

      const plotSeries = chartResponse.series || [];

      const colors = ["#38bdf8", "#a855f7", "#10b981"];
      const lines = plotSeries.map((series, idx) => ({
        key: series.endpointName,
        color: colors[idx % colors.length],
      }));
      setChartLines(lines);

      if (plotSeries.length === 0) {
        setChartData([]);
        return;
      }

      const timeMap = new Map<string, LatencyChartPoint>();

      plotSeries.forEach((series) => {
        series.points.forEach((point) => {
          const date = new Date(point.checkedAt);
          const timeKey = date.toLocaleTimeString("vi-VN", {
            hour12: false,
          });

          if (!timeMap.has(timeKey)) {
            timeMap.set(timeKey, { time: timeKey });
          }
          const existing = timeMap.get(timeKey) ?? { time: timeKey };
          existing[series.endpointName] = point.responseTimeMillis;
          timeMap.set(timeKey, existing);
        });
      });

      const mergedData = Array.from(timeMap.values()).sort((a, b) =>
        a.time.localeCompare(b.time),
      );
      setChartData(mergedData);
    } catch (error) {
      setError(
        getErrorMessage(
          error,
          "Dashboard đang tải quá lâu hoặc backend chưa phản hồi.",
        ),
      );
      console.error(
        "Failed to fetch dashboard data",
        getErrorMessage(error, "Unknown dashboard error"),
      );
    } finally {
      setLoading(false);
    }
  }, [activeWorkspace]);

  useEffect(() => {
    void fetchDashboardData();
    const interval = setInterval(() => {
      void fetchDashboardData();
    }, 60000);
    return () => clearInterval(interval);
  }, [fetchDashboardData]);

  if (!activeWorkspace) {
    return (
      <div style={{ color: "var(--text-muted)" }}>
        {t(
          "dashboard.noWorkspace",
          "Vui lòng chọn hoặc tạo một Workspace để xem thống kê.",
        )}
      </div>
    );
  }

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "32px" }}>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "flex-end",
        }}
      >
        <div>
          <p className="eyebrow">
            {t("dashboard.overview", "Tổng quan hệ thống")}
          </p>
          <h1 style={{ fontSize: "2rem", fontWeight: 700, margin: 0 }}>
            Dashboard - {activeWorkspace.name}
          </h1>
        </div>
        <button
          onClick={fetchDashboardData}
          disabled={loading}
          style={{
            display: "flex",
            alignItems: "center",
            gap: "8px",
            background: "var(--accent-bg)",
            border: "1px solid var(--accent-hover)",
            color: "var(--accent-color)",
            padding: "10px 16px",
            borderRadius: "12px",
            cursor: loading ? "not-allowed" : "pointer",
            transition: "all 0.2s",
            opacity: loading ? 0.7 : 1,
          }}
        >
          <RefreshCw size={16} className={loading ? "spin" : ""} />
          {loading
            ? t("common.loading")
            : t("dashboard.refresh", "Cập nhật ngay")}
        </button>
      </div>

      {error && (
        <div
          className="card"
          style={{
            border: "1px solid rgba(245, 158, 11, 0.35)",
            background: "rgba(245, 158, 11, 0.08)",
            color: "var(--text-primary)",
          }}
        >
          <div style={{ fontWeight: 600, marginBottom: "8px" }}>
            Khong tai duoc dashboard
          </div>
          <div style={{ color: "var(--text-secondary)", lineHeight: 1.5 }}>
            {error}
          </div>
        </div>
      )}

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))",
          gap: "24px",
        }}
      >
        <StatCard
          title={t("dashboard.totalEndpoints", "Tổng số Endpoints")}
          value={stats?.totalEndpoints || 0}
          icon={<Globe size={28} />}
          color="var(--accent-color)"
          bgColor="var(--accent-bg)"
          loading={loading}
        />
        <StatCard
          title={t("dashboard.upEndpoints", "Endpoints Ổn định")}
          value={stats?.upEndpoints || 0}
          icon={<CheckCircle size={28} />}
          color="var(--success-color)"
          bgColor="rgba(16, 185, 129, 0.15)"
          loading={loading}
        />
        <StatCard
          title={t("dashboard.degradedEndpoints", "Endpoints Cảnh báo")}
          value={stats?.degradedEndpoints || 0}
          icon={<Clock size={28} />}
          color="var(--warning-color)"
          bgColor="rgba(245, 158, 11, 0.15)"
          loading={loading}
        />
        <StatCard
          title={t("dashboard.downEndpoints", "Endpoints Đang sập")}
          value={stats?.downEndpoints || 0}
          icon={<AlertTriangle size={28} />}
          color="var(--error-color)"
          bgColor="rgba(239, 68, 68, 0.15)"
          loading={loading}
        />
      </div>

      <div
        style={{ display: "grid", gridTemplateColumns: "2fr 1fr", gap: "24px" }}
      >
        <LatencyChart data={chartData} lines={chartLines} />
        <ActiveIncidentsBoard
          incidents={incidents?.incidents || []}
          count={incidents?.openIncidentsCount || 0}
        />
      </div>
    </div>
  );
};

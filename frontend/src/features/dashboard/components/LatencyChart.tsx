import React from "react";
import { Activity } from "lucide-react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from "recharts";
import { useTranslation } from "react-i18next";
import { LatencyChartLine, LatencyChartPoint } from "../types";

interface LatencyChartProps {
  data: LatencyChartPoint[];
  lines: LatencyChartLine[];
}

export const LatencyChart: React.FC<LatencyChartProps> = ({ data, lines }) => {
  const { t } = useTranslation();

  return (
    <div className="card" style={{ display: "flex", flexDirection: "column" }}>
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: "12px",
          marginBottom: "24px",
        }}
      >
        <div
          style={{
            width: "40px",
            height: "40px",
            borderRadius: "10px",
            background: "rgba(168, 85, 247, 0.15)",
            color: "#a855f7",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <Activity size={20} />
        </div>
        <div>
          <h2
            style={{ fontSize: "1.2rem", fontWeight: 600, margin: "0 0 4px 0" }}
          >
            {t("dashboard.latencyChart", "Lịch sử Độ trễ (Latency)")}
          </h2>
          <p
            style={{
              color: "var(--text-muted)",
              fontSize: "0.85rem",
              margin: 0,
            }}
          >
            {t(
              "dashboard.latencyDesc",
              "Hiển thị tối đa 3 endpoints hoạt động gần nhất",
            )}
          </p>
        </div>
      </div>

      <div style={{ flex: 1, minHeight: "300px" }}>
        {data.length > 0 ? (
          <ResponsiveContainer width="100%" height="100%">
            <LineChart
              data={data}
              margin={{ top: 5, right: 20, bottom: 5, left: 0 }}
            >
              <CartesianGrid
                strokeDasharray="3 3"
                stroke="var(--card-border)"
                vertical={false}
              />
              <XAxis
                dataKey="time"
                stroke="var(--text-muted)"
                fontSize={12}
                tickMargin={10}
              />
              <YAxis
                stroke="var(--text-muted)"
                fontSize={12}
                unit="ms"
                tickMargin={10}
              />
              <Tooltip
                contentStyle={{
                  background: "var(--card-bg)",
                  border: "1px solid var(--card-border)",
                  borderRadius: "8px",
                  color: "var(--text-primary)",
                  backdropFilter: "blur(16px)",
                }}
                itemStyle={{ fontSize: "0.9rem" }}
              />
              <Legend wrapperStyle={{ paddingTop: "20px" }} />
              {lines.map((line) => (
                <Line
                  key={line.key}
                  type="monotone"
                  dataKey={line.key}
                  stroke={line.color}
                  strokeWidth={3}
                  dot={{ r: 4, fill: "var(--bg-primary)", strokeWidth: 2 }}
                  activeDot={{ r: 6 }}
                  connectNulls={true}
                />
              ))}
            </LineChart>
          </ResponsiveContainer>
        ) : (
          <div
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              height: "100%",
              color: "var(--text-muted)",
            }}
          >
            {t("dashboard.noData", "Chưa có dữ liệu độ trễ để hiển thị.")}
          </div>
        )}
      </div>
    </div>
  );
};

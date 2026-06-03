import React from "react";
import { Outlet } from "react-router-dom";
import { Sidebar } from "./components/Sidebar";
import { Header } from "./components/Header";

export const MainLayout: React.FC = () => {
  return (
    <div
      style={{
        display: "flex",
        minHeight: "100vh",
        background: "var(--bg-primary)",
        color: "var(--text-primary)",
        fontFamily: "system-ui, -apple-system, sans-serif",
      }}
    >
      <Sidebar />

      {/* Main panel */}
      <div
        style={{
          flex: 1,
          marginLeft: "280px",
          display: "flex",
          flexDirection: "column",
          minWidth: 0,
        }}
        className="main-content-area"
      >
        <Header />

        {/* Page Content viewport */}
        <main style={{ flex: 1, padding: "40px", overflowY: "auto" }}>
          <Outlet />
        </main>
      </div>

      {/* CSS to manage responsiveness cleanly */}
      <style>{`
        @media (max-width: 1024px) {
          .desktop-sidebar {
            display: none !important;
          }
          .main-content-area {
            margin-left: 0 !important;
          }
          .top-header {
            padding: 0 20px !important;
          }
        }
      `}</style>
    </div>
  );
};

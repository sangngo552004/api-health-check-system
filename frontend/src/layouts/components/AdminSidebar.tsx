import React from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import {
  Briefcase,
  LogOut,
  Shield,
  User as UserIcon,
  Users,
} from "lucide-react";
import { useAuth } from "../../context/useAuth";

export const AdminSidebar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems = [
    { name: "Users", path: "/admin/users", icon: <Users size={20} /> },
    {
      name: "Workspaces",
      path: "/admin/workspaces",
      icon: <Briefcase size={20} />,
    },
  ];

  return (
    <aside
      style={{
        width: "280px",
        background: "var(--sidebar-bg)",
        backdropFilter: "blur(16px)",
        borderRight: "1px solid var(--card-border)",
        display: "flex",
        flexDirection: "column",
        zIndex: 10,
        position: "fixed",
        top: 0,
        bottom: 0,
        left: 0,
      }}
      className="desktop-sidebar"
    >
      <div
        style={{
          padding: "24px",
          display: "flex",
          alignItems: "center",
          gap: "12px",
          borderBottom: "1px solid var(--card-border)",
        }}
      >
        <div
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            width: "40px",
            height: "40px",
            borderRadius: "12px",
            background: "linear-gradient(135deg, #f97316, #fb7185)",
            color: "#fff",
          }}
        >
          <Shield size={22} />
        </div>
        <span
          style={{
            fontSize: "1.2rem",
            fontWeight: 700,
            color: "var(--text-primary)",
          }}
        >
          ADMIN AREA
        </span>
      </div>

      <div
        style={{
          padding: "20px 24px",
          borderBottom: "1px solid var(--card-border)",
        }}
      >
        <div
          style={{
            fontSize: "0.75rem",
            textTransform: "uppercase",
            color: "var(--text-muted)",
          }}
        >
          System scope
        </div>
        <div
          style={{
            marginTop: "10px",
            padding: "12px",
            borderRadius: "10px",
            background: "rgba(249, 115, 22, 0.1)",
            border: "1px solid rgba(249, 115, 22, 0.2)",
            color: "#fdba74",
            fontSize: "0.9rem",
            lineHeight: 1.5,
          }}
        >
          Quản lý users, workspaces và membership. Không can thiệp vào tài
          nguyên nghiệp vụ bên trong workspace.
        </div>
      </div>

      <nav
        style={{
          padding: "24px 16px",
          display: "flex",
          flexDirection: "column",
          gap: "8px",
          flex: 1,
        }}
      >
        {menuItems.map((item) => {
          const isActive = location.pathname === item.path;
          return (
            <Link
              key={item.path}
              to={item.path}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "12px",
                padding: "12px 16px",
                borderRadius: "12px",
                textDecoration: "none",
                fontSize: "0.95rem",
                fontWeight: isActive ? 600 : 500,
                color: isActive ? "#fdba74" : "var(--text-muted)",
                background: isActive
                  ? "rgba(249, 115, 22, 0.12)"
                  : "transparent",
                border: isActive
                  ? "1px solid rgba(249, 115, 22, 0.25)"
                  : "1px solid transparent",
              }}
            >
              {item.icon}
              <span>{item.name}</span>
            </Link>
          );
        })}
      </nav>

      <div
        style={{
          padding: "20px",
          borderTop: "1px solid var(--card-border)",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          background: "var(--accent-bg)",
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
          <div
            style={{
              width: "36px",
              height: "36px",
              borderRadius: "50%",
              background: "var(--card-bg)",
              border: "1px solid var(--card-border)",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: "#fdba74",
            }}
          >
            <UserIcon size={18} />
          </div>
          <div style={{ display: "flex", flexDirection: "column" }}>
            <span
              style={{
                fontSize: "0.875rem",
                fontWeight: 600,
                color: "var(--text-primary)",
              }}
            >
              {user?.username || "Admin"}
            </span>
            <span style={{ fontSize: "0.725rem", color: "var(--text-muted)" }}>
              {user?.role || "SUPER_ADMIN"}
            </span>
          </div>
        </div>
        <button
          onClick={() => {
            logout();
            navigate("/login");
          }}
          style={{
            background: "none",
            border: "none",
            color: "var(--text-muted)",
            cursor: "pointer",
          }}
          title="Đăng xuất"
        >
          <LogOut size={18} />
        </button>
      </div>
    </aside>
  );
};

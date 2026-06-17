import React, { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/useAuth";
import { useWorkspace } from "../../context/useWorkspace";
import { useTranslation } from "react-i18next";
import {
  Shield,
  Activity,
  Globe,
  AlertTriangle,
  Users,
  LogOut,
  ChevronDown,
  Briefcase,
  User as UserIcon,
  ShieldAlert,
  BellRing,
} from "lucide-react";

export const Sidebar: React.FC = () => {
  const { user, logout } = useAuth();
  const { workspaces, activeWorkspace, selectWorkspace, loadingWorkspaces } =
    useWorkspace();
  const { t } = useTranslation();
  const navigate = useNavigate();
  const location = useLocation();
  const [wsDropdownOpen, setWsDropdownOpen] = useState(false);

  const menuItems = [
    {
      name: t("menu.dashboard", "Tổng quan"),
      path: "/app",
      icon: <Activity size={20} />,
    },
    {
      name: t("menu.endpoints", "Giám sát APIs"),
      path: "/app/endpoints",
      icon: <Globe size={20} />,
    },
    {
      name: "Check Policies",
      path: "/app/policies",
      icon: <ShieldAlert size={20} />,
    },
    { name: "Alert Rules", path: "/app/alerts", icon: <BellRing size={20} /> },
    {
      name: "Contact Groups",
      path: "/app/contacts",
      icon: <Users size={20} />,
    },
    {
      name: t("menu.incidents", "Sự cố & Cảnh báo"),
      path: "/app/incidents",
      icon: <AlertTriangle size={20} />,
    },
  ];

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

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
        transition: "background 0.3s ease, border-color 0.3s ease",
      }}
      className="desktop-sidebar"
    >
      {/* Brand Logo */}
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
            background: "linear-gradient(135deg, #4facfe, #00f2fe)",
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
            letterSpacing: "0.05em",
          }}
        >
          API MONITOR
        </span>
      </div>

      {/* Workspace Display */}
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
            letterSpacing: "0.08em",
            marginBottom: "6px",
          }}
        >
          {t("sidebar.activeWorkspace", "Workspace hoạt động")}
        </div>
        <div
          onClick={() => setWsDropdownOpen(!wsDropdownOpen)}
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            padding: "10px 12px",
            background: "var(--accent-bg)",
            border: "1px solid var(--card-border)",
            borderRadius: "10px",
            cursor: "pointer",
            transition: "all 0.2s",
          }}
        >
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: "8px",
              overflow: "hidden",
            }}
          >
            <Briefcase
              size={16}
              style={{ color: "var(--accent-color)", flexShrink: 0 }}
            />
            <span
              style={{
                fontSize: "0.9rem",
                fontWeight: 600,
                color: "var(--text-primary)",
                whiteSpace: "nowrap",
                overflow: "hidden",
                textOverflow: "ellipsis",
              }}
            >
              {activeWorkspace
                ? activeWorkspace.name
                : t("workspace.select", "Chọn workspace")}
            </span>
          </div>
          <ChevronDown
            size={16}
            style={{
              color: "var(--text-muted)",
              flexShrink: 0,
              transform: wsDropdownOpen ? "rotate(180deg)" : "none",
              transition: "transform 0.2s",
            }}
          />
        </div>

        {/* Workspace Dropdown */}
        {wsDropdownOpen && (
          <div
            style={{
              position: "absolute",
              left: "24px",
              right: "24px",
              marginTop: "8px",
              background: "var(--card-bg)",
              backdropFilter: "blur(20px)",
              border: "1px solid var(--card-border)",
              borderRadius: "12px",
              boxShadow: "var(--card-shadow)",
              zIndex: 100,
              overflow: "hidden",
            }}
          >
            {loadingWorkspaces ? (
              <div
                style={{
                  padding: "12px",
                  fontSize: "0.85rem",
                  color: "var(--text-muted)",
                }}
              >
                {t("common.loading")}
              </div>
            ) : workspaces.length === 0 ? (
              <div
                style={{
                  padding: "12px",
                  fontSize: "0.85rem",
                  color: "var(--text-muted)",
                }}
              >
                {t("workspace.notFound", "Không tìm thấy workspace.")}
              </div>
            ) : (
              workspaces.map((ws) => (
                <div
                  key={ws.id}
                  onClick={() => {
                    selectWorkspace(ws.id);
                    setWsDropdownOpen(false);
                  }}
                  style={{
                    padding: "12px 16px",
                    fontSize: "0.875rem",
                    cursor: "pointer",
                    background:
                      activeWorkspace?.id === ws.id
                        ? "var(--accent-bg)"
                        : "transparent",
                    color:
                      activeWorkspace?.id === ws.id
                        ? "var(--accent-color)"
                        : "var(--text-secondary)",
                    transition: "all 0.15s",
                    display: "flex",
                    alignItems: "center",
                    gap: "8px",
                  }}
                >
                  <Briefcase size={14} />
                  <span>{ws.name}</span>
                </div>
              ))
            )}
          </div>
        )}
      </div>

      {/* Sidebar Nav links */}
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
              key={item.name}
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
                color: isActive ? "var(--accent-color)" : "var(--text-muted)",
                background: isActive ? "var(--accent-bg)" : "transparent",
                border: isActive
                  ? "1px solid var(--accent-hover)"
                  : "1px solid transparent",
                transition: "all 0.2s",
              }}
            >
              {item.icon}
              <span>{item.name}</span>
            </Link>
          );
        })}
      </nav>

      {/* Sidebar footer (User profile & Logout) */}
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
        <div
          style={{
            display: "flex",
            alignItems: "center",
            gap: "10px",
            overflow: "hidden",
          }}
        >
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
              color: "var(--accent-color)",
              flexShrink: 0,
            }}
          >
            <UserIcon size={18} />
          </div>
          <div
            style={{
              display: "flex",
              flexDirection: "column",
              overflow: "hidden",
            }}
          >
            <span
              style={{
                fontSize: "0.875rem",
                fontWeight: 600,
                color: "var(--text-primary)",
                whiteSpace: "nowrap",
                overflow: "hidden",
                textOverflow: "ellipsis",
              }}
            >
              {user ? user.username : "Guest"}
            </span>
            <span style={{ fontSize: "0.725rem", color: "var(--text-muted)" }}>
              {user ? user.role : "USER"}
            </span>
          </div>
        </div>
        <button
          onClick={handleLogout}
          style={{
            background: "none",
            border: "none",
            color: "var(--text-muted)",
            cursor: "pointer",
            padding: "6px",
            borderRadius: "8px",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            transition: "all 0.15s",
          }}
          title={t("common.logout", "Đăng xuất")}
        >
          <LogOut size={18} />
        </button>
      </div>
    </aside>
  );
};

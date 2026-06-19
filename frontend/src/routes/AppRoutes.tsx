import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";
import { MainLayout } from "../layouts/MainLayout";
import { AdminLayout } from "../layouts/AdminLayout";
import { Login } from "../features/auth/pages/Login";
import { DashboardOverview } from "../features/dashboard/DashboardOverview";
import { EndpointsList } from "../features/endpoints/EndpointsList";
import { PoliciesList } from "../features/policies/PoliciesList";
import { AlertsList } from "../features/alerts/AlertsList";
import { ContactsList } from "../features/contacts/ContactsList";
import { IncidentsList } from "../features/incidents/IncidentsList";
import { WorkspaceSelectorPage } from "../features/workspace/WorkspaceSelectorPage";
import { AdminUsersPage } from "../features/admin/AdminUsersPage";
import { AdminWorkspacesPage } from "../features/admin/AdminWorkspacesPage";
import { useWorkspace } from "../context/useWorkspace";

const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "100vh",
          background: "#0b0f19",
          color: "#38bdf8",
          fontSize: "1.2rem",
          fontWeight: 600,
        }}
      >
        Đang khởi tạo phiên làm việc...
      </div>
    );
  }

  return user ? <>{children}</> : <Navigate to="/login" replace />;
};

export const AppRoutes: React.FC = () => {
  const { user } = useAuth();
  const { activeWorkspace } = useWorkspace();
  const homeRedirect =
    user?.role === "ADMIN"
      ? "/admin/users"
      : activeWorkspace
        ? "/app"
        : "/select-workspace";

  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <PrivateRoute>
            <Navigate to={homeRedirect} replace />
          </PrivateRoute>
        }
      />
      <Route
        path="/select-workspace"
        element={
          <PrivateRoute>
            {user?.role === "USER" ? (
              <WorkspaceSelectorPage />
            ) : (
              <Navigate to="/admin/users" replace />
            )}
          </PrivateRoute>
        }
      />

      <Route
        path="/admin"
        element={
          <PrivateRoute>
            {user?.role === "ADMIN" ? (
              <AdminLayout />
            ) : (
              <Navigate to={homeRedirect} replace />
            )}
          </PrivateRoute>
        }
      >
        <Route index element={<Navigate to="users" replace />} />
        <Route path="users" element={<AdminUsersPage />} />
        <Route path="workspaces" element={<AdminWorkspacesPage />} />
      </Route>

      <Route
        path="/app"
        element={
          <PrivateRoute>
            {user?.role === "USER" ? (
              <MainLayout />
            ) : (
              <Navigate to="/admin/users" replace />
            )}
          </PrivateRoute>
        }
      >
        <Route
          index
          element={
            activeWorkspace ? (
              <DashboardOverview />
            ) : (
              <Navigate to="/select-workspace" replace />
            )
          }
        />
        <Route
          path="endpoints"
          element={
            activeWorkspace ? (
              <EndpointsList />
            ) : (
              <Navigate to="/select-workspace" replace />
            )
          }
        />
        <Route
          path="policies"
          element={
            activeWorkspace ? (
              <PoliciesList />
            ) : (
              <Navigate to="/select-workspace" replace />
            )
          }
        />
        <Route
          path="alerts"
          element={
            activeWorkspace ? (
              <AlertsList />
            ) : (
              <Navigate to="/select-workspace" replace />
            )
          }
        />
        <Route
          path="contacts"
          element={
            activeWorkspace ? (
              <ContactsList />
            ) : (
              <Navigate to="/select-workspace" replace />
            )
          }
        />
        <Route
          path="incidents"
          element={
            activeWorkspace ? (
              <IncidentsList />
            ) : (
              <Navigate to="/select-workspace" replace />
            )
          }
        />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

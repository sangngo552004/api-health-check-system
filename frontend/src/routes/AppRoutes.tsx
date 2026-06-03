import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";
import { MainLayout } from "../layouts/MainLayout";
import { Login } from "../features/auth/pages/Login";
import { Register } from "../features/auth/pages/Register";
import { DashboardOverview } from "../features/dashboard/DashboardOverview";
import { EndpointsList } from "../features/endpoints/EndpointsList";
import { MembersList } from "../features/members/MembersList";
import { PoliciesList } from "../features/policies/PoliciesList";
import { AlertsList } from "../features/alerts/AlertsList";
import { ContactsList } from "../features/contacts/ContactsList";
import { IncidentsPlaceholder } from "../pages/Placeholders";

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
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      {/* Protected Routes (Wrapped in Layout Shell) */}
      <Route
        path="/"
        element={
          <PrivateRoute>
            <MainLayout />
          </PrivateRoute>
        }
      >
        <Route index element={<DashboardOverview />} />
        <Route path="endpoints" element={<EndpointsList />} />
        <Route path="policies" element={<PoliciesList />} />
        <Route path="alerts" element={<AlertsList />} />
        <Route path="contacts" element={<ContactsList />} />
        <Route path="incidents" element={<IncidentsPlaceholder />} />
        <Route path="members" element={<MembersList />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
};

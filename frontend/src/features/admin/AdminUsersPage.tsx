import React, { useCallback, useEffect, useState } from "react";
import { Plus } from "lucide-react";
import { workspacesApi } from "../../services/api/workspaces.api";
import {
  AdminUserCreateCommand,
  AdminUserDto,
  AdminUserUpdateCommand,
} from "../../types/workspace.types";
import { getErrorMessage } from "../../utils/error";
import { PaginationBar } from "./components/adminUi";
import { primaryButton } from "./components/adminStyles";
import { UserFilters } from "./users/UserFilters";
import { UserModal } from "./users/UserModal";
import { emptyUserForm, UserFormState } from "./users/userPage.types";
import { UsersTable } from "./users/UsersTable";

export const AdminUsersPage: React.FC = () => {
  const [users, setUsers] = useState<AdminUserDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searchInput, setSearchInput] = useState("");
  const [search, setSearch] = useState("");
  const [role, setRole] = useState<"ALL" | "SUPER_ADMIN" | "USER">("ALL");
  const [status, setStatus] = useState<"ALL" | "ACTIVE" | "INACTIVE">("ALL");
  const [sortBy, setSortBy] = useState("createdAt");
  const [sortDir, setSortDir] = useState<"asc" | "desc">("desc");
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<AdminUserDto | null>(null);
  const [form, setForm] = useState<UserFormState>(emptyUserForm);

  const loadUsers = useCallback(async () => {
    try {
      setLoading(true);
      const response = await workspacesApi.getAdminUsers({
        page,
        size,
        search: search || undefined,
        role: role === "ALL" ? undefined : role,
        isActive: status === "ALL" ? undefined : status === "ACTIVE",
        sortBy,
        sortDir,
      });
      setUsers(response.items);
      setTotalPages(response.totalPages);
      setTotalItems(response.totalItems);
      setError(null);
    } catch (err) {
      setError(getErrorMessage(err, "Không thể tải danh sách users"));
    } finally {
      setLoading(false);
    }
  }, [page, role, search, size, sortBy, sortDir, status]);

  useEffect(() => {
    void loadUsers();
  }, [loadUsers]);

  const openCreateModal = () => {
    setEditingUser(null);
    setForm(emptyUserForm);
    setModalOpen(true);
  };

  const openEditModal = (user: AdminUserDto) => {
    setEditingUser(user);
    setForm({
      username: user.username,
      email: user.email || "",
      phoneNumber: user.phoneNumber || "",
      password: "",
      role: user.role,
      isActive: user.isActive ?? true,
      requiresPasswordChange: false,
    });
    setModalOpen(true);
  };

  const closeModal = () => {
    if (submitting) {
      return;
    }
    setModalOpen(false);
    setEditingUser(null);
    setForm(emptyUserForm);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setSubmitting(true);
      setError(null);

      if (editingUser) {
        const payload: AdminUserUpdateCommand = {
          username: form.username.trim(),
          email: form.email.trim() || undefined,
          phoneNumber: form.phoneNumber.trim() || undefined,
          password: form.password.trim() || undefined,
          role: form.role,
          isActive: form.isActive,
          requiresPasswordChange: form.requiresPasswordChange,
        };
        await workspacesApi.updateAdminUser(editingUser.id, payload);
      } else {
        const payload: AdminUserCreateCommand = {
          username: form.username.trim(),
          email: form.email.trim() || undefined,
          phoneNumber: form.phoneNumber.trim() || undefined,
          password: form.password,
          role: form.role,
          isActive: form.isActive,
          requiresPasswordChange: form.requiresPasswordChange,
        };
        await workspacesApi.createAdminUser(payload);
      }

      closeModal();
      await loadUsers();
    } catch (err) {
      setError(getErrorMessage(err, "Không thể lưu user"));
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (user: AdminUserDto) => {
    if (!window.confirm(`Xóa user "${user.username}"?`)) {
      return;
    }

    try {
      setError(null);
      await workspacesApi.deleteAdminUser(user.id);
      if (users.length === 1 && page > 0) {
        setPage((current) => current - 1);
      } else {
        await loadUsers();
      }
    } catch (err) {
      setError(getErrorMessage(err, "Không thể xóa user"));
    }
  };

  return (
    <div style={{ animation: "fadeIn 0.4s ease-out" }}>
      <p className="eyebrow">Quản trị hệ thống</p>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          gap: "16px",
          marginBottom: "24px",
          flexWrap: "wrap",
        }}
      >
        <div>
          <h1
            style={{
              fontSize: "2rem",
              margin: "8px 0 6px",
              color: "var(--text-primary)",
            }}
          >
            Users
          </h1>
          <div style={{ color: "var(--text-muted)" }}>
            {loading ? "Đang tải..." : `${totalItems} user trong hệ thống`}
          </div>
        </div>
        <button type="button" onClick={openCreateModal} style={primaryButton}>
          <Plus size={18} />
          Tạo user
        </button>
      </div>

      <UserFilters
        searchInput={searchInput}
        role={role}
        status={status}
        sortBy={sortBy}
        sortDir={sortDir}
        onSearchInputChange={setSearchInput}
        onRoleChange={(value) => {
          setRole(value);
          setPage(0);
        }}
        onStatusChange={(value) => {
          setStatus(value);
          setPage(0);
        }}
        onSortByChange={(value) => {
          setSortBy(value);
          setPage(0);
        }}
        onSortDirChange={(value) => {
          setSortDir(value);
          setPage(0);
        }}
        onApply={() => {
          setPage(0);
          setSearch(searchInput.trim());
        }}
        onReset={() => {
          setSearchInput("");
          setSearch("");
          setRole("ALL");
          setStatus("ALL");
          setSortBy("createdAt");
          setSortDir("desc");
          setPage(0);
          setSize(10);
        }}
      />

      <div className="card" style={{ padding: "24px" }}>
        {loading && <div>Đang tải users...</div>}
        {error && (
          <div style={{ color: "var(--error-color)", marginBottom: "12px" }}>
            {error}
          </div>
        )}
        {!loading && !error && (
          <>
            <UsersTable
              users={users}
              onEdit={openEditModal}
              onDelete={(user) => void handleDelete(user)}
            />

            {users.length === 0 && (
              <div style={{ marginTop: "16px", color: "var(--text-muted)" }}>
                Không có user nào khớp bộ lọc hiện tại.
              </div>
            )}

            <PaginationBar
              page={page}
              totalPages={totalPages}
              size={size}
              onPageChange={setPage}
              onSizeChange={(next) => {
                setSize(next);
                setPage(0);
              }}
            />
          </>
        )}
      </div>

      {modalOpen && (
        <UserModal
          editingUser={editingUser}
          form={form}
          submitting={submitting}
          onChange={setForm}
          onClose={closeModal}
          onSubmit={handleSubmit}
        />
      )}
    </div>
  );
};

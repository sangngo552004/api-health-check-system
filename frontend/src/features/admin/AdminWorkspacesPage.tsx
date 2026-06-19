import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Plus } from "lucide-react";
import { useAuth } from "../../context/useAuth";
import { useToast } from "../../context/useToast";
import { workspacesApi } from "../../services/api/workspaces.api";
import {
  AdminWorkspaceCreateCommand,
  AdminUserDto,
  AdminWorkspaceUpdateCommand,
  WorkspaceDto,
  WorkspaceMemberDto,
} from "../../types/workspace.types";
import { getErrorMessage } from "../../utils/error";
import { PaginationBar } from "./components/adminUi";
import { primaryButton } from "./components/adminStyles";
import { WorkspaceCard } from "./workspaces/WorkspaceCard";
import { WorkspaceFilters } from "./workspaces/WorkspaceFilters";
import { WorkspaceModal } from "./workspaces/WorkspaceModal";
import {
  emptyWorkspaceForm,
  WorkspaceFormState,
} from "./workspaces/workspacePage.types";
import { generateWorkspaceSlug } from "./workspaces/workspaceSlug";

export const AdminWorkspacesPage: React.FC = () => {
  const { user } = useAuth();
  const { showToast } = useToast();
  const [workspaces, setWorkspaces] = useState<WorkspaceDto[]>([]);
  const [availableUsers, setAvailableUsers] = useState<AdminUserDto[]>([]);
  const [members, setMembers] = useState<Record<number, WorkspaceMemberDto[]>>(
    {},
  );
  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searchInput, setSearchInput] = useState("");
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState<"ALL" | "ACTIVE" | "INACTIVE">("ALL");
  const [sortBy, setSortBy] = useState("createdAt");
  const [sortDir, setSortDir] = useState<"asc" | "desc">("desc");
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingWorkspace, setEditingWorkspace] = useState<WorkspaceDto | null>(
    null,
  );
  const [form, setForm] = useState<WorkspaceFormState>(emptyWorkspaceForm);
  const [memberSearch, setMemberSearch] = useState("");
  const [selectedMemberId, setSelectedMemberId] = useState<number | null>(null);
  const activeMembers = useMemo(
    () => (expandedId ? members[expandedId] || [] : []),
    [expandedId, members],
  );

  const loadWorkspaces = useCallback(async () => {
    try {
      setLoading(true);
      const response = await workspacesApi.getAllWorkspacesForAdmin({
        page,
        size,
        search: search || undefined,
        isActive: status === "ALL" ? undefined : status === "ACTIVE",
        sortBy,
        sortDir,
      });
      setWorkspaces(response.items);
      setTotalItems(response.totalItems);
      setTotalPages(response.totalPages);
      setError(null);
    } catch (err) {
      const message = getErrorMessage(err, "Không thể tải danh sách workspace");
      setError(message);
    } finally {
      setLoading(false);
    }
  }, [page, search, size, sortBy, sortDir, status]);

  useEffect(() => {
    void loadWorkspaces();
  }, [loadWorkspaces]);

  useEffect(() => {
    const loadUsers = async () => {
      try {
        const response = await workspacesApi.getAdminUsers({
          page: 0,
          size: 100,
          role: "USER",
          isActive: true,
          sortBy: "username",
          sortDir: "asc",
        });
        setAvailableUsers(response.items);
      } catch {
        setAvailableUsers([]);
      }
    };

    void loadUsers();
  }, []);

  const toggleMembers = async (workspaceId: number) => {
    if (expandedId === workspaceId) {
      setExpandedId(null);
      setMemberSearch("");
      setSelectedMemberId(null);
      return;
    }

    setExpandedId(workspaceId);
    setMemberSearch("");
    setSelectedMemberId(null);
    if (!members[workspaceId]) {
      try {
        const data = await workspacesApi.getMembers(workspaceId);
        setMembers((prev) => ({ ...prev, [workspaceId]: data }));
      } catch (err) {
        const message = getErrorMessage(err, "Không thể tải danh sách members");
        setError(message);
        showToast({
          title: "Tải danh sách thành viên thất bại",
          description: message,
          variant: "error",
        });
      }
    }
  };

  const openCreateModal = () => {
    setEditingWorkspace(null);
    setForm(emptyWorkspaceForm);
    setModalOpen(true);
  };

  const openEditModal = (workspace: WorkspaceDto) => {
    setEditingWorkspace(workspace);
    setForm({
      name: workspace.name,
      description: workspace.description || "",
      isActive: workspace.isActive,
    });
    setModalOpen(true);
  };

  const closeModal = () => {
    if (submitting) {
      return;
    }
    setModalOpen(false);
    setEditingWorkspace(null);
    setForm(emptyWorkspaceForm);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const normalizedName = form.name.trim();
    const normalizedDescription = form.description.trim() || undefined;
    const generatedSlug = generateWorkspaceSlug(normalizedName);

    if (!user?.id) {
      const message = "Không xác định được tài khoản admin hiện tại để tạo workspace.";
      setError(message);
      showToast({
        title: "Lưu workspace thất bại",
        description: message,
        variant: "error",
      });
      return;
    }

    try {
      setSubmitting(true);
      setError(null);

      if (editingWorkspace) {
        const payload: AdminWorkspaceUpdateCommand = {
          name: normalizedName,
          description: normalizedDescription,
          slug: generatedSlug,
          ownerId: editingWorkspace.ownerId,
          isActive: form.isActive,
        };
        await workspacesApi.updateAdminWorkspace(editingWorkspace.id, payload);
        showToast({
          title: "Cập nhật workspace thành công",
          description: `Workspace ${payload.name} đã được cập nhật.`,
          variant: "success",
        });
      } else {
        const payload: AdminWorkspaceCreateCommand = {
          name: normalizedName,
          description: normalizedDescription,
          slug: generatedSlug,
          ownerId: user.id,
          isActive: form.isActive,
        };
        await workspacesApi.createAdminWorkspace(payload);
        showToast({
          title: "Tạo workspace thành công",
          description: `Workspace ${payload.name} đã được tạo với slug ${payload.slug}.`,
          variant: "success",
        });
      }

      closeModal();
      await loadWorkspaces();
    } catch (err) {
      const message = getErrorMessage(err, "Không thể lưu workspace");
      setError(message);
      showToast({
        title: "Lưu workspace thất bại",
        description: message,
        variant: "error",
      });
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (workspaceId: number) => {
    if (!window.confirm("Xóa workspace này?")) {
      return;
    }
    try {
      setError(null);
      await workspacesApi.deleteAdminWorkspace(workspaceId);
      showToast({
        title: "Xóa workspace thành công",
        description: "Workspace đã được xóa khỏi hệ thống.",
        variant: "success",
      });
      if (expandedId === workspaceId) {
        setExpandedId(null);
      }
      if (workspaces.length === 1 && page > 0) {
        setPage((current) => current - 1);
      } else {
        await loadWorkspaces();
      }
    } catch (err) {
      const message = getErrorMessage(err, "Không thể xóa workspace");
      setError(message);
      showToast({
        title: "Xóa workspace thất bại",
        description: message,
        variant: "error",
      });
    }
  };

  const handleAddMember = async () => {
    if (!expandedId || !selectedMemberId) {
      return;
    }
    try {
      setError(null);
      await workspacesApi.addMember(expandedId, selectedMemberId);
      const data = await workspacesApi.getMembers(expandedId);
      setMembers((prev) => ({ ...prev, [expandedId]: data }));
      const addedUser =
        availableUsers.find((user) => user.id === selectedMemberId) ?? null;
      setMemberSearch("");
      setSelectedMemberId(null);
      showToast({
        title: "Thêm thành viên thành công",
        description: addedUser
          ? `${addedUser.username} đã được thêm vào workspace.`
          : "Thành viên đã được thêm vào workspace.",
        variant: "success",
      });
    } catch (err) {
      const message = getErrorMessage(err, "Không thể thêm member");
      setError(message);
      showToast({
        title: "Thêm thành viên thất bại",
        description: message,
        variant: "error",
      });
    }
  };

  const handleRemoveMember = async (workspaceId: number, userId: number) => {
    try {
      setError(null);
      await workspacesApi.removeMember(workspaceId, userId);
      const data = await workspacesApi.getMembers(workspaceId);
      setMembers((prev) => ({ ...prev, [workspaceId]: data }));
      const removedMember =
        activeMembers.find((member) => member.userId === userId) ?? null;
      showToast({
        title: "Xóa thành viên thành công",
        description: removedMember
          ? `${removedMember.username} đã bị xóa khỏi workspace.`
          : "Thành viên đã bị xóa khỏi workspace.",
        variant: "success",
      });
    } catch (err) {
      const message = getErrorMessage(err, "Không thể xóa member");
      setError(message);
      showToast({
        title: "Xóa thành viên thất bại",
        description: message,
        variant: "error",
      });
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
            Workspace
          </h1>
          <div style={{ color: "var(--text-muted)" }}>
            {loading ? "Đang tải..." : `${totalItems} workspace trong hệ thống`}
          </div>
        </div>
        <button type="button" onClick={openCreateModal} style={primaryButton}>
          <Plus size={18} />
          Tạo workspace
        </button>
      </div>

      <WorkspaceFilters
        searchInput={searchInput}
        status={status}
        sortBy={sortBy}
        sortDir={sortDir}
        onSearchInputChange={setSearchInput}
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
          setStatus("ALL");
          setSortBy("createdAt");
          setSortDir("desc");
          setPage(0);
          setSize(10);
        }}
      />

      <div className="card" style={{ padding: "24px" }}>
        {loading && <div>Đang tải workspaces...</div>}
        {error && (
          <div style={{ color: "var(--error-color)", marginBottom: "12px" }}>
            {error}
          </div>
        )}
        {!loading && !error && (
          <>
            <div style={{ display: "grid", gap: "16px" }}>
              {workspaces.map((workspace) => (
                <WorkspaceCard
                  key={workspace.id}
                  workspace={workspace}
                  isExpanded={expandedId === workspace.id}
                  activeMembers={
                    expandedId === workspace.id ? activeMembers : []
                  }
                  availableUsers={availableUsers}
                  memberSearch={memberSearch}
                  selectedMemberId={selectedMemberId}
                  onToggleMembers={(workspaceId) =>
                    void toggleMembers(workspaceId)
                  }
                  onEdit={openEditModal}
                  onDelete={(workspaceId) => void handleDelete(workspaceId)}
                  onMemberSearchChange={setMemberSearch}
                  onSelectMember={setSelectedMemberId}
                  onAddMember={() => void handleAddMember()}
                  onRemoveMember={(workspaceId, userId) =>
                    void handleRemoveMember(workspaceId, userId)
                  }
                />
              ))}
            </div>

            {workspaces.length === 0 && (
              <div style={{ marginTop: "16px", color: "var(--text-muted)" }}>
                Không có workspace nào khớp bộ lọc hiện tại.
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
        <WorkspaceModal
          editingWorkspace={editingWorkspace}
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

import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Plus } from "lucide-react";
import { workspacesApi } from "../../services/api/workspaces.api";
import {
  AdminWorkspaceCreateCommand,
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

export const AdminWorkspacesPage: React.FC = () => {
  const [workspaces, setWorkspaces] = useState<WorkspaceDto[]>([]);
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
  const [ownerIdFilter, setOwnerIdFilter] = useState("");
  const [ownerId, setOwnerId] = useState<number | undefined>(undefined);
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
  const [memberUserId, setMemberUserId] = useState("");
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
        ownerId,
        sortBy,
        sortDir,
      });
      setWorkspaces(response.items);
      setTotalItems(response.totalItems);
      setTotalPages(response.totalPages);
      setError(null);
    } catch (err) {
      setError(getErrorMessage(err, "Không thể tải danh sách workspace"));
    } finally {
      setLoading(false);
    }
  }, [ownerId, page, search, size, sortBy, sortDir, status]);

  useEffect(() => {
    void loadWorkspaces();
  }, [loadWorkspaces]);

  const toggleMembers = async (workspaceId: number) => {
    if (expandedId === workspaceId) {
      setExpandedId(null);
      return;
    }

    setExpandedId(workspaceId);
    if (!members[workspaceId]) {
      try {
        const data = await workspacesApi.getMembers(workspaceId);
        setMembers((prev) => ({ ...prev, [workspaceId]: data }));
      } catch (err) {
        setError(getErrorMessage(err, "Không thể tải danh sách members"));
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
      slug: workspace.slug,
      ownerId: String(workspace.ownerId),
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
    try {
      setSubmitting(true);
      setError(null);
      const parsedOwnerId = Number(form.ownerId);

      if (editingWorkspace) {
        const payload: AdminWorkspaceUpdateCommand = {
          name: form.name.trim(),
          description: form.description.trim() || undefined,
          slug: form.slug.trim(),
          ownerId: parsedOwnerId,
          isActive: form.isActive,
        };
        await workspacesApi.updateAdminWorkspace(editingWorkspace.id, payload);
      } else {
        const payload: AdminWorkspaceCreateCommand = {
          name: form.name.trim(),
          description: form.description.trim() || undefined,
          slug: form.slug.trim(),
          ownerId: parsedOwnerId,
          isActive: form.isActive,
        };
        await workspacesApi.createAdminWorkspace(payload);
      }

      closeModal();
      await loadWorkspaces();
    } catch (err) {
      setError(getErrorMessage(err, "Không thể lưu workspace"));
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
      if (expandedId === workspaceId) {
        setExpandedId(null);
      }
      if (workspaces.length === 1 && page > 0) {
        setPage((current) => current - 1);
      } else {
        await loadWorkspaces();
      }
    } catch (err) {
      setError(getErrorMessage(err, "Không thể xóa workspace"));
    }
  };

  const handleAddMember = async () => {
    if (!expandedId || !memberUserId) {
      return;
    }
    try {
      setError(null);
      await workspacesApi.addMember(expandedId, Number(memberUserId));
      const data = await workspacesApi.getMembers(expandedId);
      setMembers((prev) => ({ ...prev, [expandedId]: data }));
      setMemberUserId("");
    } catch (err) {
      setError(getErrorMessage(err, "Không thể thêm member"));
    }
  };

  const handleRemoveMember = async (workspaceId: number, userId: number) => {
    try {
      setError(null);
      await workspacesApi.removeMember(workspaceId, userId);
      const data = await workspacesApi.getMembers(workspaceId);
      setMembers((prev) => ({ ...prev, [workspaceId]: data }));
    } catch (err) {
      setError(getErrorMessage(err, "Không thể xóa member"));
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
            Workspaces
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
        ownerIdFilter={ownerIdFilter}
        status={status}
        sortBy={sortBy}
        sortDir={sortDir}
        onSearchInputChange={setSearchInput}
        onOwnerIdFilterChange={setOwnerIdFilter}
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
          setOwnerId(
            ownerIdFilter.trim() ? Number(ownerIdFilter.trim()) : undefined,
          );
        }}
        onReset={() => {
          setSearchInput("");
          setSearch("");
          setOwnerIdFilter("");
          setOwnerId(undefined);
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
                  memberUserId={memberUserId}
                  onToggleMembers={(workspaceId) =>
                    void toggleMembers(workspaceId)
                  }
                  onEdit={openEditModal}
                  onDelete={(workspaceId) => void handleDelete(workspaceId)}
                  onMemberUserIdChange={setMemberUserId}
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

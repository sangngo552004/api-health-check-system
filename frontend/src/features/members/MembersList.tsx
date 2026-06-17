import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useAuth } from "../../context/useAuth";
import { useWorkspace } from "../../context/useWorkspace";
import { useMemberStore } from "../../store/useMemberStore";
import { getErrorMessage } from "../../utils/error";
import { MemberForm } from "./MemberForm";
import { MembersTable } from "./MembersTable";
import { MembersToolbar } from "./MembersToolbar";

export const MembersList: React.FC = () => {
  const { members, loading, fetchMembers, addMember, removeMember } =
    useMemberStore();
  const { activeWorkspace } = useWorkspace();
  const { user: currentUser } = useAuth();
  const { t } = useTranslation();

  const [searchTerm, setSearchTerm] = useState("");
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (activeWorkspace) {
      void fetchMembers(activeWorkspace.id);
    }
  }, [activeWorkspace, fetchMembers]);

  const filteredMembers = members.filter(
    (member) =>
      member.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
      member.email.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  const handleAddSubmit = async (userId: number) => {
    if (!activeWorkspace) {
      return;
    }

    setSubmitting(true);
    try {
      await addMember(activeWorkspace.id, userId);
      setIsFormOpen(false);
    } catch (error) {
      alert("Khong the them thanh vien: " + getErrorMessage(error));
    } finally {
      setSubmitting(false);
    }
  };

  const handleRemove = (userId: number) => {
    if (!activeWorkspace) {
      return;
    }

    if (window.confirm("Ban co chac muon xoa thanh vien nay khoi Workspace?")) {
      removeMember(activeWorkspace.id, userId);
    }
  };

  if (!activeWorkspace) {
    return (
      <div style={{ color: "var(--text-muted)" }}>Vui long chon Workspace.</div>
    );
  }

  return (
    <div style={{ animation: "fadeIn 0.5s ease-out" }}>
      <MembersToolbar
        searchTerm={searchTerm}
        onSearchTermChange={setSearchTerm}
        onOpenInvite={() => setIsFormOpen(true)}
        addLabel={t("members.addBtn", "Moi thanh vien")}
        subtitle={t("members.subtitle", "Cau hinh Workspace")}
        title={t("members.title", "Workspace Members")}
        searchPlaceholder={t(
          "members.search",
          "Tim kiem theo ten hoac Email...",
        )}
      />

      <MembersTable
        loading={loading}
        members={filteredMembers}
        currentUserId={currentUser?.id}
        onRemove={handleRemove}
      />

      {isFormOpen && (
        <MemberForm
          loading={submitting}
          onSubmit={handleAddSubmit}
          onCancel={() => setIsFormOpen(false)}
        />
      )}
    </div>
  );
};

import React, { useEffect, useState } from "react";
import { useToast } from "../../context/useToast";
import { useContactStore } from "../../store/useContactStore";
import {
  ContactGroupCreateCommand,
  ContactGroupDto,
  ContactGroupUpdateCommand,
} from "../../types/contact.types";
import { getErrorMessage } from "../../utils/error";
import { ContactForm, ContactFormData } from "./ContactForm";
import { ContactsTable } from "./ContactsTable";
import { ContactsToolbar } from "./ContactsToolbar";

export const ContactsList: React.FC = () => {
  const {
    contactGroups,
    loading,
    fetchContactGroups,
    createContactGroup,
    updateContactGroup,
    deleteContactGroup,
  } = useContactStore();
  const { showToast } = useToast();
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [sortBy, setSortBy] = useState("createdAt");
  const [sortDir, setSortDir] = useState<"asc" | "desc">("desc");
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingContact, setEditingContact] =
    useState<ContactGroupUpdateCommand | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    void fetchContactGroups({
      page: 0,
      size: 100,
      search: searchTerm.trim() || undefined,
      isActive:
        statusFilter === "" ? undefined : statusFilter === "true",
      sortBy,
      sortDir,
    });
  }, [fetchContactGroups, searchTerm, sortBy, sortDir, statusFilter]);

  const handleAdd = () => {
    setEditingContact(null);
    setIsFormOpen(true);
  };

  const handleEdit = (contact: ContactGroupDto) => {
    setEditingContact({
      id: contact.id,
      name: contact.name,
      description: contact.description,
      isActive: contact.isActive,
      emailAddresses: contact.emailAddresses,
    });
    setIsFormOpen(true);
  };

  const handleDelete = async (contactId: number) => {
    if (!window.confirm("Ban co chac muon xoa Nhom lien he nay?")) {
      return;
    }

    try {
      await deleteContactGroup(contactId);
      showToast({
        title: "Xóa nhóm liên hệ thành công",
        description: "Nhóm liên hệ đã được xóa.",
        variant: "success",
      });
    } catch (error) {
      showToast({
        title: "Xóa nhóm liên hệ thất bại",
        description: getErrorMessage(error),
        variant: "error",
      });
    }
  };

  const handleFormSubmit = async (data: ContactFormData) => {
    setSubmitting(true);
    try {
      if (editingContact) {
        const payload: ContactGroupUpdateCommand = {
          ...data,
          id: editingContact.id,
          isActive: data.isActive,
        };
        await updateContactGroup(editingContact.id, payload);
        showToast({
          title: "Cập nhật nhóm liên hệ thành công",
          description: `Nhóm ${data.name} đã được cập nhật.`,
          variant: "success",
        });
      } else {
        const payload: ContactGroupCreateCommand = {
          name: data.name,
          description: data.description,
          emailAddresses: data.emailAddresses,
        };
        await createContactGroup(payload);
        showToast({
          title: "Tạo nhóm liên hệ thành công",
          description: `Nhóm ${data.name} đã được tạo.`,
          variant: "success",
        });
      }
      setIsFormOpen(false);
    } catch (error) {
      showToast({
        title: editingContact
          ? "Cập nhật nhóm liên hệ thất bại"
          : "Tạo nhóm liên hệ thất bại",
        description: getErrorMessage(error),
        variant: "error",
      });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ animation: "fadeIn 0.5s ease-out" }}>
      <ContactsToolbar
        searchTerm={searchTerm}
        onSearchTermChange={setSearchTerm}
        onCreate={handleAdd}
        filters={
          <>
            <select
              value={statusFilter}
              onChange={(event) => setStatusFilter(event.target.value)}
              style={filterStyle}
            >
              <option value="">Tất cả trạng thái</option>
              <option value="true">Đang bật</option>
              <option value="false">Đang tắt</option>
            </select>
            <select
              value={sortBy}
              onChange={(event) => setSortBy(event.target.value)}
              style={filterStyle}
            >
              <option value="createdAt">Mới tạo gần đây</option>
              <option value="name">Tên nhóm</option>
            </select>
            <select
              value={sortDir}
              onChange={(event) =>
                setSortDir(event.target.value as "asc" | "desc")
              }
              style={filterStyle}
            >
              <option value="desc">Giảm dần</option>
              <option value="asc">Tăng dần</option>
            </select>
          </>
        }
      />

      <ContactsTable
        loading={loading}
        contacts={contactGroups}
        onEdit={handleEdit}
        onDelete={(contactId) => void handleDelete(contactId)}
      />

      {isFormOpen && (
        <ContactForm
          initialData={editingContact}
          loading={submitting}
          onSubmit={handleFormSubmit}
          onCancel={() => setIsFormOpen(false)}
        />
      )}
    </div>
  );
};

const filterStyle: React.CSSProperties = {
  minWidth: "170px",
  padding: "10px 14px",
  background: "var(--bg-secondary)",
  border: "1px solid var(--card-border)",
  borderRadius: "10px",
  color: "var(--text-primary)",
};

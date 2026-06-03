import React, { useEffect, useState } from "react";
import { useContactStore } from "../../store/useContactStore";
import {
  Users,
  Plus,
  Search,
  Edit2,
  Trash2,
  Activity,
  Mail,
  Link as LinkIcon,
  Zap,
} from "lucide-react";
import {
  ContactGroupCreateCommand,
  ContactGroupDto,
  ContactGroupUpdateCommand,
} from "../../types/contact.types";
import { ContactForm } from "./ContactForm";
import { getErrorMessage } from "../../utils/error";

export const ContactsList: React.FC = () => {
  const {
    contactGroups,
    loading,
    fetchContactGroups,
    createContactGroup,
    updateContactGroup,
    deleteContactGroup,
  } = useContactStore();
  const [searchTerm, setSearchTerm] = useState("");

  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingContact, setEditingContact] =
    useState<ContactGroupUpdateCommand | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    void fetchContactGroups(0, 100);
  }, [fetchContactGroups]);

  const filteredContacts = contactGroups.filter((c) =>
    c.name.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  const handleAdd = () => {
    setEditingContact(null);
    setIsFormOpen(true);
  };

  const handleEdit = (c: ContactGroupDto) => {
    setEditingContact({
      id: c.id,
      name: c.name,
      description: c.description,
      isActive: c.isActive,
      userIds: c.userIds,
      emailAddresses: c.emailAddresses,
      webhookUrls: c.webhookUrls,
    });
    setIsFormOpen(true);
  };

  const handleFormSubmit = async (data: ContactGroupCreateCommand) => {
    setSubmitting(true);
    try {
      if (editingContact) {
        await updateContactGroup(editingContact.id, {
          ...data,
          id: editingContact.id,
        });
      } else {
        await createContactGroup(data);
      }
      setIsFormOpen(false);
    } catch (error) {
      // Thông báo lỗi đã được map tiếng việt từ api.ts nên chỉ cần in ra
      alert(getErrorMessage(error));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ animation: "fadeIn 0.5s ease-out" }}>
      {/* Header Section */}
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "flex-end",
          marginBottom: "32px",
        }}
      >
        <div>
          <p className="eyebrow">Quản lý nhận thông báo</p>
          <h1
            style={{
              color: "var(--text-primary)",
              fontSize: "2rem",
              fontWeight: 700,
              margin: "8px 0 0 0",
            }}
          >
            Contact Groups
          </h1>
        </div>
        <button
          onClick={handleAdd}
          style={{
            display: "flex",
            alignItems: "center",
            gap: "8px",
            background: "linear-gradient(135deg, #10b981 0%, #047857 100%)",
            border: "none",
            color: "#fff",
            padding: "12px 20px",
            borderRadius: "12px",
            fontWeight: 600,
            cursor: "pointer",
            boxShadow: "0 4px 15px rgba(16, 185, 129, 0.3)",
            transition: "all 0.2s",
          }}
          onMouseOver={(e) =>
            (e.currentTarget.style.transform = "translateY(-2px)")
          }
          onMouseOut={(e) => (e.currentTarget.style.transform = "none")}
        >
          <Plus size={18} />
          Tạo Contact Group
        </button>
      </div>

      {/* Toolbar */}
      <div
        className="card"
        style={{
          marginBottom: "24px",
          padding: "16px 24px",
          display: "flex",
          gap: "16px",
        }}
      >
        <div style={{ position: "relative", flex: 1, maxWidth: "400px" }}>
          <Search
            size={18}
            style={{
              position: "absolute",
              left: "14px",
              top: "50%",
              transform: "translateY(-50%)",
              color: "var(--text-muted)",
            }}
          />
          <input
            type="text"
            placeholder="Tìm kiếm nhóm..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{
              width: "100%",
              padding: "10px 16px 10px 42px",
              background: "var(--bg-secondary)",
              border: "1px solid var(--card-border)",
              borderRadius: "10px",
              color: "var(--text-primary)",
              outline: "none",
            }}
          />
        </div>
      </div>

      {/* Table */}
      <div className="card" style={{ padding: 0, overflow: "hidden" }}>
        <table
          style={{
            width: "100%",
            borderCollapse: "collapse",
            textAlign: "left",
          }}
        >
          <thead>
            <tr
              style={{
                borderBottom: "1px solid var(--card-border)",
                background: "var(--bg-secondary)",
              }}
            >
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                Tên nhóm
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                Phương thức nhận
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                Trạng thái
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                  textAlign: "right",
                }}
              >
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td
                  colSpan={4}
                  style={{
                    padding: "40px",
                    textAlign: "center",
                    color: "var(--text-muted)",
                  }}
                >
                  <Activity
                    size={24}
                    className="spin"
                    style={{ margin: "0 auto 12px" }}
                  />
                  Đang tải danh sách...
                </td>
              </tr>
            ) : filteredContacts.length === 0 ? (
              <tr>
                <td
                  colSpan={4}
                  style={{
                    padding: "40px",
                    textAlign: "center",
                    color: "var(--text-muted)",
                  }}
                >
                  Chưa có Contact Group nào. Hãy tạo một cái!
                </td>
              </tr>
            ) : (
              filteredContacts.map((c) => (
                <tr
                  key={c.id}
                  style={{
                    borderBottom: "1px solid var(--card-border)",
                    transition: "background 0.2s",
                  }}
                >
                  <td style={{ padding: "16px 24px" }}>
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "12px",
                      }}
                    >
                      <div
                        style={{
                          width: "40px",
                          height: "40px",
                          borderRadius: "10px",
                          background: "rgba(16, 185, 129, 0.1)",
                          color: "#10b981",
                          display: "flex",
                          alignItems: "center",
                          justifyContent: "center",
                        }}
                      >
                        <Users size={20} />
                      </div>
                      <div>
                        <div
                          style={{
                            fontWeight: 600,
                            color: "var(--text-primary)",
                            marginBottom: "4px",
                          }}
                        >
                          {c.name}
                        </div>
                        <div
                          style={{
                            fontSize: "0.8rem",
                            color: "var(--text-muted)",
                          }}
                        >
                          {c.description || "Không có mô tả"}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td style={{ padding: "16px 24px" }}>
                    <div
                      style={{
                        display: "flex",
                        flexDirection: "column",
                        gap: "6px",
                      }}
                    >
                      {c.emailAddresses.length > 0 && (
                        <span
                          style={{
                            display: "flex",
                            alignItems: "center",
                            gap: "6px",
                            fontSize: "0.8rem",
                            color: "var(--text-secondary)",
                          }}
                        >
                          <Mail size={14} /> {c.emailAddresses.length} Emails
                        </span>
                      )}
                      {c.webhookUrls.length > 0 && (
                        <span
                          style={{
                            display: "flex",
                            alignItems: "center",
                            gap: "6px",
                            fontSize: "0.8rem",
                            color: "var(--text-secondary)",
                          }}
                        >
                          <LinkIcon size={14} /> {c.webhookUrls.length} Webhooks
                        </span>
                      )}
                      {c.userIds.length > 0 && (
                        <span
                          style={{
                            display: "flex",
                            alignItems: "center",
                            gap: "6px",
                            fontSize: "0.8rem",
                            color: "var(--text-secondary)",
                          }}
                        >
                          <Users size={14} /> {c.userIds.length} Hệ thống User
                        </span>
                      )}
                      {c.emailAddresses.length === 0 &&
                        c.webhookUrls.length === 0 &&
                        c.userIds.length === 0 && (
                          <span
                            style={{
                              fontSize: "0.8rem",
                              color: "var(--text-muted)",
                            }}
                          >
                            Chưa cấu hình
                          </span>
                        )}
                    </div>
                  </td>
                  <td style={{ padding: "16px 24px" }}>
                    <span
                      style={{
                        display: "inline-flex",
                        alignItems: "center",
                        gap: "4px",
                        padding: "4px 8px",
                        borderRadius: "6px",
                        fontSize: "0.75rem",
                        fontWeight: 600,
                        color: c.isActive
                          ? "var(--success-color)"
                          : "var(--text-muted)",
                        background: c.isActive
                          ? "rgba(16, 185, 129, 0.1)"
                          : "var(--bg-secondary)",
                      }}
                    >
                      <Zap size={12} />
                      {c.isActive ? "Hoạt động" : "Tắt"}
                    </span>
                  </td>
                  <td style={{ padding: "16px 24px", textAlign: "right" }}>
                    <div
                      style={{
                        display: "flex",
                        gap: "8px",
                        justifyContent: "flex-end",
                      }}
                    >
                      <button
                        onClick={() => handleEdit(c)}
                        style={{
                          background: "none",
                          border: "none",
                          color: "var(--accent-color)",
                          cursor: "pointer",
                          padding: "6px",
                        }}
                        title="Chỉnh sửa"
                      >
                        <Edit2 size={18} />
                      </button>
                      <button
                        onClick={() => {
                          if (
                            window.confirm(
                              "Bạn có chắc muốn xoá Nhóm liên hệ này?",
                            )
                          ) {
                            deleteContactGroup(c.id);
                          }
                        }}
                        style={{
                          background: "none",
                          border: "none",
                          color: "var(--error-color)",
                          cursor: "pointer",
                          padding: "6px",
                        }}
                        title="Xóa"
                      >
                        <Trash2 size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

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

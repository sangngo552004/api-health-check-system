import React from "react";
import {
  Activity,
  Edit2,
  Link as LinkIcon,
  Mail,
  Trash2,
  Users,
  Zap,
} from "lucide-react";
import { ContactGroupDto } from "../../types/contact.types";

export const ContactsTable: React.FC<{
  loading: boolean;
  contacts: ContactGroupDto[];
  onEdit: (contact: ContactGroupDto) => void;
  onDelete: (contactId: number) => void;
}> = ({ loading, contacts, onEdit, onDelete }) => (
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
          <th style={thStyle}>Ten nhom</th>
          <th style={thStyle}>Phuong thuc nhan</th>
          <th style={thStyle}>Trang thai</th>
          <th style={{ ...thStyle, textAlign: "right" }}>Thao tac</th>
        </tr>
      </thead>
      <tbody>
        {loading ? (
          <tr>
            <td colSpan={4} style={emptyCellStyle}>
              <Activity
                size={24}
                className="spin"
                style={{ margin: "0 auto 12px" }}
              />
              Dang tai danh sach...
            </td>
          </tr>
        ) : contacts.length === 0 ? (
          <tr>
            <td colSpan={4} style={emptyCellStyle}>
              Chua co Contact Group nao. Hay tao mot cai!
            </td>
          </tr>
        ) : (
          contacts.map((contact) => (
            <tr
              key={contact.id}
              style={{
                borderBottom: "1px solid var(--card-border)",
                transition: "background 0.2s",
              }}
            >
              <td style={{ padding: "16px 24px" }}>
                <div
                  style={{ display: "flex", alignItems: "center", gap: "12px" }}
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
                      {contact.name}
                    </div>
                    <div
                      style={{ fontSize: "0.8rem", color: "var(--text-muted)" }}
                    >
                      {contact.description || "Khong co mo ta"}
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
                  {contact.emailAddresses.length > 0 && (
                    <span style={channelStyle}>
                      <Mail size={14} /> {contact.emailAddresses.length} Emails
                    </span>
                  )}
                  {contact.webhookUrls.length > 0 && (
                    <span style={channelStyle}>
                      <LinkIcon size={14} /> {contact.webhookUrls.length}{" "}
                      Webhooks
                    </span>
                  )}
                  {contact.userIds.length > 0 && (
                    <span style={channelStyle}>
                      <Users size={14} /> {contact.userIds.length} He thong User
                    </span>
                  )}
                  {contact.emailAddresses.length === 0 &&
                    contact.webhookUrls.length === 0 &&
                    contact.userIds.length === 0 && (
                      <span
                        style={{
                          fontSize: "0.8rem",
                          color: "var(--text-muted)",
                        }}
                      >
                        Chua cau hinh
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
                    color: contact.isActive
                      ? "var(--success-color)"
                      : "var(--text-muted)",
                    background: contact.isActive
                      ? "rgba(16, 185, 129, 0.1)"
                      : "var(--bg-secondary)",
                  }}
                >
                  <Zap size={12} />
                  {contact.isActive ? "Hoat dong" : "Tat"}
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
                    onClick={() => onEdit(contact)}
                    style={iconButton("var(--accent-color)")}
                    title="Chinh sua"
                  >
                    <Edit2 size={18} />
                  </button>
                  <button
                    onClick={() => onDelete(contact.id)}
                    style={iconButton("var(--error-color)")}
                    title="Xoa"
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
);

const thStyle: React.CSSProperties = {
  padding: "16px 24px",
  color: "var(--text-muted)",
  fontWeight: 600,
  fontSize: "0.85rem",
};

const emptyCellStyle: React.CSSProperties = {
  padding: "40px",
  textAlign: "center",
  color: "var(--text-muted)",
};

const channelStyle: React.CSSProperties = {
  display: "flex",
  alignItems: "center",
  gap: "6px",
  fontSize: "0.8rem",
  color: "var(--text-secondary)",
};

const iconButton = (color: string): React.CSSProperties => ({
  background: "none",
  border: "none",
  color,
  cursor: "pointer",
  padding: "6px",
});

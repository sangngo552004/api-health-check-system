import React, { useEffect, useState } from "react";
import { useAlertStore } from "../../store/useAlertStore";
import {
  BellRing,
  Plus,
  Search,
  Edit2,
  Trash2,
  Activity,
  Zap,
} from "lucide-react";
import {
  AlertRuleCreateCommand,
  AlertRuleDto,
  AlertRuleUpdateCommand,
} from "../../types/alert.types";
import { AlertForm } from "./AlertForm";
import { getErrorMessage } from "../../utils/error";

export const AlertsList: React.FC = () => {
  const {
    alertRules,
    loading,
    fetchAlertRules,
    createAlertRule,
    updateAlertRule,
    deleteAlertRule,
  } = useAlertStore();
  const [searchTerm, setSearchTerm] = useState("");

  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingAlert, setEditingAlert] =
    useState<AlertRuleUpdateCommand | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    void fetchAlertRules(0, 100);
  }, [fetchAlertRules]);

  const filteredAlerts = alertRules.filter((a) =>
    a.name.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  const handleAdd = () => {
    setEditingAlert(null);
    setIsFormOpen(true);
  };

  const handleEdit = (a: AlertRuleDto) => {
    setEditingAlert({
      id: a.id,
      name: a.name,
      ruleType: a.ruleType,
      operator: a.operator,
      thresholdValue: a.thresholdValue,
      isActive: a.isActive,
      contactGroupIds: a.contactGroupIds,
      overrideDefaultContacts: a.overrideDefaultContacts,
    });
    setIsFormOpen(true);
  };

  const handleFormSubmit = async (data: AlertRuleCreateCommand) => {
    setSubmitting(true);
    try {
      if (editingAlert) {
        await updateAlertRule(editingAlert.id, {
          ...data,
          id: editingAlert.id,
        });
      } else {
        await createAlertRule(data);
      }
      setIsFormOpen(false);
    } catch (error) {
      alert("Có lỗi xảy ra: " + getErrorMessage(error));
    } finally {
      setSubmitting(false);
    }
  };

  const getRuleTypeLabel = (type: string) => {
    switch (type) {
      case "STATUS_CHANGE":
        return "Status";
      case "LATENCY_SPIKE":
        return "Latency";
      case "ERROR_RATE":
        return "Error Rate";
      default:
        return type;
    }
  };

  const getOperatorSymbol = (op: string) => {
    switch (op) {
      case "GREATER_THAN":
        return ">";
      case "LESS_THAN":
        return "<";
      case "EQUAL":
        return "=";
      default:
        return op;
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
          <p className="eyebrow">Quy tắc thông báo</p>
          <h1
            style={{
              color: "var(--text-primary)",
              fontSize: "2rem",
              fontWeight: 700,
              margin: "8px 0 0 0",
            }}
          >
            Alert Rules
          </h1>
        </div>
        <button
          onClick={handleAdd}
          style={{
            display: "flex",
            alignItems: "center",
            gap: "8px",
            background: "linear-gradient(135deg, #ef4444 0%, #b91c1c 100%)",
            border: "none",
            color: "#fff",
            padding: "12px 20px",
            borderRadius: "12px",
            fontWeight: 600,
            cursor: "pointer",
            boxShadow: "0 4px 15px rgba(239, 68, 68, 0.3)",
            transition: "all 0.2s",
          }}
          onMouseOver={(e) =>
            (e.currentTarget.style.transform = "translateY(-2px)")
          }
          onMouseOut={(e) => (e.currentTarget.style.transform = "none")}
        >
          <Plus size={18} />
          Tạo Alert Rule
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
            placeholder="Tìm kiếm quy tắc..."
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
                Tên Cảnh báo
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                Điều kiện
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
            ) : filteredAlerts.length === 0 ? (
              <tr>
                <td
                  colSpan={4}
                  style={{
                    padding: "40px",
                    textAlign: "center",
                    color: "var(--text-muted)",
                  }}
                >
                  Chưa có Alert Rule nào. Hãy tạo một cái!
                </td>
              </tr>
            ) : (
              filteredAlerts.map((a) => (
                <tr
                  key={a.id}
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
                          background: "rgba(239, 68, 68, 0.1)",
                          color: "#ef4444",
                          display: "flex",
                          alignItems: "center",
                          justifyContent: "center",
                        }}
                      >
                        <BellRing size={20} />
                      </div>
                      <div>
                        <div
                          style={{
                            fontWeight: 600,
                            color: "var(--text-primary)",
                            marginBottom: "4px",
                          }}
                        >
                          {a.name}
                        </div>
                        <div
                          style={{
                            fontSize: "0.8rem",
                            color: "var(--text-muted)",
                          }}
                        >
                          Groups:{" "}
                          {a.contactGroupIds.length > 0
                            ? a.contactGroupIds.join(", ")
                            : "Mặc định"}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td style={{ padding: "16px 24px" }}>
                    <span
                      style={{
                        fontSize: "0.85rem",
                        color: "var(--accent-color)",
                        background: "var(--accent-bg)",
                        padding: "4px 8px",
                        borderRadius: "6px",
                        fontWeight: 600,
                      }}
                    >
                      {getRuleTypeLabel(a.ruleType)}{" "}
                      {getOperatorSymbol(a.operator)} {a.thresholdValue}
                    </span>
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
                        color: a.isActive
                          ? "var(--success-color)"
                          : "var(--text-muted)",
                        background: a.isActive
                          ? "rgba(16, 185, 129, 0.1)"
                          : "var(--bg-secondary)",
                      }}
                    >
                      <Zap size={12} />
                      {a.isActive ? "Bật" : "Tắt"}
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
                        onClick={() => handleEdit(a)}
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
                              "Bạn có chắc muốn xoá Alert Rule này?",
                            )
                          ) {
                            deleteAlertRule(a.id);
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
        <AlertForm
          initialData={editingAlert}
          loading={submitting}
          onSubmit={handleFormSubmit}
          onCancel={() => setIsFormOpen(false)}
        />
      )}
    </div>
  );
};

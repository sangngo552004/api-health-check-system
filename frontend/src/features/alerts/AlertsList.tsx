import React, { useEffect, useState } from "react";
import { useToast } from "../../context/useToast";
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
import { AlertForm, AlertFormData } from "./AlertForm";
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
  const { showToast } = useToast();
  const [searchTerm, setSearchTerm] = useState("");
  const [ruleTypeFilter, setRuleTypeFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [sortBy, setSortBy] = useState("createdAt");
  const [sortDir, setSortDir] = useState<"asc" | "desc">("desc");

  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingAlert, setEditingAlert] =
    useState<AlertRuleUpdateCommand | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    void fetchAlertRules({
      page: 0,
      size: 100,
      search: searchTerm.trim() || undefined,
      ruleType: ruleTypeFilter || undefined,
      isActive:
        statusFilter === "" ? undefined : statusFilter === "true",
      sortBy,
      sortDir,
    });
  }, [fetchAlertRules, ruleTypeFilter, searchTerm, sortBy, sortDir, statusFilter]);

  const handleAdd = () => {
    setEditingAlert(null);
    setIsFormOpen(true);
  };

  const handleEdit = (rule: AlertRuleDto) => {
    setEditingAlert({
      id: rule.id,
      name: rule.name,
      ruleType: rule.ruleType,
      operator: rule.operator,
      thresholdValue: rule.thresholdValue,
      severity: rule.severity,
      isActive: rule.isActive,
      contactGroupIds: rule.contactGroupIds,
    });
    setIsFormOpen(true);
  };

  const handleDelete = async (rule: AlertRuleDto) => {
    if (!window.confirm("Bạn có chắc muốn xoá quy tắc cảnh báo này?")) {
      return;
    }

    try {
      await deleteAlertRule(rule.id);
      showToast({
        title: "Xóa quy tắc thành công",
        description: `Quy tắc ${rule.name} đã được xóa.`,
        variant: "success",
      });
    } catch (error) {
      showToast({
        title: "Xóa quy tắc thất bại",
        description: getErrorMessage(error),
        variant: "error",
      });
    }
  };

  const handleFormSubmit = async (data: AlertFormData) => {
    setSubmitting(true);
    try {
      if (editingAlert) {
        const payload: AlertRuleUpdateCommand = {
          id: editingAlert.id,
          name: data.name,
          ruleType: data.ruleType,
          operator: data.operator ?? null,
          thresholdValue: data.thresholdValue,
          severity: data.severity,
          isActive: data.isActive,
          contactGroupIds: data.contactGroupIds,
        };
        await updateAlertRule(editingAlert.id, payload);
        showToast({
          title: "Cập nhật quy tắc thành công",
          description: `Quy tắc ${data.name} đã được cập nhật.`,
          variant: "success",
        });
      } else {
        const payload: AlertRuleCreateCommand = {
          name: data.name,
          ruleType: data.ruleType,
          operator: data.operator ?? null,
          thresholdValue: data.thresholdValue,
          severity: data.severity,
          contactGroupIds: data.contactGroupIds,
        };
        await createAlertRule(payload);
        showToast({
          title: "Tạo quy tắc thành công",
          description: `Quy tắc ${data.name} đã được tạo.`,
          variant: "success",
        });
      }
      setIsFormOpen(false);
    } catch (error) {
      showToast({
        title: editingAlert
          ? "Cập nhật quy tắc thất bại"
          : "Tạo quy tắc thất bại",
        description: getErrorMessage(error),
        variant: "error",
      });
    } finally {
      setSubmitting(false);
    }
  };

  const getRuleTypeLabel = (type: string) => {
    switch (type) {
      case "CONSECUTIVE_FAILURE":
        return "Thất bại liên tiếp";
      case "RESPONSE_TIME":
        return "Thời gian phản hồi";
      case "HTTP_STATUS_CODE":
        return "HTTP status code";
      default:
        return type;
    }
  };

  const getOperatorSymbol = (op: string) => {
    switch (op) {
      case "GT":
        return ">";
      case "GTE":
        return ">=";
      case "LT":
        return "<";
      case "LTE":
        return "<=";
      case "EQ":
        return "=";
      case "NE":
        return "!=";
      default:
        return op;
    }
  };

  return (
    <div style={{ animation: "fadeIn 0.5s ease-out" }}>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "flex-end",
          marginBottom: "32px",
          gap: "16px",
          flexWrap: "wrap",
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
            Quy tắc cảnh báo
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
          }}
        >
          <Plus size={18} />
          Tạo quy tắc
        </button>
      </div>

      <div
        className="card"
        style={{
          marginBottom: "24px",
          padding: "16px 24px",
          display: "flex",
          gap: "16px",
          flexWrap: "wrap",
        }}
      >
        <div style={{ position: "relative", flex: 1, minWidth: "240px" }}>
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
            style={toolbarInputStyle}
          />
        </div>
        <select
          value={ruleTypeFilter}
          onChange={(event) => setRuleTypeFilter(event.target.value)}
          style={toolbarSelectStyle}
        >
          <option value="">Tất cả loại cảnh báo</option>
          <option value="CONSECUTIVE_FAILURE">Thất bại liên tiếp</option>
          <option value="RESPONSE_TIME">Thời gian phản hồi</option>
          <option value="HTTP_STATUS_CODE">HTTP status code</option>
        </select>
        <select
          value={statusFilter}
          onChange={(event) => setStatusFilter(event.target.value)}
          style={toolbarSelectStyle}
        >
          <option value="">Tất cả trạng thái</option>
          <option value="true">Đang bật</option>
          <option value="false">Đang tắt</option>
        </select>
        <select
          value={sortBy}
          onChange={(event) => setSortBy(event.target.value)}
          style={toolbarSelectStyle}
        >
          <option value="createdAt">Mới tạo gần đây</option>
          <option value="name">Tên quy tắc</option>
          <option value="thresholdValue">Ngưỡng giá trị</option>
        </select>
        <select
          value={sortDir}
          onChange={(event) => setSortDir(event.target.value as "asc" | "desc")}
          style={toolbarSelectStyle}
        >
          <option value="desc">Giảm dần</option>
          <option value="asc">Tăng dần</option>
        </select>
      </div>

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
              <th style={thStyle}>Quy tắc</th>
              <th style={thStyle}>Điều kiện</th>
              <th style={thStyle}>Trạng thái</th>
              <th style={{ ...thStyle, textAlign: "right" }}>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} style={loadingCellStyle}>
                  <Activity
                    size={24}
                    className="spin"
                    style={{ margin: "0 auto 12px" }}
                  />
                  Đang tải danh sách...
                </td>
              </tr>
            ) : alertRules.length === 0 ? (
              <tr>
                <td colSpan={4} style={loadingCellStyle}>
                  Chưa có quy tắc cảnh báo nào.
                </td>
              </tr>
            ) : (
              alertRules.map((rule) => (
                <tr
                  key={rule.id}
                  style={{
                    borderBottom: "1px solid var(--card-border)",
                    transition: "background 0.2s",
                  }}
                >
                  <td style={{ padding: "18px 24px" }}>
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "12px",
                      }}
                    >
                      <div
                        style={{
                          width: "42px",
                          height: "42px",
                          borderRadius: "12px",
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
                            fontWeight: 700,
                            color: "var(--text-primary)",
                            marginBottom: "4px",
                          }}
                        >
                          {rule.name}
                        </div>
                        <div
                          style={{
                            fontSize: "0.82rem",
                            color: "var(--text-muted)",
                          }}
                        >
                          {rule.contactGroupIds.length > 0
                            ? `Gửi cho ${rule.contactGroupIds.length} nhóm liên hệ`
                            : "Chưa cấu hình nhóm liên hệ"}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td style={{ padding: "18px 24px" }}>
                    <div
                      style={{
                        display: "inline-flex",
                        alignItems: "center",
                        gap: "6px",
                        padding: "8px 10px",
                        borderRadius: "10px",
                        fontSize: "0.85rem",
                        color: "var(--accent-color)",
                        background: "var(--accent-bg)",
                        fontWeight: 600,
                      }}
                    >
                      {getRuleTypeLabel(rule.ruleType)}
                      {rule.operator && <strong>{getOperatorSymbol(rule.operator)}</strong>}
                      {rule.thresholdValue}
                    </div>
                    <div
                      style={{
                        marginTop: "8px",
                        fontSize: "0.8rem",
                        color: "var(--text-muted)",
                      }}
                    >
                      Severity: <strong>{rule.severity}</strong>
                    </div>
                  </td>
                  <td style={{ padding: "18px 24px" }}>
                    <span
                      style={{
                        display: "inline-flex",
                        alignItems: "center",
                        gap: "4px",
                        padding: "6px 10px",
                        borderRadius: "999px",
                        fontSize: "0.78rem",
                        fontWeight: 700,
                        color: rule.isActive
                          ? "var(--success-color)"
                          : "var(--text-muted)",
                        background: rule.isActive
                          ? "rgba(16, 185, 129, 0.12)"
                          : "var(--bg-secondary)",
                      }}
                    >
                      <Zap size={12} />
                      {rule.isActive ? "Đang bật" : "Đang tắt"}
                    </span>
                  </td>
                  <td style={{ padding: "18px 24px", textAlign: "right" }}>
                    <div
                      style={{
                        display: "flex",
                        gap: "8px",
                        justifyContent: "flex-end",
                      }}
                    >
                      <button
                        onClick={() => handleEdit(rule)}
                        style={iconButtonStyle("var(--accent-color)")}
                        title="Chỉnh sửa"
                      >
                        <Edit2 size={18} />
                      </button>
                      <button
                        onClick={() => void handleDelete(rule)}
                        style={iconButtonStyle("var(--error-color)")}
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

const toolbarInputStyle: React.CSSProperties = {
  width: "100%",
  padding: "10px 16px 10px 42px",
  background: "var(--bg-secondary)",
  border: "1px solid var(--card-border)",
  borderRadius: "10px",
  color: "var(--text-primary)",
  outline: "none",
};

const toolbarSelectStyle: React.CSSProperties = {
  minWidth: "180px",
  padding: "10px 14px",
  background: "var(--bg-secondary)",
  border: "1px solid var(--card-border)",
  borderRadius: "10px",
  color: "var(--text-primary)",
};

const thStyle: React.CSSProperties = {
  padding: "16px 24px",
  color: "var(--text-muted)",
  fontWeight: 600,
  fontSize: "0.85rem",
};

const loadingCellStyle: React.CSSProperties = {
  padding: "40px",
  textAlign: "center",
  color: "var(--text-muted)",
};

const iconButtonStyle = (color: string): React.CSSProperties => ({
  background: "none",
  border: "none",
  color,
  cursor: "pointer",
  padding: "6px",
});

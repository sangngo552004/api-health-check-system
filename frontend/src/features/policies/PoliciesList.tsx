import React, { useEffect, useState } from "react";
import { usePolicyStore } from "../../store/usePolicyStore";
import { Plus, Search, Edit2, Trash2, Activity, Settings } from "lucide-react";
import {
  CheckPolicyCreateCommand,
  CheckPolicyDto,
  CheckPolicyUpdateCommand,
} from "../../types/policy.types";
import { PolicyForm } from "./PolicyForm";
import { getErrorMessage } from "../../utils/error";

export const PoliciesList: React.FC = () => {
  const {
    policies,
    loading,
    fetchPolicies,
    createPolicy,
    updatePolicy,
    deletePolicy,
  } = usePolicyStore();
  const [searchTerm, setSearchTerm] = useState("");

  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingPolicy, setEditingPolicy] =
    useState<CheckPolicyUpdateCommand | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    void fetchPolicies(0, 100);
  }, [fetchPolicies]);

  const filteredPolicies = policies.filter((p) =>
    p.name.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  const handleAdd = () => {
    setEditingPolicy(null);
    setIsFormOpen(true);
  };

  const handleEdit = (p: CheckPolicyDto) => {
    setEditingPolicy({
      id: p.id,
      name: p.name,
      intervalSeconds: p.intervalSeconds,
      timeoutMillis: p.timeoutMillis,
      retryCount: p.retryCount,
      failureThreshold: p.failureThreshold,
      latencyThresholdMillis: p.latencyThresholdMillis,
      expectedStatusCode: p.expectedStatusCode,
      expectedResponseBody: p.expectedResponseBody,
      responseRegex: p.responseRegex,
    });
    setIsFormOpen(true);
  };

  const handleFormSubmit = async (data: CheckPolicyCreateCommand) => {
    setSubmitting(true);
    try {
      if (editingPolicy) {
        await updatePolicy(editingPolicy.id, { ...data, id: editingPolicy.id });
      } else {
        await createPolicy(data);
      }
      setIsFormOpen(false);
    } catch (error) {
      alert("Có lỗi xảy ra: " + getErrorMessage(error));
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
          <p className="eyebrow">Cấu trúc kiểm tra mạng</p>
          <h1
            style={{
              color: "var(--text-primary)",
              fontSize: "2rem",
              fontWeight: 700,
              margin: "8px 0 0 0",
            }}
          >
            Check Policies
          </h1>
        </div>
        <button
          onClick={handleAdd}
          style={{
            display: "flex",
            alignItems: "center",
            gap: "8px",
            background: "linear-gradient(135deg, #f59e0b 0%, #d97706 100%)",
            border: "none",
            color: "#fff",
            padding: "12px 20px",
            borderRadius: "12px",
            fontWeight: 600,
            cursor: "pointer",
            boxShadow: "0 4px 15px rgba(245, 158, 11, 0.3)",
            transition: "all 0.2s",
          }}
          onMouseOver={(e) =>
            (e.currentTarget.style.transform = "translateY(-2px)")
          }
          onMouseOut={(e) => (e.currentTarget.style.transform = "none")}
        >
          <Plus size={18} />
          Tạo Policy
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
            placeholder="Tìm kiếm theo tên Policy..."
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
                Tên Policy
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                Chu kỳ
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                Ngưỡng sập
              </th>
              <th
                style={{
                  padding: "16px 24px",
                  color: "var(--text-muted)",
                  fontWeight: 600,
                  fontSize: "0.85rem",
                }}
              >
                Ngưỡng trễ
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
                  colSpan={5}
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
            ) : filteredPolicies.length === 0 ? (
              <tr>
                <td
                  colSpan={5}
                  style={{
                    padding: "40px",
                    textAlign: "center",
                    color: "var(--text-muted)",
                  }}
                >
                  Chưa có Policy nào. Hãy tạo một cái!
                </td>
              </tr>
            ) : (
              filteredPolicies.map((p) => (
                <tr
                  key={p.id}
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
                          background: "rgba(245, 158, 11, 0.1)",
                          color: "#f59e0b",
                          display: "flex",
                          alignItems: "center",
                          justifyContent: "center",
                        }}
                      >
                        <Settings size={20} />
                      </div>
                      <div
                        style={{
                          fontWeight: 600,
                          color: "var(--text-primary)",
                        }}
                      >
                        {p.name}
                      </div>
                    </div>
                  </td>
                  <td
                    style={{
                      padding: "16px 24px",
                      color: "var(--text-secondary)",
                    }}
                  >
                    Mỗi{" "}
                    <strong style={{ color: "var(--text-primary)" }}>
                      {p.intervalSeconds}s
                    </strong>
                  </td>
                  <td
                    style={{
                      padding: "16px 24px",
                      color: "var(--text-secondary)",
                    }}
                  >
                    Fail{" "}
                    <strong style={{ color: "var(--error-color)" }}>
                      {p.failureThreshold}
                    </strong>{" "}
                    lần (Timeout: {p.timeoutMillis}ms)
                  </td>
                  <td
                    style={{
                      padding: "16px 24px",
                      color: "var(--text-secondary)",
                    }}
                  >
                    <strong style={{ color: "var(--warning-color)" }}>
                      &gt; {p.latencyThresholdMillis}ms
                    </strong>
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
                        onClick={() => handleEdit(p)}
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
                              "Bạn có chắc muốn xoá Policy này? Các Endpoint dùng Policy này sẽ bị ảnh hưởng.",
                            )
                          ) {
                            deletePolicy(p.id);
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
        <PolicyForm
          initialData={editingPolicy}
          loading={submitting}
          onSubmit={handleFormSubmit}
          onCancel={() => setIsFormOpen(false)}
        />
      )}
    </div>
  );
};

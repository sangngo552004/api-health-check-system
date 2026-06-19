import React from "react";
import { X } from "lucide-react";
import { LabeledField } from "../components/adminUi";
import {
  closeButtonStyle,
  inputStyle,
  modalStyle,
  overlayStyle,
  primaryButton,
  secondaryButton,
  twoColumnGridStyle,
} from "../components/adminStyles";
import { WorkspaceModalProps } from "./workspacePage.types";
import { generateWorkspaceSlug } from "./workspaceSlug";

export const WorkspaceModal: React.FC<WorkspaceModalProps> = ({
  editingWorkspace,
  form,
  submitting,
  onChange,
  onClose,
  onSubmit,
}) => {
  const isEdit = Boolean(editingWorkspace);
  const generatedSlug = generateWorkspaceSlug(form.name);

  return (
    <div style={overlayStyle}>
      <div className="card" style={{ ...modalStyle, maxWidth: "760px" }}>
        <button onClick={onClose} style={closeButtonStyle} type="button">
          <X size={22} />
        </button>
        <p className="eyebrow">
          {isEdit ? "Cập nhật workspace" : "Tạo workspace mới"}
        </p>
        <h2 style={{ margin: "8px 0 24px", fontSize: "1.6rem" }}>
          {isEdit
            ? `Chỉnh sửa ${editingWorkspace?.name}`
            : "Thông tin workspace"}
        </h2>

        <form
          onSubmit={(e) => void onSubmit(e)}
          style={{ display: "grid", gap: "16px" }}
        >
          <div style={twoColumnGridStyle}>
            <LabeledField label="Tên workspace">
              <input
                value={form.name}
                onChange={(e) =>
                  onChange((prev) => ({ ...prev, name: e.target.value }))
                }
                style={inputStyle}
                required
              />
            </LabeledField>
            <LabeledField label="Trạng thái">
              <select
                value={String(form.isActive)}
                onChange={(e) =>
                  onChange((prev) => ({
                    ...prev,
                    isActive: e.target.value === "true",
                  }))
                }
                style={inputStyle}
              >
                <option value="true">Đang hoạt động</option>
                <option value="false">Ngưng hoạt động</option>
              </select>
            </LabeledField>
          </div>

          <LabeledField label="Slug sẽ được tạo tự động từ tên workspace">
            <div
              style={{
                ...inputStyle,
                minHeight: "46px",
                display: "flex",
                alignItems: "center",
                color: "var(--text-secondary)",
                background: "var(--bg-primary)",
              }}
            >
              {generatedSlug}
            </div>
          </LabeledField>

          <LabeledField label="Mô tả">
            <textarea
              value={form.description}
              onChange={(e) =>
                onChange((prev) => ({ ...prev, description: e.target.value }))
              }
              style={{ ...inputStyle, minHeight: "100px", resize: "vertical" }}
            />
          </LabeledField>

          <div
            style={{
              display: "flex",
              justifyContent: "flex-end",
              gap: "12px",
              marginTop: "8px",
            }}
          >
            <button type="button" onClick={onClose} style={secondaryButton}>
              Hủy
            </button>
            <button type="submit" disabled={submitting} style={primaryButton}>
              {submitting
                ? "Đang lưu..."
                : isEdit
                  ? "Cập nhật workspace"
                  : "Tạo workspace"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

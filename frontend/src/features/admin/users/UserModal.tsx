import React from "react";
import { X } from "lucide-react";
import { CheckboxField, LabeledField } from "../components/adminUi";
import {
  closeButtonStyle,
  inputStyle,
  modalStyle,
  overlayStyle,
  primaryButton,
  secondaryButton,
  twoColumnGridStyle,
} from "../components/adminStyles";
import { UserModalProps } from "./userPage.types";

export const UserModal: React.FC<UserModalProps> = ({
  editingUser,
  form,
  submitting,
  onChange,
  onClose,
  onSubmit,
}) => {
  const isEdit = Boolean(editingUser);

  return (
    <div style={overlayStyle}>
      <div className="card" style={{ ...modalStyle, maxWidth: "760px" }}>
        <button onClick={onClose} style={closeButtonStyle} type="button">
          <X size={22} />
        </button>
        <p className="eyebrow">{isEdit ? "Cập nhật user" : "Tạo user mới"}</p>
        <h2 style={{ margin: "8px 0 24px", fontSize: "1.6rem" }}>
          {isEdit ? `Chỉnh sửa ${editingUser?.username}` : "Thông tin user"}
        </h2>

        <form
          onSubmit={(e) => void onSubmit(e)}
          style={{ display: "grid", gap: "16px" }}
        >
          <div style={twoColumnGridStyle}>
            <LabeledField label="Username">
              <input
                value={form.username}
                onChange={(e) =>
                  onChange((prev) => ({ ...prev, username: e.target.value }))
                }
                style={inputStyle}
                required
              />
            </LabeledField>
            <LabeledField label="Email">
              <input
                type="email"
                value={form.email}
                onChange={(e) =>
                  onChange((prev) => ({ ...prev, email: e.target.value }))
                }
                style={inputStyle}
              />
            </LabeledField>
          </div>

          <div style={twoColumnGridStyle}>
            <LabeledField label="Phone number">
              <input
                value={form.phoneNumber}
                onChange={(e) =>
                  onChange((prev) => ({ ...prev, phoneNumber: e.target.value }))
                }
                style={inputStyle}
              />
            </LabeledField>
            <LabeledField label={isEdit ? "Password mới" : "Password"}>
              <input
                type="password"
                value={form.password}
                onChange={(e) =>
                  onChange((prev) => ({ ...prev, password: e.target.value }))
                }
                style={inputStyle}
                required={!isEdit}
                placeholder={isEdit ? "Để trống nếu không đổi" : ""}
              />
            </LabeledField>
          </div>

          <div style={twoColumnGridStyle}>
            <LabeledField label="Role">
              <select
                value={form.role}
                onChange={(e) =>
                  onChange((prev) => ({
                    ...prev,
                    role: e.target.value as "SUPER_ADMIN" | "USER",
                  }))
                }
                style={inputStyle}
              >
                <option value="USER">USER</option>
                <option value="SUPER_ADMIN">SUPER_ADMIN</option>
              </select>
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
                <option value="true">Active</option>
                <option value="false">Inactive</option>
              </select>
            </LabeledField>
          </div>

          <div style={{ display: "flex", gap: "24px", flexWrap: "wrap" }}>
            <CheckboxField
              label="Yêu cầu đổi mật khẩu"
              checked={form.requiresPasswordChange}
              onChange={(checked) =>
                onChange((prev) => ({
                  ...prev,
                  requiresPasswordChange: checked,
                }))
              }
            />
          </div>

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
                  ? "Cập nhật user"
                  : "Tạo user"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

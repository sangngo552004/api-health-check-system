import React, { useEffect, useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import {
  AlertSeverity,
  AlertOperator,
  AlertRuleType,
  AlertRuleUpdateCommand,
} from "../../types/alert.types";
import { X, Save, AlertCircle } from "lucide-react";
import { contactsApi } from "../../services/api/contacts.api";
import { ContactGroupDto } from "../../types/contact.types";
import {
  formActionsStyle,
  formCheckboxRowStyle,
  formCloseButtonStyle,
  formErrorStyle,
  formInputStyle,
  formLabelStyle,
  formModalStyle,
  formOverlayStyle,
  formPrimaryButtonStyle,
  formSecondaryButtonStyle,
  formTitleStyle,
} from "../shared/formStyles";

const alertSchema = z.object({
  name: z.string().min(3, "Tên cảnh báo phải chứa ít nhất 3 ký tự"),
  ruleType: z.enum(["CONSECUTIVE_FAILURE", "RESPONSE_TIME", "HTTP_STATUS_CODE"]),
  operator: z.enum(["GT", "GTE", "LT", "LTE", "EQ", "NE"]).nullable().optional(),
  thresholdValue: z.number().min(0, "Ngưỡng giá trị không được âm"),
  severity: z.enum(["INFO", "WARNING", "CRITICAL"]),
  isActive: z.boolean().default(true),
}).superRefine((data, ctx) => {
  if (data.ruleType !== "CONSECUTIVE_FAILURE" && !data.operator) {
    ctx.addIssue({
      code: z.ZodIssueCode.custom,
      path: ["operator"],
      message: "Toán tử là bắt buộc với loại quy tắc này",
    });
  }
});

type AlertFormValues = z.input<typeof alertSchema>;
export type AlertFormData = z.output<typeof alertSchema> & {
  contactGroupIds: number[];
};

interface AlertFormProps {
  initialData?: AlertRuleUpdateCommand | null;
  onSubmit: (data: AlertFormData) => Promise<void>;
  onCancel: () => void;
  loading: boolean;
}

export const AlertForm: React.FC<AlertFormProps> = ({
  initialData,
  onSubmit,
  onCancel,
  loading,
}) => {
  const [contactGroups, setContactGroups] = useState<ContactGroupDto[]>([]);
  const [contactSearch, setContactSearch] = useState("");
  const [selectedContactGroupIds, setSelectedContactGroupIds] = useState<number[]>([]);
  const {
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors },
  } = useForm<AlertFormValues>({
    resolver: zodResolver(alertSchema),
    defaultValues: {
      name: "",
      ruleType: "CONSECUTIVE_FAILURE" as AlertRuleType,
      operator: null,
      thresholdValue: 0,
      severity: "WARNING" as AlertSeverity,
      isActive: true,
    },
  });
  const selectedRuleType = watch("ruleType");
  const showOperator = selectedRuleType !== "CONSECUTIVE_FAILURE";

  useEffect(() => {
    const loadContactGroups = async () => {
      try {
        const response = await contactsApi.getContactGroups({
          page: 0,
          size: 100,
          sortBy: "name",
          sortDir: "asc",
        });
        setContactGroups(response.items);
      } catch {
        setContactGroups([]);
      }
    };

    void loadContactGroups();
  }, []);

  useEffect(() => {
    if (initialData) {
      reset({
        name: initialData.name,
        ruleType: initialData.ruleType,
        operator: initialData.operator ?? null,
        thresholdValue: initialData.thresholdValue,
        severity: initialData.severity,
        isActive: initialData.isActive ?? true,
      });
      setSelectedContactGroupIds(initialData.contactGroupIds ?? []);
      return;
    }

    reset({
      name: "",
      ruleType: "CONSECUTIVE_FAILURE",
      operator: null,
      thresholdValue: 0,
      severity: "WARNING",
      isActive: true,
    });
    setSelectedContactGroupIds([]);
  }, [initialData, reset]);

  const filteredContactGroups = useMemo(() => {
    if (!contactSearch.trim()) {
      return contactGroups;
    }

    const keyword = contactSearch.trim().toLowerCase();
    return contactGroups.filter((group) =>
      [group.name, group.description || "", ...(group.emailAddresses ?? [])]
        .join(" ")
        .toLowerCase()
        .includes(keyword),
    );
  }, [contactGroups, contactSearch]);

  const toggleContactGroup = (groupId: number) => {
    setSelectedContactGroupIds((current) =>
      current.includes(groupId)
        ? current.filter((id) => id !== groupId)
        : [...current, groupId],
    );
  };

  const submitHandler = async (data: AlertFormValues) => {
    await onSubmit({
      ...data,
      isActive: data.isActive ?? true,
      operator: data.ruleType === "CONSECUTIVE_FAILURE" ? null : data.operator,
      contactGroupIds: selectedContactGroupIds,
    });
  };

  return (
    <div style={formOverlayStyle}>
      <div
        className="card"
        style={{
          ...formModalStyle,
          maxWidth: "620px",
          animation: "fadeIn 0.3s ease-out",
        }}
      >
        <button onClick={onCancel} style={formCloseButtonStyle}>
          <X size={24} />
        </button>

        <h2 style={formTitleStyle}>
          {initialData ? "Chỉnh sửa quy tắc cảnh báo" : "Tạo quy tắc cảnh báo"}
        </h2>

        <form
          onSubmit={handleSubmit(submitHandler)}
          style={{ display: "flex", flexDirection: "column", gap: "20px" }}
        >
          <div>
            <label style={formLabelStyle}>Tên quy tắc (*)</label>
            <input
              {...register("name")}
              placeholder="VD: Cảnh báo thất bại liên tiếp"
              style={formInputStyle(Boolean(errors.name))}
            />
            {errors.name && (
              <div style={formErrorStyle}>
                <AlertCircle size={12} />
                {errors.name.message as string}
              </div>
            )}
          </div>

          <div>
            <label style={formLabelStyle}>Loại cảnh báo (*)</label>
            <select {...register("ruleType")} style={formInputStyle()}>
              <option value="CONSECUTIVE_FAILURE">Thất bại liên tiếp</option>
              <option value="RESPONSE_TIME">Thời gian phản hồi</option>
              <option value="HTTP_STATUS_CODE">HTTP status code</option>
            </select>
          </div>

          <div
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(2, minmax(0, 1fr))",
              gap: "16px",
            }}
          >
            {showOperator ? (
              <div>
                <label style={formLabelStyle}>Toán tử (*)</label>
                <select {...register("operator")} style={formInputStyle(Boolean(errors.operator))}>
                  <option value="GT">Lớn hơn {">"}</option>
                  <option value="GTE">Lớn hơn hoặc bằng {">="}</option>
                  <option value="LT">Nhỏ hơn {"<"}</option>
                  <option value="LTE">Nhỏ hơn hoặc bằng {"<="}</option>
                  <option value="EQ">Bằng {"="}</option>
                  <option value="NE">Khác {"!="}</option>
                </select>
                {errors.operator && (
                  <div style={formErrorStyle}>
                    <AlertCircle size={12} />
                    {errors.operator.message as string}
                  </div>
                )}
              </div>
            ) : (
              <div>
                <label style={formLabelStyle}>Toán tử</label>
                <div style={{ ...formInputStyle(), display: "flex", alignItems: "center", color: "var(--text-muted)" }}>
                  Không áp dụng cho thất bại liên tiếp
                </div>
              </div>
            )}

            <div>
              <label style={formLabelStyle}>
                {selectedRuleType === "CONSECUTIVE_FAILURE"
                  ? "Số lần liên tiếp (*)"
                  : selectedRuleType === "RESPONSE_TIME"
                    ? "Ngưỡng thời gian (ms) (*)"
                    : "HTTP status code (*)"}
              </label>
              <input
                type="number"
                {...register("thresholdValue", { valueAsNumber: true })}
                style={formInputStyle(Boolean(errors.thresholdValue))}
              />
              {errors.thresholdValue && (
                <div style={formErrorStyle}>
                  <AlertCircle size={12} />
                  {errors.thresholdValue.message as string}
                </div>
              )}
            </div>
          </div>

          <div>
            <label style={formLabelStyle}>Mức độ incident (*)</label>
            <select {...register("severity")} style={formInputStyle()}>
              <option value="INFO">Info</option>
              <option value="WARNING">Warning</option>
              <option value="CRITICAL">Critical</option>
            </select>
          </div>

          <div style={{ display: "grid", gap: "12px" }}>
            <label style={formLabelStyle}>Nhóm liên hệ nhận cảnh báo</label>
            <input
              value={contactSearch}
              onChange={(event) => setContactSearch(event.target.value)}
              placeholder="Tìm theo tên nhóm hoặc email"
              style={formInputStyle()}
            />

            <div
              style={{
                display: "grid",
                gap: "10px",
                maxHeight: "220px",
                overflowY: "auto",
                paddingRight: "4px",
              }}
            >
              {filteredContactGroups.map((group) => {
                const checked = selectedContactGroupIds.includes(group.id);
                return (
                  <button
                    key={group.id}
                    type="button"
                    onClick={() => toggleContactGroup(group.id)}
                    style={{
                      textAlign: "left",
                      padding: "12px 14px",
                      borderRadius: "12px",
                      border: checked
                        ? "1px solid var(--accent-color)"
                        : "1px solid var(--card-border)",
                      background: checked ? "var(--accent-bg)" : "var(--bg-secondary)",
                      color: "var(--text-primary)",
                      cursor: "pointer",
                    }}
                  >
                    <div style={{ fontWeight: 600 }}>{group.name}</div>
                    <div
                      style={{ fontSize: "0.82rem", color: "var(--text-muted)" }}
                    >
                      {group.emailAddresses.length > 0
                        ? group.emailAddresses.join(", ")
                        : "Chưa có email bổ sung"}
                    </div>
                  </button>
                );
              })}
              {filteredContactGroups.length === 0 && (
                <div style={{ color: "var(--text-muted)" }}>
                  Không tìm thấy nhóm liên hệ phù hợp.
                </div>
              )}
            </div>

            {selectedContactGroupIds.length > 0 && (
              <div style={{ color: "var(--text-muted)", fontSize: "0.85rem" }}>
                Đã chọn {selectedContactGroupIds.length} nhóm liên hệ.
              </div>
            )}
          </div>

          <div style={formCheckboxRowStyle}>
            <input
              type="checkbox"
              id="isActive"
              {...register("isActive")}
              style={{ width: "16px", height: "16px", cursor: "pointer" }}
            />
            <label
              htmlFor="isActive"
              style={{ fontWeight: 600, cursor: "pointer" }}
            >
              Kích hoạt quy tắc này
            </label>
          </div>

          <div style={formActionsStyle}>
            <button
              type="button"
              onClick={onCancel}
              style={formSecondaryButtonStyle}
            >
              Huỷ bỏ
            </button>
            <button
              type="submit"
              disabled={loading}
              style={{ ...formPrimaryButtonStyle, opacity: loading ? 0.7 : 1 }}
            >
              <Save size={18} />
              {loading ? "Đang lưu..." : "Lưu quy tắc"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

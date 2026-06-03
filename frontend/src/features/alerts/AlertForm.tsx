import React, { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import {
  AlertRuleUpdateCommand,
  AlertRuleCreateCommand,
  AlertRuleType,
  AlertOperator,
} from "../../types/alert.types";
import { X, Save, AlertCircle } from "lucide-react";

const alertSchema = z.object({
  name: z.string().min(3, "Tên cảnh báo phải chứa ít nhất 3 ký tự"),
  ruleType: z.enum(["STATUS_CHANGE", "LATENCY_SPIKE", "ERROR_RATE"]),
  operator: z.enum(["GREATER_THAN", "LESS_THAN", "EQUAL"]),
  thresholdValue: z.number().min(0, "Ngưỡng giá trị không được âm"),
  isActive: z.boolean().default(true),
  overrideDefaultContacts: z.boolean().default(false),
  // For simplicity in this demo, we'll map contactGroupIds from a comma separated string
  contactGroupIds: z.string().transform((val) =>
    val
      ? val
          .split(",")
          .map((id) => parseInt(id.trim()))
          .filter((n) => !isNaN(n))
      : [],
  ),
});

type AlertFormValues = z.input<typeof alertSchema>;
type AlertFormData = z.output<typeof alertSchema>;

interface AlertFormProps {
  initialData?: AlertRuleUpdateCommand | null;
  onSubmit: (data: AlertRuleCreateCommand) => Promise<void>;
  onCancel: () => void;
  loading: boolean;
}

export const AlertForm: React.FC<AlertFormProps> = ({
  initialData,
  onSubmit,
  onCancel,
  loading,
}) => {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<AlertFormValues, unknown, AlertFormData>({
    resolver: zodResolver(alertSchema),
    defaultValues: {
      name: "",
      ruleType: "STATUS_CHANGE" as AlertRuleType,
      operator: "GREATER_THAN" as AlertOperator,
      thresholdValue: 0,
      isActive: true,
      overrideDefaultContacts: false,
      contactGroupIds: "",
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        ...initialData,
        contactGroupIds: initialData.contactGroupIds?.join(", ") || "",
      });
    }
  }, [initialData, reset]);

  return (
    <div
      style={{
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        background: "rgba(0,0,0,0.5)",
        backdropFilter: "blur(4px)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        zIndex: 100,
      }}
    >
      <div
        className="card"
        style={{
          width: "100%",
          maxWidth: "500px",
          padding: "32px",
          position: "relative",
          animation: "fadeIn 0.3s ease-out",
          maxHeight: "90vh",
          overflowY: "auto",
        }}
      >
        <button
          onClick={onCancel}
          style={{
            position: "absolute",
            top: "24px",
            right: "24px",
            background: "none",
            border: "none",
            color: "var(--text-muted)",
            cursor: "pointer",
          }}
        >
          <X size={24} />
        </button>

        <h2
          style={{ fontSize: "1.5rem", fontWeight: 700, margin: "0 0 24px 0" }}
        >
          {initialData ? "Chỉnh sửa Cảnh báo" : "Thêm mới Cảnh báo"}
        </h2>

        <form
          onSubmit={handleSubmit(onSubmit)}
          style={{ display: "flex", flexDirection: "column", gap: "20px" }}
        >
          <div>
            <label
              style={{
                display: "block",
                fontSize: "0.85rem",
                fontWeight: 600,
                color: "var(--text-secondary)",
                marginBottom: "8px",
              }}
            >
              Tên Cảnh báo (*)
            </label>
            <input
              {...register("name")}
              placeholder="VD: Cảnh báo sập hệ thống"
              style={{
                width: "100%",
                padding: "12px",
                background: "var(--bg-secondary)",
                border: `1px solid ${errors.name ? "var(--error-color)" : "var(--card-border)"}`,
                borderRadius: "10px",
                color: "var(--text-primary)",
                outline: "none",
              }}
            />
            {errors.name && (
              <div
                style={{
                  color: "var(--error-color)",
                  fontSize: "0.75rem",
                  marginTop: "4px",
                  display: "flex",
                  alignItems: "center",
                  gap: "4px",
                }}
              >
                <AlertCircle size={12} />
                {errors.name.message as string}
              </div>
            )}
          </div>

          <div>
            <label
              style={{
                display: "block",
                fontSize: "0.85rem",
                fontWeight: 600,
                color: "var(--text-secondary)",
                marginBottom: "8px",
              }}
            >
              Loại cảnh báo (*)
            </label>
            <select
              {...register("ruleType")}
              style={{
                width: "100%",
                padding: "12px",
                background: "var(--bg-secondary)",
                border: "1px solid var(--card-border)",
                borderRadius: "10px",
                color: "var(--text-primary)",
                outline: "none",
              }}
            >
              <option value="STATUS_CHANGE">
                Thay đổi trạng thái (Lên/Xuống)
              </option>
              <option value="LATENCY_SPIKE">Tăng vọt độ trễ</option>
              <option value="ERROR_RATE">Tỉ lệ lỗi (HTTP 5xx)</option>
            </select>
          </div>

          <div
            style={{
              display: "grid",
              gridTemplateColumns: "1fr 1fr",
              gap: "16px",
            }}
          >
            <div>
              <label
                style={{
                  display: "block",
                  fontSize: "0.85rem",
                  fontWeight: 600,
                  color: "var(--text-secondary)",
                  marginBottom: "8px",
                }}
              >
                Toán tử (*)
              </label>
              <select
                {...register("operator")}
                style={{
                  width: "100%",
                  padding: "12px",
                  background: "var(--bg-secondary)",
                  border: "1px solid var(--card-border)",
                  borderRadius: "10px",
                  color: "var(--text-primary)",
                  outline: "none",
                }}
              >
                <option value="GREATER_THAN">Lớn hơn {">"}</option>
                <option value="LESS_THAN">Nhỏ hơn {"<"}</option>
                <option value="EQUAL">Bằng {"="}</option>
              </select>
            </div>
            <div>
              <label
                style={{
                  display: "block",
                  fontSize: "0.85rem",
                  fontWeight: 600,
                  color: "var(--text-secondary)",
                  marginBottom: "8px",
                }}
              >
                Ngưỡng giá trị (*)
              </label>
              <input
                type="number"
                {...register("thresholdValue", { valueAsNumber: true })}
                style={{
                  width: "100%",
                  padding: "12px",
                  background: "var(--bg-secondary)",
                  border: `1px solid ${errors.thresholdValue ? "var(--error-color)" : "var(--card-border)"}`,
                  borderRadius: "10px",
                  color: "var(--text-primary)",
                  outline: "none",
                }}
              />
            </div>
          </div>

          <div>
            <label
              style={{
                display: "block",
                fontSize: "0.85rem",
                fontWeight: 600,
                color: "var(--text-secondary)",
                marginBottom: "8px",
              }}
            >
              Contact Group IDs (Nhóm liên hệ)
            </label>
            <input
              {...register("contactGroupIds")}
              placeholder="VD: 1, 2, 3 (Cách nhau bằng dấu phẩy)"
              style={{
                width: "100%",
                padding: "12px",
                background: "var(--bg-secondary)",
                border: "1px solid var(--card-border)",
                borderRadius: "10px",
                color: "var(--text-primary)",
                outline: "none",
              }}
            />
          </div>

          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: "12px",
              marginTop: "8px",
            }}
          >
            <input
              type="checkbox"
              id="overrideDefaultContacts"
              {...register("overrideDefaultContacts")}
              style={{ width: "16px", height: "16px", cursor: "pointer" }}
            />
            <label
              htmlFor="overrideDefaultContacts"
              style={{
                fontWeight: 600,
                cursor: "pointer",
                color: "var(--text-primary)",
              }}
            >
              Chỉ gửi cho các Group này (Bỏ qua mặc định)
            </label>
          </div>

          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: "12px",
              marginTop: "8px",
            }}
          >
            <input
              type="checkbox"
              id="isActive"
              {...register("isActive")}
              style={{ width: "16px", height: "16px", cursor: "pointer" }}
            />
            <label
              htmlFor="isActive"
              style={{
                fontWeight: 600,
                cursor: "pointer",
                color: "var(--text-primary)",
              }}
            >
              Kích hoạt quy tắc này
            </label>
          </div>

          <div
            style={{
              display: "flex",
              gap: "16px",
              marginTop: "16px",
              justifyContent: "flex-end",
            }}
          >
            <button
              type="button"
              onClick={onCancel}
              style={{
                padding: "12px 24px",
                background: "none",
                border: "1px solid var(--card-border)",
                color: "var(--text-primary)",
                borderRadius: "10px",
                fontWeight: 600,
                cursor: "pointer",
              }}
            >
              Huỷ bỏ
            </button>
            <button
              type="submit"
              disabled={loading}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "8px",
                padding: "12px 24px",
                background: "var(--accent-color)",
                border: "none",
                color: "#fff",
                borderRadius: "10px",
                fontWeight: 600,
                cursor: loading ? "not-allowed" : "pointer",
                opacity: loading ? 0.7 : 1,
              }}
            >
              <Save size={18} />
              {loading ? "Đang lưu..." : "Lưu Alert Rule"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

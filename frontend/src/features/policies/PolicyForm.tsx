import React, { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import {
  CheckPolicyUpdateCommand,
  CheckPolicyCreateCommand,
} from "../../types/policy.types";
import { X, Save, AlertCircle } from "lucide-react";

const policySchema = z.object({
  name: z.string().min(3, "Tên chính sách phải chứa ít nhất 3 ký tự"),
  intervalSeconds: z.number().min(5, "Chu kỳ kiểm tra tối thiểu là 5 giây"),
  timeoutMillis: z.number().min(100, "Timeout tối thiểu 100ms"),
  retryCount: z.number().min(0, "Số lần thử lại không được âm"),
  failureThreshold: z.number().min(1, "Ngưỡng thất bại tối thiểu là 1"),
  latencyThresholdMillis: z.number().min(50, "Ngưỡng độ trễ tối thiểu 50ms"),
  expectedStatusCode: z.number().optional().nullable(),
  expectedResponseBody: z.string().optional().nullable(),
  responseRegex: z.string().optional().nullable(),
});

type PolicyFormValues = z.input<typeof policySchema>;
type PolicyFormData = z.output<typeof policySchema>;

interface PolicyFormProps {
  initialData?: CheckPolicyUpdateCommand | null;
  onSubmit: (data: CheckPolicyCreateCommand) => Promise<void>;
  onCancel: () => void;
  loading: boolean;
}

export const PolicyForm: React.FC<PolicyFormProps> = ({
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
  } = useForm<PolicyFormValues, unknown, PolicyFormData>({
    resolver: zodResolver(policySchema),
    defaultValues: {
      name: "",
      intervalSeconds: 60,
      timeoutMillis: 5000,
      retryCount: 3,
      failureThreshold: 3,
      latencyThresholdMillis: 2000,
      expectedStatusCode: 200,
      expectedResponseBody: "",
      responseRegex: "",
    },
  });

  useEffect(() => {
    if (initialData) {
      reset(initialData);
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
          maxWidth: "600px",
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
          {initialData ? "Chỉnh sửa Policy" : "Thêm mới Policy"}
        </h2>

        <form
          onSubmit={handleSubmit(onSubmit)}
          style={{ display: "flex", flexDirection: "column", gap: "20px" }}
        >
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "1fr 1fr",
              gap: "20px",
            }}
          >
            <div style={{ gridColumn: "span 2" }}>
              <label
                style={{
                  display: "block",
                  fontSize: "0.85rem",
                  fontWeight: 600,
                  color: "var(--text-secondary)",
                  marginBottom: "8px",
                }}
              >
                Tên Policy (*)
              </label>
              <input
                {...register("name")}
                placeholder="VD: Strict Production Policy"
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
                Chu kỳ kiểm tra (giây) (*)
              </label>
              <input
                type="number"
                {...register("intervalSeconds", { valueAsNumber: true })}
                style={{
                  width: "100%",
                  padding: "12px",
                  background: "var(--bg-secondary)",
                  border: `1px solid ${errors.intervalSeconds ? "var(--error-color)" : "var(--card-border)"}`,
                  borderRadius: "10px",
                  color: "var(--text-primary)",
                  outline: "none",
                }}
              />
              {errors.intervalSeconds && (
                <div
                  style={{
                    color: "var(--error-color)",
                    fontSize: "0.75rem",
                    marginTop: "4px",
                  }}
                >
                  {errors.intervalSeconds.message as string}
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
                Timeout (ms) (*)
              </label>
              <input
                type="number"
                {...register("timeoutMillis", { valueAsNumber: true })}
                style={{
                  width: "100%",
                  padding: "12px",
                  background: "var(--bg-secondary)",
                  border: `1px solid ${errors.timeoutMillis ? "var(--error-color)" : "var(--card-border)"}`,
                  borderRadius: "10px",
                  color: "var(--text-primary)",
                  outline: "none",
                }}
              />
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
                Số lần thử lại (*)
              </label>
              <input
                type="number"
                {...register("retryCount", { valueAsNumber: true })}
                style={{
                  width: "100%",
                  padding: "12px",
                  background: "var(--bg-secondary)",
                  border: `1px solid ${errors.retryCount ? "var(--error-color)" : "var(--card-border)"}`,
                  borderRadius: "10px",
                  color: "var(--text-primary)",
                  outline: "none",
                }}
              />
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
                Ngưỡng đánh dấu DOWN (*)
              </label>
              <input
                type="number"
                {...register("failureThreshold", { valueAsNumber: true })}
                style={{
                  width: "100%",
                  padding: "12px",
                  background: "var(--bg-secondary)",
                  border: `1px solid ${errors.failureThreshold ? "var(--error-color)" : "var(--card-border)"}`,
                  borderRadius: "10px",
                  color: "var(--text-primary)",
                  outline: "none",
                }}
              />
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
                Ngưỡng độ trễ (ms) (*)
              </label>
              <input
                type="number"
                {...register("latencyThresholdMillis", { valueAsNumber: true })}
                style={{
                  width: "100%",
                  padding: "12px",
                  background: "var(--bg-secondary)",
                  border: `1px solid ${errors.latencyThresholdMillis ? "var(--error-color)" : "var(--card-border)"}`,
                  borderRadius: "10px",
                  color: "var(--text-primary)",
                  outline: "none",
                }}
              />
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
                HTTP Code kỳ vọng
              </label>
              <input
                type="number"
                {...register("expectedStatusCode", { valueAsNumber: true })}
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

            <div style={{ gridColumn: "span 2" }}>
              <label
                style={{
                  display: "block",
                  fontSize: "0.85rem",
                  fontWeight: 600,
                  color: "var(--text-secondary)",
                  marginBottom: "8px",
                }}
              >
                Nội dung Regex kỳ vọng (Tùy chọn)
              </label>
              <input
                {...register("responseRegex")}
                placeholder="VD: .*success.*"
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
              {loading ? "Đang lưu..." : "Lưu Policy"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

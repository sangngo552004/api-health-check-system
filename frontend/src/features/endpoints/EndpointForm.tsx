import React, { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import {
  EndpointUpdateCommand,
  EndpointCreateCommand,
  HttpMethod,
  Environment,
  CheckType,
} from "../../types/endpoint.types";
import { X, Save, AlertCircle } from "lucide-react";

const endpointSchema = z.object({
  name: z
    .string()
    .min(3, "Tên phải chứa ít nhất 3 ký tự")
    .max(100, "Tên không quá 100 ký tự"),
  url: z.string().url("Đường dẫn URL không hợp lệ"),
  method: z.enum(["GET", "POST", "PUT", "DELETE", "PATCH", "HEAD"]),
  environment: z.enum(["PRODUCTION", "STAGING", "DEVELOPMENT"]),
  checkType: z.enum(["HTTP", "TCP"]),
  isActive: z.boolean().default(true),
  policyId: z.number().optional().nullable(),
  tags: z.string().transform((val) =>
    val
      ? val
          .split(",")
          .map((t) => t.trim())
          .filter(Boolean)
      : [],
  ),
});

type EndpointFormValues = z.input<typeof endpointSchema>;
type EndpointFormData = Omit<EndpointCreateCommand, "alertRuleIds" | "headers">;

interface EndpointFormProps {
  initialData?: EndpointUpdateCommand | null;
  onSubmit: (data: EndpointFormData) => Promise<void>;
  onCancel: () => void;
  loading: boolean;
}

export const EndpointForm: React.FC<EndpointFormProps> = ({
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
  } = useForm<EndpointFormValues, unknown, EndpointFormData>({
    resolver: zodResolver(endpointSchema),
    defaultValues: {
      name: "",
      url: "",
      method: "GET" as HttpMethod,
      environment: "PRODUCTION" as Environment,
      checkType: "HTTP" as CheckType,
      isActive: true,
      tags: "", // Displayed as comma separated string
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        ...initialData,
        tags: initialData.tags?.join(", ") || "",
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
          {initialData ? "Chỉnh sửa Endpoint" : "Thêm mới Endpoint"}
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
                Tên hệ thống (*)
              </label>
              <input
                {...register("name")}
                placeholder="VD: Payment API Core"
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
                URL kiểm tra (*)
              </label>
              <input
                {...register("url")}
                placeholder="https://api.domain.com/health"
                style={{
                  width: "100%",
                  padding: "12px",
                  background: "var(--bg-secondary)",
                  border: `1px solid ${errors.url ? "var(--error-color)" : "var(--card-border)"}`,
                  borderRadius: "10px",
                  color: "var(--text-primary)",
                  outline: "none",
                }}
              />
              {errors.url && (
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
                  {errors.url.message as string}
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
                Giao thức
              </label>
              <select
                {...register("checkType")}
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
                <option value="HTTP">HTTP/HTTPS</option>
                <option value="TCP">TCP Socket</option>
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
                HTTP Method
              </label>
              <select
                {...register("method")}
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
                <option value="GET">GET</option>
                <option value="POST">POST</option>
                <option value="PUT">PUT</option>
                <option value="DELETE">DELETE</option>
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
                Môi trường
              </label>
              <select
                {...register("environment")}
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
                <option value="PRODUCTION">Production</option>
                <option value="STAGING">Staging</option>
                <option value="DEVELOPMENT">Development</option>
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
                Thẻ phân loại (Tags)
              </label>
              <input
                {...register("tags")}
                placeholder="VD: critical, payment"
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
                gridColumn: "span 2",
                display: "flex",
                alignItems: "center",
                gap: "12px",
                marginTop: "8px",
                padding: "16px",
                background: "rgba(56, 189, 248, 0.05)",
                borderRadius: "12px",
                border: "1px solid rgba(56, 189, 248, 0.1)",
              }}
            >
              <input
                type="checkbox"
                id="isActive"
                {...register("isActive")}
                style={{ width: "20px", height: "20px", cursor: "pointer" }}
              />
              <label
                htmlFor="isActive"
                style={{
                  fontWeight: 600,
                  cursor: "pointer",
                  color: "var(--accent-color)",
                }}
              >
                Kích hoạt giám sát ngay lập tức
              </label>
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
              {loading ? "Đang lưu..." : "Lưu Endpoint"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

import React, { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import {
  ContactGroupUpdateCommand,
  ContactGroupCreateCommand,
} from "../../types/contact.types";
import { X, Save, AlertCircle } from "lucide-react";

const contactSchema = z.object({
  name: z.string().min(3, "Tên nhóm phải chứa ít nhất 3 ký tự"),
  description: z.string().optional(),
  isActive: z.boolean().default(true),
  userIds: z.string().transform((val) =>
    val
      ? val
          .split(",")
          .map((id) => parseInt(id.trim()))
          .filter((n) => !isNaN(n))
      : [],
  ),
  emailAddresses: z.string().transform((val) =>
    val
      ? val
          .split(",")
          .map((e) => e.trim())
          .filter((e) => e.length > 0)
      : [],
  ),
  webhookUrls: z.string().transform((val) =>
    val
      ? val
          .split(",")
          .map((w) => w.trim())
          .filter((w) => w.length > 0)
      : [],
  ),
});

type ContactFormValues = z.input<typeof contactSchema>;
type ContactFormData = z.output<typeof contactSchema>;

interface ContactFormProps {
  initialData?: ContactGroupUpdateCommand | null;
  onSubmit: (data: ContactGroupCreateCommand) => Promise<void>;
  onCancel: () => void;
  loading: boolean;
}

export const ContactForm: React.FC<ContactFormProps> = ({
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
  } = useForm<ContactFormValues, unknown, ContactFormData>({
    resolver: zodResolver(contactSchema),
    defaultValues: {
      name: "",
      description: "",
      isActive: true,
      userIds: "",
      emailAddresses: "",
      webhookUrls: "",
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        ...initialData,
        userIds: initialData.userIds?.join(", ") || "",
        emailAddresses: initialData.emailAddresses?.join(", ") || "",
        webhookUrls: initialData.webhookUrls?.join(", ") || "",
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
          maxWidth: "550px",
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
          {initialData ? "Chỉnh sửa Contact Group" : "Thêm mới Contact Group"}
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
              Tên nhóm liên hệ (*)
            </label>
            <input
              {...register("name")}
              placeholder="VD: Nhóm kỹ thuật Backend"
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
              Mô tả
            </label>
            <input
              {...register("description")}
              placeholder="Mô tả ngắn gọn về nhóm này..."
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
              Danh sách User IDs
            </label>
            <input
              {...register("userIds")}
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
              Danh sách Email (Tuỳ chọn)
            </label>
            <input
              {...register("emailAddresses")}
              placeholder="VD: oncall@company.com, alert@company.com"
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
              Danh sách Webhook URLs (Slack, Teams...)
            </label>
            <input
              {...register("webhookUrls")}
              placeholder="VD: https://hooks.slack.com/..."
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
              Kích hoạt nhóm này
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
              {loading ? "Đang lưu..." : "Lưu Nhóm"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { ContactGroupUpdateCommand } from "../../types/contact.types";
import { X, Save, AlertCircle, Mail, Plus } from "lucide-react";
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
  formTextareaStyle,
  formTitleStyle,
} from "../shared/formStyles";

const contactSchema = z.object({
  name: z.string().min(3, "Tên nhóm phải chứa ít nhất 3 ký tự"),
  description: z.string().optional(),
  isActive: z.boolean().default(true),
});

const singleEmailSchema = z.string().email("Email không hợp lệ");

type ContactFormValues = z.input<typeof contactSchema>;
export type ContactFormData = z.output<typeof contactSchema> & {
  emailAddresses: string[];
};

interface ContactFormProps {
  initialData?: ContactGroupUpdateCommand | null;
  onSubmit: (data: ContactFormData) => Promise<void>;
  onCancel: () => void;
  loading: boolean;
}

export const ContactForm: React.FC<ContactFormProps> = ({
  initialData,
  onSubmit,
  onCancel,
  loading,
}) => {
  const [emailDraft, setEmailDraft] = useState("");
  const [emailAddresses, setEmailAddresses] = useState<string[]>([]);
  const [emailError, setEmailError] = useState("");
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ContactFormValues>({
    resolver: zodResolver(contactSchema),
    defaultValues: {
      name: "",
      description: "",
      isActive: true,
    },
  });

  useEffect(() => {
    if (initialData) {
      reset({
        name: initialData.name,
        description: initialData.description || "",
        isActive: initialData.isActive ?? true,
      });
      setEmailAddresses(initialData.emailAddresses ?? []);
      setEmailDraft("");
      setEmailError("");
      return;
    }

    reset({
      name: "",
      description: "",
      isActive: true,
    });
    setEmailAddresses([]);
    setEmailDraft("");
    setEmailError("");
  }, [initialData, reset]);

  const addEmail = () => {
    const normalizedEmail = emailDraft.trim().toLowerCase();
    if (!normalizedEmail) {
      return;
    }

    const parsed = singleEmailSchema.safeParse(normalizedEmail);
    if (!parsed.success) {
      setEmailError(parsed.error.issues[0]?.message || "Email không hợp lệ");
      return;
    }

    if (emailAddresses.includes(normalizedEmail)) {
      setEmailError("Email này đã được thêm");
      return;
    }

    setEmailAddresses((current) => [...current, normalizedEmail]);
    setEmailDraft("");
    setEmailError("");
  };

  const removeEmail = (email: string) => {
    setEmailAddresses((current) => current.filter((item) => item !== email));
  };

  const submitHandler = async (data: ContactFormValues) => {
    await onSubmit({
      ...data,
      isActive: data.isActive ?? true,
      emailAddresses,
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
          {initialData ? "Chỉnh sửa nhóm liên hệ" : "Tạo nhóm liên hệ"}
        </h2>

        <form
          onSubmit={handleSubmit(submitHandler)}
          style={{ display: "flex", flexDirection: "column", gap: "20px" }}
        >
          <div>
            <label style={formLabelStyle}>Tên nhóm liên hệ (*)</label>
            <input
              {...register("name")}
              placeholder="VD: Nhóm kỹ thuật Backend"
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
            <label style={formLabelStyle}>Mô tả</label>
            <textarea
              {...register("description")}
              placeholder="Mô tả ngắn gọn về nhóm này..."
              style={formTextareaStyle()}
            />
          </div>

          <div style={{ display: "grid", gap: "12px" }}>
            <label style={formLabelStyle}>Email nhận thông báo (tuỳ chọn)</label>
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "minmax(0, 1fr) auto",
                gap: "12px",
              }}
            >
              <input
                value={emailDraft}
                onChange={(event) => {
                  setEmailDraft(event.target.value);
                  if (emailError) {
                    setEmailError("");
                  }
                }}
                onKeyDown={(event) => {
                  if (event.key === "Enter") {
                    event.preventDefault();
                    addEmail();
                  }
                }}
                placeholder="Nhập email rồi bấm thêm"
                style={formInputStyle(Boolean(emailError))}
              />
              <button
                type="button"
                onClick={addEmail}
                style={formSecondaryButtonStyle}
              >
                <Plus size={16} />
                Thêm email
              </button>
            </div>
            {emailError && (
              <div style={formErrorStyle}>
                <AlertCircle size={12} />
                {emailError}
              </div>
            )}

            <div
              style={{
                display: "flex",
                flexWrap: "wrap",
                gap: "10px",
                minHeight: "16px",
              }}
            >
              {emailAddresses.map((email) => (
                <button
                  key={email}
                  type="button"
                  onClick={() => removeEmail(email)}
                  style={{
                    display: "inline-flex",
                    alignItems: "center",
                    gap: "8px",
                    padding: "8px 12px",
                    borderRadius: "999px",
                    border: "1px solid var(--card-border)",
                    background: "var(--accent-bg)",
                    color: "var(--text-primary)",
                    cursor: "pointer",
                  }}
                  title="Bấm để xoá email này"
                >
                  <Mail size={14} />
                  {email}
                </button>
              ))}
            </div>
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
              style={{
                fontWeight: 600,
                cursor: "pointer",
                color: "var(--text-primary)",
              }}
            >
              Kích hoạt nhóm liên hệ này
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
              {loading ? "Đang lưu..." : "Lưu nhóm liên hệ"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

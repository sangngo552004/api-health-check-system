import React from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { X, UserPlus, AlertCircle } from "lucide-react";

const memberSchema = z.object({
  userId: z.number().min(1, "User ID không hợp lệ"),
  role: z.enum(["ADMIN", "MEMBER"]),
});

type MemberFormData = z.output<typeof memberSchema>;

interface MemberFormProps {
  onSubmit: (userId: number, role: MemberFormData["role"]) => Promise<void>;
  onCancel: () => void;
  loading: boolean;
}

export const MemberForm: React.FC<MemberFormProps> = ({
  onSubmit,
  onCancel,
  loading,
}) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<MemberFormData>({
    resolver: zodResolver(memberSchema),
    defaultValues: {
      userId: undefined as unknown as number,
      role: "MEMBER" as "ADMIN" | "MEMBER",
    },
  });

  const submitHandler = async (data: MemberFormData) => {
    await onSubmit(data.userId, data.role);
  };

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
          maxWidth: "420px",
          padding: "32px",
          position: "relative",
          animation: "fadeIn 0.3s ease-out",
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
          style={{
            fontSize: "1.4rem",
            fontWeight: 700,
            margin: "0 0 24px 0",
            display: "flex",
            alignItems: "center",
            gap: "8px",
          }}
        >
          <UserPlus size={24} color="var(--accent-color)" />
          Thêm thành viên
        </h2>

        <form
          onSubmit={handleSubmit(submitHandler)}
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
              User ID (*)
            </label>
            <input
              type="number"
              {...register("userId", { valueAsNumber: true })}
              placeholder="VD: 123"
              style={{
                width: "100%",
                padding: "12px",
                background: "var(--bg-secondary)",
                border: `1px solid ${errors.userId ? "var(--error-color)" : "var(--card-border)"}`,
                borderRadius: "10px",
                color: "var(--text-primary)",
                outline: "none",
              }}
            />
            {errors.userId && (
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
                {errors.userId.message as string}
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
              Quyền hạn (Role)
            </label>
            <select
              {...register("role")}
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
              <option value="MEMBER">Thành viên (MEMBER)</option>
              <option value="ADMIN">Quản trị viên (ADMIN)</option>
            </select>
          </div>

          <div
            style={{
              display: "flex",
              gap: "16px",
              marginTop: "8px",
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
              {loading ? "Đang thêm..." : "Thêm vào"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

import React from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { X, UserPlus, AlertCircle } from "lucide-react";
import {
  formActionsStyle,
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

const memberSchema = z.object({
  userId: z.number().min(1, "User ID không hợp lệ"),
});

type MemberFormData = z.output<typeof memberSchema>;

interface MemberFormProps {
  onSubmit: (userId: number) => Promise<void>;
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
    },
  });

  const submitHandler = async (data: MemberFormData) => {
    await onSubmit(data.userId);
  };

  return (
    <div style={formOverlayStyle}>
      <div
        className="card"
        style={{
          ...formModalStyle,
          maxWidth: "440px",
          animation: "fadeIn 0.3s ease-out",
        }}
      >
        <button onClick={onCancel} style={formCloseButtonStyle}>
          <X size={24} />
        </button>

        <h2
          style={{
            ...formTitleStyle,
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
            <label style={formLabelStyle}>User ID (*)</label>
            <input
              type="number"
              {...register("userId", { valueAsNumber: true })}
              placeholder="VD: 123"
              style={formInputStyle(Boolean(errors.userId))}
            />
            {errors.userId && (
              <div style={formErrorStyle}>
                <AlertCircle size={12} />
                {errors.userId.message as string}
              </div>
            )}
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
              {loading ? "Đang thêm..." : "Thêm vào"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

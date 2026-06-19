import React, { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { CheckPolicyUpdateCommand } from "../../types/policy.types";
import { X, Save, AlertCircle } from "lucide-react";
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
  formTextareaStyle,
  formTitleStyle,
  formTwoColumnGridStyle,
} from "../shared/formStyles";

const policySchema = z.object({
  name: z.string().min(3, "Tên chính sách phải chứa ít nhất 3 ký tự"),
  intervalSeconds: z.number().min(5, "Chu kỳ kiểm tra tối thiểu là 5 giây").optional(),
  timeoutMillis: z.number().min(100, "Timeout tối thiểu 100ms").optional(),
  retryCount: z.number().min(0, "Số lần thử lại không được âm").optional(),
  degradedResponseTimeMillis: z.number().min(50, "Ngưỡng chậm tối thiểu 50ms").optional().nullable(),
  expectedStatusCode: z.number().optional().nullable(),
  expectedResponseBody: z.string().optional().nullable(),
  responseRegex: z.string().optional().nullable(),
});

type PolicyFormValues = z.input<typeof policySchema>;
export type PolicyFormData = z.output<typeof policySchema>;

interface PolicyFormProps {
  initialData?: CheckPolicyUpdateCommand | null;
  onSubmit: (data: PolicyFormData) => Promise<void>;
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
      retryCount: 0,
      degradedResponseTimeMillis: 2000,
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
    <div style={formOverlayStyle}>
      <div
        className="card"
        style={{
          ...formModalStyle,
          maxWidth: "680px",
          animation: "fadeIn 0.3s ease-out",
        }}
      >
        <button onClick={onCancel} style={formCloseButtonStyle}>
          <X size={24} />
        </button>

        <h2 style={formTitleStyle}>
          {initialData ? "Chỉnh sửa Policy" : "Thêm mới Policy"}
        </h2>

        <form
          onSubmit={handleSubmit(onSubmit)}
          style={{ display: "flex", flexDirection: "column", gap: "20px" }}
        >
          <div style={formTwoColumnGridStyle}>
            <div style={{ gridColumn: "1 / -1" }}>
              <label style={formLabelStyle}>Tên Policy (*)</label>
              <input
                {...register("name")}
                placeholder="VD: Strict Production Policy"
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
              <label style={formLabelStyle}>Chu kỳ kiểm tra (giây) (*)</label>
              <input
                type="number"
                {...register("intervalSeconds", { valueAsNumber: true })}
                style={formInputStyle(Boolean(errors.intervalSeconds))}
              />
              {errors.intervalSeconds && (
                <div style={formErrorStyle}>
                  <AlertCircle size={12} />
                  {errors.intervalSeconds.message as string}
                </div>
              )}
            </div>

            <div>
              <label style={formLabelStyle}>Timeout (ms) (*)</label>
              <input
                type="number"
                {...register("timeoutMillis", { valueAsNumber: true })}
                style={formInputStyle(Boolean(errors.timeoutMillis))}
              />
            </div>

            <div>
              <label style={formLabelStyle}>Số lần thử lại (*)</label>
              <input
                type="number"
                {...register("retryCount", { valueAsNumber: true })}
                style={formInputStyle(Boolean(errors.retryCount))}
              />
            </div>

            <div>
              <label style={formLabelStyle}>Ngưỡng đánh dấu chậm (ms)</label>
              <input
                type="number"
                {...register("degradedResponseTimeMillis", { valueAsNumber: true })}
                style={formInputStyle(Boolean(errors.degradedResponseTimeMillis))}
              />
            </div>

            <div>
              <label style={formLabelStyle}>HTTP Code kỳ vọng</label>
              <input
                type="number"
                {...register("expectedStatusCode", { valueAsNumber: true })}
                style={formInputStyle()}
              />
            </div>

            <div style={{ gridColumn: "1 / -1" }}>
              <label style={formLabelStyle}>Expected response body</label>
              <textarea
                {...register("expectedResponseBody")}
                placeholder="Nội dung response mong đợi nếu cần so khớp chính xác..."
                style={formTextareaStyle()}
              />
            </div>

            <div style={{ gridColumn: "1 / -1" }}>
              <label style={formLabelStyle}>Regex kỳ vọng</label>
              <input
                {...register("responseRegex")}
                placeholder="VD: .*success.*"
                style={formInputStyle()}
              />
            </div>
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
              {loading ? "Đang lưu..." : "Lưu Policy"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

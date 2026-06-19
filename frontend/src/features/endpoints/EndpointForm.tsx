import React, { useEffect, useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import {
  CheckType,
  EndpointUpdateCommand,
  Environment,
  HttpMethod,
} from "../../types/endpoint.types";
import { CheckPolicyDto } from "../../types/policy.types";
import { policiesApi } from "../../services/api/policies.api";
import { alertsApi } from "../../services/api/alerts.api";
import { AlertRuleDto } from "../../types/alert.types";
import { X, Save, AlertCircle, Plus, Search, Tag } from "lucide-react";
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
  formTwoColumnGridStyle,
} from "../shared/formStyles";

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
  policyId: z.number().min(1, "Vui lòng chọn policy"),
  alertRuleIds: z.array(z.number()).min(1, "Vui lòng chọn ít nhất 1 alert rule"),
  requestBody: z.string().optional(),
  headersText: z.string().optional(),
});

type EndpointFormValues = z.input<typeof endpointSchema>;
export type EndpointFormData = z.output<typeof endpointSchema> & {
  headers: Record<string, string>;
  tags: string[];
};

interface EndpointFormProps {
  initialData?: EndpointUpdateCommand | null;
  onSubmit: (data: EndpointFormData) => Promise<void>;
  onCancel: () => void;
  loading: boolean;
}

const normalizeEnvironment = (value?: string): Environment => {
  const normalized = value?.trim().toUpperCase();

  if (
    normalized === "PRODUCTION" ||
    normalized === "STAGING" ||
    normalized === "DEVELOPMENT"
  ) {
    return normalized;
  }

  return "PRODUCTION";
};

const normalizePolicyId = (value?: number | string | null): number => {
  const normalized = Number(value);
  return Number.isFinite(normalized) ? normalized : 0;
};

export const EndpointForm: React.FC<EndpointFormProps> = ({
  initialData,
  onSubmit,
  onCancel,
  loading,
}) => {
  const [policies, setPolicies] = useState<CheckPolicyDto[]>([]);
  const [alertRules, setAlertRules] = useState<AlertRuleDto[]>([]);
  const [alertRuleSearch, setAlertRuleSearch] = useState("");
  const [tagDraft, setTagDraft] = useState("");
  const [tags, setTags] = useState<string[]>([]);
  const [headersError, setHeadersError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm<EndpointFormValues>({
    resolver: zodResolver(endpointSchema),
    defaultValues: {
      name: "",
      url: "",
      method: "GET" as HttpMethod,
      environment: "PRODUCTION" as Environment,
      checkType: "HTTP" as CheckType,
      isActive: true,
      policyId: 0,
      alertRuleIds: [],
      requestBody: "",
      headersText: "",
    },
  });
  const isMonitoringEnabled = watch("isActive");
  const selectedAlertRuleIds = watch("alertRuleIds");

  useEffect(() => {
    const loadDependencies = async () => {
      try {
        const [policiesResponse, alertRulesResponse] = await Promise.all([
          policiesApi.getPolicies({
            page: 0,
            size: 100,
            sortBy: "name",
            sortDir: "asc",
          }),
          alertsApi.getAlertRules({
            page: 0,
            size: 100,
            sortBy: "name",
            sortDir: "asc",
          }),
        ]);

        let nextPolicies = policiesResponse.items;
        let nextAlertRules = alertRulesResponse.items;

        const selectedPolicyId = normalizePolicyId(initialData?.policyId);
        const hasSelectedPolicy =
          selectedPolicyId > 0 &&
          nextPolicies.some((policy) => policy.id === selectedPolicyId);

        if (selectedPolicyId > 0 && !hasSelectedPolicy) {
          try {
            const selectedPolicy =
              await policiesApi.getPolicyById(selectedPolicyId);
            nextPolicies = [selectedPolicy, ...nextPolicies];
          } catch {
            // Keep the fetched list if the current policy cannot be loaded separately.
          }
        }

        const missingAlertRuleIds = (initialData?.alertRuleIds ?? []).filter(
          (ruleId) => !nextAlertRules.some((rule) => rule.id === ruleId),
        );

        if (missingAlertRuleIds.length > 0) {
          const fetchedRules = await Promise.all(
            missingAlertRuleIds.map(async (ruleId) => {
              try {
                return await alertsApi.getAlertRuleById(ruleId);
              } catch {
                return null;
              }
            }),
          );

          nextAlertRules = [
            ...fetchedRules.filter((rule): rule is AlertRuleDto => rule !== null),
            ...nextAlertRules,
          ];
        }

        setPolicies(nextPolicies);
        setAlertRules(nextAlertRules);
      } catch {
        setPolicies([]);
        setAlertRules([]);
      }
    };

    void loadDependencies();
  }, [initialData?.alertRuleIds, initialData?.policyId]);

  useEffect(() => {
    if (initialData) {
      reset({
        name: initialData.name,
        url: initialData.url,
        method: initialData.method,
        environment: normalizeEnvironment(initialData.environment),
        checkType: initialData.checkType,
        isActive: initialData.isActive ?? true,
        policyId: normalizePolicyId(initialData.policyId),
        alertRuleIds: initialData.alertRuleIds ?? [],
        requestBody: initialData.requestBody || "",
        headersText: JSON.stringify(initialData.headers ?? {}, null, 2),
      });
      setTags(initialData.tags ?? []);
      setTagDraft("");
      setHeadersError(null);
      setAlertRuleSearch("");
      return;
    }

    reset({
      name: "",
      url: "",
      method: "GET",
      environment: "PRODUCTION",
      checkType: "HTTP",
      isActive: true,
      policyId: 0,
      alertRuleIds: [],
      requestBody: "",
      headersText: "",
    });
    setTags([]);
    setTagDraft("");
    setHeadersError(null);
    setAlertRuleSearch("");
  }, [initialData, reset]);

  const filteredAlertRules = useMemo(() => {
    if (!alertRuleSearch.trim()) {
      return alertRules;
    }

    const keyword = alertRuleSearch.trim().toLowerCase();
    return alertRules.filter((rule) =>
      [rule.name, rule.ruleType, rule.severity]
        .join(" ")
        .toLowerCase()
        .includes(keyword),
    );
  }, [alertRuleSearch, alertRules]);

  const selectedAlertRules = useMemo(
    () =>
      selectedAlertRuleIds
        .map((ruleId) => alertRules.find((rule) => rule.id === ruleId))
        .filter((rule): rule is AlertRuleDto => Boolean(rule)),
    [alertRules, selectedAlertRuleIds],
  );

  const addTag = () => {
    const normalizedTag = tagDraft.trim();
    if (!normalizedTag || tags.includes(normalizedTag)) {
      return;
    }
    setTags((current) => [...current, normalizedTag]);
    setTagDraft("");
  };

  const removeTag = (tag: string) => {
    setTags((current) => current.filter((item) => item !== tag));
  };

  const toggleAlertRule = (ruleId: number) => {
    const nextSelected = selectedAlertRuleIds.includes(ruleId)
      ? selectedAlertRuleIds.filter((id) => id !== ruleId)
      : [...selectedAlertRuleIds, ruleId];

    setValue("alertRuleIds", nextSelected, {
      shouldDirty: true,
      shouldTouch: true,
      shouldValidate: true,
    });
  };

  const submitHandler = async (data: EndpointFormValues) => {
    const rawHeaders = data.headersText?.trim();
    let parsedHeaders: Record<string, string> = {};

    if (rawHeaders) {
      try {
        const candidate = JSON.parse(rawHeaders) as unknown;
        if (
          typeof candidate !== "object" ||
          candidate === null ||
          Array.isArray(candidate)
        ) {
          setHeadersError("Headers phải là JSON object, ví dụ {\"Authorization\":\"Bearer ...\"}");
          return;
        }

        parsedHeaders = Object.entries(
          candidate as Record<string, unknown>,
        ).reduce<Record<string, string>>((result, [key, value]) => {
          result[key] = String(value ?? "");
          return result;
        }, {});
      } catch {
        setHeadersError("Headers JSON không hợp lệ.");
        return;
      }
    }

    setHeadersError(null);
    await onSubmit({
      ...data,
      headers: parsedHeaders,
      isActive: data.isActive ?? true,
      tags,
    });
  };

  return (
    <div style={formOverlayStyle}>
      <div
        className="card"
        style={{
          ...formModalStyle,
          maxWidth: "760px",
          animation: "fadeIn 0.3s ease-out",
        }}
      >
        <button onClick={onCancel} style={formCloseButtonStyle}>
          <X size={24} />
        </button>

        <h2 style={formTitleStyle}>
          {initialData ? "Chỉnh sửa endpoint" : "Tạo endpoint giám sát"}
        </h2>

        <form
          onSubmit={handleSubmit(submitHandler)}
          style={{ display: "flex", flexDirection: "column", gap: "20px" }}
        >
          <div style={formTwoColumnGridStyle}>
            <div style={{ gridColumn: "1 / -1" }}>
              <label style={formLabelStyle}>Tên hệ thống (*)</label>
              <input
                {...register("name")}
                placeholder="VD: Payment API Core"
                style={formInputStyle(Boolean(errors.name))}
              />
              {errors.name && (
                <div style={formErrorStyle}>
                  <AlertCircle size={12} />
                  {errors.name.message as string}
                </div>
              )}
            </div>

            <div style={{ gridColumn: "1 / -1" }}>
              <label style={formLabelStyle}>URL kiểm tra (*)</label>
              <input
                {...register("url")}
                placeholder="https://api.domain.com/health"
                style={formInputStyle(Boolean(errors.url))}
              />
              {errors.url && (
                <div style={formErrorStyle}>
                  <AlertCircle size={12} />
                  {errors.url.message as string}
                </div>
              )}
            </div>

            <div>
              <label style={formLabelStyle}>Giao thức</label>
              <select {...register("checkType")} style={formInputStyle()}>
                <option value="HTTP">HTTP/HTTPS</option>
                <option value="TCP">TCP Socket</option>
              </select>
            </div>

            <div>
              <label style={formLabelStyle}>HTTP method</label>
              <select {...register("method")} style={formInputStyle()}>
                <option value="GET">GET</option>
                <option value="POST">POST</option>
                <option value="PUT">PUT</option>
                <option value="DELETE">DELETE</option>
                <option value="PATCH">PATCH</option>
                <option value="HEAD">HEAD</option>
              </select>
            </div>

            <div>
              <label style={formLabelStyle}>Môi trường</label>
              <select {...register("environment")} style={formInputStyle()}>
                <option value="PRODUCTION">Production</option>
                <option value="STAGING">Staging</option>
                <option value="DEVELOPMENT">Development</option>
              </select>
            </div>

            <div>
              <label style={formLabelStyle}>Policy áp dụng (*)</label>
              <select
                {...register("policyId", { valueAsNumber: true })}
                style={formInputStyle(Boolean(errors.policyId))}
              >
                <option value={0}>Chọn policy</option>
                {policies.map((policy) => (
                  <option key={policy.id} value={policy.id}>
                    {policy.name} · {policy.intervalSeconds}s
                  </option>
                ))}
              </select>
              {errors.policyId && (
                <div style={formErrorStyle}>
                  <AlertCircle size={12} />
                  {errors.policyId.message as string}
                </div>
              )}
            </div>

            <div style={{ gridColumn: "1 / -1", display: "grid", gap: "12px" }}>
              <label style={formLabelStyle}>Alert rule áp dụng (*)</label>
              <div style={{ position: "relative" }}>
                <Search
                  size={16}
                  style={{
                    position: "absolute",
                    top: "50%",
                    left: "12px",
                    transform: "translateY(-50%)",
                    color: "var(--text-muted)",
                  }}
                />
                <input
                  value={alertRuleSearch}
                  onChange={(event) => setAlertRuleSearch(event.target.value)}
                  placeholder="Tìm theo tên, loại rule hoặc severity"
                  style={{ ...formInputStyle(), paddingLeft: "38px" }}
                />
              </div>

              <div
                style={{
                  display: "grid",
                  gap: "10px",
                  maxHeight: "240px",
                  overflowY: "auto",
                  paddingRight: "4px",
                }}
              >
                {filteredAlertRules.map((rule) => {
                  const checked = selectedAlertRuleIds.includes(rule.id);
                  return (
                    <button
                      key={rule.id}
                      type="button"
                      onClick={() => toggleAlertRule(rule.id)}
                      style={{
                        textAlign: "left",
                        padding: "14px 16px",
                        borderRadius: "14px",
                        border: checked
                          ? "1px solid var(--accent-color)"
                          : "1px solid var(--card-border)",
                        background: checked ? "var(--accent-bg)" : "var(--bg-secondary)",
                        color: "var(--text-primary)",
                        cursor: "pointer",
                        display: "grid",
                        gap: "6px",
                      }}
                    >
                      <div
                        style={{
                          display: "flex",
                          justifyContent: "space-between",
                          gap: "12px",
                          alignItems: "center",
                          flexWrap: "wrap",
                        }}
                      >
                        <div style={{ fontWeight: 600 }}>{rule.name}</div>
                        <div
                          style={{
                            fontSize: "0.76rem",
                            color: checked ? "var(--accent-color)" : "var(--text-muted)",
                            fontWeight: 700,
                          }}
                        >
                          {checked ? "Đã chọn" : "Bấm để chọn"}
                        </div>
                      </div>
                      <div
                        style={{
                          display: "flex",
                          gap: "8px",
                          flexWrap: "wrap",
                          fontSize: "0.8rem",
                          color: "var(--text-muted)",
                        }}
                      >
                        <span>#{rule.id}</span>
                        <span>{rule.ruleType}</span>
                        <span>{rule.severity}</span>
                        <span>{rule.isActive ? "Dang hoat dong" : "Tam tat"}</span>
                      </div>
                    </button>
                  );
                })}
                {filteredAlertRules.length === 0 && (
                  <div style={{ color: "var(--text-muted)" }}>
                    Không tìm thấy alert rule phù hợp.
                  </div>
                )}
              </div>

              {errors.alertRuleIds && (
                <div style={formErrorStyle}>
                  <AlertCircle size={12} />
                  {errors.alertRuleIds.message as string}
                </div>
              )}

              {selectedAlertRules.length > 0 && (
                <div
                  style={{
                    display: "grid",
                    gap: "10px",
                    padding: "14px",
                    borderRadius: "16px",
                    border: "1px solid var(--accent-hover)",
                    background: "var(--accent-bg)",
                  }}
                >
                  <div style={{ fontWeight: 700 }}>
                    Đã chọn {selectedAlertRules.length} alert rule
                  </div>
                  <div style={{ display: "grid", gap: "8px" }}>
                    {selectedAlertRules.map((rule) => (
                      <div
                        key={rule.id}
                        style={{
                          display: "flex",
                          justifyContent: "space-between",
                          alignItems: "center",
                          gap: "12px",
                          padding: "10px 12px",
                          borderRadius: "12px",
                          background: "rgba(255,255,255,0.04)",
                          flexWrap: "wrap",
                        }}
                      >
                        <div style={{ display: "grid", gap: "4px" }}>
                          <div style={{ fontWeight: 600 }}>{rule.name}</div>
                          <div
                            style={{
                              fontSize: "0.8rem",
                              color: "var(--text-muted)",
                            }}
                          >
                            {rule.ruleType} · {rule.severity} · Rule #{rule.id}
                          </div>
                        </div>
                        <button
                          type="button"
                          onClick={() => toggleAlertRule(rule.id)}
                          style={{
                            ...formSecondaryButtonStyle,
                            padding: "8px 12px",
                          }}
                        >
                          Bỏ chọn
                        </button>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>

            <div style={{ gridColumn: "1 / -1", display: "grid", gap: "12px" }}>
              <label style={formLabelStyle}>Thẻ phân loại</label>
              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "minmax(0, 1fr) auto",
                  gap: "12px",
                }}
              >
                <input
                  value={tagDraft}
                  onChange={(event) => setTagDraft(event.target.value)}
                  onKeyDown={(event) => {
                    if (event.key === "Enter") {
                      event.preventDefault();
                      addTag();
                    }
                  }}
                  placeholder="Nhập thẻ rồi bấm thêm"
                  style={formInputStyle()}
                />
                <button
                  type="button"
                  onClick={addTag}
                  style={formSecondaryButtonStyle}
                >
                  <Plus size={16} />
                  Thêm thẻ
                </button>
              </div>

              <div
                style={{
                  display: "flex",
                  flexWrap: "wrap",
                  gap: "10px",
                  minHeight: "16px",
                }}
              >
                {tags.map((tag) => (
                  <button
                    key={tag}
                    type="button"
                    onClick={() => removeTag(tag)}
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
                    title="Bấm để xoá thẻ này"
                  >
                    <Tag size={14} />
                    {tag}
                  </button>
                ))}
              </div>
            </div>

            <div style={{ gridColumn: "1 / -1" }}>
              <label style={formLabelStyle}>Request body</label>
              <textarea
                {...register("requestBody")}
                placeholder="Nội dung request body nếu endpoint dùng POST/PUT..."
                style={formTextareaStyle()}
              />
            </div>

            <div style={{ gridColumn: "1 / -1" }}>
              <label style={formLabelStyle}>Headers gửi kèm</label>
              <textarea
                {...register("headersText")}
                placeholder={'{\n  "Authorization": "Bearer ...",\n  "Content-Type": "application/json"\n}'}
                style={formTextareaStyle(Boolean(headersError))}
              />
              {headersError && (
                <div style={formErrorStyle}>
                  <AlertCircle size={12} />
                  {headersError}
                </div>
              )}
            </div>
          </div>

          <div
            style={{
              ...formCheckboxRowStyle,
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
              Bật giám sát endpoint này
            </label>
            <span style={{ color: "var(--text-muted)", fontSize: "0.9rem" }}>
              {isMonitoringEnabled
                ? "Khi bật, scheduler sẽ tiếp tục kiểm tra endpoint."
                : "Khi tắt, endpoint sẽ không được scheduler kiểm tra."}
            </span>
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
              {loading ? "Đang lưu..." : "Lưu endpoint"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

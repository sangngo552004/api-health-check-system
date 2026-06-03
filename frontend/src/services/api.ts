import i18n from "../i18n";

const BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1";

export interface RequestOptions extends RequestInit {
  params?: Record<string, string | number | boolean>;
}

interface ApiErrorResponse {
  success?: boolean;
  message?: string;
  code?: string;
  errors?: string[];
}

interface ApiResponse<T> extends ApiErrorResponse {
  data?: T;
}

// LƯU TRỮ TOKEN IN-MEMORY ĐỂ CHỐNG XSS
let inMemoryAccessToken: string | null = null;

export const setInMemoryToken = (token: string | null) => {
  inMemoryAccessToken = token;
};

export const getInMemoryToken = () => inMemoryAccessToken;

async function request<T>(
  path: string,
  options: RequestOptions = {},
): Promise<T> {
  const workspaceId = localStorage.getItem("workspace_id");

  const headers = new Headers(options.headers);
  headers.set("Content-Type", "application/json");

  if (inMemoryAccessToken) {
    headers.set("Authorization", `Bearer ${inMemoryAccessToken}`);
  }

  if (workspaceId) {
    headers.set("X-Workspace-Id", workspaceId);
  }

  let url = `${BASE_URL}${path}`;
  if (options.params) {
    const searchParams = new URLSearchParams();
    Object.entries(options.params).forEach(([key, value]) => {
      searchParams.append(key, String(value));
    });
    url += `?${searchParams.toString()}`;
  }

  const config: RequestInit = {
    ...options,
    headers,
  };

  const response = await fetch(url, config);

  if (response.status === 401 || response.status === 403) {
    // Auth errors: if not logging in, dispatch event to logout cleanly
    if (
      path !== "/auth/login" &&
      path !== "/auth/register" &&
      path !== "/auth/refresh"
    ) {
      setInMemoryToken(null);
      localStorage.removeItem("refresh_token");
      window.dispatchEvent(new Event("auth-logout"));
    }
  }

  const contentType = response.headers.get("content-type");
  let data: ApiResponse<T> | null = null;
  if (contentType && contentType.includes("application/json")) {
    data = (await response.json()) as ApiResponse<T>;
  }

  if (!response.ok || (data && data.success === false)) {
    let errorMsg =
      data?.message ||
      data?.errors?.[0] ||
      response.statusText ||
      "An error occurred";

    // Tự động map Error Code của Backend sang tiếng Việt/Anh thông qua file JSON locale
    if (data?.code) {
      errorMsg = i18n.t(`api_errors.${data.code}`, { defaultValue: errorMsg });
    }

    throw new Error(errorMsg);
  }

  const responseData =
    data?.data !== undefined ? data.data : ((data as T | null) ?? null);

  return responseData as T;
}

export const api = {
  get: <T>(path: string, options?: RequestOptions) =>
    request<T>(path, { ...options, method: "GET" }),
  post: <T, B = unknown>(path: string, body?: B, options?: RequestOptions) =>
    request<T>(path, {
      ...options,
      method: "POST",
      body: body ? JSON.stringify(body) : undefined,
    }),
  put: <T, B = unknown>(path: string, body?: B, options?: RequestOptions) =>
    request<T>(path, {
      ...options,
      method: "PUT",
      body: body ? JSON.stringify(body) : undefined,
    }),
  delete: <T>(path: string, options?: RequestOptions) =>
    request<T>(path, { ...options, method: "DELETE" }),
};

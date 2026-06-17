import i18n from "../i18n";

const BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1";
const REQUEST_TIMEOUT_MS = 10000;

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

  if (workspaceId && !path.startsWith("/admin/")) {
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
    credentials: "include",
  };

  const controller = new AbortController();
  const timeoutId = window.setTimeout(
    () => controller.abort(),
    REQUEST_TIMEOUT_MS,
  );

  let response: Response;
  try {
    response = await fetch(url, {
      ...config,
      signal: controller.signal,
    });
  } catch (error) {
    if (error instanceof DOMException && error.name === "AbortError") {
      throw new Error("Yeu cau toi backend bi timeout sau 10 giay.");
    }
    throw error;
  } finally {
    window.clearTimeout(timeoutId);
  }

  if (response.status === 401 || response.status === 403) {
    // Auth errors: if not logging in, dispatch event to logout cleanly
    if (
      path !== "/auth/login" &&
      path !== "/auth/refresh" &&
      path !== "/auth/logout"
    ) {
      setInMemoryToken(null);
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

const API_BASE = import.meta.env.VITE_API_BASE_URL || "/api";

function parseResponse(response) {
  const contentType = response.headers.get("content-type") || "";
  if (response.status === 204) {
    return Promise.resolve(null);
  }
  if (contentType.includes("application/json")) {
    return response.json();
  }
  return response.text();
}

function normalizeErrorPayload(payload) {
  if (!payload) return "Unexpected error";
  if (typeof payload === "string") return payload;
  if (typeof payload === "object") return Object.values(payload).join(" | ");
  return "Unexpected error";
}

export async function apiRequest(
  path,
  { method = "GET", token = "", body = null, isFormData = false } = {}
) {
  const headers = {};
  if (token) {
    headers.Authorization = `Basic ${token}`;
  }
  if (!isFormData) {
    headers["Content-Type"] = "application/json";
  }

  const response = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    body: body
      ? isFormData
        ? body
        : JSON.stringify(body)
      : null
  });

  const payload = await parseResponse(response);
  if (!response.ok) {
    throw new Error(normalizeErrorPayload(payload));
  }

  return payload;
}

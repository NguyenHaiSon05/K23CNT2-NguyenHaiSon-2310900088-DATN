const BASE_URL = "http://localhost:8080";

export function getAuth() {
  const raw = localStorage.getItem("auth");
  return raw ? JSON.parse(raw) : null;
}

export function saveAuth(data) {
  localStorage.setItem("auth", JSON.stringify(data));
}

export function logout() {
  localStorage.removeItem("auth");
}

export async function apiFetch(path, options = {}) {
  const auth = getAuth();
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
  if (auth?.token) headers.Authorization = "Bearer " + auth.token;

  const res = await fetch(BASE_URL + path, { ...options, headers });
  if (res.status === 401) {
    logout();
    window.location.href = "/login";
    throw new Error("Phiên đăng nhập hết hạn");
  }
  if (!res.ok) throw new Error("Lỗi " + res.status);
  return res.json();
}
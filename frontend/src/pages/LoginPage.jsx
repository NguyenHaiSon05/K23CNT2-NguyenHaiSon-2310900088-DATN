import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { saveAuth } from "../api";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    try {
      const res = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });
      if (!res.ok) {
        setError("Sai tên đăng nhập hoặc mật khẩu");
        return;
      }
      saveAuth(await res.json());
      navigate("/");
    } catch {
      setError("Không kết nối được máy chủ");
    }
  }

  return (
    <div className="container" style={{ maxWidth: 360 }}>
      <h1>Đăng nhập RoomS</h1>
      <form onSubmit={handleSubmit}>
        <p>
          <input placeholder="Tên đăng nhập" value={username}
            onChange={(e) => setUsername(e.target.value)} style={{ width: "100%", padding: 8 }} />
        </p>
        <p>
          <input type="password" placeholder="Mật khẩu" value={password}
            onChange={(e) => setPassword(e.target.value)} style={{ width: "100%", padding: 8 }} />
        </p>
        {error && <p className="error">{error}</p>}
        <button type="submit" style={{ padding: "8px 16px" }}>Đăng nhập</button>
      </form>
    </div>
  );
}
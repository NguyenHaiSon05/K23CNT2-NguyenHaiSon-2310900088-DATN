import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiFetch, getAuth, logout } from "../api";

export default function RoomListPage() {
  const [rooms, setRooms] = useState([]);
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const auth = getAuth();

  useEffect(() => {
    apiFetch("/api/rooms")
      .then(setRooms)
      .catch((err) => setError(err.message));
  }, []);

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <div className="container">
      <p>
        Xin chào <b>{auth?.fullName}</b> ({auth?.role}){" "}
        <button onClick={handleLogout}>Đăng xuất</button>
      </p>
      <h1>RoomS - Danh sách phòng</h1>
      {error && <p className="error">{error}</p>}
      <table>
        <thead>
          <tr>
            <th>Mã phòng</th><th>Tên phòng</th><th>Sức chứa</th><th>Vị trí</th><th>Trạng thái</th>
          </tr>
        </thead>
        <tbody>
          {rooms.map((r) => (
            <tr key={r.id}>
              <td>{r.roomCode}</td><td>{r.name}</td><td>{r.capacity}</td>
              <td>{r.location}</td><td>{r.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
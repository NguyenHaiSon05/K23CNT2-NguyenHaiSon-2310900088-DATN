import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import { apiFetch, getAuth, logout } from "../api";

const EMPTY_FORM = {
  roomCode: "", name: "", capacity: 30, location: "", status: "ACTIVE", roomTypeId: "",
};

export default function RoomListPage() {
  const [rooms, setRooms] = useState([]);
  const [types, setTypes] = useState([]);
  const [error, setError] = useState("");
  const [form, setForm] = useState(null);   // null = ẩn form
  const [editingId, setEditingId] = useState(null);
  const navigate = useNavigate();
  const auth = getAuth();
  const canManage = auth?.role === "ADMIN" || auth?.role === "ROOM_MANAGER";

  async function loadRooms() {
    try {
      setRooms(await apiFetch("/api/rooms"));
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => {
    loadRooms();
    apiFetch("/api/room-types").then(setTypes).catch(() => {});
  }, []);

  function openCreate() {
    setEditingId(null);
    setForm({ ...EMPTY_FORM });
    setError("");
  }

  function openEdit(room) {
    setEditingId(room.id);
    setForm({
      roomCode: room.roomCode,
      name: room.name,
      capacity: room.capacity,
      location: room.location || "",
      status: room.status,
      roomTypeId: room.roomType ? room.roomType.id : "",
    });
    setError("");
  }

  async function handleSave(e) {
    e.preventDefault();
    setError("");
    const body = {
      ...form,
      capacity: Number(form.capacity),
      roomTypeId: form.roomTypeId === "" ? null : Number(form.roomTypeId),
    };
    try {
      if (editingId) {
        await apiFetch("/api/rooms/" + editingId, { method: "PUT", body: JSON.stringify(body) });
      } else {
        await apiFetch("/api/rooms", { method: "POST", body: JSON.stringify(body) });
      }
      setForm(null);
      loadRooms();
    } catch (err) {
      setError(err.message);
    }
  }

  async function handleDelete(room) {
    if (!window.confirm("Xóa phòng " + room.roomCode + "?")) return;
    try {
      await apiFetch("/api/rooms/" + room.id, { method: "DELETE" });
      loadRooms();
    } catch (err) {
      setError(err.message);
    }
  }

  function handleLogout() {
    logout();
    navigate("/login");
  }

  function setField(name, value) {
    setForm((f) => ({ ...f, [name]: value }));
  }

  return (
    <div className="container">
      <p>
        Xin chào <b>{auth?.fullName}</b> ({auth?.role}){" "}
        <button onClick={handleLogout}>Đăng xuất</button>
      </p>
      <h1>RoomS - Danh sách phòng</h1>
      <p><Link to="/booking">→ Tìm phòng trống và đặt phòng</Link></p>
      {canManage && <p><Link to="/approvals">→ Duyệt đặt phòng</Link></p>}
      {error && <p className="error">{error}</p>}

      {canManage && !form && <p><button onClick={openCreate}>+ Thêm phòng</button></p>}

      {form && (
        <form onSubmit={handleSave} style={{ border: "1px solid #ccc", padding: 12, marginBottom: 16 }}>
          <h3>{editingId ? "Sửa phòng" : "Thêm phòng"}</h3>
          <p><input placeholder="Mã phòng" value={form.roomCode}
            onChange={(e) => setField("roomCode", e.target.value)} /></p>
          <p><input placeholder="Tên phòng" value={form.name}
            onChange={(e) => setField("name", e.target.value)} /></p>
          <p><input type="number" placeholder="Sức chứa" value={form.capacity}
            onChange={(e) => setField("capacity", e.target.value)} /></p>
          <p><input placeholder="Vị trí" value={form.location}
            onChange={(e) => setField("location", e.target.value)} /></p>
          <p>
            <select value={form.roomTypeId} onChange={(e) => setField("roomTypeId", e.target.value)}>
              <option value="">-- Loại phòng --</option>
              {types.map((t) => <option key={t.id} value={t.id}>{t.name}</option>)}
            </select>{" "}
            <select value={form.status} onChange={(e) => setField("status", e.target.value)}>
              <option value="ACTIVE">ACTIVE</option>
              <option value="MAINTENANCE">MAINTENANCE</option>
            </select>
          </p>
          <button type="submit">Lưu</button>{" "}
          <button type="button" onClick={() => setForm(null)}>Hủy</button>
        </form>
      )}

      <table>
        <thead>
          <tr>
            <th>Mã phòng</th><th>Tên phòng</th><th>Loại</th><th>Sức chứa</th>
            <th>Vị trí</th><th>Trạng thái</th>{canManage && <th>Thao tác</th>}
          </tr>
        </thead>
        <tbody>
          {rooms.map((r) => (
            <tr key={r.id}>
              <td>{r.roomCode}</td><td>{r.name}</td>
              <td>{r.roomType ? r.roomType.name : ""}</td>
              <td>{r.capacity}</td><td>{r.location}</td><td>{r.status}</td>
              {canManage && (
                <td>
                  <button onClick={() => openEdit(r)}>Sửa</button>{" "}
                  <button onClick={() => handleDelete(r)}>Xóa</button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
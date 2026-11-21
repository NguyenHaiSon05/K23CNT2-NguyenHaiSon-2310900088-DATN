import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { apiFetch } from "../api";

export default function ApprovalPage() {
  const [items, setItems] = useState([]);
  const [error, setError] = useState("");

  async function load() {
    try {
      setItems(await apiFetch("/api/bookings/pending"));
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => { load(); }, []);

  async function decide(id, action) {
    setError("");
    try {
      await apiFetch(`/api/bookings/${id}/${action}`, { method: "PUT" });
      load();
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="container">
      <p><Link to="/">← Danh sách phòng</Link></p>
      <h1>Duyệt đặt phòng</h1>
      {error && <p className="error">{error}</p>}
      <table>
        <thead>
          <tr><th>Phòng</th><th>Người đặt</th><th>Ngày</th><th>Giờ</th><th>Mục đích</th><th></th></tr>
        </thead>
        <tbody>
          {items.length === 0 && <tr><td colSpan={6}>Không có lượt nào chờ duyệt</td></tr>}
          {items.map((b) => (
            <tr key={b.id}>
              <td>{b.roomCode}</td>
              <td>{b.fullName}</td>
              <td>{b.bookingDate}</td>
              <td>{b.startTime.slice(0, 5)} - {b.endTime.slice(0, 5)}</td>
              <td>{b.purpose}</td>
              <td>
                <button onClick={() => decide(b.id, "approve")}>Duyệt</button>{" "}
                <button onClick={() => decide(b.id, "reject")}>Từ chối</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
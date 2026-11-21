import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { apiFetch } from "../api";

const STATUS_TEXT = {
  PENDING: "Chờ duyệt",
  APPROVED: "Đã duyệt",
  REJECTED: "Bị từ chối",
  CANCELLED: "Đã hủy",
};

export default function MyBookingsPage() {
  const [items, setItems] = useState([]);
  const [error, setError] = useState("");

  async function load() {
    try {
      setItems(await apiFetch("/api/bookings/my"));
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => { load(); }, []);

  async function handleCancel(b) {
    if (!window.confirm("Hủy đặt phòng " + b.roomCode + " ngày " + b.bookingDate + "?")) return;
    setError("");
    try {
      await apiFetch(`/api/bookings/${b.id}/cancel`, { method: "PUT" });
      load();
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="container">
      <p><Link to="/">← Danh sách phòng</Link></p>
      <h1>Lịch đặt phòng của tôi</h1>
      {error && <p className="error">{error}</p>}
      <table>
        <thead>
          <tr><th>Phòng</th><th>Ngày</th><th>Giờ</th><th>Mục đích</th><th>Trạng thái</th><th></th></tr>
        </thead>
        <tbody>
          {items.length === 0 && <tr><td colSpan={6}>Bạn chưa có lượt đặt nào</td></tr>}
          {items.map((b) => (
            <tr key={b.id}>
              <td>{b.roomCode}</td>
              <td>{b.bookingDate}</td>
              <td>{b.startTime.slice(0, 5)} - {b.endTime.slice(0, 5)}</td>
              <td>{b.purpose}</td>
              <td>{STATUS_TEXT[b.status] || b.status}</td>
              <td>
                {(b.status === "PENDING" || b.status === "APPROVED") && (
                  <button onClick={() => handleCancel(b)}>Hủy</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { apiFetch } from "../api";

const STATUS_TEXT = {
  PENDING: "Chờ duyệt", APPROVED: "Đã duyệt", REJECTED: "Bị từ chối", CANCELLED: "Đã hủy",
};

export default function StatsPage() {
  const [stats, setStats] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    apiFetch("/api/stats").then(setStats).catch((err) => setError(err.message));
  }, []);

  return (
    <div className="container">
      <p><Link to="/">← Danh sách phòng</Link></p>
      <h1>Thống kê</h1>
      {error && <p className="error">{error}</p>}
      {stats && (
        <>
          <p>Tổng số phòng: <b>{stats.totalRooms}</b> | Tổng lượt đặt: <b>{stats.totalBookings}</b></p>

          <h3>Lượt đặt theo trạng thái</h3>
          <table>
            <thead><tr><th>Trạng thái</th><th>Số lượt</th></tr></thead>
            <tbody>
              {Object.entries(stats.byStatus).map(([k, v]) => (
                <tr key={k}><td>{STATUS_TEXT[k] || k}</td><td>{v}</td></tr>
              ))}
            </tbody>
          </table>

          <h3>Phòng được đặt nhiều nhất</h3>
          <table>
            <thead><tr><th>Mã phòng</th><th>Tên phòng</th><th>Số lượt</th></tr></thead>
            <tbody>
              {stats.topRooms.length === 0 && <tr><td colSpan={3}>Chưa có dữ liệu</td></tr>}
              {stats.topRooms.map((t) => (
                <tr key={t.roomCode}><td>{t.roomCode}</td><td>{t.roomName}</td><td>{t.count}</td></tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  );
}
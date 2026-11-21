import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { apiFetch } from "../api";

const DAY_NAMES = ["Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"];
const HOURS = Array.from({ length: 14 }, (_, i) => i + 6); // 6h đến 19h

function toIso(d) {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

function mondayOf(d) {
  const x = new Date(d);
  const diff = (x.getDay() + 6) % 7; // Chủ nhật = 0 -> lùi 6 ngày
  x.setDate(x.getDate() - diff);
  x.setHours(0, 0, 0, 0);
  return x;
}

export default function CalendarPage() {
  const [rooms, setRooms] = useState([]);
  const [roomId, setRoomId] = useState("");
  const [monday, setMonday] = useState(mondayOf(new Date()));
  const [items, setItems] = useState([]);
  const [error, setError] = useState("");

  const days = Array.from({ length: 7 }, (_, i) => {
    const d = new Date(monday);
    d.setDate(d.getDate() + i);
    return d;
  });

  useEffect(() => {
    apiFetch("/api/rooms")
      .then((list) => {
        setRooms(list);
        if (list.length > 0) setRoomId(String(list[0].id));
      })
      .catch((err) => setError(err.message));
  }, []);

  useEffect(() => {
    if (!roomId) return;
    const from = toIso(days[0]);
    const to = toIso(days[6]);
    apiFetch(`/api/bookings/calendar?roomId=${roomId}&from=${from}&to=${to}`)
      .then((data) => { setItems(data); setError(""); })
      .catch((err) => setError(err.message));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [roomId, monday]);

  function shiftWeek(n) {
    const d = new Date(monday);
    d.setDate(d.getDate() + n * 7);
    setMonday(d);
  }

  // Tìm lượt đặt phủ ô (ngày, giờ)
  function findBooking(day, hour) {
    const iso = toIso(day);
    return items.find((b) => {
      if (b.bookingDate !== iso) return false;
      const s = parseInt(b.startTime.slice(0, 2), 10);
      const e = parseInt(b.endTime.slice(0, 2), 10) + (b.endTime.slice(3, 5) !== "00" ? 1 : 0);
      return hour >= s && hour < e;
    });
  }

  return (
    <div className="container" style={{ maxWidth: 1100 }}>
      <p><Link to="/">← Danh sách phòng</Link></p>
      <h1>Lịch tuần của phòng</h1>
      {error && <p className="error">{error}</p>}

      <p>
        <select value={roomId} onChange={(e) => setRoomId(e.target.value)}>
          {rooms.map((r) => <option key={r.id} value={r.id}>{r.roomCode} - {r.name}</option>)}
        </select>{" "}
        <button onClick={() => shiftWeek(-1)}>← Tuần trước</button>{" "}
        <button onClick={() => setMonday(mondayOf(new Date()))}>Tuần này</button>{" "}
        <button onClick={() => shiftWeek(1)}>Tuần sau →</button>
      </p>

      <table>
        <thead>
          <tr>
            <th>Giờ</th>
            {days.map((d, i) => (
              <th key={i}>{DAY_NAMES[i]}<br />{d.getDate()}/{d.getMonth() + 1}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {HOURS.map((h) => (
            <tr key={h}>
              <td>{h}:00</td>
              {days.map((d, i) => {
                const b = findBooking(d, h);
                return (
                  <td key={i} style={b ? {
                    background: b.status === "APPROVED" ? "#2e7d32" : "#f9a825",
                    color: "#fff", fontSize: 12,
                  } : {}}>
                    {b ? `${b.fullName}${b.status === "PENDING" ? " (chờ)" : ""}` : ""}
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
      <p style={{ fontSize: 12 }}>Xanh: đã duyệt. Vàng: chờ duyệt.</p>
    </div>
  );
}
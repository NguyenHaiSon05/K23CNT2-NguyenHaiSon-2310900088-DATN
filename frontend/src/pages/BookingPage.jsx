import { useState } from "react";
import { Link } from "react-router-dom";
import { apiFetch, getAuth } from "../api";

export default function BookingPage() {
  const [date, setDate] = useState("");
  const [start, setStart] = useState("07:00");
  const [end, setEnd] = useState("09:00");
  const [purpose, setPurpose] = useState("");
  const [rooms, setRooms] = useState(null);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const auth = getAuth();

  async function handleSearch(e) {
    e.preventDefault();
    setError("");
    setMessage("");
    try {
      setRooms(await apiFetch(`/api/rooms/available?date=${date}&start=${start}&end=${end}`));
    } catch (err) {
      setRooms(null);
      setError(err.message);
    }
  }

  async function handleBook(room) {
    setError("");
    setMessage("");
    try {
      const b = await apiFetch("/api/bookings", {
        method: "POST",
        body: JSON.stringify({
          roomId: room.id, bookingDate: date, startTime: start, endTime: end, purpose,
        }),
      });
      setMessage(
        `Đặt phòng ${b.roomCode} thành công. Trạng thái: ` +
        (b.status === "PENDING" ? "chờ quản lý duyệt" : "đã được duyệt")
      );
      setRooms(await apiFetch(`/api/rooms/available?date=${date}&start=${start}&end=${end}`));
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="container">
      <p><Link to="/">← Danh sách phòng</Link> | Xin chào <b>{auth?.fullName}</b></p>
      <h1>Tìm phòng trống và đặt phòng</h1>

      <form onSubmit={handleSearch} style={{ marginBottom: 16 }}>
        <input type="date" value={date} onChange={(e) => setDate(e.target.value)} required />{" "}
        <input type="time" value={start} onChange={(e) => setStart(e.target.value)} required />{" "}
        <input type="time" value={end} onChange={(e) => setEnd(e.target.value)} required />{" "}
        <input placeholder="Mục đích" value={purpose} onChange={(e) => setPurpose(e.target.value)} />{" "}
        <button type="submit">Tìm phòng trống</button>
      </form>

      {error && <p className="error">{error}</p>}
      {message && <p style={{ color: "green" }}>{message}</p>}

      {rooms && (
        <table>
          <thead>
            <tr><th>Mã phòng</th><th>Tên phòng</th><th>Sức chứa</th><th>Vị trí</th><th></th></tr>
          </thead>
          <tbody>
            {rooms.length === 0 && <tr><td colSpan={5}>Không có phòng trống trong khung giờ này</td></tr>}
            {rooms.map((r) => (
              <tr key={r.id}>
                <td>{r.roomCode}</td><td>{r.name}</td><td>{r.capacity}</td><td>{r.location}</td>
                <td><button onClick={() => handleBook(r)}>Đặt phòng</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
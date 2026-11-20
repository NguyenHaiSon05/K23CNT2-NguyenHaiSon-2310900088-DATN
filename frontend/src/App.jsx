import { useEffect, useState } from "react";
import "./App.css";

function App() {
  const [rooms, setRooms] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/api/rooms")
      .then((res) => {
        if (!res.ok) throw new Error("Lỗi " + res.status);
        return res.json();
      })
      .then((data) => setRooms(data))
      .catch((err) => setError(err.message));
  }, []);

  return (
    <div className="container">
      <h1>RoomS - Danh sách phòng</h1>
      {error && <p className="error">Không tải được dữ liệu: {error}</p>}
      <table>
        <thead>
          <tr>
            <th>Mã phòng</th>
            <th>Tên phòng</th>
            <th>Sức chứa</th>
            <th>Vị trí</th>
            <th>Trạng thái</th>
          </tr>
        </thead>
        <tbody>
          {rooms.map((room) => (
            <tr key={room.id}>
              <td>{room.roomCode}</td>
              <td>{room.name}</td>
              <td>{room.capacity}</td>
              <td>{room.location}</td>
              <td>{room.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default App;
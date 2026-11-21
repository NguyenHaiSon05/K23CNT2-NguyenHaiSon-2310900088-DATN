import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import "./App.css";
import { getAuth } from "./api";
import LoginPage from "./pages/LoginPage";
import RoomListPage from "./pages/RoomListPage";
import BookingPage from "./pages/BookingPage";
import ApprovalPage from "./pages/ApprovalPage";
import MyBookingsPage from "./pages/MyBookingsPage";
import CalendarPage from "./pages/CalendarPage";
import StatsPage from "./pages/StatsPage";

function PrivateRoute({ children }) {
  return getAuth() ? children : <Navigate to="/login" replace />;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<PrivateRoute><RoomListPage /></PrivateRoute>} />
        <Route path="/booking" element={<PrivateRoute><BookingPage /></PrivateRoute>} />
        <Route path="/approvals" element={<PrivateRoute><ApprovalPage /></PrivateRoute>} />
        <Route path="/my-bookings" element={<PrivateRoute><MyBookingsPage /></PrivateRoute>} />
        <Route path="/calendar" element={<PrivateRoute><CalendarPage /></PrivateRoute>} />
        <Route path="/stats" element={<PrivateRoute><StatsPage /></PrivateRoute>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
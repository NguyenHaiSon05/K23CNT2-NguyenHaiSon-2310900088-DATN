import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import "./App.css";
import { getAuth } from "./api";
import LoginPage from "./pages/LoginPage";
import RoomListPage from "./pages/RoomListPage";
import BookingPage from "./pages/BookingPage";
import ApprovalPage from "./pages/ApprovalPage";

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
      </Routes>
    </BrowserRouter>
  );
}

export default App;
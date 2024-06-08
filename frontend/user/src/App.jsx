import { Routes, Route } from "react-router-dom";
import WelcomePage from "./pages/WelcomePage";
import LoginResultPage from "./pages/LoginResultPage";

export default function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<WelcomePage />} />
        <Route path="/oauth/redirect" element={<LoginResultPage />} />
      </Routes>
    </>
  );
}

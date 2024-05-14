import { useState } from "react";
import "../../index.css";
import LogoValhalla from "../../../public/assets/images/Generation_Valhalla.svg";

const LoginComponent = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });

  const [error, setError] = useState(null);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.username)) {
      setError("El correo electrónico no es válido");
      return;
    }

    if (formData.password.length < 8) {
      setError("La contraseña debe tener al menos 8 caracteres");
      return;
    }

    try {
      const response = await fetch("https://localhost:8080", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        throw new Error("Error al enviar los datos");
      }

      const data = await response.json();
      console.log(data);
    } catch (error) {
      console.error("Error:", error);
    }
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  return (
    <>
    <div className="login-container">
      <section className="d-flex justify-center align-items flex-direction-column">
        <h1 className="text-5xl font-SemiBold py-3 font-roboto">Login</h1>
        <hr className="line" />
        <div className="text-xl font-extralight py-1 font-roboto">
          Bienvenido de nuevo a tu crm de confianza
        </div>
        <div className="text-base font-extralight py-4 font-roboto">
          Has olvidado{" "}
          <span className="forgot-password">¿tu contraseña?</span>
        </div>

        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="d-flex flex-direction-column mr-10 w-25 text-left">
            <label className="font-thin text-lg font-roboto">
              Username *
            </label>
            <input
              type="text"
              className="login__input  px-2 py-2"
              name="username"
              value={formData.username}
              onChange={handleChange}
            />
          </div>
          <div className="font-roboto d-flex flex-direction-column w-25 mr-10 py-8 text-left">
            <label className="font-thin text-lg">Password *</label>
            <div className="password-input-container">
              <input
                type={showPassword ? "text" : "password"}
                className="login__input  px-2 py-2"
                name="password"
                value={formData.password}
                onChange={handleChange}
              />
              <span
                className="toggle-password"
                onClick={togglePasswordVisibility}
              >
                {showPassword ? "👁️" : "🔒"}
              </span>
            </div>
          </div>
          <button className="font-roboto mb-3 button-login pt-2 pb-2 ">
            Entrar
          </button>
        </form>
        <button className="font-roboto mt-3 pb-2 pt-2 button-create">
          Crea una cuenta
        </button>
      </section>
      <div className="circle top-right"></div>
      <div className="circle small bottom-left"></div>
      <div className="image-valhalla">
        <img
          className="logo-valhalla top-left"
          src={LogoValhalla}
          alt="valhalla"
        />
      </div>
    </div>
  </>
  );
};

export default LoginComponent;

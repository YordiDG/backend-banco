# backend-banco

# 📘 Documentación de Endpoints – API de Gestión Financiera

## 🔐 Autenticación

### ▶️ Registro de usuario
- **Método:** `POST`
- **Ruta:** `/api/auth/register`
- **Descripción:** Registra un nuevo usuario en el sistema.
- **Cuerpo de solicitud (JSON):**
```json
{
  "username": "usuario1",
  "password": "123456"
}
```

---

### 🔓 Inicio de sesión
- **Método:** `POST`
- **Ruta:** `/api/auth/login`
- **Descripción:** Inicia sesión y retorna un token JWT para autenticación.
- **Cuerpo de solicitud (JSON):**
```json
{
  "username": "usuario1",
  "password": "123456"
}
```

---

## 🏦 Cuentas

### 📋 Listar todas las cuentas
- **Método:** `GET`
- **Ruta:** `/api/cuentas`
- **Descripción:** Devuelve un listado con todas las cuentas registradas.
- **Ejemplo de respuesta:**
```json
[
  {
    "id": 1,
    "saldo": 5000,
    "titularCuenta": "John Doe"
  },
  {
    "id": 2,
    "saldo": 200,
    "titularCuenta": "Jane Smith"
  }
]
```

---

### 🔍 Obtener detalle de una cuenta
- **Método:** `GET`
- **Ruta:** `/api/cuentas/{id}`
- **Descripción:** Obtiene el detalle completo de una cuenta específica, incluyendo historial de transacciones.
- **Ejemplo de respuesta:**
```json
{
  "id": 1,
  "saldo": 5000,
  "historialTransacciones": [
    {
      "tipo": "depósito",
      "monto": 5000,
      "fecha": "2024-09-10 10:00:00"
    }
  ]
}
```

---

### 🕓 Obtener historial de transacciones
- **Método:** `GET`
- **Ruta:** `/api/cuentas/{id}/transacciones`
- **Descripción:** Lista todas las transacciones asociadas a una cuenta específica.

---

## 💰 Transacciones

### ➕ Realizar depósito
- **Método:** `POST`
- **Ruta:** `/api/cuentas/{id}/depositar`
- **Descripción:** Permite agregar fondos a una cuenta.
- **Cuerpo de solicitud (JSON):**
```json
{
  "monto": 1000
}
```

---

### ➖ Realizar retiro
- **Método:** `POST`
- **Ruta:** `/api/cuentas/{id}/retirar`
- **Descripción:** Permite retirar fondos de una cuenta. Aplica una comisión del 2% si es una `CuentaEstandar`.
- **Cuerpo de solicitud (JSON):**
```json
{
  "monto": 200
}
```

---

### 🔄 Realizar transferencia
- **Método:** `POST`
- **Ruta:** `/api/cuentas/{id}/transferir`
- **Descripción:** Permite transferir fondos desde una cuenta origen a una cuenta destino. Aplica una comisión del 1% en cuentas `CuentaEstandar`.
- **Cuerpo de solicitud (JSON):**
```json
{
  "cuentaDestinoId": 2,
  "monto": 500
}
```

---

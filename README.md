# Virtual Linux File System Simulator 🚀🐧

¡Bienvenido al **Virtual Linux File System Simulator**! Este es un proyecto Full-Stack diseñado para simular el comportamiento de un sistema de archivos basado en Linux (como la gestión de directorios, inodos y comandos de terminal) completamente en memoria (RAM), utilizando estructuras de datos avanzadas.

## 🔗 Enlaces del Proyecto

* **Live Demo (Frontend):** [https://diego-virtual-linux.vercel.app/](https://diego-virtual-linux.vercel.app/)
* **Servidor API (Backend):** [https://virtualos-backend.onrender.com/](https://virtualos-backend.onrender.com/)

---

## 🏗️ Arquitectura del Sistema

El proyecto está diseñado bajo una arquitectura desacoplada (Client-Server) distribuida en la nube:

```text
┌─────────────────────────────────┐        Peticiones HTTP (POST)       ┌─────────────────────────────────┐
│        Frontend (React)         │  ────────────────────────────────>  │        Backend (Spring)         │
│   Desplegado global en Vercel   │  <────────────────────────────────  │   Contenedor Docker en Render   │
└─────────────────────────────────┘             Respuestas JSON         └─────────────────────────────────┘

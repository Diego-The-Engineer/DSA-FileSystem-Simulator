# Virtual Linux File System Simulator 
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E)
![React](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
#
¡Bienvenido al **Virtual Linux File System Simulator**! Este es un proyecto Full-Stack diseñado para simular el comportamiento de un sistema de archivos basado en Linux (como la gestión de directorios, inodos y comandos de terminal) completamente en memoria (RAM), utilizando estructuras de datos avanzadas.

Enlace del Proyecto

* **Live Demo (Frontend):** [https://diego-virtual-linux.vercel.app/](https://diego-virtual-linux.vercel.app/)
* AVISO IMPORTANTE: Al ser desplegado con servicios gratuitos, si en un periodo de 15 minutos nadie ha usado el servicio de la página, la respuesta de comunicación de la API puede tardar un poco, aproximadamente 1 minuto.

---

Arquitectura del Sistema

El proyecto está diseñado bajo una arquitectura desacoplada (Client-Server) distribuida en la nube:

```text
┌─────────────────────────────────┐        Peticiones HTTP (POST)       ┌─────────────────────────────────┐
│        Frontend (React)         │  ────────────────────────────────>  │        Backend (Spring)         │
│   Desplegado global en Vercel   │  <────────────────────────────────  │   Contenedor Docker en Render   │
└─────────────────────────────────┘             Respuestas JSON         └─────────────────────────────────┘

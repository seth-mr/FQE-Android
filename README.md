# FQE-Android

Cliente Android (Kotlin + Jetpack Compose) para autenticacion de FigurasQE.

## Implementado

- Login contra `POST /auth/login`
- Signup contra `POST /auth/register`
- Retrofit con `GsonConverterFactory`
- Persistencia de JWT en DataStore para mantener sesion
- Navegacion por rol (`student` / `tutor`) leyendo claim `role` del JWT

## Base URL

Se usa en debug y release:

- `http://10.0.2.2:3000/`

Esto apunta al host local desde el emulador Android Studio.

## Ejecutar

1. Levanta tus servicios locales:
- Gateway en `localhost:3000`
- Auth service accesible desde gateway

2. Abre la carpeta `FQE-Android` en Android Studio.

3. Sincroniza Gradle y ejecuta en emulador.

## Contratos usados

### Login

Request JSON:

```json
{
  "email": "user@mail.com",
  "password": "Secret123"
}
```

Response exitosa:

```json
{
  "token": "<jwt>"
}
```

### Signup

Request JSON:

```json
{
  "name": "Nombre",
  "email": "user@mail.com",
  "password": "Secret123",
  "age": 20,
  "genre": "M",
  "country": "MX",
  "role": "student",
  "neurodivergency": "ninguna",
  "degree": null
}
```

Para `tutor`, enviar `degree` y omitir `neurodivergency`.

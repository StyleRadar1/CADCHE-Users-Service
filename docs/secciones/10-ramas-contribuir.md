# 10. Ramas y contribución

El desarrollo del microservicio `ms-usuarios` se organiza mediante Git y GitHub, utilizando ramas para separar el trabajo y facilitar la integración de nuevas funcionalidades.

## 10.1. Estrategia de ramas

Se utilizan las siguientes ramas principales:

| Rama | Propósito |
|---|---|
| `main` | Contiene la versión estable del proyecto y corresponde al flujo de producción. |
| `develop` | Integra los cambios destinados al ambiente de QA. |
| `feature/*` | Permite desarrollar nuevas funcionalidades de manera independiente. |
| `fix/*` | Se utiliza para corregir errores identificados durante el desarrollo. |

## 10.2. Flujo de trabajo

El flujo recomendado para contribuir al proyecto es:

1. Actualizar la rama `develop`.
2. Crear una rama para la funcionalidad o corrección.
3. Realizar los cambios necesarios.
4. Ejecutar las pruebas y verificar que el proyecto compile.
5. Realizar commits con mensajes descriptivos.
6. Subir la rama al repositorio remoto.
7. Crear un Pull Request hacia `develop`.
8. Revisar e integrar los cambios después de validar el pipeline.

Cuando los cambios están listos para producción, se integran en `main` mediante el proceso correspondiente.

## 10.3. Convención de nombres

Se recomienda utilizar nombres descriptivos para las ramas:

```bash
feature/nombre-funcionalidad
fix/nombre-correccion
```

Ejemplos:

```bash
feature/verificacion-correo
feature/actualizar-perfil
fix/validacion-usuario
```

## 10.4. Comandos básicos de Git

Actualizar el repositorio:

```bash
git checkout develop
git pull origin develop
```

Crear una nueva rama:

```bash
git checkout -b feature/nombre-funcionalidad
```

Agregar y confirmar cambios:

```bash
git add .
git commit -m "feat: descripción del cambio"
```

Subir la rama:

```bash
git push -u origin feature/nombre-funcionalidad
```

## 10.5. Recomendaciones para contribuir

- Mantener los cambios relacionados con una sola funcionalidad.
- Utilizar nombres claros para las ramas y los commits.
- Ejecutar las pruebas antes de crear un Pull Request.
- Revisar los cambios antes de integrarlos.
- No subir contraseñas, tokens ni secretos al repositorio.
- Mantener actualizada la documentación cuando se modifique el comportamiento del servicio.

---

[← Anterior: 9. CI/CD](09-cicd-pipeline.md) · [Índice](../../README.md) · [Siguiente: 11. Seguridad y OWASP](11-seguridad-owasp.md)


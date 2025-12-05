# GitHub Actions Setup - FullSound CI/CD

Configuración de CI/CD para FullSound. El sistema construye la APK Android firmada, triggerea el deploy del backend, y sube todo a AWS.

## Flujo

```
Push a FullSound-KOTLIN → Build + Test + Firma APK → Release en GitHub → 
Trigger SPRINGBOOT → Build Backend + Frontend → Deploy a AWS S3/EC2
```

---

## 1. Generar Keystore

Crear keystore para firmar la APK:

```powershell
keytool -genkey -v -keystore fullsound-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias fullsound-key
```

Guarda las contraseñas que ingreses. Luego convertir a Base64:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("fullsound-release.jks")) | Set-Clipboard
```

**IMPORTANTE**: Guarda el archivo `.jks` en un lugar seguro. Si lo pierdes, no podrás actualizar la app en Google Play.

---

## 2. Configurar Secrets en FullSound-KOTLIN

URL: `https://github.com/VECTORG99/FullSound-KOTLIN/settings/secrets/actions`

Crear estos 5 secrets:

| Secret | Valor |
|--------|-------|
| `KEYSTORE_BASE64` | Output del comando Base64 anterior |
| `KEYSTORE_PASSWORD` | Password del keystore |
| `KEY_ALIAS` | `fullsound-key` |
| `KEY_PASSWORD` | Password de la key |
| `PAT_TOKEN` | Personal Access Token (ver siguiente sección) |

---

## 3. Crear Personal Access Token

URL: `https://github.com/settings/tokens`

1. Click "Generate new token (classic)"
2. Name: `FullSound CI/CD`
3. Scopes: Marcar `repo` (full control)
4. Generate token y copiarlo
5. Añadirlo como secret `PAT_TOKEN` en FullSound-KOTLIN

---

## 4. Verificar Secrets en FULLSOUND-SPRINGBOOT

URL: `https://github.com/VECTORG99/FULLSOUND-SPRINGBOOT/settings/secrets/actions`

Verificar que existan estos secrets (ya configurados):

- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_SESSION_TOKEN`
- `AWS_ACCOUNT_ID`
- `DB_PASSWORD`
- `JWT_SECRET`

No es necesario crear nuevos secrets en este repositorio.

---

## 5. Probar

### Push a main:
```bash
cd FullSound-KOTLIN
git add .
git commit -m "test: trigger CI/CD"
git push origin main
```

### Trigger manual:
1. `https://github.com/VECTORG99/FullSound-KOTLIN/actions`
2. Click "Build and Release APK"
3. Click "Run workflow"

---

## Resultados Esperados

**FullSound-KOTLIN:**
- Release con APK firmada: `fullsound-vX.apk` y `fullsound-latest.apk`

**FULLSOUND-SPRINGBOOT:**
- Release consolidado con APK incluida
- Deploy en AWS

**AWS S3:**
- `s3://fullsound-deployments-{ID}/fullsound-backend-latest.jar`
- `s3://fullsound-deployments-{ID}/mobile/fullsound-latest.apk`

---

## Troubleshooting

**Error: "Keystore was tampered with"**
- Verificar `KEYSTORE_PASSWORD` sea correcta

**Error: "repository_dispatch failed"**
- Verificar `PAT_TOKEN` tenga permisos `repo`
- Regenerar token si expiró

**Workflow no se triggea automáticamente**
- Verificar `PAT_TOKEN` esté configurado
- Revisar logs en el step "Trigger Backend Deployment"

---

## Checklist

FullSound-KOTLIN:
- [ ] 5 secrets configurados
- [ ] Workflows habilitados

FULLSOUND-SPRINGBOOT:
- [ ] 6 secrets de AWS verificados
- [ ] Workflows habilitados

Prueba:
- [ ] Push triggera build exitoso
- [ ] Release creado con APK
- [ ] SPRINGBOOT se triggea automáticamente
- [ ] APK sube a S3

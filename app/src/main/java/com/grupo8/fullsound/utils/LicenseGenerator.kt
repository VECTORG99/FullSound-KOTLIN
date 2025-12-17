package com.grupo8.fullsound.utils

import com.grupo8.fullsound.model.CarritoItem
import com.grupo8.fullsound.model.User
import java.text.SimpleDateFormat
import java.util.*

/**
 * Generador de licencias para beats comprados
 */
object LicenseGenerator {

    /**
     * Genera el contenido de la licencia para los beats comprados
     */
    fun generateLicenseContent(
        items: List<CarritoItem>,
        user: User?,
        licenseNumber: String = generateLicenseNumber()
    ): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val userName = user?.name ?: "Usuario"
        val userEmail = user?.email ?: "No disponible"
        val userRut = user?.rut ?: "No disponible"

        val beatsInfo = items.joinToString("\n") { item ->
            """
            │   - Título: ${item.titulo}
            │     Artista: ${item.artista ?: "N/A"}
            """.trimMargin()
        }

        return """
═══════════════════════════════════════════════════════════════════════════
                          LICENCIA DE USO EXCLUSIVO
                                FULLSOUND
═══════════════════════════════════════════════════════════════════════════

NÚMERO DE LICENCIA: $licenseNumber
FECHA DE EMISIÓN: $currentDate

───────────────────────────────────────────────────────────────────────────
1. INFORMACIÓN DEL LICENCIATARIO
───────────────────────────────────────────────────────────────────────────

Nombre: $userName
Email: $userEmail
RUT: $userRut

───────────────────────────────────────────────────────────────────────────
2. BEATS LICENCIADOS
───────────────────────────────────────────────────────────────────────────

$beatsInfo

───────────────────────────────────────────────────────────────────────────
3. CERTIFICACIÓN DE PROPIEDAD
───────────────────────────────────────────────────────────────────────────

FullSound certifica que el licenciatario arriba mencionado es el propietario
legítimo de los derechos de uso exclusivo sobre los beats listados en esta
licencia, adquiridos mediante compra válida en la plataforma FullSound.

───────────────────────────────────────────────────────────────────────────
4. TÉRMINOS Y CONDICIONES DE LA LICENCIA
───────────────────────────────────────────────────────────────────────────

4.1 DERECHOS OTORGADOS

Por medio de la presente licencia exclusiva de uso, FullSound otorga al
licenciatario los siguientes derechos sobre los beats adquiridos:

a) Uso ilimitado del beat para producción musical, grabación, mezcla y
   masterización de canciones.

b) Derecho de distribución digital y física de las obras creadas con el
   beat en todas las plataformas disponibles (streaming, descarga, CD, vinilo).

c) Derecho de monetización completa de las obras creadas.

d) Derecho de presentación pública y ejecución en vivo.

e) Derecho de sincronización para uso en videos, películas, publicidad,
   videojuegos y otros medios audiovisuales.

f) Duración ILIMITADA de todos los derechos otorgados.

4.2 USO EXCLUSIVO

Esta es una licencia de uso EXCLUSIVO. El beat no será vendido ni licenciado
a ninguna otra persona o entidad después de esta compra. El licenciatario
tiene derechos exclusivos sobre el uso comercial del beat.

4.3 RESTRICCIONES IMPORTANTES

a) NO TRANSFERIBLE: Esta licencia es personal e intransferible. El
   licenciatario NO puede vender, ceder, transferir o sublicenciar esta
   licencia a terceros.

b) NO REVENDIBLE: El licenciatario NO puede revender el beat en su forma
   original o modificada como producto musical independiente (beats, loops,
   samples, etc.).

c) PROPIEDAD INTELECTUAL: FullSound retiene todos los derechos de autor
   subyacentes del beat. Esta licencia otorga derechos de uso, no de
   propiedad del master original.

d) ATRIBUCIÓN: Se recomienda (pero no es obligatorio) acreditar al productor
   original en las obras creadas.

4.4 GARANTÍAS

FullSound garantiza que:

a) Es el titular legítimo de los derechos sobre los beats licenciados.

b) Los beats no infringen derechos de terceros.

c) El licenciatario puede usar los beats de acuerdo a los términos aquí
   establecidos sin temor a reclamaciones de terceros.

4.5 VIGENCIA

Esta licencia entra en vigor en la fecha de emisión indicada arriba y
tiene una duración ILIMITADA, sujeta al cumplimiento de los términos
aquí establecidos.

4.6 INCUMPLIMIENTO

El incumplimiento de cualquiera de los términos de esta licencia resultará
en la terminación inmediata de todos los derechos otorgados, y el
licenciatario deberá cesar todo uso de los beats.

4.7 LEY APLICABLE

Esta licencia se rige por las leyes de la República de Chile.

───────────────────────────────────────────────────────────────────────────
5. CONFIRMACIÓN
───────────────────────────────────────────────────────────────────────────

Al realizar esta compra, el licenciatario confirma haber leído, entendido
y aceptado todos los términos y condiciones de esta licencia.

Este documento constituye prueba legal de la transacción y los derechos
adquiridos. Conserve este documento para sus registros.

───────────────────────────────────────────────────────────────────────────

© ${Calendar.getInstance().get(Calendar.YEAR)} FullSound. Todos los derechos reservados.
Documento generado automáticamente.

═══════════════════════════════════════════════════════════════════════════
        """.trimIndent()
    }

    /**
     * Genera un número de licencia único
     */
    private fun generateLicenseNumber(): String {
        val timestamp = System.currentTimeMillis()
        val random = UUID.randomUUID().toString().substring(0, 8).uppercase()
        return "FS-$timestamp-$random"
    }

    /**
     * Genera el nombre del archivo de licencia
     */
    fun generateLicenseFileName(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        return "Licencia_FullSound_$currentDate.txt"
    }
}


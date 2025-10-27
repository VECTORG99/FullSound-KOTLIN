# Proyecto FullSound
Este proyecto es una tienda de Beats donde puedes comprar y vender beats.

## Rutas principales del proyecto

| Componente         | Ruta principal                                                                 |
|--------------------|-------------------------------------------------------------------------------|
| MainActivity       | app/src/main/java/com/grupo8/fullsound/MainActivity.kt                        |
| Login              | app/src/main/java/com/grupo8/fullsound/ui/auth/login/LoginFragment.kt         |
| Registro           | app/src/main/java/com/grupo8/fullsound/ui/auth/register/RegisterFragment.kt   |
| Beats (CRUD)       | app/src/main/java/com/grupo8/fullsound/ui/beats/BeatsFragment.kt              |
| Lista de Beats     | app/src/main/java/com/grupo8/fullsound/ui/beats/BeatsListaFragment.kt         |
| Carrito            | app/src/main/java/com/grupo8/fullsound/ui/Carrito/CarritoFragment.kt          |
| ViewModels         | app/src/main/java/com/grupo8/fullsound/viewmodel/                             |
| Repositorios       | app/src/main/java/com/grupo8/fullsound/repository/                            |
| Modelos            | app/src/main/java/com/grupo8/fullsound/model/                                 |
| Base de datos      | app/src/main/java/com/grupo8/fullsound/data/local/                            |
| Layouts XML        | app/src/main/res/layout/                                                      |
| Navegación         | app/src/main/res/navigation/nav_graph.xml                                     |
| Documentación      | Documentacion/                                                                |

## Arquitectura
- MVVM (Model-View-ViewModel)
- Room Database
- Navigation Component
- Material Design 3

## Compilación

```bash
./gradlew.bat assembleDebug
```

## Funcionalidades Implementadas
- ✅ Sistema de autenticacion (Login/Registro)
- ✅ CRUD completo de Beats
- ✅ Reglas de negocio (No se puede agregar mas de un beat por ejemplo)
- ✅ Persistencia con Room Database
- ✅ Navegación con Navigation Component
- ✅ Animaciones personalizadas
- ✅ Material Design 3
- ✅ Arquitectura MVVM

# Documentacion

- En carpeta [Documentacion](/Documentacion)
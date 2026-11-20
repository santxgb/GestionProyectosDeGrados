# SisTG UPTC – Gestión de Trabajos de Grado

Aplicación de escritorio desarrollada en **Java 17**, con interfaz gráfica **Swing**, Programación Orientada a Objetos y arquitectura en capas. Apoya el seguimiento de propuestas y trabajos de grado de la Escuela de Ingeniería de Sistemas y Computación de la UPTC, sede Sogamoso.

Proyecto de **Programación II** para el **Ing. Jairo Armando Riaño Herrera**, director de la Escuela.

## Problema

El registro actual se lleva en Notion y la revisión de los plazos es manual. SisTG permite dar seguimiento a las propuestas y a los trabajos de grado aprobados. Según las reglas del proyecto, el plazo del trabajo de grado es de **mínimo 4 y máximo 12 meses** a partir de la aprobación por el Comité Curricular.

## Funcionalidades principales

- Registrar y consultar estudiantes y docentes.
- Radicar propuestas y registrar la decisión del Comité Curricular.
- Consultar trabajos aprobados en el tablero de vencimientos.
- Registrar jurados, observaciones y anexos asociados a los expedientes.
- Consultar las alertas de plazos y exportarlas a TXT.
- Conservar los registros en archivos JSON mediante Gson.

## Requisitos

- **JDK 17 o superior** instalado y seleccionado para el proyecto.
- **IntelliJ IDEA** con soporte para Maven (o Maven instalado para compilar desde una terminal).
- Conexión a Internet durante la primera sincronización de Maven, para descargar Gson si todavía no está en la caché local.

El archivo `pom.xml` de la **raíz del proyecto** define Gson como dependencia; **no es necesario descargar ni añadir manualmente el JAR de Gson**.

## Cómo abrir y ejecutar en IntelliJ IDEA

1. Descarga o clona el repositorio y abre en IntelliJ **la carpeta que contiene `pom.xml`**, no únicamente la carpeta `src`.
2. Comprueba que el SDK del proyecto sea **JDK 17 o superior**.
3. Si IntelliJ lo solicita, importa o sincroniza el proyecto **como Maven** y espera a que descargue la dependencia Gson.
4. Ejecuta la clase `co.edu.uptc.main.Main`, ubicada en `src/co/edu/uptc/main/Main.java`.
5. Asegúrate de que el **directorio de trabajo** de la configuración de ejecución sea la **raíz del proyecto** (la carpeta donde está `pom.xml`).

Si IntelliJ muestra una biblioteca Gson configurada con una ruta a otra carpeta o a otro computador, elimina esa referencia manual del proyecto y vuelve a **recargar Maven**. No necesitas esa biblioteca externa: Gson ya está declarada en `pom.xml`.

## Compilar mediante Maven (opcional)

Desde la carpeta donde está `pom.xml`, ejecuta:

```bash
mvn clean compile
```

Esta instrucción **compila** las clases, pero **no crea por sí sola un instalador ni un JAR ejecutable autónomo** con Gson incluido. Para este proyecto académico, la ejecución recomendada es desde IntelliJ con Maven sincronizado.

## Archivos de datos

Al guardar registros se crean archivos JSON en la carpeta `data/` de la raíz del proyecto:

- `data/student.json`
- `data/teacher.json`
- `data/document.json`

La carpeta `data/` está excluida del repositorio por `.gitignore`; por ello, los datos registrados en un computador **no se transfieren automáticamente** al clonar el proyecto en otro. Si necesitas trasladarlos, copia esa carpeta de forma controlada, teniendo en cuenta los datos personales que contenga.

Los anexos se registran mediante rutas de archivos; SisTG **no copia los archivos adjuntos** a una carpeta compartida. Sus rutas pueden no funcionar en otro computador.

## Arquitectura

El código Java se encuentra bajo `src/co/edu/uptc/`, organizado en los paquetes `model`, `model.enums`, `repository`, `service`, `controller`, `view`, `main` y `exception`.

- **Model:** entidades y reglas propias de cada expediente, incluidos los plazos de `DegreeWork`.
- **Repository:** persistencia JSON con `JsonRepository<T>` y Gson.
- **Service:** validaciones, seguimiento y generación de alertas o reportes.
- **Controller:** comunicación entre las ventanas y los servicios.
- **View:** interfaz Swing.
- **Main:** inicio de la aplicación.

## Modalidades

Monografía · Proyecto de investigación · Participación en grupo de investigación · Práctica empresarial o social · Primer semestre de posgrado.

# ESTRUCTURA DETALLADA DEL WSDL - Hermosa Cartagena Service

## 📋 Visión General del Contrato WSDL

El archivo `HermosaCartagenaService.wsdl` define un contrato formal completo para el sistema hotelero "Hermosa Cartagena", siguiendo los estándares WSDL 1.1 del W3C.

---

## 🏗️ Estructura Principal del WSDL

```
<definitions>
├── <types>           # Definición de tipos de datos
├── <messages>         # Mensajes de entrada/salida
├── <portType>         # Definición de operaciones
├── <binding>          # Protocolo de comunicación
└── <service>          # Punto de acceso al servicio
```

---

## 📊 1. TIPOS DE DATOS (TYPES)

### Namespace y Esquema
```xml
<types>
    <xsd:schema targetNamespace="http://hermosacartagena.com/ws">
        <!-- Definiciones de tipos complejos -->
    </xsd:schema>
</types>
```

### Entidades Definidas

#### 1.1 Entidad Cliente
```xml
<xsd:complexType name="Cliente">
    <xsd:sequence>
        <xsd:element name="idCliente" type="xsd:long"/>
        <xsd:element name="nombre" type="xsd:string"/>
        <xsd:element name="apellido" type="xsd:string"/>
        <xsd:element name="email" type="xsd:string"/>
        <xsd:element name="telefono" type="xsd:string"/>
        <xsd:element name="direccion" type="xsd:string"/>
        <xsd:element name="tipoDocumento" type="xsd:string"/>
        <xsd:element name="numeroDocumento" type="xsd:string"/>
        <xsd:element name="puntosLealtad" type="xsd:int"/>
        <xsd:element name="estado" type="xsd:string"/>
    </xsd:sequence>
</xsd:complexType>
```

**Propósito:** Representa a los clientes del hotel con información personal y de lealtad.

#### 1.2 Entidad Habitación
```xml
<xsd:complexType name="Habitacion">
    <xsd:sequence>
        <xsd:element name="idHabitacion" type="xsd:long"/>
        <xsd:element name="numero" type="xsd:string"/>
        <xsd:element name="tipo" type="xsd:string"/>
        <xsd:element name="capacidad" type="xsd:int"/>
        <xsd:element name="precioPorNoche" type="xsd:decimal"/>
        <xsd:element name="estado" type="xsd:string"/>
        <xsd:element name="descripcion" type="xsd:string"/>
    </xsd:sequence>
</xsd:complexType>
```

**Propósito:** Define las habitaciones disponibles con características y precios.

#### 1.3 Entidad Reserva
```xml
<xsd:complexType name="Reserva">
    <xsd:sequence>
        <xsd:element name="idReserva" type="xsd:long"/>
        <xsd:element name="idCliente" type="xsd:long"/>
        <xsd:element name="idHabitacion" type="xsd:long"/>
        <xsd:element name="fechaCheckIn" type="xsd:date"/>
        <xsd:element name="fechaCheckOut" type="xsd:date"/>
        <xsd:element name="estado" type="xsd:string"/>
        <xsd:element name="totalPagado" type="xsd:decimal"/>
    </xsd:sequence>
</xsd:complexType>
```

**Propósito:** Gestiona las reservas de habitaciones con fechas y estados.

#### 1.4 Entidad Servicio
```xml
<xsd:complexType name="Servicio">
    <xsd:sequence>
        <xsd:element name="idServicio" type="xsd:long"/>
        <xsd:element name="nombre" type="xsd:string"/>
        <xsd:element name="descripcion" type="xsd:string"/>
        <xsd:element name="precio" type="xsd:decimal"/>
        <xsd:element name="categoria" type="xsd:string"/>
        <xsd:element name="disponible" type="xsd:boolean"/>
    </xsd:sequence>
</xsd:complexType>
```

**Propósito:** Define servicios adicionales del hotel (spa, restaurante, tours).

#### 1.5 Tipos de Respuesta y Error
```xml
<!-- Respuesta estándar -->
<xsd:complexType name="Respuesta">
    <xsd:sequence>
        <xsd:element name="codigo" type="xsd:int"/>
        <xsd:element name="mensaje" type="xsd:string"/>
        <xsd:element name="exitoso" type="xsd:boolean"/>
    </xsd:sequence>
</xsd:complexType>

<!-- Error SOAP -->
<xsd:complexType name="ErrorInfo">
    <xsd:sequence>
        <xsd:element name="codigoError" type="xsd:string"/>
        <xsd:element name="mensajeError" type="xsd:string"/>
        <xsd:element name="detalleError" type="xsd:string"/>
    </xsd:sequence>
</xsd:complexType>
```

---

## 📨 2. MENSAJES (MESSAGES)

### 2.1 Mensajes de Cliente

#### Operación Registrar Cliente
```xml
<message name="RegistrarClienteRequest">
    <part name="cliente" type="tns:Cliente"/>
</message>

<message name="RegistrarClienteResponse">
    <part name="respuesta" type="tns:Respuesta"/>
    <part name="clienteRegistrado" type="tns:Cliente"/>
</message>
```

#### Operación Consultar Cliente
```xml
<message name="ConsultarClienteRequest">
    <part name="idCliente" type="xsd:long"/>
</message>

<message name="ConsultarClienteResponse">
    <part name="cliente" type="tns:Cliente"/>
</message>
```

#### Operación Listar Clientes
```xml
<message name="ListarClientesRequest">
    <part name="pagina" type="xsd:int"/>
    <part name="tamano" type="xsd:int"/>
</message>

<message name="ListarClientesResponse">
    <part name="clientes" type="tns:Cliente"/>
</message>
```

### 2.2 Mensajes de Habitación

#### Consulta de Habitación
```xml
<message name="ConsultarHabitacionRequest">
    <part name="idHabitacion" type="xsd:long"/>
</message>

<message name="ConsultarHabitacionResponse">
    <part name="habitacion" type="tns:Habitacion"/>
</message>
```

#### Listar Habitaciones Disponibles
```xml
<message name="ListarHabitacionesDisponiblesRequest">
    <part name="fechaInicio" type="xsd:date"/>
    <part name="fechaFin" type="xsd:date"/>
    <part name="capacidad" type="xsd:int"/>
</message>

<message name="ListarHabitacionesDisponiblesResponse">
    <part name="habitaciones" type="tns:Habitacion"/>
</message>
```

### 2.3 Mensajes de Reserva

#### Crear Reserva
```xml
<message name="CrearReservaRequest">
    <part name="reserva" type="tns:Reserva"/>
</message>

<message name="CrearReservaResponse">
    <part name="respuesta" type="tns:Respuesta"/>
    <part name="reservaCreada" type="tns:Reserva"/>
</message>
```

#### Consultar y Cancelar Reserva
```xml
<message name="ConsultarReservaRequest">
    <part name="idReserva" type="xsd:long"/>
</message>

<message name="ConsultarReservaResponse">
    <part name="reserva" type="tns:Reserva"/>
</message>

<message name="CancelarReservaRequest">
    <part name="idReserva" type="xsd:long"/>
</message>

<message name="CancelarReservaResponse">
    <part name="respuesta" type="tns:Respuesta"/>
</message>
```

### 2.4 Mensajes de Servicios

#### Listar y Consultar Servicios
```xml
<message name="ListarServiciosRequest">
    <part name="categoria" type="xsd:string"/>
</message>

<message name="ListarServiciosResponse">
    <part name="servicios" type="tns:Servicio"/>
</message>

<message name="ConsultarServicioRequest">
    <part name="idServicio" type="xsd:long"/>
</message>

<message name="ConsultarServicioResponse">
    <part name="servicio" type="tns:Servicio"/>
</message>
```

### 2.5 Mensajes de Error
```xml
<message name="SOAPFaultRequest">
    <part name="errorInfo" type="tns:ErrorInfo"/>
</message>
```

---

## 🔌 3. OPERACIONES (PORT TYPE)

### 3.1 Definición del Port Type
```xml
<portType name="HermosaCartagenaPortType">
    <!-- Operaciones definidas aquí -->
</portType>
```

### 3.2 Operaciones de Cliente

#### Registrar Cliente
```xml
<operation name="registrarCliente">
    <input message="tns:RegistrarClienteRequest"/>
    <output message="tns:RegistrarClienteResponse"/>
    <fault name="ClienteError" message="tns:SOAPFaultRequest"/>
</operation>
```

#### Consultar Cliente
```xml
<operation name="consultarCliente">
    <input message="tns:ConsultarClienteRequest"/>
    <output message="tns:ConsultarClienteResponse"/>
    <fault name="ClienteError" message="tns:SOAPFaultRequest"/>
</operation>
```

#### Listar Clientes
```xml
<operation name="listarClientes">
    <input message="tns:ListarClientesRequest"/>
    <output message="tns:ListarClientesResponse"/>
</operation>
```

### 3.3 Operaciones de Habitación

#### Consultar Habitación
```xml
<operation name="consultarHabitacion">
    <input message="tns:ConsultarHabitacionRequest"/>
    <output message="tns:ConsultarHabitacionResponse"/>
    <fault name="HabitacionError" message="tns:SOAPFaultRequest"/>
</operation>
```

#### Listar Habitaciones Disponibles
```xml
<operation name="listarHabitacionesDisponibles">
    <input message="tns:ListarHabitacionesDisponiblesRequest"/>
    <output message="tns:ListarHabitacionesDisponiblesResponse"/>
    <fault name="HabitacionError" message="tns:SOAPFaultRequest"/>
</operation>
```

### 3.4 Operaciones de Reserva

#### Crear Reserva
```xml
<operation name="crearReserva">
    <input message="tns:CrearReservaRequest"/>
    <output message="tns:CrearReservaResponse"/>
    <fault name="ReservaError" message="tns:SOAPFaultRequest"/>
</operation>
```

#### Consultar Reserva
```xml
<operation name="consultarReserva">
    <input message="tns:ConsultarReservaRequest"/>
    <output message="tns:ConsultarReservaResponse"/>
    <fault name="ReservaError" message="tns:SOAPFaultRequest"/>
</operation>
```

#### Cancelar Reserva
```xml
<operation name="cancelarReserva">
    <input message="tns:CancelarReservaRequest"/>
    <output message="tns:CancelarReservaResponse"/>
    <fault name="ReservaError" message="tns:SOAPFaultRequest"/>
</operation>
```

### 3.5 Operaciones de Servicios

#### Listar Servicios
```xml
<operation name="listarServicios">
    <input message="tns:ListarServiciosRequest"/>
    <output message="tns:ListarServiciosResponse"/>
</operation>
```

#### Consultar Servicio
```xml
<operation name="consultarServicio">
    <input message="tns:ConsultarServicioRequest"/>
    <output message="tns:ConsultarServicioResponse"/>
    <fault name="ServicioError" message="tns:SOAPFaultRequest"/>
</operation>
```

---

## 🔗 4. BINDING (SOAP OVER HTTP)

### 4.1 Definición del Binding
```xml
<binding name="HermosaCartagenaBinding" type="tns:HermosaCartagenaPortType">
    <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
    <!-- Operaciones con binding específico -->
</binding>
```

### 4.2 Ejemplo de Binding de Operación

#### Registrar Cliente
```xml
<operation name="registrarCliente">
    <soap:operation soapAction="http://hermosacartagena.com/ws/registrarCliente"/>
    <input>
        <soap:body use="literal"/>
    </input>
    <output>
        <soap:body use="literal"/>
    </output>
    <fault name="ClienteError">
        <soap:fault name="ClienteError" use="literal"/>
    </fault>
</operation>
```

**Características del Binding:**
- **Style**: document (mensajes literales)
- **Transport**: HTTP
- **Use**: literal (XML literal)
- **SOAP Action**: URI única por operación

---

## 🌐 5. SERVICIO (SERVICE)

### 5.1 Definición del Servicio
```xml
<service name="HermosaCartagenaService">
    <documentation>
        Sistema de Gestión Turística "Hermosa Cartagena" - Servicios Web SOAP
        Proporciona acceso a las operaciones principales del sistema hotelero.
    </documentation>
    
    <port name="HermosaCartagenaPort" binding="tns:HermosaCartagenaBinding">
        <soap:address location="http://localhost:8080/hermosacartagena/ws"/>
    </port>
</service>
```

### 5.2 Componentes del Servicio

#### Documentation
- **Propósito**: Descripción del servicio
- **Contenido**: Información general del sistema hotelero

#### Port
- **Nombre**: HermosaCartagenaPort
- **Binding**: HermosaCartagenaBinding
- **Address**: Endpoint HTTP del servicio

---

## 📊 6. ESTADÍSTICAS DEL CONTRATO

### 6.1 Resumen de Componentes

| Componente | Cantidad | Descripción |
|------------|-----------|-------------|
| **Tipos Complejos** | 6 | Cliente, Habitación, Reserva, Servicio, Respuesta, ErrorInfo |
| **Operaciones** | 11 | 3 clientes, 2 habitaciones, 3 reservas, 2 servicios, 1 listado |
| **Mensajes** | 22 | 11 requests, 11 responses |
| **Faults** | 3 | ClienteError, HabitacionError, ReservaError, ServicioError |
| **Bindings** | 1 | SOAP sobre HTTP |

### 6.2 Distribución por Entidad

| Entidad | Operaciones | Mensajes | Faults |
|---------|-------------|-----------|---------|
| **Cliente** | 3 | 6 | 1 |
| **Habitación** | 2 | 4 | 1 |
| **Reserva** | 3 | 6 | 1 |
| **Servicio** | 2 | 4 | 1 |
| **General** | 1 | 2 | 0 |

---

## 🔧 7. CARACTERÍSTICAS TÉCNICAS

### 7.1 Namespaces
```xml
xmlns:tns="http://hermosacartagena.com/ws"
xmlns:xsd="http://www.w3.org/2001/XMLSchema"
xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
xmlns="http://schemas.xmlsoap.org/wsdl/"
```

### 7.2 Tipos de Datos Utilizados
- **xsd:long**: Identificadores únicos
- **xsd:string**: Texto y nombres
- **xsd:int**: Valores numéricos enteros
- **xsd:decimal**: Valores monetarios
- **xsd:date**: Fechas
- **xsd:boolean**: Estados lógicos

### 7.3 Patrones de Diseño
- **Document/Literal**: Estilo estándar WSDL
- **Wrapper Elements**: Elementos request/response
- **Fault Handling**: SOAP Fault personalizados
- **Namespace Consistency**: Mismo namespace para todos los elementos

---

## 🎯 8. USO DEL CONTRATO

### 8.1 Generación de Clientes
```bash
# Java (JAX-WS)
wsimport -keep -p com.hermosacartagena.client http://localhost:8080/hermosacartagena/ws?wsdl

# .NET (svcutil)
svcutil /out:HermosaCartagenaClient.cs http://localhost:8080/hermosacartagena/ws?wsdl

# PHP (wsdl2php)
php wsdl2php.php -i http://localhost:8080/hermosacartagena/ws?wsdl
```

### 8.2 Validación de Mensajes
```xml
<!-- Mensaje válido según el esquema -->
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <tns:registrarClienteRequest xmlns:tns="http://hermosacartagena.com/ws">
            <tns:cliente>
                <!-- Datos del cliente según el tipo complejo -->
            </tns:cliente>
        </tns:registrarClienteRequest>
    </soap:Body>
</soap:Envelope>
```

---

## 📋 9. BENEFICIOS DEL CONTRATO FORMAL

### 9.1 Ventajas Técnicas
- **Validación Automática**: Esquemas XSD integros
- **Generación de Código**: Clientes automáticos
- **Documentación Viva**: Contrato como fuente de verdad
- **Versionado**: Control de cambios estructurado
- **Interoperabilidad**: Multiplataforma garantizada

### 9.2 Beneficios de Negocio
- **Integración Simplificada**: Partners pueden integrarse fácilmente
- **Reducción de Errores**: Validación previa a la ejecución
- **Mantenimiento**: Cambios controlados y documentados
- **Testing**: Contratos como especificaciones ejecutables

---

## 🎓 CONCLUSIÓN

El WSDL de "Hermosa Cartagena" representa un contrato empresarial completo que:

1. **Formaliza la comunicación** entre cliente y servidor
2. **Define tipos de datos** con validación automática
3. **Especifica operaciones** con manejo de errores
4. **Establece protocolos** estándar de comunicación
5. **Facilita la integración** con sistemas externos
6. **Garantiza interoperabilidad** multiplataforma

**Resultado**: Un contrato robusto, mantenible y escalable para el sistema hotelero.

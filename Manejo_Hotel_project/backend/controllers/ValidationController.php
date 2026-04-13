<?php
/**
 * =============================================
 * SISTEMA DE GESTIÓN TURÍSTICA "HERMOSA CARTAGENA"
 * CONTROLADOR: VALIDATION
 * =============================================
 * 
 * Este controlador maneja la validación de formularios,
 * sanitización de datos y manejo de errores.
 */

require_once __DIR__ . '/../config/database.php';

class ValidationController {
    
    /**
     * Validar y sanitizar datos de registro de usuario
     * @param array $data Datos del formulario
     * @return array Resultado de validación
     */
    public function validateUserRegistration($data) {
        $errors = [];
        $sanitized_data = [];
        
        // Validar username
        if (empty($data['username'])) {
            $errors['username'] = 'El nombre de usuario es obligatorio';
        } elseif (strlen($data['username']) < 3) {
            $errors['username'] = 'El nombre de usuario debe tener al menos 3 caracteres';
        } elseif (!preg_match('/^[a-zA-Z0-9_]+$/', $data['username'])) {
            $errors['username'] = 'El nombre de usuario solo puede contener letras, números y guiones bajos';
        } else {
            $sanitized_data['username'] = sanitizeInput($data['username']);
        }
        
        // Validar email
        if (empty($data['email'])) {
            $errors['email'] = 'El email es obligatorio';
        } elseif (!validateEmail($data['email'])) {
            $errors['email'] = 'El formato del email no es válido';
        } else {
            $sanitized_data['email'] = sanitizeInput($data['email']);
        }
        
        // Validar nombre completo
        if (empty($data['nombre_completo'])) {
            $errors['nombre_completo'] = 'El nombre completo es obligatorio';
        } elseif (strlen($data['nombre_completo']) < 5) {
            $errors['nombre_completo'] = 'El nombre completo debe tener al menos 5 caracteres';
        } elseif (!preg_match('/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/', $data['nombre_completo'])) {
            $errors['nombre_completo'] = 'El nombre solo puede contener letras y espacios';
        } else {
            $sanitized_data['nombre_completo'] = sanitizeInput($data['nombre_completo']);
        }
        
        // Validar teléfono
        if (!empty($data['telefono'])) {
            if (!preg_match('/^[0-9+\-\s()]+$/', $data['telefono'])) {
                $errors['telefono'] = 'El formato del teléfono no es válido';
            } else {
                $sanitized_data['telefono'] = sanitizeInput($data['telefono']);
            }
        } else {
            $sanitized_data['telefono'] = '';
        }
        
        // Validar contraseña
        if (empty($data['password'])) {
            $errors['password'] = 'La contraseña es obligatoria';
        } elseif (strlen($data['password']) < 6) {
            $errors['password'] = 'La contraseña debe tener al menos 6 caracteres';
        } elseif (!preg_match('/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/', $data['password'])) {
            $errors['password'] = 'La contraseña debe contener al menos una mayúscula, una minúscula y un número';
        }
        
        // Validar confirmación de contraseña
        if (empty($data['confirm_password'])) {
            $errors['confirm_password'] = 'Debe confirmar la contraseña';
        } elseif ($data['password'] !== $data['confirm_password']) {
            $errors['confirm_password'] = 'Las contraseñas no coinciden';
        }
        
        // Validar rol
        if (empty($data['id_rol']) || !in_array($data['id_rol'], ['1', '2', '3'])) {
            $errors['id_rol'] = 'Debe seleccionar un rol válido';
        } else {
            $sanitized_data['id_rol'] = (int)$data['id_rol'];
        }
        
        return [
            'success' => empty($errors),
            'errors' => $errors,
            'data' => $sanitized_data
        ];
    }
    
    /**
     * Validar datos de reserva
     * @param array $data Datos del formulario
     * @return array Resultado de validación
     */
    public function validateReservation($data) {
        $errors = [];
        $sanitized_data = [];
        
        // Validar cliente
        if (empty($data['id_cliente'])) {
            $errors['id_cliente'] = 'Debe seleccionar un cliente';
        } elseif (!is_numeric($data['id_cliente'])) {
            $errors['id_cliente'] = 'El cliente seleccionado no es válido';
        } else {
            $sanitized_data['id_cliente'] = (int)$data['id_cliente'];
        }
        
        // Validar servicio
        if (empty($data['id_servicio'])) {
            $errors['id_servicio'] = 'Debe seleccionar un servicio';
        } elseif (!is_numeric($data['id_servicio'])) {
            $errors['id_servicio'] = 'El servicio seleccionado no es válido';
        } else {
            $sanitized_data['id_servicio'] = (int)$data['id_servicio'];
        }
        
        // Validar fecha de inicio
        if (empty($data['fecha_inicio_servicio'])) {
            $errors['fecha_inicio_servicio'] = 'La fecha de inicio es obligatoria';
        } elseif (!strtotime($data['fecha_inicio_servicio'])) {
            $errors['fecha_inicio_servicio'] = 'El formato de la fecha no es válido';
        } elseif (strtotime($data['fecha_inicio_servicio']) < strtotime(date('Y-m-d'))) {
            $errors['fecha_inicio_servicio'] = 'La fecha de inicio no puede ser anterior a hoy';
        } else {
            $sanitized_data['fecha_inicio_servicio'] = $data['fecha_inicio_servicio'];
        }
        
        // Validar fecha de fin (opcional)
        if (!empty($data['fecha_fin_servicio'])) {
            if (!strtotime($data['fecha_fin_servicio'])) {
                $errors['fecha_fin_servicio'] = 'El formato de la fecha de fin no es válido';
            } elseif (strtotime($data['fecha_fin_servicio']) < strtotime($data['fecha_inicio_servicio'])) {
                $errors['fecha_fin_servicio'] = 'La fecha de fin no puede ser anterior a la fecha de inicio';
            } else {
                $sanitized_data['fecha_fin_servicio'] = $data['fecha_fin_servicio'];
            }
        }
        
        // Validar cantidad de personas
        if (empty($data['cantidad_personas'])) {
            $errors['cantidad_personas'] = 'La cantidad de personas es obligatoria';
        } elseif (!is_numeric($data['cantidad_personas']) || $data['cantidad_personas'] <= 0) {
            $errors['cantidad_personas'] = 'La cantidad de personas debe ser un número mayor a 0';
        } elseif ($data['cantidad_personas'] > 50) {
            $errors['cantidad_personas'] = 'La cantidad de personas no puede ser mayor a 50';
        } else {
            $sanitized_data['cantidad_personas'] = (int)$data['cantidad_personas'];
        }
        
        // Validar precio unitario
        if (empty($data['precio_unitario'])) {
            $errors['precio_unitario'] = 'El precio unitario es obligatorio';
        } elseif (!is_numeric($data['precio_unitario']) || $data['precio_unitario'] <= 0) {
            $errors['precio_unitario'] = 'El precio unitario debe ser un número mayor a 0';
        } else {
            $sanitized_data['precio_unitario'] = (float)$data['precio_unitario'];
        }
        
        // Validar notas (opcional)
        if (!empty($data['notas'])) {
            if (strlen($data['notas']) > 1000) {
                $errors['notas'] = 'Las notas no pueden exceder 1000 caracteres';
            } else {
                $sanitized_data['notas'] = sanitizeInput($data['notas']);
            }
        } else {
            $sanitized_data['notas'] = '';
        }
        
        // Validar estado
        if (empty($data['estado_reserva'])) {
            $sanitized_data['estado_reserva'] = 'pendiente';
        } elseif (!in_array($data['estado_reserva'], ['pendiente', 'confirmada', 'pagada', 'cancelada', 'completada'])) {
            $errors['estado_reserva'] = 'El estado de reserva no es válido';
        } else {
            $sanitized_data['estado_reserva'] = $data['estado_reserva'];
        }
        
        return [
            'success' => empty($errors),
            'errors' => $errors,
            'data' => $sanitized_data
        ];
    }
    
    /**
     * Validar datos de pago
     * @param array $data Datos del formulario
     * @return array Resultado de validación
     */
    public function validatePayment($data) {
        $errors = [];
        $sanitized_data = [];
        
        // Validar reserva
        if (empty($data['id_reserva'])) {
            $errors['id_reserva'] = 'Debe seleccionar una reserva';
        } elseif (!is_numeric($data['id_reserva'])) {
            $errors['id_reserva'] = 'La reserva seleccionada no es válida';
        } else {
            $sanitized_data['id_reserva'] = (int)$data['id_reserva'];
        }
        
        // Validar monto
        if (empty($data['monto_pago'])) {
            $errors['monto_pago'] = 'El monto del pago es obligatorio';
        } elseif (!is_numeric($data['monto_pago']) || $data['monto_pago'] <= 0) {
            $errors['monto_pago'] = 'El monto debe ser un número mayor a 0';
        } elseif ($data['monto_pago'] > 999999999.99) {
            $errors['monto_pago'] = 'El monto excede el límite permitido';
        } else {
            $sanitized_data['monto_pago'] = (float)$data['monto_pago'];
        }
        
        // Validar método de pago
        if (empty($data['metodo_pago'])) {
            $errors['metodo_pago'] = 'Debe seleccionar un método de pago';
        } elseif (!in_array($data['metodo_pago'], ['efectivo', 'transferencia', 'tarjeta_credito', 'tarjeta_debito', 'paypal', 'nequi', 'daviplata'])) {
            $errors['metodo_pago'] = 'El método de pago no es válido';
        } else {
            $sanitized_data['metodo_pago'] = $data['metodo_pago'];
        }
        
        // Validar tipo de pago
        if (empty($data['tipo_pago'])) {
            $errors['tipo_pago'] = 'Debe seleccionar un tipo de pago';
        } elseif (!in_array($data['tipo_pago'], ['completo', 'abono'])) {
            $errors['tipo_pago'] = 'El tipo de pago no es válido';
        } else {
            $sanitized_data['tipo_pago'] = $data['tipo_pago'];
        }
        
        // Validar banco (si aplica)
        if (in_array($data['metodo_pago'] ?? '', ['transferencia', 'tarjeta_credito', 'tarjeta_debito'])) {
            if (empty($data['banco'])) {
                $errors['banco'] = 'Debe seleccionar un banco';
            } else {
                $sanitized_data['banco'] = sanitizeInput($data['banco']);
            }
        }
        
        // Validar número de referencia (si aplica)
        if (in_array($data['metodo_pago'] ?? '', ['transferencia', 'paypal', 'nequi', 'daviplata'])) {
            if (empty($data['numero_referencia'])) {
                $errors['numero_referencia'] = 'El número de referencia es obligatorio';
            } elseif (strlen($data['numero_referencia']) < 5) {
                $errors['numero_referencia'] = 'El número de referencia debe tener al menos 5 caracteres';
            } else {
                $sanitized_data['numero_referencia'] = sanitizeInput($data['numero_referencia']);
            }
        }
        
        // Validar moneda
        if (empty($data['moneda'])) {
            $sanitized_data['moneda'] = 'COP';
        } elseif (!in_array($data['moneda'], ['COP', 'USD', 'EUR'])) {
            $errors['moneda'] = 'La moneda no es válida';
        } else {
            $sanitized_data['moneda'] = $data['moneda'];
        }
        
        return [
            'success' => empty($errors),
            'errors' => $errors,
            'data' => $sanitized_data
        ];
    }
    
    /**
     * Validar datos de servicio
     * @param array $data Datos del formulario
     * @return array Resultado de validación
     */
    public function validateService($data) {
        $errors = [];
        $sanitized_data = [];
        
        // Validar nombre del servicio
        if (empty($data['nombre_servicio'])) {
            $errors['nombre_servicio'] = 'El nombre del servicio es obligatorio';
        } elseif (strlen($data['nombre_servicio']) < 5) {
            $errors['nombre_servicio'] = 'El nombre debe tener al menos 5 caracteres';
        } elseif (strlen($data['nombre_servicio']) > 100) {
            $errors['nombre_servicio'] = 'El nombre no puede exceder 100 caracteres';
        } else {
            $sanitized_data['nombre_servicio'] = sanitizeInput($data['nombre_servicio']);
        }
        
        // Validar proveedor
        if (empty($data['id_proveedor'])) {
            $errors['id_proveedor'] = 'Debe seleccionar un proveedor';
        } elseif (!is_numeric($data['id_proveedor'])) {
            $errors['id_proveedor'] = 'El proveedor seleccionado no es válido';
        } else {
            $sanitized_data['id_proveedor'] = (int)$data['id_proveedor'];
        }
        
        // Validar tipo de servicio
        if (empty($data['tipo_servicio'])) {
            $errors['tipo_servicio'] = 'Debe seleccionar un tipo de servicio';
        } elseif (!in_array($data['tipo_servicio'], ['tour', 'hospedaje', 'transporte', 'alimentacion', 'actividad'])) {
            $errors['tipo_servicio'] = 'El tipo de servicio no es válido';
        } else {
            $sanitized_data['tipo_servicio'] = $data['tipo_servicio'];
        }
        
        // Validar descripción
        if (empty($data['descripcion'])) {
            $errors['descripcion'] = 'La descripción es obligatoria';
        } elseif (strlen($data['descripcion']) < 10) {
            $errors['descripcion'] = 'La descripción debe tener al menos 10 caracteres';
        } elseif (strlen($data['descripcion']) > 2000) {
            $errors['descripcion'] = 'La descripción no puede exceder 2000 caracteres';
        } else {
            $sanitized_data['descripcion'] = sanitizeInput($data['descripcion']);
        }
        
        // Validar precio base
        if (empty($data['precio_base'])) {
            $errors['precio_base'] = 'El precio base es obligatorio';
        } elseif (!is_numeric($data['precio_base']) || $data['precio_base'] <= 0) {
            $errors['precio_base'] = 'El precio base debe ser un número mayor a 0';
        } else {
            $sanitized_data['precio_base'] = (float)$data['precio_base'];
        }
        
        // Validar duración
        if (empty($data['duracion_horas'])) {
            $errors['duracion_horas'] = 'La duración es obligatoria';
        } elseif (!is_numeric($data['duracion_horas']) || $data['duracion_horas'] <= 0) {
            $errors['duracion_horas'] = 'La duración debe ser un número mayor a 0';
        } elseif ($data['duracion_horas'] > 168) { // Máximo 7 días
            $errors['duracion_horas'] = 'La duración no puede exceder 168 horas';
        } else {
            $sanitized_data['duracion_horas'] = (float)$data['duracion_horas'];
        }
        
        // Validar capacidad máxima
        if (empty($data['capacidad_maxima'])) {
            $errors['capacidad_maxima'] = 'La capacidad máxima es obligatoria';
        } elseif (!is_numeric($data['capacidad_maxima']) || $data['capacidad_maxima'] <= 0) {
            $errors['capacidad_maxima'] = 'La capacidad máxima debe ser un número mayor a 0';
        } elseif ($data['capacidad_maxima'] > 1000) {
            $errors['capacidad_maxima'] = 'La capacidad máxima no puede exceder 1000 personas';
        } else {
            $sanitized_data['capacidad_maxima'] = (int)$data['capacidad_maxima'];
        }
        
        // Validar ubicación
        if (empty($data['ubicacion'])) {
            $errors['ubicacion'] = 'La ubicación es obligatoria';
        } elseif (strlen($data['ubicacion']) < 5) {
            $errors['ubicacion'] = 'La ubicación debe tener al menos 5 caracteres';
        } else {
            $sanitized_data['ubicacion'] = sanitizeInput($data['ubicacion']);
        }
        
        // Validar campos opcionales
        $optional_fields = ['requisitos', 'incluye', 'no_incluye'];
        foreach ($optional_fields as $field) {
            if (!empty($data[$field])) {
                if (strlen($data[$field]) > 1000) {
                    $errors[$field] = ucfirst(str_replace('_', ' ', $field)) . ' no puede exceder 1000 caracteres';
                } else {
                    $sanitized_data[$field] = sanitizeInput($data[$field]);
                }
            } else {
                $sanitized_data[$field] = '';
            }
        }
        
        // Validar estado
        if (empty($data['estado'])) {
            $sanitized_data['estado'] = 'activo';
        } elseif (!in_array($data['estado'], ['activo', 'inactivo', 'temporada'])) {
            $errors['estado'] = 'El estado no es válido';
        } else {
            $sanitized_data['estado'] = $data['estado'];
        }
        
        return [
            'success' => empty($errors),
            'errors' => $errors,
            'data' => $sanitized_data
        ];
    }
    
    /**
     * Validar datos de cliente
     * @param array $data Datos del formulario
     * @return array Resultado de validación
     */
    public function validateClient($data) {
        $errors = [];
        $sanitized_data = [];
        
        // Validar tipo de documento
        if (empty($data['tipo_documento'])) {
            $errors['tipo_documento'] = 'El tipo de documento es obligatorio';
        } elseif (!in_array($data['tipo_documento'], ['CC', 'CE', 'PPN', 'TI'])) {
            $errors['tipo_documento'] = 'El tipo de documento no es válido';
        } else {
            $sanitized_data['tipo_documento'] = $data['tipo_documento'];
        }
        
        // Validar número de documento
        if (empty($data['numero_documento'])) {
            $errors['numero_documento'] = 'El número de documento es obligatorio';
        } elseif (!preg_match('/^[0-9]+$/', $data['numero_documento'])) {
            $errors['numero_documento'] = 'El número de documento solo puede contener números';
        } elseif (strlen($data['numero_documento']) < 5 || strlen($data['numero_documento']) > 15) {
            $errors['numero_documento'] = 'El número de documento debe tener entre 5 y 15 dígitos';
        } else {
            $sanitized_data['numero_documento'] = sanitizeInput($data['numero_documento']);
        }
        
        // Validar fecha de nacimiento
        if (!empty($data['fecha_nacimiento'])) {
            if (!strtotime($data['fecha_nacimiento'])) {
                $errors['fecha_nacimiento'] = 'El formato de la fecha no es válido';
            } elseif (strtotime($data['fecha_nacimiento']) > strtotime(date('Y-m-d'))) {
                $errors['fecha_nacimiento'] = 'La fecha de nacimiento no puede ser futura';
            } elseif (strtotime($data['fecha_nacimiento']) < strtotime('1900-01-01')) {
                $errors['fecha_nacimiento'] = 'La fecha de nacimiento no es válida';
            } else {
                $sanitized_data['fecha_nacimiento'] = $data['fecha_nacimiento'];
            }
        }
        
        // Validar nacionalidad
        if (!empty($data['nacionalidad'])) {
            if (strlen($data['nacionalidad']) < 3 || strlen($data['nacionalidad']) > 50) {
                $errors['nacionalidad'] = 'La nacionalidad debe tener entre 3 y 50 caracteres';
            } else {
                $sanitized_data['nacionalidad'] = sanitizeInput($data['nacionalidad']);
            }
        } else {
            $sanitized_data['nacionalidad'] = 'Colombiana';
        }
        
        // Validar dirección
        if (!empty($data['direccion'])) {
            if (strlen($data['direccion']) < 5 || strlen($data['direccion']) > 255) {
                $errors['direccion'] = 'La dirección debe tener entre 5 y 255 caracteres';
            } else {
                $sanitized_data['direccion'] = sanitizeInput($data['direccion']);
            }
        } else {
            $sanitized_data['direccion'] = '';
        }
        
        // Validar ciudad
        if (!empty($data['ciudad'])) {
            if (strlen($data['ciudad']) < 3 || strlen($data['ciudad']) > 100) {
                $errors['ciudad'] = 'La ciudad debe tener entre 3 y 100 caracteres';
            } else {
                $sanitized_data['ciudad'] = sanitizeInput($data['ciudad']);
            }
        } else {
            $sanitized_data['ciudad'] = '';
        }
        
        // Validar preferencias de contacto
        if (empty($data['preferencias_contacto'])) {
            $sanitized_data['preferencias_contacto'] = 'whatsapp';
        } elseif (!in_array($data['preferencias_contacto'], ['email', 'telefono', 'whatsapp'])) {
            $errors['preferencias_contacto'] = 'La preferencia de contacto no es válida';
        } else {
            $sanitized_data['preferencias_contacto'] = $data['preferencias_contacto'];
        }
        
        return [
            'success' => empty($errors),
            'errors' => $errors,
            'data' => $sanitized_data
        ];
    }
    
    /**
     * Generar respuesta JSON para validación AJAX
     * @param array $validation_result Resultado de la validación
     * @return void
     */
    public function jsonResponse($validation_result) {
        header('Content-Type: application/json');
        echo json_encode($validation_result);
        exit();
    }
    
    /**
     * Validar CSRF token
     * @param string $token Token a validar
     * @return bool
     */
    public function validateCSRF($token) {
        return verifyCSRFToken($token);
    }
    
    /**
     * Generar mensaje de error formateado
     * @param array $errors Array de errores
     * @return string Mensaje formateado
     */
    public function formatErrors($errors) {
        if (empty($errors)) {
            return '';
        }
        
        $message = '<div class="alert alert-danger"><ul class="mb-0">';
        foreach ($errors as $field => $error) {
            $message .= '<li>' . htmlspecialchars($error) . '</li>';
        }
        $message .= '</ul></div>';
        
        return $message;
    }
    
    /**
     * Validar y sanitizar datos genéricos
     * @param array $data Datos a validar
     * @param array $rules Reglas de validación
     * @return array Resultado de validación
     */
    public function validateGeneric($data, $rules) {
        $errors = [];
        $sanitized_data = [];
        
        foreach ($rules as $field => $rule) {
            $value = $data[$field] ?? '';
            
            // Validar campo requerido
            if (isset($rule['required']) && $rule['required'] && empty($value)) {
                $errors[$field] = ($rule['label'] ?? ucfirst($field)) . ' es obligatorio';
                continue;
            }
            
            // Si no es requerido y está vacío, continuar
            if (empty($value)) {
                $sanitized_data[$field] = '';
                continue;
            }
            
            // Validar tipo
            if (isset($rule['type'])) {
                switch ($rule['type']) {
                    case 'email':
                        if (!validateEmail($value)) {
                            $errors[$field] = 'El formato del email no es válido';
                        }
                        break;
                    case 'number':
                        if (!is_numeric($value)) {
                            $errors[$field] = 'Debe ser un número';
                        }
                        break;
                    case 'integer':
                        if (!is_numeric($value) || (int)$value != $value) {
                            $errors[$field] = 'Debe ser un número entero';
                        }
                        break;
                    case 'alpha':
                        if (!preg_match('/^[a-zA-Z]+$/', $value)) {
                            $errors[$field] = 'Solo puede contener letras';
                        }
                        break;
                    case 'alphanumeric':
                        if (!preg_match('/^[a-zA-Z0-9]+$/', $value)) {
                            $errors[$field] = 'Solo puede contener letras y números';
                        }
                        break;
                }
            }
            
            // Validar longitud
            if (isset($rule['min_length']) && strlen($value) < $rule['min_length']) {
                $errors[$field] = 'Debe tener al menos ' . $rule['min_length'] . ' caracteres';
            }
            
            if (isset($rule['max_length']) && strlen($value) > $rule['max_length']) {
                $errors[$field] = 'No puede exceder ' . $rule['max_length'] . ' caracteres';
            }
            
            // Validar rango (para números)
            if (isset($rule['min']) && is_numeric($value) && $value < $rule['min']) {
                $errors[$field] = 'Debe ser mayor o igual a ' . $rule['min'];
            }
            
            if (isset($rule['max']) && is_numeric($value) && $value > $rule['max']) {
                $errors[$field] = 'Debe ser menor o igual a ' . $rule['max'];
            }
            
            // Validar opciones
            if (isset($rule['options']) && !in_array($value, $rule['options'])) {
                $errors[$field] = 'El valor seleccionado no es válido';
            }
            
            // Sanitizar y guardar si no hay errores
            if (!isset($errors[$field])) {
                $sanitized_data[$field] = sanitizeInput($value);
            }
        }
        
        return [
            'success' => empty($errors),
            'errors' => $errors,
            'data' => $sanitized_data
        ];
    }
}
?>

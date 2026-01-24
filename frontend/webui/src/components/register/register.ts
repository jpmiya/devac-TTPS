import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UsuarioService, UsuarioRegister } from '../../app/services/UsuarioService';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {
  usuario: UsuarioRegister = {
    nombreYApellido: '',
    email: '',
    password: '',
    telefono: '',
    barrio: '',
    ciudad: '',
    posicion: 0,
    puntos: 0,
    casosEnZona: 0
  };

  confirmPassword: string = '';
  loading = false;
  error = '';
  success = false;

  constructor(
    private usuarioService: UsuarioService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  onSubmit(): void {
    // Validaciones básicas
    if (!this.validateForm()) {
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = false;

    console.log('Enviando registro:', this.usuario);

    this.usuarioService.register(this.usuario).subscribe({
      next: (response) => {
        console.log('✅ NEXT - Registro exitoso:', response);
        this.success = true;
        this.loading = false;
        this.cdr.detectChanges(); // Forzar detección de cambios
        this.error = '';
        // Comentado: más adelante redirigiremos al login automático
        // setTimeout(() => {
        //   this.router.navigate(['/login']);
        // }, 2000);
      },
      error: (err) => {
        console.error('❌ ERROR - Error completo:', err);
        this.loading = false;
        this.success = false;
        
        // Mostrar el mensaje del backend directamente
        if (err.status === 0) {
          this.error = 'No se puede conectar con el servidor. Verifica que la API esté corriendo en http://localhost:8080';
        } else {
          // El backend devuelve { error: "mensaje" }
          this.error = err.error?.error || err.error?.message || 'Error al registrar usuario. Por favor, intenta nuevamente.';
        }
        
        this.cdr.detectChanges(); // Forzar detección de cambios
      },
      complete: () => {
        console.log('✅ COMPLETE - Observable completado');
      }
    });
  }

  validateForm(): boolean {
    console.log('Validando formulario. Usuario:', this.usuario);
    console.log('Confirm password:', this.confirmPassword);
    
    if (!this.usuario.nombreYApellido || !this.usuario.email || !this.usuario.password) {
      console.log('Falla validación de campos obligatorios');
      this.error = 'Por favor completa todos los campos obligatorios';
      return false;
    }

    if (this.usuario.password !== this.confirmPassword) {
      console.log('Falla validación de contraseñas no coinciden');
      this.error = 'Las contraseñas no coinciden';
      return false;
    }

    if (this.usuario.password.length < 6) {
      console.log('Falla validación de longitud de contraseña');
      this.error = 'La contraseña debe tener al menos 6 caracteres';
      return false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.usuario.email)) {
      console.log('Falla validación de email');
      this.error = 'Por favor ingresa un email válido';
      return false;
    }

    console.log('Validación exitosa');
    return true;
  }
}

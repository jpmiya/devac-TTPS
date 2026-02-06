import {Component, signal} from '@angular/core';
import {FormGroup, FormBuilder, Validators, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {UsuarioService} from '../../app/services/UsuarioService';
import {AuthService} from '../../app/services/AuthService';
import { Router } from '@angular/router';



@Component({
  selector: 'app-log-in',
  templateUrl: './LogIn.html',
  imports:[ReactiveFormsModule, CommonModule],
  standalone: true
}) export class LogInComponent {

  loginForm: FormGroup;
  errorMessage = signal('');
  loading = signal(false);
  showPassword = signal(false);


  constructor(private fb: FormBuilder,
              private usuarioService: UsuarioService,
              private authService: AuthService,
              private router: Router) {
    this.loginForm = this.fb.group({
      email:['' , [Validators.required, Validators.email]],
      password:['', [Validators.required]]
    });

  }

  submit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    const { email, password } = this.loginForm.value;

    this.loading.set(true);
    this.errorMessage.set('');

    this.usuarioService.login(email, password).subscribe({
      next: (res) => {
        this.loading.set(false);
        this.authService.setLoggedIn();
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.loading.set(false);
        this.loginForm.get('password')?.reset();
        this.showPassword.set(false);
        console.error(err);
        if (err.status === 401) {
          this.errorMessage.set('Email o contraseña incorrectos');
        } else if (err.status === 0) {
          this.errorMessage.set('No se puede conectar con el servidor');
        } else {
          this.errorMessage.set('Error al iniciar sesión. Intenta nuevamente.');
        }
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/']);
  };


}

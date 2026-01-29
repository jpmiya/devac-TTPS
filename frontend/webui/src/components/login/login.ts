import {Component} from '@angular/core';
import {FormGroup, FormBuilder, Validators, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {UsuarioService} from '../../app/services/UsuarioService';
import { Router } from '@angular/router';



@Component({
  selector: 'app-log-in',
  templateUrl: './LogIn.html',
  imports:[ReactiveFormsModule, CommonModule],
  standalone: true
}) export class LogInComponent {

  loginForm: FormGroup;
  errorMessage = '';
  loading = false;


  constructor(private fb: FormBuilder,
              private usuarioService :UsuarioService,
              private router: Router,) {
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

    this.loading = true;
    this.errorMessage = '';

    this.usuarioService.login(email, password).subscribe({
      next: (res) => {
        this.loading = false;
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.loading = false;
        console.error(err);
        if (err.status === 401) {
          this.errorMessage = 'Email o contraseña incorrectos';
        } else if (err.status === 0) {
          this.errorMessage = 'No se puede conectar con el servidor';
        } else {
          this.errorMessage = 'Error al iniciar sesión. Intenta nuevamente.';
        }
      }
    });
  }


}

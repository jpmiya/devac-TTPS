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

    this.usuarioService.login(email, password).subscribe({
      next: (res) => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error(err);
        // 401 → credenciales inválidas
      }
    });
  }


}

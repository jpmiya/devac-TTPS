import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { EditUserService, MeDTO, UpdateMeDTO } from '../../app/services/EditUserService';
import {UsuarioService} from '../../app/services/UsuarioService';

@Component({
  selector: 'app-edit-user',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css'],
})
export class EditUserComponent implements OnInit {
  private fb = inject(FormBuilder);
  private editUserService = inject(EditUserService);
  private userService = inject(UsuarioService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);


  loading = false;
  saving = false;

  successMessage = '';
  errorMessage = '';

  private originalMe: MeDTO | null = null;

  form = this.fb.nonNullable.group({
    nombreYApellido: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(80)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(120)]],
    telefono: ['', [Validators.maxLength(30)]],
    barrio: ['', [Validators.maxLength(60)]],
    ciudad: ['', [Validators.maxLength(60)]],
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.editUserService
      .getMe()
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (me) => {
          this.originalMe = me;

          this.form.patchValue({
            nombreYApellido: me.nombreYApellido ?? '',
            email: me.email ?? '',
            telefono: me.telefono ?? '',
            barrio: me.barrio ?? '',
            ciudad: me.ciudad ?? '',
          });

          this.form.markAsPristine();
          this.form.markAsUntouched();

          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error(err);
          this.errorMessage = 'No se pudo cargar tu perfil.';
          this.cdr.detectChanges();
        },
      });
  }


  save(): void {
    this.successMessage = '';
    this.errorMessage = '';
    this.cdr.detectChanges();

    if (!this.originalMe?.id) {
      this.errorMessage = 'No se pudo determinar el id del usuario.';
      this.cdr.detectChanges();
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.errorMessage = 'Revisá los campos.';
      this.cdr.detectChanges();
      return;
    }

    if (!this.form.dirty) {
      this.successMessage = 'No hay cambios para guardar.';
      this.cdr.detectChanges();
      return;
    }

    const v = this.form.getRawValue();
    const payload: UpdateMeDTO = {
      nombreYApellido: v.nombreYApellido.trim(),
      email: v.email.trim(),
      telefono: v.telefono.trim(),
      barrio: v.barrio.trim(),
      ciudad: v.ciudad.trim(),
    };

    this.saving = true;
    this.cdr.detectChanges();

    this.editUserService
      .updateMe(this.originalMe.id, payload)
      .pipe(finalize(() => {
        this.saving = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: () => {
          this.originalMe = { ...this.originalMe!, ...payload } as MeDTO;
          this.form.markAsPristine();
          this.successMessage = 'Cambios guardados.';
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error(err);
          // ojo: tu backend a veces manda string, no {message:...}
          const msg =
            typeof err?.error === 'string'
              ? err.error
              : err?.error?.message ?? 'No se pudieron guardar los cambios.';

          this.errorMessage = msg;
          this.cdr.detectChanges();
        },
      });
  }

  cancel(): void {
    if (this.originalMe) {
      this.form.patchValue({
        nombreYApellido: this.originalMe.nombreYApellido ?? '',
        email: this.originalMe.email ?? '',
        telefono: this.originalMe.telefono ?? '',
        barrio: this.originalMe.barrio ?? '',
        ciudad: this.originalMe.ciudad ?? '',
      });

      this.form.markAsPristine();
      this.form.markAsUntouched();
      this.successMessage = '';
      this.errorMessage = '';
      return;
    }

    this.load();
  }

  goBack(): void {
    this.router.navigateByUrl('/profile');
  }

  hasError(name: keyof typeof this.form.controls, err: string): boolean {
    const c = this.form.controls[name];
    return !!(c.touched && c.errors?.[err]);
  }
}

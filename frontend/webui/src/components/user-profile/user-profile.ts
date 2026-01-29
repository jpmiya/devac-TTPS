import { Component, OnInit, inject, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { ProfileService, MeDTO, UpdateMeDTO } from '../../app/services/UserProfileService';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
})
export class ProfileComponent implements OnInit {
  private profileSvc = inject(ProfileService);
  private fb = inject(FormBuilder);
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router);

  me: MeDTO | null = null;

  loading = false;
  saving = false;
  error: string | null = null;
  success: string | null = null;

  editMode = false;
  showPassword = false;

  form = this.fb.group({
    nombreYApellido: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    telefono: [''],
    barrio: [''],
    ciudad: [''],
    password: [''], // opcional
  });

  ngOnInit(): void {
    this.loadMe();
  }

  loadMe(): void {
    this.loading = true;
    this.error = null;
    this.success = null;

    this.profileSvc.getMe()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (me) => {
          this.me = me;
          this.form.patchValue({
            nombreYApellido: me.nombreYApellido ?? '',
            email: me.email ?? '',
            telefono: me.telefono ?? '',
            barrio: me.barrio ?? '',
            ciudad: me.ciudad ?? '',
            password: '',
          });
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (e) => {
          this.error = this.humanizeHttpError(e, 'No pude cargar tu perfil.');
        },
      });
  }

  goToEdit(): void {
    this.router.navigate(['/edit-user']);
  }


  private humanizeHttpError(e: any, fallback: string): string {
    const status = e?.status;
    const msg =
      e?.error?.message ||
      e?.error?.error ||
      e?.message;

    if (status === 0) return 'No hay conexión con el backend (¿CORS / servidor caído?).';
    if (status === 401) return 'No estás logueado (401).';
    if (status === 403) return 'No tenés permisos (403).';
    if (status === 409) return 'Conflicto: ese email ya existe (409).';

    return msg ? String(msg) : fallback;
  }


}

import { Component, OnInit, inject, ChangeDetectorRef} from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { ProfileService, MeDTO, UpdateMeDTO } from '../../app/services/UserProfileService';
import { LocationPickerComponent, LatLon } from '../location-picker/location-picker';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LocationPickerComponent],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
})
export class ProfileComponent implements OnInit {
  private profileSvc = inject(ProfileService);
  private fb = inject(FormBuilder);
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router);
  private location = inject(Location);

  me: MeDTO | null = null;

  loading = false;
  saving = false;
  error: string | null = null;
  success: string | null = null;

  editMode = false;
  showPassword = false;
  selectedCoords: string | null = null;

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
          if (e?.status === 401 || e?.status === 403) {
            alert('Tenés que estar logueado para ver tu perfil.');
            this.router.navigate(['/']);
            return;
          }
          this.error = this.humanizeHttpError(e, 'No pude cargar tu perfil.');
          this.cdr.detectChanges();
        },
      });
  }

  toggleEdit(): void {
    this.editMode = !this.editMode;
    this.success = null;
    this.error = null;

    if (this.editMode && this.me) {
      this.form.patchValue({
        nombreYApellido: this.me.nombreYApellido ?? '',
        email: this.me.email ?? '',
        telefono: this.me.telefono ?? '',
        barrio: this.me.barrio ?? '',
        ciudad: this.me.ciudad ?? '',
        password: '',
      });
    }
  }

  cancelEdit(): void {
    this.editMode = false;
    this.error = null;
    this.success = null;
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onLocationSelected(coords: string): void {
    this.selectedCoords = coords;
  }

  saveProfile(): void {
    if (!this.me || this.form.invalid) return;

    this.saving = true;
    this.error = null;
    this.success = null;

    const v = this.form.getRawValue();
    const payload: UpdateMeDTO = {
      nombreYApellido: v.nombreYApellido ?? undefined,
      email: v.email ?? undefined,
      telefono: v.telefono || undefined,
      barrio: v.barrio || undefined,
      ciudad: v.ciudad || undefined,
    };
    if (this.selectedCoords) {
      payload.coordenadas = this.selectedCoords;
    }
    if (v.password?.trim()) {
      payload.password = v.password.trim();
    }

    this.profileSvc.updateMe(this.me.id, payload)
      .pipe(finalize(() => {
        this.saving = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: () => {
          this.success = 'Perfil actualizado correctamente.';
          this.editMode = false;
          this.loadMe();
        },
        error: (e) => {
          this.error = this.humanizeHttpError(e, 'No se pudo guardar los cambios.');
        },
      });
  }

  goToEdit(): void {
    this.router.navigate(['/edit-user']);
  }

  goToMyPets(): void {
    this.router.navigate(['/my-pets']);
  }

  goBack(): void {
    this.location.back();
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

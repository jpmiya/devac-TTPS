import { Routes } from '@angular/router';
import { LogInComponent } from '../components/login/login';
import { HomeComponent } from '../components/home/home';
import { LostDogsComponent } from '../components/lostPets/lostPets';
import { RegisterComponent } from '../components/register/register';
import { CreateMascotaComponent } from '../components/create-mascota/create-mascota';
import { EditUserComponent } from '../components/edit-user/edit-user';
import {ProfileComponent} from '../components/user-profile/user-profile';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LogInComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'lost-dogs', component: LostDogsComponent },
  {path: 'create-mascota', component:  CreateMascotaComponent },
  { path: 'edit-user', component: EditUserComponent },
  { path: 'profile', component:  ProfileComponent },
];

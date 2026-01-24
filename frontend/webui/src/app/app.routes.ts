import { Routes } from '@angular/router';
import { LogInComponent } from '../components/login/login';
import { HomeComponent } from '../components/home/home';
import { LostDogsComponent } from '../components/lostPets/lostPets';
import { RegisterComponent } from '../components/register/register';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LogInComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'lost-dogs', component: LostDogsComponent }
];

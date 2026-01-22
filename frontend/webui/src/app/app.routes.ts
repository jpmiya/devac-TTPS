import { Routes } from '@angular/router';
import { LogInComponent } from '../components/login/login';
import { HomeComponent } from '../components/home/home';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LogInComponent }
];

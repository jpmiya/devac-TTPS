import {Component} from '@angular/core';
import {FormGroup, FormBuilder, Validators, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';


@Component({
  selector: 'app-log-in',
  templateUrl: './LogIn.html',
  imports:[ReactiveFormsModule, CommonModule],
  standalone: true
}) export class LogInComponent {

  loginForm: FormGroup;


  constructor(private fb: FormBuilder) {
    this.loginForm = this.fb.group({
      email:['' , [Validators.required, Validators.email]],
      password:['', [Validators.required]]
    });
  }
  submit(): void {
    if (this.loginForm.invalid) {
      return;
    }
    console.log(this.loginForm.value);
  }

}

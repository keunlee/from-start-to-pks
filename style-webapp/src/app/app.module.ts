import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ColorPickerModule } from 'ngx-color-picker';

import { AppComponent } from './app.component';
import { ProfileFormComponent } from './input-form/input-form.component';
import { FileValueAccessorDirective } from './file-accessor/file-value-accessor.directive'
import { FileValidator } from './file-accessor/file-validator.directive'
import { FileDropModule } from 'ngx-file-drop'

import { ProfileService } from './services/profile.service';

@NgModule({
  declarations: [
    AppComponent,
    ProfileFormComponent,
    FileValueAccessorDirective,
    FileValidator
  ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    HttpClientModule,
    ColorPickerModule,
    FileDropModule,
  ],
  providers: [
    ProfileService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

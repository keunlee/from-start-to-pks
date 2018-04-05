import {Component, NgZone, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UploadEvent} from 'ngx-file-drop';

import {ProfileService} from '../services/profile.service';

const SubmissionStatus = {
  success: 'success',
  error: 'error',
  open: 'open',
};

@Component({
  selector: 'profile-input-form',
  templateUrl: './input-form.component.html',
  styleUrls: ['./input-form.component.scss']
})
export class ProfileFormComponent implements OnInit {
  profileForm: FormGroup;
  services = [];
  isDragover = false;
  statuses = SubmissionStatus;
  submissionStatus = SubmissionStatus.open;

  constructor(private formBuilder: FormBuilder, private profileService: ProfileService, private zone: NgZone) { }

  ngOnInit() {
    this.setProfileForm();

    this.profileForm.get('imageFile').valueChanges.subscribe(value => this.onImageFileChanged(value));
  }

  onImageFileChanged(imageFiles) {
    this.loadImage(imageFiles[0]);
  }

  onFileDrop(event: UploadEvent) {
    event.files[0].fileEntry.file(data => this.loadImage(data));
  }

  onFileOver() {
    this.isDragover = true;
  }

  onFileLeave() {
    this.isDragover = false;
  }

  loadImage(data) {
    const reader  = new FileReader();

    reader.onload = e => this.zone.run(() => this.profileForm.patchValue({'logo': e.target['result'] }));

    reader.readAsDataURL(data);
  }

  formIsValid() {
    const { orgId, orgName, color, logo } = this.profileForm.getRawValue();
    return orgId && orgName && color && logo;
  }

  setProfileForm() {
    this.profileForm = this.formBuilder.group({
      orgId: [null, Validators.compose([Validators.required])],
      orgName: [null, Validators.compose([Validators.required])],
      color: [null, Validators.compose([Validators.required])],
      logo: [null, Validators.compose([Validators.required])],
      imageFile: [null]
    });
  }

  submitProfile() {
    const { orgId, orgName, color, logo } = this.profileForm.getRawValue();
    const unhashedColor = color ? color.replace("#", "") : color;
    this.profileService.submitProfile(orgId, orgName, logo, "service", unhashedColor)
      .subscribe(() => this.submissionStatus = SubmissionStatus.success,
        () => this.submissionStatus = SubmissionStatus.error);
  }

  resetForm() {
    this.submissionStatus = SubmissionStatus.open;
    this.profileForm.reset();
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { PROFILE_SERVICE_ENDPOINT } from '../constants/constants';

@Injectable()
export class ProfileService {

  constructor(private http: HttpClient) {}

  submitProfile(orgId, name, logo, serviceName, themeColor) {
    return this.http.post(`${PROFILE_SERVICE_ENDPOINT}styles`, {
      id: orgId,
      name: name,
      logo: logo,
      serviceName: serviceName,
      themeColor: themeColor
    });
  }
}

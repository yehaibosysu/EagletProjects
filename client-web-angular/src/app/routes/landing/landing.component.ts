import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, of } from 'rxjs';
import { environment } from '@env/environment';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzFlexModule } from 'ng-zorro-antd/flex';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NzButtonModule, NzLayoutModule, NzFlexModule, NzDropDownModule, NzIconModule, NzTableModule, NgFor],
  templateUrl: './landing.component.html',
  styles: [
    `
      nz-content {
        padding: 0 50px;
      }

      nz-footer {
        text-align: center;
      }

      .inner-content {
        background: #fff;
        margin-top: 24px;
        padding: 24px;
        min-height: 780px;
      }
    `
  ]
})
export class LandingComponent implements OnInit {
  isAuthenticated: boolean = false;
  userName: string = '';
  messages: string[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.getUserInfo();
    this.getMessages();
  }

  login(): void {
    // The Backend is configured to trigger login when unauthenticated
    window.location.href = environment.api.baseUrl;
  }

  logout(): void {
    this.http
      .post('/logout', null)
      .pipe(
        catchError(error => {
          console.error(error);
          return of(null);
        })
      )
      .subscribe(() => {
        this.isAuthenticated = false;
        this.userName = '';
        this.messages = [];
      });
  }

  getUserInfo(): void {
    this.http
      .get<any>('/userinfo')
      .pipe(
        catchError(error => {
          console.error(error);
          return of(null);
        })
      )
      .subscribe(userInfo => {
        if (userInfo) {
          this.isAuthenticated = true;
          this.userName = userInfo.sub;
        }
      });
  }

  authorizeMessages(): void {
    // Trigger the Backend to perform the authorization_code grant flow.
    // After authorization is complete, the Backend will redirect back to this app.
    window.location.href = environment.api.baseUrl + '/oauth2/authorization/messaging-client-authorization-code';
  }

  getMessages(): void {
    this.http
      .get<string[]>('/messages')
      .pipe(
        catchError(error => {
          console.error(error);
          return of([]);
        })
      )
      .subscribe(messages => {
        this.messages = messages;
      });
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserDTO, UpdateUserDTO } from '../models/user.models';
import { RegisterDTO } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/users`;

  list(role?: string): Observable<UserDTO[]> {
    let params = new HttpParams();
    if (role) params = params.set('role', role);
    return this.http.get<UserDTO[]>(this.base, { params });
  }

  get(userId: number): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.base}/${userId}`);
  }

  registerByAdmin(role: string, dto: RegisterDTO): Observable<string> {
    return this.http.post(`${this.base}/register-by-admin`, dto, {
      params: new HttpParams().set('role', role),
      responseType: 'text'
    });
  }

  update(userId: number, dto: UpdateUserDTO): Observable<string> {
    return this.http.put(`${this.base}/${userId}`, dto, { responseType: 'text' });
  }

  updateStatus(userId: number, status: string): Observable<string> {
    return this.http.put(`${this.base}/${userId}/status`, null, {
      params: new HttpParams().set('status', status),
      responseType: 'text'
    });
  }
}

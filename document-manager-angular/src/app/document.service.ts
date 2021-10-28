import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {

  private baseUrl = "http://localhost:8080";

  constructor(private _http: HttpClient) { }

  //upload files functions
  upload(formData: FormData): Observable<HttpEvent<string[]>>{
    return this._http.post<string[]>(`${this.baseUrl}/document/upload`, formData, {
      reportProgress: true,
      observe: 'events'
    });
  }

  //download files function
  download(filename: string): Observable<HttpEvent<Blob>>{
    return this._http.get(`${this.baseUrl}/document/download/${filename}`, {
      reportProgress: true,
      observe: 'events',
      responseType: 'blob'
    });
  }
}

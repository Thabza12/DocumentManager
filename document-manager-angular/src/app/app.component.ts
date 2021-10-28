import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { Component } from '@angular/core';
import { DocumentService } from './document.service';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  filenames: string[] = [];
  fileStatus = { status: '', requestType: '', percent: 0};
  
  constructor(private _documentService: DocumentService){}

  //upload files
  onUploadFiles(files: File[]): void{
    const formData = new FormData();
    for (const file of files){ 
      formData.append('files', file, file.name);
    }
    this._documentService.upload(formData).subscribe(
      event => {
        console.log(event);
        this.reportProgress(event);
      },
      (error: HttpErrorResponse) => {
        console.log(error);
      }
    );
  }

  onDownloadFile(filename: string): void{
    this._documentService.download(filename).subscribe(
      event => {
        console.log(event);
        this.reportProgress(event);
      },
      (error: HttpErrorResponse) => {
        console.log(error);
      }
    );
  }

  private reportProgress(httpEvent: HttpEvent<string[] | Blob>): void {
    switch(httpEvent.type){
      case HttpEventType.UploadProgress:
        this.updateStatus(httpEvent.loaded, httpEvent.total!, 'Uploading...');
        break;
      case HttpEventType.DownloadProgress:
        this.updateStatus(httpEvent.loaded, httpEvent.total!, 'Downloading...');
        break;
      case HttpEventType.ResponseHeader:
        console.log('Header returned', httpEvent);
        break;
      case HttpEventType.Response:
        if (httpEvent.body instanceof Array){ 
          for (const filename of httpEvent.body){
            this.filenames.unshift(filename);
          }
        }else{
          saveAs(new File([httpEvent.body!], httpEvent.headers.get('File-Name')!,
                  {type: `${httpEvent.headers.get('Content-type')};charset=utf-8`}));

        }
        this.fileStatus.status = 'done';
        break;
        default:
          console.log(httpEvent);
          break;
    }
  }
  
  private updateStatus(loaded: number, total: number, requestType: string) {
    this.fileStatus.status = 'progress';
    this.fileStatus.requestType = requestType;
    this.fileStatus.percent = Math.round(100 * loaded / total);
  }
}
